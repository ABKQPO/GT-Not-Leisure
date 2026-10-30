package com.science.gtnl.mixins.early.gregtech.assLineRemover;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.recipe.AssLineRecipeHook;

import tectech.recipe.TTRecipeAdder;

@Mixin(value = TTRecipeAdder.class, remap = false)
public class MixinTTRecipeAdder {

    @Inject(
        method = "addResearchableAssemblylineRecipe(Lnet/minecraft/item/ItemStack;IIII[Lnet/minecraft/item/ItemStack;[Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/item/ItemStack;II)Z",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private static void science$TTHook$1(ItemStack aResearchItem, int totalComputationRequired,
        int computationRequiredPerSec, int researchEUt, int researchAmperage, ItemStack[] aInputs,
        FluidStack[] aFluidInputs, ItemStack aOutput, int assDuration, int assEUt,
        CallbackInfoReturnable<Boolean> cir) {
        if (AssLineRecipeHook.removeTTRecipe(
            aResearchItem,
            totalComputationRequired,
            computationRequiredPerSec,
            researchEUt,
            researchAmperage,
            aOutput,
            assDuration,
            assEUt)) cir.setReturnValue(true);
    }

    @Inject(
        method = "addResearchableAssemblylineRecipe(Lnet/minecraft/item/ItemStack;IIII[Ljava/lang/Object;[Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/item/ItemStack;II)Z",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private static void science$TTHook$2(ItemStack aResearchItem, int totalComputationRequired,
        int computationRequiredPerSec, int researchEUt, int researchAmperage, Object[] aInputs,
        FluidStack[] aFluidInputs, ItemStack aOutput, int assDuration, int assEUt,
        CallbackInfoReturnable<Boolean> cir) {
        if (AssLineRecipeHook.removeTTRecipe(
            aResearchItem,
            totalComputationRequired,
            computationRequiredPerSec,
            researchEUt,
            researchAmperage,
            aOutput,
            assDuration,
            assEUt)) cir.setReturnValue(true);
    }
}
