package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.enums.Mods.BloodArsenal;
import static gregtech.api.enums.Mods.IndustrialCraft2;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.MeteorRecipeData;

import WayofTime.alchemicalWizardry.common.summoning.meteor.Meteor;
import WayofTime.alchemicalWizardry.common.summoning.meteor.MeteorRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;

public class FallingTowerRecipes implements IRecipePool {

    public RecipeMap<?> BSTR = RecipePool.FallingTowerRecipes;

    @Override
    public void loadRecipes() {
        for (Meteor meteor : MeteorRegistry.meteorList) {
            MeteorRecipeData data = new MeteorRecipeData(meteor);

            ItemStack input = data.input;

            if (input != null) {
                if (input.isItemEqual(GTModHandler.getModItem(IndustrialCraft2.ID, "blockNuke", 1))
                    || input.isItemEqual(GTModHandler.getModItem(BloodArsenal.ID, "blood_tnt", 1))) {
                    continue;
                }
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
                .duration(data.getTotalExpectedAmount() / 2)
                .addTo(BSTR);
        }
    }

}
