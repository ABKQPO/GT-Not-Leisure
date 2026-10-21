package com.science.gtnl.common.railgun;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.equipment.EquipmentNet;
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.common.packet.RailgunChargeCapPacket;
import com.science.gtnl.common.packet.RailgunFireFxPacket;
import com.science.gtnl.common.packet.RailgunRecoilFxPacket;
import com.science.gtnl.common.railgun.RailgunChainResolver.Hit;
import com.science.gtnl.common.railgun.RailgunChainResolver.Kind;
import com.science.gtnl.config.MainConfig;

public class RailgunFireService {

    private static final int CHARGED_RANGE = 64;
    private static final double PENETRATION_DESTROY_HARDNESS = 25;
    private static final double PENETRATION_DESTROY_RADIUS = 3;
    private static final int PENETRATION_MAX_TARGETS = 5;
    private static final double[] PULSE_DAMAGE_RATIOS = { 0.6, 0.85, 1 };
    private static final int PULSE_RADIUS = 10;
    private static final double RECOIL_AIRBORNE_MUL = 1.5;
    private static final double RECOIL_CROUCH_MUL = 0.5;

    public static final String RECOIL_GRACE = "gtnl.railgunRecoilGrace";
    private static final int RECOIL_GRACE_TICKS = 60;
    private static final double MAX_RATE = 3;
    private static final double CHARGED_TRACE_PADDING = 1, CHARGED_TRACE_INFLATION = 0.3,
        PULSE_RADIUS_PER_COMPUTE = 1.5;

    private static class ChargeSession {

        private final long startTick;
        private final double rate;
        private final ItemStack stack;
        private final int slot;
        private final int dimension;
        private long lastActiveTick;
        private int cap = Integer.MAX_VALUE;

        private ChargeSession(long startTick, double rate, ItemStack stack, int slot, int dimension) {
            this.startTick = startTick;
            this.rate = rate;
            this.stack = stack;
            this.slot = slot;
            this.dimension = dimension;
            this.lastActiveTick = startTick;
        }

        private boolean matches(EntityPlayerMP player) {
            return player.getHeldItem() == stack && player.inventory.currentItem == slot
                && player.dimension == dimension;
        }

        private boolean live(long now) {
            return now - lastActiveTick <= RailgunData.CHARGE_RESUME_GRACE_TICKS;
        }

        private int units(long now) {
            return Math.min(cap, RailgunData.units(elapsed(now), rate));
        }

        private boolean capped() {
            return cap != Integer.MAX_VALUE;
        }

        private int capAtAffordable(long now, double energy) {
            long ticks = elapsed(now);
            while (ticks > 0 && energy < totalCostUpTo(RailgunData.units(ticks, rate), rate)) ticks--;
            cap = RailgunData.units(ticks, rate);
            return cap;
        }

        private long elapsed(long now) {
            return Math.max(0, Math.min(ElectromagneticRailgun.USE_DURATION, now - startTick));
        }
    }

    private static final Map<UUID, ChargeSession> CHARGING = new HashMap<>();

    private RailgunFireService() {}

    public static void beginCharge(EntityPlayerMP player, ItemStack stack) {
        long now = now(player);
        ChargeSession session = CHARGING.get(player.getUniqueID());
        if (session != null && session.matches(player) && session.live(now)) {
            session.lastActiveTick = now;
            return;
        }
        CHARGING.put(
            player.getUniqueID(),
            new ChargeSession(
                now,
                RailgunData.chargeRate(stack),
                stack,
                player.inventory.currentItem,
                player.dimension));
    }

    public static void tickCharge(EntityPlayerMP player, ItemStack stack) {
        ChargeSession session = CHARGING.get(player.getUniqueID());
        if (session == null || session.stack != stack) return;
        long now = now(player);
        session.lastActiveTick = now;
        if (session.capped() || EquipmentEnergy.free(player)) return;
        double energy = RailgunEnergy.available(stack);
        if (energy >= totalCostUpTo(session.units(now), session.rate)) return;
        RailgunNet
            .sendAround(player, new RailgunChargeCapPacket(player.getEntityId(), session.capAtAffordable(now, energy)));
        noEnergy(player);
    }

