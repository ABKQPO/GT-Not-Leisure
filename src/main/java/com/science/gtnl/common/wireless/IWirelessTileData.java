package com.science.gtnl.common.wireless;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public interface IWirelessTileData {

    String KEY = "GTNLWirelessLinks";

    NBTTagCompound gtnl$wirelessData();

    boolean gtnl$hasWirelessData();

    static NBTTagCompound of(TileEntity tile) {
        return ((IWirelessTileData) tile).gtnl$wirelessData();
    }
}
