package com.science.gtnl.common.wireless;

import java.util.Collection;
import java.util.Comparator;
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
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class AutomaticWirelessEntrances {

    private static final Map<IGrid, AutomaticEntrancePlanner<IGridNode>> PLANNERS = new Reference2ObjectOpenHashMap<>();
    private static final Map<IGrid, GridTargets> TARGETS = new Reference2ObjectOpenHashMap<>();
    private static final Map<IGrid, ChannelCount> COUNTS = new Reference2ObjectOpenHashMap<>();

    private record GridTargets(List<Cluster<IGridNode, WirelessFrequency>> clusters,
        Map<IGridNode, WirelessFrequency> sources, List<IGridNode> nodes, long fingerprint) {}

    private record ChannelCount(GridTargets targets, long revision, int served, List<IGridNode> missing) {}

    private static class Work {

        public final List<Cluster<IGridNode, WirelessFrequency>> clusters = new ObjectArrayList<>();
        public final Set<IGridNode> occupied = new ReferenceOpenHashSet<>();
        public final List<IGridNode> missing = new ObjectArrayList<>();
        public final WirelessFrequency frequency;
        public final IGridNode source;
        public int served;

        Work(WirelessFrequency frequency, IGridNode source) {
            this.frequency = frequency;
            this.source = source;
        }
    }

    private static class CandidateSearch {

        private final Work work;
        private final GridTargets targets;
        private final PhysicalMeTopology topology;
        private final Comparator<IGridNode> order;
        private final Map<Cluster<IGridNode, WirelessFrequency>, EntranceCandidates.Batch<IGridNode>> batches = new Reference2ObjectOpenHashMap<>();
        private List<Cluster<IGridNode, WirelessFrequency>> ordered;
        private Map<Cluster<IGridNode, WirelessFrequency>, List<IGridNode>> missing;

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
            ordered = new ObjectArrayList<>(work.clusters);
            Map<Cluster<IGridNode, WirelessFrequency>, IGridNode> anchors = new Reference2ObjectOpenHashMap<>();
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
            missing = new Reference2ObjectOpenHashMap<>();
            for (IGridNode node : work.missing) {
                for (var cluster : ordered) {
                    if (cluster.nodes()
                        .contains(node)) {
                        missing.computeIfAbsent(cluster, ignored -> new ObjectArrayList<>())
                            .add(node);
                        break;
                    }
                }
            }
        }

        IGridNode select(Predicate<IGridNode> accepted) {
            if (ordered == null) prepare();
            for (Cluster<IGridNode, WirelessFrequency> cluster : ordered) {
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

    static void refresh(Collection<Cluster<IGridNode, WirelessFrequency>> clusters, PhysicalMeTopology topology,
        WirelessEntranceIndex entrances) {
        Map<IGrid, Work> workByGrid = new Reference2ObjectOpenHashMap<>();
        for (Cluster<IGridNode, WirelessFrequency> cluster : clusters) {
            if (cluster.blocked() || cluster.frequencies()
                .size() != 1) continue;
            WirelessFrequency source = cluster.frequencies()
                .iterator()
                .next();
            IGridNode sourceNode = WirelessFrequencies.controllerNode(source);
            if (sourceNode == null) continue;
            List<IGridNode> occupied = new ObjectArrayList<>();
            for (Entrance entrance : entrances.forCluster(cluster)) {
                if (WirelessFrequencies.same(entrance.frequency(), source) && entrance.isLive()) {
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
        for (var entry : workByGrid.entrySet()) run(entry.getKey(), entry.getValue(), topology, entrances);
    }

    private static GridTargets targets(IGrid grid, List<Cluster<IGridNode, WirelessFrequency>> clusters) {
        GridTargets cached = TARGETS.get(grid);
        if (cached != null && cached.clusters()
            .size() == clusters.size()) {
            int index = 0;
            while (index < clusters.size() && cached.clusters()
                .get(index) == clusters.get(index)) index++;
            if (index == clusters.size()) return cached;
        }
        Map<IGridNode, WirelessFrequency> sources = new Reference2ObjectOpenHashMap<>();
        for (var cluster : clusters) {
            WirelessFrequency source = cluster.frequencies()
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
        List<IGridNode> missing = new ObjectArrayList<>();
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

    private static void run(IGrid grid, Work work, PhysicalMeTopology topology, WirelessEntranceIndex entrances) {
        GridTargets targets = targets(grid, work.clusters);
        var allocation = WirelessChannelManager.allocation(grid);
        long revision = allocation == null ? -1 : allocation.revision();
        ChannelCount count = count(grid, targets, revision);
        work.served = count.served();
        work.missing.addAll(count.missing());
        AutomaticEntrancePlanner<IGridNode> planner = PLANNERS
            .computeIfAbsent(grid, ignored -> new AutomaticEntrancePlanner<>());
        if (planner.hasTrial()) {
            IntList pending = planner.pendingEntrances();
            for (int i = 0; i < pending.size(); i++) {
                if (WirelessChannelManager.entrance(pending.getInt(i)) == null) {
                    planner.entranceLost();
                    break;
                }
            }
        }
        IEnergyGrid energy = grid.getCache(IEnergyGrid.class);
        int capacity = allocation == null ? WirelessChannelManager.capacity(grid) : allocation.capacity();
        boolean settled = allocation != null;
        if (settled && energy.isNetworkPowered()) {
            for (var cluster : work.clusters) {
                for (Entrance entrance : entrances.forCluster(cluster)) {
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
            IntList ids = decision.entranceIds();
            for (int i = 0; i < ids.size(); i++) WirelessChannelManager.disconnect(ids.getInt(i));
        } else if (decision.action() == Action.KEEP) {
            keep(planner, decision, topology);
        } else if (decision.action() == Action.ADD) {
            List<IGridNode> created = new ObjectArrayList<>();
            IntArrayList ids = new IntArrayList();
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
        IntList ids = decision.entranceIds();
        boolean prune = ids.size() > 1 && WirelessChannelManager.channelsEnabled();
        for (int index = 0; index < ids.size(); index++) {
            Entrance entrance = WirelessChannelManager.entrance(ids.getInt(index));
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

    private static boolean hasSourceConnection(IGridNode target, WirelessFrequency source) {
        IGridNode sourceNode = WirelessFrequencies.controllerNode(source);
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

    public static Status status(WirelessFrequency source) {
        IGridNode node = WirelessFrequencies.controllerNode(source);
        AutomaticEntrancePlanner<IGridNode> planner = node == null ? null : PLANNERS.get(node.getGrid());
        return planner == null ? Status.WAITING : planner.status();
    }

    public static void clear() {
        PLANNERS.clear();
        TARGETS.clear();
        COUNTS.clear();
    }
}
