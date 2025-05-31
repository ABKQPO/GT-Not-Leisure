package com.science.gtnl.common.recipe.GTNL;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;

public class SmeltingMixingFurnaceRecipes implements IRecipePool {

    final RecipeMap<?> SMFR = RecipeRegister.SmeltingMixingFurnaceRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .fluidInputs(new FluidStack(MaterialsElements.getInstance().ZIRCONIUM.getFluid(), 144))
            .fluidOutputs(MaterialsAlloy.ZIRCONIUM_CARBIDE.getFluidStack(288))
            .specialValue(0)
            .duration(200)
            .eut(15)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Gold.getMolten(144 * 7), Materials.Copper.getMolten(144 * 3))
            .fluidOutputs(MaterialsAlloy.TUMBAGA.getFluidStack(1440))
            .specialValue(0)
            .duration(200)
            .eut(15)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .fluidInputs(Materials.Silicon.getMolten(144))
            .fluidOutputs(MaterialsAlloy.SILICON_CARBIDE.getFluidStack(288))
            .specialValue(0)
            .duration(200)
            .eut(15)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(
                Materials.Lead.getMolten(144 * 4),
                Materials.Copper.getMolten(144 * 3),
                Materials.Tin.getMolten(144 * 3))
            .fluidOutputs(MaterialsAlloy.POTIN.getFluidStack(1440))
            .specialValue(0)
            .duration(200 * 2)
            .eut(15)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 3))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 23),
                Materials.Aluminium.getMolten(144),
                Materials.Chrome.getMolten(144),
                Materials.Nickel.getMolten(144 * 5),
                Materials.Silicon.getMolten(144 * 12))
            .fluidOutputs(MaterialsAlloy.EGLIN_STEEL.getFluidStack(6912))
            .specialValue(0)
            .duration(900 * 3)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(8))
            .fluidInputs(
                Materials.Oxygen.getGas(77000),
                Materials.Iron.getMolten(144 * 75),
                Materials.Silicon.getMolten(144 * 25),
                Materials.Aluminium.getMolten(144 * 18),
                Materials.Potassium.getMolten(144 * 45),
                Materials.Calcium.getMolten(144 * 30),
                Materials.Ytterbium.getMolten(144 * 15),
                Materials.Sodium.getFluid(30000))
            .fluidOutputs(new FluidStack(MaterialsElements.STANDALONE.GRANITE.getFluid(), 62640))
            .specialValue(0)
            .duration(200 * 15)
            .eut(15)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Tin.getMolten(144 * 5),
                Materials.Lead.getMolten(144 * 36),
                Materials.Antimony.getMolten(144 * 8),
                Materials.Arsenic.getMolten(144))
            .fluidOutputs(MaterialsAlloy.BABBIT_ALLOY.getFluidStack(7200))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 12))
            .fluidInputs(Materials.Lead.getMolten(144 * 3), Materials.Manganese.getMolten(144 * 5))
            .fluidOutputs(new FluidStack(MaterialsElements.STANDALONE.BLACK_METAL.getFluid(), 2880))
            .specialValue(0)
            .duration(400)
            .eut(60)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Zinc.getMolten(144), Materials.Thorium.getMolten(144))
            .fluidOutputs(GGMaterial.zincThoriumAlloy.getMolten(288))
            .specialValue(0)
            .duration(280)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .fluidInputs(Materials.Niobium.getMolten(144))
            .fluidOutputs(MaterialsAlloy.NIOBIUM_CARBIDE.getFluidStack(288))
            .specialValue(0)
            .duration(400)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 6),
                Materials.Nickel.getMolten(144),
                Materials.Manganese.getMolten(144),
                Materials.Chrome.getMolten(144))
            .fluidOutputs(Materials.StainlessSteel.getMolten(1296))
            .specialValue(0)
            .duration(900)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Oxygen.getGas(16000),
                Materials.Iron.getMolten(144 * 16),
                Materials.Molybdenum.getMolten(144),
                Materials.Titanium.getMolten(144),
                Materials.Nickel.getMolten(144 * 4),
                Materials.Cobalt.getMolten(144 * 2))
            .fluidOutputs(MaterialsAlloy.MARAGING250.getFluidStack(3456))
            .specialValue(0)
            .duration(400)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Nickel.getMolten(144 * 251),
                Materials.Chrome.getMolten(144 * 144),
                Materials.Molybdenum.getMolten(144 * 150),
                Materials.Iron.getMolten(144 * 100))
            .fluidOutputs(MaterialsAlloy.INCONEL_625.getFluidStack(92880))
            .specialValue(0)
            .duration(400 * 15)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Oxygen.getGas(16000),
                Materials.Iron.getMolten(144 * 16),
                Materials.Aluminium.getMolten(144),
                Materials.Titanium.getMolten(144),
                Materials.Nickel.getMolten(144 * 4),
                Materials.Cobalt.getMolten(144 * 2))
            .fluidOutputs(MaterialsAlloy.MARAGING300.getFluidStack(3456))
            .specialValue(0)
            .duration(400)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Oxygen.getGas(16000),
                Materials.Iron.getMolten(144 * 16),
                Materials.Molybdenum.getMolten(144),
                Materials.Aluminium.getMolten(144),
                Materials.Nickel.getMolten(144 * 4),
                Materials.Cobalt.getMolten(144 * 2))
            .fluidOutputs(MaterialsAlloy.MARAGING350.getFluidStack(3456))
            .specialValue(0)
            .duration(400)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1))
            .fluidInputs(
                Materials.Oxygen.getGas(12000),
                Materials.Iron.getMolten(144 * 12),
                Materials.Manganese.getMolten(144),
                Materials.Silicon.getMolten(144 * 2),
                Materials.Aluminium.getMolten(144))
            .fluidOutputs(MaterialsAlloy.AQUATIC_STEEL.getFluidStack(2880))
            .specialValue(0)
            .duration(400)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Tungsten.getMolten(144 * 2), Materials.Tantalum.getMolten(144 * 23))
            .fluidOutputs(MaterialsAlloy.TANTALLOY_60.getFluidStack(3600))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .fluidInputs(Materials.Tantalum.getMolten(144))
            .fluidOutputs(MaterialsAlloy.TANTALUM_CARBIDE.getFluidStack(288))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Titanium.getMolten(144), Materials.Uranium.getMolten(144 * 9))
            .fluidOutputs(MaterialsAlloy.STABALLOY.getFluidStack(1440))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(
                Materials.Titanium.getMolten(144 * 150),
                Materials.Tantalum.getMolten(144 * 23),
                Materials.Ytterbium.getMolten(144 * 100),
                Materials.Tungsten.getMolten(144 * 2))
            .fluidOutputs(MaterialsAlloy.TANTALLOY_61.getFluidStack(39600))
            .specialValue(0)
            .duration(600 * 25)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 2))
            .fluidInputs(
                Materials.Cobalt.getMolten(144 * 4),
                Materials.Chrome.getMolten(144 * 3),
                Materials.Molybdenum.getMolten(144))
            .fluidOutputs(MaterialsAlloy.TALONITE.getFluidStack(1440))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 10),
                Materials.Copper.getMolten(144),
                Materials.Chrome.getMolten(144 * 5),
                Materials.Nickel.getMolten(144 * 9))
            .fluidOutputs(MaterialsAlloy.INCOLOY_020.getFluidStack(3600))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 23),
                Materials.Cobalt.getMolten(144 * 9),
                Materials.Chrome.getMolten(144 * 9),
                Materials.Nickel.getMolten(144 * 9))
            .fluidOutputs(MaterialsAlloy.INCOLOY_DS.getFluidStack(7200))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Chrome.getMolten(144 * 8),
                Materials.Niobium.getMolten(144 * 10),
                Materials.Molybdenum.getMolten(144 * 10),
                Materials.Nickel.getMolten(144 * 12))
            .fluidOutputs(MaterialsAlloy.INCONEL_690.getFluidStack(5760))
            .specialValue(0)
            .duration(600 * 5)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Nickel.getMolten(144 * 14),
                Materials.Chrome.getMolten(144),
                Materials.Niobium.getMolten(144 * 5),
                Materials.Aluminium.getMolten(144 * 10))
            .fluidOutputs(MaterialsAlloy.INCONEL_792.getFluidStack(4320))
            .specialValue(0)
            .duration(600 * 5)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 3),
                Materials.Cobalt.getMolten(144),
                Materials.Molybdenum.getMolten(144 * 12),
                Materials.Chrome.getMolten(144 * 3),
                Materials.Nickel.getMolten(144 * 31))
            .fluidOutputs(MaterialsAlloy.HASTELLOY_W.getFluidStack(7200))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 9),
                Materials.Manganese.getMolten(144),
                Materials.Silicon.getMolten(144),
                Materials.Molybdenum.getMolten(144 * 4),
                Materials.Chrome.getMolten(144 * 11),
                Materials.Nickel.getMolten(144 * 24))
            .fluidOutputs(MaterialsAlloy.HASTELLOY_X.getFluidStack(7200))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Trinium.getMolten(144 * 5), Materials.Naquadah.getMolten(144 * 9))
            .fluidOutputs(MaterialsAlloy.TRINIUM_NAQUADAH.getFluidStack(2106))
            .specialValue(0)
            .duration(800)
            .eut(960)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Copper.getMolten(144),
                Materials.Antimony.getMolten(144 * 2),
                Materials.Platinum.getMolten(144 * 2),
                Materials.Tin.getMolten(144 * 15))
            .fluidOutputs(new FluidStack(MaterialsElements.STANDALONE.WHITE_METAL.getFluid(), 2880))
            .specialValue(0)
            .duration(800)
            .eut(960)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 7))
            .fluidInputs(Materials.Tungsten.getMolten(144 * 7), Materials.Titanium.getMolten(144 * 6))
            .fluidOutputs(MaterialsAlloy.TUNGSTEN_TITANIUM_CARBIDE.getFluidStack(2880))
            .specialValue(0)
            .duration(800 * 2)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Cobalt.getMolten(144 * 7),
                Materials.Chrome.getMolten(144 * 7),
                Materials.Manganese.getMolten(144 * 4),
                Materials.Titanium.getMolten(144 * 2))
            .fluidOutputs(MaterialsAlloy.STELLITE.getFluidStack(2880))
            .specialValue(0)
            .duration(800)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 16),
                Materials.Aluminium.getMolten(144 * 3),
                Materials.Chrome.getMolten(144 * 5),
                Materials.Yttrium.getMolten(144))
            .fluidOutputs(MaterialsAlloy.INCOLOY_MA956.getFluidStack(3600))
            .specialValue(0)
            .duration(800)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1))
            .fluidOutputs(MaterialsAlloy.ENERGYCRYSTAL.getFluidStack(576))
            .specialValue(0)
            .duration(800)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Yttrium.getMolten(144 * 2),
                Materials.Molybdenum.getMolten(144 * 4),
                Materials.Chrome.getMolten(144 * 2),
                Materials.Titanium.getMolten(144 * 2),
                Materials.Nickel.getMolten(144 * 15))
            .fluidOutputs(MaterialsAlloy.HASTELLOY_N.getFluidStack(3600))
            .specialValue(0)
            .duration(800)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(6))
            .fluidInputs(
                Materials.Cobalt.getMolten(144),
                Materials.Molybdenum.getMolten(144 * 8),
                Materials.Tungsten.getMolten(144),
                Materials.Copper.getMolten(144),
                Materials.Chrome.getMolten(144 * 7),
                Materials.Nickel.getMolten(144 * 32))
            .fluidOutputs(MaterialsAlloy.HASTELLOY_C276.getFluidStack(7200))
            .specialValue(0)
            .duration(800)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 9),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 9))
            .fluidInputs(
                Materials.Titanium.getMolten(144 * 9),
                Materials.Potassium.getMolten(144 * 9),
                Materials.Lithium.getMolten(144 * 9),
                Materials.Hydrogen.getGas(5000))
            .fluidOutputs(MaterialsAlloy.LEAGRISIUM.getFluidStack(7200))
            .specialValue(0)
            .duration(800)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(5),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cadmium, 10))
            .fluidInputs(
                Materials.Bismuth.getMolten(144 * 47),
                Materials.Lead.getMolten(144 * 25),
                Materials.Tin.getMolten(144 * 13),
                Materials.Indium.getMolten(144 * 5))
            .fluidOutputs(MaterialsAlloy.INDALLOY_140.getFluidStack(14400))
            .specialValue(0)
            .duration(800)
            .eut(7680)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(6))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 47),
                Materials.Nickel.getMolten(144 * 25),
                Materials.Cobalt.getMolten(144 * 13),
                Materials.Titanium.getMolten(144 * 10),
                Materials.Molybdenum.getMolten(144 * 5),
                Materials.Aluminium.getMolten(144))
            .fluidOutputs(GGMaterial.incoloy903.getMolten(5328))
            .specialValue(0)
            .duration(1200)
            .eut(7680)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Titanium.getMolten(144 * 3), Materials.Nickel.getMolten(144 * 2))
            .fluidOutputs(MaterialsAlloy.NITINOL_60.getFluidStack(720))
            .specialValue(0)
            .duration(1500)
            .eut(7680)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                WerkstoffLoader.Thorium232.get(OrePrefixes.dust, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedOrder, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 1))
            .fluidOutputs(MaterialsAlloy.ARCANITE.getFluidStack(1440))
            .specialValue(0)
            .duration(6000)
            .eut(7680)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTUtility.copyAmountUnsafe(225, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)),
                GTUtility.copyAmountUnsafe(200, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 1)))
            .fluidInputs(
                Materials.Cobalt.getMolten(144 * 600),
                new FluidStack(MaterialsElements.getInstance().HAFNIUM.getFluid(), 144 * 500),
                Materials.Chrome.getMolten(144 * 344),
                Materials.Molybdenum.getMolten(144 * 116),
                MaterialsElements.getInstance().RHENIUM.getFluidStack(144 * 500),
                Materials.Niobium.getMolten(144 * 125),
                Materials.Iron.getMolten(144 * 136),
                Materials.Manganese.getMolten(144 * 4),
                Materials.Silicon.getMolten(144 * 4),
                Materials.Nickel.getMolten(144 * 96),
                Materials.Tungsten.getMolten(144 * 100),
                Materials.Oxygen.getGas(100000),
                new FluidStack(MaterialsElements.getInstance().ZIRCONIUM.getFluid(), 144 * 100))
            .fluidOutputs(MaterialsAlloy.HS188A.getFluidStack(7200 * 50))
            .specialValue(0)
            .duration(6000 * 50)
            .eut(7680)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Barium.getMolten(144 * 2),
                Materials.Calcium.getMolten(144 * 2),
                Materials.Copper.getMolten(144 * 3),
                Materials.Oxygen.getGas(8000),
                Materials.Mercury.getFluid(1000))
            .fluidOutputs(MaterialsAlloy.HG1223.getFluidStack(2304))
            .specialValue(0)
            .duration(2400)
            .eut(30720)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 14))
            .fluidInputs(Materials.Trinium.getMolten(144 * 45), Materials.Naquadah.getMolten(144 * 81))
            .fluidOutputs(MaterialsAlloy.TRINIUM_NAQUADAH_CARBON.getFluidStack(1440 * 14))
            .specialValue(0)
            .duration(7200 * 14)
            .eut(30720)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(6))
            .fluidInputs(
                Materials.Chrome.getMolten(144 * 13),
                Materials.Nickel.getMolten(144 * 3),
                Materials.Molybdenum.getMolten(144 * 2),
                Materials.Copper.getMolten(144 * 10),
                Materials.Tungsten.getMolten(144 * 2),
                Materials.Iron.getMolten(144 * 20),
                Materials.Oxygen.getGas(20000))
            .fluidOutputs(MaterialsAlloy.ZERON_100.getFluidStack(7200))
            .specialValue(0)
            .duration(7200)
            .eut(30720)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Lithium.getMolten(144),
                Materials.Cobalt.getMolten(144),
                Materials.Platinum.getMolten(144),
                Materials.Erbium.getMolten(144),
                Materials.Helium.getMolten(1000))
            .fluidOutputs(MaterialsAlloy.HELICOPTER.getFluidStack(720))
            .specialValue(0)
            .duration(7200)
            .eut(30720)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 25))
            .fluidInputs(
                Materials.Yttrium.getMolten(144 * 8),
                Materials.Molybdenum.getMolten(144 * 16),
                Materials.Chrome.getMolten(144 * 8),
                Materials.Titanium.getMolten(144 * 8),
                Materials.Nickel.getMolten(144 * 160),
                Materials.Naquadah.getMolten(144 * 50),
                Materials.Samarium.getMolten(144 * 25),
                Materials.Tungsten.getMolten(144 * 50),
                Materials.Aluminium.getMolten(144 * 75),
                Materials.Argon.getGas(25000))
            .fluidOutputs(MaterialsAlloy.LAFIUM.getFluidStack(64800))
            .specialValue(0)
            .duration(7200 * 25)
            .eut(30720)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(
                MaterialsElements.getInstance().GERMANIUM.getFluidStack(144 * 3),
                Materials.Tungsten.getMolten(144 * 3),
                Materials.Nitrogen.getGas(10000))
            .fluidOutputs(MaterialPool.Germaniumtungstennitride.getMolten(2304))
            .specialValue(0)
            .duration(9600)
            .eut(30720)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Trinium.getMolten(144 * 36),
                Materials.Iron.getMolten(144 * 44),
                Materials.Oxygen.getGas(44000),
                Materials.Aluminium.getMolten(144 * 2),
                Materials.Molybdenum.getMolten(144 * 2),
                Materials.Nickel.getMolten(144 * 8),
                Materials.Cobalt.getMolten(144 * 4),
                Materials.Tungsten.getMolten(144 * 12),
                Materials.Iridium.getMolten(144 * 9),
                Materials.Osmium.getMolten(144 * 3),
                Materials.Strontium.getMolten(144 * 12))
            .fluidOutputs(MaterialsAlloy.TRINIUM_REINFORCED_STEEL.getFluidStack(18984))
            .specialValue(0)
            .duration(8400 * 12)
            .eut(122880)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(5),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 3))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 23),
                Materials.Aluminium.getMolten(144),
                Materials.Chrome.getMolten(144 * 25),
                Materials.Nickel.getMolten(144 * 5),
                Materials.Silicon.getMolten(144 * 12),
                Materials.Indium.getMolten(144 * 12),
                Materials.Dysprosium.getMolten(144 * 6),
                MaterialsElements.getInstance().RHENIUM.getFluidStack(144 * 6))
            .fluidOutputs(MaterialsAlloy.LAURENIUM.getFluidStack(2304 * 6))
            .specialValue(0)
            .duration(8400 * 6)
            .eut(122880)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 3))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 1055),
                Materials.Aluminium.getMolten(144 * 409),
                Materials.Niobium.getMolten(144 * 192),
                Materials.Chrome.getMolten(144 * 217),
                Materials.Nickel.getMolten(144 * 1227),
                Materials.Silicon.getMolten(144 * 300),
                Materials.NaquadahEnriched.getMolten(144 * 960),
                Materials.Cerium.getMolten(144 * 720),
                Materials.Antimony.getMolten(144 * 480),
                Materials.Platinum.getMolten(144 * 480),
                Materials.Ytterbium.getMolten(144 * 240),
                Materials.Oxygen.getGas(480000))
            .fluidOutputs(MaterialsAlloy.PIKYONIUM.getFluidStack(1002240))
            .specialValue(0)
            .duration(8400 * 240)
            .eut(122880)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(8))
            .fluidInputs(
                Materials.Chrome.getMolten(144 * 208),
                Materials.Nickel.getMolten(144 * 48),
                Materials.Molybdenum.getMolten(144 * 32),
                Materials.Copper.getMolten(144 * 160),
                Materials.Tungsten.getMolten(144 * 32),
                Materials.Iron.getMolten(144 * 320),
                Materials.Naquadria.getMolten(144 * 350),
                Materials.Gadolinium.getMolten(144 * 250),
                Materials.Aluminium.getMolten(144 * 150),
                Materials.Tin.getMolten(144 * 100),
                Materials.Titanium.getMolten(144 * 600),
                Materials.Iridium.getMolten(144 * 225),
                Materials.Osmium.getMolten(144 * 75),
                Materials.Oxygen.getGas(320000),
                Materials.Mercury.getFluid(100000))
            .fluidOutputs(MaterialsAlloy.CINOBITE.getFluidStack(7632 * 50))
            .specialValue(0)
            .duration(8400 * 50)
            .eut(122880)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Nickel.getMolten(144 * 2),
                Materials.Titanium.getMolten(144 * 13),
                Materials.Osmium.getMolten(144 * 90),
                new FluidStack(MaterialsElements.getInstance().RUTHENIUM.getFluid(), 144 * 90),
                MaterialsElements.getInstance().THALLIUM.getFluidStack(144 * 45))
            .fluidOutputs(MaterialsAlloy.BOTMIUM.getFluidStack(2304 * 15))
            .specialValue(0)
            .duration(2400 * 15)
            .eut(491520)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 17),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 17),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 17),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 17))
            .fluidInputs(Materials.Tungsten.getMolten(144 * 7), Materials.Titanium.getMolten(144 * 3))
            .fluidOutputs(MaterialsAlloy.TITANSTEEL.getFluidStack(864 * 17))
            .specialValue(0)
            .duration(9600 * 17)
            .eut(491520)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 4))
            .fluidInputs(
                Materials.Titanium.getMolten(144 * 55),
                Materials.Lanthanum.getMolten(144 * 12),
                Materials.Tungsten.getMolten(144 * 8),
                Materials.Cobalt.getMolten(144 * 6),
                Materials.Manganese.getMolten(144 * 4),
                Materials.Palladium.getMolten(144 * 4),
                Materials.Niobium.getMolten(144 * 2),
                Materials.Argon.getGas(5000))
            .fluidOutputs(MaterialsAlloy.BLACK_TITANIUM.getFluidStack(14400))
            .specialValue(0)
            .duration(9600)
            .eut(491520)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTUtility.copyAmountUnsafe(105, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)),
                GTUtility.copyAmountUnsafe(85, GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 1)),
                GTUtility.copyAmountUnsafe(185, GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1)),
                GTUtility.copyAmountUnsafe(185, GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1)),
                GTUtility.copyAmountUnsafe(85, GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 1)),
                GTUtility.copyAmountUnsafe(40, GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedOrder, 1)))
            .fluidInputs(
                Materials.Thorium.getMolten(144 * 240),
                Materials.Copper.getMolten(144 * 24),
                Materials.Gold.getMolten(144 * 8),
                Materials.Silver.getMolten(144 * 8),
                Materials.Silicon.getMolten(144 * 40),
                Materials.Nickel.getMolten(144 * 40),
                Materials.Iron.getMolten(144 * 120),
                Materials.Oxygen.getGas(120000),
                Materials.Tungsten.getMolten(144 * 105),
                Materials.Titanium.getMolten(144 * 90),
                Materials.Thaumium.getMolten(144 * 500))
            .fluidOutputs(MaterialsAlloy.OCTIRON.getFluidStack(1440 * 2000))
            .specialValue(0)
            .duration(10800 * 2000)
            .eut(1966080)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTUtility.copyAmountUnsafe(450, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
            .fluidInputs(
                Materials.Tungsten.getMolten(144 * 450),
                Materials.Nickel.getMolten(144 * 820),
                Materials.Chrome.getMolten(144 * 460),
                Materials.Tin.getMolten(144 * 226),
                Materials.Copper.getMolten(144 * 675),
                Materials.Iron.getMolten(144 * 1176),
                Materials.Manganese.getMolten(144 * 100),
                Materials.Aluminium.getMolten(144 * 108),
                Materials.Yttrium.getMolten(144 * 35),
                MaterialsElements.getInstance().IODINE.getFluidStack(160000),
                MaterialsElements.getInstance().GERMANIUM.getFluidStack(144 * 160),
                Materials.Radon.getGas(160000))
            .fluidOutputs(MaterialsAlloy.ABYSSAL.getFluidStack(4032 * 160))
            .specialValue(0)
            .duration(10800 * 160)
            .eut(1966080)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 4))
            .fluidInputs(
                Materials.Vanadium.getMolten(144 * 3),
                Materials.Tungsten.getMolten(144 * 8),
                Materials.Naquadria.getMolten(144 * 7),
                Materials.BlackPlutonium.getMolten(144),
                Materials.Bedrockium.getMolten(144 * 4))
            .fluidOutputs(GGMaterial.tairitsu.getMolten(3888))
            .specialValue(0)
            .duration(400)
            .eut(1966080)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 20))
            .fluidInputs(
                Materials.Cobalt.getMolten(144 * 21),
                Materials.Chrome.getMolten(144 * 21),
                Materials.Manganese.getMolten(144 * 12),
                Materials.Titanium.getMolten(144 * 6),
                Materials.Silicon.getMolten(144 * 10),
                Materials.Gallium.getMolten(144 * 20),
                Materials.Americium.getMolten(144 * 20),
                Materials.Palladium.getMolten(144 * 20),
                Materials.Bismuth.getMolten(144 * 20),
                MaterialsElements.getInstance().GERMANIUM.getFluidStack(144 * 20))
            .fluidOutputs(MaterialsAlloy.QUANTUM.getFluidStack(1440 * 20))
            .specialValue(0)
            .duration(12000 * 20)
            .eut(7864320)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(6))
            .fluidInputs(
                new FluidStack(MaterialsElements.getInstance().RUTHENIUM.getFluid(), 144),
                new FluidStack(MaterialsElements.getInstance().RHODIUM.getFluid(), 144),
                Materials.Palladium.getMolten(144),
                Materials.Platinum.getMolten(144),
                Materials.Osmium.getMolten(144),
                Materials.Iridium.getMolten(144))
            .fluidOutputs(GGMaterial.preciousMetalAlloy.getMolten(864))
            .specialValue(0)
            .duration(10800)
            .eut(7864320)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTUtility.copyAmountUnsafe(509, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)),
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1)),
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1)),
                GTUtility.copyAmountUnsafe(180, GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 1)))
            .fluidInputs(
                Materials.Vanadium.getMolten(144 * 240),
                Materials.Tungsten.getMolten(144 * 749),
                Materials.Naquadria.getMolten(144 * 560),
                Materials.BlackPlutonium.getMolten(144 * 80),
                Materials.Bedrockium.getMolten(144 * 320),
                Materials.Titanium.getMolten(144 * 162),
                MaterialsUEVplus.TranscendentMetal.getMolten(144 * 2160),
                Materials.Tartarite.getMolten(144 * 2160),
                Materials.Infinity.getMolten(144 * 1080),
                MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(1080000))
            .fluidOutputs(MaterialsUEVplus.MoltenProtoHalkoniteBase.getFluid(1152 * 1080))
            .specialValue(0)
            .duration(1200 * 1080)
            .eut(7864320)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(8))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 2625),
                Materials.Oxygen.getGas(8250000),
                Materials.Silicon.getMolten(144 * 1875),
                Materials.Bismuth.getMolten(144 * 3200),
                Materials.Tellurium.getMolten(144 * 4800),
                new FluidStack(MaterialsElements.getInstance().ZIRCONIUM.getFluid(), 144 * 1125),
                Materials.RadoxPolymer.getMolten(144 * 3456),
                MaterialsUEVplus.TranscendentMetal.getMolten(144 * 8640),
                new FluidStack(MaterialsElements.STANDALONE.RHUGNOR.getFluid(), 144 * 5184),
                new FluidStack(MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluid(), 144 * 4320),
                Materials.Bismuth.getPlasma(144 * 864),
                GGMaterial.metastableOganesson.getMolten(144 * 3600),
                Materials.Praseodymium.getMolten(144 * 2160),
                MaterialsUEVplus.PhononCrystalSolution.getFluid(14400000))
            .fluidOutputs(MaterialsUEVplus.PhononMedium.getFluid(3600000))
            .specialValue(0)
            .duration(2400 * 3600)
            .eut(31457280)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(
                new FluidStack(MaterialsElements.getInstance().RUTHENIUM.getFluid(), 144 * 2),
                Materials.Iridium.getMolten(144))
            .fluidOutputs(WerkstoffLoader.Ruridit.getMolten(432))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                new FluidStack(MaterialsElements.getInstance().ZIRCONIUM.getFluid(), 144 * 34),
                Materials.Zinc.getMolten(144 * 5),
                Materials.Iron.getMolten(144 * 2),
                Materials.Chrome.getMolten(144))
            .fluidOutputs(GGMaterial.zircaloy4.getMolten(144 * 42))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                new FluidStack(MaterialsElements.getInstance().ZIRCONIUM.getFluid(), 144 * 34),
                Materials.Zinc.getMolten(144 * 4),
                Materials.Iron.getMolten(144),
                Materials.Chrome.getMolten(144),
                Materials.Nickel.getMolten(144))
            .fluidOutputs(GGMaterial.zircaloy2.getMolten(144 * 41))
            .specialValue(0)
            .duration(600)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(
                Materials.Adamantium.getMolten(144 * 5),
                Materials.Naquadah.getMolten(144 * 2),
                Materials.Lanthanum.getMolten(144 * 3))
            .fluidOutputs(GGMaterial.adamantiumAlloy.getMolten(1440))
            .specialValue(0)
            .duration(885)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Titanium.getMolten(144 * 5),
                Materials.Molybdenum.getMolten(144 * 2),
                Materials.Vanadium.getMolten(144 * 2),
                Materials.Chrome.getMolten(144 * 2),
                Materials.Aluminium.getMolten(144))
            .fluidOutputs(GGMaterial.titaniumBetaC.getMolten(144 * 16))
            .specialValue(0)
            .duration(145)
            .eut(7680)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(6))
            .fluidInputs(
                Materials.Titanium.getMolten(144 * 71),
                Materials.Molybdenum.getMolten(144 * 14),
                Materials.Vanadium.getMolten(144 * 94),
                Materials.Niobium.getMolten(144 * 36),
                Materials.Palladium.getMolten(144 * 48),
                new FluidStack(MaterialsElements.getInstance().RHODIUM.getFluid(), 144 * 16),
                Materials.Chrome.getMolten(144 * 14),
                Materials.Quantium.getMolten(144 * 56),
                Materials.Erbium.getMolten(144 * 24),
                Materials.Aluminium.getMolten(144 * 7))
            .fluidOutputs(GGMaterial.dalisenite.getMolten(144 * 16))
            .specialValue(0)
            .duration(283)
            .eut(491520)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.NaquadahEnriched.getMolten(144 * 8),
                Materials.Tritanium.getMolten(144 * 5),
                WerkstoffLoader.Californium.getMolten(144 * 3),
                Materials.BlackPlutonium.getMolten(144 * 2))
            .fluidOutputs(GGMaterial.enrichedNaquadahAlloy.getMolten(144 * 18))
            .specialValue(0)
            .duration(800)
            .eut(7864320)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Copper.getMolten(144), Materials.Redstone.getMolten(576))
            .fluidOutputs(Materials.RedAlloy.getMolten(144))
            .specialValue(0)
            .duration(100)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(
                Materials.Redstone.getMolten(288),
                Materials.Silver.getMolten(432),
                Materials.Gold.getMolten(144))
            .fluidOutputs(Materials.BlueAlloy.getMolten(288))
            .specialValue(0)
            .duration(80)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Nickel.getMolten(144 * 4), Materials.Chrome.getMolten(144))
            .fluidOutputs(Materials.Nichrome.getMolten(144 * 5))
            .specialValue(0)
            .duration(360)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(
                Materials.Iron.getMolten(144),
                Materials.Aluminium.getMolten(144),
                Materials.Chrome.getMolten(144))
            .fluidOutputs(Materials.Kanthal.getMolten(144 * 3))
            .specialValue(0)
            .duration(900)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Magnesium.getMolten(144), Materials.Aluminium.getMolten(144 * 2))
            .fluidOutputs(Materials.Magnalium.getMolten(144 * 3))
            .specialValue(0)
            .duration(200)
            .eut(4)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Lead.getMolten(144 * 4), Materials.Antimony.getMolten(144))
            .fluidOutputs(Materials.BatteryAlloy.getMolten(144 * 5))
            .specialValue(0)
            .duration(200)
            .eut(4)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Osmium.getMolten(144), Materials.Iridium.getMolten(144 * 3))
            .fluidOutputs(Materials.Osmiridium.getMolten(144 * 4))
            .specialValue(0)
            .duration(500)
            .eut(30720)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .fluidInputs(Materials.Naquadah.getMolten(144 * 2), Materials.Trinium.getMolten(144))
            .fluidOutputs(Materials.NaquadahAlloy.getMolten(144 * 4))
            .specialValue(0)
            .duration(360)
            .eut(61440)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Cobalt.getMolten(144 * 5),
                Materials.Chrome.getMolten(144 * 2),
                Materials.Nickel.getMolten(144),
                Materials.Molybdenum.getMolten(144))
            .fluidOutputs(Materials.Ultimet.getMolten(144 * 9))
            .specialValue(0)
            .duration(2700)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Vanadium.getMolten(144 * 3), Materials.Gallium.getMolten(144))
            .fluidOutputs(Materials.VanadiumGallium.getMolten(144 * 4))
            .specialValue(0)
            .duration(444)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4))
            .fluidInputs(
                Materials.Yttrium.getMolten(144),
                Materials.Barium.getMolten(144 * 2),
                Materials.Copper.getMolten(144 * 3),
                Materials.Oxygen.getGas(7000))
            .fluidOutputs(Materials.YttriumBariumCuprate.getMolten(144 * 13))
            .specialValue(0)
            .duration(280)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Niobium.getMolten(144), Materials.Titanium.getMolten(144))
            .fluidOutputs(Materials.NiobiumTitanium.getMolten(288))
            .specialValue(0)
            .duration(444)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Iron.getMolten(144), Materials.Tin.getMolten(144))
            .fluidOutputs(Materials.TinAlloy.getMolten(288))
            .specialValue(0)
            .duration(100)
            .eut(16)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 3))
            .fluidInputs(
                Materials.Magnesium.getMolten(144 * 2),
                Materials.Iron.getMolten(144 * 14),
                Materials.Silicon.getMolten(144 * 4),
                Materials.Oxygen.getGas(28000))
            .fluidOutputs(Materials.Reinforced.getMolten(1440))
            .specialValue(0)
            .duration(1200)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTUtility.copyAmountUnsafe(385, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1)),
                GTUtility.copyAmountUnsafe(80, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1)),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 60))
            .fluidInputs(
                Materials.Magnesium.getMolten(144 * 16),
                Materials.Iron.getMolten(144 * 112),
                Materials.Silicon.getMolten(144 * 38),
                Materials.Beryllium.getMolten(144 * 12),
                Materials.Potassium.getMolten(144 * 48),
                Materials.Chrome.getMolten(144),
                Materials.Aluminium.getMolten(144 * 2),
                Materials.Gold.getMolten(144 * 60),
                Materials.Nitrogen.getGas(60000),
                Materials.Oxygen.getGas(227000))
            .fluidOutputs(Materials.Galgadorian.getMolten(144 * 80))
            .specialValue(0)
            .duration(2000)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(9),
                GTUtility.copyAmountUnsafe(770, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1)),
                GTUtility.copyAmountUnsafe(160, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1)),
                GTUtility.copyAmountUnsafe(120, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
            .fluidInputs(
                Materials.Magnesium.getMolten(144 * 32),
                Materials.Iron.getMolten(144 * 224),
                Materials.Silicon.getMolten(144 * 76),
                Materials.Beryllium.getMolten(144 * 24),
                Materials.Potassium.getMolten(144 * 96),
                Materials.Chrome.getMolten(144 * 2),
                Materials.Aluminium.getMolten(144 * 4),
                Materials.Gold.getMolten(144 * 120),
                Materials.Nitrogen.getGas(120000),
                Materials.Oxygen.getGas(454000))
            .fluidOutputs(Materials.EnhancedGalgadorian.getMolten(144 * 160))
            .specialValue(0)
            .duration(3000)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(
                Materials.Nickel.getMolten(144),
                Materials.Zinc.getMolten(144),
                Materials.Iron.getMolten(144 * 4))
            .fluidOutputs(Materials.NickelZincFerrite.getMolten(144 * 6))
            .specialValue(0)
            .duration(480)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(
                Materials.Titanium.getMolten(144 * 3),
                Materials.Platinum.getMolten(144 * 3),
                Materials.Vanadium.getMolten(144))
            .fluidOutputs(Materials.TPV.getMolten(144 * 7))
            .specialValue(0)
            .duration(750)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1))
            .fluidInputs(Materials.Redstone.getMolten(144), Materials.Silicon.getMolten(144))
            .fluidOutputs(Materials.RedstoneAlloy.getMolten(432))
            .specialValue(0)
            .duration(100)
            .eut(8)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(5))
            .fluidInputs(
                Materials.Copper.getMolten(144 * 3),
                Materials.Gold.getMolten(144),
                Materials.Silver.getMolten(144),
                Materials.Nickel.getMolten(144 * 5),
                Materials.Iron.getMolten(144 * 15),
                Materials.Oxygen.getGas(15000))
            .fluidOutputs(Materials.BlackSteel.getMolten(144 * 25))
            .specialValue(0)
            .duration(1000)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(
                Materials.Copper.getMolten(144 * 3),
                Materials.Gold.getMolten(144),
                Materials.Silver.getMolten(144))
            .fluidOutputs(Materials.BlackBronze.getMolten(144 * 5))
            .specialValue(0)
            .duration(4000)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 16))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 5),
                Materials.Silicon.getMolten(144 * 6),
                Materials.Magnesium.getMolten(144),
                Materials.Oxygen.getGas(33000))
            .fluidOutputs(Materials.DarkSteel.getMolten(144 * 36))
            .specialValue(0)
            .duration(1000 * 4)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 25))
            .fluidInputs(
                Materials.Redstone.getMolten(144 * 25),
                Materials.Silicon.getMolten(144 * 25),
                Materials.Iron.getMolten(144 * 210),
                Materials.Oxygen.getGas(135000),
                Materials.Silver.getMolten(144 * 84),
                Materials.Gold.getMolten(144 * 84),
                Materials.Copper.getMolten(144 * 27))
            .fluidOutputs(Materials.EnergeticAlloy.getMolten(144 * 675))
            .specialValue(0)
            .duration(160 * 25)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 50))
            .fluidInputs(
                Materials.Redstone.getMolten(144 * 50),
                Materials.Silicon.getMolten(144 * 50),
                Materials.Iron.getMolten(144 * 420),
                Materials.Oxygen.getGas(270000),
                Materials.Silver.getMolten(144 * 168),
                Materials.Gold.getMolten(144 * 168),
                Materials.Copper.getMolten(144 * 54),
                Materials.Chrome.getMolten(144 * 1350),
                Materials.Blaze.getMolten(144 * 1350),
                Materials.Beryllium.getMolten(144 * 135),
                Materials.Potassium.getMolten(144 * 540),
                Materials.Nitrogen.getGas(675000))
            .fluidOutputs(Materials.VibrantAlloy.getMolten(144 * 4050))
            .specialValue(0)
            .duration(3000 * 50)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(6))
            .fluidInputs(
                Materials.Copper.getMolten(144 * 143),
                Materials.Gold.getMolten(144 * 96),
                Materials.Zinc.getMolten(144 * 80),
                Materials.Silver.getMolten(144 * 16),
                Materials.Nickel.getMolten(144 * 80),
                Materials.Iron.getMolten(144 * 440),
                Materials.Oxygen.getGas(440000))
            .fluidOutputs(Materials.BlueSteel.getMolten(144 * 800))
            .specialValue(0)
            .duration(3600 * 5)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(7))
            .fluidInputs(
                Materials.Copper.getMolten(144 * 32),
                Materials.Gold.getMolten(144 * 4),
                Materials.Silver.getMolten(144 * 24),
                Materials.Bismuth.getMolten(144 * 5),
                Materials.Zinc.getMolten(144 * 5),
                Materials.Nickel.getMolten(144 * 20),
                Materials.Iron.getMolten(144 * 110),
                Materials.Oxygen.getGas(110000))
            .fluidOutputs(Materials.RedSteel.getMolten(144 * 200))
            .specialValue(0)
            .duration(1200 * 5)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 10))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 90),
                Materials.Redstone.getMolten(144 * 10),
                Materials.Silicon.getMolten(144 * 10),
                Materials.Beryllium.getMolten(144 * 3),
                Materials.Potassium.getMolten(144 * 12),
                Materials.Nitrogen.getGas(15000))
            .fluidOutputs(Materials.PulsatingIron.getMolten(144 * 90))
            .specialValue(0)
            .duration(3200 * 90)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 10))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 90),
                Materials.Redstone.getMolten(144 * 10),
                Materials.Silicon.getMolten(144 * 10),
                Materials.Beryllium.getMolten(144 * 3),
                Materials.Potassium.getMolten(144 * 12),
                Materials.Nitrogen.getGas(15000))
            .fluidOutputs(Materials.PulsatingIron.getMolten(144 * 90))
            .specialValue(0)
            .duration(3200 * 90)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 10),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 1)))
            .fluidInputs(
                Materials.Gold.getMolten(144 * 90),
                Materials.Iron.getMolten(144 * 90),
                Materials.Redstone.getMolten(144 * 10),
                Materials.Silicon.getMolten(144 * 10),
                Materials.Beryllium.getMolten(144 * 3),
                Materials.Potassium.getMolten(144 * 12),
                Materials.Nitrogen.getGas(15000))
            .fluidOutputs(Materials.CrystallineAlloy.getMolten(144 * 270))
            .specialValue(0)
            .duration(1200 * 90)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(9),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 40),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 30),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Endstone, 1)))
            .fluidInputs(
                Materials.Iron.getMolten(144 * 10),
                Materials.Silicon.getMolten(144 * 10),
                Materials.Tungsten.getMolten(144 * 90),
                Materials.Oriharukon.getMolten(144 * 270),
                Materials.Blaze.getMolten(144 * 270),
                Materials.Beryllium.getMolten(144 * 27),
                Materials.Potassium.getMolten(144 * 108),
                Materials.Oxygen.getGas(10000),
                Materials.Nitrogen.getGas(135000))
            .fluidOutputs(Materials.MelodicAlloy.getMolten(144 * 810))
            .specialValue(0)
            .duration(3000 * 810)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 40),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 30),
                GTUtility.copyAmountUnsafe(810, GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Endstone, 1)))
            .fluidInputs(
                Materials.Naquadah.getMolten(144 * 810),
                Materials.Iron.getMolten(144 * 10),
                Materials.Silicon.getMolten(144 * 10),
                Materials.Tungsten.getMolten(144 * 90),
                Materials.Oriharukon.getMolten(144 * 270),
                Materials.Blaze.getMolten(144 * 270),
                Materials.Beryllium.getMolten(144 * 27),
                Materials.Potassium.getMolten(144 * 108),
                Materials.Oxygen.getGas(10000),
                Materials.Nitrogen.getGas(135000))
            .fluidOutputs(Materials.StellarAlloy.getMolten(144 * 2430))
            .specialValue(0)
            .duration(3600 * 2430)
            .eut(30720)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 50))
            .fluidInputs(
                Materials.Redstone.getMolten(144 * 50),
                Materials.Silicon.getMolten(144 * 50),
                Materials.Blaze.getMolten(144 * 1350),
                Materials.Beryllium.getMolten(144 * 135),
                Materials.Potassium.getMolten(144 * 540),
                Materials.Chrome.getMolten(144 * 1350),
                Materials.Copper.getMolten(144 * 540),
                Materials.Gold.getMolten(144 * 18),
                Materials.Silver.getMolten(144 * 618),
                Materials.Nickel.getMolten(144 * 90),
                Materials.Iron.getMolten(144 * 420),
                Materials.Oxygen.getGas(270000),
                Materials.Nitrogen.getGas(675000))
            .fluidOutputs(Materials.BlackSteel.getMolten(144 * 4050))
            .specialValue(0)
            .duration(6000 * 4050)
            .eut(120)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 10),
                GTUtility.copyAmountUnsafe(360, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1)),
                GTUtility.copyAmountUnsafe(90, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 1)))
            .fluidInputs(
                Materials.Glue.getFluid(144 * 270),
                Materials.Gold.getMolten(144 * 90),
                Materials.Iron.getMolten(144 * 90),
                Materials.Redstone.getMolten(144 * 10),
                Materials.Silicon.getMolten(144 * 10),
                Materials.Beryllium.getMolten(144 * 3),
                Materials.Potassium.getMolten(144 * 12),
                Materials.Nitrogen.getGas(15000))
            .fluidOutputs(Materials.CrystallinePinkSlime.getMolten(144 * 810))
            .specialValue(0)
            .duration(1800 * 270)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Endstone, 9),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 3))
            .fluidInputs(
                Materials.Iron.getMolten(144),
                Materials.Silicon.getMolten(144),
                Materials.Tungsten.getMolten(144 * 9),
                Materials.Oxygen.getGas(1000))
            .fluidOutputs(Materials.EndSteel.getMolten(144 * 27))
            .specialValue(0)
            .duration(1200)
            .eut(1920)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(
                Materials.Copper.getMolten(144 * 6),
                Materials.Redstone.getMolten(144 * 10),
                Materials.Ardite.getMolten(144 * 2))
            .fluidOutputs(GGMaterial.signalium.getMolten(144))
            .specialValue(0)
            .duration(1600)
            .eut(30720)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GTUtility.copyAmountUnsafe(126, GGMaterial.orundum.get(OrePrefixes.dust, 1)))
            .fluidInputs(
                Materials.Blaze.getMolten(144 * 128),
                Materials.Draconic.getMolten(144 * 16),
                Materials.Ardite.getMolten(144 * 16),
                Materials.Plutonium.getMolten(144 * 63),
                Materials.Naquadah.getMolten(144 * 8))
            .fluidOutputs(GGMaterial.atomicSeparationCatalyst.getMolten(144 * 63))
            .specialValue(0)
            .duration(3600 * 63)
            .eut(480)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GGMaterial.orundum.get(OrePrefixes.dust, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 13))
            .fluidInputs(
                Materials.Tritanium.getMolten(144 * 11),
                Materials.Rubidium.getMolten(144 * 11),
                Materials.FierySteel.getMolten(144 * 7),
                Materials.Ardite.getMolten(144 * 16),
                Materials.Plutonium.getMolten(144 * 63),
                GGMaterial.atomicSeparationCatalyst.getMolten(144 * 13),
                MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(5000))
            .fluidOutputs(MaterialsUEVplus.Mellion.getMolten(144 * 63))
            .specialValue(0)
            .duration(1142)
            .eut(TierEU.RECIPE_UXV)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(
                new FluidStack(MaterialsElements.getInstance().FERMIUM.getPlasma(), 1000),
                Materials.Thorium.getPlasma(1000),
                new FluidStack(MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getPlasma(), 1000),
                Materials.Calcium.getPlasma(1000),
                MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(1000))
            .fluidOutputs(MaterialsUEVplus.Creon.getPlasma(5000))
            .specialValue(0)
            .duration(400)
            .eut(TierEU.RECIPE_UXV)
            .addTo(SMFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(
                new FluidStack(MaterialsElements.getInstance().FERMIUM.getPlasma(), 1000),
                Materials.Thorium.getPlasma(1000),
                new FluidStack(MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getPlasma(), 1000),
                Materials.Calcium.getPlasma(1000),
                MaterialsUEVplus.DimensionallyTranscendentResidue.getFluid(1000))
            .fluidOutputs(MaterialsUEVplus.Creon.getMolten(5000))
            .specialValue(0)
            .duration(400)
            .eut(TierEU.RECIPE_UXV)
            .addTo(SMFR);
    }
}
