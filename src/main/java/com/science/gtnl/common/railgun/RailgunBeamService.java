package com.science.gtnl.common.railgun;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.item.items.equipment.ElectromagneticRailgun;
import com.science.gtnl.common.packet.RailgunBeamChainFxPacket;
import com.science.gtnl.common.packet.RailgunBeamUpdatePacket;
import com.science.gtnl.common.railgun.RailgunChainResolver.Hit;
import com.science.gtnl.config.ItemConfig;

import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class RailgunBeamService {

    private static final int BEAM_CHAIN_THROTTLE_TICKS = 5;
    private static final int BEAM_MAX_CONCURRENT = 16;
    private static final double BEAM_RESEND_DISTANCE = 0.02;
    private static final int BEAM_SETTLE_INTERVAL_TICKS = 2;
    private static final int BEAM_USE_CONFLICT_TICKS = 20, BEAM_USE_CONFLICT_WINDOWS = 3;
    private static final double BEAM_TRACE_PADDING = 0.5, BEAM_TRACE_INFLATION = 0;
    private static final double EHV_BEAM_LIGHTNINGS_PER_SETTLE = 16;
    private static final double HV_BEAM_LIGHTNINGS_PER_SETTLE = 0.125;

    private static class BeamState {

        final ItemStack stack;
        final int slot, dimension;
        int lastSettleTick, lastChainTick;
        Vec3d lastSentEnd;
        boolean lastSentEhv;

        BeamState(EntityPlayerMP player) {
            stack = player.getHeldItem();
            slot = player.inventory.currentItem;
            dimension = player.dimension;
            lastSettleTick = player.ticksExisted - BEAM_SETTLE_INTERVAL_TICKS;
            lastChainTick = player.ticksExisted - BEAM_CHAIN_THROTTLE_TICKS;
        }

        boolean holds(EntityPlayerMP player) {
            return !player.isDead && player.getHeldItem() == stack
                && player.inventory.currentItem == slot
                && player.dimension == dimension
                && !player.isUsingItem()
                && player.openContainer == player.inventoryContainer;
        }
    }

    private static final Map<UUID, BeamState> ACTIVE = new Object2ObjectOpenHashMap<>();
    private static final Object2LongOpenHashMap<UUID> USE_CONFLICT = new Object2LongOpenHashMap<>();
    private static final Set<UUID> DRAINED = new ObjectOpenHashSet<>();

    public static void setFiring(EntityPlayerMP player, boolean firing) {
        UUID id = player.getUniqueID();
        if (!firing) {
            forget(player);
            return;
        }
        if (player.isDead || !ElectromagneticRailgun.is(player.getHeldItem())
            || player.openContainer != player.inventoryContainer
            || DRAINED.contains(id)
            || ACTIVE.containsKey(id)) return;
        if (player.isUsingItem() && !resolveUseConflict(player)) return;
        USE_CONFLICT.removeLong(id);
        if (ACTIVE.size() >= BEAM_MAX_CONCURRENT) {
            broadcastStop(player);
            return;
        }
        ACTIVE.put(id, new BeamState(player));
    }

    private static boolean resolveUseConflict(EntityPlayerMP player) {
        long now = player.worldObj.getTotalWorldTime();
        UUID id = player.getUniqueID();
        long first = USE_CONFLICT.getLong(id);
        if (!USE_CONFLICT.containsKey(id) || now - first > (long) BEAM_USE_CONFLICT_TICKS * BEAM_USE_CONFLICT_WINDOWS) {
            USE_CONFLICT.put(id, now);
            return false;
        }
        if (now - first < BEAM_USE_CONFLICT_TICKS) return false;
        player.clearItemInUse();
        RailgunFireService.forget(player);
        return true;
    }

    public static void forget(EntityPlayerMP player) {
        stop(player);
        USE_CONFLICT.removeLong(player.getUniqueID());
        DRAINED.remove(player.getUniqueID());
    }

    public static void stop(EntityPlayerMP player) {
        if (ACTIVE.remove(player.getUniqueID()) != null) broadcastStop(player);
    }

    public static void unload(int dimension) {
        ACTIVE.values()
            .removeIf(state -> state.dimension == dimension);
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
            if (state.holds(player)) {
                if (player.ticksExisted - state.lastSettleTick < BEAM_SETTLE_INTERVAL_TICKS) continue;
                state.lastSettleTick = player.ticksExisted;
                if (settle(player, state.stack, state)) continue;
            }
            iterator.remove();
            broadcastStop(player);
        }
    }

    private static boolean settle(EntityPlayerMP player, ItemStack stack, BeamState state) {
        var config = ItemConfig.railgun;
        var modules = RailgunData.modules(stack);
        boolean pvp = RailgunSetting.PVP.active(stack), ehv = RailgunSetting.EHV_BEAM.active(stack);
        double cost = ehv
            ? Math.max(0, config.ehvBeamEuPerSettle)
                + EHV_BEAM_LIGHTNINGS_PER_SETTLE * Math.max(0, config.ehvLightningEu)
            : Math.max(0, config.beamEuPerSettle) + HV_BEAM_LIGHTNINGS_PER_SETTLE * Math.max(0, config.hvLightningEu);
        if (!EquipmentEnergy.drain(player, stack, cost)) {
            DRAINED.add(player.getUniqueID());
            RailgunFireService.noEnergy(player);
            return false;
        }
        WorldServer world = (WorldServer) player.worldObj;
        Vec3d from = Vec3d.eye(player);
        double range = RailgunRaycast.effectiveRange(RailgunData.BEAM_RANGE, modules.count(RailgunModule.RANGE));
        var trace = RailgunRaycast.traceFirst(
            world,
            player,
            from,
            from.add(
                Vec3d.look(player)
                    .scale(range)),
            BEAM_TRACE_PADDING,
            BEAM_TRACE_INFLATION,
            entity -> RailgunTargetRules.canAffect(player, entity, pvp));
        Vec3d end = trace.hit() == null ? trace.blockEnd()
            : Vec3d.center(
                trace.hit()
                    .entity());
        Vec3d hitPoint = trace.hit() == null ? trace.blockEnd()
            : trace.hit()
                .position();
        var context = RailgunDamage
            .buildBeam(player, modules, world, pvp, ehv ? config.ehvBeamDamage : config.beamDamage);
        EntityLivingBase anchor = trace.hit() != null && trace.hit()
            .entity() instanceof EntityLivingBase living ? living : null;
        if (anchor != null)
            RailgunDamage.applyAll(player, List.of(Hit.primary(anchor, context.firstDamage())), context, ehv, hitPoint);
        else if (trace.hit() != null) RailgunDamage.hitObject(
            player,
            trace.hit()
                .entity(),
            hitPoint,
            context.firstDamage());
        if (RailgunSetting.CHAIN.active(stack)
            && player.ticksExisted - state.lastChainTick >= BEAM_CHAIN_THROTTLE_TICKS) {
            state.lastChainTick = player.ticksExisted;
            Vec3d origin = anchor == null ? end : Vec3d.center(anchor);
            var chain = anchor == null ? RailgunChainResolver.chainFromPoint(world, player, origin, context)
                : RailgunChainResolver.chainFrom(world, player, anchor, context, IntSets.emptySet(), origin);
            var path = RailgunNet.path(origin, chain);
            RailgunDamage.applyAll(player, chain, context, false, hitPoint);
            if (!path.isEmpty())
                RailgunNet.sendAround(player, new RailgunBeamChainFxPacket(player.getEntityId(), origin, path, ehv));
        }
        sendUpdate(player, state, from, end, ehv);
        return true;
    }

    private static void sendUpdate(EntityPlayerMP player, BeamState state, Vec3d from, Vec3d end, boolean ehv) {
        boolean moved = state.lastSentEnd == null || state.lastSentEhv != ehv
            || state.lastSentEnd.distanceToSqr(end) > BEAM_RESEND_DISTANCE * BEAM_RESEND_DISTANCE;
        state.lastSentEnd = end;
        state.lastSentEhv = ehv;
        double range = from.subtract(end)
            .length();
        RailgunNet.sendAround(
            player,
            moved ? RailgunBeamUpdatePacket.move(player.getEntityId(), range, ehv)
                : RailgunBeamUpdatePacket.hold(player.getEntityId(), range, ehv));
    }

    private static void broadcastStop(EntityPlayerMP player) {
        RailgunNet.sendAround(player, RailgunBeamUpdatePacket.stop(player.getEntityId()));
    }
}
