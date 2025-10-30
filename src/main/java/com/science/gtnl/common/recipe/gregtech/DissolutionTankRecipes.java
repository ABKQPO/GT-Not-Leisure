package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeConstants.DISSOLUTION_TANK_RATIO;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;

public class DissolutionTankRecipes implements IRecipePool {

    public RecipeMap<?> DTR = LanthanidesRecipeMaps.dissolutionTankRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.RareEarth, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 30))
            .fluidInputs(Materials.Water.getFluid(9000), Materials.NitricAcid.getFluid(1000))
            .fluidOutputs(MaterialPool.RareEarthHydroxides.getFluidOrGas(10000))
            .metadata(DISSOLUTION_TANK_RATIO, 9)
            .specialValue(0)
            .duration(50)
            .eut(480)
            .addTo(DTR);
    }
}