    public static void release(EntityPlayerMP player, ItemStack stack) {
        ChargeSession session = CHARGING.remove(player.getUniqueID());
        if (session == null || session.stack != stack || !session.matches(player)) return;
        int units = session.units(now(player));
        RailgunChargeTier tier = RailgunChargeTier.fromUnits(units);
        double chargeCost = chargeCostUpTo(units, session.rate);
        if (tier == RailgunChargeTier.NONE) {
            EquipmentEnergy.drain(player, stack, chargeCost);
            return;
        }
        if (!EquipmentEnergy.drain(player, stack, chargeCost + shotCost(tier))) {
            noEnergy(player);
            return;
        }
        fireCharged((WorldServer) player.worldObj, player, stack, tier);
    }

    public static void tick() {
        CHARGING.entrySet()
            .removeIf(entry -> {
                EntityPlayerMP player = RailgunServerEvents.player(entry.getKey());
                if (player == null || player.isDead) return true;
                ChargeSession session = entry.getValue();
                if (!session.matches(player)) return true;
                long now = now(player);
                if (player.isUsingItem()) session.lastActiveTick = now;
                return !session.live(now);
            });
    }

    public static void forget(EntityPlayer player) {
        CHARGING.remove(player.getUniqueID());
    }

    public static void unload(int dimension) {
        CHARGING.values()
            .removeIf(session -> session.dimension == dimension);
    }

    private static long now(EntityPlayerMP player) {
        return player.worldObj.getTotalWorldTime();
    }

    public static double totalCostUpTo(int units, double rate) {
        return chargeCostUpTo(units, rate) + shotCost(RailgunChargeTier.fromUnits(units));
    }

    public static double chargeCostUpTo(int units, double rate) {
        var config = MainConfig.item.railgun;
        rate = Math.max(1, Math.min(MAX_RATE, rate));
        int ticks = ticksFor(
            Math.max(0, Math.min((int) (ElectromagneticRailgun.USE_DURATION * MAX_RATE), units)),
            rate);
        int first = Math.min(ticks, ticksFor(RailgunChargeTier.EHV2.chargeTicks, rate));
        int second = Math.min(ticks - first, ticksFor(RailgunChargeTier.EHV3.chargeTicks, rate) - first);
        return first * Math.max(0, config.chargeEuEhv1) + second * Math.max(0, config.chargeEuEhv2)
            + (ticks - first - second) * Math.max(0, config.chargeEuEhv3);
    }

    private static int ticksFor(int units, double rate) {
        return (int) Math.ceil(units / rate - RailgunData.RATE_EPSILON);
    }

    public static double shotCost(RailgunChargeTier tier) {
        return Math.max(0, tier.shotEu()) + tier.lightning * Math.max(0, MainConfig.item.railgun.ehvLightningEu);
    }

    public static void noEnergy(EntityPlayerMP player) {
        EquipmentNet.hotbar(player, "item.gtnl.railgun.fail.no_eu");
    }

