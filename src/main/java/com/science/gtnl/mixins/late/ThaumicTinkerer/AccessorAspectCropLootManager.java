package com.science.gtnl.mixins.late.ThaumicTinkerer;

import java.util.HashMap;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import thaumcraft.api.aspects.Aspect;
import thaumic.tinkerer.common.core.helper.AspectCropLootManager;

@Mixin(AspectCropLootManager.class)
public interface AccessorAspectCropLootManager {

    @Accessor("lootMap")
    static HashMap<Aspect, HashMap<ItemStack, Integer>> getLootMap() {
        throw new UnsupportedOperationException("Mixin Accessor");
    }
}
