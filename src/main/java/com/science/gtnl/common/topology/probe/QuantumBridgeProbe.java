package com.science.gtnl.common.topology.probe;

import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.api.topology.BridgeReport;
import com.science.gtnl.api.topology.BridgeSink;
import com.science.gtnl.api.topology.GridCoord;
import com.science.gtnl.api.topology.MeBridgeProbe;
import com.science.gtnl.api.topology.TopologyLinkKind;

import appeng.api.AEApi;
import appeng.api.features.ILocatable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.me.cluster.implementations.QuantumCluster;
import appeng.tile.qnb.TileQuantumBridge;

public class QuantumBridgeProbe implements MeBridgeProbe {

    @Override
    public String id() {
        return "gtnl:quantum_bridge";
    }

    @Override
    public boolean accepts(Class<? extends IGridHost> hostType) {
        return TileQuantumBridge.class.isAssignableFrom(hostType);
    }

    @Override
    public void probe(IGridNode node, IGrid grid, BridgeSink sink) {
        if (!(node.getMachine() instanceof TileQuantumBridge bridge)) return;
        if (!(bridge.getCluster() instanceof QuantumCluster cluster)) return;
        // Every block of the ring is its own node; only the singularity holder represents the cluster.
        if (cluster.getCenter() != bridge) return;
        GridCoord origin = GridCoord.of(node);
        if (origin == null) return;
        QuantumCluster partner = resolvePartner(cluster);
        TileQuantumBridge partnerCenter = partner == null ? null : partner.getCenter();
        IGridNode partnerNode = partnerCenter == null ? null : partnerCenter.getGridNode(ForgeDirection.UNKNOWN);
        IGrid remote = partnerNode == null ? null : partnerNode.getGrid();
        sink.report(
            new BridgeReport(
                TopologyLinkKind.QUANTUM,
                remote == null ? grid : remote,
                origin,
                partnerCenter == null ? null : GridCoord.of(partnerCenter.getLocation(), ForgeDirection.UNKNOWN),
                partnerNode != null && bridge.isPowered() && node.isActive(),
                ProbeSupport.icon(node)));
    }

    private static QuantumCluster resolvePartner(QuantumCluster cluster) {
        long serial = cluster.getOtherSide();
        if (serial == 0) return null;
        ILocatable located = AEApi.instance()
            .registries()
            .locatable()
            .getLocatableBy(serial);
        return located instanceof QuantumCluster partner && partner != cluster ? partner : null;
    }
}
