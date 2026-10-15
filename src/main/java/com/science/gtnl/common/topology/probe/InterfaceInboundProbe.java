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
import appeng.api.parts.IPartHost;
import appeng.parts.misc.PartInterface;
import appeng.tile.misc.TileInterface;

public class InterfaceInboundProbe implements MeBridgeProbe {

    @Override
    public String id() {
        return "gtnl:interface_inbound";
    }

    @Override
    public boolean accepts(Class<? extends IGridHost> hostType) {
        return PartInterface.class.isAssignableFrom(hostType) || TileInterface.class.isAssignableFrom(hostType);
    }

    @Override
    public void probe(IGridNode node, IGrid grid, BridgeSink sink) {
        GridCoord origin = GridCoord.of(node);
        if (origin == null) return;
        boolean part = node.getMachine() instanceof IPart;
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            // A part interface only ever faces outwards along its own side; a block interface exposes all six.
            if (part && origin.side() != ForgeDirection.UNKNOWN && side != origin.side()) continue;
            GridCoord neighbour = origin.withSide(side)
                .offset();
            TileEntity tile = neighbour.tile();
            if (!(tile instanceof IGridHost host)) continue;
            IGridNode remoteNode = resolve(host, tile, side.getOpposite());
            if (remoteNode == null) continue;
            IGrid remote = remoteNode.getGrid();
            if (remote == null || remote == grid) continue;
            sink.report(
                new BridgeReport(
                    TopologyLinkKind.STORAGE,
                    remote,
                    origin,
                    GridCoord.of(remoteNode),
                    node.isActive() && remoteNode.isActive(),
                    ProbeSupport.icon(remoteNode)));
        }
    }

    private static IGridNode resolve(IGridHost host, TileEntity tile, ForgeDirection facing) {
        if (tile instanceof IPartHost parts) {
            IPart facingPart = parts.getPart(facing);
            if (facingPart != null) {
                IGridNode external = facingPart.getExternalFacingNode();
                return external != null ? external : facingPart.getGridNode();
            }
        }
        return host.getGridNode(facing);
    }
}
