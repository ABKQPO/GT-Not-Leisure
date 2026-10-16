package com.science.gtnl.common.railgun;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.science.gtnl.common.item.items.ElectromagneticRailgun;

public final class RailgunData {

    public static final int MODULE_SLOTS = 8;

    private RailgunData() {}

    public static NBTTagCompound tag(ItemStack stack) {
        if (stack.getTagCompound() == null) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    public static RailgunSettings settings(ItemStack stack) {
        return RailgunSettings.read(
            stack.hasTagCompound() ? stack.getTagCompound()
                .getCompoundTag("Settings") : new NBTTagCompound());
    }

    public static void setSetting(ItemStack stack, String key, boolean value) {
        NBTTagCompound settings = settings(stack).write();
        settings.setBoolean(key, value);
        tag(stack).setTag("Settings", settings);
        updateMode(stack);
    }

    public static void updateMode(ItemStack stack) {
        tag(stack).setBoolean(
            ElectromagneticRailgun.NBT_EXTREME_HIGH_VOLTAGE,
            settings(stack).ehvBeam() && modules(stack).count(RailgunModuleType.EHV_BEAM) > 0);
    }

    public static ItemStack[] moduleStacks(ItemStack stack) {
        ItemStack[] slots = new ItemStack[MODULE_SLOTS];
        if (!stack.hasTagCompound()) return slots;
        NBTTagList list = stack.getTagCompound()
            .getTagList("Modules", 10);
        for (int i = 0; i < Math.min(list.tagCount(), MODULE_SLOTS); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.hasKey("Slot") ? tag.getByte("Slot") & 255 : i;
            if (slot < MODULE_SLOTS) slots[slot] = ItemStack.loadItemStackFromNBT(tag);
        }
        return slots;
    }

    public static void setModuleStacks(ItemStack stack, ItemStack[] slots) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < Math.min(slots.length, MODULE_SLOTS); i++) {
            if (slots[i] == null) continue;
            NBTTagCompound module = new NBTTagCompound();
            ItemStack single = slots[i].copy();
            single.stackSize = 1;
            single.writeToNBT(module);
            module.setByte("Slot", (byte) i);
            list.appendTag(module);
        }
        tag(stack).setTag("Modules", list);
        updateMode(stack);
    }

    public static Modules modules(ItemStack stack) {
        Map<RailgunModuleType, Integer> counts = new EnumMap<>(RailgunModuleType.class);
        boolean energy = false;
        for (ItemStack module : moduleStacks(stack)) {
            RailgunModuleType type = RailgunModuleType.of(module);
            if (type == null || type.energy() && energy) continue;
            counts.put(type, Math.min(type.limit, counts.getOrDefault(type, 0) + 1));
            energy |= type.energy();
        }
        return new Modules(counts);
    }

    public record Modules(Map<RailgunModuleType, Integer> counts) {

        public int count(RailgunModuleType type) {
            return counts.getOrDefault(type, 0);
        }
    }
}
