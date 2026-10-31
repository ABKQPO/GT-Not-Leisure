package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.NANO_FORGE_TIER;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.compat.newhorizons.NHCoreRecipes;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class NanoForgeRecipes implements IRecipePool {

    public RecipeMap<?> NFR = RecipeMaps.nanoForgeRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.GarnetRed, 0),
                GGMaterial.shirabon.get(OrePrefixes.block, 8),
                GTNLItemList.HighlyAdvancedSoc.get(64),
                GTNLItemList.HighlyAdvancedSoc.get(64),
                GTNLItemList.HighlyAdvancedSoc.get(64),
                GTNLItemList.HighlyAdvancedSoc.get(64))
            .itemOutputs(GGMaterial.shirabon.get(OrePrefixes.nanite, 1))
            .fluidInputs(
                Materials.UUMatter.getFluid(500_000),
                Materials.TranscendentMetal.getMolten(9216),
                Materials.PrimordialMatter.getFluid(32000))
            .metadata(NANO_FORGE_TIER, 3)
            .duration(200 * SECONDS)
            .eut(2_000_000_000)
            .addTo(NFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Topaz, 0),
                GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.block, 8),
                ItemList.Circuit_Chip_SoC.get(64),
                ItemList.Circuit_Chip_SoC.get(64))
            .itemOutputs(GGMaterial.atomicSeparationCatalyst.get(OrePrefixes.nanite, 2))
            .fluidInputs(Materials.UUMatter.getFluid(200_000))
            .metadata(NANO_FORGE_TIER, 1)
            .duration(200 * SECONDS)
            .eut(20_000_000)
            .addTo(NFR);

        RecipeBuilder.builder()
            .itemInputs(
                GregtechItemList.Laser_Lens_Special.get(0),
                GGMaterial.preciousMetalAlloy.get(OrePrefixes.block, 8),
                ItemList.Circuit_Chip_NanoCPU.get(64),
                ItemList.Circuit_Chip_NanoCPU.get(64))
            .itemOutputs(GGMaterial.preciousMetalAlloy.get(OrePrefixes.nanite, 1))
            .fluidInputs(Materials.UUMatter.getFluid(300_000))
            .metadata(NANO_FORGE_TIER, 1)
            .duration(400 * SECONDS)
            .eut(40_000_000)
            .addTo(NFR);

        NHCoreRecipes.nanoForge();
    }

}
