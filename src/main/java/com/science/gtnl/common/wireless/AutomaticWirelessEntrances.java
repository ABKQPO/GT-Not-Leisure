package com.science.gtnl.common.wireless;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

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

public class AutomaticWirelessEntrances {

    private static final Map<IGrid, AutomaticEntrancePlanner<IGridNode>> PLANNERS = new IdentityHashMap<>();
    private static final Map<IGrid, GridTargets> TARGETS = new IdentityHashMap<>();
    private static final Map<IGrid, ChannelCount> COUNTS = new IdentityHashMap<>();

    private record GridTargets(List<Cluster<IGridNode, Address>> clusters, Map<IGridNode, Address> sources,
        List<IGridNode> nodes, long fingerprint) {}

    private record ChannelCount(GridTargets targets, long revision, int served, List<IGridNode> missing) {}

    private static class Work {

        public final List<Cluster<IGridNode, Address>> clusters = new ArrayList<>();
        public final Set<IGridNode> occupied = Collections.newSetFromMap(new IdentityHashMap<>());
        public final List<IGridNode> missing = new ArrayList<>();
        public final Address frequency;
        public final IGridNode source;
        public int served;

        Work(Address frequency, IGridNode source) {
            this.frequency = frequency;
            this.source = source;
        }
    }

    private static class CandidateSearch {

        private final Work work;
        private final GridTargets targets;
        private final PhysicalMeTopology topology;
        private final Comparator<IGridNode> order;
        private final Map<Cluster<IGridNode, Address>, EntranceCandidates.Batch<IGridNode>> batches = new IdentityHashMap<>();
        private List<Cluster<IGridNode, Address>> ordered;
        private Map<Cluster<IGridNode, Address>, List<IGridNode>> missing;

        CandidateSearch(Work work, GridTargets targets, PhysicalMeTopology topology) {
            this.work = work;
            this.targets = targets;
            this.topology = topology;
            order = Comparator.comparing(
                topology::nodeAddress,
                Comparator.comparingInt(Address::dimension)
                    .thenComparing(Address.POSITION_ORDER));
        }

        private void prepare() {
            ordered = new ArrayList<>(work.clusters);
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
            missing = new IdentityHashMap<>();
            for (IGridNode node : work.missing) {
                for (var cluster : ordered) {
                    if (cluster.nodes()
                        .contains(node)) {
                        missing.computeIfAbsent(cluster, ignored -> new ArrayList<>())
                            .add(node);
                        break;
                    }
                }
            }
        }

        IGridNode select(Predicate<IGridNode> accepted) {
            if (ordered == null) prepare();
            for (Cluster<IGridNode, Address> cluster : ordered) {
                List<IGridNode> needs = missing.get(cluster);
                if (needs == null || needs.isEmpty()) continue;
                var batch = batches.computeIfAbsent(
                    cluster,
                    key -> new EntranceCandidates.Batch<>(key.nodes(), work.occupied, topology::neighbours));
                IGridNode candidate = batch.select(
                    needs,
                    node -> accepted.test(node) && !work.occupied.contains(node)
                        && topology.entranceAddress(node) != null
                        && !node.hasFlag(GridFlags.CANNOT_CARRY)
                        && !hasSourceConnection(
                            node,
                            targets.sources()
                                .get(node)),
                    PhysicalMeTopology::entranceRank,
                    PhysicalMeTopology::entranceCapacity,
                    order);
                if (candidate != null) {
                    work.occupied.add(candidate);
                    batch.occupy(candidate);
                    return candidate;
                }
            }
            return null;
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
            if (occupied.isEmpty()) continue;
            Work work = workByGrid.computeIfAbsent(sourceNode.getGrid(), grid -> new Work(source, sourceNode));
            work.clusters.add(cluster);
            work.occupied.addAll(occupied);
        }
        PLANNERS.keySet()
            .retainAll(workByGrid.keySet());
        TARGETS.keySet()
            .retainAll(workByGrid.keySet());
        COUNTS.keySet()
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

    private static ChannelCount count(IGrid grid, GridTargets targets, long revision) {
        ChannelCount cached = COUNTS.get(grid);
        if (cached != null && revision >= 0 && cached.targets() == targets && cached.revision() == revision)
            return cached;
        int served = 0;
        List<IGridNode> missing = new ArrayList<>();
        for (IGridNode node : targets.nodes()) {
            if (!node.hasFlag(GridFlags.REQUIRE_CHANNEL) || node.hasFlag(GridFlags.CANNOT_CARRY)) continue;
            if (node.meetsChannelRequirements()) served++;
            else missing.add(node);
        }
        ChannelCount count = new ChannelCount(targets, revision, served, List.copyOf(missing));
        if (revision >= 0) COUNTS.put(grid, count);
        else COUNTS.remove(grid);
        return count;
    }

