package com.science.gtnl.common.recipe.GregTech;

import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class LaserEngraverRecipes implements IRecipePool {

    final RecipeMap<?> lER = RecipeMaps.laserEngraverRecipes;

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
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTNLItemList.NinefoldInputHatchUHV.get(1), ItemList.Quantum_Tank_IV.get(1))
            .itemOutputs(GTNLItemList.HumongousNinefoldInputHatch.get(1))
            .specialValue(0)
            .duration(200)
            .eut(7864320)
            .addTo(lER);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTNLItemList.NeutroniumWafer.get(1),
                GTUtility.copyAmountUnsafe(0, GTOreDictUnificator.get(OrePrefixes.lens, Materials.EnderPearl, 1L)))
            .itemOutputs(GTUtility.copyAmountUnsafe(64, ItemList.Circuit_Wafer_NAND.get(1)))
            .fluidInputs(Materials.Grade5PurifiedWater.getFluid(100))
            .specialValue(0)
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(lER);

        recipeWithPurifiedWater(
            new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1),
                GTUtility.copyAmountUnsafe(0, GTOreDictUnificator.get(OrePrefixes.lens, Materials.EnderPearl, 1L)) },
            new ItemStack[] { GTUtility.copyAmountUnsafe(64, ItemList.Circuit_Wafer_NAND.get(1)) },
            Materials.Grade5PurifiedWater,
            Materials.Grade6PurifiedWater,
            200,
            100,
            TierEU.RECIPE_IV);

        recipeWithPurifiedWater(
            new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1),
                GTUtility.copyAmountUnsafe(0, GTOreDictUnificator.get(OrePrefixes.lens, Materials.EnderEye, 1L)) },
            new ItemStack[] { GTUtility.copyAmountUnsafe(64, ItemList.Circuit_Wafer_NOR.get(1)) },
            Materials.Grade5PurifiedWater,
            Materials.Grade6PurifiedWater,
            200,
            100,
            TierEU.RECIPE_IV);

        recipeWithPurifiedWater(
            new ItemStack[] { GTNLItemList.NeutroniumWafer.get(1),
                GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.lens, Materials.NetherStar, 1L)) },
            new ItemStack[] { GTUtility.copyAmountUnsafe(16, ItemList.Circuit_Wafer_SoC2.get(1)) },
            Materials.Grade5PurifiedWater,
            Materials.Grade6PurifiedWater,
            500,
            250,
            TierEU.RECIPE_IV);

    }
}
