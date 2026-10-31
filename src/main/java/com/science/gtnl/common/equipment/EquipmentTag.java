package com.science.gtnl.common.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public class EquipmentTag {

    public static NBTTagCompound child(ItemStack stack, String key) {
        NBTTagCompound tag = stack == null ? null : stack.getTagCompound();
        return tag != null && tag.hasKey(key, Constants.NBT.TAG_COMPOUND) ? tag.getCompoundTag(key)
            : new NBTTagCompound();
    }

    public static NBTTagCompound childForWrite(ItemStack stack, String key) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey(key, Constants.NBT.TAG_COMPOUND)) tag.setTag(key, new NBTTagCompound());
        return tag.getCompoundTag(key);
    }
}
