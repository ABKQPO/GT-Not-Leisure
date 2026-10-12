package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;

public class TranscendentPlasmaMixerRecipes implements IRecipePool {

    public RecipeMap<?> TPMP = RecipeMaps.transcendentPlasmaMixerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs()
            .circuit(6)
            .fluidInputs(
                Materials.Time.getMolten(710),
                Materials.Space.getMolten(710),
                GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(1000),
                GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(1000),
                GGMaterial.naquadahBasedFuelMkIII.getFluidOrGas(1000),
                GGMaterial.naquadahBasedFuelMkIV.getFluidOrGas(1000),
                GGMaterial.naquadahBasedFuelMkV.getFluidOrGas(1000),
                GGMaterial.naquadahBasedFuelMkVI.getFluidOrGas(1000),
                Materials.LiquidAir.getFluid(85200))
            .fluidOutputs(GTNLMaterials.ExcitedNaquadahFuel.getFluidOrGas(1000))
            .duration(20)
            .eut(TierEU.RECIPE_UXV)
            .addTo(TPMP);
    }
}
