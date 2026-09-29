// Pigmee Fumo port from AE2 Lightning Tech Reborn
// (https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn).
// LGPL-3.0, model by TedXenon. Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.science.gtnl.common.render.model.pigmeeModel.PigmeeFumoAnimation;

import lombok.Getter;

public class TileEntityPigmeeFumo extends TileEntity {

    public static final String TAG_SPINNING = "Spinning";

    @Getter
    public boolean spinning;

    public transient long clientSpinStartNanos;

    public float getRenderYRot() {
        if (!spinning) return 0.0F;
        if (clientSpinStartNanos == 0L) clientSpinStartNanos = System.nanoTime();
        return PigmeeFumoAnimation.degreesSince(clientSpinStartNanos);
    }

    public void toggleSpinningServer() {
        if (worldObj == null || worldObj.isRemote) {
            return;
        }
        setSpinning(!spinning);
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(TAG_SPINNING, spinning);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        setSpinning(tag.getBoolean(TAG_SPINNING));
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(TAG_SPINNING, spinning);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound tag = packet.func_148857_g();
        setSpinning(tag.getBoolean(TAG_SPINNING));
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setSpinning(boolean value) {
        if (spinning != value) clientSpinStartNanos = 0L;
        spinning = value;
    }
}
