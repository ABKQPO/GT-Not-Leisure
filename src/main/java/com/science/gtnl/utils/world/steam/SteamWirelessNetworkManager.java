package com.science.gtnl.utils.world.steam;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.UUID;

import com.science.gtnl.ScienceNotLeisure;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.misc.spaceprojects.SpaceProjectManager;

public class SteamWirelessNetworkManager {

    public static HashMap<UUID, BigInteger> GlobalSteam = new HashMap<>(100, 0.9f);

    private SteamWirelessNetworkManager() {}

    public static void strongCheckOrAddUser(UUID user_uuid) {
        SpaceProjectManager.checkOrCreateTeam(user_uuid);
        user_uuid = SpaceProjectManager.getLeader(user_uuid);
        GlobalSteam.putIfAbsent(user_uuid, BigInteger.ZERO);
    }

    // ------------------------------------------------------------------------------------
    // Add Steam to the users global steam storage. You can enter a negative number to subtract it.
    // If the value goes below 0 it will return false and not perform the operation.
    // BigIntegers have much slower operations than longs/ints. You should call these methods
    // as infrequently as possible and bulk store values to add to the global map.
    public static boolean addSteamToGlobalSteamMap(UUID user_uuid, BigInteger steamAmount) {
        try {
            GlobalSteamWorldSavedData.INSTANCE.markDirty();
        } catch (Exception exception) {
            ScienceNotLeisure.LOG
                .error("[SteamWirelessNetworkManager] Could not mark GlobalSteam dirty in addSteam", exception);
        }

        UUID teamUUID = SpaceProjectManager.getLeader(user_uuid);

        BigInteger totalSteam = GlobalSteam.getOrDefault(teamUUID, BigInteger.ZERO);
        totalSteam = totalSteam.add(steamAmount);

        if (totalSteam.signum() >= 0) {
            GlobalSteam.put(teamUUID, totalSteam);
            return true;
        }

        return false;
    }

    public static boolean addSteamToGlobalSteamMap(UUID user_uuid, long steamAmount) {
        return addSteamToGlobalSteamMap(user_uuid, BigInteger.valueOf(steamAmount));
    }

    public static boolean addSteamToGlobalSteamMap(UUID user_uuid, int steamAmount) {
        return addSteamToGlobalSteamMap(user_uuid, BigInteger.valueOf(steamAmount));
    }

    // Ticks between steam additions to the hatch. For a boiler this is how often steam is sent.
    public static long ticks_between_steam_addition = 100L * 20L;

    // Total number of steam additions this multi can store before it is full.
    public static long number_of_steam_additions = 4L;

    public static long totalStorage(long tier_steam_per_tick) {
        return tier_steam_per_tick * ticks_between_steam_addition * number_of_steam_additions;
    }

    // ------------------------------------------------------------------------------------

    public static BigInteger getUserSteam(UUID user_uuid) {
        return GlobalSteam.getOrDefault(SpaceProjectManager.getLeader(user_uuid), BigInteger.ZERO);
    }

    public static int getUserSteamInt(UUID user_uuid) {
        BigInteger value = GlobalSteam.getOrDefault(SpaceProjectManager.getLeader(user_uuid), BigInteger.ZERO);

        if (value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            return Integer.MAX_VALUE;
        }
        if (value.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0) {
            return Integer.MIN_VALUE;
        }
        return value.intValue();
    }

    // This overwrites the steam in the network. Only use this if you are absolutely sure you know what you are doing.
    public static void setUserSteam(UUID user_uuid, BigInteger steamAmount) {
        try {
            GlobalSteamWorldSavedData.INSTANCE.markDirty();
        } catch (Exception exception) {
            ScienceNotLeisure.LOG
                .error("[SteamWirelessNetworkManager] Could not mark GlobalSteam dirty in setSteam", exception);
        }

        GlobalSteam.put(SpaceProjectManager.getLeader(user_uuid), steamAmount);
    }

    public static void clearGlobalSteamInformationMaps() {
        GlobalSteam.clear();
    }

    public static UUID processInitialSettings(final IGregTechTileEntity machine) {
        final UUID UUID = machine.getOwnerUuid();
        SpaceProjectManager.checkOrCreateTeam(UUID);
        return UUID;
    }
}
