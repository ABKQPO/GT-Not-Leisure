package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;
import com.science.gtnl.utils.recipes.data.CircuitNanitesRecipeData;
import com.science.gtnl.utils.recipes.metadata.CircuitNanitesDataMetadata;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;

public class CircuitNanitesDataRecipes implements IRecipePool {

    public RecipeMap<?> CNDR = GTNLRecipeMaps.CircuitNanitesDataRecipes;
    public CircuitNanitesDataMetadata RECIPE_DATA = CircuitNanitesDataMetadata.INSTANCE;
    public long worldSeed;

    public CircuitNanitesDataRecipes(long worldSeed) {
        this.worldSeed = worldSeed;
    }

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1))
            .metadata(
                RECIPE_DATA,
                CircuitNanitesRecipeData.getOrCreate(
                    GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1),
                    worldSeed,
                    0.1,
                    2.0,
                    0.8,
                    1.5,
                    1,
                    2,
                    0.1,
                    0.2,
                    64,
                    128,
                    0.05,
                    0.1))
            .duration(0)
            .eut(0)
            .addTo(CNDR);
    }
}
