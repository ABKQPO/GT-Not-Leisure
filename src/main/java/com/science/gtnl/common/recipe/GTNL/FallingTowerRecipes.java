package com.science.gtnl.common.recipe.GTNL;

import static gregtech.api.enums.Mods.IndustrialCraft2;

import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.recipes.MeteorRecipeData;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import WayofTime.alchemicalWizardry.common.summoning.meteor.Meteor;
import WayofTime.alchemicalWizardry.common.summoning.meteor.MeteorRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class FallingTowerRecipes implements IRecipePool {

    final RecipeMap<?> BSTR = RecipeRegister.FallingTowerRecipes;

    @Override
    public void loadRecipes() {
        for (Meteor meteor : MeteorRegistry.meteorList) {
            MeteorRecipeData data = new MeteorRecipeData(meteor);

            ItemStack input = data.input;

            if (input != null && input.isItemEqual(GTModHandler.getModItem(IndustrialCraft2.ID, "blockNuke", 1))) {
                continue;
            }

            ItemStack[] outputs = data.outputs.toArray(new ItemStack[0]);
            int[] chances = data.chances.stream()
                .mapToInt(chance -> (int) (chance * 20000))
                .toArray();

            GTValues.RA.stdBuilder()
                .itemInputs(input)
                .itemOutputs(outputs)
                .specialValue(data.cost)
                .eut(0)
                .duration(data.getTotalExpectedAmount())
                .addTo(BSTR);
        }
    }

}
