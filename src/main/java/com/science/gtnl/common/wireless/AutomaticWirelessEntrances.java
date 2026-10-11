package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.science.gtnl.common.wireless.AutomaticEntrancePlanner.Action;
import com.science.gtnl.common.wireless.AutomaticEntrancePlanner.Observation;
import com.science.gtnl.common.wireless.AutomaticEntrancePlanner.Status;
import com.science.gtnl.common.wireless.PhysicalClusterTracker.Cluster;
import com.science.gtnl.common.wireless.WirelessChannelManager.Address;
import com.science.gtnl.common.wireless.WirelessChannelManager.Entrance;

import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.pathing.ControllerState;
import appeng.api.networking.pathing.IPathingGrid;

/** Adapts the feedback planner to real AE2 allocations. Never changes native cable capacities or channel flags. */
public final class AutomaticWirelessEntrances {

    private static final Map<IGrid, AutomaticEntrancePlanner<IGridNode>> PLANNERS = new IdentityHashMap<>();
    private static final Map<IGrid, GridShape> SHAPES = new IdentityHashMap<>();
    private static final Map<IGrid, GridTargets> TARGETS = new IdentityHashMap<>();

    private record GridShape(long revision, long fingerprint) {}

    private record GridTargets(List<Cluster<IGridNode, Address>> clusters, Map<IGridNode, Address> sources,
        List<IGridNode> nodes, long fingerprint) {}

    private static final class Work {

        final List<Cluster<IGridNode, Address>> clusters = new ArrayList<>();
        final Set<IGridNode> occupied = Collections.newSetFromMap(new IdentityHashMap<>());
        final List<IGridNode> missing = new ArrayList<>();
        final Address frequency;
        final IGridNode source;
        int served;

        Work(Address frequency, IGridNode source) {
            this.frequency = frequency;
            this.source = source;
        }
    }

    private AutomaticWirelessEntrances() {}

    static void refresh(Collection<Cluster<IGridNode, Address>> clusters, PhysicalMeTopology topology,
        WirelessEntranceIndex entrances) {
        Map<IGrid, Work> workByGrid = new IdentityHashMap<>();
        for (Cluster<IGridNode, Address> cluster : clusters) {
            if (cluster.blocked() || cluster.frequencies()
                .size() != 1) continue;
            Address source = cluster.frequencies()
                .iterator()
                .next();
            IGridNode sourceNode = source.node();
            if (sourceNode == null) continue;
            List<IGridNode> occupied = new ArrayList<>();
            for (Entrance entrance : entrances.forCluster(cluster)) {
                if (WirelessChannelManager.sameSource(entrance.source(), source) && entrance.isLive()) {
                    occupied.add(entrance.targetNode());
                }
            }
            if (occupied.isEmpty()) continue; // The cluster manager restores its first entrance before planning extra
                                              // ones.
            Work work = workByGrid.computeIfAbsent(sourceNode.getGrid(), grid -> new Work(source, sourceNode));
            work.clusters.add(cluster);
            work.occupied.addAll(occupied);
        }
        PLANNERS.keySet()
            .retainAll(workByGrid.keySet());
        SHAPES.keySet()
            .retainAll(workByGrid.keySet());
        TARGETS.keySet()
            .retainAll(workByGrid.keySet());
        for (var entry : workByGrid.entrySet()) run(entry.getKey(), entry.getValue(), topology);
    }

    private static GridTargets targets(IGrid grid, List<Cluster<IGridNode, Address>> clusters) {
        GridTargets cached = TARGETS.get(grid);
        if (cached != null && cached.clusters()
            .size() == clusters.size()) {
            int index = 0;
            while (index < clusters.size() && cached.clusters()
                .get(index) == clusters.get(index)) index++;
            if (index == clusters.size()) return cached;
        }
        Map<IGridNode, Address> sources = new IdentityHashMap<>();
        for (var cluster : clusters) {
            Address source = cluster.frequencies()
                .iterator()
                .next();
            for (IGridNode node : cluster.nodes()) sources.put(node, source);
        }
        long fingerprint = 0;
        for (var entry : sources.entrySet()) {
            fingerprint += mix(
                ((long) System.identityHashCode(entry.getKey()) << 32) ^ entry.getValue()
                    .hashCode());
        }
        GridTargets targets = new GridTargets(
            List.copyOf(clusters),
            sources,
            List.copyOf(sources.keySet()),
            fingerprint);
        TARGETS.put(grid, targets);
        return targets;
    }

