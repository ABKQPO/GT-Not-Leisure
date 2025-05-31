package com.science.gtnl.mixins.late.Bartwork;

import static gregtech.api.enums.OrePrefixes.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.werkstoff_loaders.recipe.SimpleMetalLoader;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

@SuppressWarnings("UnusedMixin")
@Mixin(value = SimpleMetalLoader.class, remap = false)
public abstract class SimpleMetalLoader_Mixin {

    @Inject(method = "run(Lbartworks/system/material/Werkstoff;)V", at = @At("TAIL"), remap = false)
    private void injectSuperdensePlateRecipe(Werkstoff werkstoff, CallbackInfo ci) {
        if (werkstoff.hasItemType(turbineBlade)) {
            if (werkstoff.hasItemType(cellMolten)) {
                GTValues.RA.stdBuilder()
                    .itemInputs(GTUtility.copyAmount(0, ItemList.Shape_Mold_Turbine_Blade.get(1)))
                    .itemOutputs(werkstoff.get(turbineBlade, 1))
                    .fluidInputs(werkstoff.getMolten(864))

                    .duration(
                        (int) Math.max(
                            werkstoff.getStats()
                                .getMass(),
                            1L))
                    .eut(16)
                    .addTo(RecipeMaps.fluidSolidifierRecipes);
            }

            if (werkstoff.hasItemType(cell)) {
                GTValues.RA.stdBuilder()
                    .itemInputs(GTUtility.copyAmount(0, ItemList.Shape_Mold_Turbine_Blade.get(1)))
                    .itemOutputs(werkstoff.get(turbineBlade, 1))
                    .fluidInputs(werkstoff.getFluidOrGas(864))

                    .duration(
                        (int) Math.max(
                            werkstoff.getStats()
                                .getMass(),
                            1L))
                    .eut(16)
                    .addTo(RecipeMaps.fluidSolidifierRecipes);
            }

            GTValues.RA.stdBuilder()
                .itemInputs(
                    werkstoff.get(ingot, 6),
                    GTUtility.copyAmount(0, ItemList.Shape_Extruder_Turbine_Blade.get(1)))
                .itemOutputs(werkstoff.get(turbineBlade, 1))

                .duration(
                    (int) Math.max(
                        werkstoff.getStats()
                            .getMass(),
                        1L))
                .eut(16)
                .addTo(RecipeMaps.extruderRecipes);

            GTValues.RA.stdBuilder()
                .itemInputs(werkstoff.get(plateDouble, 3), werkstoff.get(screw, 2))
                .itemOutputs(werkstoff.get(turbineBlade, 1))

                .duration(
                    (int) Math.max(
                        werkstoff.getStats()
                            .getMass(),
                        1L))
                .eut(16)
                .addTo(RecipeMaps.formingPressRecipes);

            GTModHandler.addCraftingRecipe(
                werkstoff.get(turbineBlade, 1),
                new Object[] { "ABC", "DBD", " B ", 'A', "craftingToolFile", 'B', werkstoff.get(plateDouble, 1), 'C',
                    "craftingToolScrewdriver", 'D', werkstoff.get(screw, 1) });
        }
    }
}
