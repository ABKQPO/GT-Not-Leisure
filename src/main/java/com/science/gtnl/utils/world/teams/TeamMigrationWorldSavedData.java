package com.science.gtnl.utils.world.teams;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class TeamMigrationWorldSavedData extends WorldSavedData {

    public static final String DATA_NAME = "GTNL_TeamMigration";
    private static final String COMPLETE_TAG = "complete";

    private boolean complete;

    public TeamMigrationWorldSavedData() {
        super(DATA_NAME);
    }

    public TeamMigrationWorldSavedData(String name) {
        super(name);
    }

    public static TeamMigrationWorldSavedData get(MapStorage storage) {
        TeamMigrationWorldSavedData data = (TeamMigrationWorldSavedData) storage
            .loadData(TeamMigrationWorldSavedData.class, DATA_NAME);
        if (data == null) {
            data = new TeamMigrationWorldSavedData();
            storage.setData(DATA_NAME, data);
        }
        return data;
    }

    public boolean isComplete() {
        return complete;
    }

    public void markComplete() {
        complete = true;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        complete = tag.getBoolean(COMPLETE_TAG);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setBoolean(COMPLETE_TAG, complete);
    }
}
