package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.MINUTES;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialsElements;

public class ReactorProcessingUnitRecipes implements IRecipePool {

    public RecipeMap<?> RPU = GTPPRecipeMaps.reactorProcessingUnitRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4), MaterialsElements.getInstance().FLUORINE.getCell(3))
            .itemOutputs(
                MaterialsElements.getInstance().KRYPTON.getCell(1),
                MaterialsElements.getInstance().XENON.getCell(1),
                MaterialsElements.getInstance().RADON.getCell(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 1),
                MaterialsElements.getInstance().RUTHENIUM.getDust(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 1),
                MaterialsElements.getInstance().RHODIUM.getDust(1))
            .outputChances(10000, 10000, 10000, 3000, 3000, 3000, 3000, 7500, 3000)
            .fluidInputs(GTNLMaterials.UraniumWaste.getFluidOrGas(3600))
            .duration(20 * MINUTES)
            .eut(TierEU.RECIPE_MV)
            .addTo(RPU);

    }
}
