package com.science.gtnl.common.machine.cover;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;

import org.jetbrains.annotations.NotNull;

import com.google.common.io.ByteArrayDataInput;

import gregtech.api.covers.CoverContext;
import gregtech.common.covers.Cover;
import io.netty.buffer.ByteBuf;

/**
 * Base of GTNL covers that keep one int of settings. The int, its packet and its save format are those of GT's old
 * {@code CoverLegacyData}, so covers placed before the move to {@link Cover} keep their settings. Covers that save more
 * override the NBT methods and still read a bare int.
 */
public abstract class IntDataCover extends Cover {

    public int coverData;

    public IntDataCover(CoverContext context) {
        super(context, null);
    }

    @Override
    protected void readDataFromNbt(NBTBase nbt) {
        coverData = nbt instanceof NBTTagInt tagInt ? tagInt.func_150287_d() : 0;
    }

    @Override
    protected @NotNull NBTBase saveDataToNbt() {
        return new NBTTagInt(coverData);
    }

    @Override
    protected void readDataFromPacket(ByteArrayDataInput byteData) {
        coverData = byteData.readInt();
    }

    @Override
    protected void writeDataToByteBuf(ByteBuf byteBuf) {
        byteBuf.writeInt(coverData);
    }

    @Override
    public boolean isRedstoneSensitive(long aTimer) {
        return true;
    }
}
