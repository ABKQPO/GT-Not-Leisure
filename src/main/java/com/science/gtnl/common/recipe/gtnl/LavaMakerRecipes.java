package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;

public class LavaMakerRecipes implements IRecipePool {

    public RecipeMap<?> LMR = GTNLRecipeMaps.LavaMakerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.stone, 1))
            .fluidOutputs(Materials.Lava.getFluid(1000))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(LMR);
    }
}
