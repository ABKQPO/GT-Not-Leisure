package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import appeng.api.AEApi;
import bartworks.API.recipe.BartWorksRecipeMaps;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;

public class ElectricImplosionCompressorRecipes implements IRecipePool {

    public final RecipeMap<?> EICR = BartWorksRecipeMaps.electricImplosionCompressorRecipes;

    @Override
    public void loadRecipes() {
        var aeMaterials = AEApi.instance()
            .definitions()
            .materials();

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.EternalSingularity.get(1), ItemList.EnergisedTesseract.get(1))
            .fluidInputs(Materials.Protomatter.getFluid(1000))
            .itemOutputs(
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull())
            .fluidOutputs(Materials.DTR.getFluid(50000))
            .duration(2 * SECONDS)
            .eut(TierEU.RECIPE_UMV)
            .addTo(EICR);

    }
}
