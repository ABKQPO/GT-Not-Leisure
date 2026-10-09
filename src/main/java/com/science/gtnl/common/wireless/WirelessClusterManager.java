package com.science.gtnl.common.wireless;

import static com.science.gtnl.common.wireless.WirelessChannelPrototype.controllerSource;
import static com.science.gtnl.common.wireless.WirelessChannelPrototype.sameSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.tileentity.TileEntity;

import com.science.gtnl.common.wireless.PhysicalClusterTracker.Cluster;
import com.science.gtnl.common.wireless.VerifiedEntranceRecovery.Candidate;
import com.science.gtnl.common.wireless.VerifiedEntranceRecovery.Result;
import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;
import com.science.gtnl.common.wireless.WirelessChannelPrototype.Entrance;

import appeng.api.exceptions.FailedConnection;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.pathing.ControllerState;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.tile.networking.TileController;

/** Loaded cluster records backed by tile-owned provenance. One component is one link even with several entrances. */
public final class WirelessClusterManager {

    private static final int MAX_SCANNED_NODES = 32768;
    private static final PhysicalClusterTracker<IGridNode, Address> TRACKER = new PhysicalClusterTracker<>(
        MAX_SCANNED_NODES);
    private static final PhysicalMeTopology TOPOLOGY = new PhysicalMeTopology();
    private static final Map<Cluster<IGridNode, Address>, State> STATES = new IdentityHashMap<>();
    private static final Map<IGridNode, Long> RETRY_AFTER = new IdentityHashMap<>();
    private static final Map<Cluster<IGridNode, Address>, Set<Address>> PAUSES = new IdentityHashMap<>();
    private static final Map<Cluster<IGridNode, Address>, Set<IGridNode>> SAVED_ENTRANCES = new IdentityHashMap<>();
    private static final VerifiedEntranceRecovery<IGridNode, Address> RECOVERY = new VerifiedEntranceRecovery<>();
    private static boolean scanLimited;
    private static final ScanSchedule SCANS = new ScanSchedule();
    private static final WirelessTopologyWatch WATCH = new WirelessTopologyWatch();

    private enum State {
        ACTIVE,
        CONFLICT,
        WAITING,
        PAUSED
    }

    public record Summary(int clusters, int active, int conflicted, int waiting, boolean scanLimited) {}

    private WirelessClusterManager() {}

    /** Coalesces notifications; callbacks during a scan remain pending for the next tick. */
    static final class ScanSchedule {

        private boolean dirty = true;
        private int remaining;

        void invalidate() {
            dirty = true;
        }

        boolean tick() {
            if (remaining > 0) remaining--;
            return dirty || remaining == 0;
        }

        void beginScan() {
            dirty = false;
            remaining = 20;
        }

        void clear() {
            dirty = true;
            remaining = 0;
        }
    }

    public static void invalidateTopology() {
        WirelessChannelPrototype.invalidateControllerSources();
        SCANS.invalidate();
    }

    public static void nodeChanged(IGridNode node) {
        if (node.getWorld() != null && !node.getWorld().isRemote
            && (Object) node.getMachine() instanceof TileController) invalidateTopology();
        WATCH.nodeChanged(node);
    }

    public static void tick() {
        RECOVERY.nextTick();
        if (SCANS.tick()) refresh();
        else if (!scanLimited) {
            TOPOLOGY.beginScan();
            reconcile();
        }
    }

    public static void refresh() {
        refresh(List.of());
    }

    public static void refreshTarget(IGridNode target) {
        if (!refresh(List.of(target))) throw new PhysicalClusterTracker.ScanLimitException();
    }

