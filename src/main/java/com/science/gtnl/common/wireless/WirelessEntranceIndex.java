package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.science.gtnl.common.wireless.PhysicalClusterTracker.Cluster;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessChannelManager.Entrance;

import appeng.api.networking.IGridNode;

/** One maintenance pass's entrance groups. Native connection liveness is still checked by callers. */
final class WirelessEntranceIndex {

    private final PhysicalClusterTracker<IGridNode, Address> tracker;
    private final Map<Cluster<IGridNode, Address>, List<Entrance>> byCluster = new IdentityHashMap<>();
    private final Map<IGridNode, List<Entrance>> byNode = new IdentityHashMap<>();
    private List<Entrance> entrances;
    private List<Cluster<IGridNode, Address>> clusters;

    WirelessEntranceIndex(PhysicalClusterTracker<IGridNode, Address> tracker) {
        this.tracker = tracker;
    }

    List<Entrance> forCluster(Cluster<IGridNode, Address> cluster) {
        refresh();
        return byCluster.getOrDefault(cluster, List.of());
    }

    List<Entrance> atNode(IGridNode node) {
        refresh();
        return byNode.getOrDefault(node, List.of());
    }

    private void refresh() {
        List<Entrance> currentEntrances = WirelessChannelManager.entrances();
        List<Cluster<IGridNode, Address>> currentClusters = tracker.clusters();
        if (entrances == currentEntrances && clusters == currentClusters) return;
        byCluster.clear();
        byNode.clear();
        for (Entrance entrance : currentEntrances) {
            byNode.computeIfAbsent(entrance.targetNode(), ignored -> new ArrayList<>())
                .add(entrance);
            Cluster<IGridNode, Address> cluster = tracker.clusterOf(entrance.targetNode());
            if (cluster != null) byCluster.computeIfAbsent(cluster, ignored -> new ArrayList<>())
                .add(entrance);
        }
        byCluster.replaceAll((cluster, entries) -> List.copyOf(entries));
        byNode.replaceAll((node, entries) -> List.copyOf(entries));
        entrances = currentEntrances;
        clusters = currentClusters;
    }
}
