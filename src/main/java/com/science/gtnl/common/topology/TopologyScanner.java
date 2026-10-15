package com.science.gtnl.common.topology;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

import com.science.gtnl.api.topology.BridgeReport;
import com.science.gtnl.api.topology.BridgeSink;
import com.science.gtnl.api.topology.GridCoord;
import com.science.gtnl.api.topology.MeBridgeProbe;
import com.science.gtnl.api.topology.MeBridgeProbeRegistry;
import com.science.gtnl.api.topology.TopologyBridge;
import com.science.gtnl.api.topology.TopologyDevice;
import com.science.gtnl.api.topology.TopologyNetwork;
import com.science.gtnl.api.topology.TopologySnapshot;
import com.science.gtnl.common.topology.probe.ProbeSupport;
import com.science.gtnl.config.MainConfig;

import appeng.api.config.SecurityPermissions;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.pathing.ControllerState;
import appeng.api.networking.pathing.IPathingGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.tile.networking.TileController;
import appeng.tile.networking.TileCreativeEnergyController;

public class TopologyScanner {

    private static final int MAX_REPORTS_PER_NETWORK = 4096;

    private final Map<IGrid, Integer> ids = new IdentityHashMap<>();
    private int nextId;

    public TopologySnapshot scan(IGridNode origin, EntityPlayer viewer) {
        IGrid main = origin == null ? null : origin.getGrid();
        if (main == null || !canView(main, viewer))
            return new TopologySnapshot(List.of(), List.of(), List.of(), true, false, false, 0L);

        int networkBudget = Math.max(1, MainConfig.topology.maxNetworks);
        int nodeBudget = Math.max(64, MainConfig.topology.maxProbedNodes);

        List<TopologyNetwork> networks = new ArrayList<>();
        List<TopologyDevice> devices = new ArrayList<>();
        List<TopologyBridge> bridges = new ArrayList<>();
        int deviceBudget = Math.max(0, MainConfig.topology.maxDevicesTotal);
        int devicesPerNetwork = Math.max(0, MainConfig.topology.maxDevicesPerNetwork);
        Set<BridgeKey> seenBridges = new HashSet<>();
        Map<IGrid, Integer> depths = new IdentityHashMap<>();
        ArrayDeque<IGrid> pending = new ArrayDeque<>();

        depths.put(main, 0);
        pending.add(main);
        boolean truncated = false;
        int probedNodes = 0;

        while (!pending.isEmpty()) {
            IGrid grid = pending.removeFirst();
            int depth = depths.get(grid);
            int networkId = id(grid);
            networks.add(describe(grid, networkId, depth));
            if (collect(grid, networkId, devices, Math.min(devicesPerNetwork, deviceBudget - devices.size())))
                truncated = true;

            Collector collector = new Collector();
            probedNodes += probe(grid, collector, nodeBudget - probedNodes);
            if (probedNodes >= nodeBudget) truncated = true;

            for (BridgeReport report : collector.reports) {
                if (!seenBridges.add(BridgeKey.of(report))) continue;
                IGrid remote = report.remote();
                int remoteId = TopologyBridge.UNRESOLVED;
                if (remote != null && canView(remote, viewer)) {
                    if (depths.containsKey(remote)) {
                        remoteId = id(remote);
                    } else if (depths.size() < networkBudget) {
                        depths.put(remote, depth + 1);
                        pending.add(remote);
                        remoteId = id(remote);
                    } else {
                        truncated = true;
                    }
                }
                bridges.add(
                    new TopologyBridge(
                        bridges.size(),
                        report.kind(),
                        id(grid),
                        remoteId,
                        report.origin(),
                        report.remoteEnd(),
                        report.icon(),
                        report.active()));
            }
        }
        return new TopologySnapshot(networks, devices, bridges, true, true, truncated, 0L);
    }

    private int id(IGrid grid) {
        Integer existing = ids.get(grid);
        if (existing != null) return existing;
        ids.put(grid, nextId);
        return nextId++;
    }

    public void retain(TopologySnapshot snapshot) {
        int live = snapshot.networks()
            .size();
        if (ids.size() <= Math.max(16, live * 2)) return;
        Set<Integer> keep = new HashSet<>();
        for (TopologyNetwork network : snapshot.networks()) keep.add(network.id());
        ids.values()
            .removeIf(id -> !keep.contains(id));
    }

    private static boolean canView(IGrid grid, EntityPlayer viewer) {
        ISecurityGrid security = grid.getCache(ISecurityGrid.class);
        return security == null || security.hasPermission(viewer, SecurityPermissions.BUILD);
    }

