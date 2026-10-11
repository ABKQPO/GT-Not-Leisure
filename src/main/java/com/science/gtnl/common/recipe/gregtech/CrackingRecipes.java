package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;

public class CrackingRecipes implements IRecipePool {

    public RecipeMap<?> CR = RecipeMaps.crackingRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs()
            .circuit(1)
            .fluidInputs(GGMaterial.naquadahSolution.getFluidOrGas(1000), Materials.Fluorine.getGas(1000))
            .fluidOutputs(GTNLMaterials.FluorineCrackedNaquadah.getFluidOrGas(1000))
            .duration(120)
            .eut(TierEU.RECIPE_UV)
            .addTo(CR);

        RecipeBuilder.builder()
            .itemInputs()
            .circuit(1)
            .fluidInputs(GGMaterial.enrichedNaquadahRichSolution.getFluidOrGas(1000), Materials.Radon.getGas(1000))
            .fluidOutputs(GTNLMaterials.RadonCrackedEnrichedNaquadah.getFluidOrGas(1000))
            .duration(160)
            .eut(TierEU.RECIPE_UHV)
            .addTo(CR);
    }
}
