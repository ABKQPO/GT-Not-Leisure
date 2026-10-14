package com.science.gtnl.common.meminiterminal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.science.gtnl.common.meminiterminal.MiniTerminalUpdatePacket.Entry;

import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

final class MiniTerminalCatalog {

    private static final int MIN_UPDATE_TICKS = 5;
    private static final int MIN_SCAN_TICKS = 20;
    private static final int MAX_DIRTY = 4096;

    private final IMEMonitor<IAEItemStack> itemMonitor;
    private final IMEMonitor<IAEFluidStack> fluidMonitor;
    private final Object2IntOpenHashMap<IAEStack<?>> ids = new Object2IntOpenHashMap<>();
    private final Int2ObjectOpenHashMap<IAEStack<?>> identities = new Int2ObjectOpenHashMap<>();
    private final Set<IAEStack<?>> dirty = new ObjectOpenHashSet<>();
    private long nextId;
    private boolean active = true;
    private boolean fluidsVisible;
    private boolean fullUpdate = true;
    private boolean listUpdate;
    private int lastUpdate;
    private int lastScan;
    long revision;

    MiniTerminalCatalog(IMEMonitor<IAEItemStack> itemMonitor, IMEMonitor<IAEFluidStack> fluidMonitor,
        boolean fluidsVisible) {
        this.itemMonitor = itemMonitor;
        this.fluidMonitor = fluidMonitor;
        this.fluidsVisible = fluidsVisible;
        ids.defaultReturnValue(-1);
    }

    void showFluids(boolean visible) {
        if (fluidsVisible == visible) return;
        fluidsVisible = visible;
        requestSnapshot();
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

    List<Entry> collect(int tick) throws IOException {
        List<Entry> entries = new ArrayList<>();

        if (fullUpdate || listUpdate && elapsed(tick, lastScan, MIN_SCAN_TICKS)) {
            // IDs are allocated densely, so a bit set marks survivors without hashing every stack a second time.
            BitSet seen = new BitSet((int) Math.min(nextId, Integer.MAX_VALUE));
            for (IAEItemStack stack : itemMonitor.getStorageList()) scan(entries, stack, seen);
            if (fluidsVisible && fluidMonitor != null) {
                for (IAEFluidStack stack : fluidMonitor.getStorageList()) scan(entries, stack, seen);
            }
            Iterator<Int2ObjectMap.Entry<IAEStack<?>>> previous = identities.int2ObjectEntrySet()
                .iterator();
            while (previous.hasNext()) {
                Int2ObjectMap.Entry<IAEStack<?>> entry = previous.next();
                if (seen.get(entry.getIntKey())) continue;
                if (!fullUpdate) entries.add(
                    new Entry(
                        entry.getIntKey(),
                        entry.getValue()
                            .copy()
                            .reset()));
                ids.removeInt(entry.getValue());
                previous.remove();
            }
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

    private void scan(List<Entry> entries, IAEStack<?> stack, BitSet seen) throws IOException {
        if (!visible(stack)) return;
        int id = update(entries, stack);
        if (id >= 0) seen.set(id);
    }

    /** Returns the stack's catalog ID, or -1 once it is no longer visible. */
    private int update(List<Entry> entries, IAEStack<?> stack) throws IOException {
        int id = ids.getInt(stack);
        if (!visible(stack)) {
            if (id >= 0) {
                entries.add(
                    new Entry(
                        id,
                        stack.copy()
                            .reset()));
                ids.removeInt(stack);
                identities.remove(id);
            }
            return -1;
        }
        // One copy serves as the ID key, the remembered state and the outgoing entry; none of them mutate it.
        IAEStack<?> copy;
        if (id < 0) {
            if (nextId > Integer.MAX_VALUE)
                throw new IOException("ME mini portable terminal catalog exceeds the identity limit");
            // Never reuse an ID during a session, including after a snapshot: queued older clicks stay safe.
            id = (int) nextId++;
            copy = stack.copy();
            ids.put(copy, id);
        } else if (!fullUpdate && sameState(identities.get(id), stack)) {
            return id;
        } else {
            copy = stack.copy();
        }
        entries.add(new Entry(id, copy));
        identities.put(id, copy);
        return id;
    }

    private static boolean sameState(IAEStack<?> previous, IAEStack<?> current) {
        return previous.getStackSize() == current.getStackSize() && previous.isCraftable() == current.isCraftable()
            && previous.getCountRequestable() == current.getCountRequestable()
            && previous.getCountRequestableCrafts() == current.getCountRequestableCrafts()
            && Float.compare(previous.getUsedPercent(), current.getUsedPercent()) == 0;
    }

    void close() {
        if (!active) return;
        active = false;
        dirty.clear();
        ids.clear();
        identities.clear();
    }

    void changed(IAEStack<?> stack) {
        if (!active || !(stack instanceof IAEItemStack || fluidsVisible && stack instanceof IAEFluidStack)) return;
        if (dirty.contains(stack)) return;
        if (dirty.size() >= MAX_DIRTY) {
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
