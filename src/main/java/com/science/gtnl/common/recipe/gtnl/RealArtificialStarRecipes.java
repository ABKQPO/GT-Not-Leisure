package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.compat.twistspacetechnology.TwistSpaceTechnologyRecipes;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.recipe.RecipeMap;

public class RealArtificialStarRecipes implements IRecipePool {

    public RecipeMap<?> RAS = GTNLRecipeMaps.RealArtificialStarRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.EnhancementCore.get(1))
            .specialValue(MainConfig.machine.artificial_star.euEveryEnhancementCore)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(RAS);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.DepletedExcitedNaquadahFuelRod.get(1))
            .specialValue(MainConfig.machine.artificial_star.euEveryDepletedExcitedNaquadahFuelRod)
            .eut(0)
            .duration(0)
            .fake()
            .addTo(RAS);

        TwistSpaceTechnologyRecipes.realArtificialStar();
    }
}
