package com.science.gtnl.common.wireless;

import java.util.Collection;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.pathing.IPathingGrid;

/** Read-only observations; channel consumers and physical nodes are deliberately counted separately. */
public record WirelessNetworkStatistics(int nodes, int devices, int online, int missing, boolean stable) {

    public static WirelessNetworkStatistics measure(Collection<IGridNode> nodes, boolean channelsEnabled) {
        int devices = 0, online = 0, missing = 0;
        boolean stable = true;
        for (IGridNode node : nodes) {
            if (!node.hasFlag(GridFlags.REQUIRE_CHANNEL)) continue;
            devices++;
            if (node.isActive()) online++;
            if (channelsEnabled && !node.meetsChannelRequirements()) missing++;
            stable &= channelsStable(node);
        }
        return new WirelessNetworkStatistics(nodes.size(), devices, online, missing, stable);
    }

    static boolean channelsStable(IGridNode node) {
        if (node == null || node.getGrid() == null) return false;
        IPathingGrid path = node.getGrid()
            .getCache(IPathingGrid.class);
        return path != null && !path.isNetworkBooting()
            && (!(path instanceof WirelessPathingState state) || !state.gtnl$isRepathPending());
    }
}
