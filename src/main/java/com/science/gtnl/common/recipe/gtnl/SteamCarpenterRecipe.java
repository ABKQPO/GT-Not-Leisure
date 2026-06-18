package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public class SteamCarpenterRecipe implements IRecipePool {

    public RecipeMap<?> LR = RecipeMaps.latheRecipes;
    public RecipeMap<?> SCR = GTNLRecipeMaps.SteamCarpenterRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.stick, Materials.Wood, 4))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Wood, 1))
            .duration(1 * SECONDS)
            .eut(4)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Wood, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iron, 6))
            .itemOutputs(GTNLItemList.IronReinforcedWood.get(1))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Wood, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 6))
            .itemOutputs(GTNLItemList.BronzeReinforcedWood.get(1))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Wood, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 6))
            .itemOutputs(GTNLItemList.SteelReinforcedWood.get(1))
            .duration(5 * SECONDS)
            .eut(16)
            .addTo(SCR);

        registerLogRecipes();
        registerPlankRecipes();
    }

    public void registerLogRecipes() {
        ArrayList<ItemStack> logStacks = OreDictionary.getOres("logWood");
        for (ItemStack logStack : logStacks) {
            registerLogRecipe(logStack);
        }
    }

    public void registerPlankRecipes() {
        Item gtPlank = GTOreDictUnificator.get(OrePrefixes.plate, Materials.Wood, 1L)
            .getItem();
        ArrayList<ItemStack> plankStacks = OreDictionary.getOres("plankWood");
        for (ItemStack plankStack : plankStacks) {
            if (plankStack.getItem() != gtPlank) {
                registerPlankRecipe(plankStack);
            }
        }
    }

    public void registerLogRecipe(ItemStack logStack) {
        ItemStack singleLog = GTUtility.copyAmount(1, logStack);
        if (GTUtility
            .areStacksEqual(GTModHandler.getSmeltingOutput(singleLog, false, null), new ItemStack(Items.coal, 1, 1))) {
            GTModHandler.removeFurnaceSmelting(singleLog);
        }

        ItemStack plankOutput = GTModHandler.getRecipeOutput(singleLog);
        ItemStack steamCarpenterOutput = GTUtility.copyOrNull(plankOutput);
        if (steamCarpenterOutput == null) {
            return;
        }

        steamCarpenterOutput.stackSize = steamCarpenterOutput.stackSize * 3 / 2;
        RecipeBuilder.builder()
            .itemInputs(singleLog)
            .itemOutputs(
                GTUtility.copyOrNull(steamCarpenterOutput),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Wood, 1L))
            .duration(10 * GTRecipeBuilder.SECONDS)
            .eut(8)
            .addTo(SCR);
    }

    public void registerPlankRecipe(ItemStack plankStack) {
        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(1, plankStack), GTUtility.getIntegratedCircuit(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stick, Materials.Wood, 2L))
            .duration(10 * GTRecipeBuilder.TICKS)
            .eut(8)
            .addTo(LR);
        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(1, plankStack), GTUtility.getIntegratedCircuit(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stick, Materials.Wood, 2L))
            .duration(2 * GTRecipeBuilder.SECONDS)
            .eut(8)
            .addTo(SCR);
        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(1, plankStack), GTUtility.getIntegratedCircuit(2))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.slab, Materials.Wood, 2L))
            .duration(2 * GTRecipeBuilder.SECONDS)
            .eut(8)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(8, plankStack), GTUtility.getIntegratedCircuit(8))
            .itemOutputs(new ItemStack(Blocks.chest, 1))
            .duration(2 * GTRecipeBuilder.SECONDS)
            .eut(4)
            .addTo(SCR);
    }

}
