package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.data.NanitesIntegratedProcessingRecipesData;
import com.science.gtnl.utils.recipes.metadata.NanitesIntegratedProcessingMetadata;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;

public class NanitesIntegratedProcessingRecipes implements IRecipePool {

    public NanitesIntegratedProcessingMetadata MODULE_REQ = NanitesIntegratedProcessingMetadata.INSTANCE;
    public RecipeMap<?> NIPR = RecipePool.NanitesIntegratedProcessingRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.dirt, 1))
            .itemOutputs(GTUtility.copyAmountUnsafe(1919810, ItemList.KevlarFiber.get(1)))
            .metadata(MODULE_REQ, new NanitesIntegratedProcessingRecipesData(true, true, true))
            .duration(300)
            .eut(TierEU.RECIPE_UEV)
            .addTo(NIPR);
    }
}
