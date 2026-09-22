package com.science.gtnl.utils.world.steam;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.UUID;

import com.science.gtnl.utils.world.teams.TeamNetworkManager;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public class SteamWirelessNetworkManager {

    public static HashMap<UUID, BigInteger> GLOBAL_STEAM = new HashMap<>(100, 0.9f);

    private SteamWirelessNetworkManager() {}

    public static void strongCheckOrAddUser(UUID user_uuid) {
        user_uuid = TeamNetworkManager.getTeamId(user_uuid);
        GLOBAL_STEAM.putIfAbsent(user_uuid, BigInteger.ZERO);
    }

    // Add Steam to the users global steam storage. You can enter a negative number to subtract it.
    // If the value goes below 0 it will return false and not perform the operation.
    // BigIntegers have much slower operations than longs/ints. You should call these methods
    // as infrequently as possible and bulk store values to add to the global map.
    public static boolean addSteamToGlobalSteamMap(UUID user_uuid, BigInteger steamAmount) {
        UUID teamUUID = TeamNetworkManager.getTeamId(user_uuid);
        if (steamAmount.signum() == 0) return true;

        BigInteger totalSteam = GLOBAL_STEAM.getOrDefault(teamUUID, BigInteger.ZERO);
        totalSteam = totalSteam.add(steamAmount);

        if (totalSteam.signum() >= 0) {
            GLOBAL_STEAM.put(teamUUID, totalSteam);
            markSteamDirty();
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
    public static long TICKS_BETWEEN_STEAM_ADDITION = 100L * 20L;

    // Total number of steam additions this multi can store before it is full.
    public static long NUMBER_OF_STEAM_ADDITIONS = 4L;

    public static long totalStorage(long tier_steam_per_tick) {
        return tier_steam_per_tick * TICKS_BETWEEN_STEAM_ADDITION * NUMBER_OF_STEAM_ADDITIONS;
    }

    public static BigInteger getUserSteam(UUID user_uuid) {
        return GLOBAL_STEAM.getOrDefault(TeamNetworkManager.getTeamId(user_uuid), BigInteger.ZERO);
    }

    public static int getUserSteamInt(UUID user_uuid) {
        BigInteger value = getUserSteam(user_uuid);

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
        GLOBAL_STEAM.put(TeamNetworkManager.getTeamId(user_uuid), steamAmount);
        markSteamDirty();
    }

    /** Moves a balance between team IDs without resolving either ID as a player. */
    public static void mergeTeamSteam(UUID consumedTeamId, UUID survivingTeamId) {
        if (consumedTeamId.equals(survivingTeamId)) return;

        BigInteger steam = GLOBAL_STEAM.remove(consumedTeamId);
        if (steam == null) return;

        GLOBAL_STEAM.merge(survivingTeamId, steam, BigInteger::add);
        markSteamDirty();
    }

    private static void markSteamDirty() {
        if (GlobalSteamWorldSavedData.INSTANCE != null) {
            GlobalSteamWorldSavedData.INSTANCE.markDirty();
        }
    }

    public static void clearGlobalSteamInformationMaps() {
        GLOBAL_STEAM.clear();
    }

    public static UUID processInitialSettings(final IGregTechTileEntity machine) {
        return machine.getOwnerUuid();
    }
}
