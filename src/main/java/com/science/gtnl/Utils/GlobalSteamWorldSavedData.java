package com.science.gtnl.Utils;

import static com.science.gtnl.Utils.SteamWirelessNetworkManager.GlobalSteam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class GlobalSteamWorldSavedData extends WorldSavedData {

    public static GlobalSteamWorldSavedData INSTANCE;

    public static final String DATA_NAME = "GregTech_WirelessSteamWorldSavedData";

    public static final String GlobalSteamNBTTag = "GregTech_GlobalSteam_MapNBTTag";
    public static final String GlobalSteamTeamNBTTag = "GregTech_GlobalSteamTeam_MapNBTTag";

    public static void loadInstance(World world) {
        GlobalSteam.clear();

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
            byte[] ba = nbtTagCompound.getByteArray(GlobalSteamNBTTag);
            InputStream byteArrayInputStream = new ByteArrayInputStream(ba);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object data = objectInputStream.readObject();
            HashMap<Object, BigInteger> hashData = (HashMap<Object, BigInteger>) data;
            for (Map.Entry<Object, BigInteger> entry : hashData.entrySet()) {
                try {
                    GlobalSteam.put(
                        UUID.fromString(
                            entry.getKey()
                                .toString()),
                        entry.getValue());
                } catch (RuntimeException ignored) {
                    // likely a malformed UUID, skip
                }
            }
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println(GlobalSteamNBTTag + " LOAD FAILED");
            exception.printStackTrace();
        }

        try {
            if (!nbtTagCompound.hasKey(GlobalSteamTeamNBTTag)) return;
            byte[] ba = nbtTagCompound.getByteArray(GlobalSteamTeamNBTTag);
            InputStream byteArrayInputStream = new ByteArrayInputStream(ba);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object data = objectInputStream.readObject();
            HashMap<String, String> oldTeams = (HashMap<String, String>) data;
            for (String member : oldTeams.keySet()) {
                String leader = oldTeams.get(member);
                try {
                    SpaceProjectManager.putInTeam(UUID.fromString(member), UUID.fromString(leader));
                } catch (RuntimeException ignored) {
                    // likely a malformed UUID, skip
                }
            }
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println(GlobalSteamTeamNBTTag + " LOAD FAILED");
            exception.printStackTrace();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(GlobalSteam);
            objectOutputStream.flush();
            byte[] data = byteArrayOutputStream.toByteArray();
            nbtTagCompound.setByteArray(GlobalSteamNBTTag, data);
        } catch (IOException exception) {
            System.out.println(GlobalSteamNBTTag + " SAVE FAILED");
            exception.printStackTrace();
        }
    }
}
