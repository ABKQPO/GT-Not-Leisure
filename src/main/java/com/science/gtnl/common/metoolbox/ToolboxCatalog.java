package com.science.gtnl.common.metoolbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

final class ToolboxCatalog {

    private static final int MIN_UPDATE_TICKS = 5;
    private static final int MIN_SCAN_TICKS = 20;

    private final IMEMonitor<IAEItemStack> itemMonitor;
    private final IMEMonitor<IAEFluidStack> fluidMonitor;
    private final Object2IntOpenHashMap<IAEStack<?>> ids = new Object2IntOpenHashMap<>();
    private final Int2ObjectOpenHashMap<IAEStack<?>> identities = new Int2ObjectOpenHashMap<>();
    private final Set<IAEStack<?>> dirty = new ObjectOpenHashSet<>();
    private long nextId;
    private boolean active = true;
    private boolean fullUpdate = true;
    private boolean listUpdate;
    private int lastUpdate;
    private int lastScan;
    long revision;

    ToolboxCatalog(IMEMonitor<IAEItemStack> itemMonitor, IMEMonitor<IAEFluidStack> fluidMonitor) {
        this.itemMonitor = itemMonitor;
        this.fluidMonitor = fluidMonitor;
        ids.defaultReturnValue(-1);
    }

    IAEStack<?> target(int id) {
        IAEStack<?> stack = identities.get(id);
        return stack == null ? null : stack.copy();
    }

    IAEStack<?> current(IAEStack<?> identity) {
        if (identity instanceof IAEItemStack item) return itemMonitor.getStorageList()
            .findPrecise(item);
        if (identity instanceof IAEFluidStack fluid && fluidMonitor != null) return fluidMonitor.getStorageList()
            .findPrecise(fluid);
        return null;
    }

    void requestSnapshot() {
        fullUpdate = true;
    }

    boolean needsSnapshot() {
        return fullUpdate;
    }

    boolean canCollect(int tick) {
        if (!active || !fullUpdate && !listUpdate && dirty.isEmpty()) return false;
        if (revision == 0) return true;
        if (!elapsed(tick, lastUpdate, MIN_UPDATE_TICKS)) return false;
        if (fullUpdate) return elapsed(tick, lastScan, MIN_SCAN_TICKS);
        return !dirty.isEmpty() || listUpdate && elapsed(tick, lastScan, MIN_SCAN_TICKS);
    }

    private static boolean elapsed(int tick, int previous, int interval) {
        return tick < previous || tick - previous >= interval;
    }

    List<NBTTagCompound> collect(int tick) throws IOException {
        List<NBTTagCompound> entries = new ArrayList<>();

        if (fullUpdate || listUpdate && elapsed(tick, lastScan, MIN_SCAN_TICKS)) {
            Map<IAEStack<?>, IAEStack<?>> available = new Object2ObjectOpenHashMap<>();
            for (IAEItemStack stack : itemMonitor.getStorageList()) addAvailable(available, stack);
            if (fluidMonitor != null) {
                for (IAEFluidStack stack : fluidMonitor.getStorageList()) addAvailable(available, stack);
            }
            Iterator<Int2ObjectMap.Entry<IAEStack<?>>> previous = identities.int2ObjectEntrySet()
                .iterator();
            while (previous.hasNext()) {
                Int2ObjectMap.Entry<IAEStack<?>> entry = previous.next();
                if (available.containsKey(entry.getValue())) continue;
                if (!fullUpdate) addEntry(
                    entries,
                    entry.getIntKey(),
                    entry.getValue()
                        .copy()
                        .reset());
                ids.removeInt(entry.getValue());
                previous.remove();
            }
            for (IAEStack<?> stack : available.values()) update(entries, stack);
            listUpdate = false;
            lastScan = tick;
        } else {
            List<IAEStack<?>> updates = new ArrayList<>();
            for (IAEStack<?> identity : dirty) {
                IAEStack<?> stack = current(identity);
                if (stack == null || !visible(stack)) update(
                    entries,
                    identity.copy()
                        .reset());
                else updates.add(stack);
            }
            for (IAEStack<?> stack : updates) update(entries, stack);
        }
        dirty.clear();
        if (fullUpdate || !entries.isEmpty()) revision++;
        fullUpdate = false;
        lastUpdate = tick;
        return entries;
    }

    private static boolean visible(IAEStack<?> stack) {
        return stack.getStackSize() != 0 || stack.isCraftable();
    }

    private static void addAvailable(Map<IAEStack<?>, IAEStack<?>> available, IAEStack<?> stack) throws IOException {
        if (!visible(stack)) return;
        if (available.size() >= ToolboxUpdatePacket.MAX_UPDATE_ENTRIES && !available.containsKey(stack))
            throw new IOException("ME toolbox catalog exceeds the entry limit");
        available.put(stack, stack);
    }

    private void update(List<NBTTagCompound> entries, IAEStack<?> stack) throws IOException {
        int id = ids.getInt(stack);
        if (!visible(stack)) {
            if (id >= 0) {
                addEntry(
                    entries,
                    id,
                    stack.copy()
                        .reset());
                ids.removeInt(stack);
                identities.remove(id);
            }
            return;
        }
        if (id < 0) {
            if (ids.size() >= ToolboxUpdatePacket.MAX_UPDATE_ENTRIES || nextId > Integer.MAX_VALUE)
                throw new IOException("ME toolbox catalog exceeds the identity limit");
            // Never reuse an ID during a session, including after a snapshot: queued older clicks stay safe.
            id = (int) nextId++;
            ids.put(stack.copy(), id);
        } else if (!fullUpdate && sameState(identities.get(id), stack)) {
            return;
        }
        addEntry(entries, id, stack);
        identities.put(id, stack.copy());
    }

    private static boolean sameState(IAEStack<?> previous, IAEStack<?> current) {
        return previous.getStackSize() == current.getStackSize() && previous.isCraftable() == current.isCraftable()
            && previous.getCountRequestable() == current.getCountRequestable()
            && previous.getCountRequestableCrafts() == current.getCountRequestableCrafts()
            && Float.compare(previous.getUsedPercent(), current.getUsedPercent()) == 0;
    }

    private static void addEntry(List<NBTTagCompound> entries, int id, IAEStack<?> stack) throws IOException {
        if (entries.size() >= ToolboxUpdatePacket.MAX_UPDATE_ENTRIES)
            throw new IOException("ME toolbox update exceeds the entry limit");
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("id", id);
        tag.setTag("stack", stack.toNBTGeneric());
        entries.add(tag);
    }

    void close() {
        if (!active) return;
        active = false;
        dirty.clear();
        ids.clear();
        identities.clear();
    }

    void changed(IAEStack<?> stack) {
        if (!active || !(stack instanceof IAEItemStack || stack instanceof IAEFluidStack)) return;
        if (dirty.contains(stack)) return;
        if (dirty.size() >= ToolboxUpdatePacket.MAX_UPDATE_ENTRIES) {
            // A bounded scan covers discarded notifications, including identities already removed from storage.
            dirty.clear();
            listUpdate = true;
        }
        dirty.add(stack.copy());
    }

    void onListUpdate() {
        if (active) listUpdate = true;
    }
}
