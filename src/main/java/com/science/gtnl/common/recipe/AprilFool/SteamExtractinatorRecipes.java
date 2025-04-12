package com.science.gtnl.common.recipe.AprilFool;

import static gregtech.api.enums.GTValues.RA;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import com.science.gtnl.Utils.recipes.IRecipePool;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.common.recipe.RecipeRegister;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;

public class SteamExtractinatorRecipes implements IRecipePool {

    final RecipeMap<?> SER = RecipeRegister.SteamExtractinatorRecipes;

    @Override
    public void loadRecipes() {

        RA.stdBuilder()
            .fluidInputs(MaterialPool.GravelSluice.getFluidOrGas(4000))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Iron, 8),
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Copper, 8),
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Tin, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Clay, 8),
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Salt, 8),
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Coal, 8))
            .duration(5 * SECONDS)
            .eut(300)
            .addTo(SER);

        RA.stdBuilder()
            .fluidInputs(MaterialPool.SandSluice.getFluidOrGas(4000))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Gold, 8),
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Redstone, 8),
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Zinc, 8),
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Ruby, 8),
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Sulfur, 8))
            .duration(5 * SECONDS)
            .eut(300)
            .addTo(SER);

        RA.stdBuilder()
            .fluidInputs(MaterialPool.ObsidianSluice.getFluidOrGas(4000))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Silver, 8),
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Diamond, 8),
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Emerald, 8),
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Gypsum, 1),
                GTOreDictUnificator.get(OrePrefixes.crushed, Materials.Calcite, 1))
            .duration(5 * SECONDS)
            .eut(300)
            .addTo(SER);

        RA.stdBuilder()
            .fluidInputs(MaterialPool.GemSluice.getFluidOrGas(4000))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Diamond, 1),
                GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Emerald, 1),
                GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Ruby, 1),
                GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Salt, 1))
            .outputChances(1000, 1000, 1000, 2000)
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(SER);
    }
}
