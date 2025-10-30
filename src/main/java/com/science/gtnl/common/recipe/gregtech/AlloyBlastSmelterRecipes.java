package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.config.MainConfig;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialsElements;

public class AlloyBlastSmelterRecipes implements IRecipePool {

    public RecipeMap<?> aBS = GTPPRecipeMaps.alloyBlastSmelterRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(MaterialsElements.getInstance().GERMANIUM.getDust(3)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 3))
            .fluidInputs(Materials.Nitrogen.getGas(10000))
            .fluidOutputs(MaterialPool.Germaniumtungstennitride.getMolten(2304))
            .duration(9600)
            .eut(30720)
            .addTo(aBS);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Invar, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1))
            .fluidOutputs(MaterialPool.HSLASteel.getMolten(720))
            .duration(3750)
            .eut(480)
            .addTo(aBS);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 2))
            .fluidOutputs(MaterialPool.MolybdenumDisilicide.getMolten(432))
            .duration(1800)
            .eut(1920)
            .addTo(aBS);

        if (MainConfig.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadDeleteRecipe() {
        GTValues.RA.stdBuilder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1))
            .fluidOutputs(Materials.Europium.getMolten(144))
            .duration(120)
            .eut(TierEU.RECIPE_LuV)
            .addTo(aBS);
    }
}
