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

import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class GlobalSteamWorldSavedData extends WorldSavedData {

    public static GlobalSteamWorldSavedData INSTANCE;

    public static final String DATA_NAME = "GregTech_WirelessSteamWorldSavedData";

    public static final String GLOBAL_STEAM_NBT_TAG = "GregTech_GlobalSteam_MapNBTTag";
    public static final String GLOBAL_STEAM_TEAM_NBT_TAG = "GregTech_GlobalSteamTeam_MapNBTTag";

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
        try {
            byte[] ba = nbtTagCompound.getByteArray(GLOBAL_STEAM_NBT_TAG);
            if (ba.length == 0) return;

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ba);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

                Object data = objectInputStream.readObject();
                HashMap<Object, BigInteger> hashData = (HashMap<Object, BigInteger>) data;

                for (Map.Entry<Object, BigInteger> entry : hashData.entrySet()) {
                    try {
                        GLOBAL_STEAM.put(
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

        try {
            if (!nbtTagCompound.hasKey(GLOBAL_STEAM_TEAM_NBT_TAG)) return;

            byte[] ba = nbtTagCompound.getByteArray(GLOBAL_STEAM_TEAM_NBT_TAG);
            if (ba.length == 0) return;

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ba);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

                Object data = objectInputStream.readObject();
                HashMap<String, String> oldTeams = (HashMap<String, String>) data;

                for (Map.Entry<String, String> entry : oldTeams.entrySet()) {
                    try {
                        SpaceProjectManager
                            .putInTeam(UUID.fromString(entry.getKey()), UUID.fromString(entry.getValue()));
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
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            objectOutputStream.writeObject(GLOBAL_STEAM);
            objectOutputStream.flush();

            nbtTagCompound.setByteArray(GLOBAL_STEAM_NBT_TAG, byteArrayOutputStream.toByteArray());

        } catch (IOException exception) {
            ScienceNotLeisure.LOG.error("[GlobalSteamWorldSavedData] {} SAVE FAILED", GLOBAL_STEAM_NBT_TAG, exception);
        }
    }
}
