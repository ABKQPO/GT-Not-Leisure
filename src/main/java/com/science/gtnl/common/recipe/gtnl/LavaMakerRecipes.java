package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.enums.GTValues.RA;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;

public class LavaMakerRecipes implements IRecipePool {

    public RecipeMap<?> LMR = RecipePool.LavaMakerRecipes;

    @Override
    public void loadRecipes() {

        RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.stone, 1))
            .fluidOutputs(Materials.Lava.getFluid(1000))
            .duration(1 * SECONDS)
            .eut(16)
            .addTo(LMR);
    }
}
