package com.science.gtnl.utils;

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.github.bsideup.jabel.Desugar;

import appeng.api.storage.data.IAEItemStack;

@Desugar
public record SimpleItem(Item item, int meta, NBTTagCompound nbt) {

    public static final SimpleItem empty = new SimpleItem(null, 0, null);

    public static SimpleItem getInstance(ItemStack stack) {
        if (stack == null) return empty;
        return new SimpleItem(stack.getItem(), stack.getItemDamage(), stack.getTagCompound());
    }

    public static SimpleItem getInstance(IAEItemStack stack) {
        return getInstance(stack.getItemStack());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SimpleItem that = (SimpleItem) o;
        return meta == that.meta && Objects.equals(item, that.item) && Objects.equals(nbt, that.nbt);
    }

    @Override
    public int hashCode() {
        int result = item.hashCode();
        result = 31 * result + meta;
        result = 31 * result + (nbt != null ? nbt.hashCode() : 0);
        return result;
    }

    public ItemStack toItemStack() {
        if (this == empty || item == null) return null;
        ItemStack stack = new ItemStack(item, 1, meta);
        if (nbt != null) {
            stack.setTagCompound((NBTTagCompound) nbt.copy());
        }
        return stack;
    }

}
