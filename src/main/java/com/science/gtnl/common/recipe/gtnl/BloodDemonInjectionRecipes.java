package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;

import WayofTime.alchemicalWizardry.ModItems;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipe;
import WayofTime.alchemicalWizardry.api.altarRecipeRegistry.AltarRecipeRegistry;
import WayofTime.alchemicalWizardry.api.bindingRegistry.BindingRecipe;
import WayofTime.alchemicalWizardry.api.bindingRegistry.BindingRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;

public class BloodDemonInjectionRecipes implements IRecipePool {

    public RecipeMap<?> BIR = RecipePool.BloodDemonInjectionRecipes;

    @Override
    public void loadRecipes() {
        for (AltarRecipe recipe : AltarRecipeRegistry.altarRecipes) {
            // filter empty output recipes, which these recipes are most likely charging orbs.
            if (recipe.result == null) continue;

            GTValues.RA.stdBuilder()
                .itemInputs(recipe.requiredItem)
                .itemOutputs(recipe.result)
                .eut(0)
                .specialValue(recipe.liquidRequired)
                .duration(128)
                .addTo(BIR);
        }

        for (BindingRecipe recipe : BindingRegistry.bindingRecipes) {
            GTValues.RA.stdBuilder()
                .itemInputs(
                    recipe.requiredItem,
                    new ItemStack(ModItems.weakBloodShard, 1),
                    GTUtility.getIntegratedCircuit(11))
                .itemOutputs(recipe.outputItem)
                .eut(0)
                .specialValue(30000)
                .duration(128)
                .addTo(BIR);
        }
    }
}
