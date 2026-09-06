package com.science.gtnl.utils.world.stellar;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.world.teams.TeamNetworkManager;

public class StellarIrisWorldSavedData extends WorldSavedData {

    public static final String DATA_NAME = "GTNL_StellarIrisWorldData";
    private static final String TEAMS_TAG = "teams";
    private static final String LEADER_TAG = "leader";
    private static final String STATE_TAG = "state";

    private final Map<UUID, StellarIrisTeamState> teamStates = new LinkedHashMap<>();

    public StellarIrisWorldSavedData() {
        super(DATA_NAME);
    }

    public StellarIrisWorldSavedData(String name) {
        super(name);
    }

    public static StellarIrisWorldSavedData get(World world) {
        World storageWorld = DimensionManager.getWorld(0);
        if (storageWorld == null) {
            storageWorld = world;
        }
        MapStorage storage = storageWorld.mapStorage;
        StellarIrisWorldSavedData data = (StellarIrisWorldSavedData) storage
            .loadData(StellarIrisWorldSavedData.class, DATA_NAME);
        if (data == null) {
            data = new StellarIrisWorldSavedData();
            storage.setData(DATA_NAME, data);
        }
        return data;
    }

    public StellarIrisTeamState getOrCreateTeamState(UUID teamLeaderId) {
        StellarIrisTeamState state = teamStates.get(teamLeaderId);
        if (state == null) {
            state = new StellarIrisTeamState();
            teamStates.put(teamLeaderId, state);
            markDirty();
        }
        return state;
    }

    public StellarIrisTeamState getTeamState(UUID teamLeaderId) {
        return teamStates.get(teamLeaderId);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        teamStates.clear();
        NBTTagList teams = tag.getTagList(TEAMS_TAG, 10);
        for (int index = 0; index < teams.tagCount(); index++) {
            NBTTagCompound teamTag = teams.getCompoundTagAt(index);
            try {
                UUID leaderId = UUID.fromString(teamTag.getString(LEADER_TAG));
                StellarIrisTeamState state = new StellarIrisTeamState();
                state.deserializeNBT(teamTag.getCompoundTag(STATE_TAG));
                teamStates.put(TeamNetworkManager.getTeamId(leaderId), state);
            } catch (IllegalArgumentException exception) {
                ScienceNotLeisure.LOG.warn("Skipping invalid Stellar Iris team data entry", exception);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList teams = new NBTTagList();
        for (Map.Entry<UUID, StellarIrisTeamState> entry : teamStates.entrySet()) {
            NBTTagCompound teamTag = new NBTTagCompound();
            teamTag.setString(
                LEADER_TAG,
                entry.getKey()
                    .toString());
            teamTag.setTag(
                STATE_TAG,
                entry.getValue()
                    .serializeNBT());
            teams.appendTag(teamTag);
        }
        tag.setTag(TEAMS_TAG, teams);
    }
}
