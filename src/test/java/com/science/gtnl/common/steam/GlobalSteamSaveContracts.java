package com.science.gtnl.common.steam;

import static com.gtnllib.GTNLLibContractTest.check;
import static com.science.gtnl.common.steam.SteamWirelessNetworkManager.GLOBAL_STEAM;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class GlobalSteamSaveContracts {

    private static final UUID TEAM = UUID.randomUUID();
    private static final BigInteger STEAM = BigInteger.ONE.shiftLeft(130)
        .add(BigInteger.valueOf(12346));

    private GlobalSteamSaveContracts() {}

    public static void run() throws IOException, ClassNotFoundException {
        GLOBAL_STEAM.clear();
        GLOBAL_STEAM.put(TEAM, STEAM);
        NBTTagCompound saved = new NBTTagCompound();
        new GlobalSteamWorldSavedData().writeToNBT(saved);
        check(
            deserialize(saved.getByteArray(GlobalSteamWorldSavedData.GLOBAL_STEAM_NBT_TAG)) instanceof HashMap,
            "wireless steam is saved as a java.util.HashMap, the format older versions read");
        check(STEAM.equals(reload(saved)), "a saved wireless steam balance survives a restart");

        NBTTagCompound disagreeingCopies = new NBTTagCompound();
        disagreeingCopies.setTag(
            GlobalSteamWorldSavedData.STEAM_BALANCES_NBT_TAG,
            saved.getTagList(GlobalSteamWorldSavedData.STEAM_BALANCES_NBT_TAG, 10));
        disagreeingCopies.setInteger("formatVersion", 2);
        disagreeingCopies
            .setByteArray(GlobalSteamWorldSavedData.GLOBAL_STEAM_NBT_TAG, serialize(new HashMap<UUID, BigInteger>()));
        check(
            STEAM.equals(reload(disagreeingCopies)),
            "the NBT balance list is the source of truth when an older Java-serialized copy disagrees");

        NBTTagList entries = SteamBalanceCodec.encode(Map.of(TEAM, STEAM, UUID.randomUUID(), BigInteger.ZERO));
        NBTTagCompound broken = new NBTTagCompound();
        broken.setString(SteamBalanceCodec.TEAM_TAG, "not-a-uuid");
        broken.setByteArray(SteamBalanceCodec.STEAM_TAG, BigInteger.TEN.toByteArray());
        entries.appendTag(broken);
        List<NBTTagCompound> skipped = new ArrayList<>();
        Map<UUID, BigInteger> decoded = SteamBalanceCodec.decode(entries, skipped::add);
        check(
            decoded.size() == 2 && STEAM.equals(decoded.get(TEAM)) && skipped.size() == 1,
            "one damaged balance entry is skipped without losing the others, zero balances included");

        Object2ObjectOpenHashMap<UUID, BigInteger> fastutilMap = new Object2ObjectOpenHashMap<>();
        fastutilMap.put(TEAM, STEAM);
        NBTTagCompound fastutilSave = new NBTTagCompound();
        fastutilSave.setByteArray(GlobalSteamWorldSavedData.GLOBAL_STEAM_NBT_TAG, serialize(fastutilMap));
        fastutilSave.setInteger("formatVersion", 2);
        check(STEAM.equals(reload(fastutilSave)), "saves written as a fastutil map since 2026-10-24 still load");

        byte[] unreadable = { 1, 2, 3, 4 };
        NBTTagCompound unreadableSave = new NBTTagCompound();
        unreadableSave.setByteArray(GlobalSteamWorldSavedData.GLOBAL_STEAM_NBT_TAG, unreadable);
        unreadableSave.setInteger("formatVersion", 2);
        GLOBAL_STEAM.clear();
        NBTTagCompound afterUnreadable = resave(unreadableSave);
        check(
            Arrays.equals(unreadable, afterUnreadable.getByteArray(GlobalSteamWorldSavedData.UNREADABLE_STEAM_NBT_TAG)),
            "steam data that cannot be read is kept instead of being overwritten by the next save");
        check(STEAM.equals(reload(afterUnreadable)), "balances gained after an unreadable load are still saved");
        check(
            Arrays.equals(
                unreadable,
                resave(afterUnreadable).getByteArray(GlobalSteamWorldSavedData.UNREADABLE_STEAM_NBT_TAG)),
            "the kept unreadable data survives later saves");
        GLOBAL_STEAM.clear();
    }

    private static NBTTagCompound resave(NBTTagCompound saved) {
        GLOBAL_STEAM.clear();
        GlobalSteamWorldSavedData data = new GlobalSteamWorldSavedData();
        data.readFromNBT(saved);
        GLOBAL_STEAM.put(TEAM, STEAM);
        NBTTagCompound written = new NBTTagCompound();
        data.writeToNBT(written);
        return written;
    }

    private static BigInteger reload(NBTTagCompound saved) {
        GLOBAL_STEAM.clear();
        new GlobalSteamWorldSavedData().readFromNBT(saved);
        return GLOBAL_STEAM.get(TEAM);
    }

    private static byte[] serialize(Object value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        return bytes.toByteArray();
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return input.readObject();
        }
    }
}
