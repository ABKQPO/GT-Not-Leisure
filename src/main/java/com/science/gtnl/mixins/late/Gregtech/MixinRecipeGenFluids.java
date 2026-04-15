package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
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
    private void injectBeforeGetPlate(Material material, boolean dO, CallbackInfo ci) {
        if (material.getDust(1) == null) return;

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Ball.get(0))
            .itemOutputs(material.getDust(1))
            .fluidInputs(material.getFluidStack(1 * GTRecipeBuilder.INGOTS))
            .duration(1 * GTRecipeBuilder.SECONDS + 12 * GTRecipeBuilder.TICKS)
            .eut(material.vVoltageMultiplier)
            .addTo(RecipeMaps.fluidSolidifierRecipes);

        if (MainConfig.debug.enableDebugMode) ScienceNotLeisure.LOG
            .warn("GTNL: 144l fluid molder for 1 dust Recipe: {} - Success", material.getLocalizedName());
    }
}
