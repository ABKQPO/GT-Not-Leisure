package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.recipe.RecipeMap;

public class SteamWeatherModuleRecipes implements IRecipePool {

    public RecipeMap<?> SWMR = GTNLRecipeMaps.SteamWeatherModuleRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.NaturaSulfurCloud.get(16), ModsItemlist.ThaumcraftFireCrystal.get(1))
            .specialValue(1)
            .duration(36000)
            .eut(0)
            .addTo(SWMR);

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.NaturaGrayCloud.get(16), ModsItemlist.ThaumcraftWaterCrystal.get(1))
            .specialValue(2)
            .duration(36000)
            .eut(0)
            .addTo(SWMR);

        RecipeBuilder.builder()
            .itemInputs(
                ModsItemlist.NaturaBlackCloud.get(16),
                ModsItemlist.ThaumcraftWaterCrystal.get(1),
                ModsItemlist.ThaumcraftBlockCrystal.get(1))
            .specialValue(3)
            .duration(36000)
            .eut(0)
            .addTo(SWMR);
    }
}
