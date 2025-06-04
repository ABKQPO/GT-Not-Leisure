package com.science.gtnl.common.recipe.GregTech;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialsElements;

public class AlloyBlastSmelterRecipes implements IRecipePool {

    final RecipeMap<?> aBS = GTPPRecipeMaps.alloyBlastSmelterRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(MaterialsElements.getInstance().GERMANIUM.getDust(3)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 3))
            .fluidInputs(Materials.Nitrogen.getGas(10000))
            .fluidOutputs(MaterialPool.Germaniumtungstennitride.getMolten(2304))
            .specialValue(0)
            .duration(9600)
            .eut(30720)
            .addTo(aBS);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Invar, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1))
            .fluidOutputs(MaterialPool.HSLASteel.getMolten(720))
            .specialValue(0)
            .duration(3750)
            .eut(480)
            .addTo(aBS);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 2))
            .fluidOutputs(MaterialPool.MolybdenumDisilicide.getMolten(432))
            .specialValue(0)
            .duration(1800)
            .eut(1920)
            .addTo(aBS);
    }
}
