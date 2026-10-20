package com.science.gtnl.common.equipment;

import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.science.gtnl.common.item.items.equipment.EnergyModuleItem;

public class EquipmentModules {

    private static final String SLOT = "Slot";

    private EquipmentModules() {}

    public static ItemStack[] read(NBTTagList list, int slots) {
        ItemStack[] stacks = new ItemStack[slots];
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            if (!entry.hasKey(SLOT)) continue;
            int slot = entry.getByte(SLOT) & 255;
            if (slot < slots) stacks[slot] = ItemStack.loadItemStackFromNBT((NBTTagCompound) entry.copy());
        }
        return stacks;
    }

    public static NBTTagList write(ItemStack[] stacks, int slots) {
        NBTTagList list = new NBTTagList();
        for (int slot = 0; slot < Math.min(slots, stacks.length); slot++) {
            if (stacks[slot] == null) continue;
            ItemStack single = stacks[slot].copy();
            single.stackSize = 1;
            NBTTagCompound entry = single.writeToNBT(new NBTTagCompound());
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
