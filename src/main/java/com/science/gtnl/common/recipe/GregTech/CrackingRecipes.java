package com.science.gtnl.common.recipe.GregTech;

import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTUtility;

public class CrackingRecipes implements IRecipePool {

    final RecipeMap<?> CR = RecipeMaps.crackingRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(GGMaterial.naquadahSolution.getFluidOrGas(1000), Materials.Fluorine.getGas(1000))
            .fluidOutputs(MaterialPool.FluorineCrackedNaquadah.getFluidOrGas(1000))
            .duration(120)
            .eut(TierEU.RECIPE_UV)
            .addTo(CR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(GGMaterial.enrichedNaquadahRichSolution.getFluidOrGas(1000), Materials.Radon.getGas(1000))
            .fluidOutputs(MaterialPool.RadonCrackedEnrichedNaquadah.getFluidOrGas(1000))
            .duration(160)
            .eut(TierEU.RECIPE_UHV)
            .addTo(CR);
    }
}
