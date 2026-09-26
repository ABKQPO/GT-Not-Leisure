package com.science.gtnl.utils.appliedEnergistics.processingPattern;

import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;

import appeng.tile.inventory.IAEStackInventory;

public class ProcessingPatternInventoryData {

    private static final String OVERFLOW_TAG = "gtnlProcessingPatternOverflow";

    public static void clearHiddenSlots(IAEStackInventory inventory) {
        for (int index = inventory.getSizeInventory() / 4; index < inventory.getSizeInventory(); index++) {
            if (inventory.getAEStackInSlot(index) != null) {
                inventory.putAEStackInSlot(index, null);
            }
        }
    }

    public static NBTTagCompound snapshotInventories(NBTTagCompound source, int inputs, int outputs) {
        NBTTagCompound snapshot = new NBTTagCompound();
        preserveOverflow(snapshot, source, "craftingGrid", inputs);
        preserveOverflow(snapshot, source, "outputList", outputs);
        return snapshot;
    }

    public static int nativePage(int page) {
        return Math.max(0, Math.min(ProcessingPatternCapacity.DEFAULT_MULTIPLIER - 1, page));
    }

    public static void clearSavedOverflow(NBTTagCompound root) {
        root.removeTag(OVERFLOW_TAG);
    }

    public static void limitSlots(NBTTagCompound inventory, String name, int capacity) {
        NBTTagCompound slots = inventory.getCompoundTag(name);
        Iterator<?> keys = slots.func_150296_c()
            .iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (!key.startsWith("#")) {
                continue;
            }
            try {
                if (Integer.parseInt(key.substring(1)) >= capacity) {
                    keys.remove();
                }
            } catch (NumberFormatException ignored) {
                // Ignore tags that do not represent inventory slots.
            }
        }
    }

    public static void preserveOverflow(NBTTagCompound destination, NBTTagCompound previous, String name,
        int capacity) {
        if (previous == null || !previous.hasKey(name, 10)) {
            return;
        }

        NBTTagCompound stored = previous.getCompoundTag(name);
        NBTTagCompound current = destination.getCompoundTag(name);
        for (Object keyObject : stored.func_150296_c()) {
            String key = (String) keyObject;
            if (!key.startsWith("#")) {
                continue;
            }
            try {
                if (Integer.parseInt(key.substring(1)) >= capacity && !current.hasKey(key)) {
                    current.setTag(
                        key,
                        stored.getTag(key)
                            .copy());
                }
            } catch (NumberFormatException ignored) {
                // Ignore tags that do not represent inventory slots.
            }
        }
        if (!current.hasNoTags()) {
            destination.setTag(name, current);
        }
    }

    public static void restoreSavedOverflow(NBTTagCompound root, NBTTagCompound inventory, String name) {
        if (!root.hasKey(OVERFLOW_TAG, 10)) {
            return;
        }
        NBTTagCompound saved = root.getCompoundTag(OVERFLOW_TAG)
            .getCompoundTag(name);
        NBTTagCompound current = inventory.getCompoundTag(name);
        for (Object keyObject : saved.func_150296_c()) {
            String key = (String) keyObject;
            if (!current.hasKey(key)) {
                current.setTag(
                    key,
                    saved.getTag(key)
                        .copy());
            }
        }
        if (!current.hasNoTags()) {
            inventory.setTag(name, current);
        }
    }

    public static void saveOverflow(NBTTagCompound root, NBTTagCompound inventory, String name, int nativeCapacity) {
        NBTTagCompound current = inventory.getCompoundTag(name);
        NBTTagCompound saved = new NBTTagCompound();
        for (Object keyObject : current.func_150296_c()) {
            String key = (String) keyObject;
            if (!key.startsWith("#")) {
                continue;
            }
            try {
                if (Integer.parseInt(key.substring(1)) >= nativeCapacity) {
                    saved.setTag(
                        key,
                        current.getTag(key)
                            .copy());
                }
            } catch (NumberFormatException ignored) {
                // Ignore tags that do not represent inventory slots.
            }
        }
        NBTTagCompound overflow = root.getCompoundTag(OVERFLOW_TAG);
        if (saved.hasNoTags()) {
            overflow.removeTag(name);
        } else {
            overflow.setTag(name, saved);
        }
        if (overflow.hasNoTags()) {
            root.removeTag(OVERFLOW_TAG);
        } else {
            root.setTag(OVERFLOW_TAG, overflow);
        }
    }
}
