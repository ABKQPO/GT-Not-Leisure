package com.science.gtnl.api.armorflight;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public record ArmorFlightAnchor(int dimension, int x, int y, int z, double lift) {

    public static ArmorFlightAnchor of(TileEntity tile, double lift) {
        return new ArmorFlightAnchor(
            tile.getWorldObj().provider.dimensionId,
            tile.xCoord,
            tile.yCoord,
            tile.zCoord,
            lift);
    }

    public double topX() {
        return x + 0.5;
    }

    public double topY() {
        return y + 0.5 + lift;
    }

    public double topZ() {
        return z + 0.5;
    }

    public boolean isIn(World world) {
        return world != null && world.provider.dimensionId == dimension;
    }

    public IArmorFlightHome home() {
        WorldServer world = DimensionManager.getWorld(dimension);
        if (world == null || !world.blockExists(x, y, z)) return null;
        return world.getTileEntity(x, y, z) instanceof IArmorFlightHome home ? home : null;
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Dim", dimension);
        tag.setInteger("X", x);
        tag.setInteger("Y", y);
        tag.setInteger("Z", z);
        tag.setDouble("Lift", lift);
        return tag;
    }

    public static ArmorFlightAnchor readFromNBT(NBTTagCompound tag) {
        return new ArmorFlightAnchor(
            tag.getInteger("Dim"),
            tag.getInteger("X"),
            tag.getInteger("Y"),
            tag.getInteger("Z"),
            tag.getDouble("Lift"));
    }
}
