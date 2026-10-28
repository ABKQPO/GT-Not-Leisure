package com.science.gtnl.common.wireless;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.common.wireless.WirelessChannelManager.Address;

import appeng.api.networking.IGridNode;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class WirelessFrequencies {

    public static final String KEY = IWirelessTileData.FREQUENCY_KEY;
    private static final int VERSION = 1;
    private static final int MAX_STRUCTURE_BLOCKS = 4096;
    private static final int CACHE_TICKS = 100;
    private static final Home NOWHERE = new Home(null, false), SPLIT = new Home(null, true);
    private static final Map<UUID, Set<TileEntity>> CARRIERS = new Object2ObjectOpenHashMap<>();
    private static final Map<TileEntity, Structure> STRUCTURES = new Reference2ObjectOpenHashMap<>();
    private static final Map<WirelessFrequency, Home> HOMES = new Object2ObjectOpenHashMap<>();
    private static final Map<Structure, WirelessFrequency> PRIMARIES = new Reference2ObjectOpenHashMap<>();
    private static int generation, cacheAge;

    public record Entry(UUID id, long epoch) {}

    public record Structure(List<TileEntity> blocks, Set<Address> positions, Address representative,
        Map<UUID, Entry> entries) {

        public boolean contains(Address position) {
            return positions.contains(block(position));
        }

        public UUID canonical() {
            return WirelessFrequencies.canonical(entries.keySet());
        }
    }

    record Home(Structure structure, boolean split) {}

    public static List<Entry> read(NBTTagCompound tileData) {
        List<Entry> result = new ObjectArrayList<>();
        if (!tileData.hasKey(KEY, 10)) return result;
        NBTTagCompound data = tileData.getCompoundTag(KEY);
        if (data.getInteger("version") != VERSION) return result;
        NBTTagList entries = data.getTagList("entries", 10);
        for (int i = 0; i < entries.tagCount(); i++) {
            NBTTagCompound entry = entries.getCompoundTagAt(i);
            UUID id = uuid(entry, "id");
            if (id != null) result.add(new Entry(id, entry.getLong("epoch")));
        }
        return result;
    }

    public static void write(NBTTagCompound tileData, Collection<Entry> entries) {
        if (entries.isEmpty()) {
            tileData.removeTag(KEY);
            return;
        }
        NBTTagList list = new NBTTagList();
        for (Entry entry : entries) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString(
                "id",
                entry.id()
                    .toString());
            tag.setLong("epoch", entry.epoch());
            list.appendTag(tag);
        }
        NBTTagCompound data = new NBTTagCompound();
        data.setInteger("version", VERSION);
        data.setTag("entries", list);
        tileData.setTag(KEY, data);
    }

    static UUID uuid(NBTTagCompound tag, String key) {
        if (!tag.hasKey(key, 8)) return null;
        try {
            return UUID.fromString(tag.getString(key));
        } catch (IllegalArgumentException invalid) {
            return null;
        }
    }

    static UUID canonical(Collection<UUID> ids) {
        UUID result = null;
        for (UUID id : ids) if (result == null || id.compareTo(result) < 0) result = id;
        return result;
    }

    static List<Entry> current(List<Entry> entries, WirelessFrequencyWorldSavedData registry) {
        if (registry == null) return entries;
        List<Entry> result = new ObjectArrayList<>(entries.size());
        for (Entry entry : entries) {
            var known = registry.frequency(entry.id());
            if (registry.accept(entry.id(), entry.epoch())) result.add(entry);
            if (registry.frequency(entry.id()) != known) invalidate();
        }
        return result;
    }

    public static void register(TileEntity tile) {
        World world = tile.getWorldObj();
        if (world == null || world.isRemote) return;
        for (Entry entry : entries(tile)) index(tile, entry);
        invalidate();
    }

    public static void controllerChanged(TileEntity tile) {
        if (!WirelessChannelManager.isSupportedController(tile)) return;
        World world = tile.getWorldObj();
        if (world != null && !world.isRemote) invalidate();
    }

    public static Structure structure(TileEntity start) {
        Structure cached = STRUCTURES.get(start);
        if (cached != null) return cached;
        World world = start.getWorldObj();
        List<TileEntity> blocks = new ObjectArrayList<>();
        Set<TileEntity> seen = new ReferenceOpenHashSet<>();
        ObjectArrayFIFOQueue<TileEntity> pending = new ObjectArrayFIFOQueue<>();
        seen.add(start);
        pending.enqueue(start);
        while (!pending.isEmpty() && blocks.size() < MAX_STRUCTURE_BLOCKS) {
            TileEntity block = pending.dequeue();
            blocks.add(block);
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                int x = block.xCoord + side.offsetX, y = block.yCoord + side.offsetY, z = block.zCoord + side.offsetZ;
                if (!world.blockExists(x, y, z)) continue;
                TileEntity next = world.getTileEntity(x, y, z);
                if (WirelessChannelManager.isSupportedController(next) && !next.isInvalid() && seen.add(next))
                    pending.enqueue(next);
            }
        }
        WirelessFrequencyWorldSavedData registry = WirelessFrequencyWorldSavedData.get();
        Map<UUID, Entry> entries = new Object2ObjectOpenHashMap<>();
        List<TileEntity> unmarked = new ObjectArrayList<>();
        for (TileEntity block : blocks) {
            List<Entry> own = entries(block);
            List<Entry> current = current(own, registry);
            // Superseded copies must go before inheritance, or a stale id would spread to new blocks.
            if (current.size() != own.size()) store(block, current);
            if (current.isEmpty()) unmarked.add(block);
            for (Entry entry : current) {
                entries.putIfAbsent(entry.id(), entry);
                index(block, entry);
            }
        }
        if (!entries.isEmpty()) {
            Entry inherited = entries.get(canonical(entries.keySet()));
            for (TileEntity block : unmarked) store(block, List.of(inherited));
        }
        Set<Address> positions = new ObjectOpenHashSet<>();
        for (TileEntity block : blocks) positions.add(WirelessLinkPersistence.address(block));
        Address representative = positions.stream()
            .min(Address.POSITION_ORDER)
            .orElseThrow();
        Structure structure = new Structure(
            List.copyOf(blocks),
            Set.copyOf(positions),
            representative,
            Map.copyOf(entries));
        for (TileEntity block : blocks) STRUCTURES.put(block, structure);
        return structure;
    }

    static Home home(WirelessFrequency frequency) {
        Home cached = HOMES.get(frequency);
        if (cached != null) return cached;
        int before = generation;
        Home home = frequency.isLegacy() ? legacyHome(frequency.legacy()) : find(frequency.id());
        // A lookup that invalidated the caches part-way may have seen both old and new structures.
        if (generation == before) HOMES.put(frequency, home);
        return home;
    }

    private static Home legacyHome(Address position) {
        WirelessFrequencyWorldSavedData registry = WirelessFrequencyWorldSavedData.get();
        UUID mapped = registry == null ? null : registry.legacy(position);
        if (mapped != null) return home(WirelessFrequency.of(mapped));
        TileEntity tile = position.tile();
        if (!WirelessChannelManager.isSupportedController(tile) || tile.isInvalid()) return NOWHERE;
        Structure structure = structure(tile);
        // An id somebody bound since the upgrade has an owner the old links never had; only reuse migrated ids.
        UUID id = registry == null ? structure.canonical() : migrated(structure, registry);
        if (id == null) {
            id = UUID.randomUUID();
            long epoch = registry == null ? 0 : registry.create(id, null);
            update(structure, new Entry(id, epoch));
        }
        if (registry != null) {
            registry.mapLegacy(position, id);
            invalidate();
        }
        return home(WirelessFrequency.of(id));
    }

    private static UUID migrated(Structure structure, WirelessFrequencyWorldSavedData registry) {
        UUID result = null;
        for (UUID id : structure.entries()
            .keySet()) {
            if (registry.migrated(id) && (result == null || id.compareTo(result) < 0)) result = id;
        }
        return result;
    }

    private static WirelessFrequency primary(Structure structure) {
        WirelessFrequency known = PRIMARIES.get(structure);
        if (known != null) return known;
        int before = generation;
        UUID result = null;
        for (UUID id : structure.entries()
            .keySet()) {
            Structure home = home(WirelessFrequency.of(id)).structure();
            if (home == null || !home.positions()
                .equals(structure.positions())) continue;
            if (result == null || id.compareTo(result) < 0) result = id;
        }
        if (result == null) return null;
        WirelessFrequency primary = WirelessFrequency.of(result);
        if (generation == before) PRIMARIES.put(structure, primary);
        return primary;
    }

    private static Home find(UUID id) {
        Set<TileEntity> carriers = CARRIERS.get(id);
        if (carriers == null) return NOWHERE;
        // Keyed by position: after a mid-lookup invalidation the same structure comes back as a new object.
        Map<Address, Structure> homes = new Object2ObjectOpenHashMap<>();
        // Resolving a structure can index newly inherited blocks into this very set.
        for (TileEntity tile : new ObjectArrayList<>(carriers)) {
            if (!WirelessLinkPersistence.loaded(tile) || !WirelessChannelManager.isSupportedController(tile)) {
                carriers.remove(tile);
                continue;
            }
            Structure structure = structure(tile);
            if (structure.entries()
                .containsKey(id)) homes.put(structure.representative(), structure);
            else carriers.remove(tile);
        }
        if (homes.size() == 1) return new Home(
            homes.values()
                .iterator()
                .next(),
            false);
        return homes.isEmpty() ? NOWHERE : SPLIT;
    }

    public static WirelessFrequency canonical(WirelessFrequency frequency) {
        Structure home = home(frequency).structure();
        return home == null ? frequency : primary(home);
    }

    public static boolean same(WirelessFrequency first, WirelessFrequency second) {
        return first.equals(second) || canonical(first).equals(canonical(second));
    }

    public static WirelessFrequency upgrade(WirelessFrequency frequency) {
        if (!frequency.isLegacy()) return frequency;
        WirelessFrequencyWorldSavedData registry = WirelessFrequencyWorldSavedData.get();
        if (registry == null) return frequency;
        if (registry.legacy(frequency.legacy()) == null) home(frequency);
        UUID id = registry.legacy(frequency.legacy());
        return id == null ? frequency : WirelessFrequency.of(id);
    }

    public static Address controller(WirelessFrequency frequency) {
        Structure home = home(frequency).structure();
        return home == null ? null : home.representative();
    }

    public static IGridNode controllerNode(WirelessFrequency frequency) {
        Address controller = controller(frequency);
        return controller == null || !WirelessChannelManager.isSupportedController(controller.tile()) ? null
            : controller.node();
    }

    public static boolean split(WirelessFrequency frequency) {
        return home(frequency).split();
    }

    static boolean hosts(WirelessFrequency frequency, Address position) {
        Structure home = home(frequency).structure();
        return home != null && home.contains(position);
    }

    public static WirelessFrequency bind(TileEntity controller, UUID creator) {
        Structure structure = structure(controller);
        WirelessFrequency primary = primary(structure);
        if (primary != null) return primary;
        UUID id = UUID.randomUUID();
        WirelessFrequencyWorldSavedData registry = WirelessFrequencyWorldSavedData.get();
        update(structure, new Entry(id, registry == null ? 0 : registry.create(id, creator)));
        return WirelessFrequency.of(id);
    }

    public static UUID creator(WirelessFrequency frequency) {
        WirelessFrequency known = upgrade(frequency);
        WirelessFrequencyWorldSavedData registry = WirelessFrequencyWorldSavedData.get();
        var record = known.isLegacy() || registry == null ? null : registry.frequency(known.id());
        return record == null ? null : record.creator();
    }

    public static boolean setCreator(WirelessFrequency frequency, UUID creator) {
        WirelessFrequency known = upgrade(frequency);
        WirelessFrequencyWorldSavedData registry = WirelessFrequencyWorldSavedData.get();
        if (known.isLegacy() || registry == null) return false;
        registry.setCreator(known.id(), creator);
        invalidate();
        return true;
    }

    public static Structure adopt(TileEntity controller, WirelessFrequency frequency) {
        WirelessFrequencyWorldSavedData registry = WirelessFrequencyWorldSavedData.get();
        if (frequency.isLegacy() || registry == null) return null;
        Entry entry = new Entry(frequency.id(), registry.adopt(frequency.id()));
        return update(structure(controller), entry);
    }

    private static Structure update(Structure structure, Entry entry) {
        for (TileEntity block : structure.blocks()) {
            List<Entry> own = new ObjectArrayList<>(entries(block));
            own.removeIf(
                existing -> existing.id()
                    .equals(entry.id()));
            own.add(entry);
            store(block, own);
        }
        invalidate();
        return structure(
            structure.blocks()
                .get(0));
    }

    private static List<Entry> entries(TileEntity tile) {
        return tile instanceof IWirelessTileData data && data.gtnl$hasFrequencyData() ? read(data.gtnl$wirelessData())
            : List.of();
    }

    private static void store(TileEntity tile, Collection<Entry> entries) {
        write(IWirelessTileData.of(tile), entries);
        tile.markDirty();
        for (Entry entry : entries) index(tile, entry);
    }

    private static void index(TileEntity tile, Entry entry) {
        CARRIERS.computeIfAbsent(entry.id(), ignored -> new ReferenceOpenHashSet<>())
            .add(tile);
    }

    private static Address block(Address position) {
        return new Address(position.dimension(), position.x(), position.y(), position.z(), ForgeDirection.UNKNOWN);
    }

    static void forget(Predicate<Address> removed) {
        Predicate<TileEntity> gone = tile -> tile.getWorldObj() != null
            && removed.test(WirelessLinkPersistence.address(tile));
        CARRIERS.values()
            .removeIf(carriers -> carriers.removeIf(gone) && carriers.isEmpty());
        invalidate();
    }

    static void invalidate() {
        STRUCTURES.clear();
        HOMES.clear();
        PRIMARIES.clear();
        generation++;
        cacheAge = 0;
    }

    static void tick() {
        if (++cacheAge >= CACHE_TICKS) invalidate();
    }

    static void clear() {
        CARRIERS.clear();
        invalidate();
        WirelessFrequencyWorldSavedData.unload();
    }
}
