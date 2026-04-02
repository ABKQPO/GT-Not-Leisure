package com.science.gtnl.mixins.late.Gregtech;

import static gregtech.api.util.GTRecipeBuilder.INGOTS;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeBuilder.TICKS;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.recipe.RecipeMapBuilder;
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

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Ball.get(0))
            .itemOutputs(material.getDust(1))
            .fluidInputs(material.getFluidStack(1 * INGOTS))
            .duration(1 * SECONDS + 12 * TICKS)
            .eut(material.vVoltageMultiplier)
            .addTo(
                RecipeMapBuilder.of("gt.recipe.fluidsolidifier")
                    .maxIO(1, 1, 1, 0)
                    .minInputs(1, 1)
                    .slotOverlays(
                        (index, isFluid, isOutput, isSpecial) -> !isFluid && !isOutput ? GTUITextures.OVERLAY_SLOT_MOLD
                            : null)
                    .build());

        if (MainConfig.debug.enableDebugMode) ScienceNotLeisure.LOG
            .warn("GTNL: 144l fluid molder for 1 dust Recipe: {} - Success", material.getLocalizedName());
    }
}