    private static int probe(IGrid grid, BridgeSink sink, int nodeBudget) {
        if (nodeBudget <= 0) return 0;
        int probed = 0;
        for (Class<? extends IGridHost> hostType : grid.getMachinesClasses()) {
            List<MeBridgeProbe> probes = MeBridgeProbeRegistry.probesFor(hostType);
            if (probes.isEmpty()) continue;
            for (IGridNode node : grid.getMachines(hostType)) {
                if (probed >= nodeBudget) return probed;
                probed++;
                for (MeBridgeProbe probe : probes) probe.probe(node, grid, sink);
            }
        }
        return probed;
    }

    private static IGridNode representative(IGrid grid) {
        IGridNode fallback = null;
        IGridNode device = null;
        for (IGridNode node : grid.getNodes()) {
            if (GridCoord.of(node) == null) continue;
            // Class identity rather than instanceof: resolving the controller hierarchy drags in optional mod APIs.
            if (isController(node.getMachine())) return node;
            if (device == null && node.hasFlag(GridFlags.REQUIRE_CHANNEL)) device = node;
            if (fallback == null) fallback = node;
        }
        if (device != null) return device;
        if (fallback != null) return fallback;
        IGridNode pivot = grid.getPivot();
        return GridCoord.of(pivot) == null ? null : pivot;
    }

    private static boolean collect(IGrid grid, int networkId, List<TopologyDevice> sink, int budget) {
        if (budget <= 0) return grid.getNodes()
            .size() > 0 && hasDevices(grid);
        Set<GridCoord> seen = new HashSet<>();
        int added = 0;
        for (IGridNode node : grid.getNodes()) {
            if (!node.hasFlag(GridFlags.REQUIRE_CHANNEL)) continue;
            if (added >= budget) return true;
            GridCoord position = GridCoord.of(node);
            if (position == null || !seen.add(position)) continue;
            sink.add(
                new TopologyDevice(
                    sink.size(),
                    networkId,
                    position,
                    ProbeSupport.icon(node),
                    node.isActive(),
                    node.meetsChannelRequirements()));
            added++;
        }
        return false;
    }

    private static boolean hasDevices(IGrid grid) {
        for (IGridNode node : grid.getNodes()) {
            if (node.hasFlag(GridFlags.REQUIRE_CHANNEL)) return true;
        }
        return false;
    }

    private static boolean isController(IGridHost machine) {
        if (machine == null) return false;
        Class<?> type = machine.getClass();
        return type == TileController.class || type == TileCreativeEnergyController.class;
    }

    private static TopologyNetwork describe(IGrid grid, int id, int depth) {
        IGridNode pivot = representative(grid);
        GridCoord coord = GridCoord.of(pivot);
        IPathingGrid pathing = grid.getCache(IPathingGrid.class);
        IEnergyGrid energy = grid.getCache(IEnergyGrid.class);
        ICraftingGrid crafting = grid.getCache(ICraftingGrid.class);

        int nodes = 0, devices = 0, online = 0, starving = 0;
        for (IGridNode node : grid.getNodes()) {
            nodes++;
            if (!node.hasFlag(GridFlags.REQUIRE_CHANNEL)) continue;
            devices++;
            if (node.isActive()) online++;
            if (!node.meetsChannelRequirements()) starving++;
        }
        return new TopologyNetwork(
            id,
            coord == null ? 0 : coord.dimension(),
            coord,
            pivot == null ? null : ProbeSupport.icon(pivot),
            pathing == null ? ControllerState.NO_CONTROLLER : pathing.getControllerState(),
            pathing != null && pathing.isNetworkBooting(),
            energy != null && energy.isNetworkPowered(),
            nodes,
            devices,
            online,
            starving,
            energy == null ? 0 : energy.getStoredPower(),
            energy == null ? 0 : energy.getMaxStoredPower(),
            energy == null ? 0 : energy.getIdlePowerUsage(),
            crafting == null ? 0
                : crafting.getCpus()
                    .size(),
            depth);
    }

    private static class Collector implements BridgeSink {

        private final List<BridgeReport> reports = new ArrayList<>();

        @Override
        public void report(BridgeReport report) {
            if (reports.size() < MAX_REPORTS_PER_NETWORK) reports.add(report);
        }
    }

    private record BridgeKey(int kind, GridCoord low, GridCoord high) {

        static BridgeKey of(BridgeReport report) {
            GridCoord a = report.origin();
            GridCoord b = report.remoteEnd();
            boolean ordered = b == null || compare(a, b) <= 0;
            return new BridgeKey(
                report.kind()
                    .ordinal(),
                ordered ? a : b,
                ordered ? b : a);
        }

        private static int compare(GridCoord a, GridCoord b) {
            if (a.dimension() != b.dimension()) return Integer.compare(a.dimension(), b.dimension());
            if (a.x() != b.x()) return Integer.compare(a.x(), b.x());
            if (a.y() != b.y()) return Integer.compare(a.y(), b.y());
            if (a.z() != b.z()) return Integer.compare(a.z(), b.z());
            return Integer.compare(
                a.side()
                    .ordinal(),
                b.side()
                    .ordinal());
        }
    }
}
