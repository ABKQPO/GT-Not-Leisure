package com.science.gtnl.mixins.late.gregtech;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.recipe.gregtech.MaterialProcessingRecipes;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.loaders.oreprocessing.ProcessingDust;

@Mixin(value = ProcessingDust.class, remap = false)
public abstract class MixinProcessingDust {

    @Inject(method = "registerOre", at = @At(value = "HEAD"))
    private void injectBeforeGetPlate(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName,
        ItemStack aStack, CallbackInfo ci) {
        MaterialProcessingRecipes.addDustSolidifierRecipe(aPrefix, aMaterial);
    }
}
