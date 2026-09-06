package com.science.gtnl.utils.world.steam;

import static com.science.gtnl.utils.world.steam.SteamWirelessNetworkManager.GLOBAL_STEAM;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.world.teams.TeamNetworkManager;

public class GlobalSteamWorldSavedData extends WorldSavedData {

    public static GlobalSteamWorldSavedData INSTANCE;

    public static final String DATA_NAME = "GregTech_WirelessSteamWorldSavedData";

    public static final String GLOBAL_STEAM_NBT_TAG = "GregTech_GlobalSteam_MapNBTTag";
    public static final String GLOBAL_STEAM_TEAM_NBT_TAG = "GregTech_GlobalSteamTeam_MapNBTTag";
    private static final String FORMAT_VERSION_TAG = "formatVersion";
    private static final int TEAM_ID_FORMAT_VERSION = 2;

    public static void loadInstance(World world) {
        GLOBAL_STEAM.clear();

        MapStorage storage = world.mapStorage;
        INSTANCE = (GlobalSteamWorldSavedData) storage.loadData(GlobalSteamWorldSavedData.class, DATA_NAME);
        if (INSTANCE == null) {
            INSTANCE = new GlobalSteamWorldSavedData();
            storage.setData(DATA_NAME, INSTANCE);
        }
        INSTANCE.markDirty();
    }

    public GlobalSteamWorldSavedData() {
        super(DATA_NAME);
    }

    public GlobalSteamWorldSavedData(String name) {
        super(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        Map<UUID, BigInteger> loadedSteam = readSteam(nbtTagCompound);
        TeamNetworkManager.mergeLegacyTeams(readLegacyTeams(nbtTagCompound));

        if (nbtTagCompound.getInteger(FORMAT_VERSION_TAG) < TEAM_ID_FORMAT_VERSION) {
            loadedSteam.forEach(
                (legacyLeaderId, steam) -> GLOBAL_STEAM
                    .merge(TeamNetworkManager.getTeamId(legacyLeaderId), steam, BigInteger::add));
            markDirty();
        } else {
            GLOBAL_STEAM.putAll(loadedSteam);
        }
    }

    private Map<UUID, BigInteger> readSteam(NBTTagCompound nbtTagCompound) {
        Map<UUID, BigInteger> loadedSteam = new HashMap<>();
        try {
            byte[] ba = nbtTagCompound.getByteArray(GLOBAL_STEAM_NBT_TAG);
            if (ba.length == 0) return loadedSteam;

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ba);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

                Object data = objectInputStream.readObject();
                HashMap<Object, BigInteger> hashData = (HashMap<Object, BigInteger>) data;

                for (Map.Entry<Object, BigInteger> entry : hashData.entrySet()) {
                    try {
                        loadedSteam.put(
                            UUID.fromString(
                                entry.getKey()
                                    .toString()),
                            entry.getValue());
                    } catch (RuntimeException ignored) {
                        ScienceNotLeisure.LOG.warn(
                            "[GlobalSteamWorldSavedData] Skipping invalid UUID key in GlobalSteam: {}",
                            entry.getKey());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException exception) {
            ScienceNotLeisure.LOG.error("[GlobalSteamWorldSavedData] {} LOAD FAILED", GLOBAL_STEAM_NBT_TAG, exception);
        }
        return loadedSteam;
    }

    private Map<UUID, UUID> readLegacyTeams(NBTTagCompound nbtTagCompound) {
        // TODO: Remove this legacy mapping after the migration support window ends.
        Map<UUID, UUID> legacyTeams = new HashMap<>();
        try {
            if (!nbtTagCompound.hasKey(GLOBAL_STEAM_TEAM_NBT_TAG)) return legacyTeams;

            byte[] ba = nbtTagCompound.getByteArray(GLOBAL_STEAM_TEAM_NBT_TAG);
            if (ba.length == 0) return legacyTeams;

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ba);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

                Object data = objectInputStream.readObject();
                HashMap<String, String> oldTeams = (HashMap<String, String>) data;

                for (Map.Entry<String, String> entry : oldTeams.entrySet()) {
                    try {
                        legacyTeams.put(UUID.fromString(entry.getKey()), UUID.fromString(entry.getValue()));
                    } catch (RuntimeException ignored) {
                        ScienceNotLeisure.LOG.warn(
                            "[GlobalSteamWorldSavedData] Skipping invalid UUID in team entry: {}",
                            entry.getKey());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException exception) {
            ScienceNotLeisure.LOG
                .error("[GlobalSteamWorldSavedData] {} LOAD FAILED", GLOBAL_STEAM_TEAM_NBT_TAG, exception);
        }
        return legacyTeams;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(GLOBAL_STEAM);
            objectOutputStream.flush();

            nbtTagCompound.setByteArray(GLOBAL_STEAM_NBT_TAG, byteArrayOutputStream.toByteArray());
            nbtTagCompound.setInteger(FORMAT_VERSION_TAG, TEAM_ID_FORMAT_VERSION);

        } catch (IOException exception) {
            ScienceNotLeisure.LOG.error("[GlobalSteamWorldSavedData] {} SAVE FAILED", GLOBAL_STEAM_NBT_TAG, exception);
        }
    }
}
