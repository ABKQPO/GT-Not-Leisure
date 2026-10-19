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

import com.science.gtnl.common.equipment.EquipmentEnergy;
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.common.item.items.ElectromagneticRailgun;
import com.science.gtnl.common.packet.RailgunBeamChainFxPacket;
import com.science.gtnl.common.packet.RailgunBeamUpdatePacket;
import com.science.gtnl.common.railgun.RailgunChainResolver.Hit;
import com.science.gtnl.config.MainConfig;

public final class RailgunBeamService {

    private static final class BeamState {

        final ItemStack stack;
        final int slot, dimension;
        int lastSettleTick, lastChainTick;
        EquipmentVec lastSentEnd;
        boolean lastSentEhv;

        BeamState(EntityPlayerMP player) {
            stack = player.getHeldItem();
            slot = player.inventory.currentItem;
            dimension = player.dimension;
            lastSettleTick = player.ticksExisted - RailgunDefaults.BEAM_SETTLE_INTERVAL_TICKS;
            lastChainTick = player.ticksExisted - RailgunDefaults.BEAM_CHAIN_THROTTLE_TICKS;
        }

        boolean holds(EntityPlayerMP player) {
            return !player.isDead && player.getHeldItem() == stack
                && player.inventory.currentItem == slot
                && player.dimension == dimension
                && !player.isUsingItem()
                && player.openContainer == player.inventoryContainer;
        }
    }

    private static final Map<UUID, BeamState> ACTIVE = new HashMap<>();
    private static final Map<UUID, Long> USE_CONFLICT = new HashMap<>();
    private static final Set<UUID> DRAINED = new HashSet<>();

    private RailgunBeamService() {}

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
        USE_CONFLICT.remove(id);
        if (ACTIVE.size() >= RailgunDefaults.BEAM_MAX_CONCURRENT) {
            broadcastStop(player);
            return;
        }
        ACTIVE.put(id, new BeamState(player));
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
        RailgunFireService.forget(player);
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
                if (player.ticksExisted - state.lastSettleTick < RailgunDefaults.BEAM_SETTLE_INTERVAL_TICKS) continue;
                state.lastSettleTick = player.ticksExisted;
                if (settle(player, state.stack, state)) continue;
            }
            iterator.remove();
            broadcastStop(player);
        }
    }

    private static boolean settle(EntityPlayerMP player, ItemStack stack, BeamState state) {
        var config = MainConfig.item.railgun;
        var modules = RailgunData.modules(stack);
        boolean pvp = RailgunSetting.PVP.active(stack), ehv = RailgunSetting.EHV_BEAM.active(stack);
        double cost = ehv ? Math.max(0, config.ehvBeamEuPerSettle) + 16 * Math.max(0, config.ehvLightningEu)
            : Math.max(0, config.beamEuPerSettle) + Math.max(0, config.hvLightningEu) / 8;
        if (!EquipmentEnergy.drain(player, stack, cost)) {
            DRAINED.add(player.getUniqueID());
            RailgunFireService.noEnergy(player);
            return false;
        }
        WorldServer world = (WorldServer) player.worldObj;
        EquipmentVec from = EquipmentVec.eye(player);
        double range = RailgunRaycast
            .effectiveRange(RailgunDefaults.BEAM_RANGE, modules.count(RailgunModuleType.RANGE));
        var trace = RailgunRaycast.traceFirst(
            world,
            player,
            from,
            from.add(
                EquipmentVec.look(player)
                    .scale(range)),
            0.5,
            0,
            entity -> RailgunTargetRules.canAffect(player, entity, pvp));
        EquipmentVec end = trace.hit() == null ? trace.blockEnd()
            : EquipmentVec.center(
                trace.hit()
                    .entity());
        EquipmentVec hitPoint = trace.hit() == null ? trace.blockEnd()
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
            && player.ticksExisted - state.lastChainTick >= RailgunDefaults.BEAM_CHAIN_THROTTLE_TICKS) {
            state.lastChainTick = player.ticksExisted;
            EquipmentVec origin = anchor == null ? end : EquipmentVec.center(anchor);
            var chain = anchor == null ? RailgunChainResolver.chainFromPoint(world, player, origin, context)
                : RailgunChainResolver.chainFrom(world, player, anchor, context, Set.of(), origin);
            var path = RailgunNet.path(origin, chain);
            RailgunDamage.applyAll(player, chain, context, false, hitPoint);
            if (!path.isEmpty()) RailgunNet.sendAround(
                player,
                new RailgunBeamChainFxPacket(
                    player.getEntityId(),
                    origin,
                    path,
                    RailgunSetting.SOUND.active(stack),
                    ehv));
        }
        sendUpdate(player, state, from, end, ehv);
        return true;
    }

    private static void sendUpdate(EntityPlayerMP player, BeamState state, EquipmentVec from, EquipmentVec end,
        boolean ehv) {
        boolean moved = state.lastSentEnd == null || state.lastSentEhv != ehv
            || state.lastSentEnd.distanceToSqr(end)
                > RailgunDefaults.BEAM_RESEND_DISTANCE * RailgunDefaults.BEAM_RESEND_DISTANCE;
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
