package com.science.gtnl.common.recipe.GTNL;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.recipe.IRecipePool;
import com.science.gtnl.common.recipe.RecipeRegister;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialMisc;

public class PlatinumBasedTreatmentRecipes implements IRecipePool {

    final RecipeMap<?> PBTR = RecipeRegister.PlatinumBasedTreatmentRecipes;

    @Override
    public void loadRecipes() {

        // 铂金属粉处理
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(192, WerkstoffLoader.PTMetallicPowder.get(OrePrefixes.dust, 1)),
                GTUtility.copyAmountUnsafe(175, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 13),
                GTUtility.copyAmountUnsafe(137, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Saltpeter, 1)),
                GTUtility.copyAmountUnsafe(148, MaterialMisc.SODIUM_NITRATE.getDust(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(138, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1)),
                GTUtility.copyAmountUnsafe(176, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1)),
                WerkstoffLoader.Rhodium.get(OrePrefixes.dust, 29),
                GTUtility.copyAmountUnsafe(99, WerkstoffLoader.Ruthenium.get(OrePrefixes.dust, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 42),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 5),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 17),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 21),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 21),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 17),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 9))
            .fluidInputs(
                Materials.Oxygen.getGas(488000),
                Materials.Hydrogen.getGas(773800),
                Materials.Ammonia.getGas(42000))
            .fluidOutputs(
                Materials.Ethylene.getGas(176000),
                Materials.Chlorine.getGas(158000),
                FluidRegistry.getFluidStack("steam", 14000))
            .specialValue(0)
            .noOptimize()
            .duration(12000)
            .eut(TierEU.RECIPE_IV)
            .addTo(PBTR);

        // 铂渣粉处理
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(225, WerkstoffLoader.PTResidue.get(OrePrefixes.dust, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 21),
                MaterialMisc.SODIUM_NITRATE.getDust(49),
                GTUtility.copyAmountUnsafe(233, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Saltpeter, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcium, 140))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 36),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconDioxide, 42),
                WerkstoffLoader.Rhodium.get(OrePrefixes.dust, 47),
                WerkstoffLoader.Ruthenium.get(OrePrefixes.dust, 168),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 70),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 7),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 28),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 35),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 35))
            .fluidInputs(
                Materials.Oxygen.getGas(78000),
                Materials.Hydrogen.getGas(1182000),
                Materials.Chlorine.getGas(248000))
            .fluidOutputs(Materials.Ammonia.getGas(46000))
            .specialValue(0)
            .noOptimize()
            .duration(6000)
            .eut(TierEU.RECIPE_LuV)
            .addTo(PBTR);

        // 浸出渣处理
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(100, WerkstoffLoader.LeachResidue.get(OrePrefixes.dust, 1)),
                GTUtility.copyAmountUnsafe(100, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Saltpeter, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcium, 60))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 40),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconDioxide, 18),
                WerkstoffLoader.Ruthenium.get(OrePrefixes.dust, 72),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 30),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 15),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 15))
            .fluidInputs(
                Materials.Oxygen.getGas(10000),
                Materials.Hydrogen.getGas(497000),
                Materials.Chlorine.getGas(127000))
            .fluidOutputs()
            .specialValue(0)
            .noOptimize()
            .duration(6000)
            .eut(TierEU.RECIPE_LuV)
            .addTo(PBTR);

        // 稀有金属渣粉处理
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(40, WerkstoffLoader.IrOsLeachResidue.get(OrePrefixes.dust, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcium, 40))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconDioxide, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 10))
            .fluidInputs(Materials.Hydrogen.getGas(30000), Materials.Chlorine.getGas(48000))
            .fluidOutputs()
            .specialValue(0)
            .noOptimize()
            .duration(1500)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(PBTR);

        // 铱金属渣粉处理
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(20, WerkstoffLoader.IrLeachResidue.get(OrePrefixes.dust, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcium, 40))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconDioxide, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 10))
            .fluidInputs(Materials.Hydrogen.getGas(8000), Materials.Chlorine.getGas(40000))
            .fluidOutputs()
            .specialValue(0)
            .noOptimize()
            .duration(1500)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(PBTR);

        // 钯金属粉处理
        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmountUnsafe(90, WerkstoffLoader.PDMetallicPowder.get(OrePrefixes.dust, 1)))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 64))
            .fluidInputs(
                Materials.Hydrogen.getGas(130000),
                Materials.CarbonMonoxide.getGas(64000),
                Materials.Ammonia.getGas(224000))
            .fluidOutputs(Materials.Ethylene.getGas(64000))
            .specialValue(0)
            .noOptimize()
            .duration(2400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(PBTR);

        // 富钯氨处理
        RecipeBuilder.builder()
            .itemInputs()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 64))
            .fluidInputs(
                WerkstoffLoader.PDAmmonia.getFluidOrGas(90000),
                Materials.Hydrogen.getGas(130000),
                Materials.CarbonMonoxide.getGas(64000),
                Materials.Ammonia.getGas(134000))
            .fluidOutputs(Materials.Ethylene.getGas(64000))
            .specialValue(0)
            .noOptimize()
            .duration(2400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(PBTR);
    }
}
