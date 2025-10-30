package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialMisc;

public class ChemicalPlantRecipes implements IRecipePool {

    public RecipeMap<?> CPR = GTPPRecipeMaps.chemicalPlantRecipes;

    @Override
    public void loadRecipes() {
        // 海晶石溶液单步结晶
        GTValues.RA.stdBuilder()
            .itemInputs(MaterialMisc.STRONTIUM_HYDROXIDE.getDust(48))
            .itemOutputs(ItemList.Prismarine_Precipitate.get(8))
            .fluidInputs(Materials.PrismarineSolution.getFluid(8000))
            .fluidOutputs(Materials.PrismarineContaminatedHydrogenPeroxide.getFluid(6000))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CPR);
    }
}
