package com.science.gtnl.common.wireless;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public interface IWirelessTileData {

    String KEY = "GTNLWirelessLinks";
    String FREQUENCY_KEY = "GTNLWirelessFrequency";
    String[] KEYS = { KEY, FREQUENCY_KEY };

    NBTTagCompound gtnl$wirelessData();

    boolean gtnl$hasWirelessData();

    boolean gtnl$hasFrequencyData();

    static NBTTagCompound of(TileEntity tile) {
        return ((IWirelessTileData) tile).gtnl$wirelessData();
    }

    static NBTTagCompound load(NBTTagCompound saved) {
        NBTTagCompound data = null;
        for (String key : KEYS) {
            if (!saved.hasKey(key, 10)) continue;
            if (data == null) data = new NBTTagCompound();
            data.setTag(
                key,
                saved.getCompoundTag(key)
                    .copy());
        }
        return data;
    }

    static void save(NBTTagCompound data, NBTTagCompound out) {
        for (String key : KEYS) {
            if (data != null && data.hasKey(key, 10)) out.setTag(
                key,
                data.getCompoundTag(key)
                    .copy());
            else out.removeTag(key);
        }
    }
}
