package com.science.gtnl.common.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class EquipmentTag {

    private EquipmentTag() {}

    public static NBTTagCompound child(ItemStack stack, String key) {
        NBTTagCompound tag = stack == null ? null : stack.getTagCompound();
        return tag != null && tag.hasKey(key, 10) ? tag.getCompoundTag(key) : new NBTTagCompound();
    }

    public static NBTTagCompound childForWrite(ItemStack stack, String key) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey(key, 10)) tag.setTag(key, new NBTTagCompound());
        return tag.getCompoundTag(key);
    }
}
