// Pigmee Fumo port from AE2 Lightning Tech Reborn.
// Upstream: https://github.com/AE2-Lightning-Tech-Reborn/AE2-Lightning-Tech-Reborn
// License: LGPL-3.0. Model author: TedXenon.
// Original model credit: "Made with Blockbench, made by TedXenon".
// Adapted for GT-Not-Leisure, Forge 1.7.10.
package com.science.gtnl.common.block.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

// Spin state for a placed Pigmee Fumo.
// The angle is derived from the world clock rather than accumulated per tick, so a doll that is already turning
// keeps its phase when the world is reloaded. Storing the angle itself would not work: it changes every tick, so
// syncing it would be a packet per tick, and every doll would restart from zero on load because the value is not
// persisted. The world time a doll started turning does not change, so it survives a save and can ride the
// description packet.
// This class is intentionally not @SideOnly(Side.CLIENT). Both sides share the same tile type, and the client-only
// rendering reads these fields directly.
public class TileEntityPigmeeFumo extends TileEntity {

    // Rotation applied per tick while spinning, matching the upstream constant exactly. At this rate one revolution
    // takes sixty ticks.
    public static final float SPIN_DEGREES_PER_TICK = 6.0F;

    private static final String TAG_SPINNING = "Spinning";
    private static final String TAG_SPIN_START = "SpinStart";

    // Whether the doll is turning. Defaults to false, matching upstream: a freshly placed doll stands still until it is
    // right-clicked.
    private boolean spinning;

    // World time at which the current turn began. The rendered angle is measured from here, so every doll carries its
    // own phase, and a doll turned on once continues where it left off after a reload instead of snapping back to zero.
    private long spinStartTime;

    // Returns the currently synchronised spin flag; does not tick or trigger synchronisation.
    public boolean isSpinning() {
        return spinning;
    }

    // partialTick: frame fraction, normally within [0,1]
    // Returns the angle the doll should be drawn at: the ticks elapsed since it started turning, times the per-tick
    // rotation. The elapsed count is taken modulo one revolution so the value stays in a range where a float still
    // resolves single degrees however long the world has run. Modifies no field.
    public float getRenderYRot(float partialTick) {
        if (worldObj == null) return 0.0F;
        long elapsed = worldObj.getTotalWorldTime() - spinStartTime;
        return (elapsed % 60L + partialTick) * SPIN_DEGREES_PER_TICK;
    }

    // Flips the flag on the server only and notifies clients; a no-op on the client or without a world.
    // Turning the doll on restarts the angle from the model's resting orientation, because the elapsed count is
    // measured from the moment of the flip.
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

    // tag: non-null output tag; super runs first, then the spin flags are written.
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(TAG_SPINNING, spinning);
        tag.setLong(TAG_SPIN_START, spinStartTime);
    }

    // tag: non-null input tag; super runs first, then the spin flags are read. Spinning is treated as false when
    // absent, matching upstream.
    // A tile saved before the start time was recorded, which includes anything placed by an earlier build, falls back
    // to a position-derived value: it needs no meaning, only to differ between neighbouring dolls so that they do not
    // all present the same face at the same instant.
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        spinning = tag.getBoolean(TAG_SPINNING);
        spinStartTime = tag.hasKey(TAG_SPIN_START) ? tag.getLong(TAG_SPIN_START)
            : xCoord * 31L + yCoord * 17L + zCoord * 71L;
    }

    // Returns an S35 packet carrying the full description NBT.
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    // net: receiving network context
    // packet: the S35 state packet; reads func_148857_g() and neither flips state nor replies
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
