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

// Animation and orientation state for a placed Pigmee Fumo.
// The spin angle is advanced on the client only and is deliberately never written to NBT: it is a purely visual phase.
// Clients are told the on/off flag through the standard description packet, and the angle carried over from the
// previous frame gives free partialTick interpolation.
// This class is intentionally not @SideOnly(Side.CLIENT). Both sides share the same tile type, and the
// client-only rendering reads these fields directly.
public class TileEntityPigmeeFumo extends TileEntity {

    // Rotation applied per client tick while spinning, matching the upstream constant exactly.
    public static final float SPIN_DEGREES_PER_TICK = 6.0F;

    private static final String TAG_SPINNING = "Spinning";

    // Whether the doll is turning. Defaults to false, matching upstream: a freshly placed doll stands still until it is
    // right-clicked.
    private boolean spinning;
    private float yRot;
    private float prevYRot;
    // Client-side memory of the last observed flag, used to restart the turn from the resting orientation.
    private boolean wasSpinning;

    // Returns the currently synchronised spin flag; does not tick or trigger synchronisation.
    public boolean isSpinning() {
        return spinning;
    }

    // Advances the client-side animation by one tick; a no-op on the server or without a world.
    // The angle is rewound whenever the flag goes from off to on, so each start begins from the model's resting
    // orientation rather than resuming wherever the previous turn stopped. The flag itself arrives from the server
    // through the description packet, so the transition has to be detected here rather than where it is toggled.
    @Override
    public void updateEntity() {
        if (worldObj == null || !worldObj.isRemote) {
            return;
        }
        if (spinning && !wasSpinning) {
            yRot = 0.0F;
            prevYRot = 0.0F;
        }
        wasSpinning = spinning;
        prevYRot = yRot;
        if (spinning) {
            yRot += SPIN_DEGREES_PER_TICK;
        }
    }

    // partialTick: frame fraction, normally within [0,1]
    // Returns prevYRot + (yRot - prevYRot) * partialTick; modifies no field and applies no modulo
    public float getRenderYRot(float partialTick) {
        return prevYRot + (yRot - prevYRot) * partialTick;
    }

    // Flips the flag on the server only and notifies clients; a no-op on the client or without a world.
    public void toggleSpinningServer() {
        if (worldObj == null || worldObj.isRemote) {
            return;
        }
        spinning = !spinning;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    // tag: non-null output tag; super runs first, then only Spinning is written.
    // The animation floats are intentionally omitted.
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(TAG_SPINNING, spinning);
    }

    // tag: non-null input tag; super runs first, then Spinning is read and is treated as false
    // when absent, matching upstream. The client animation floats are deliberately left untouched.
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        spinning = tag.getBoolean(TAG_SPINNING);
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
