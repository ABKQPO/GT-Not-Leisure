package com.science.gtnl.common.wireless;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/** A small early hook owns the saved payload independently of AE node invalidation. */
public interface WirelessTileData {

    String KEY = "GTNLWirelessLinks";

    NBTTagCompound gtnl$wirelessData();

    boolean gtnl$hasWirelessData();

    static NBTTagCompound of(TileEntity tile) {
        return ((WirelessTileData) tile).gtnl$wirelessData();
    }
}
