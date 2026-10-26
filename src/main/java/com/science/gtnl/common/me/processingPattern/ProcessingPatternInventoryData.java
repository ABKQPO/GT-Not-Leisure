package com.science.gtnl.common.me.processingPattern;

import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;

import appeng.api.storage.StorageName;
import appeng.api.storage.data.IAEStack;
import appeng.tile.inventory.IAEStackInventory;
import appeng.tile.inventory.IIAEStackInventory;

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
        for (String key : stored.func_150296_c()) {
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
        for (String key : saved.func_150296_c()) {
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
        for (String key : current.func_150296_c()) {
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

    public record Resized(IAEStackInventory crafting, IAEStackInventory output, NBTTagCompound stored) {}

    // Slots of each side that are in use: the small side has a quarter of the large one.
    public static int visibleInputs(boolean inverted, int craftingSize) {
        return inverted ? craftingSize / 4 : craftingSize;
    }

    public static int visibleOutputs(boolean inverted, int outputSize) {
        return inverted ? outputSize : outputSize / 4;
    }

    // Puts back the overflow saved beside the inventory and returns the snapshot to preserve it with on the next save.
    public static NBTTagCompound rememberInventory(NBTTagCompound root, NBTTagCompound inventory, int craftingSize,
        int outputSize) {
        restoreSavedOverflow(root, inventory, "craftingGrid");
        restoreSavedOverflow(root, inventory, "outputList");
        boolean inverted = inventory.getBoolean("inverted");
        return snapshotInventories(
            inventory,
            visibleInputs(inverted, craftingSize),
            visibleOutputs(inverted, outputSize));
    }

    // Writes what AE2 can read itself into the inventory and the rest beside it; returns the new snapshot.
    public static NBTTagCompound preserveInventory(NBTTagCompound root, NBTTagCompound inventory, NBTTagCompound stored,
        boolean inverted, int craftingSize, int outputSize) {
        preserveOverflow(inventory, stored, "craftingGrid", visibleInputs(inverted, craftingSize));
        preserveOverflow(inventory, stored, "outputList", visibleOutputs(inverted, outputSize));
        NBTTagCompound snapshot = snapshotInventories(
            inventory,
            visibleInputs(inverted, craftingSize),
            visibleOutputs(inverted, outputSize));
        int nativeInputs = ProcessingPatternCapacity.DEFAULT_MULTIPLIER
            * (inverted ? ProcessingPatternCapacity.SMALL_SIDE_SLOTS_PER_PAGE
                : ProcessingPatternCapacity.SLOTS_PER_PAGE);
        int nativeOutputs = ProcessingPatternCapacity.DEFAULT_MULTIPLIER
            * (inverted ? ProcessingPatternCapacity.SLOTS_PER_PAGE
                : ProcessingPatternCapacity.SMALL_SIDE_SLOTS_PER_PAGE);
        saveOverflow(root, inventory, "craftingGrid", nativeInputs);
        saveOverflow(root, inventory, "outputList", nativeOutputs);
        limitSlots(inventory, "craftingGrid", nativeInputs);
        limitSlots(inventory, "outputList", nativeOutputs);
        return snapshot;
    }

    public static Resized resize(IAEStackInventory crafting, IAEStackInventory output, NBTTagCompound stored,
        boolean inverted, int size, IIAEStackInventory host) {
        NBTTagCompound data = new NBTTagCompound();
        crafting.writeToNBT(data, "craftingGrid");
        output.writeToNBT(data, "outputList");
        preserveOverflow(data, stored, "craftingGrid", visibleInputs(inverted, crafting.getSizeInventory()));
        preserveOverflow(data, stored, "outputList", visibleOutputs(inverted, output.getSizeInventory()));

        NBTTagCompound visible = (NBTTagCompound) data.copy();
        limitSlots(visible, "craftingGrid", visibleInputs(inverted, size));
        limitSlots(visible, "outputList", visibleOutputs(inverted, size));

        IAEStackInventory newCrafting = new IAEStackInventory(host, size, StorageName.CRAFTING_INPUT);
        IAEStackInventory newOutput = new IAEStackInventory(host, size, StorageName.CRAFTING_OUTPUT);
        newCrafting.readFromNBT(visible, "craftingGrid");
        newOutput.readFromNBT(visible, "outputList");
        return new Resized(
            newCrafting,
            newOutput,
            snapshotInventories(data, visibleInputs(inverted, size), visibleOutputs(inverted, size)));
    }

    public static int countStacks(IAEStack<?>[] stacks) {
        int count = 0;
        for (IAEStack<?> stack : stacks) {
            if (stack != null) {
                count++;
            }
        }
        return count;
    }

    // A pattern whose inputs fit the small side is shown inverted, with the outputs on the large side.
    public static boolean invertFor(IAEStack<?>[] inputs, IAEStack<?>[] outputs, int craftingSize) {
        int smallSideCapacity = craftingSize / 4;
        return countStacks(inputs) <= smallSideCapacity && countStacks(outputs) >= smallSideCapacity;
    }

    public static int viewRows(int count, int columns) {
        return Math.max(8, (count + columns - 1) / columns);
    }

    // Which NEI layout shows the pattern shorter: 1 the processing one, -1 the reversed one, 0 when both are as tall.
    public static int shorterViewLayout(int inputCount, int outputCount) {
        int normalHeight = Math.max(viewRows(inputCount, 4), viewRows(outputCount, 1));
        int reversedHeight = Math.max(viewRows(inputCount, 1), viewRows(outputCount, 4));
        return Integer.compare(reversedHeight, normalHeight);
    }
}
