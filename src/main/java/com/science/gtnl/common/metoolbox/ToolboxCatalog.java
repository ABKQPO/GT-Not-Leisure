package com.science.gtnl.common.metoolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

final class ToolboxCatalog {

    private final IMEMonitor<IAEItemStack> monitor;
    private final Map<IAEItemStack, Integer> ids = new HashMap<>();
    private final Map<Integer, IAEItemStack> identities = new HashMap<>();
    private final Set<IAEItemStack> dirty = new HashSet<>();
    private int nextId;
    private boolean active = true;
    private boolean fullUpdate = true;
    long revision;

    ToolboxCatalog(IMEMonitor<IAEItemStack> monitor) {
        this.monitor = monitor;
    }

    IAEItemStack target(int id) {
        IAEItemStack stack = identities.get(id);
        return stack == null ? null : stack.copy();
    }

    void requestSnapshot() {
        fullUpdate = true;
    }

    boolean needsSnapshot() {
        return fullUpdate;
    }

    boolean hasChanges() {
        return fullUpdate || !dirty.isEmpty();
    }

    List<NBTTagCompound> collect() {
        List<NBTTagCompound> entries = new ArrayList<>();

        IItemList<IAEItemStack> available = monitor.getStorageList();
        if (fullUpdate) {
            for (IAEItemStack stack : available) entries.add(entry(stack));
        } else {
            for (IAEItemStack identity : dirty) {
                IAEItemStack stack = available.findPrecise(identity);
                if (stack == null) {
                    stack = identity.copy();
                    stack.reset();
                }
                entries.add(entry(stack));
            }
        }
        dirty.clear();
        fullUpdate = false;
        revision++;
        return entries;
    }

    private NBTTagCompound entry(IAEItemStack stack) {
        int id = ids.computeIfAbsent(stack.copy(), identity -> {
            int value = nextId++;
            identities.put(value, identity);
            return value;
        });
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("id", id);
        tag.setTag("stack", stack.toNBTGeneric());
        return tag;
    }

    void close() {
        if (!active) return;
        active = false;
        dirty.clear();
    }

    void changed(IAEItemStack stack) {
        if (active) dirty.add(stack.copy());
    }

    void onListUpdate() {
        if (active) fullUpdate = true;
    }
}
