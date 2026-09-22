package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityArtificialStar extends TileEntity {

    public double size = 0;
    public double targetSize = 30;
    public double initialSize = 0;
    public int ticks = 0;
    public int duration = 150;

    private double previousSize = 0;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setDouble("size", size);
        nbt.setDouble("targetSize", targetSize);
        nbt.setDouble("initialSize", initialSize);
        nbt.setInteger("ticks", ticks);
        nbt.setInteger("duration", duration);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        size = nbt.getDouble("size");
        previousSize = size;
        targetSize = nbt.hasKey("targetSize") ? nbt.getDouble("targetSize") : 30;
        duration = nbt.hasKey("duration") ? Math.max(0, nbt.getInteger("duration")) : 150;
        // Older saves only stored the current size; continue growing from that radius.
        initialSize = nbt.hasKey("initialSize") ? nbt.getDouble("initialSize") : size;
        ticks = nbt.hasKey("ticks") ? Math.max(0, Math.min(duration, nbt.getInteger("ticks")))
            : size == targetSize ? duration : 0;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        previousSize = size;
        if (ticks < duration) {
            double t = (double) ++ticks / duration;
            double easedT = cubicEaseOut(t);
            size = initialSize + (targetSize - initialSize) * easedT;
        } else {
            size = targetSize;
        }
        if (ticks >= duration && size != previousSize && worldObj != null && !worldObj.isRemote) {
            markDirty();
        }
    }

    public double getRenderSize(float partialTicks) {
        return previousSize + (size - previousSize) * partialTicks;
    }

    public double cubicEaseOut(double t) {
        double remaining = 1 - t;
        return 1 - remaining * remaining * remaining;
    }
}