    private static boolean refresh(List<IGridNode> extraSeeds) {
        SCANS.beginScan();
        TOPOLOGY.beginScan();
        try {
            List<IGridNode> seeds = new ArrayList<>(WirelessLinkPersistence.seeds());
            seeds.addAll(extraSeeds);
            TRACKER.refresh(
                TOPOLOGY,
                seeds,
                node -> WirelessLinkPersistence.restore(node, TOPOLOGY),
                WirelessChannelPrototype::controllerSource);
            WirelessLinkPersistence.synchronize(TRACKER, TOPOLOGY);
            scanLimited = false;
        } catch (PhysicalClusterTracker.ScanLimitException limit) {
            scanLimited = true;
            // Never apply a partial scan. Keep all provenance so reducing the network can recover it.
            for (Entrance entrance : WirelessChannelPrototype.entrances())
                WirelessChannelPrototype.disconnect(entrance.id());
            TOPOLOGY.clear();
            AutomaticWirelessEntrances.clear();
            return false;
        }
        PAUSES.clear();
        SAVED_ENTRANCES.clear();
        for (var cluster : TRACKER.clusters()) {
            PAUSES.put(cluster, WirelessLinkPersistence.pausedSources(cluster.nodes(), TOPOLOGY));
            if (cluster.frequencies()
                .size() == 1) {
                SAVED_ENTRANCES.put(
                    cluster,
                    WirelessLinkPersistence.savedEntrances(
                        cluster.nodes(),
                        cluster.frequencies()
                            .iterator()
                            .next(),
                        TOPOLOGY));
            }
        }
        WATCH.update(TRACKER.clusters(), TOPOLOGY);
        reconcile();
        return true;
    }

