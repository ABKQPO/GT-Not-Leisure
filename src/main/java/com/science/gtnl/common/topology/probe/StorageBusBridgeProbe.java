package com.science.gtnl.common.topology.probe;

import com.science.gtnl.api.topology.BridgeReport;
import com.science.gtnl.api.topology.BridgeSink;
import com.science.gtnl.api.topology.GridCoord;
import com.science.gtnl.api.topology.MeBridgeProbe;
import com.science.gtnl.api.topology.TopologyLinkKind;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.parts.misc.PartStorageBus;

public class StorageBusBridgeProbe implements MeBridgeProbe {

    @Override
    public String id() {
        return "gtnl:storage_bus";
    }

    @Override
    public boolean accepts(Class<? extends IGridHost> hostType) {
        return PartStorageBus.class.isAssignableFrom(hostType);
    }

    @Override
    public void probe(IGridNode node, IGrid grid, BridgeSink sink) {
        if (!(node.getMachine() instanceof PartStorageBus bus)) return;
        IGrid remote = bus.getConnectedGrid();
        if (remote == null || remote == grid) return;
        GridCoord origin = GridCoord.of(node);
        if (origin == null) return;
        sink.report(
            new BridgeReport(
                TopologyLinkKind.STORAGE,
                remote,
                origin,
                origin.offset(),
                node.isActive(),
                ProbeSupport.icon(bus)));
    }
}
