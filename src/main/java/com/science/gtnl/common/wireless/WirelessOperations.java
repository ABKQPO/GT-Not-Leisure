package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessChannelManager.canBuild;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessChannelManager.Entrance;

import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.pathing.IPathingGrid;

public class WirelessOperations {

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
        IGridNode source = binding.source()
            .node();
        // A placement task must retry initialization before evaluating permissions on a nonexistent grid.
        if (automatic && (source == null || source.getGrid() == null)) return Result.WAITING;
        if (source != null && !canBuild(source, player)) return Result.DENIED;
        if (automatic && !AutomaticWirelessEntrances.isSettled(
            source.getGrid()
                .getCache(IPathingGrid.class)))
            return Result.WAITING;
        if (!budget.getAsBoolean()) return Result.DEFERRED;
        try (var ignored = WirelessLinkData.readBatch()) {
            var operation = WirelessClusterManager.prepareLink(binding.source(), target);
            // Reconciliation can merge AE grids and change their security caches without another physical scan.
            source = binding.source()
                .node();
            if (!canBuild(target.node(), player) || source != null && !canBuild(source, player)) return Result.DENIED;
            if (operation.linked()) {
                if (automatic) return Result.UNCHANGED;
                operation.disconnect(); // An unavailable source must not prevent removing a suspended binding.
                return Result.DISCONNECTED;
            }
            if (source == null || !WirelessChannelManager.isSupportedController(
                binding.source()
                    .tile()))
                return Result.UNAVAILABLE;
            operation.connect();
            return Result.CONNECTED;
        }
    }

    public static Result claim(EntityPlayer player, Address source, Address target,
        Predicate<List<Address>> clusterAgrees) {
        IGridNode node = target.node();
        if (node == null || node.getGrid() == null) return Result.WAITING;
        try (var ignored = WirelessLinkData.readBatch()) {
            var operation = WirelessClusterManager.prepareLink(source, target);
            // Disagreement must win over a missing source or permission, or dropping one copy decides the rest.
            if (!clusterAgrees.test(operation.internalAddresses())) return Result.CONFLICT;
            if (!WirelessCardInventory.hasBoundCard(player, source)) return Result.CARD_REQUIRED;
            if (!canBuild(target.node(), player)) return Result.DENIED;
            IGridNode controller = WirelessChannelManager.isSupportedController(source.tile()) ? source.node() : null;
            if (controller == null || controller.getGrid() == null) return Result.UNAVAILABLE;
            if (!canBuild(controller, player)) return Result.DENIED;
            return operation.claim() ? Result.APPLIED : Result.UNCHANGED;
        }
    }

    public static int debugConnect(Address source, Address target) throws FailedConnection {
        try (var ignored = WirelessLinkData.readBatch()) {
            return WirelessClusterManager.prepareLink(source, target)
                .connect();
        }
    }

    public static boolean debugDisconnect(Entrance entrance) {
        if (entrance.target()
            .node() != entrance.targetNode()) return false;
        try (var ignored = WirelessLinkData.readBatch()) {
            return WirelessClusterManager.prepareLink(entrance.source(), entrance.target())
                .disconnect();
        }
    }

    static boolean rename(EntityPlayer player, Address source, IWirelessLinkView view, String name) {
        if (!canBuild(view.node(), player) || !canBuild(source.node(), player)) throw new Denied();
        return WirelessClusterManager.rename(source, view, name, node -> canBuild(node, player));
    }

    static void setPaused(EntityPlayer player, Address source, IWirelessLinkView view) {
        IGridNode controller = source.node();
        if (view.target()
            .node() != view.node() || !canBuild(view.node(), player)
            || controller != null && !canBuild(controller, player)
            || view.paused() && controller == null) throw new Denied();
        WirelessClusterManager.setPaused(source, view, !view.paused());
    }

    static Result teleport(EntityPlayerMP player, Address source, IWirelessLinkView view) {
        if (!WirelessClusterManager.isCurrentLink(source, view)) return Result.STALE;
        IGridNode controller = source.node();
        if (controller == null) return Result.UNAVAILABLE;
        if (!canBuild(controller, player) || !canBuild(view.node(), player)) return Result.DENIED;
        return WirelessCardTeleport.teleport(player, view.target()) ? Result.APPLIED : Result.UNCHANGED;
    }
}
