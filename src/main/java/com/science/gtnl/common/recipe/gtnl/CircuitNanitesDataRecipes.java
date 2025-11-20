package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.CircuitNanitesDataSpecialValue;
import com.science.gtnl.utils.recipes.CircuitNanitesRecipeData;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;

public class CircuitNanitesDataRecipes implements IRecipePool {

    public RecipeMap<?> CNDR = RecipePool.CircuitNanitesDataRecipes;
    public CircuitNanitesDataSpecialValue RECIPE_DATA = CircuitNanitesDataSpecialValue.INSTANCE;
    public long worldSeed;

    public CircuitNanitesDataRecipes(long worldSeed) {
        this.worldSeed = worldSeed;
    }

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.MagMatter, 1))
            .metadata(
                RECIPE_DATA,
                CircuitNanitesRecipeData.getOrCreate(
                    GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.MagMatter, 1),
                    worldSeed,
                    0.1,
                    2.0,
                    0.8,
                    1.5,
                    1,
                    2,
                    0.2,
                    0.5,
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
