package com.science.gtnl.common.wireless;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public interface WirelessTileData {

    String KEY = "GTNLWirelessLinks";

    NBTTagCompound gtnl$wirelessData();

    boolean gtnl$hasWirelessData();

    static NBTTagCompound of(TileEntity tile) {
        return ((WirelessTileData) tile).gtnl$wirelessData();
    }
}
