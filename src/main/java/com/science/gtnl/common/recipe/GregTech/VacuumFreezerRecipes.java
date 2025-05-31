package com.science.gtnl.common.recipe.GregTech;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;

public class VacuumFreezerRecipes implements IRecipePool {

    final RecipeMap<?> CNCR = RecipeMaps.vacuumFreezerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.EnderAir.getFluidOrGas(4000))
            .fluidOutputs(MaterialPool.FluidEnderAir.getFluidOrGas(4000))
            .specialValue(0)
            .duration(80)
            .eut(TierEU.RECIPE_IV)
            .addTo(CNCR);

    }
}
