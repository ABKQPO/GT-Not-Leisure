package com.science.gtnl.common.recipe.GTNL;

import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.common.recipe.IRecipePool;
import com.science.gtnl.common.recipe.RecipeRegister;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.item.chemistry.CoalTar;
import gtPlusPlus.core.item.chemistry.GenericChem;
import gtPlusPlus.core.item.chemistry.RocketFuels;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.material.nuclear.MaterialsNuclides;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class FuelRefiningComplexRecipes implements IRecipePool {

    final RecipeMap<?> FCR = RecipeRegister.FuelRefiningComplexRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 44L))
            .fluidInputs(
                Materials.Oxygen.getGas(12000),
                Materials.Nitrogen.getGas(8000),
                Materials.Naphtha.getFluid(16000),
                Materials.Gas.getGas(2000),
                Materials.Toluene.getFluid(4000),
                Materials.Octane.getFluid(3000))
            .fluidOutputs(Materials.GasolinePremium.getFluid(50000))
            .noOptimize()
            .duration(1200)
            .metadata(COIL_HEAT, 6800)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 64L))
            .fluidInputs(new FluidStack(CoalTar.Coal_Gas, 80000), Materials.Oxygen.getGas(10000))
            .fluidOutputs(FluidUtils.getFluidStack(RocketFuels.RP1_Plus_Liquid_Oxygen, 4000))
            .noOptimize()
            .duration(1200)
            .metadata(COIL_HEAT, 6300)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 32L))
            .fluidInputs(
                Materials.Hydrogen.getGas(12000),
                Materials.Oxygen.getGas(8000),
                Materials.Nitrogen.getGas(10000),
                new FluidStack(GenericChem.Hydrogen_Peroxide, 4000))
            .fluidOutputs(FluidUtils.getFluidStack(RocketFuels.Dense_Hydrazine_Mix, 4000))
            .noOptimize()
            .duration(800)
            .metadata(COIL_HEAT, 7400)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 16L))
            .fluidInputs(
                Materials.Hydrogen.getGas(10000),
                Materials.Oxygen.getGas(5000),
                Materials.LightFuel.getFluid(10000),
                Materials.HeavyFuel.getFluid(2000),
                Materials.NitrationMixture.getFluid(4000))
            .fluidOutputs(Materials.NitroFuel.getFluid(18000))
            .noOptimize()
            .duration(400)
            .metadata(COIL_HEAT, 4800)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 12L))
            .fluidInputs(
                Materials.Hydrogen.getGas(10000),
                Materials.Oxygen.getGas(5000),
                Materials.BioDiesel.getFluid(16000),
                Materials.NitrationMixture.getFluid(4000))
            .fluidOutputs(Materials.NitroFuel.getFluid(14000))
            .noOptimize()
            .duration(600)
            .metadata(COIL_HEAT, 3600)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 11L))
            .fluidInputs(
                Materials.Naphtha.getFluid(16000),
                Materials.Gas.getGas(2000),
                Materials.Hydrogen.getGas(18000),
                Materials.Oxygen.getGas(2000))
            .fluidOutputs(Materials.GasolineRegular.getFluid(22000))
            .noOptimize()
            .duration(400)
            .metadata(COIL_HEAT, 3200)
            .eut(TierEU.RECIPE_HV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 6L))
            .fluidInputs(
                Materials.Naphtha.getFluid(16000),
                Materials.Gas.getGas(2000),
                Materials.Hydrogen.getGas(16000),
                Materials.Oxygen.getGas(3000))
            .fluidOutputs(Materials.GasolineRegular.getFluid(20000))
            .noOptimize()
            .duration(400)
            .metadata(COIL_HEAT, 3200)
            .eut(TierEU.RECIPE_HV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 12L))
            .fluidInputs(
                Materials.Hydrogen.getGas(14000),
                Materials.Nitrogen.getGas(6000),
                Materials.NitricAcid.getFluid(3000),
                new FluidStack(GenericChem.Hydrogen_Peroxide, 2000))
            .fluidOutputs(Materials.GasolineRegular.getFluid(4000))
            .noOptimize()
            .duration(1200)
            .metadata(COIL_HEAT, 8100)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(5),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 13L))
            .fluidInputs(
                Materials.Hydrogen.getGas(15000),
                Materials.Nitrogen.getGas(7000),
                Materials.NitricAcid.getFluid(3000),
                Materials.Oxygen.getGas(1000),
                new FluidStack(GenericChem.Hydrogen_Peroxide, 2000))
            .fluidOutputs(Materials.GasolineRegular.getFluid(4000))
            .noOptimize()
            .duration(1200)
            .metadata(COIL_HEAT, 9000)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 64L))
            .fluidInputs(
                Materials.Lithium.getMolten(586),
                Materials.Draconium.getMolten(288),
                Materials.Mercury.getFluid(1000))
            .fluidOutputs(GGMaterial.thoriumBasedLiquidFuel.getFluidOrGas(4000))
            .noOptimize()
            .duration(1200)
            .metadata(COIL_HEAT, 8500)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 36L))
            .fluidInputs(
                Materials.Potassium.getMolten(1152),
                Materials.Quantium.getMolten(576),
                Materials.Radon.getGas(1000))
            .fluidOutputs(GGMaterial.uraniumBasedLiquidFuel.getFluidOrGas(1000))
            .noOptimize()
            .duration(800)
            .metadata(COIL_HEAT, 9200)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 45L))
            .fluidInputs(
                Materials.Neutronium.getMolten(1152),
                Materials.Caesium.getMolten(2304),
                Materials.Naquadah.getGas(288))
            .fluidOutputs(GGMaterial.plutoniumBasedLiquidFuel.getFluidOrGas(1000))
            .noOptimize()
            .duration(400)
            .metadata(COIL_HEAT, 9900)
            .eut(TierEU.RECIPE_LuV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 42L))
            .fluidInputs(
                Materials.Naphtha.getFluid(40000),
                Materials.Gas.getGas(40000),
                Materials.LightFuel.getFluid(3000),
                Materials.Nitrogen.getGas(9000),
                Materials.Hydrogen.getGas(40000),
                Materials.Oxygen.getGas(17000))
            .fluidOutputs(GGMaterial.ironedFuel.getFluidOrGas(50000))
            .noOptimize()
            .duration(600)
            .metadata(COIL_HEAT, 8200)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 40L))
            .fluidInputs(
                new FluidStack(RocketFuels.Kerosene, 40000),
                Materials.Naphtha.getFluid(3000),
                Materials.Gas.getGas(40000),
                Materials.Nitrogen.getGas(1000),
                Materials.Hydrogen.getGas(59000),
                Materials.Oxygen.getGas(12000))
            .fluidOutputs(GGMaterial.ironedKerosene.getFluidOrGas(44000))
            .noOptimize()
            .duration(600)
            .metadata(COIL_HEAT, 4800)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1L))
            .fluidInputs(
                Materials.Fluorine.getGas(7000),
                Materials.Lithium.getMolten(1008),
                Materials.Beryllium.getMolten(144))
            .fluidOutputs(MaterialsNuclides.LiFBeF2UF4.getFluidStack(1000))
            .noOptimize()
            .duration(200)
            .metadata(COIL_HEAT, 5800)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1L),
                MaterialsElements.getInstance().ZIRCONIUM.getDust(1))
            .fluidInputs(
                Materials.Fluorine.getGas(7000),
                Materials.Lithium.getMolten(1008),
                Materials.Beryllium.getMolten(144))
            .fluidOutputs(MaterialsNuclides.LiFBeF2ZrF4U235.getFluidStack(1000))
            .noOptimize()
            .duration(400)
            .metadata(COIL_HEAT, 6100)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1L),
                MaterialsElements.getInstance().ZIRCONIUM.getDust(1))
            .fluidInputs(
                Materials.Fluorine.getGas(11000),
                Materials.Lithium.getMolten(1008),
                Materials.Beryllium.getMolten(144))
            .fluidOutputs(MaterialsNuclides.LiFBeF2ZrF4UF4.getFluidStack(1000))
            .noOptimize()
            .duration(200)
            .metadata(COIL_HEAT, 6400)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 1L))
            .fluidInputs(
                Materials.Fluorine.getGas(11000),
                Materials.Lithium.getMolten(1008),
                Materials.Beryllium.getMolten(144))
            .fluidOutputs(MaterialsNuclides.LiFBeF2ThF4UF4.getFluidStack(1000))
            .noOptimize()
            .duration(400)
            .metadata(COIL_HEAT, 6700)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);
    }
}
