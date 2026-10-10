package com.science.gtnl.common.wireless;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessChannelPrototype.Address;

/** Tile-owned data: cable, mounted parts and external nodes each retain their own frequency provenance. */
public final class WirelessLinkData {

    public static final String KEY = WirelessTileData.KEY;
    private static final int VERSION = 1;

    public record Slot(ForgeDirection side, boolean external) {}

    public record Claim(String kind, Set<Address> frequencies, Set<Address> paused, Map<Address, String> names,
        Set<Address> entrances) {

        public Claim(String kind, Set<Address> frequencies) {
            this(kind, frequencies, Set.of());
        }

        public Claim(String kind, Set<Address> frequencies, Set<Address> paused) {
            this(kind, frequencies, paused, Map.of());
        }

        public Claim(String kind, Set<Address> frequencies, Set<Address> paused, Map<Address, String> names) {
            this(kind, frequencies, paused, names, Set.of());
        }

        public Claim {
            frequencies = Set.copyOf(frequencies);
            entrances = entrances.stream()
                .filter(frequencies::contains)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
            paused = paused.stream()
                .filter(frequencies::contains)
                .collect(java.util.stream.Collectors.toUnmodifiableSet());
            Map<Address, String> clean = new HashMap<>();
            for (var entry : names.entrySet()) {
                String name = cleanName(entry.getValue());
                if (frequencies.contains(entry.getKey()) && !name.isEmpty()) clean.put(entry.getKey(), name);
            }
            names = Map.copyOf(clean);
        }
    }

    public static String cleanName(String value) {
        if (value == null) return "";
        return value.codePoints()
            .filter(c -> !Character.isISOControl(c) && c != 0xA7 && !(c >= 0xD800 && c <= 0xDFFF))
            .limit(32)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString()
            .strip();
    }

    private WirelessLinkData() {}

    public static Map<Slot, Claim> read(NBTTagCompound tileData) {
        Map<Slot, Claim> result = new HashMap<>();
        if (!tileData.hasKey(KEY, 10)) return result;
        NBTTagCompound data = tileData.getCompoundTag(KEY);
        if (!data.hasKey("version", 3) || data.getInteger("version") != VERSION) return result;
        NBTTagList entries = data.getTagList("nodes", 10);
        for (int i = 0; i < entries.tagCount(); i++) {
            NBTTagCompound entry = entries.getCompoundTagAt(i);
            int side = entry.getInteger("side");
            if (!entry.hasKey("side", 3) || side < 0
                || side >= ForgeDirection.values().length
                || !entry.hasKey("external", 1)
                || !entry.hasKey("kind", 8)
                || entry.getString("kind")
                    .isEmpty())
                continue;
            Set<Address> frequencies = new HashSet<>();
            Set<Address> paused = new HashSet<>();
            Set<Address> entrances = new HashSet<>();
            Map<Address, String> names = new HashMap<>();
            NBTTagList sources = entry.getTagList("frequencies", 10);
            for (int j = 0; j < sources.tagCount(); j++) {
                NBTTagCompound source = sources.getCompoundTagAt(j);
                if (!source.hasKey("dimension", 3) || !source.hasKey("x", 3)
                    || !source.hasKey("y", 3)
                    || !source.hasKey("z", 3)
                    || !source.hasKey("side", 3)) continue;
                int sourceSide = source.getInteger("side");
                if (sourceSide < 0 || sourceSide >= ForgeDirection.values().length) continue;
                Address address = new Address(
                    source.getInteger("dimension"),
                    source.getInteger("x"),
                    source.getInteger("y"),
                    source.getInteger("z"),
                    ForgeDirection.values()[sourceSide]);
                frequencies.add(address);
                if (source.getBoolean("paused")) paused.add(address);
                if (source.getBoolean("entrance")) entrances.add(address);
                names.put(address, source.getString("name"));
            }
            Slot slot = new Slot(ForgeDirection.values()[side], entry.getBoolean("external"));
            if (!frequencies.isEmpty())
                result.put(slot, new Claim(entry.getString("kind"), frequencies, paused, names, entrances));
        }
        return result;
    }

    /** Returns whether the tile needs saving. Unknown versions are preserved, never silently rewritten. */
    public static boolean write(NBTTagCompound tileData, Map<Slot, Claim> claims) {
        if (tileData.hasKey(KEY, 10) && tileData.getCompoundTag(KEY)
            .getInteger("version") != VERSION) return false;
        if (read(tileData).equals(claims)) return false;
        if (claims.isEmpty()) {
            tileData.removeTag(KEY);
            return true;
        }
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("version", VERSION);
        NBTTagList entries = new NBTTagList();
        for (var entry : claims.entrySet()) {
            if (entry.getValue()
                .frequencies()
                .isEmpty()) continue;
            NBTTagCompound node = new NBTTagCompound();
            node.setInteger(
                "side",
                entry.getKey()
                    .side()
                    .ordinal());
            node.setBoolean(
                "external",
                entry.getKey()
                    .external());
            node.setString(
                "kind",
                entry.getValue()
                    .kind());
            NBTTagList frequencies = new NBTTagList();
            for (Address address : entry.getValue()
                .frequencies()) {
                NBTTagCompound source = new NBTTagCompound();
                source.setInteger("dimension", address.dimension());
                source.setInteger("x", address.x());
                source.setInteger("y", address.y());
                source.setInteger("z", address.z());
                String name = entry.getValue()
                    .names()
                    .get(address);
                if (name != null) source.setString("name", name);
                if (entry.getValue()
                    .paused()
                    .contains(address)) source.setBoolean("paused", true);
                if (entry.getValue()
                    .entrances()
                    .contains(address)) source.setBoolean("entrance", true);
                source.setInteger(
                    "side",
                    address.side()
                        .ordinal());
                frequencies.appendTag(source);
            }
            node.setTag("frequencies", frequencies);
            entries.appendTag(node);
        }
        data.setTag("nodes", entries);
        tileData.setTag(KEY, data);
        return true;
    }
}
