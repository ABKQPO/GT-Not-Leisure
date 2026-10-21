package com.science.gtnl.common.equipment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;

public class EquipmentModules {

    public static final int ENERGY_SLOTS = 1;
    private static final String SLOT = "Slot";

    private EquipmentModules() {}

    public static ItemStack[] read(NBTTagList list, int slots) {
        ItemStack[] stacks = new ItemStack[slots];
        List<ItemStack> overflow = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            if (!entry.hasKey(SLOT)) continue;
            int slot = entry.getByte(SLOT) & 255;
            ItemStack stack = ItemStack.loadItemStackFromNBT((NBTTagCompound) entry.copy());
            if (slot < slots && stacks[slot] == null) stacks[slot] = stack;
            else if (stack != null) overflow.add(stack);
        }
        List<ItemStack> unplaced = new ArrayList<>();
        for (ItemStack extra : overflow) {
            int free = Arrays.asList(stacks)
                .indexOf(null);
            if (free >= 0) stacks[free] = extra;
            else unplaced.add(extra);
        }
        if (unplaced.isEmpty()) return stacks;
        ItemStack[] grown = Arrays.copyOf(stacks, slots + unplaced.size());
        for (int i = 0; i < unplaced.size(); i++) grown[slots + i] = unplaced.get(i);
        return grown;
    }

    public static NBTTagList write(ItemStack[] stacks) {
        NBTTagList list = new NBTTagList();
        for (int slot = 0; slot < stacks.length; slot++) {
            if (stacks[slot] == null) continue;
            NBTTagCompound entry = stacks[slot].writeToNBT(new NBTTagCompound());
            entry.setByte(SLOT, (byte) slot);
            list.appendTag(entry);
        }
        return list;
    }

    public static int energyTier(ItemStack[] modules) {
        for (ItemStack module : modules) {
            int tier = EnergyModuleItem.tier(module);
            if (tier > 0) return tier;
        }
        return 0;
    }

    public static Storage storage(Function<ItemStack, NBTTagList> list, Function<ItemStack, ItemStack[]> read,
        BiConsumer<ItemStack, ItemStack[]> write) {
        return new Storage() {

            @Override
            public NBTTagList moduleList(ItemStack host) {
                return list.apply(host);
            }

            @Override
            public ItemStack[] moduleStacks(ItemStack host) {
                return read.apply(host);
            }

            @Override
            public void writeModuleStacks(ItemStack host, ItemStack[] modules) {
                write.accept(host, modules);
            }
        };
    }

    public interface Storage {

        /**
         * The NBT list the host keeps its modules in, used to notice changes made outside the GUI.
         */
        NBTTagList moduleList(ItemStack host);

        /**
         * Reads the module stacks of the host, one per slot.
         */
        ItemStack[] moduleStacks(ItemStack host);

        /**
         * Writes the module stacks back to the host.
         */
        void writeModuleStacks(ItemStack host, ItemStack[] modules);
    }
}
