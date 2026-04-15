package com.science.gtnl.mixins.late.Bartwork;

import static gregtech.api.util.GTRecipeBuilder.INGOTS;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.werkstoff_loaders.recipe.MoltenCellLoader;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;

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
        if (!werkstoff.getGenerationFeatures()
            .hasMetalCraftingSolidifierRecipes()) {
            return;
        }

        if (!werkstoff.hasItemType(OrePrefixes.dust)) {
            return;
        }

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Ball.get(0))
            .itemOutputs(werkstoff.get(OrePrefixes.dust))
            .fluidInputs(werkstoff.getMolten(1 * INGOTS))
            .duration(
                (int) Math.max(
                    werkstoff.getStats()
                        .getMass(),
                    1L))
            .eut(
                werkstoff.getStats()
                    .getMass() > 128 ? 64 : 30)
            .addTo(RecipeMaps.fluidSolidifierRecipes);

        if (MainConfig.debug.enableDebugMode) ScienceNotLeisure.LOG
            .warn("GTNL: 144l fluid molder for 1 dust Recipe: {} - Success", OrePrefixes.material.mRegularLocalName);
    }
}
