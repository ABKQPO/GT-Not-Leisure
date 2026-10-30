package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.INGOTS;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.metadata.CompressionTierKey;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public class MaterialProcessingRecipes {

    public static void addTurbineBladeRecipes(Werkstoff werkstoff) {
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

    public static void addSuperdensePlateRecipe(Werkstoff werkstoff) {
        if (werkstoff.hasItemType(OrePrefixes.plateDense)) {

            final CompressionTierKey COMPRESSION_TIER = CompressionTierKey.INSTANCE;

            RecipeBuilder.builder()
                .itemInputs(werkstoff.get(OrePrefixes.plate, 64))
                .itemOutputs(werkstoff.get(OrePrefixes.plateSuperdense, 1))

                .metadata(COMPRESSION_TIER, 1)
                .duration(
                    (int) Math.max(
                        werkstoff.getStats()
                            .getMass(),
                        1L))
                .eut(
                    werkstoff.getStats()
                        .getMass() > 128 ? TierEU.RECIPE_IV : TierEU.RECIPE_EV)
                .addTo(RecipeMaps.compressorRecipes);
        }
    }

    public static void addDustSolidifierRecipe(Werkstoff werkstoff) {
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
            .warn("GTNL: 144l fluid molder for 1 dust Recipe: {} - Success", werkstoff.getDefaultName());
    }

    public static void addDustSolidifierRecipe(OrePrefixes aPrefix, Materials aMaterial) {
        if (aPrefix != OrePrefixes.dust) return;
        if (aMaterial.mStandardMoltenFluid == null) return;
        if (aMaterial == Materials.Rubber) return;
        if (aMaterial == Materials.BorosilicateGlass) return;
        if (aMaterial == Materials.Glass) return;

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Ball.get(0))
            .itemOutputs(aMaterial.getDust(1))
            .fluidInputs(aMaterial.getMolten(1 * GTRecipeBuilder.INGOTS))
            .duration(1 * GTRecipeBuilder.SECONDS + 12 * GTRecipeBuilder.TICKS)
            .eut(GTUtility.calculateRecipeEU(aMaterial, 8))
            .addTo(RecipeMaps.fluidSolidifierRecipes);

        if (MainConfig.debug.enableDebugMode) ScienceNotLeisure.LOG
            .warn("GTNL: 144l fluid molder for 1 dust Recipe: {} - Success", aMaterial.getDefaultLocalName());
    }
}
