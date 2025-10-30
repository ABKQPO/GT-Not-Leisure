package com.science.gtnl.mixins.late.TecTech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.science.gtnl.config.MainConfig;

import tectech.recipe.TTRecipeAdder;

@Mixin(value = TTRecipeAdder.class, remap = false)
public abstract class MixinTTRecipeAdder {

    @ModifyVariable(
        method = "addResearchableAssemblylineRecipe(Lnet/minecraft/item/ItemStack;IIII[Lnet/minecraft/item/ItemStack;[Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/item/ItemStack;II)Z",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 4)
    private static int modifyAssemblyLineDuration(int assDuration) {
        if (!MainConfig.enableAssemblingLineRecipesTimeChange) return assDuration;
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

        return modified;
    }

    @ModifyVariable(
        method = "addResearchableAssemblylineRecipe(Lnet/minecraft/item/ItemStack;IIII[Ljava/lang/Object;[Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/item/ItemStack;II)Z",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 4)
    private static int modifyAssemblyLineDurationObject(int assDuration) {
        if (!MainConfig.enableAssemblingLineRecipesTimeChange) return assDuration;
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

        return modified;
    }
}
