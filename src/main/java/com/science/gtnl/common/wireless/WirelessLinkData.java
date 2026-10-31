package com.science.gtnl.common.wireless;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

public class WirelessLinkData {

    public static final String KEY = IWirelessTileData.KEY;
    private static final int LEGACY_VERSION = 1;
    private static final int VERSION = 2;
    private static Map<NBTTagCompound, Decoded> readBatch;
    private static int readBatchDepth;

    private record Decoded(NBTTagCompound payload, Map<Slot, Claim> claims) {}

    static void beginReadBatch() {
        if (readBatchDepth++ == 0) readBatch = new Reference2ObjectOpenHashMap<>();
    }

    static void endReadBatch() {
        if (--readBatchDepth == 0) readBatch = null;
    }

    static ReadBatch readBatch() {
        return new ReadBatch();
    }

    public static class ReadBatch implements AutoCloseable {

        private boolean closed;

        private ReadBatch() {
            beginReadBatch();
        }

        @Override
        public void close() {
            if (closed) return;
            closed = true;
            endReadBatch();
        }
    }

    public record Slot(ForgeDirection side, boolean external) {}

    public record Claim(String kind, Set<WirelessFrequency> frequencies, Set<WirelessFrequency> paused,
        Set<WirelessFrequency> entrances) {

        public Claim {
            frequencies = Set.copyOf(frequencies);
            entrances = entrances.isEmpty() ? Set.of()
                : entrances.stream()
                    .filter(frequencies::contains)
                    .collect(Collectors.toUnmodifiableSet());
            paused = paused.isEmpty() ? Set.of()
                : paused.stream()
                    .filter(frequencies::contains)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    public static Map<Slot, Claim> read(NBTTagCompound tileData) {
        // Callers edit their own slot map; Claim and its contents are immutable.
        return readBatch == null ? decode(tileData) : new Object2ObjectOpenHashMap<>(readView(tileData));
    }

    static Claim claim(NBTTagCompound tileData, Slot slot) {
        return readView(tileData).get(slot);
    }

    private static Map<Slot, Claim> readView(NBTTagCompound tileData) {
        if (readBatch == null) return decode(tileData);
        NBTTagCompound payload = tileData.hasKey(KEY, 10) ? tileData.getCompoundTag(KEY) : null;
        Decoded saved = readBatch.get(tileData);
        if (saved == null || saved.payload() != payload) {
            saved = new Decoded(payload, Map.copyOf(decode(tileData)));
            readBatch.put(tileData, saved);
        }
        return saved.claims();
    }

    private static Map<Slot, Claim> decode(NBTTagCompound tileData) {
        Map<Slot, Claim> result = new Object2ObjectOpenHashMap<>();
        if (!tileData.hasKey(KEY, 10)) return result;
        NBTTagCompound data = tileData.getCompoundTag(KEY);
        int version = data.getInteger("version");
        if (!data.hasKey("version", 3) || version != VERSION && version != LEGACY_VERSION) return result;
        ForgeDirection[] directions = ForgeDirection.values();
        NBTTagList entries = data.getTagList("nodes", 10);
        for (int i = 0; i < entries.tagCount(); i++) {
            NBTTagCompound entry = entries.getCompoundTagAt(i);
            int side = entry.getInteger("side");
            if (!entry.hasKey("side", 3) || side < 0
                || side >= directions.length
                || !entry.hasKey("external", 1)
                || !entry.hasKey("kind", 8)
                || entry.getString("kind")
                    .isEmpty())
                continue;
            Set<WirelessFrequency> frequencies = new ObjectOpenHashSet<>();
            Set<WirelessFrequency> paused = new ObjectOpenHashSet<>();
            Set<WirelessFrequency> entrances = new ObjectOpenHashSet<>();
            NBTTagList sources = entry.getTagList("frequencies", 10);
            for (int j = 0; j < sources.tagCount(); j++) {
                NBTTagCompound source = sources.getCompoundTagAt(j);
                WirelessFrequency frequency = readFrequency(source);
                if (frequency == null) continue;
                frequencies.add(frequency);
                if (source.getBoolean("paused")) paused.add(frequency);
                if (source.getBoolean("entrance")) entrances.add(frequency);
            }
            Slot slot = new Slot(directions[side], entry.getBoolean("external"));
            if (!frequencies.isEmpty())
                result.put(slot, new Claim(entry.getString("kind"), frequencies, paused, entrances));
        }
        return result;
    }

    private static WirelessFrequency readFrequency(NBTTagCompound source) {
        if (source.hasKey("id", 8)) {
            UUID id = WirelessFrequencies.uuid(source, "id");
            return id == null ? null : WirelessFrequency.of(id);
        }
        Address position = readPosition(source);
        return position == null ? null : WirelessFrequency.legacy(position);
    }

    private static void writeFrequency(NBTTagCompound source, WirelessFrequency frequency) {
        if (frequency.isLegacy()) writePosition(source, frequency.legacy());
        else source.setString(
            "id",
            frequency.id()
                .toString());
    }

    static Address readPosition(NBTTagCompound tag) {
        if (!tag.hasKey("dimension", 3) || !tag.hasKey("x", 3) || !tag.hasKey("y", 3) || !tag.hasKey("z", 3))
            return null;
        return new Address(
            tag.getInteger("dimension"),
            tag.getInteger("x"),
            tag.getInteger("y"),
            tag.getInteger("z"),
            ForgeDirection.UNKNOWN);
    }

    static NBTTagCompound writePosition(NBTTagCompound tag, Address position) {
        tag.setInteger("dimension", position.dimension());
        tag.setInteger("x", position.x());
        tag.setInteger("y", position.y());
        tag.setInteger("z", position.z());
        return tag;
    }

    public static boolean write(NBTTagCompound tileData, Map<Slot, Claim> claims) {
        if (tileData.hasKey(KEY, 10) && tileData.getCompoundTag(KEY)
            .getInteger("version") > VERSION) return false;
        if (readView(tileData).equals(claims)) return false;
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
            for (WirelessFrequency frequency : entry.getValue()
                .frequencies()) {
                NBTTagCompound source = new NBTTagCompound();
                writeFrequency(source, frequency);
                if (entry.getValue()
                    .paused()
                    .contains(frequency)) source.setBoolean("paused", true);
                if (entry.getValue()
                    .entrances()
                    .contains(frequency)) source.setBoolean("entrance", true);
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
