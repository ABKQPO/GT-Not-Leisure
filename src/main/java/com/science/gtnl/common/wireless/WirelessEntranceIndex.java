package com.science.gtnl.common.wireless;

import java.util.List;
import java.util.Map;

import com.science.gtnl.common.wireless.PhysicalClusterTracker.Cluster;
import com.science.gtnl.common.wireless.WirelessChannelManager.Entrance;

import appeng.api.networking.IGridNode;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

public class WirelessEntranceIndex {

    private final PhysicalClusterTracker<IGridNode, WirelessFrequency> tracker;
    private final Map<Cluster<IGridNode, WirelessFrequency>, List<Entrance>> byCluster = new Reference2ObjectOpenHashMap<>();
    private final Map<IGridNode, List<Entrance>> byNode = new Reference2ObjectOpenHashMap<>();
    private List<Entrance> entrances;
    private List<Cluster<IGridNode, WirelessFrequency>> clusters;

    WirelessEntranceIndex(PhysicalClusterTracker<IGridNode, WirelessFrequency> tracker) {
        this.tracker = tracker;
    }

    List<Entrance> forCluster(Cluster<IGridNode, WirelessFrequency> cluster) {
        refresh();
        return byCluster.getOrDefault(cluster, List.of());
    }

    List<Entrance> atNode(IGridNode node) {
        refresh();
        return byNode.getOrDefault(node, List.of());
    }

    private void refresh() {
        List<Entrance> currentEntrances = WirelessChannelManager.entrances();
        List<Cluster<IGridNode, WirelessFrequency>> currentClusters = tracker.clusters();
        if (entrances == currentEntrances && clusters == currentClusters) return;
        byCluster.clear();
        byNode.clear();
        for (Entrance entrance : currentEntrances) {
            byNode.computeIfAbsent(entrance.targetNode(), ignored -> new ObjectArrayList<>())
                .add(entrance);
            Cluster<IGridNode, WirelessFrequency> cluster = tracker.clusterOf(entrance.targetNode());
            if (cluster != null) byCluster.computeIfAbsent(cluster, ignored -> new ObjectArrayList<>())
                .add(entrance);
        }
        byCluster.replaceAll((cluster, entries) -> List.copyOf(entries));
        byNode.replaceAll((node, entries) -> List.copyOf(entries));
        entrances = currentEntrances;
        clusters = currentClusters;
    }
}
