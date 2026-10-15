package com.science.gtnl.common.topology.probe;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.api.topology.BridgeReport;
import com.science.gtnl.api.topology.BridgeSink;
import com.science.gtnl.api.topology.GridCoord;
import com.science.gtnl.api.topology.MeBridgeProbe;
import com.science.gtnl.api.topology.TopologyLinkKind;

import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.parts.automation.PartBaseAnnihilationPlane;
import appeng.parts.automation.PartBaseFormationPlane;
import appeng.parts.automation.PartSharedItemBus;

public class AdjacentHostBridgeProbe implements MeBridgeProbe {

    @Override
    public String id() {
        return "gtnl:adjacent_host";
    }

    @Override
    public boolean accepts(Class<? extends IGridHost> hostType) {
        return PartSharedItemBus.class.isAssignableFrom(hostType)
            || PartBaseFormationPlane.class.isAssignableFrom(hostType)
            || PartBaseAnnihilationPlane.class.isAssignableFrom(hostType);
    }

    @Override
    public void probe(IGridNode node, IGrid grid, BridgeSink sink) {
        GridCoord origin = GridCoord.of(node);
        if (origin == null || origin.side() == ForgeDirection.UNKNOWN) return;
        TileEntity target = origin.offset()
            .tile();
        if (!(target instanceof IGridHost host)) return;
        IGridNode remoteNode = host.getGridNode(
            origin.side()
                .getOpposite());
        if (remoteNode == null) return;
        IGrid remote = remoteNode.getGrid();
        if (remote == null || remote == grid) return;
        sink.report(
            new BridgeReport(
                TopologyLinkKind.ADJACENT,
                remote,
                origin,
                GridCoord.of(remoteNode),
                node.isActive() && remoteNode.isActive(),
                node.getMachine() instanceof IPart part ? ProbeSupport.icon(part) : ProbeSupport.icon(node)));
    }
}
