package com.science.gtnl.mixins.late.Bartwork;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.werkstoff_loaders.recipe.SimpleMetalLoader;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

@Mixin(value = SimpleMetalLoader.class, remap = false)
public abstract class MixinSimpleMetalLoader {

    @Inject(method = "run(Lbartworks/system/material/Werkstoff;)V", at = @At("TAIL"), remap = false)
    private void injectSuperdensePlateRecipe(Werkstoff werkstoff, CallbackInfo ci) {
        if (werkstoff.hasItemType(OrePrefixes.turbineBlade)) {
            if (werkstoff.hasItemType(OrePrefixes.cellMolten)) {
                RecipeBuilder.builder()
                    .itemInputs(ItemList.Shape_Mold_Turbine_Blade.get(0))
                    .itemOutputs(werkstoff.get(OrePrefixes.turbineBlade, 1))
                    .fluidInputs(werkstoff.getMolten(864))
                    .duration(
                        (int) Math.max(
                            werkstoff.getStats()
                                .getMass(),
                            1L))
                    .eut(16)
                    .addTo(RecipeMaps.fluidSolidifierRecipes);
            }

            if (werkstoff.hasItemType(OrePrefixes.cell)) {
                RecipeBuilder.builder()
                    .itemInputs(ItemList.Shape_Mold_Turbine_Blade.get(0))
                    .itemOutputs(werkstoff.get(OrePrefixes.turbineBlade, 1))
                    .fluidInputs(werkstoff.getFluidOrGas(864))
                    .duration(
                        (int) Math.max(
                            werkstoff.getStats()
                                .getMass(),
                            1L))
                    .eut(16)
                    .addTo(RecipeMaps.fluidSolidifierRecipes);
            }

            RecipeBuilder.builder()
                .itemInputs(
                    werkstoff.get(OrePrefixes.ingot, 6),
                    GTUtility.copyAmount(0, ItemList.Shape_Extruder_Turbine_Blade.get(1)))
                .itemOutputs(werkstoff.get(OrePrefixes.turbineBlade, 1))
                .duration(
                    (int) Math.max(
                        werkstoff.getStats()
                            .getMass(),
                        1L))
                .eut(16)
                .addTo(RecipeMaps.extruderRecipes);

            RecipeBuilder.builder()
                .itemInputs(werkstoff.get(OrePrefixes.plateDouble, 3), werkstoff.get(OrePrefixes.screw, 2))
                .itemOutputs(werkstoff.get(OrePrefixes.turbineBlade, 1))
                .duration(
                    (int) Math.max(
                        werkstoff.getStats()
                            .getMass(),
                        1L))
                .eut(16)
                .addTo(RecipeMaps.formingPressRecipes);

            GTModHandler.addCraftingRecipe(
                werkstoff.get(OrePrefixes.turbineBlade, 1),
                new Object[] { "ABC", "DBD", " B ", 'A', "craftingToolFile", 'B',
                    werkstoff.get(OrePrefixes.plateDouble, 1), 'C', "craftingToolScrewdriver", 'D',
                    werkstoff.get(OrePrefixes.screw, 1) });
        }
    }
}
