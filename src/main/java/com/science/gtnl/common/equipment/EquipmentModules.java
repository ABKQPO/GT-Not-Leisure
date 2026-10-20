package com.science.gtnl.common.equipment;

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
            int slot = entry.hasKey(SLOT) ? entry.getByte(SLOT) & 255 : i;
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
}
