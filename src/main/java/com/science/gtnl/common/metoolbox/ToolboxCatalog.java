package com.science.gtnl.common.metoolbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;

import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;

final class ToolboxCatalog {

    private final IMEMonitor<IAEItemStack> itemMonitor;
    private final IMEMonitor<IAEFluidStack> fluidMonitor;
    private final Map<IAEStack<?>, Integer> ids = new HashMap<>();
    private final Map<Integer, IAEStack<?>> identities = new HashMap<>();
    private final Set<IAEStack<?>> dirty = new HashSet<>();
    private int nextId;
    private boolean active = true;
    private boolean fullUpdate = true;
    long revision;

    ToolboxCatalog(IMEMonitor<IAEItemStack> itemMonitor, IMEMonitor<IAEFluidStack> fluidMonitor) {
        this.itemMonitor = itemMonitor;
        this.fluidMonitor = fluidMonitor;
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

    boolean hasChanges() {
        return fullUpdate || !dirty.isEmpty();
    }

    List<NBTTagCompound> collect() {
        List<NBTTagCompound> entries = new ArrayList<>();

        if (fullUpdate) {
            for (IAEItemStack stack : itemMonitor.getStorageList()) entries.add(entry(stack));
            if (fluidMonitor != null) {
                for (IAEFluidStack stack : fluidMonitor.getStorageList()) entries.add(entry(stack));
            }
        } else {
            for (IAEStack<?> identity : dirty) {
                IAEStack<?> stack = current(identity);
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

    private NBTTagCompound entry(IAEStack<?> stack) {
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

    void changed(IAEStack<?> stack) {
        if (active && (stack instanceof IAEItemStack || stack instanceof IAEFluidStack)) dirty.add(stack.copy());
    }

    void onListUpdate() {
        if (active) fullUpdate = true;
    }
}
