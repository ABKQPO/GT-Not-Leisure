package com.science.gtnl.mixins.late.NotEnoughItems;

import java.util.Map;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import codechicken.nei.ItemList;

@Mixin(value = ItemList.class, remap = false)
public interface AccessorItemList {

    @Accessor("ordering")
    static Map<ItemStack, Integer> getOrdering() {
        throw new AssertionError();
    }
}
