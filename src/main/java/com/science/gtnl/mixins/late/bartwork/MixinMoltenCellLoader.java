package com.science.gtnl.mixins.late.bartwork;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.recipe.gregtech.MaterialProcessingRecipes;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.werkstoff_loaders.recipe.MoltenCellLoader;

@Mixin(value = MoltenCellLoader.class, remap = false)
public abstract class MixinMoltenCellLoader {

    @Inject(
        method = "run",
        at = @At(
            value = "INVOKE",
            target = "Lbartworks/system/material/Werkstoff$GenerationFeatures;hasMetalCraftingSolidifierRecipes()Z",
            shift = At.Shift.AFTER,
            ordinal = 0))
    private void injectAfterHasMetalCrafting(Werkstoff werkstoff, CallbackInfo ci) {
        MaterialProcessingRecipes.addDustSolidifierRecipe(werkstoff);
    }
}