    private static void run(IGrid grid, Work work, PhysicalMeTopology topology) {
        GridTargets targets = targets(grid, work.clusters);
        // Membership is immutable between scans; flags and channel allocation remain live on every pass.
        for (IGridNode node : targets.nodes()) {
            if (!node.hasFlag(GridFlags.REQUIRE_CHANNEL) || node.hasFlag(GridFlags.CANNOT_CARRY)) continue;
            if (node.meetsChannelRequirements()) work.served++;
            else work.missing.add(node);
        }
        AutomaticEntrancePlanner<IGridNode> planner = PLANNERS
            .computeIfAbsent(grid, ignored -> new AutomaticEntrancePlanner<>());
        if (planner.pendingEntrance() >= 0 && WirelessChannelManager.entrance(planner.pendingEntrance()) == null) {
            planner.entranceLost();
        }
        IPathingGrid path = grid.getCache(IPathingGrid.class);
        IEnergyGrid energy = grid.getCache(IEnergyGrid.class);
        var allocation = WirelessChannelManager.allocation(grid);
        int capacity = allocation == null ? 0 : allocation.capacity();
        boolean settled = allocation != null && isSettled(path);
        if (settled && energy.isNetworkPowered()) {
            for (Entrance entrance : WirelessChannelManager.entrances()) {
                if (entrance.sourceNode()
                    .getGrid() != grid || !entrance.isLive()
                    || !work.occupied.contains(entrance.targetNode())) continue;
                if (VerifiedEntranceRecovery.verified(
                    settled,
                    energy.isNetworkPowered(),
                    entrance.id() == planner.pendingEntrance(),
                    WirelessChannelManager.channelsEnabled(),
                    entrance.connection()
                        .getUsedChannels(),
                    work.served + work.missing.size())) {
                    WirelessLinkPersistence.rememberEntrance(entrance, topology);
                }
            }
        }
        long revision = allocation == null ? -1 : allocation.revision();
        Observation observation = new Observation(
            context(grid, targets, capacity, revision),
            revision,
            work.source.getWorld()
                .getTotalWorldTime(),
            WirelessChannelManager.channelsEnabled(),
            energy.isNetworkPowered(),
            settled,
            capacity,
            allocation == null ? 0 : allocation.used(),
            work.served,
            work.missing.size(),
            WirelessChannelManager.entranceLimitReached(work.frequency));
        var decision = planner.decide(observation, accepted -> {
            Comparator<IGridNode> order = addressOrder(topology);
            // Prefer the cluster with the earliest unresolved device for deterministic, bounded work per allocation.
            List<Cluster<IGridNode, Address>> ordered = new ArrayList<>(work.clusters);
            Map<Cluster<IGridNode, Address>, IGridNode> anchors = new IdentityHashMap<>();
            for (var cluster : ordered) {
                anchors.put(
                    cluster,
                    cluster.nodes()
                        .stream()
                        .filter(node -> topology.entranceAddress(node) != null)
                        .min(order)
                        .orElse(null));
            }
            ordered.sort(Comparator.comparing(anchors::get, Comparator.nullsLast(order)));
            for (Cluster<IGridNode, Address> cluster : ordered) {
                List<IGridNode> missing = work.missing.stream()
                    .filter(cluster.nodes()::contains)
                    .toList();
                if (missing.isEmpty()) continue;
                IGridNode candidate = EntranceCandidates.select(
                    cluster.nodes(),
                    missing,
                    work.occupied,
                    topology::neighbours,
                    node -> accepted.test(node) && !work.occupied.contains(node)
                        && topology.entranceAddress(node) != null
                        && !node.hasFlag(GridFlags.CANNOT_CARRY)
                        && !hasSourceConnection(
                            node,
                            targets.sources()
                                .get(node)),
                    PhysicalMeTopology::entranceRank,
                    order);
                if (candidate != null) return candidate;
            }
            return null;
        });
        if (decision.action() == Action.REMOVE) {
            WirelessChannelManager.disconnect(decision.entranceId());
        } else if (decision.action() == Action.KEEP) {
            WirelessLinkPersistence.rememberEntrance(WirelessChannelManager.entrance(decision.entranceId()), topology);
        } else if (decision.action() == Action.ADD) {
            IGridNode target = decision.target();
            try {
                int id = WirelessChannelManager.connect(
                    targets.sources()
                        .get(target),
                    topology.entranceAddress(target));
                planner.created(target, id, observation);
            } catch (IllegalArgumentException | FailedConnection failure) {
                planner.failed(target, observation.tick());
            }
        }
    }

