package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.recipe.gregtech.FluidSolidifierDustRecipes;

import gtPlusPlus.core.material.Material;
import gtPlusPlus.xmod.gregtech.loaders.RecipeGenFluids;

@Mixin(value = RecipeGenFluids.class, remap = false)
public abstract class MixinRecipeGenFluids {

    @Inject(
        method = "generateRecipes",
        at = @At(
            value = "INVOKE",
            target = "LgtPlusPlus/core/material/Material;getPlate(I)Lnet/minecraft/item/ItemStack;",
            shift = At.Shift.BEFORE,
            ordinal = 0))
    private void injectBeforeGetPlate(Material material, CallbackInfo ci) {
        FluidSolidifierDustRecipes.add(material);
    }
}
