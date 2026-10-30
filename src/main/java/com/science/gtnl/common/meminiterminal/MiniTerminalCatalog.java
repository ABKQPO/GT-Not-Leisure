package com.science.gtnl.common.meminiterminal;

import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.science.gtnl.common.packet.MiniTerminalUpdatePacket.Entry;

import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.AEStackTypeRegistry;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IAEStackType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class MiniTerminalCatalog {

    private static final int MIN_UPDATE_TICKS = 5;
    private static final int MIN_SCAN_TICKS = 20;
    private static final int MAX_DIRTY = 32768;

    private final Map<IAEStackType<?>, IMEMonitor<?>> monitors;
    private final Object2IntOpenHashMap<IAEStack<?>> ids = new Object2IntOpenHashMap<>();
    private final Int2ObjectOpenHashMap<IAEStack<?>> identities = new Int2ObjectOpenHashMap<>();
    private final Set<IAEStack<?>> dirty = new ObjectOpenHashSet<>();
    private long nextId;
    private boolean active = true;
    private boolean suspended;
    private boolean closed;
    private boolean fullUpdate = true;
    private boolean listUpdate;
    private boolean immediateScan;
    private int lastUpdate;
    private int lastScan;
    final long epoch;
    long revision;

    MiniTerminalCatalog(Map<IAEStackType<?>, IMEMonitor<?>> monitors) {
        this.monitors = monitors;
        ids.defaultReturnValue(-1);
        long next;
        do next = ThreadLocalRandom.current()
            .nextLong();
        while (next == 0);
        epoch = next;
    }

    boolean suspend() {
        if (!active || revision == 0 || fullUpdate) return false;
        active = false;
        suspended = true;
        listUpdate = false;
        dirty.clear();
        return true;
    }

    boolean resume(Map<IAEStackType<?>, IMEMonitor<?>> monitors) {
        if (!suspended || !this.monitors.equals(monitors)) return false;
        suspended = false;
        active = true;
        listUpdate = true;
        immediateScan = true;
        return true;
    }

    IAEStack<?> target(int id) {
        IAEStack<?> stack = identities.get(id);
        return stack == null ? null : stack.copy();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    IAEStack<?> current(IAEStack<?> identity) {
        IMEMonitor monitor = identity == null ? null : monitors.get(identity.getStackType());
        return monitor == null ? null
            : (IAEStack<?>) monitor.getStorageList()
                .findPrecise(identity);
    }

    void requestSnapshot() {
        fullUpdate = true;
    }

    boolean needsSnapshot() {
        return fullUpdate;
    }

    boolean canCollect(int tick) {
        if (!active || !fullUpdate && !listUpdate && dirty.isEmpty()) return false;
        if (revision == 0 || immediateScan) return true;
        if (!elapsed(tick, lastUpdate, MIN_UPDATE_TICKS)) return false;
        if (fullUpdate) return elapsed(tick, lastScan, MIN_SCAN_TICKS);
        return !dirty.isEmpty() || listUpdate && elapsed(tick, lastScan, MIN_SCAN_TICKS);
    }

    private static boolean elapsed(int tick, int previous, int interval) {
        return tick < previous || tick - previous >= interval;
    }

    List<Entry> collect(int tick) throws IOException {
        List<Entry> entries = new ObjectArrayList<>();

        if (fullUpdate || listUpdate && (immediateScan || elapsed(tick, lastScan, MIN_SCAN_TICKS))) {
            BitSet seen = new BitSet((int) Math.min(nextId, Integer.MAX_VALUE));
            for (IAEStackType<?> type : AEStackTypeRegistry.getSortedTypes()) {
                IMEMonitor<?> monitor = monitors.get(type);
                if (monitor == null) continue;
                for (Object stack : monitor.getStorageList()) scan(entries, (IAEStack<?>) stack, seen);
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
            List<IAEStack<?>> updates = new ObjectArrayList<>();
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
        immediateScan = false;
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
        IAEStack<?> copy;
        if (id < 0) {
            if (nextId > Integer.MAX_VALUE)
                throw new IOException("ME mini portable terminal catalog exceeds the identity limit");
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
        if (closed) return;
        closed = true;
        active = false;
        suspended = false;
        dirty.clear();
        ids.clear();
        identities.clear();
    }

    void changed(IAEStack<?> stack) {
        if (!active || stack == null || !monitors.containsKey(stack.getStackType())) return;
        if (dirty.contains(stack)) return;
        if (dirty.size() >= MAX_DIRTY) {
            dirty.clear();
            listUpdate = true;
        }
        dirty.add(stack.copy());
    }

    void onListUpdate() {
        if (active) listUpdate = true;
    }
}
