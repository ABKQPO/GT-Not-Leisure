package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
import gtPlusPlus.core.material.Material;

public class FluidSolidifierDustRecipes {

    private FluidSolidifierDustRecipes() {}

    public static void add(Material material) {
        if (material.getDust(1) == null) return;
        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Ball.get(0))
            .itemOutputs(material.getDust(1))
            .fluidInputs(material.getFluidStack(1 * GTRecipeBuilder.INGOTS))
            .duration(1 * GTRecipeBuilder.SECONDS + 12 * GTRecipeBuilder.TICKS)
            .eut(material.voltageMultiplier)
            .addTo(RecipeMaps.fluidSolidifierRecipes);
        if (MainConfig.debug.enableDebugMode) ScienceNotLeisure.LOG
            .warn("GTNL: 144l fluid molder for 1 dust Recipe: {} - Success", material.getLocalizedName());
    }
}
