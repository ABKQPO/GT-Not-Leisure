package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.enums.Mods.*;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.API.recipe.BartWorksRecipeMaps;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class ElectricImplosionCompressorRecipes implements IRecipePool {

    public final RecipeMap<?> EICR = BartWorksRecipeMaps.electricImplosionCompressorRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(EternalSingularity.ID, "eternal_singularity", 1, 0),
                ItemList.EnergisedTesseract.get(1))
            .fluidInputs(MaterialsUEVplus.Protomatter.getFluid(1000))
            .itemOutputs(GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 47))
            .fluidOutputs(MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(50000))
            .duration(2 * SECONDS)
            .eut(TierEU.RECIPE_UMV)
            .addTo(EICR);

    }
}
