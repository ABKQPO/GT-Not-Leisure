package com.science.gtnl.common.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import lombok.Getter;
import lombok.Setter;

public class VoidWorldSavedData extends WorldSavedData {

    private static final String DATA_NAME = "GTNL_VoidPlatformData";
    @Getter
    @Setter
    private boolean platformGenerated = false;

    public VoidWorldSavedData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        platformGenerated = nbt.getBoolean("generated");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setBoolean("generated", platformGenerated);
    }

    public static VoidWorldSavedData get(World world) {
        MapStorage storage = world.perWorldStorage;
        VoidWorldSavedData instance = (VoidWorldSavedData) storage.loadData(VoidWorldSavedData.class, DATA_NAME);

        if (instance == null) {
            instance = new VoidWorldSavedData(DATA_NAME);
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }
}
