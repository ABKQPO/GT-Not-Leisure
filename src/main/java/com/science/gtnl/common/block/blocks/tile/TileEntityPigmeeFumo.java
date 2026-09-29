// Pigmee Fumo port from AE2 Lightning Tech Reborn
// (https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn).
// LGPL-3.0, model by TedXenon. Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

// Spin state (not @SideOnly(CLIENT)): the angle derives from the world clock; the saved value is the turn's start time.
public class TileEntityPigmeeFumo extends TileEntity {

    // Matches the upstream constant; one revolution takes sixty ticks.
    public static final float SPIN_DEGREES_PER_TICK = 6.0F;

    private static final String TAG_SPINNING = "Spinning";
    private static final String TAG_SPIN_START = "SpinStart";

    // Matching upstream, a fresh doll stands still until it is right-clicked.
    private boolean spinning;

    // World time the current turn began, which is what gives every doll its own phase.
    private long spinStartTime;

    public boolean isSpinning() {
        return spinning;
    }

    // Elapsed is taken modulo one revolution so a float can resolve single degrees however long the world has run.
    public float getRenderYRot(float partialTick) {
        if (worldObj == null) return 0.0F;
        long elapsed = worldObj.getTotalWorldTime() - spinStartTime;
        return (elapsed % 60L + partialTick) * SPIN_DEGREES_PER_TICK;
    }

    // Turning the doll on restarts the angle, since the elapsed count is measured from the flip.
    public void toggleSpinningServer() {
        if (worldObj == null || worldObj.isRemote) {
            return;
        }
        spinning = !spinning;
        if (spinning) {
            spinStartTime = worldObj.getTotalWorldTime();
        }
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(TAG_SPINNING, spinning);
        tag.setLong(TAG_SPIN_START, spinStartTime);
    }

    // Tiles saved before the start time was recorded fall back to a position-derived value, so neighbours differ.
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        spinning = tag.getBoolean(TAG_SPINNING);
        spinStartTime = tag.hasKey(TAG_SPIN_START) ? tag.getLong(TAG_SPIN_START)
            : xCoord * 31L + yCoord * 17L + zCoord * 71L;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
