package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.metadata.ElectrocellGeneratorMetadata;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.util.minecraft.ItemUtils;

public class ElectrocellGeneratorRecipes implements IRecipePool {

    public RecipeMap<?> EGR = RecipePool.ElectrocellGeneratorRecipes;
    public ElectrocellGeneratorMetadata GENERATOR_EUT = ElectrocellGeneratorMetadata.INSTANCE;

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
            .fluidInputs(Materials.Mercury.getFluid(35))
            .fluidOutputs(MaterialPool.ToxicMercurySludge.getFluidOrGas(40))
            .outputChances(10000)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 10240L)
            .duration(1000)
            .addTo(EGR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Aluminium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumBisulfate, 4))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumSulfide, 24))
            .fluidInputs(Materials.Sodium.getFluid(400))
            .fluidOutputs(Materials.Hydrogen.getGas(120))
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
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumSulfide, 30))
            .fluidInputs(Materials.Sodium.getFluid(400))
            .fluidOutputs(Materials.Hydrogen.getGas(100))
            .outputChances(8000)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 14400L)
            .duration(1000)
            .addTo(EGR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.TungstenSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 7))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 5))
            .fluidInputs(GGMaterial.thoriumNitrate.getFluidOrGas(250))
            .fluidOutputs(Materials.NitricAcid.getFluid(1000))
            .outputChances(8000)
            .eut(0)
            .specialValue(110)
            .metadata(GENERATOR_EUT, 20480L)
            .duration(1500)
            .addTo(EGR);
    }
}
