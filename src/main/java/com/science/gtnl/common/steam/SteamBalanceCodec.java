package com.science.gtnl.common.steam;

import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class SteamBalanceCodec {

    public static final String TEAM_TAG = "Team";
    public static final String STEAM_TAG = "Steam";

    private SteamBalanceCodec() {}

    public static NBTTagList encode(Map<UUID, BigInteger> balances) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<UUID, BigInteger> entry : balances.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(
                TEAM_TAG,
                entry.getKey()
                    .toString());
            tag.setByteArray(
                STEAM_TAG,
                entry.getValue()
                    .toByteArray());
            list.appendTag(tag);
        }
        return list;
    }

    public static Map<UUID, BigInteger> decode(NBTTagList list, Consumer<NBTTagCompound> invalidEntry) {
        Map<UUID, BigInteger> balances = new Object2ObjectOpenHashMap<>();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            byte[] steam = tag.getByteArray(STEAM_TAG);
            try {
                if (steam.length == 0) throw new IllegalArgumentException("missing steam amount");
                balances.merge(UUID.fromString(tag.getString(TEAM_TAG)), new BigInteger(steam), BigInteger::add);
            } catch (IllegalArgumentException exception) {
                invalidEntry.accept(tag);
            }
        }
        return balances;
    }
}
