package com.science.gtnl.common.recipe.GregTech;

import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;

public class DigesterRecipes implements IRecipePool {

    final RecipeMap<?> dR = LanthanidesRecipeMaps.digesterRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.RareEarthOxide.get(OrePrefixes.dust, 1))
            .fluidInputs(Materials.Hydrogen.getGas(1000))
            .itemOutputs(MaterialPool.RareEarthMetal.get(OrePrefixes.dust, 1))
            .fluidOutputs(Materials.Water.getFluid(500))
            .specialValue(0)
            .metadata(COIL_HEAT, 800)
            .duration(400)
            .eut(7680)
            .addTo(dR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumOxide, 3))
            .fluidInputs(MaterialPool.RareEarthChlorides.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.RareEarthOxide.get(OrePrefixes.dust, 1))
            .fluidOutputs(Materials.SaltWater.getFluid(1000))
            .specialValue(0)
            .metadata(COIL_HEAT, 2580)
            .duration(800)
            .eut(1920)
            .addTo(dR);
    }
}
