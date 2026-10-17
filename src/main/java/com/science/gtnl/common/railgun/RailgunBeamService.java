package com.science.gtnl.common.railgun;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.packet.RailgunBeamChainFxPacket;
import com.science.gtnl.common.packet.RailgunBeamUpdatePacket;
import com.science.gtnl.config.MainConfig;

public final class RailgunBeamService {

    private static final class BeamState {

        final ItemStack stack;
        final int slot, dimension;
        int lastSettleTick, lastChainTick, settleCount;
        RailgunVec lastSentEnd;
        boolean lastSentEhv;

        BeamState(EntityPlayerMP player) {
            stack = player.getHeldItem();
            slot = player.inventory.currentItem;
            dimension = player.dimension;
            lastSettleTick = player.ticksExisted - RailgunDefaults.BEAM_SETTLE_INTERVAL_TICKS;
            lastChainTick = player.ticksExisted - RailgunDefaults.BEAM_CHAIN_THROTTLE_TICKS;
        }
    }

    private static final Map<UUID, BeamState> ACTIVE = new HashMap<>();
    private static final Map<UUID, Long> USE_CONFLICT = new HashMap<>();
    private static final Set<UUID> DRAINED = new HashSet<>();

    private RailgunBeamService() {}

    public static void setFiring(EntityPlayerMP player, boolean firing) {
        UUID id = player.getUniqueID();
        if (!firing) {
            DRAINED.remove(id);
            USE_CONFLICT.remove(id);
            stop(player);
            return;
        }
        ItemStack stack = player.getHeldItem();
        if (player.isDead || stack == null
            || !(stack.getItem() instanceof ElectromagneticRailgun)
            || player.openContainer != player.inventoryContainer
            || DRAINED.contains(id)
            || ACTIVE.containsKey(id)) return;
        if (player.isUsingItem() && !resolveUseConflict(player)) return;
        USE_CONFLICT.remove(id);
        if (ACTIVE.size() >= RailgunDefaults.BEAM_MAX_CONCURRENT) {
            broadcastStop(player);
            return;
        }
        ACTIVE.put(player.getUniqueID(), new BeamState(player));
    }

    private static boolean resolveUseConflict(EntityPlayerMP player) {
        long now = player.worldObj.getTotalWorldTime();
        Long first = USE_CONFLICT.get(player.getUniqueID());
        if (first == null || now - first > RailgunDefaults.BEAM_USE_CONFLICT_TICKS * 3L) {
            USE_CONFLICT.put(player.getUniqueID(), now);
            return false;
        }
        if (now - first < RailgunDefaults.BEAM_USE_CONFLICT_TICKS) return false;
        player.clearItemInUse();
        RailgunFireService.CHARGING.remove(player.getUniqueID());
        return true;
    }

    public static void forget(EntityPlayerMP player) {
        stop(player);
        USE_CONFLICT.remove(player.getUniqueID());
        DRAINED.remove(player.getUniqueID());
    }

    public static void stop(EntityPlayerMP player) {
        if (ACTIVE.remove(player.getUniqueID()) != null) broadcastStop(player);
    }

    public static void unload(int dimension) {
        ACTIVE.entrySet()
            .removeIf(entry -> entry.getValue().dimension == dimension);
    }

