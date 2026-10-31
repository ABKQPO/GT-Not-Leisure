package com.science.gtnl.mixins.late.tecTech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.science.gtnl.common.recipe.AssemblyLineDurations;

import tectech.recipe.TTRecipeAdder;

@Mixin(value = TTRecipeAdder.class, remap = false)
public abstract class MixinTTRecipeAdder {

    @ModifyVariable(
        method = "addResearchableAssemblylineRecipe(Lnet/minecraft/item/ItemStack;IIII[Lnet/minecraft/item/ItemStack;[Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/item/ItemStack;II)Z",
        at = @At("HEAD"),
        argsOnly = true,
        name = "assDuration")
    private static int modifyAssemblyLineDuration(int assDuration) {
        return AssemblyLineDurations.scale(assDuration);
    }

    @ModifyVariable(
        method = "addResearchableAssemblylineRecipe(Lnet/minecraft/item/ItemStack;IIII[Ljava/lang/Object;[Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/item/ItemStack;II)Z",
        at = @At("HEAD"),
        argsOnly = true,
        name = "assDuration")
    private static int modifyAssemblyLineDurationObject(int assDuration) {
        return AssemblyLineDurations.scale(assDuration);
    }
}
