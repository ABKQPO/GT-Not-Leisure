package com.science.gtnl.mixins.late.notEnoughItems;

import java.util.List;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import codechicken.nei.ItemList;

@Mixin(value = ItemList.class, remap = false)
public interface AccessorItemList {

    @Accessor("items")
    static List<ItemStack> getItems() {
        throw new AssertionError();
    }
}
