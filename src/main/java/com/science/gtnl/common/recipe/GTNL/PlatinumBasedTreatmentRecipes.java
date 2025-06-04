package com.science.gtnl.common.recipe.GTNL;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsElements;

public class PlatinumBasedTreatmentRecipes implements IRecipePool {

    final RecipeMap<?> PBTR = RecipeRegister.PlatinumBasedTreatmentRecipes;

    @Override
    public void loadRecipes() {

        // 铂金属粉处理
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(192, WerkstoffLoader.PTMetallicPowder.get(OrePrefixes.dust, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 13),
                GTUtility.copyAmountUnsafe(137, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Saltpeter, 1)),
                GTUtility.copyAmountUnsafe(148, MaterialMisc.SODIUM_NITRATE.getDust(1)))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(138, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1)),
                GTUtility.copyAmountUnsafe(176, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1)),
                WerkstoffLoader.Rhodium.get(OrePrefixes.dust, 29),
                GTUtility.copyAmountUnsafe(99, MaterialsElements.getInstance().RUTHENIUM.getDust(1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 42),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 5),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 17),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 21),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 21),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 17),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 9))
            .fluidInputs(
                Materials.Oxygen.getGas(313000),
                Materials.Hydrogen.getGas(773800),
                Materials.Ammonia.getGas(42000))
            .fluidOutputs(
                Materials.Ethylene.getGas(176000),
                Materials.Chlorine.getGas(158000),
                FluidRegistry.getFluidStack("steam", 14000))
            .specialValue(0)
            .duration(12000)
            .eut(TierEU.RECIPE_IV)
            .addTo(PBTR);

        // 铂渣粉处理
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(96, WerkstoffLoader.PTResidue.get(OrePrefixes.dust, 1)),
                MaterialMisc.SODIUM_NITRATE.getDust(103),
                GTUtility.copyAmountUnsafe(100, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Saltpeter, 1)))
            .itemOutputs(
                WerkstoffLoader.Rhodium.get(OrePrefixes.dust, 20),
                GTUtility.copyAmountUnsafe(72, MaterialsElements.getInstance().RUTHENIUM.getDust(1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 30),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 15),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 15),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Salt, 15))
            .fluidInputs(Materials.Oxygen.getGas(2000), Materials.Hydrogen.getGas(300000))
            .fluidOutputs(
                Materials.Ammonia.getGas(20000),
                Materials.Chlorine.getGas(183000),
                FluidRegistry.getFluidStack("steam", 10000))
            .specialValue(0)
            .duration(6000)
            .eut(TierEU.RECIPE_LuV)
            .addTo(PBTR);

        // 浸出渣处理
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(64, WerkstoffLoader.LeachResidue.get(OrePrefixes.dust, 1)),
                GTUtility.copyAmountUnsafe(64, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Saltpeter, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Salt, 25))
            .itemOutputs(
                MaterialsElements.getInstance().RUTHENIUM.getDust(46),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 10))
            .fluidInputs(Materials.Hydrogen.getGas(187000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("steam", 6000),
                Materials.Chlorine.getGas(132000),
                Materials.Oxygen.getGas(8000))
            .specialValue(0)
            .duration(6000)
            .eut(TierEU.RECIPE_LuV)
            .addTo(PBTR);

        // 稀有金属渣粉处理
        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmountUnsafe(20, WerkstoffLoader.IrOsLeachResidue.get(OrePrefixes.dust, 1)))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 5),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 5),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 4))
            .fluidInputs(Materials.Hydrogen.getGas(25000))
            .fluidOutputs(Materials.Oxygen.getGas(4000), Materials.Chlorine.getGas(2000))
            .specialValue(0)
            .duration(1500)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(PBTR);

        // 铱金属渣粉处理
        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmountUnsafe(20, WerkstoffLoader.IrLeachResidue.get(OrePrefixes.dust, 1)))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 10))
            .fluidInputs(Materials.Hydrogen.getGas(8000), Materials.Chlorine.getGas(40000))
            .fluidOutputs(Materials.Oxygen.getGas(8000))
            .specialValue(0)
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
            .duration(2400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(PBTR);

        // 粗制铑金属粉处理
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(100, WerkstoffLoader.CrudeRhMetall.get(OrePrefixes.dust, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 50))
            .itemOutputs(WerkstoffLoader.Rhodium.get(OrePrefixes.dust, 57))
            .fluidInputs(
                Materials.Nitrogen.getGas(1000),
                Materials.Oxygen.getGas(180000),
                Materials.Chlorine.getGas(90000))
            .fluidOutputs(Materials.Hydrogen.getGas(71000))
            .specialValue(0)
            .duration(6000)
            .eut(TierEU.RECIPE_LuV)
            .addTo(PBTR);
    }
}