    public static void tick() {
        var iterator = ACTIVE.entrySet()
            .iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            EntityPlayerMP player = RailgunServerEvents.player(entry.getKey());
            BeamState state = entry.getValue();
            if (player == null) {
                iterator.remove();
                continue;
            }
            ItemStack stack = player.getHeldItem();
            if (player.isDead || stack != state.stack
                || player.inventory.currentItem != state.slot
                || player.dimension != state.dimension
                || player.isUsingItem()
                || player.openContainer != player.inventoryContainer) {
                iterator.remove();
                broadcastStop(player);
                continue;
            }
            if (player.ticksExisted - state.lastSettleTick < RailgunDefaults.BEAM_SETTLE_INTERVAL_TICKS) continue;
            state.lastSettleTick = player.ticksExisted;
            state.settleCount++;
            if (!settle(player, stack, state)) {
                iterator.remove();
                broadcastStop(player);
            }
        }
    }

    private static boolean settle(EntityPlayerMP player, ItemStack stack, BeamState state) {
        if (player.isUsingItem()) return false;
        var config = MainConfig.item.railgun;
        var modules = RailgunData.modules(stack);
        var settings = RailgunData.settings(stack);
        boolean pvp = config.damagePlayers && settings.pvp();
        boolean ehv = settings.ehvBeam() && modules.count(RailgunModuleType.EHV_BEAM) > 0;
        double cost = ehv ? Math.max(0, config.ehvBeamEuPerSettle) + 16 * Math.max(0, config.ehvLightningEu)
            : Math.max(0, config.beamEuPerSettle) + Math.max(0, config.hvLightningEu) / 8;
        if (!RailgunEnergy.tryConsume(stack, cost)) {
            DRAINED.add(player.getUniqueID());
            RailgunFireService.noEnergy(player);
            return false;
        }
        RailgunData.keepInUse(player, stack);
        WorldServer world = (WorldServer) player.worldObj;
        RailgunVec from = RailgunVec.eye(player);
        double range = RailgunRaycast
            .effectiveRange(RailgunDefaults.BEAM_RANGE, modules.count(RailgunModuleType.RANGE));
        var trace = RailgunRaycast.traceFirst(
            world,
            player,
            from,
            from.add(
                RailgunVec.look(player)
                    .scale(range)),
            0.5,
            0,
            entity -> RailgunTargetRules.canAffect(player, entity, pvp));
        RailgunVec end = trace.hit() == null ? trace.blockEnd()
            : RailgunVec.center(
                trace.hit()
                    .entity());
        RailgunVec hitPoint = trace.hit() == null ? trace.blockEnd()
            : trace.hit()
                .position();
        var context = RailgunDamage
            .buildBeam(player, modules, world, pvp, ehv ? config.ehvBeamDamage : config.beamDamage);
        EntityLivingBase anchor = trace.hit() != null && trace.hit()
            .entity() instanceof EntityLivingBase living ? living : null;
        if (anchor != null) RailgunDamage.applyAll(
            player,
            List.of(new RailgunChainResolver.Hit(anchor, context.firstDamage(), false, false)),
            context,
            ehv,
            hitPoint);
        else if (trace.hit() != null) RailgunTargetRules.damageAnchor(
            trace.hit()
                .entity(),
            hitPoint)
            .attackEntityFrom(RailgunDamage.source(player), (float) Math.min(context.firstDamage(), Float.MAX_VALUE));
        if (settings.chain()
            && player.ticksExisted - state.lastChainTick >= RailgunDefaults.BEAM_CHAIN_THROTTLE_TICKS) {
            state.lastChainTick = player.ticksExisted;
            RailgunVec origin = anchor == null ? end : RailgunVec.center(anchor);
            var chain = anchor == null ? RailgunChainResolver.resolveChainFromPoint(world, player, origin, context)
                : RailgunChainResolver.resolveChainFrom(world, player, anchor, context, new HashSet<>(), null);
            var path = RailgunNet.path(origin, chain);
            RailgunDamage.applyAll(player, chain, context, false, hitPoint);
            if (!path.isEmpty()) RailgunNet.sendAround(
                world,
                player,
                new RailgunBeamChainFxPacket(player.getEntityId(), origin, path, settings.sound(), ehv));
        }
        sendUpdate(world, player, state, from, end, ehv);
        return true;
    }

    private static void sendUpdate(WorldServer world, EntityPlayerMP player, BeamState state, RailgunVec from,
        RailgunVec end, boolean ehv) {
        boolean moved = state.lastSentEnd == null || state.lastSentEhv != ehv
            || state.lastSentEnd.distanceToSqr(end)
                > RailgunDefaults.BEAM_RESEND_DISTANCE * RailgunDefaults.BEAM_RESEND_DISTANCE;
        state.lastSentEnd = end;
        state.lastSentEhv = ehv;
        double range = from.subtract(end)
            .length();
        RailgunNet.sendAround(
            world,
            player,
            moved ? RailgunBeamUpdatePacket.move(player.getEntityId(), range, ehv)
                : RailgunBeamUpdatePacket.hold(player.getEntityId(), range, ehv));
    }

    private static void broadcastStop(EntityPlayerMP player) {
        RailgunNet.sendAround(player.worldObj, player, RailgunBeamUpdatePacket.stop(player.getEntityId()));
    }
}
