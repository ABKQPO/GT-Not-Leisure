package com.science.gtnl.common.recipe.gtnl;

import DummyCore.Utils.MiscUtils;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.recipes.ElectrocellGeneratorSpecialValue;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.material.nuclear.MaterialsFluorides;
import gtPlusPlus.core.util.minecraft.ItemUtils;

public class ElectrocellGeneratorRecipes implements IRecipePool {

    public RecipeMap<?> EGR = RecipePool.ElectrocellGeneratorRecipes;
    public ElectrocellGeneratorSpecialValue GENERATOR_EUT = ElectrocellGeneratorSpecialValue.INSTANCE;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, MaterialsUEVplus.Universium, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, MaterialsUEVplus.MagMatter, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Stone, 1))
            .fluidInputs(
                Materials.Lava.getFluid(2147483647),
                Materials.Iron.getMolten(114514),
                MaterialsUEVplus.SpaceTime.getMolten(144),
                Materials.Oxygen.getGas(1919810))
            .fluidOutputs(MaterialsUEVplus.MagnetohydrodynamicallyConstrainedStarMatter.getMolten(1919810))
            .outputChances(5000)
            .eut(0)
            .specialValue(120)
            .metadata(GENERATOR_EUT, Long.MAX_VALUE)
            .duration(5000)
            .addTo(EGR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1),
                ItemUtils.getItemStackOfAmountFromOreDict("dustBerylliumHydroxide", 6))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Zinc, 1))
            .fluidInputs(Materials.Mercury.getFluid(1500))
            .fluidOutputs(MaterialPool.ToxicMercurySludge.getFluidOrGas(2000))
            .outputChances(10000)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 10240L)
            .duration(1020)
            .addTo(EGR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Aluminium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumBisulfate, 4))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumSulfide, 4))
            .fluidInputs(Materials.Sodium.getFluid(4000))
            .fluidOutputs(Materials.Hydrogen.getGas(4000))
            .outputChances(10000)
            .eut(0)
            .specialValue(100)
            .metadata(GENERATOR_EUT, 8192L)
            .duration(600)
            .addTo(EGR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.TungstenSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumBisulfate, 6))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumSulfide, 6))
            .fluidInputs(Materials.Sodium.getFluid(6000))
            .fluidOutputs(Materials.Hydrogen.getGas(6000))
            .outputChances(10000)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 12288L)
            .duration(1000)
            .addTo(EGR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.TungstenSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 8))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 2))
            .fluidInputs(GGMaterial.thoriumNitrate.getFluidOrGas(2000))
            .fluidOutputs(Materials.NitrogenDioxide.getGas(8000))
            .outputChances(3500)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 16384L)
            .duration(1400)
            .addTo(EGR);
    }
}
