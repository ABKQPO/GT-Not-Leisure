package com.science.gtnl.common.celestweave;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class CelestweaveBackpackData extends WorldSavedData {

    public static final String NAME = "gtnl_backpack";

    private final Map<String, NBTTagCompound> entries = new HashMap<>();

    public CelestweaveBackpackData(String name) {
        super(name);
    }

    public static CelestweaveBackpackData get() {
        World world = DimensionManager.getWorld(0);
        if (world == null) return null;
        CelestweaveBackpackData data = (CelestweaveBackpackData) world
            .loadItemData(CelestweaveBackpackData.class, NAME);
        if (data == null) {
            data = new CelestweaveBackpackData(NAME);
            world.setItemData(NAME, data);
        }
        return data;
    }

    public NBTTagCompound read(String id) {
        return entries.get(id);
    }

    public void write(String id, NBTTagCompound contents) {
        entries.put(id, contents);
        markDirty();
    }

    public void remove(String id) {
        if (entries.remove(id) != null) markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        entries.clear();
        for (String key : tag.func_150296_c()) entries.put(key, tag.getCompoundTag(key));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        for (Map.Entry<String, NBTTagCompound> entry : entries.entrySet()) tag.setTag(entry.getKey(), entry.getValue());
    }
}
