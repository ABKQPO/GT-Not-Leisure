package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.science.gtnl.config.MainConfig;

import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;

@Mixin(value = GTRecipeConstants.class, remap = false)
public abstract class MixinGTRecipeConstants {

    @ModifyVariable(method = "lambda$static$6", at = @At(value = "STORE"), ordinal = 0)
    private static GTRecipe.GTRecipe_WithAlt modifyAssemblyLineRecipe(GTRecipe.GTRecipe_WithAlt original) {
        int assDuration = original.mDuration;
        if (!MainConfig.enableAssemblingLineRecipesTimeChange) return original;
        int modified = assDuration;

        if (assDuration >= 200000) {
            modified /= 100;
        } else if (assDuration >= 40000) {
            modified /= 10;
        } else if (assDuration >= 10000) {
            modified /= 4;
        } else if (assDuration >= 4000) {
            modified /= 2;
        }

        original.mDuration = modified;

        return original;
    }
}