    private static void run(IGrid grid, Work work, PhysicalMeTopology topology) {
        GridTargets targets = targets(grid, work.clusters);
        var allocation = WirelessChannelManager.allocation(grid);
        long revision = allocation == null ? -1 : allocation.revision();
        ChannelCount count = count(grid, targets, revision);
        work.served = count.served();
        work.missing.addAll(count.missing());
        AutomaticEntrancePlanner<IGridNode> planner = PLANNERS
            .computeIfAbsent(grid, ignored -> new AutomaticEntrancePlanner<>());
        if (planner.hasTrial()) {
            for (int id : planner.pendingEntrances()) {
                if (WirelessChannelManager.entrance(id) == null) {
                    planner.entranceLost();
                    break;
                }
            }
        }
        IEnergyGrid energy = grid.getCache(IEnergyGrid.class);
        int capacity = allocation == null ? WirelessChannelManager.capacity(grid) : allocation.capacity();
        boolean settled = allocation != null;
        if (settled && energy.isNetworkPowered()) {
            for (Entrance entrance : WirelessChannelManager.entrances()) {
                if (entrance.sourceNode()
                    .getGrid() != grid || !entrance.isLive()
                    || !work.occupied.contains(entrance.targetNode())) continue;
                if (VerifiedEntranceRecovery.verified(
                    settled,
                    energy.isNetworkPowered(),
                    planner.isPending(entrance.id()),
                    WirelessChannelManager.channelsEnabled(),
                    entrance.connection()
                        .getUsedChannels(),
                    work.served + work.missing.size())) {
                    WirelessLinkPersistence.rememberEntrance(entrance, topology);
                }
            }
        }
        Observation observation = new Observation(
            context(grid, targets, capacity),
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
            WirelessChannelManager.entranceLimit() - WirelessChannelManager.entranceCount(work.frequency));
        var decision = planner.decide(observation, new CandidateSearch(work, targets, topology)::select);
        if (decision.action() == Action.REMOVE) {
            for (int id : decision.entranceIds()) WirelessChannelManager.disconnect(id);
        } else if (decision.action() == Action.KEEP) {
            keep(planner, decision, topology);
        } else if (decision.action() == Action.ADD) {
            List<IGridNode> created = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();
            for (IGridNode target : decision.targets()) {
                try {
                    ids.add(
                        WirelessChannelManager.connect(
                            targets.sources()
                                .get(target),
                            topology.entranceAddress(target)));
                    created.add(target);
                } catch (IllegalArgumentException | FailedConnection failure) {
                    planner.failed(target, observation.tick());
                }
            }
            if (!ids.isEmpty()) planner.created(created, ids, observation);
        }
    }

    private static void keep(AutomaticEntrancePlanner<IGridNode> planner,
        AutomaticEntrancePlanner.Decision<IGridNode> decision, PhysicalMeTopology topology) {
        List<Integer> ids = decision.entranceIds();
        boolean prune = ids.size() > 1 && WirelessChannelManager.channelsEnabled();
        for (int index = 0; index < ids.size(); index++) {
            Entrance entrance = WirelessChannelManager.entrance(ids.get(index));
            if (entrance == null) continue;
            if (prune && entrance.isLive()
                && entrance.connection()
                    .getUsedChannels() == 0) {
                WirelessChannelManager.disconnect(entrance.id());
                planner.reject(
                    decision.targets()
                        .get(index));
            } else {
                WirelessLinkPersistence.rememberEntrance(entrance, topology);
            }
        }
    }

    public static boolean isSettled(IPathingGrid path) {
        return path.getControllerState() == ControllerState.CONTROLLER_ONLINE && !path.isNetworkBooting()
            && path instanceof IWirelessPathingState state
            && !state.gtnl$isRepathPending();
    }

    static boolean hasPendingTrial(IGrid grid) {
        var planner = PLANNERS.get(grid);
        return planner != null && planner.hasTrial();
    }

    private static boolean hasSourceConnection(IGridNode target, Address source) {
        IGridNode sourceNode = source.node();
        for (IGridConnection connection : target.getConnections()) {
            if (connection.getOtherSide(target) == sourceNode) return true;
        }
        return false;
    }

    private static long context(IGrid grid, GridTargets targets, int capacity) {
        long topology = grid.getCache(IPathingGrid.class) instanceof IWirelessPathingState state
            ? state.gtnl$topologyRevision()
            : 0;
        return mix(capacity) + mix(topology) + targets.fingerprint();
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
        TARGETS.clear();
        COUNTS.clear();
    }
}
