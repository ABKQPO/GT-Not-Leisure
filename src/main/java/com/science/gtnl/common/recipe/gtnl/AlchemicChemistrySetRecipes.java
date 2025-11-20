package com.science.gtnl.common.recipe.gtnl;

import java.lang.reflect.Array;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;

import WayofTime.alchemicalWizardry.api.alchemy.AlchemyRecipe;
import WayofTime.alchemicalWizardry.api.alchemy.AlchemyRecipeRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;

public class AlchemicChemistrySetRecipes implements IRecipePool {

    public RecipeMap<?> ACSR = RecipePool.AlchemicChemistrySetRecipes;

    @Override
    public void loadRecipes() {
        for (AlchemyRecipe recipe : AlchemyRecipeRegistry.recipes) {
            GTValues.RA.stdBuilder()
                .itemInputs(concatToLast(ItemStack.class, recipe.getRecipe()))
                .itemOutputs(recipe.getResult())
                .specialValue(recipe.getAmountNeeded() * 2)
                .eut(0)
                .duration(128)
                .addTo(ACSR);
        }
    }

    public static <T> T[] concatToLast(Class<T> clazz, T[] array) {
        // noinspection unchecked
        T[] ret = (T[]) Array.newInstance(clazz, array.length);

        System.arraycopy(array, 0, ret, 0, array.length);
        return ret;
    }
}
