package com.science.gtnl.common.railgun;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.packet.RailgunChargeCapPacket;
import com.science.gtnl.common.packet.RailgunFireFxPacket;
import com.science.gtnl.common.packet.RailgunRecoilFxPacket;
import com.science.gtnl.common.packet.StatusMessage;
import com.science.gtnl.config.MainConfig;

public final class RailgunFireService {

    public static final String RECOIL_GRACE = "gtnl.railgunRecoilGrace";

    public static class ChargeSession {

        private final long startTick;
        private final double rate;
        private final ItemStack stack;
        private final int slot;
        private final int dimension;
        private long lastActiveTick;
        private int cap = Integer.MAX_VALUE;

        public ChargeSession(long startTick, double rate, ItemStack stack, int slot, int dimension) {
            this.startTick = startTick;
            this.rate = rate;
            this.stack = stack;
            this.slot = slot;
            this.dimension = dimension;
            this.lastActiveTick = startTick;
        }

        public double rate() {
            return rate;
        }

        public ItemStack stack() {
            return stack;
        }

        public int dimension() {
            return dimension;
        }

        public boolean matches(EntityPlayerMP player) {
            return player.getHeldItem() == stack && player.inventory.currentItem == slot
                && player.dimension == dimension;
        }

        public boolean live(long now) {
            return now - lastActiveTick <= RailgunDefaults.CHARGE_RESUME_GRACE_TICKS;
        }

        public void touch(long now) {
            lastActiveTick = now;
        }

        public int units(long now) {
            return Math.min(cap, RailgunData.units(elapsed(now), rate));
        }

        public boolean capped() {
            return cap != Integer.MAX_VALUE;
        }

        public int capAtAffordable(long now, double energy) {
            long ticks = elapsed(now);
            while (ticks > 0 && energy < chargeCostUpTo(RailgunData.units(ticks, rate), rate)) ticks--;
            cap = RailgunData.units(ticks, rate);
            return cap;
        }

        private long elapsed(long now) {
            return Math.max(0, Math.min(ElectromagneticRailgun.USE_DURATION, now - startTick));
        }
    }

    static final Map<UUID, ChargeSession> CHARGING = new HashMap<>();

    private RailgunFireService() {}

