package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.mixins.early.nHCoreMod.AccessorBacteriaRegistry;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.common.loaders.BioItemList;
import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;

public class PetrochemicalPlantRecipes implements IRecipePool {

    public RecipeMap<?> PPR = GTNLRecipeMaps.PetrochemicalPlantRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.OilHeavy.getFluid(1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                Materials.Ethylene.getGas(225),
                Materials.Methane.getGas(225),
                Materials.Helium.getGas(10),
                Materials.Propane.getGas(20),
                Materials.Propene.getGas(150),
                Materials.Ethane.getGas(25),
                Materials.Butane.getGas(30),
                Materials.Butene.getGas(180),
                Materials.Butadiene.getGas(120),
                Materials.Toluene.getFluid(120),
                Materials.Benzene.getFluid(600),
                Materials.Octane.getFluid(20))
            .duration(120)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.OilLight.getFluid(1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                Materials.Ethylene.getGas(125),
                Materials.Methane.getGas(1000),
                Materials.Helium.getGas(20),
                Materials.Propane.getGas(75),
                Materials.Propene.getGas(50),
                Materials.Ethane.getGas(100),
                Materials.Butane.getGas(80),
                Materials.Butene.getGas(40),
                Materials.Butadiene.getGas(40),
                Materials.Toluene.getFluid(10),
                Materials.Benzene.getFluid(50),
                Materials.Octane.getFluid(20))
            .duration(120)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Oil.getFluid(1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                Materials.Ethylene.getGas(200),
                Materials.Methane.getGas(200),
                Materials.Helium.getGas(10),
                Materials.Propane.getGas(40),
                Materials.Propene.getGas(200),
                Materials.Ethane.getGas(40),
                Materials.Butane.getGas(40),
                Materials.Butene.getGas(50),
                Materials.Butadiene.getGas(40),
                Materials.Toluene.getFluid(30),
                Materials.Benzene.getFluid(100),
                Materials.Octane.getFluid(40))
            .duration(120)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.OilMedium.getFluid(1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                Materials.Ethylene.getGas(500),
                Materials.Methane.getGas(500),
                Materials.Helium.getGas(10),
                Materials.Propane.getGas(20),
                Materials.Propene.getGas(300),
                Materials.Ethane.getGas(80),
                Materials.Butane.getGas(30),
                Materials.Butene.getGas(60),
                Materials.Butadiene.getGas(50),
                Materials.Toluene.getFluid(20),
                Materials.Benzene.getFluid(100),
                Materials.Octane.getFluid(20))
            .duration(120)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .fluidInputs(Materials.WoodTar.getFluid(1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                Materials.Creosote.getFluid(150),
                Materials.Phenol.getFluid(100),
                Materials.Benzene.getFluid(250),
                Materials.Toluene.getFluid(50),
                Materials.Dimethylbenzene.getFluid(150),
                Materials.IIIDimethylbenzene.getFluid(150),
                Materials.IVDimethylbenzene.getFluid(150))
            .duration(100)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(
                Mods.NewHorizonsCoreMod.isModLoaded() ? getBacPetriDish() : new ItemStack(Items.paper, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.AntimonyTrioxide, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 16))
            .fluidInputs(
                GTNLMaterials.BarnardaCSappy.getFluidOrGas(1000),
                Materials.Oil.getFluid(2000),
                Materials.Silver.getPlasma(10))
            .fluidOutputs(
                Materials.OilHeavy.getFluid(60),
                Materials.Creosote.getFluid(120),
                Materials.Water.getFluid(250),
                Materials.FermentedBacterialSludge.getFluid(10),
                Materials.FermentedBiomass.getFluid(100),
                Materials.RadoxSuperHeavy.getFluid(15),
                Materials.RadoxHeavy.getFluid(20),
                Materials.XenoxeneDiluted.getFluid(2),
                Materials.RadoxLight.getGas(50),
                Materials.RadoxGas.getGas(20))
            .duration(1600)
            .eut(1966080)
            .addTo(PPR);

        RecipeBuilder.builder()
            .fluidInputs(new FluidStack(GTPPFluids.CoalTar, 1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                new FluidStack(GTPPFluids.CoalTarOil, 150),
                Materials.Naphtha.getFluid(75),
                new FluidStack(GTPPFluids.Ethylbenzene, 100),
                new FluidStack(GTPPFluids.Anthracene, 25),
                new FluidStack(GTPPFluids.Kerosene, 300),
                new FluidStack(GTPPFluids.Naphthalene, 180))
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.OilHeavy.getFluid(2000))
            .fluidOutputs(
                Materials.Naphtha.getFluid(75),
                Materials.NaphthenicAcid.getFluid(25),
                Materials.Gas.getGas(300),
                Materials.HeavyFuel.getFluid(500),
                Materials.LightFuel.getFluid(300),
                Materials.Lubricant.getFluid(400))
            .duration(150)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.OilLight.getFluid(2000))
            .fluidOutputs(
                Materials.Naphtha.getFluid(100),
                Materials.NaphthenicAcid.getFluid(10),
                Materials.Gas.getGas(800),
                Materials.HeavyFuel.getFluid(40),
                Materials.LightFuel.getFluid(70),
                Materials.Lubricant.getFluid(120))
            .duration(150)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.Oil.getFluid(1000))
            .fluidOutputs(
                Materials.Naphtha.getFluid(200),
                Materials.NaphthenicAcid.getFluid(30),
                Materials.Gas.getGas(600),
                Materials.HeavyFuel.getFluid(150),
                Materials.LightFuel.getFluid(400))
            .duration(100)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.OilMedium.getFluid(2000))
            .fluidOutputs(
                Materials.Naphtha.getFluid(700),
                Materials.NaphthenicAcid.getFluid(15),
                Materials.Gas.getGas(300),
                Materials.HeavyFuel.getFluid(50),
                Materials.LightFuel.getFluid(300),
                Materials.Lubricant.getFluid(300))
            .duration(100)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.OilExtraHeavy.getFluid(2000))
            .fluidOutputs(
                Materials.Naphtha.getFluid(220),
                Materials.NaphthenicAcid.getFluid(60),
                Materials.Gas.getGas(500),
                Materials.HeavyFuel.getFluid(750),
                Materials.LightFuel.getFluid(350),
                Materials.Lubricant.getFluid(600))
            .duration(200)
            .eut(1920)
            .addTo(PPR);
    }

    @Optional.Method(modid = "dreamcraft")
    public ItemStack getBacPetriDish() {
        return GTUtility.copyAmount(
            0,
            BioItemList.getPetriDish(
                AccessorBacteriaRegistry.getCultureSet()
                    .get("CombinedBac")));
    }
}
