package com.science.gtnl.common.wireless;

import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class WirelessFrequencyWorldSavedData extends WorldSavedData {

    public static final String DATA_NAME = "GTNL_WirelessFrequencies";
    private static WirelessFrequencyWorldSavedData instance;

    private final Map<UUID, Frequency> frequencies = new Object2ObjectOpenHashMap<>();
    private final Map<Address, UUID> legacy = new Object2ObjectOpenHashMap<>();

    public record Frequency(UUID creator, long epoch) {}

    public WirelessFrequencyWorldSavedData() {
        super(DATA_NAME);
    }

    public WirelessFrequencyWorldSavedData(String name) {
        super(name);
    }

    static WirelessFrequencyWorldSavedData get() {
        if (instance != null) return instance;
        World world = DimensionManager.getWorld(0);
        MapStorage storage = world == null ? null : world.mapStorage;
        if (storage == null) return null;
        instance = (WirelessFrequencyWorldSavedData) storage.loadData(WirelessFrequencyWorldSavedData.class, DATA_NAME);
        if (instance == null) {
            instance = new WirelessFrequencyWorldSavedData();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

    static void unload() {
        instance = null;
    }

    Frequency frequency(UUID id) {
        return frequencies.get(id);
    }

    long create(UUID id, UUID creator) {
        frequencies.put(id, new Frequency(creator, 0));
        markDirty();
        return 0;
    }

    boolean accept(UUID id, long epoch) {
        Frequency known = frequencies.get(id);
        if (known != null && epoch <= known.epoch()) return epoch == known.epoch();
        // A copy nobody has recorded yet, or one newer than the record, is the first evidence of this frequency.
        frequencies.put(id, new Frequency(known == null ? null : known.creator(), epoch));
        markDirty();
        return true;
    }

    long adopt(UUID id) {
        Frequency known = frequencies.get(id);
        long epoch = known == null ? 1 : known.epoch() + 1;
        frequencies.put(id, new Frequency(known == null ? null : known.creator(), epoch));
        markDirty();
        return epoch;
    }

    void setCreator(UUID id, UUID creator) {
        Frequency known = frequencies.get(id);
        frequencies.put(id, new Frequency(creator, known == null ? 0 : known.epoch()));
        markDirty();
    }

    UUID legacy(Address position) {
        return legacy.get(position);
    }

    boolean migrated(UUID id) {
        return legacy.containsValue(id);
    }

    void mapLegacy(Address position, UUID id) {
        if (id.equals(legacy.put(position, id))) return;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        frequencies.clear();
        legacy.clear();
        NBTTagList list = tag.getTagList("frequencies", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            UUID id = WirelessFrequencies.uuid(entry, "id");
            if (id != null)
                frequencies.put(id, new Frequency(WirelessFrequencies.uuid(entry, "creator"), entry.getLong("epoch")));
        }
        NBTTagList positions = tag.getTagList("legacy", 10);
        for (int i = 0; i < positions.tagCount(); i++) {
            NBTTagCompound entry = positions.getCompoundTagAt(i);
            Address position = WirelessLinkData.readPosition(entry);
            UUID id = WirelessFrequencies.uuid(entry, "id");
            if (position != null && id != null) legacy.put(position, id);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        frequencies.forEach((id, frequency) -> {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setString("id", id.toString());
            if (frequency.creator() != null) entry.setString(
                "creator",
                frequency.creator()
                    .toString());
            entry.setLong("epoch", frequency.epoch());
            list.appendTag(entry);
        });
        tag.setTag("frequencies", list);
        NBTTagList positions = new NBTTagList();
        legacy.forEach((position, id) -> {
            NBTTagCompound entry = WirelessLinkData.writePosition(new NBTTagCompound(), position);
            entry.setString("id", id.toString());
            positions.appendTag(entry);
        });
        tag.setTag("legacy", positions);
    }
}