    public static void beginCharge(EntityPlayerMP player, ItemStack stack) {
        long now = now(player);
        ChargeSession session = CHARGING.get(player.getUniqueID());
        if (session != null && session.matches(player) && session.live(now)) {
            session.touch(now);
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
        if (session == null || session.stack() != stack) return;
        long now = now(player);
        session.touch(now);
        if (session.capped()) return;
        double energy = RailgunEnergy.read(stack);
        if (energy >= chargeCostUpTo(session.units(now), session.rate())) return;
        ScienceNotLeisure.network.sendTo(new RailgunChargeCapPacket(session.capAtAffordable(now, energy)), player);
        noEnergy(player);
    }

    public static void release(EntityPlayerMP player, ItemStack stack) {
        ChargeSession session = CHARGING.remove(player.getUniqueID());
        if (session == null || session.stack() != stack || !session.matches(player)) return;
        int units = session.units(now(player));
        RailgunChargeTier tier = RailgunChargeTier.fromUnits(units);
        double chargeCost = chargeCostUpTo(units, session.rate());
        if (tier == RailgunChargeTier.NONE) {
            RailgunEnergy.tryConsume(stack, chargeCost);
            return;
        }
        if (!RailgunEnergy.tryConsume(stack, chargeCost + shotCost(tier))) {
            noEnergy(player);
            return;
        }
        fireCharged((WorldServer) player.worldObj, player, stack, tier);
    }

    private static long now(EntityPlayerMP player) {
        return player.worldObj.getTotalWorldTime();
    }

    public static double chargeCostUpTo(int units, double rate) {
        var config = MainConfig.item.railgun;
        rate = Math.max(1, Math.min(3, rate));
        int ticks = ticksFor(Math.max(0, Math.min(216000, units)), rate);
        int first = Math.min(ticks, ticksFor(RailgunChargeTier.EHV2.chargeTicks, rate));
        int second = Math.min(ticks - first, ticksFor(RailgunChargeTier.EHV3.chargeTicks, rate) - first);
        return first * Math.max(0, config.chargeEuEhv1) + second * Math.max(0, config.chargeEuEhv2)
            + (ticks - first - second) * Math.max(0, config.chargeEuEhv3);
    }

    private static int ticksFor(int units, double rate) {
        return (int) Math.ceil(units / rate - RailgunData.RATE_EPSILON);
    }

    public static double shotCost(RailgunChargeTier tier) {
        var config = MainConfig.item.railgun;
        double base = switch (tier) {
            case EHV1 -> config.shotEuEhv1;
            case EHV2 -> config.shotEuEhv2;
            case EHV3 -> config.shotEuEhv3;
            default -> 0;
        };
        return Math.max(0, base) + tier.lightning * Math.max(0, config.ehvLightningEu);
    }

    public static void noEnergy(EntityPlayerMP player) {
        ScienceNotLeisure.network
            .sendTo(new StatusMessage(new ChatComponentTranslation("item.gtnl.railgun.fail.no_eu")), player);
    }

    public static void fireCharged(WorldServer world, EntityPlayerMP player, ItemStack stack, RailgunChargeTier tier) {
        var modules = RailgunData.modules(stack);
        var settings = RailgunData.settings(stack);
        boolean players = MainConfig.item.railgun.damagePlayers && settings.pvp();
        RailgunVec from = RailgunVec.eye(player);
        double range = RailgunRaycast
            .effectiveRange(RailgunDefaults.CHARGED_RANGE, modules.count(RailgunModuleType.RANGE));
        var trace = RailgunRaycast.traceFirst(
            world,
            player,
            from,
            from.add(
                RailgunVec.look(player)
                    .scale(range)),
            1,
            0.3,
            entity -> RailgunTargetRules.canAffect(player, entity, players));
        var context = RailgunDamage.buildCharged(player, tier, modules, world, players);
        RailgunVec impact = trace.hit() == null ? trace.blockEnd()
            : trace.hit()
                .position();
        EntityLivingBase primary = trace.hit() != null && trace.hit()
            .entity() instanceof EntityLivingBase living ? living : null;
        var hits = new ArrayList<RailgunChainResolver.Hit>();
        int compute = modules.count(RailgunModuleType.COMPUTE);
        double pulseRadius = tier == RailgunChargeTier.EHV3 ? RailgunDefaults.PULSE_RADIUS + 1.5 * compute : 0;
        double pulseRatio = compute == 0 ? 0.6 : compute == 1 ? 0.85 : 1;
        if (primary != null) {
            hits.add(new RailgunChainResolver.Hit(primary, context.firstDamage(), false, false));
            if (settings.chain()) hits.addAll(
                RailgunChainResolver.resolveChainForkedFrom(world, player, primary, context, new HashSet<>(), null));
            if (tier == RailgunChargeTier.EHV3) {
                hits.addAll(
                    RailgunChainResolver
                        .resolvePenetration(world, player, primary, context, RailgunDefaults.PENETRATION_MAX_TARGETS));
                hits.addAll(
                    RailgunChainResolver.resolvePulse(
                        world,
                        player,
                        RailgunVec.pos(primary),
                        pulseRadius,
                        pulseRatio,
                        context,
                        primary.getEntityId()));
            }
        }
        double splash = settings.splash() ? tier.impactRadius : 0;
        if (splash > 0) hits.addAll(
            RailgunChainResolver.resolveImpactSplash(
                world,
                player,
                impact,
                splash,
                tier.impactRatio,
                primary == null ? -1 : primary.getEntityId(),
                context));
        EntityLivingBase anchor = hits.stream()
            .filter(
                hit -> hit.pulse() && hit.target() != primary
                    && hit.target()
                        .isEntityAlive())
            .map(RailgunChainResolver.Hit::target)
            .min(
                Comparator.comparingDouble(
                    target -> RailgunVec.pos(target)
                        .distanceToSqr(impact)))
            .orElse(null);
        if (settings.chain() && anchor != null) {
            var visited = new HashSet<Integer>();
            hits.forEach(
                hit -> visited.add(
                    hit.target()
                        .getEntityId()));
            hits.addAll(RailgunChainResolver.resolveChainForkedFrom(world, player, anchor, context, visited, impact));
        }
        var path = RailgunNet.path(impact, hits);
        RailgunDamage.applyAll(player, hits, context, true, impact);
        if (trace.hit() != null && primary == null
            && RailgunTargetRules.canAffect(
                player,
                trace.hit()
                    .entity(),
                players))
            RailgunTargetRules.damageAnchor(
                trace.hit()
                    .entity(),
                impact)
                .attackEntityFrom(
                    RailgunDamage.source(player),
                    (float) Math.min(context.firstDamage(), Float.MAX_VALUE));
        if (MainConfig.item.railgun.terrainEnabled && settings.terrain()) {
            RailgunTerrainService.queueSphere(world, impact, tier.terrainRadius, tier.terrainHardness, player);
            if (tier == RailgunChargeTier.EHV3)
                for (var hit : hits) if (hit.penetration()) RailgunTerrainService.queueSphere(
                    world,
                    RailgunVec.pos(hit.target()),
                    RailgunDefaults.PENETRATION_DESTROY_RADIUS,
                    RailgunDefaults.PENETRATION_DESTROY_HARDNESS,
                    player);
            if (settings.sound()) world.playSoundEffect(
                impact.x(),
                impact.y(),
                impact.z(),
                "sciencenotleisure:railgun.fire.impact",
                1.5F + tier.ordinal() * 0.3F,
                0.5F);
        }
        applyRecoil(player, tier);
        RailgunNet.sendAround(
            world,
            player,
            new RailgunFireFxPacket(
                player.getEntityId(),
                from,
                impact,
                path,
                tier.ordinal(),
                (float) Math.max(splash, pulseRadius),
                settings.sound()));
    }

    private static void applyRecoil(EntityPlayerMP player, RailgunChargeTier tier) {
        boolean crouching = player.isSneaking();
        double speed = tier.recoilSpeed * (crouching ? RailgunDefaults.RECOIL_CROUCH_MUL : 1)
            * (player.onGround ? 1 : RailgunDefaults.RECOIL_AIRBORNE_MUL);
        float pitch = tier.recoilPitch * (crouching ? (float) RailgunDefaults.RECOIL_CROUCH_MUL : 1);
        RailgunVec back = RailgunVec.look(player)
            .scale(-1);
        RailgunVec behind = RailgunVec.pos(player)
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
            .setLong(RECOIL_GRACE, player.worldObj.getTotalWorldTime() + 60);
        ScienceNotLeisure.network.sendTo(new RailgunRecoilFxPacket(pitch, tier.ordinal()), player);
    }
}
