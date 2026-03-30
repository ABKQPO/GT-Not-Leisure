package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;

public class TreeDiagramRecipes implements IRecipePool {

    public RecipeMap<?> ACALR = GTNLRecipeMaps.TreeDiagramRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.dirt, 1))
            .itemOutputs(ItemList.Circuit_Crystalprocessor.get(64))
            .specialValue(14)
            .duration(Integer.MAX_VALUE)
            .eut(TierEU.RECIPE_MAX)
            .addTo(ACALR);
    }
}
