package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessChannelManager.canBuild;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessChannelManager.Entrance;
import com.science.gtnl.common.wireless.WirelessFrequencies.Structure;

import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.pathing.IPathingGrid;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class WirelessOperations {

    public static final int ADOPT_CONFIRM_TICKS = 100;
    private static final Map<UUID, Adoption> ADOPTIONS = new Object2ObjectOpenHashMap<>();

    public enum Result {
        CONNECTED,
        DISCONNECTED,
        UNCHANGED,
        WAITING,
        DENIED,
        UNAVAILABLE,
        STALE,
        APPLIED,
        CONFLICT,
        CARD_REQUIRED,
        DEFERRED
    }

    public static class Denied extends IllegalStateException {
    }

    private record Adoption(WirelessFrequency frequency, Address controller, long tick) {}

    public static Result toggle(EntityPlayer player, WirelessCardBinding binding, Address target)
        throws FailedConnection {
        return change(player, binding, target, false, () -> true);
    }

    public static Result ensureConnected(EntityPlayer player, WirelessCardBinding binding, Address target,
        BooleanSupplier budget) throws FailedConnection {
        return change(player, binding, target, true, budget);
    }

    private static Result change(EntityPlayer player, WirelessCardBinding binding, Address target, boolean automatic,
        BooleanSupplier budget) throws FailedConnection {
        IGridNode node = target == null ? null : target.node();
        if (node == null) return Result.UNAVAILABLE;
        if (binding == null || !binding.belongsTo(player.getUniqueID()) || !canBuild(node, player))
            return Result.DENIED;
        WirelessFrequency frequency = binding.frequency();
        IGridNode source = WirelessFrequencies.controllerNode(frequency);
        // A placement task must retry initialization before evaluating permissions on a nonexistent grid.
        if (automatic && (source == null || source.getGrid() == null)) return Result.WAITING;
        if (source != null && !canBuild(source, player)) return Result.DENIED;
        if (automatic && !AutomaticWirelessEntrances.isSettled(
            source.getGrid()
                .getCache(IPathingGrid.class)))
            return Result.WAITING;
        if (!budget.getAsBoolean()) return Result.DEFERRED;
        try (var ignored = WirelessLinkData.readBatch()) {
            var operation = WirelessClusterManager.prepareLink(frequency, target);
            // Reconciliation can merge AE grids and change their security caches without another physical scan.
            source = WirelessFrequencies.controllerNode(frequency);
            if (!canBuild(target.node(), player) || source != null && !canBuild(source, player)) return Result.DENIED;
            if (operation.linked()) {
                if (automatic) return Result.UNCHANGED;
                operation.disconnect(); // An unavailable source must not prevent removing a suspended binding.
                return Result.DISCONNECTED;
            }
            if (source == null) return Result.UNAVAILABLE;
            operation.connect();
            return Result.CONNECTED;
        }
    }

    public static Result claim(EntityPlayer player, WirelessFrequency source, Address target,
        Predicate<List<Address>> clusterAgrees) {
        IGridNode node = target.node();
        if (node == null || node.getGrid() == null) return Result.WAITING;
        try (var ignored = WirelessLinkData.readBatch()) {
            var operation = WirelessClusterManager.prepareLink(source, target);
            // Disagreement must win over a missing source or permission, or dropping one copy decides the rest.
            if (!clusterAgrees.test(operation.internalAddresses())) return Result.CONFLICT;
            if (!WirelessCardInventory.hasBoundCard(player, source)) return Result.CARD_REQUIRED;
            if (!canBuild(target.node(), player)) return Result.DENIED;
            IGridNode controller = WirelessFrequencies.controllerNode(source);
            if (controller == null || controller.getGrid() == null) return Result.UNAVAILABLE;
            if (!canBuild(controller, player)) return Result.DENIED;
            return operation.claim() ? Result.APPLIED : Result.UNCHANGED;
        }
    }

    public static boolean adoptable(WirelessCardBinding binding) {
        return binding != null && !binding.frequency()
            .isLegacy() && WirelessFrequencies.controller(binding.frequency()) == null;
    }

    public static Result adopt(EntityPlayer player, WirelessCardBinding binding, TileEntity controller) {
        UUID creator = WirelessFrequencies.creator(binding.frequency());
        if (!binding.belongsTo(player.getUniqueID()) || creator == null
            || !creator.equals(player.getUniqueID())
            || !canBuild(
                WirelessLinkPersistence.address(controller)
                    .node(),
                player))
            return Result.DENIED;
        Structure structure = WirelessFrequencies.structure(controller);
        long tick = player.worldObj.getTotalWorldTime();
        Adoption previous = ADOPTIONS.remove(player.getUniqueID());
        if (previous == null || !previous.frequency()
            .equals(binding.frequency())
            || !structure.contains(previous.controller())
            || tick - previous.tick() > ADOPT_CONFIRM_TICKS) {
            ADOPTIONS.put(
                player.getUniqueID(),
                new Adoption(binding.frequency(), WirelessLinkPersistence.address(controller), tick));
            return Result.WAITING;
        }
        if (WirelessFrequencies.adopt(controller, binding.frequency()) == null) return Result.UNAVAILABLE;
        WirelessClusterManager.invalidateTopology();
        return Result.APPLIED;
    }

    public static int debugConnect(WirelessFrequency source, Address target) throws FailedConnection {
        try (var ignored = WirelessLinkData.readBatch()) {
            return WirelessClusterManager.prepareLink(source, target)
                .connect();
        }
    }

    public static boolean debugDisconnect(Entrance entrance) {
        if (entrance.target()
            .node() != entrance.targetNode()) return false;
        try (var ignored = WirelessLinkData.readBatch()) {
            return WirelessClusterManager.prepareLink(entrance.frequency(), entrance.target())
                .disconnect();
        }
    }

    static boolean rename(EntityPlayer player, WirelessFrequency source, IWirelessLinkView view, String name) {
        if (!canBuild(view.node(), player) || !canBuild(WirelessFrequencies.controllerNode(source), player))
            throw new Denied();
        return WirelessClusterManager.rename(source, view, name, node -> canBuild(node, player));
    }

    static void setPaused(EntityPlayer player, WirelessFrequency source, IWirelessLinkView view) {
        IGridNode controller = WirelessFrequencies.controllerNode(source);
        if (view.target()
            .node() != view.node() || !canBuild(view.node(), player)
            || controller != null && !canBuild(controller, player)
            || view.paused() && controller == null) throw new Denied();
        WirelessClusterManager.setPaused(source, view, !view.paused());
    }

    static Result teleport(EntityPlayerMP player, WirelessFrequency source, IWirelessLinkView view) {
        if (!WirelessClusterManager.isCurrentLink(source, view)) return Result.STALE;
        IGridNode controller = WirelessFrequencies.controllerNode(source);
        if (controller == null) return Result.UNAVAILABLE;
        if (!canBuild(controller, player) || !canBuild(view.node(), player)) return Result.DENIED;
        return WirelessCardTeleport.teleport(player, view.target()) ? Result.APPLIED : Result.UNCHANGED;
    }

    static void clear() {
        ADOPTIONS.clear();
    }
}
