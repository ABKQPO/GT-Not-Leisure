package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;

import lombok.Getter;

public class TileEntityEternalGregTechWorkshop extends TileEntity {

    @Getter
    private float rotAngle = 0, rotAxisX = 1, rotAxisY = 0, rotAxisZ = 0, offsetX = 0, offsetY = 1, offsetZ = 0;
    private AxisAlignedBB renderBoundingBox;
    @Getter
    private int renderCount = 1, rot = 0;

    private static final String NBT_TAG = "EGTWRender:";
    private static final String ROT_ANGLE_NBT_TAG = NBT_TAG + "ROT_ANGLE";
    private static final String ROT_AXIS_X_NBT_TAG = NBT_TAG + "ROT_AXIS_X";
    private static final String ROT_AXIS_Y_NBT_TAG = NBT_TAG + "ROT_AXIS_Y";
    private static final String ROT_AXIS_Z_NBT_TAG = NBT_TAG + "ROT_AXIS_Z";
    private static final String OFFSET_X_NBT_TAG = NBT_TAG + "OFFSET__X";
    private static final String OFFSET_Y_NBT_TAG = NBT_TAG + "OFFSET_Y";
    private static final String OFFSET_Z_NBT_TAG = NBT_TAG + "OFFSET_Z";
    private static final String ROTATION_NBT_TAG = NBT_TAG + "ROTATION";
    private static final String RENDERS_NBT_TAG = NBT_TAG + "RENDERS";

    private static final double RING_RADIUS = 512;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (renderBoundingBox == null) {
            double x = this.xCoord;
            double y = this.yCoord;
            double z = this.zCoord;
            renderBoundingBox = AxisAlignedBB.getBoundingBox(
                x - RING_RADIUS,
                y - RING_RADIUS,
                z - RING_RADIUS,
                x + RING_RADIUS + 1,
                y + RING_RADIUS + 1,
                z + RING_RADIUS + 1);
        }
        return renderBoundingBox;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return Double.MAX_VALUE;
    }

    public void setRenderCount(int count) {
        if (renderCount < 1) return;
        renderCount = count;
    }

    public int getRotation() {
        return rot;
    }

    public static float interpolate(float x0, float x1, float y0, float y1, float x) {
        return y0 + ((x - x0) * (y1 - y0)) / (x1 - x0);
    }

    public void setRenderRotation(Rotation rotation, ForgeDirection direction) {
        switch (direction) {
            case SOUTH, NORTH -> rotAngle = 90;
            case WEST -> rotAngle = 0;
            case EAST -> rotAngle = 180;
            case UP, DOWN -> rotAngle = -90;
        }
        rotAxisX = 0;
        rotAxisY = direction.offsetZ + direction.offsetX;
        rotAxisZ = direction.offsetY;
        offsetX = direction.offsetX;
        offsetY = direction.offsetY;
        offsetZ = direction.offsetZ;
        rot = rotation.getIndex();

        updateToClient();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(RENDERS_NBT_TAG, renderCount);
        compound.setInteger(ROTATION_NBT_TAG, rot);
        compound.setFloat(ROT_ANGLE_NBT_TAG, rotAngle);
        compound.setFloat(ROT_AXIS_X_NBT_TAG, rotAxisX);
        compound.setFloat(ROT_AXIS_Y_NBT_TAG, rotAxisY);
        compound.setFloat(ROT_AXIS_Z_NBT_TAG, rotAxisZ);
        compound.setFloat(OFFSET_X_NBT_TAG, offsetX);
        compound.setFloat(OFFSET_Y_NBT_TAG, offsetY);
        compound.setFloat(OFFSET_Z_NBT_TAG, offsetZ);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        renderCount = compound.getInteger(RENDERS_NBT_TAG);
        if (renderCount < 1) renderCount = 1;

        rot = compound.getInteger(ROTATION_NBT_TAG);
        rotAngle = compound.getFloat(ROT_ANGLE_NBT_TAG);
        rotAxisX = compound.getFloat(ROT_AXIS_X_NBT_TAG);
        rotAxisY = compound.getFloat(ROT_AXIS_Y_NBT_TAG);
        rotAxisZ = compound.getFloat(ROT_AXIS_Z_NBT_TAG);
        offsetX = compound.getFloat(OFFSET_X_NBT_TAG);
        offsetY = compound.getFloat(OFFSET_Y_NBT_TAG);
        offsetZ = compound.getFloat(OFFSET_Z_NBT_TAG);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!worldObj.isRemote) {
            if (worldObj.getTotalWorldTime() % 100 == 0) {
                updateToClient();
            }
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbttagcompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    public void updateToClient() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