    /** Power, source availability and native allocation feedback must remain responsive between topology scans. */
    private static void reconcile() {
        STATES.clear();
        // Tear down every conflicting entrance before trying to reconnect any source.
        for (Entrance entrance : WirelessChannelPrototype.entrances()) {
            Cluster<IGridNode, Address> cluster = TRACKER.clusterOf(entrance.targetNode());
            if (cluster == null || cluster.blocked()
                || cluster.conflicted()
                || paused(cluster, entrance.source())
                || !cluster.frequencies()
                    .contains(controllerSource(entrance.source()))
                || !entrance.isLive()
                || entrance.source()
                    .node() != entrance.sourceNode()
                || entrance.target()
                    .node() != entrance.targetNode()) {
                WirelessChannelPrototype.disconnect(entrance.id());
            }
        }
        Set<Address> pendingRecovery = restoreSavedEntrances();
        Set<IGridNode> retained = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Cluster<IGridNode, Address> cluster : TRACKER.clusters()) {
            retained.addAll(cluster.nodes());
            if (cluster.frequencies()
                .isEmpty()) continue;
            if (cluster.conflicted()) {
                STATES.put(cluster, State.CONFLICT);
            } else if (cluster.blocked()) {
                STATES.put(cluster, State.WAITING);
            } else if (paused(
                cluster,
                cluster.frequencies()
                    .iterator()
                    .next())) {
                        STATES.put(cluster, State.PAUSED);
                    } else {
                        restoreEntrance(cluster, pendingRecovery);
                    }
        }
        AutomaticWirelessEntrances.refresh(
            TRACKER.clusters()
                .stream()
                .filter(cluster -> STATES.get(cluster) != State.PAUSED)
                .filter(cluster -> Collections.disjoint(cluster.frequencies(), pendingRecovery))
                .toList(),
            TOPOLOGY);
        TOPOLOGY.retain(retained);
        RETRY_AFTER.keySet()
            .retainAll(retained);
    }

    private static Set<Address> restoreSavedEntrances() {
        List<Candidate<IGridNode, Address>> candidates = new ArrayList<>();
        for (var cluster : TRACKER.clusters()) {
            if (cluster.blocked() || cluster.frequencies()
                .size() != 1) continue;
            Address source = cluster.frequencies()
                .iterator()
                .next();
            if (paused(cluster, source)) continue;
            for (IGridNode node : SAVED_ENTRANCES.getOrDefault(cluster, Set.of())) {
                candidates.add(new Candidate<>(node, source));
            }
        }
        candidates.sort(
            Comparator.comparing(
                candidate -> TOPOLOGY.nodeAddress(candidate.node())
                    .toString()));
        return RECOVERY.restore(
            candidates,
            candidate -> TOPOLOGY.isLive(candidate.node()) && TOPOLOGY.entranceAddress(candidate.node()) != null
                && !candidate.node()
                    .hasFlag(GridFlags.CANNOT_CARRY),
            candidate -> hasEntrance(candidate.node(), candidate.source()),
            WirelessClusterManager::sourceReady,
            candidate -> {
                Address target = TOPOLOGY.entranceAddress(candidate.node());
                if (target == null || target.node() != candidate.node()) return Result.REJECTED;
                IGridNode sourceNode = candidate.source()
                    .node();
                if (sourceNode == null) return Result.WAITING;
                IPathingGrid path = sourceNode.getGrid()
                    .getCache(IPathingGrid.class);
                if (path.isNetworkBooting() || path.getControllerState() != ControllerState.CONTROLLER_ONLINE)
                    return Result.WAITING;
                try {
                    WirelessChannelPrototype.connect(candidate.source(), target);
                    return Result.CONNECTED;
                } catch (IllegalArgumentException | FailedConnection unavailable) {
                    // Keep saved hints through temporary source/security failures; other entries can still recover.
                    return Result.REJECTED;
                }
            });
    }

    private static boolean hasEntrance(IGridNode node, Address source) {
        for (Entrance entrance : WirelessChannelPrototype.entrances()) {
            if (entrance.targetNode() == node && sameSource(entrance.source(), source) && entrance.isLive())
                return true;
        }
        return false;
    }

    private static boolean sourceReady(Address source) {
        TileEntity tile = source.tile();
        if (tile == null || tile.getClass() != TileController.class) return false;
        IGridNode node = source.node();
        return node != null && !AutomaticWirelessEntrances.hasPendingTrial(node.getGrid())
            && AutomaticWirelessEntrances.isSettled(
                node.getGrid()
                    .getCache(IPathingGrid.class));
    }

    private static void restoreEntrance(Cluster<IGridNode, Address> cluster, Set<Address> pendingRecovery) {
        Address source = cluster.frequencies()
            .iterator()
            .next();
        for (Entrance entrance : WirelessChannelPrototype.entrances()) {
            if (sameSource(entrance.source(), source) && cluster.nodes()
                .contains(entrance.targetNode()) && entrance.isLive()) {
                STATES.put(cluster, State.ACTIVE);
                return; // Preserve all existing entrances; merging records must not discard proven channel capacity.
            }
        }
        STATES.put(cluster, State.WAITING);
        if (pendingRecovery.contains(source)) return;
        TileEntity tile = source.tile();
        if (tile == null || tile.getClass() != TileController.class) return;
        IGridNode sourceNode = source.node();
        if (sourceNode == null) return;
        IPathingGrid path = sourceNode.getGrid()
            .getCache(IPathingGrid.class);
        if (!AutomaticWirelessEntrances.isSettled(path)) return;
        // Legacy saves or surviving children without a valid hint still need an initial entrance.
        IGridNode anchor = cluster.nodes()
            .stream()
            .filter(node -> TOPOLOGY.entranceAddress(node) != null)
            .min(
                Comparator.comparingInt(PhysicalMeTopology::entranceRank)
                    .thenComparingInt(
                        node -> TOPOLOGY.entranceAddress(node)
                            .x())
                    .thenComparingInt(
                        node -> TOPOLOGY.entranceAddress(node)
                            .y())
                    .thenComparingInt(
                        node -> TOPOLOGY.entranceAddress(node)
                            .z())
                    .thenComparingInt(
                        node -> TOPOLOGY.entranceAddress(node)
                            .side()
                            .ordinal()))
            .orElse(null);
        if (anchor == null) return;
        long now = sourceNode.getWorld()
            .getTotalWorldTime();
        if (now < RETRY_AFTER.getOrDefault(anchor, Long.MIN_VALUE)) return;
        try {
            WirelessChannelPrototype.connect(source, TOPOLOGY.entranceAddress(anchor));
            RETRY_AFTER.remove(anchor);
            STATES.put(cluster, State.ACTIVE);
        } catch (IllegalArgumentException | FailedConnection unavailable) {
            // AE2 may still be repathing after a conflict or reject a security/foreign bridge connection.
            // Retain the claim and retry in one second; do not throw security exceptions on every server tick.
            RETRY_AFTER.put(anchor, now + 20);
        }
    }

    private static boolean paused(Cluster<IGridNode, Address> cluster, Address source) {
        return PAUSES.getOrDefault(cluster, Set.of())
            .contains(controllerSource(source));
    }

    public record LinkView(Address target, IGridNode node, Set<String> names, boolean conflicted, boolean paused,
        String state, int entrances, WirelessNetworkStatistics statistics, boolean runtimeEntrance, int usedChannels,
        Set<IGridNode> members) {

        public String name() {
            return names.stream()
                .sorted()
                .limit(4)
                .collect(java.util.stream.Collectors.joining(" / "))
                + (names.size() > 4 ? " … (" + names.size() + ")" : "");
        }
    }

    /** Loaded physical clusters only; viewing never loads remote chunks. */
    public static List<LinkView> links(Address source) {
        source = controllerSource(source);
        List<LinkView> result = new ArrayList<>();
        for (var cluster : TRACKER.clusters()) {
            if (!cluster.frequencies()
                .contains(source)) continue;
            IGridNode anchor = cluster.nodes()
                .stream()
                .filter(node -> TOPOLOGY.entranceAddress(node) != null)
                .min(
                    Comparator.comparing(
                        node -> TOPOLOGY.entranceAddress(node)
                            .toString()))
                .orElse(null);
            if (anchor == null) continue;
            int entrances = 0;
            for (Entrance entrance : WirelessChannelPrototype.entrances()) {
                if (sameSource(entrance.source(), source) && cluster.nodes()
                    .contains(entrance.targetNode()) && entrance.isLive()) entrances++;
            }
            boolean paused = paused(cluster, source);
            String state = scanLimited ? "limited"
                : paused ? "paused"
                    : STATES.getOrDefault(cluster, State.WAITING)
                        .name()
                        .toLowerCase(java.util.Locale.ROOT);
            result.add(
                new LinkView(
                    TOPOLOGY.entranceAddress(anchor),
                    anchor,
                    WirelessLinkPersistence.names(cluster.nodes(), source, TOPOLOGY),
                    cluster.conflicted(),
                    paused,
                    state,
                    entrances,
                    WirelessNetworkStatistics.measure(cluster.nodes(), WirelessChannelPrototype.channelsEnabled()),
                    false,
                    -1,
                    Set.copyOf(cluster.nodes())));
        }
        result.sort(
            Comparator.comparing(
                view -> view.target()
                    .toString()));
        return result;
    }

    public static boolean setPaused(Address source, LinkView view, boolean paused) {
        IGridNode target = view.node();
        var cluster = linkedCluster(source, view);
        if (cluster == null) return false;
        if (!cluster.nodes()
            .equals(view.members()) || paused(cluster, source) != view.paused())
            throw new IllegalStateException("The displayed cluster changed; refresh before editing.");
        WirelessLinkPersistence.setPaused(cluster.nodes(), source, paused, TOPOLOGY);
        refreshTarget(target);
        return true;
    }

    public static List<LinkView> entranceLinks(Address source, LinkView cluster) {
        List<LinkView> result = new ArrayList<>();
        for (Entrance entrance : WirelessChannelPrototype.entrances()) {
            if (sameSource(entrance.source(), source) && entrance.isLive()
                && cluster.members()
                    .contains(entrance.targetNode())) {
                result.add(
                    new LinkView(
                        entrance.target(),
                        entrance.targetNode(),
                        cluster.names(),
                        cluster.conflicted(),
                        cluster.paused(),
                        cluster.state(),
                        cluster.entrances(),
                        cluster.statistics(),
                        true,
                        WirelessNetworkStatistics.channelsStable(entrance.sourceNode())
                            && WirelessNetworkStatistics.channelsStable(entrance.targetNode())
                                ? entrance.connection()
                                    .getUsedChannels()
                                : -1,
                        cluster.members()));
            }
        }
        result.sort(
            Comparator.comparing(
                view -> view.target()
                    .toString()));
        // Keep a resumable cluster row when pausing has removed all of its runtime entrances.
        return result.isEmpty() ? List.of(cluster) : result;
    }

    public static boolean isLinked(Address source, IGridNode target) {
        Cluster<IGridNode, Address> cluster = TRACKER.clusterOf(target);
        return cluster != null && cluster.frequencies()
            .contains(controllerSource(source));
    }

    static boolean isCurrentLink(Address source, LinkView view) {
        if (view.target()
            .node() != view.node()) return false;
        var cluster = linkedCluster(source, view);
        return cluster != null && cluster.nodes()
            .equals(view.members());
    }

    public static void rename(Address source, LinkView view, String name) {
        var cluster = linkedCluster(source, view);
        if (cluster == null || !cluster.nodes()
            .equals(view.members())) throw new IllegalStateException("The displayed cluster changed");
        WirelessLinkPersistence.rename(cluster.nodes(), source, name, TOPOLOGY);
    }

    private static Cluster<IGridNode, Address> linkedCluster(Address source, LinkView view) {
        refreshTarget(view.node());
        var cluster = TRACKER.clusterOf(view.node());
        return cluster != null && cluster.frequencies()
            .contains(controllerSource(source)) ? cluster : null;
    }

    public static List<Address> highlight(LinkView view) {
        return (view.runtimeEntrance() ? java.util.stream.Stream.of(view.target())
            : view.members()
                .stream()
                .map(TOPOLOGY::nodeAddress)).filter(java.util.Objects::nonNull)
                    .map(
                        address -> new Address(
                            address.dimension(),
                            address.x(),
                            address.y(),
                            address.z(),
                            net.minecraftforge.common.util.ForgeDirection.UNKNOWN))
                    .distinct()
                    .toList();
    }

    /** Explicit debug calls may add an additional entrance to an already linked, same-frequency cluster. */
    public static int connectCluster(Address source, Address target) throws FailedConnection {
        source = controllerSource(source);
        IGridNode node = target.node();
        if (node == null) throw new IllegalArgumentException("Target ME node is unavailable.");
        refreshTarget(node);
        Cluster<IGridNode, Address> cluster = TRACKER.clusterOf(node);
        if (cluster == null || cluster.blocked())
            throw new IllegalArgumentException("A physical cluster containing a controller cannot be linked.");
        if (cluster.conflicted() || !cluster.frequencies()
            .isEmpty()
            && !cluster.frequencies()
                .contains(source)) {
            throw new IllegalArgumentException(
                "This cluster belongs to another frequency. Disconnect that frequency first.");
        }
        if (paused(cluster, source))
            throw new IllegalStateException("This cluster is paused in the frequency card GUI.");
        // Do not commit a new claim if AE2 refuses the first connection.
        int id = WirelessChannelPrototype.connect(source, target);
        TRACKER.link(cluster, source);
        refreshTarget(node);
        return id;
    }

    public static boolean disconnectCluster(Address source, IGridNode target) {
        refreshTarget(target);
        source = controllerSource(source);
        Cluster<IGridNode, Address> cluster = TRACKER.clusterOf(target);
        if (cluster == null || !cluster.frequencies()
            .contains(source)) return false;
        TRACKER.unlink(cluster, source, WirelessChannelPrototype::controllerSource);
        for (Entrance entrance : WirelessChannelPrototype.entrances()) {
            if (sameSource(entrance.source(), source) && cluster.nodes()
                .contains(entrance.targetNode())) {
                WirelessChannelPrototype.disconnect(entrance.id());
            }
        }
        refreshTarget(target);
        return true;
    }

    public static Summary summary(Address source) {
        source = controllerSource(source);
        int clusters = 0, active = 0, conflicted = 0, waiting = 0;
        for (Cluster<IGridNode, Address> cluster : TRACKER.clusters()) {
            if (!cluster.frequencies()
                .contains(source)) continue;
            clusters++;
            State state = scanLimited ? State.WAITING : STATES.getOrDefault(cluster, State.WAITING);
            switch (state) {
                case ACTIVE -> active++;
                case CONFLICT -> conflicted++;
                case WAITING, PAUSED -> waiting++;
            }
        }
        return new Summary(clusters, active, conflicted, waiting, scanLimited);
    }

    public static void forgetTargets(Predicate<Address> removed) {
        invalidateTopology();
        TRACKER.forget(node -> TOPOLOGY.matches(node, removed));
        WirelessLinkPersistence.forget(removed);
    }

    public static void clear() {
        WATCH.clear();
        SCANS.clear();
        TRACKER.clear();
        TOPOLOGY.clear();
        STATES.clear();
        RETRY_AFTER.clear();
        PAUSES.clear();
        SAVED_ENTRANCES.clear();
        RECOVERY.clear();
        AutomaticWirelessEntrances.clear();
        WirelessLinkPersistence.clear();
        scanLimited = false;
    }
}
