package com.science.gtnl.common.yogsothoth;

import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.util.Constants.NBT;

import com.science.gtnl.ScienceNotLeisure;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class YogTerminalStorage extends WorldSavedData {

    public static final String DATA_NAME = "GTNL_YogSothothTerminals";
    private static YogTerminalStorage instance;

    private final Map<UUID, YogRecord> records = new Object2ObjectOpenHashMap<>();

    public YogTerminalStorage() {
        super(DATA_NAME);
    }

    public YogTerminalStorage(String name) {
        super(name);
    }

    static void load(World world) {
        MapStorage storage = world.mapStorage;
        instance = (YogTerminalStorage) storage.loadData(YogTerminalStorage.class, DATA_NAME);
        if (instance == null) {
            instance = new YogTerminalStorage();
            storage.setData(DATA_NAME, instance);
        }
    }

    static void unload() {
        instance = null;
    }

    public static boolean loaded() {
        return instance != null;
    }

    static YogRecord get(UUID id) {
        return instance == null || id == null ? null : instance.records.get(id);
    }

    static YogRecord create(UUID owner) {
        UUID id = UUID.randomUUID();
        while (instance.records.containsKey(id)) id = UUID.randomUUID();
        YogRecord record = new YogRecord(id, owner);
        instance.records.put(id, record);
        instance.markDirty();
        return record;
    }

    static Iterable<YogRecord> all() {
        return instance == null ? java.util.Collections.emptyList() : instance.records.values();
    }

    static void markChanged() {
        if (instance != null) instance.markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        records.clear();
        NBTTagList list = tag.getTagList("records", NBT.TAG_COMPOUND);
        for (int index = 0; index < list.tagCount(); index++) {
            try {
                YogRecord record = YogRecord.read(list.getCompoundTagAt(index));
                records.put(record.id, record);
            } catch (IllegalArgumentException exception) {
                ScienceNotLeisure.LOG.error("Skipping malformed Yog-Sothoth terminal record", exception);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (YogRecord record : records.values()) list.appendTag(record.write());
        tag.setTag("records", list);
    }
}
