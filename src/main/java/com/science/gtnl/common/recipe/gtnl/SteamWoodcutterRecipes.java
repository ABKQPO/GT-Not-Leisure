package com.science.gtnl.common.recipe.gtnl;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class SteamWoodcutterRecipes implements IRecipePool {

    public RecipeMap<?> SWR = GTNLRecipeMaps.WoodcutterRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.cactus, 0))
            .itemOutputs(new ItemStack(Blocks.cactus, 48))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(SWR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.reeds, 0))
            .itemOutputs(new ItemStack(Items.reeds, 48))
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(SWR);
    }

    // A steam woodcutter recipe for every tree GT's tree farm learns.
    public static void addFromTreeFarm(ItemStack saplingIn, ItemStack log, ItemStack saplingOut, ItemStack leaves,
        ItemStack fruit) {

        if (log == null) return;

        ItemStack newSaplingIn = saplingIn.copy();
        newSaplingIn.stackSize = 0;

        List<ItemStack> outputs = new ObjectArrayList<>();
        IntArrayList chances = new IntArrayList();

        ItemStack newLogOut = log.copy();
        newLogOut.stackSize = 48;
        outputs.add(newLogOut);
        chances.add(10000);

        if (saplingOut != null) {
            ItemStack newSaplingOut = saplingOut.copy();
            newSaplingOut.stackSize = 2;
            outputs.add(newSaplingOut);
            chances.add(1000);
        }
        if (leaves != null) {
            ItemStack newLeavesOut = leaves.copy();
            newLeavesOut.stackSize = 6;
            outputs.add(newLeavesOut);
            chances.add(1500);
        }
        if (fruit != null) {
            ItemStack newFruitOut = fruit.copy();
            newFruitOut.stackSize = 4;
            outputs.add(newFruitOut);
            chances.add(3000);
        }

        RecipeBuilder.builder()
            .itemInputs(newSaplingIn)
            .itemOutputs(outputs.toArray(new ItemStack[0]))
            .outputChances(chances.toIntArray())
            .duration(40)
            .eut(TierEU.RECIPE_LV)
            .addTo(GTNLRecipeMaps.WoodcutterRecipes);
    }
}
