package com.science.gtnl.common.topology.probe;

import com.science.gtnl.api.topology.BridgeReport;
import com.science.gtnl.api.topology.BridgeSink;
import com.science.gtnl.api.topology.GridCoord;
import com.science.gtnl.api.topology.MeBridgeProbe;
import com.science.gtnl.api.topology.TopologyLinkKind;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.parts.misc.PartToggleBus;
import appeng.parts.networking.PartQuartzFiber;
import appeng.parts.p2p.PartP2PTunnelME;

public class ExternalNodeBridgeProbe implements MeBridgeProbe {

    @Override
    public String id() {
        return "gtnl:external_node";
    }

    @Override
    public boolean accepts(Class<? extends IGridHost> hostType) {
        return PartQuartzFiber.class.isAssignableFrom(hostType) || PartP2PTunnelME.class.isAssignableFrom(hostType)
            || PartToggleBus.class.isAssignableFrom(hostType);
    }

    @Override
    public void probe(IGridNode node, IGrid grid, BridgeSink sink) {
        if (!(node.getMachine() instanceof IPart part)) return;
        IGridNode external = part.getExternalFacingNode();
        if (external == null || external == node) return;
        IGrid remote = external.getGrid();
        if (remote == null || remote == grid) return;
        GridCoord origin = GridCoord.of(node);
        if (origin == null) return;
        sink.report(
            new BridgeReport(
                kind(part),
                remote,
                origin,
                GridCoord.of(external),
                node.isActive() && external.isActive(),
                ProbeSupport.icon(part)));
    }

    private static TopologyLinkKind kind(IPart part) {
        if (part instanceof PartQuartzFiber) return TopologyLinkKind.ENERGY;
        if (part instanceof PartP2PTunnelME) return TopologyLinkKind.TUNNEL;
        if (part instanceof PartToggleBus) return TopologyLinkKind.TOGGLE;
        return TopologyLinkKind.ADJACENT;
    }
}
