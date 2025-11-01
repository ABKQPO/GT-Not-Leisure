package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.enums.Mods.*;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class CuttingRecipes implements IRecipePool {

    public RecipeMap<?> CR = RecipeMaps.cutterRecipes;

    public void recipeWithPurifiedWater(ItemStack[] inputs, ItemStack[] outputs, Materials lowTierWater,
        Materials highTierWater, int duration, int boostedDuration, long eut) {
        GTValues.RA.stdBuilder()
            .itemInputs(inputs)
            .itemOutputs(outputs)
            .fluidInputs(lowTierWater.getFluid(100L))
            .duration(duration)
            .eut(eut)
            .addTo(CR);
        GTValues.RA.stdBuilder()
            .itemInputs(inputs)
            .itemOutputs(outputs)
            .fluidInputs(highTierWater.getFluid(100L))
            .duration(boostedDuration)
            .eut(eut)
            .addTo(CR);
    }

    public void registerCutterRecipes(ItemStack[] input, ItemStack[] outputItem, int lubricantAmount,
        int distilledWaterAmount, int waterAmount, int duration, long eut) {
        GTValues.RA.stdBuilder()
            .itemInputs(input)
            .itemOutputs(outputItem)
            .fluidInputs(Materials.Lubricant.getFluid(lubricantAmount))
            .duration(duration)
            .eut(eut)
            .addTo(CR);

        GTValues.RA.stdBuilder()
            .itemInputs(input)
            .itemOutputs(outputItem)
            .fluidInputs(GTModHandler.getDistilledWater(distilledWaterAmount))
            .duration(duration)
            .eut(eut)
            .addTo(CR);

        GTValues.RA.stdBuilder()
            .itemInputs(input)
            .itemOutputs(outputItem)
            .fluidInputs(Materials.Water.getFluid(waterAmount))
            .duration(duration)
            .eut(eut)
            .addTo(CR);
    }

    @Override
    public void loadRecipes() {

        recipeWithPurifiedWater(
            new ItemStack[] { GTNLItemList.NeutroniumBoule.get(1) },
            new ItemStack[] { GTNLItemList.NeutroniumWafer.get(64), GTNLItemList.NeutroniumWafer.get(64),
                GTNLItemList.NeutroniumWafer.get(16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconSG, 64) },
            Materials.Grade3PurifiedWater,
            Materials.Grade4PurifiedWater,
            2400,
            1200,
            TierEU.RECIPE_IV);

        recipeWithPurifiedWater(
            new ItemStack[] { GTNLItemList.HighlyAdvancedSocWafer.get(1) },
            new ItemStack[] { GTNLItemList.HighlyAdvancedSoc.get(6) },
            Materials.Grade5PurifiedWater,
            Materials.Grade6PurifiedWater,
            900,
            450,
            TierEU.RECIPE_IV);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Circuit_Silicon_Wafer7.get(1))
            .itemOutputs(ItemList.Circuit_Chip_Optical.get(16))
            .fluidInputs(Materials.Grade6PurifiedWater.getFluid(280L))
            .duration(560)
            .eut(TierEU.RECIPE_UHV)
            .addTo(CR);

        registerCutterRecipes(
            new ItemStack[] { GTModHandler.getModItem(IndustrialCraft2.ID, "blockRubWood", 1) },
            new ItemStack[] { new ItemStack(Blocks.planks, 6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Wood, 1) },
            1,
            3,
            5,
            200,
            TierEU.RECIPE_ULV);

    }
}
