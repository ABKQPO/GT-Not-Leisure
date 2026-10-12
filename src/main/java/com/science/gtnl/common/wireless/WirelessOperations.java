package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessChannelManager.canBuild;

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
        APPLIED
    }

    public static class Denied extends IllegalStateException {
    }

    private WirelessOperations() {}

    public static Result toggle(EntityPlayer player, WirelessCardBinding binding, Address target)
        throws FailedConnection {
        return change(player, binding, target, false);
    }

    public static Result ensureConnected(EntityPlayer player, WirelessCardBinding binding, Address target)
        throws FailedConnection {
        return change(player, binding, target, true);
    }

    private static Result change(EntityPlayer player, WirelessCardBinding binding, Address target, boolean automatic)
        throws FailedConnection {
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

    static boolean rename(EntityPlayer player, Address source, WirelessLinkView view, String name) {
        if (!canBuild(view.node(), player) || !canBuild(source.node(), player)) throw new Denied();
        return WirelessClusterManager.rename(source, view, name, node -> canBuild(node, player));
    }

    static void setPaused(EntityPlayer player, Address source, WirelessLinkView view) {
        IGridNode controller = source.node();
        if (view.target()
            .node() != view.node() || !canBuild(view.node(), player)
            || controller != null && !canBuild(controller, player)
            || view.paused() && controller == null) throw new Denied();
        WirelessClusterManager.setPaused(source, view, !view.paused());
    }

    static Result teleport(EntityPlayerMP player, Address source, WirelessLinkView view) {
        if (!WirelessClusterManager.isCurrentLink(source, view)) return Result.STALE;
        IGridNode controller = source.node();
        if (controller == null) return Result.UNAVAILABLE;
        if (!canBuild(controller, player) || !canBuild(view.node(), player)) return Result.DENIED;
        return WirelessCardTeleport.teleport(player, view.target()) ? Result.APPLIED : Result.UNCHANGED;
    }
}
