package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.science.gtnl.common.recipe.AssLineRecipeHook;
import com.science.gtnl.config.RecipeConfig;

import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;

@Mixin(value = GTRecipeConstants.class, remap = false)
public abstract class MixinGTRecipeConstants {

    @ModifyVariable(method = "lambda$static$3", at = @At(value = "STORE"), ordinal = 0)
    private static GTRecipe.GTRecipe_WithAlt modifyAssemblyLineRecipe(GTRecipe.GTRecipe_WithAlt r) {
        if (RecipeConfig.enableAssemblingLineRecipesTimeChange) {
            r.mDuration = AssLineRecipeHook.shortenedDuration(r.mDuration);
        }
        return r;
    }
}
