package com.science.gtnl.mixins.late.bartwork;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.recipe.gregtech.MaterialProcessingRecipes;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.werkstoff_loaders.recipe.MultipleMetalLoader;

@Mixin(value = MultipleMetalLoader.class, remap = false)
public abstract class MixinMultipleMetalLoader {

    @Inject(method = "run(Lbartworks/system/material/Werkstoff;)V", at = @At("TAIL"), remap = false)
    private void injectSuperdensePlateRecipe(Werkstoff werkstoff, CallbackInfo ci) {
        MaterialProcessingRecipes.addSuperdensePlateRecipe(werkstoff);
    }
}
