/*
 * Pigmee Fumo port from AE2 Lightning Tech Reborn.
 * Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
 * License: LGPL-3.0. Model author: TedXenon.
 * Original model credit: "Made with Blockbench, made by TedXenon".
 * Adapted for GT-Not-Leisure, Forge 1.7.10.
 */
package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Animation and orientation state for a placed Pigmee Fumo.
 *
 * <p>
 * The spin angle is advanced on the client only and is deliberately never written to NBT: it is a purely
 * visual phase. Clients are told the on/off flag through the standard description packet, and the angle
 * carried over from the previous frame gives free {@code partialTick} interpolation.
 *
 * <p>
 * This class is intentionally not {@code @SideOnly(Side.CLIENT)}. Both sides share the same tile type, and
 * the client-only rendering reads these fields directly.
 */
public class TileEntityPigmeeFumo extends TileEntity {

    /** Rotation applied per client tick while spinning, matching the upstream constant exactly. */
    public static final float SPIN_DEGREES_PER_TICK = 6.0F;

    private static final String TAG_SPINNING = "Spinning";

    private boolean spinning = true;
    private float yRot = 0.0F;
    private float prevYRot = 0.0F;

    /** @return the currently synchronised spin flag; does not tick or trigger synchronisation. */
    public boolean isSpinning() {
        return spinning;
    }

    /** Advances the client-side animation by one tick; a no-op on the server or without a world. */
    @Override
    public void updateEntity() {
        if (worldObj == null || !worldObj.isRemote) {
            return;
        }
        prevYRot = yRot;
        if (spinning) {
            yRot += SPIN_DEGREES_PER_TICK;
        }
    }

    /**
     * @param partialTick frame fraction, normally within [0,1]
     * @return {@code prevYRot + (yRot - prevYRot) * partialTick}; modifies no field and applies no modulo
     */
    public float getRenderYRot(float partialTick) {
        return prevYRot + (yRot - prevYRot) * partialTick;
    }

    /** Flips the flag on the server only and notifies clients; a no-op on the client or without a world. */
    public void toggleSpinningServer() {
        if (worldObj == null || worldObj.isRemote) {
            return;
        }
        spinning = !spinning;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    /**
     * @param tag non-null output tag; {@code super} runs first, then only {@code Spinning} is written.
     *            The animation floats are intentionally omitted.
     */
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(TAG_SPINNING, spinning);
    }

    /**
     * @param tag non-null input tag; {@code super} runs first, then {@code Spinning} is read and defaults to
     *            true when absent. The client animation floats are deliberately left untouched.
     */
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        spinning = !tag.hasKey(TAG_SPINNING) || tag.getBoolean(TAG_SPINNING);
    }

    /** @return a non-null S35 packet carrying the full description NBT; created lazily as needed. */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    /**
     * @param net    receiving network context
     * @param packet the S35 state packet; reads {@code func_148857_g()} and neither flips state nor replies
     */
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