    public static boolean isSettled(IPathingGrid path) {
        return path.getControllerState() == ControllerState.CONTROLLER_ONLINE && !path.isNetworkBooting()
            && path instanceof WirelessPathingState state
            && !state.gtnl$isRepathPending();
    }

    static boolean hasPendingTrial(IGrid grid) {
        var planner = PLANNERS.get(grid);
        return planner != null && planner.pendingEntrance() >= 0;
    }

    private static boolean hasSourceConnection(IGridNode target, Address source) {
        IGridNode sourceNode = source.node();
        for (IGridConnection connection : target.getConnections()) {
            if (connection.getOtherSide(target) == sourceNode) return true;
        }
        return false;
    }

    private static Comparator<IGridNode> addressOrder(PhysicalMeTopology topology) {
        return Comparator.comparingInt(
            (IGridNode node) -> topology.nodeAddress(node)
                .dimension())
            .thenComparingInt(
                node -> topology.nodeAddress(node)
                    .x())
            .thenComparingInt(
                node -> topology.nodeAddress(node)
                    .y())
            .thenComparingInt(
                node -> topology.nodeAddress(node)
                    .z())
            .thenComparingInt(
                node -> topology.nodeAddress(node)
                    .side()
                    .ordinal());
    }

    /** Excludes our own trial edges: rolling back a failed trial must not erase the failed-candidate memory. */
    private static long context(IGrid grid, GridTargets targets, int capacity, long revision) {
        GridShape shape = SHAPES.get(grid);
        if (shape == null || shape.revision() != revision) {
            long fingerprint = mix(capacity);
            for (IGridNode node : grid.getNodes()) {
                int flags = 0;
                for (GridFlags flag : node.getGridBlock()
                    .getFlags()) flags |= 1 << flag.ordinal();
                fingerprint += mix(((long) System.identityHashCode(node) << 32) ^ flags);
                for (IGridConnection connection : node.getConnections()) {
                    if (connection.a() == node && !WirelessChannelManager.isOwned(connection))
                        fingerprint += mix(System.identityHashCode(connection));
                }
            }
            shape = new GridShape(revision, fingerprint);
            SHAPES.put(grid, shape);
        }
        return shape.fingerprint() + targets.fingerprint();
    }

    private static long mix(long value) {
        value = (value ^ value >>> 30) * 0xbf58476d1ce4e5b9L;
        value = (value ^ value >>> 27) * 0x94d049bb133111ebL;
        return value ^ value >>> 31;
    }

    public static Status status(Address source) {
        IGridNode node = source.node();
        AutomaticEntrancePlanner<IGridNode> planner = node == null ? null : PLANNERS.get(node.getGrid());
        return planner == null ? Status.WAITING : planner.status();
    }

    public static void clear() {
        PLANNERS.clear();
        SHAPES.clear();
        TARGETS.clear();
    }
}
