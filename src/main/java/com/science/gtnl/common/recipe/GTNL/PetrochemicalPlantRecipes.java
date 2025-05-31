package com.science.gtnl.common.recipe.GTNL;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.bartworksHandler.BacteriaRegistry;
import com.dreammaster.fluids.FluidList;
import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import bartworks.common.loaders.BioItemList;
import bartworks.util.BioCulture;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;

public class PetrochemicalPlantRecipes implements IRecipePool {

    final RecipeMap<?> PPR = RecipeRegister.PetrochemicalPlantRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.OilHeavy.getFluid(1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                Materials.Ethylene.getGas(450),
                Materials.Methane.getGas(450),
                Materials.Helium.getGas(10),
                Materials.Propane.getGas(30),
                Materials.Propene.getGas(300),
                Materials.Ethane.getGas(45),
                Materials.Butane.getGas(60),
                Materials.Butene.getGas(240),
                Materials.Butadiene.getGas(150),
                Materials.Toluene.getFluid(240),
                Materials.Benzene.getFluid(1200),
                Materials.Octane.getFluid(20))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.OilLight.getFluid(1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                Materials.Ethylene.getGas(250),
                Materials.Methane.getGas(2000),
                Materials.Helium.getGas(40),
                Materials.Propane.getGas(140),
                Materials.Propene.getGas(90),
                Materials.Ethane.getGas(200),
                Materials.Butane.getGas(120),
                Materials.Butene.getGas(80),
                Materials.Butadiene.getGas(80),
                Materials.Toluene.getFluid(20),
                Materials.Benzene.getFluid(100),
                Materials.Octane.getFluid(20))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Oil.getFluid(1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                Materials.Ethylene.getGas(400),
                Materials.Methane.getGas(400),
                Materials.Helium.getGas(20),
                Materials.Propane.getGas(80),
                Materials.Propene.getGas(400),
                Materials.Ethane.getGas(80),
                Materials.Butane.getGas(80),
                Materials.Butene.getGas(100),
                Materials.Butadiene.getGas(90),
                Materials.Toluene.getFluid(60),
                Materials.Benzene.getFluid(180),
                Materials.Octane.getFluid(60))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.OilMedium.getFluid(1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                Materials.Ethylene.getGas(1000),
                Materials.Methane.getGas(1000),
                Materials.Helium.getGas(10),
                Materials.Propane.getGas(30),
                Materials.Propene.getGas(600),
                Materials.Ethane.getGas(130),
                Materials.Butane.getGas(70),
                Materials.Butene.getGas(100),
                Materials.Butadiene.getGas(100),
                Materials.Toluene.getFluid(40),
                Materials.Benzene.getFluid(200),
                Materials.Octane.getFluid(30))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .fluidInputs(Materials.WoodTar.getFluid(1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                Materials.Creosote.getFluid(300),
                Materials.Phenol.getFluid(200),
                Materials.Benzene.getFluid(500),
                Materials.Toluene.getFluid(100),
                Materials.Dimethylbenzene.getFluid(300),
                MaterialsKevlar.IIIDimethylbenzene.getFluid(300),
                MaterialsKevlar.IVDimethylbenzene.getFluid(300))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, BioItemList.getPetriDish(getCulture("CombinedBac"))),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.AntimonyTrioxide, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 16))
            .fluidInputs(
                MaterialPool.BarnardaCSappy.getFluidOrGas(1000),
                Materials.Oil.getFluid(2000),
                Materials.Silver.getPlasma(10))
            .fluidOutputs(
                Materials.OilHeavy.getFluid(100),
                Materials.Oil.getFluid(5),
                Materials.Creosote.getFluid(200),
                Materials.Water.getFluid(500),
                FluidList.FermentedBacterialSludge.getFluidStack(10),
                Materials.FermentedBiomass.getFluid(200),
                Materials.RadoxSuperHeavy.getFluid(20),
                Materials.RadoxHeavy.getFluid(30),
                Materials.DilutedXenoxene.getFluid(1),
                Materials.RadoxLight.getGas(80),
                Materials.RadoxGas.getGas(20))
            .specialValue(0)
            .duration(2000)
            .eut(1966080)
            .addTo(PPR);

        RecipeBuilder.builder()
            .fluidInputs(new FluidStack(GTPPFluids.CoalTar, 1000), Materials.Steam.getGas(1000))
            .fluidOutputs(
                new FluidStack(GTPPFluids.CoalTarOil, 300),
                Materials.Naphtha.getFluid(150),
                new FluidStack(GTPPFluids.Ethylbenzene, 200),
                new FluidStack(GTPPFluids.Anthracene, 50),
                new FluidStack(GTPPFluids.Kerosene, 600),
                new FluidStack(GTPPFluids.Naphthalene, 300))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.OilHeavy.getFluid(2000))
            .fluidOutputs(
                Materials.Naphtha.getFluid(150),
                MaterialsKevlar.NaphthenicAcid.getFluid(50),
                Materials.Gas.getGas(600),
                Materials.HeavyFuel.getFluid(1000),
                Materials.LightFuel.getFluid(450),
                Materials.Lubricant.getFluid(750))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.OilLight.getFluid(2000))
            .fluidOutputs(
                Materials.Naphtha.getFluid(200),
                MaterialsKevlar.NaphthenicAcid.getFluid(15),
                Materials.Gas.getGas(1600),
                Materials.HeavyFuel.getFluid(70),
                Materials.LightFuel.getFluid(130),
                Materials.Lubricant.getFluid(250))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.Oil.getFluid(1000))
            .fluidOutputs(
                Materials.Naphtha.getFluid(400),
                MaterialsKevlar.NaphthenicAcid.getFluid(50),
                Materials.Gas.getGas(1200),
                Materials.HeavyFuel.getFluid(300),
                Materials.LightFuel.getFluid(1000))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.OilMedium.getFluid(2000))
            .fluidOutputs(
                Materials.Naphtha.getFluid(1500),
                MaterialsKevlar.NaphthenicAcid.getFluid(25),
                Materials.Gas.getGas(600),
                Materials.HeavyFuel.getFluid(100),
                Materials.LightFuel.getFluid(500),
                Materials.Lubricant.getFluid(500))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(Materials.OilExtraHeavy.getFluid(2000))
            .fluidOutputs(
                Materials.Naphtha.getFluid(225),
                MaterialsKevlar.NaphthenicAcid.getFluid(75),
                Materials.Gas.getGas(900),
                Materials.HeavyFuel.getFluid(1500),
                Materials.LightFuel.getFluid(675),
                Materials.Lubricant.getFluid(1125))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(PPR);
    }

    public static BioCulture getCulture(String key) {
        try {
            Field field = BacteriaRegistry.class.getDeclaredField("CultureSet");
            field.setAccessible(true); // 绕过访问控制
            @SuppressWarnings("unchecked")
            LinkedHashMap<String, BioCulture> map = (LinkedHashMap<String, BioCulture>) field.get(null);
            return map.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