    public static void fireCharged(WorldServer world, EntityPlayerMP player, ItemStack stack, RailgunChargeTier tier) {
        var modules = RailgunData.modules(stack);
        boolean players = RailgunSetting.PVP.active(stack), chain = RailgunSetting.CHAIN.active(stack);
        boolean max = tier == RailgunChargeTier.EHV3;
        EquipmentVec from = EquipmentVec.eye(player);
        double range = RailgunRaycast.effectiveRange(CHARGED_RANGE, modules.count(RailgunModule.RANGE));
        var trace = RailgunRaycast.traceFirst(
            world,
            player,
            from,
            from.add(
                EquipmentVec.look(player)
                    .scale(range)),
            CHARGED_TRACE_PADDING,
            CHARGED_TRACE_INFLATION,
            entity -> RailgunTargetRules.canAffect(player, entity, players));
        var context = RailgunDamage.buildCharged(player, tier, modules, world, players);
        EquipmentVec impact = trace.hit() == null ? trace.blockEnd()
            : trace.hit()
                .position();
        EntityLivingBase primary = trace.hit() != null && trace.hit()
            .entity() instanceof EntityLivingBase living ? living : null;
        int primaryId = primary == null ? -1 : primary.getEntityId();
        List<Hit> hits = new ArrayList<>();
        int compute = modules.count(RailgunModule.COMPUTE);
        double pulseRadius = max ? PULSE_RADIUS + PULSE_RADIUS_PER_COMPUTE * compute : 0;
        if (primary != null) {
            hits.add(Hit.primary(primary, context.firstDamage()));
            if (chain) hits.addAll(RailgunChainResolver.chainFrom(world, player, primary, context, Set.of(), null));
            if (max) {
                hits.addAll(RailgunChainResolver.penetration(world, player, primary, context, PENETRATION_MAX_TARGETS));
                hits.addAll(
                    RailgunChainResolver.pulse(
                        world,
                        player,
                        EquipmentVec.pos(primary),
                        pulseRadius,
                        PULSE_DAMAGE_RATIOS[Math.min(compute, PULSE_DAMAGE_RATIOS.length - 1)],
                        context,
                        primaryId));
            }
        }
        double splash = RailgunSetting.SPLASH.active(stack) ? tier.impactRadius : 0;
        hits.addAll(RailgunChainResolver.splash(world, player, impact, splash, tier.impactRatio, context, primaryId));
        EntityLivingBase anchor = hits.stream()
            .filter(
                hit -> hit.kind() == Kind.AREA && hit.target() != primary
                    && hit.target()
                        .isEntityAlive())
            .map(Hit::target)
            .min(
                Comparator.comparingDouble(
                    target -> EquipmentVec.pos(target)
                        .distanceToSqr(impact)))
            .orElse(null);
        if (chain && anchor != null) {
            Set<Integer> visited = new HashSet<>();
            hits.forEach(
                hit -> visited.add(
                    hit.target()
                        .getEntityId()));
            hits.addAll(RailgunChainResolver.chainFrom(world, player, anchor, context, visited, impact));
        }
        var path = RailgunNet.path(impact, hits);
        RailgunDamage.applyAll(player, RailgunChainResolver.strongestPerTarget(hits), context, true, impact);
        if (trace.hit() != null && primary == null
            && RailgunTargetRules.canAffect(
                player,
                trace.hit()
                    .entity(),
                players))
            RailgunDamage.hitObject(
                player,
                trace.hit()
                    .entity(),
                impact,
                context.firstDamage());
        if (RailgunSetting.TERRAIN.active(stack)) {
            RailgunTerrainService.queueSphere(world, impact, tier.terrainRadius, tier.terrainHardness, player);
            for (var hit : hits) if (hit.kind() == Kind.PENETRATION) RailgunTerrainService.queueSphere(
                world,
                EquipmentVec.pos(hit.target()),
                PENETRATION_DESTROY_RADIUS,
                PENETRATION_DESTROY_HARDNESS,
                player);
        }
        applyRecoil(player, tier);
        RailgunNet.sendAround(
            player,
            new RailgunFireFxPacket(
                player.getEntityId(),
                from,
                impact,
                path,
                tier.ordinal(),
                (float) Math.max(splash, pulseRadius)));
    }

    private static void applyRecoil(EntityPlayerMP player, RailgunChargeTier tier) {
        boolean crouching = player.isSneaking();
        double speed = tier.recoilSpeed * (crouching ? RECOIL_CROUCH_MUL : 1)
            * (player.onGround ? 1 : RECOIL_AIRBORNE_MUL);
        float pitch = tier.recoilPitch * (crouching ? (float) RECOIL_CROUCH_MUL : 1);
        EquipmentVec back = EquipmentVec.look(player)
            .scale(-1);
        EquipmentVec behind = EquipmentVec.pos(player)
            .add(back);
        int x = MathHelper.floor_double(behind.x()), y = MathHelper.floor_double(behind.y()),
            z = MathHelper.floor_double(behind.z());
        if (player.worldObj.getBlock(x, y, z)
            .getCollisionBoundingBoxFromPool(player.worldObj, x, y, z) != null) speed = 0;
        player.motionX += back.x() * speed;
        player.motionY += back.y() * speed;
        player.motionZ += back.z() * speed;
        player.velocityChanged = true;
        player.fallDistance = 0;
        player.getEntityData()
            .setLong(RECOIL_GRACE, player.worldObj.getTotalWorldTime() + RECOIL_GRACE_TICKS);
        ScienceNotLeisure.network.sendTo(new RailgunRecoilFxPacket(pitch, tier.ordinal()), player);
    }
}
