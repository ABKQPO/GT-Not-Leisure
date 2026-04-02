package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;

public class DigesterRecipes implements IRecipePool {

    public RecipeMap<?> dR = LanthanidesRecipeMaps.digesterRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.RareEarthOxide.get(OrePrefixes.dust, 1))
            .fluidInputs(Materials.Hydrogen.getGas(1000))
            .itemOutputs(GTNLMaterials.RareEarthMetal.get(OrePrefixes.dust, 1))
            .fluidOutputs(Materials.Water.getFluid(500))
            .metadata(COIL_HEAT, 800)
            .duration(400)
            .eut(7680)
            .addTo(dR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumOxide, 3))
            .fluidInputs(GTNLMaterials.RareEarthChlorides.getFluidOrGas(1000))
            .itemOutputs(GTNLMaterials.RareEarthOxide.get(OrePrefixes.dust, 1))
            .fluidOutputs(Materials.SaltWater.getFluid(1000))
            .metadata(COIL_HEAT, 2580)
            .duration(800)
            .eut(1920)
            .addTo(dR);
    }
}
