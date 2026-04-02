package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;
import com.science.gtnl.utils.recipes.metadata.FuelRefiningMetadata;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.material.nuclear.MaterialsNuclides;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class FuelRefiningComplexRecipes implements IRecipePool {

    public FuelRefiningMetadata FUEL_REFINING_TIER = FuelRefiningMetadata.INSTANCE;
    public RecipeMap<?> FCR = GTNLRecipeMaps.FuelRefiningComplexRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 44L))
            .fluidInputs(
                Materials.Oxygen.getGas(12000),
                Materials.Nitrogen.getGas(8000),
                Materials.Naphtha.getFluid(16000),
                Materials.Gas.getGas(2000),
                Materials.Toluene.getFluid(4000),
                Materials.Octane.getFluid(3000))
            .fluidOutputs(Materials.GasolinePremium.getFluid(50000))
            .duration(1200)
            .metadata(COIL_HEAT, 6800)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 64L))
            .fluidInputs(new FluidStack(GTPPFluids.CoalGas, 80000), Materials.Oxygen.getGas(10000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.RP1RocketFuel, 4000))
            .duration(1200)
            .metadata(COIL_HEAT, 6300)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 32L))
            .fluidInputs(
                Materials.Hydrogen.getGas(12000),
                Materials.Oxygen.getGas(8000),
                Materials.Nitrogen.getGas(10000),
                new FluidStack(GTPPFluids.HydrogenPeroxide, 4000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.DenseHydrazineFuelMixture, 4000))
            .duration(800)
            .metadata(COIL_HEAT, 7400)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 16L))
            .fluidInputs(
                Materials.Hydrogen.getGas(10000),
                Materials.Oxygen.getGas(5000),
                Materials.LightFuel.getFluid(10000),
                Materials.HeavyFuel.getFluid(2000),
                Materials.NitrationMixture.getFluid(4000))
            .fluidOutputs(Materials.NitroFuel.getFluid(18000))
            .duration(400)
            .metadata(COIL_HEAT, 4800)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 12L))
            .fluidInputs(
                Materials.Hydrogen.getGas(10000),
                Materials.Oxygen.getGas(5000),
                Materials.BioDiesel.getFluid(16000),
                Materials.NitrationMixture.getFluid(4000))
            .fluidOutputs(Materials.NitroFuel.getFluid(14000))
            .duration(600)
            .metadata(COIL_HEAT, 3600)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 11L))
            .fluidInputs(
                Materials.Naphtha.getFluid(16000),
                Materials.Gas.getGas(2000),
                Materials.Hydrogen.getGas(18000),
                Materials.Oxygen.getGas(2000))
            .fluidOutputs(Materials.GasolineRegular.getFluid(22000))
            .duration(400)
            .metadata(COIL_HEAT, 3200)
            .eut(TierEU.RECIPE_HV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 6L))
            .fluidInputs(
                Materials.Naphtha.getFluid(16000),
                Materials.Gas.getGas(2000),
                Materials.Hydrogen.getGas(16000),
                Materials.Oxygen.getGas(3000))
            .fluidOutputs(GGMaterial.ethanolGasoline.getFluidOrGas(20000))
            .duration(400)
            .metadata(COIL_HEAT, 3200)
            .eut(TierEU.RECIPE_HV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 12L))
            .fluidInputs(
                Materials.Hydrogen.getGas(14000),
                Materials.Nitrogen.getGas(6000),
                Materials.NitricAcid.getFluid(3000),
                new FluidStack(GTPPFluids.HydrogenPeroxide, 2000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.CN3H7O3RocketFuel, 4000))
            .duration(1200)
            .metadata(COIL_HEAT, 8100)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(5),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 13L))
            .fluidInputs(
                Materials.Hydrogen.getGas(15000),
                Materials.Nitrogen.getGas(7000),
                Materials.NitricAcid.getFluid(3000),
                Materials.Oxygen.getGas(1000),
                new FluidStack(GTPPFluids.HydrogenPeroxide, 2000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.H8N4C2O4RocketFuel, 5000))
            .duration(1200)
            .metadata(COIL_HEAT, 9000)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 64L))
            .fluidInputs(
                Materials.Lithium.getMolten(586),
                Materials.Draconium.getMolten(288),
                Materials.Mercury.getFluid(1000))
            .fluidOutputs(GGMaterial.thoriumBasedLiquidFuel.getFluidOrGas(4000))
            .duration(1200)
            .metadata(COIL_HEAT, 8500)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 36L))
            .fluidInputs(
                Materials.Potassium.getMolten(1152),
                Materials.Quantium.getMolten(576),
                Materials.Radon.getGas(1000))
            .fluidOutputs(GGMaterial.uraniumBasedLiquidFuel.getFluidOrGas(1000))
            .duration(800)
            .metadata(COIL_HEAT, 9200)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 45L))
            .fluidInputs(
                Materials.Neutronium.getMolten(1152),
                Materials.Caesium.getMolten(2304),
                Materials.Naquadah.getMolten(288))
            .fluidOutputs(GGMaterial.plutoniumBasedLiquidFuel.getFluidOrGas(1000))
            .duration(400)
            .metadata(COIL_HEAT, 9900)
            .eut(TierEU.RECIPE_LuV)
            .addTo(FCR);

        RecipeBuilder.builder()
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
            .duration(600)
            .metadata(COIL_HEAT, 8200)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 40L))
            .fluidInputs(
                new FluidStack(GTPPFluids.Kerosene, 40000),
                Materials.Naphtha.getFluid(3000),
                Materials.Gas.getGas(40000),
                Materials.Nitrogen.getGas(1000),
                Materials.Hydrogen.getGas(59000),
                Materials.Oxygen.getGas(12000))
            .fluidOutputs(GGMaterial.ironedKerosene.getFluidOrGas(44000))
            .duration(600)
            .metadata(COIL_HEAT, 4800)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1L))
            .fluidInputs(
                Materials.Fluorine.getGas(7000),
                Materials.Lithium.getMolten(1008),
                Materials.Beryllium.getMolten(144))
            .fluidOutputs(MaterialsNuclides.LiFBeF2UF4.getFluidStack(1000))
            .duration(200)
            .metadata(COIL_HEAT, 5800)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1L),
                MaterialsElements.getInstance().ZIRCONIUM.getDust(1))
            .fluidInputs(
                Materials.Fluorine.getGas(7000),
                Materials.Lithium.getMolten(1008),
                Materials.Beryllium.getMolten(144))
            .fluidOutputs(MaterialsNuclides.LiFBeF2ZrF4U235.getFluidStack(1000))
            .duration(400)
            .metadata(COIL_HEAT, 6100)
            .eut(TierEU.RECIPE_EV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1L),
                MaterialsElements.getInstance().ZIRCONIUM.getDust(1))
            .fluidInputs(
                Materials.Fluorine.getGas(11000),
                Materials.Lithium.getMolten(1008),
                Materials.Beryllium.getMolten(144))
            .fluidOutputs(MaterialsNuclides.LiFBeF2ZrF4UF4.getFluidStack(1000))
            .duration(200)
            .metadata(COIL_HEAT, 6400)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 1L))
            .fluidInputs(
                Materials.Fluorine.getGas(11000),
                Materials.Lithium.getMolten(1008),
                Materials.Beryllium.getMolten(144))
            .fluidOutputs(MaterialsNuclides.LiFBeF2ThF4UF4.getFluidStack(1000))
            .duration(400)
            .metadata(COIL_HEAT, 6700)
            .eut(TierEU.RECIPE_IV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .fluidInputs(
                GGMaterial.lightNaquadahFuel.getFluidOrGas(2875),
                GGMaterial.heavyNaquadahFuel.getFluidOrGas(1375),
                GGMaterial.atomicSeparationCatalyst.getMolten(750),
                Materials.Duranium.getMolten(144))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(1250))
            .duration(100)
            .metadata(COIL_HEAT, 10800)
            .metadata(FUEL_REFINING_TIER, 1)
            .eut(TierEU.RECIPE_UHV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Gravistar.get(2))
            .fluidInputs(
                GGMaterial.lightNaquadahFuel.getFluidOrGas(2875),
                GGMaterial.heavyNaquadahFuel.getFluidOrGas(1375),
                GGMaterial.atomicSeparationCatalyst.getMolten(750),
                GGMaterial.naquadahGas.getFluidOrGas(5000),
                Materials.Americium.getPlasma(288))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(1250))
            .duration(120)
            .metadata(COIL_HEAT, 11500)
            .metadata(FUEL_REFINING_TIER, 1)
            .eut(TierEU.RECIPE_UEV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.dust, 32))
            .fluidInputs(
                GGMaterial.lightNaquadahFuel.getFluidOrGas(11500),
                GGMaterial.heavyNaquadahFuel.getFluidOrGas(5500),
                GGMaterial.atomicSeparationCatalyst.getMolten(3000),
                MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluidStack(5184),
                GGMaterial.uraniumBasedLiquidFuelExcited.getFluidOrGas(1500),
                GGMaterial.plutoniumBasedLiquidFuelExcited.getFluidOrGas(750))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIII.getFluidOrGas(1000))
            .duration(140)
            .metadata(COIL_HEAT, 12600)
            .metadata(FUEL_REFINING_TIER, 2)
            .eut(TierEU.RECIPE_UIV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.block, Materials.DraconiumAwakened, 64))
            .fluidInputs(
                GGMaterial.lightNaquadahFuel.getFluidOrGas(46000),
                GGMaterial.heavyNaquadahFuel.getFluidOrGas(22000),
                GGMaterial.atomicSeparationCatalyst.getMolten(12000),
                MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluidStack(19440),
                GGMaterial.orundum.getMolten(9216),
                MaterialsElements.STANDALONE.HYPOGEN.getFluidStack(480))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIV.getFluidOrGas(1000))
            .duration(160)
            .metadata(COIL_HEAT, 13500)
            .metadata(FUEL_REFINING_TIER, 3)
            .eut(TierEU.RECIPE_UMV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, MaterialsUEVplus.TranscendentMetal, 20),
                GTOreDictUnificator.get(OrePrefixes.plate, MaterialsUEVplus.HotProtoHalkonite, 16))
            .fluidInputs(
                GGMaterial.lightNaquadahFuel.getFluidOrGas(184000),
                GGMaterial.heavyNaquadahFuel.getFluidOrGas(88000),
                GGMaterial.atomicSeparationCatalyst.getMolten(48000),
                Materials.Infinity.getMolten(2304),
                MaterialsUEVplus.SpaceTime.getMolten(144))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkV.getFluidOrGas(1000))
            .duration(180)
            .metadata(COIL_HEAT, 13500)
            .metadata(FUEL_REFINING_TIER, 4)
            .eut(TierEU.RECIPE_UXV)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(
                GGMaterial.lightNaquadahFuel.getFluidOrGas(368000),
                GGMaterial.heavyNaquadahFuel.getFluidOrGas(176000),
                GGMaterial.atomicSeparationCatalyst.getMolten(96000),
                MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFluidStack(5760),
                MaterialsUEVplus.Mellion.getMolten(4608),
                GGMaterial.shirabon.getMolten(288),
                MaterialsUEVplus.RawStarMatter.getFluid(80))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkVI.getFluidOrGas(1500))
            .duration(200)
            .metadata(COIL_HEAT, 13500)
            .metadata(FUEL_REFINING_TIER, 4)
            .eut(TierEU.RECIPE_MAX)
            .addTo(FCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(
                GGMaterial.lightNaquadahFuel.getFluidOrGas(368000),
                GGMaterial.heavyNaquadahFuel.getFluidOrGas(176000),
                GGMaterial.atomicSeparationCatalyst.getMolten(96000),
                MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFluidStack(5760),
                MaterialsUEVplus.Mellion.getMolten(4608),
                GGMaterial.shirabon.getMolten(288),
                MaterialsUEVplus.RawStarMatter.getFluid(80),
                MaterialsUEVplus.Universium.getMolten(18))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkVI.getFluidOrGas(3750))
            .duration(100)
            .metadata(COIL_HEAT, 13500)
            .metadata(FUEL_REFINING_TIER, 4)
            .eut(TierEU.RECIPE_MAX)
            .addTo(FCR);
    }
}
