package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class VacuumFreezerRecipes implements IRecipePool {

    public RecipeMap<?> VFR = RecipeMaps.vacuumFreezerRecipes;
    public RecipeMap<?> AFR = GTPPRecipeMaps.advancedFreezerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.EnderAir.getFluidOrGas(4000))
            .fluidOutputs(MaterialPool.LiquidEnderAir.getFluidOrGas(4000))
            .duration(80)
            .eut(TierEU.RECIPE_HV)
            .addTo(VFR)
            .addTo(AFR);

        if (MainConfig.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadDeleteRecipe() {
        // 半流质下界空气增产 x10
        RecipeBuilder.builder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .fluidInputs(Materials.NetherAir.getFluid(1_000))
            .fluidOutputs(Materials.NetherSemiFluid.getFluid(1_000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(VFR)
            .addTo(AFR);
    }
}
