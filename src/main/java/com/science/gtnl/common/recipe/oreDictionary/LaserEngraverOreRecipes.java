package com.science.gtnl.common.recipe.oreDictionary;

import net.minecraft.item.ItemStack;

import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IOreRecipeRegistrator;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTUtility;

public class LaserEngraverOreRecipes implements IOreRecipeRegistrator {

    public RecipeMap<?> lER = RecipeMaps.laserEngraverRecipes;

    public LaserEngraverOreRecipes() {
        OrePrefixes.crafting.add(this);
    }

    public void recipeWithPurifiedWater(ItemStack[] inputs, ItemStack[] outputs, Materials lowTierWater,
        Materials highTierWater, int duration, int boostedDuration, long eut) {
        GTValues.RA.stdBuilder()
            .itemInputs(inputs)
            .itemOutputs(outputs)
            .fluidInputs(lowTierWater.getFluid(100L))
            .duration(duration)
            .eut(eut)
            .addTo(lER);
        GTValues.RA.stdBuilder()
            .itemInputs(inputs)
            .itemOutputs(outputs)
            .fluidInputs(highTierWater.getFluid(100L))
            .duration(boostedDuration)
            .eut(eut)
            .addTo(lER);
    }

    @Override
    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName,
        ItemStack aStack) {
        switch (aOreDictName) {
            case "craftingLensWhite" -> {

                recipeWithPurifiedWater(
                    new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1), GTUtility.copyAmount(0, aStack) },
                    new ItemStack[] { GTUtility.copyAmountUnsafe(256, ItemList.Circuit_Wafer_CPU.get(1)) },
                    Materials.Grade5PurifiedWater,
                    Materials.Grade6PurifiedWater,
                    50,
                    25,
                    TierEU.RECIPE_IV);
            }
            case "craftingLensGreen" -> {

                recipeWithPurifiedWater(
                    new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1), GTUtility.copyAmount(0, aStack) },
                    new ItemStack[] { GTUtility.copyAmountUnsafe(256, ItemList.Circuit_Wafer_ULPIC.get(1)) },
                    Materials.Grade5PurifiedWater,
                    Materials.Grade6PurifiedWater,
                    50,
                    25,
                    TierEU.RECIPE_IV);
            }
            case "craftingLensYellow" -> {

                recipeWithPurifiedWater(
                    new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1), GTUtility.copyAmount(0, aStack) },
                    new ItemStack[] { GTUtility.copyAmountUnsafe(64, ItemList.Circuit_Wafer_SoC.get(1)) },
                    Materials.Grade5PurifiedWater,
                    Materials.Grade6PurifiedWater,
                    500,
                    250,
                    TierEU.RECIPE_IV);

            }
            case "craftingLensOrange" -> {

                recipeWithPurifiedWater(
                    new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1), GTUtility.copyAmount(0, aStack) },
                    new ItemStack[] { GTUtility.copyAmountUnsafe(256, ItemList.Circuit_Wafer_Simple_SoC.get(1)) },
                    Materials.Grade5PurifiedWater,
                    Materials.Grade6PurifiedWater,
                    50,
                    25,
                    TierEU.RECIPE_IV);

            }
            case "craftingLensBlue" -> {

                recipeWithPurifiedWater(
                    new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1), GTUtility.copyAmountUnsafe(0, aStack) },
                    new ItemStack[] { GTUtility.copyAmountUnsafe(64, ItemList.Circuit_Wafer_PIC.get(1)) },
                    Materials.Grade5PurifiedWater,
                    Materials.Grade6PurifiedWater,
                    200,
                    100,
                    TierEU.RECIPE_IV);

            }
            case "craftingLensCyan" -> {

                recipeWithPurifiedWater(
                    new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1), GTUtility.copyAmountUnsafe(0, aStack) },
                    new ItemStack[] { GTUtility.copyAmountUnsafe(256, ItemList.Circuit_Wafer_Ram.get(1)) },
                    Materials.Grade5PurifiedWater,
                    Materials.Grade6PurifiedWater,
                    50,
                    25,
                    TierEU.RECIPE_IV);

            }
            case "craftingLensBlack" -> {

                recipeWithPurifiedWater(
                    new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1), GTUtility.copyAmountUnsafe(0, aStack) },
                    new ItemStack[] { GTNLItemList.HighlyAdvancedSocWafer.get(1) },
                    Materials.Grade5PurifiedWater,
                    Materials.Grade6PurifiedWater,
                    900,
                    450,
                    TierEU.RECIPE_IV);
            }
        }
    }
}
