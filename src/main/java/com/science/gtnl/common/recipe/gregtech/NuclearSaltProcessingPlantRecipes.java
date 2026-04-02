package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.MINUTES;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialsElements;

public class NuclearSaltProcessingPlantRecipes implements IRecipePool {

    public RecipeMap<?> NSPP = GTPPRecipeMaps.nuclearSaltProcessingPlantRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 1),
                MaterialsElements.getInstance().RUTHENIUM.getDust(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 1),
                MaterialsElements.getInstance().RHODIUM.getDust(1))
            .outputChances(3500, 3500, 3500, 3500, 8000, 3500)
            .fluidInputs(GTNLMaterials.UraniumWaste.getFluidOrGas(3600), Materials.Fluorine.getGas(2500))
            .fluidOutputs(
                WerkstoffLoader.Krypton.getFluidOrGas(1000),
                WerkstoffLoader.Xenon.getFluidOrGas(1000),
                Materials.Radon.getGas(1000))
            .duration(20 * MINUTES)
            .eut(TierEU.RECIPE_MV)
            .addTo(NSPP);

    }
}
