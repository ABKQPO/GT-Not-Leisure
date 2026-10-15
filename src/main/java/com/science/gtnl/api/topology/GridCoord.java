package com.science.gtnl.api.topology;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.util.DimensionalCoord;

public record GridCoord(int dimension, int x, int y, int z, ForgeDirection side) {

    public static GridCoord of(DimensionalCoord location, ForgeDirection side) {
        return location == null ? null
            : new GridCoord(location.getDimension(), location.x, location.y, location.z, side);
    }

    public static GridCoord of(IGridNode node) {
        if (node == null) return null;
        World world = node.getWorld();
        DimensionalCoord location = node.getGridBlock()
            .getLocation();
        if (world == null || location == null) return null;
        GridCoord blockCoord = new GridCoord(
            world.provider.dimensionId,
            location.x,
            location.y,
            location.z,
            ForgeDirection.UNKNOWN);
        TileEntity tile = blockCoord.tile();
        if (!(tile instanceof IPartHost host)) return blockCoord;
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            IPart part = host.getPart(side);
            if (part != null && (part.getGridNode() == node || part.getExternalFacingNode() == node))
                return blockCoord.withSide(side);
        }
        return blockCoord;
    }

    public GridCoord withSide(ForgeDirection newSide) {
        return new GridCoord(dimension, x, y, z, newSide);
    }

    public GridCoord offset() {
        return new GridCoord(dimension, x + side.offsetX, y + side.offsetY, z + side.offsetZ, ForgeDirection.UNKNOWN);
    }

    public TileEntity tile() {
        World world = DimensionManager.getWorld(dimension);
        return world != null && world.blockExists(x, y, z) ? world.getTileEntity(x, y, z) : null;
    }

    public IGridNode node() {
        TileEntity tile = tile();
        if (tile instanceof IPartHost host) {
            IPart part = host.getPart(side);
            return part == null ? null : part.getGridNode();
        }
        return tile instanceof IGridHost host ? host.getGridNode(side) : null;
    }

    public String format() {
        return x + ", " + y + ", " + z;
    }
}
