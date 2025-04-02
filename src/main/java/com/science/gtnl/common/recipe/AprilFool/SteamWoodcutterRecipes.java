package com.science.gtnl.common.recipe.AprilFool;

import static gregtech.api.enums.GTValues.RA;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeBuilder.TICKS;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.recipe.IRecipePool;
import com.science.gtnl.common.recipe.RecipeRegister;

import gregtech.api.recipe.RecipeMap;

public class SteamWoodcutterRecipes implements IRecipePool {

    final RecipeMap<?> SWR = RecipeRegister.SteamWoodcutterRecipes;

    @Override
    public void loadRecipes() {

        RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.cactus, 0))
            .itemOutputs(new ItemStack(Blocks.cactus, 64))
            .duration(2 * SECONDS + 10 * TICKS)
            .eut(16)
            .addTo(SWR);

        RA.stdBuilder()
            .itemInputs(new ItemStack(Items.reeds, 0))
            .itemOutputs(new ItemStack(Items.reeds, 64))
            .duration(2 * SECONDS + 10 * TICKS)
            .eut(16)
            .addTo(SWR);
    }
}
