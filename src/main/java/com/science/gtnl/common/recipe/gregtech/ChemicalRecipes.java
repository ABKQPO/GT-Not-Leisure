package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.enums.Mods.IndustrialCraft2;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.utils.enums.GTNLItemList;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.material.nuclear.MaterialsFluorides;
import gtPlusPlus.core.material.nuclear.MaterialsNuclides;

public class ChemicalRecipes implements IRecipePool {

    public IRecipeMap UC = GTRecipeConstants.UniversalChemical;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialPool.CrudeHexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 36),
                MaterialPool.SilicaGel.get(OrePrefixes.dust, 3))
            .fluidInputs(MaterialPool.Ethylenediamine.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.Hexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 36))
            .duration(100)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialPool.CrudeHexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 36),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 0))
            .fluidInputs(Materials.Ammonia.getGas(1000), MaterialPool.Ethanolamine.getFluidOrGas(1000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.Ethylenediamine.getFluidOrGas(1000), Materials.Water.getFluid(1000))
            .duration(180)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(MaterialsKevlar.EthyleneOxide.getGas(1000), Materials.Ammonia.getGas(1000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.Ethanolamine.getFluidOrGas(1000))
            .duration(60)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialPool.Tetraacetyldinitrohexaazaisowurtzitane.get(OrePrefixes.dust, 46),
                MaterialPool.NitroniumTetrafluoroborate.get(OrePrefixes.dust, 48))
            .fluidInputs(Materials.Water.getFluid(4000))
            .itemOutputs(
                MaterialPool.CrudeHexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 46),
                MaterialPool.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 14))
            .fluidOutputs(MaterialPool.FluoroboricAcide.getFluidOrGas(4000), Materials.AceticAcid.getFluid(4000))
            .duration(200)
            .eut(491520)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(new FluidStack(GTPPFluids.BoricAcid, 1000), Materials.HydrofluoricAcid.getFluid(4000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.FluoroboricAcide.getFluidOrGas(1000), Materials.Water.getFluid(13000))
            .duration(150)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                MaterialPool.BoronFluoride.getFluidOrGas(1000),
                Materials.HydrofluoricAcid.getFluid(1000),
                Materials.NitricAcid.getFluid(1000))
            .itemOutputs(MaterialPool.NitroniumTetrafluoroborate.get(OrePrefixes.dust, 8))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .duration(40)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                MaterialPool.SodiumTetrafluoroborate.get(OrePrefixes.dust, 6))
            .itemOutputs(MaterialsFluorides.SODIUM_FLUORIDE.getDust(2))
            .fluidOutputs(MaterialPool.BoronFluoride.getFluidOrGas(1000))
            .duration(120)
            .eut(125)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.BoronTrioxide.get(OrePrefixes.dust, 5))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(6000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.BoronFluoride.getFluidOrGas(2000))
            .duration(200)
            .eut(480)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(
                    64 + 6,
                    MaterialPool.Dibenzyltetraacetylhexaazaisowurtzitane.get(OrePrefixes.dust, 1)),
                MaterialPool.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 42))
            .fluidInputs(Materials.Water.getFluid(14000))
            .itemOutputs(MaterialPool.Tetraacetyldinitrohexaazaisowurtzitane.get(OrePrefixes.dust, 46))
            .fluidOutputs(
                Materials.HydrofluoricAcid.getFluid(24000),
                new FluidStack(GTPPFluids.BoricAcid, 6000),
                Materials.NitricOxide.getFluid(4000),
                MaterialPool.Benzaldehyde.getFluidOrGas(2000))
            .duration(140)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                MaterialPool.BoronFluoride.getFluidOrGas(2000),
                Materials.HydrofluoricAcid.getFluid(2000),
                Materials.DinitrogenTetroxide.getGas(2000))
            .itemOutputs(MaterialPool.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 7))
            .fluidOutputs(Materials.NitricAcid.getFluid(1000))
            .duration(120)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(64 + 8, MaterialPool.SuccinimidylAcetate.get(OrePrefixes.dust, 1)),
                GTUtility
                    .copyAmountUnsafe(64 + 38, MaterialPool.Hexabenzylhexaazaisowurtzitane.get(OrePrefixes.dust, 1)))
            .fluidInputs(MaterialPool.HydrobromicAcid.getFluidOrGas(100), Materials.Hydrogen.getGas(8000))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(
                    64 + 6,
                    MaterialPool.Dibenzyltetraacetylhexaazaisowurtzitane.get(OrePrefixes.dust, 1)))
            .fluidOutputs(Materials.Toluene.getFluid(6000))
            .duration(120)
            .eut(122880)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.NHydroxysuccinimide.get(OrePrefixes.dust, 13))
            .fluidInputs(MaterialMisc.ACETIC_ANHYDRIDE.getFluidStack(1000))
            .itemOutputs(MaterialPool.SuccinimidylAcetate.get(OrePrefixes.dust, 18))
            .fluidOutputs(Materials.AceticAcid.getFluid(1000))
            .duration(80)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 6),
                GTUtility.copyAmountUnsafe(64 + 2, MaterialPool.SuccinicAnhydride.get(OrePrefixes.dust, 1)))
            .fluidInputs(
                Materials.Methanol.getFluid(40000),
                Materials.Toluene.getFluid(6000),
                MaterialPool.HydroxylamineHydrochloride.getFluidOrGas(6000))
            .itemOutputs(
                MaterialPool.NHydroxysuccinimide.get(OrePrefixes.dust, 13),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Salt, 12))
            .fluidOutputs(Materials.Water.getFluid(6000), Materials.Hydrogen.getGas(6000))
            .duration(220)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialPool.HydroxylammoniumSulfate.get(OrePrefixes.dust, 17),
                MaterialPool.BariumChloride.get(OrePrefixes.dust, 3))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Barite, 6))
            .fluidOutputs(MaterialPool.HydroxylamineHydrochloride.getFluidOrGas(2000), Materials.Water.getFluid(1000))
            .duration(100)
            .eut(480)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Barium, 6))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(2000))
            .itemOutputs(MaterialPool.BariumChloride.get(OrePrefixes.dust, 3))
            .fluidOutputs(Materials.Hydrogen.getGas(2000))
            .duration(60)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                new FluidStack(MaterialsElements.getInstance().BROMINE.getFluid(), 1000),
                Materials.Hydrogen.getGas(1000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.HydrobromicAcid.getFluidOrGas(1000))
            .duration(300)
            .eut(480)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.PotassiumHydroxylaminedisulfonate.get(OrePrefixes.dust, 26))
            .fluidInputs(Materials.Water.getFluid(4000))
            .itemOutputs(
                MaterialPool.HydrobromicAcid.get(OrePrefixes.dust, 17),
                MaterialPool.PotassiumSulfate.get(OrePrefixes.dust, 14))
            .fluidOutputs(Materials.SulfuricAcid.getFluid(1000))
            .duration(200)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.PotassiumBisulfite.get(OrePrefixes.dust, 12))
            .fluidInputs(MaterialPool.NitrousAcid.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.PotassiumHydroxylaminedisulfonate.get(OrePrefixes.dust, 13))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .duration(200)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.SodiumNitrite.get(OrePrefixes.dust, 4))
            .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumBisulfate, 7))
            .fluidOutputs(MaterialPool.NitrousAcid.getFluidOrGas(1000))
            .duration(80)
            .eut(30)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.CoAcAbCatalyst.get(OrePrefixes.dust, 0))
            .fluidInputs(MaterialPool.SodiumNitrateSolution.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.SodiumNitrite.get(OrePrefixes.dust, 4))
            .fluidOutputs(Materials.Oxygen.getGas(1000), Materials.Water.getFluid(1000))
            .duration(300)
            .eut(30)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Charcoal, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polybenzimidazole, 1))
            .fluidInputs(Materials.Steam.getGas(1000), MaterialsKevlar.Acetylene.getGas(1000))
            .itemOutputs(MaterialPool.CoAcAbCatalyst.get(OrePrefixes.dust, 1))
            .fluidOutputs(Materials.Hydrogen.getGas(4000), Materials.CarbonMonoxide.getGas(1000))
            .duration(20)
            .eut(1966080)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(WerkstoffLoader.PotassiumCarbonate.get(OrePrefixes.dust, 6))
            .fluidInputs(MaterialsKevlar.SulfurDichloride.getGas(2000), Materials.Water.getFluid(1000))
            .itemOutputs(MaterialPool.PotassiumBisulfite.get(OrePrefixes.dust, 12))
            .fluidOutputs(Materials.CarbonDioxide.getGas(1000))
            .duration(160)
            .eut(480)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.Acetonitrile.get(OrePrefixes.dust, 6))
            .fluidInputs(MaterialPool.Benzylamine.getFluidOrGas(6000), MaterialPool.Glyoxal.getFluidOrGas(3000))
            .itemOutputs(
                GTUtility
                    .copyAmountUnsafe(64 + 38, MaterialPool.Hexabenzylhexaazaisowurtzitane.get(OrePrefixes.dust, 1)))
            .duration(100)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(Materials.NitricAcid.getFluid(2000), Materials.CarbonMonoxide.getGas(2000))
            .itemOutputs()
            .fluidOutputs(
                Materials.Water.getFluid(3000),
                MaterialPool.Glyoxal.getFluidOrGas(2000),
                Materials.NitrogenDioxide.getGas(1000))
            .duration(120)
            .eut(60)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                Materials.Ammonia.getGas(1000),
                Materials.CarbonMonoxide.getGas(2000),
                Materials.Hydrogen.getGas(4000))
            .itemOutputs(MaterialPool.Acetonitrile.get(OrePrefixes.dust, 6))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .duration(200)
            .eut(1966080)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.Hexamethylenetetramine.get(OrePrefixes.dust, 22))
            .fluidInputs(
                Materials.Water.getFluid(6000),
                Materials.HydrochloricAcid.getFluid(2000),
                MaterialPool.BenzylChloride.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.AmmoniumChloride.get(OrePrefixes.dust, 18))
            .fluidOutputs(MaterialPool.Benzylamine.getFluidOrGas(1000), new FluidStack(GTPPFluids.Formaldehyde, 6000))
            .duration(200)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(new FluidStack(GTPPFluids.Formaldehyde, 4000), Materials.Ammonia.getGas(6000))
            .itemOutputs(MaterialPool.Hexamethylenetetramine.get(OrePrefixes.dust, 22))
            .fluidOutputs(Materials.Water.getFluid(6000))
            .duration(160)
            .eut(30)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.SuccinicAcid.get(OrePrefixes.dust, 14))
            .fluidInputs(MaterialMisc.ACETIC_ANHYDRIDE.getFluidStack(1000))
            .itemOutputs(MaterialPool.SuccinicAnhydride.get(OrePrefixes.dust, 11))
            .fluidOutputs(Materials.AceticAcid.getFluid(2000))
            .duration(20)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(WerkstoffLoader.LuVTierMaterial.get(OrePrefixes.dust, 0))
            .fluidInputs(
                Materials.Water.getFluid(1000),
                Materials.Hydrogen.getGas(1000),
                MaterialPool.MaleicAnhydride.getFluidOrGas(2000))
            .itemOutputs(MaterialPool.SuccinicAcid.get(OrePrefixes.dust, 14))
            .duration(200)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 0))
            .fluidInputs(Materials.Oxygen.getGas(7000), Materials.Butane.getGas(1000))
            .itemOutputs()
            .fluidOutputs(Materials.Water.getFluid(4000), MaterialPool.MaleicAnhydride.getFluidOrGas(1000))
            .duration(280)
            .eut(480)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.PyromelliticDianhydride.get(OrePrefixes.dust, 18))
            .fluidInputs(MaterialPool.Oxydianiline.getFluidOrGas(1000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.PloyamicAcid.getFluidOrGas(1000))
            .duration(400)
            .eut(122880)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 0))
            .fluidInputs(new FluidStack(GTPPFluids.Aniline, 2000), Materials.Phenol.getFluid(1000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.Oxydianiline.getFluidOrGas(1000), Materials.Methane.getGas(2000))
            .duration(150)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.Durene.get(OrePrefixes.dust, 24))
            .fluidInputs(Materials.Oxygen.getGas(12000))
            .itemOutputs(MaterialPool.PyromelliticDianhydride.get(OrePrefixes.dust, 18))
            .fluidOutputs(Materials.Water.getFluid(6000))
            .duration(150)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Chloromethane.getGas(2000), Materials.Dimethylbenzene.getFluid(1000))
            .itemOutputs(MaterialPool.Durene.get(OrePrefixes.dust, 24))
            .fluidOutputs(Materials.HydrochloricAcid.getFluid(2000))
            .duration(150)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .fluidInputs(
                MaterialPool.RareEarthHydroxides.getFluidOrGas(1000),
                Materials.HydrochloricAcid.getFluid(1000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 3))
            .fluidOutputs(MaterialPool.RareEarthChlorides.getFluidOrGas(1000))
            .duration(120)
            .eut(30)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.SodiumNitrite.get(OrePrefixes.dust, 4))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(1000), MaterialPool.FluoroboricAcide.getFluidOrGas(2000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Salt, 2))
            .fluidOutputs(
                MaterialPool.BenzenediazoniumTetrafluoroborate.getFluidOrGas(1000),
                Materials.Water.getFluid(1000))
            .duration(130)
            .eut(TierEU.RECIPE_LuV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTNLItemList.BlackLight.get(0))
            .fluidInputs(
                GGMaterial.fluoroantimonicAcid.getFluidOrGas(1000),
                Materials.Methane.getGas(1000),
                MaterialPool.FluoroBenzene.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.AntimonyTrifluoride.get(OrePrefixes.dust, 4))
            .fluidOutputs(MaterialPool.Fluorotoluene.getFluidOrGas(1000), Materials.Water.getFluid(4000))
            .duration(150)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.AntimonyTrioxide, 5))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(6000))
            .itemOutputs(MaterialPool.AntimonyTrifluoride.get(OrePrefixes.dust, 8))
            .fluidOutputs(Materials.Water.getFluid(3000))
            .duration(60)
            .eut(TierEU.RECIPE_LV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.AntimonyTrifluoride.get(OrePrefixes.dust, 4))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(4000))
            .fluidOutputs(GGMaterial.fluoroantimonicAcid.getFluidOrGas(1000), Materials.Hydrogen.getGas(2000))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.AntimonyTrifluoride.get(OrePrefixes.dust, 4))
            .fluidInputs(Materials.Fluorine.getGas(2000))
            .fluidOutputs(GGMaterial.antimonyPentafluoride.getFluidOrGas(1000))
            .duration(100)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(
                Materials.Benzene.getFluid(2000),
                Materials.Oxygen.getGas(5000),
                Materials.Propene.getGas(1000))
            .fluidOutputs(
                MaterialPool.Resorcinol.getFluidOrGas(1000),
                MaterialPool.Hydroquinone.getFluidOrGas(1000),
                Materials.Acetone.getFluid(1000))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTNLItemList.ZnFeAlClCatalyst.get(0))
            .itemOutputs(MaterialPool.Difluorobenzophenone.get(OrePrefixes.dust, 24))
            .fluidInputs(
                MaterialPool.Fluorotoluene.getFluidOrGas(1000),
                Materials.Chlorine.getGas(6000),
                Materials.Water.getFluid(1000),
                MaterialPool.FluoroBenzene.getFluidOrGas(1000))
            .fluidOutputs(Materials.HydrochloricAcid.getFluid(6000))
            .duration(100)
            .eut(TierEU.RECIPE_EV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialPool.Difluorobenzophenone.get(OrePrefixes.dust, 24),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodaAsh, 6))
            .itemOutputs(MaterialsFluorides.SODIUM_FLUORIDE.getDust(4))
            .fluidInputs(MaterialPool.Hydroquinone.getFluidOrGas(1000))
            .fluidOutputs(
                MaterialPool.Polyetheretherketone.getMolten(2592),
                Materials.Water.getFluid(1000),
                Materials.CarbonDioxide.getGas(1000))
            .duration(250)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 6))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodaAsh, 6))
            .fluidInputs(Materials.CarbonDioxide.getGas(1000))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .duration(80)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Beryllium, 1))
            .fluidInputs(Materials.Fluorine.getGas(4000))
            .fluidOutputs(MaterialsNuclides.Li2BeF4.getFluidStack(1008))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                Materials.NitricAcid.getFluid(3000),
                Materials.Benzene.getFluid(1000),
                Materials.Hydrogen.getGas(5000))
            .fluidOutputs(Materials.Water.getFluid(4000), MaterialPool.SmallBaka.getFluidOrGas(1000))
            .duration(20)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Plastic, 1))
            .itemOutputs(GTModHandler.getModItem(IndustrialCraft2.ID, "blockITNT", 32))
            .fluidInputs(MaterialPool.SmallBaka.getFluidOrGas(2000))
            .duration(100)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.UraniumSlag.get(OrePrefixes.dust, 1))
            .itemOutputs(MaterialPool.UraniumChlorideSlag.get(OrePrefixes.dust, 1))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(4000))
            .fluidOutputs(Materials.Hydrogen.getGas(4000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 11))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cinnabar, 16))
            .fluidInputs(MaterialPool.ToxicMercurySludge.getFluidOrGas(12000))
            .fluidOutputs(MaterialPool.PostProcessBeWaste.getFluidOrGas(12000))
            .duration(300)
            .eut(TierEU.RECIPE_MV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphate, 10))
            .fluidInputs(Materials.Hydrogen.getGas(6000))
            .fluidOutputs(Materials.PhosphoricAcid.getFluid(2000))
            .duration(40)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);
    }
}
