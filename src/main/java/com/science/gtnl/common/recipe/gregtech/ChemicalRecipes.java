package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import appeng.api.AEApi;
import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
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
    public RecipeMap<?> MCRR = RecipeMaps.multiblockChemicalReactorRecipes;

    @Override
    public void loadRecipes() {
        var aeMaterials = AEApi.instance()
            .definitions()
            .materials();

        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.CrudeHexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 36),
                GTNLMaterials.SilicaGel.get(OrePrefixes.dust, 3))
            .fluidInputs(GTNLMaterials.Ethylenediamine.getFluidOrGas(1000))
            .itemOutputs(GTNLMaterials.Hexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 36))
            .duration(100)
            .eut(1920)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.CrudeHexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 36),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 0))
            .fluidInputs(Materials.Ammonia.getGas(1000), GTNLMaterials.Ethanolamine.getFluidOrGas(1000))
            .fluidOutputs(GTNLMaterials.Ethylenediamine.getFluidOrGas(1000), Materials.Water.getFluid(1000))
            .duration(180)
            .eut(120)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .fluidInputs(MaterialsKevlar.EthyleneOxide.getGas(1000), Materials.Ammonia.getGas(1000))
            .fluidOutputs(GTNLMaterials.Ethanolamine.getFluidOrGas(1000))
            .duration(60)
            .eut(7680)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.Tetraacetyldinitrohexaazaisowurtzitane.get(OrePrefixes.dust, 46),
                GTNLMaterials.NitroniumTetrafluoroborate.get(OrePrefixes.dust, 48))
            .fluidInputs(Materials.Water.getFluid(4000))
            .itemOutputs(
                GTNLMaterials.CrudeHexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 46),
                GTNLMaterials.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 14))
            .fluidOutputs(GTNLMaterials.FluoroboricAcide.getFluidOrGas(4000), Materials.AceticAcid.getFluid(4000))
            .duration(200)
            .eut(491520)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .fluidInputs(new FluidStack(GTPPFluids.BoricAcid, 1000), Materials.HydrofluoricAcid.getFluid(4000))
            .fluidOutputs(GTNLMaterials.FluoroboricAcide.getFluidOrGas(1000), Materials.Water.getFluid(13000))
            .duration(150)
            .eut(120)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .fluidInputs(
                GTNLMaterials.BoronFluoride.getFluidOrGas(1000),
                Materials.HydrofluoricAcid.getFluid(1000),
                Materials.NitricAcid.getFluid(1000))
            .itemOutputs(GTNLMaterials.NitroniumTetrafluoroborate.get(OrePrefixes.dust, 8))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .duration(40)
            .eut(1920)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTNLMaterials.SodiumTetrafluoroborate.get(OrePrefixes.dust, 6))
            .itemOutputs(MaterialsFluorides.SODIUM_FLUORIDE.getDust(2))
            .fluidOutputs(GTNLMaterials.BoronFluoride.getFluidOrGas(1000))
            .duration(120)
            .eut(125)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.BoronTrioxide.get(OrePrefixes.dust, 5))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(6000))
            .fluidOutputs(GTNLMaterials.BoronFluoride.getFluidOrGas(2000))
            .duration(200)
            .eut(480)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(
                    64 + 6,
                    GTNLMaterials.Dibenzyltetraacetylhexaazaisowurtzitane.get(OrePrefixes.dust, 1)),
                GTNLMaterials.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 42))
            .fluidInputs(Materials.Water.getFluid(14000))
            .itemOutputs(GTNLMaterials.Tetraacetyldinitrohexaazaisowurtzitane.get(OrePrefixes.dust, 46))
            .fluidOutputs(
                Materials.HydrofluoricAcid.getFluid(24000),
                new FluidStack(GTPPFluids.BoricAcid, 6000),
                Materials.NitricOxide.getGas(4000),
                GTNLMaterials.Benzaldehyde.getFluidOrGas(2000))
            .duration(140)
            .eut(7680)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.Benzaldehyde.getFluidOrGas(1000), Materials.Hydrogen.getGas(2000))
            .fluidOutputs(Materials.Toluene.getFluid(1000), Materials.Water.getFluid(1000))
            .duration(100)
            .eut(TierEU.RECIPE_HV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .fluidInputs(
                GTNLMaterials.BoronFluoride.getFluidOrGas(2000),
                Materials.HydrofluoricAcid.getFluid(2000),
                Materials.DinitrogenTetroxide.getGas(2000))
            .itemOutputs(GTNLMaterials.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 7))
            .fluidOutputs(Materials.NitricAcid.getFluid(1000))
            .duration(120)
            .eut(120)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(64 + 8, GTNLMaterials.SuccinimidylAcetate.get(OrePrefixes.dust, 1)),
                GTUtility
                    .copyAmountUnsafe(64 + 38, GTNLMaterials.Hexabenzylhexaazaisowurtzitane.get(OrePrefixes.dust, 1)))
            .fluidInputs(GTNLMaterials.HydrobromicAcid.getFluidOrGas(100), Materials.Hydrogen.getGas(8000))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(
                    64 + 6,
                    GTNLMaterials.Dibenzyltetraacetylhexaazaisowurtzitane.get(OrePrefixes.dust, 1)))
            .fluidOutputs(Materials.Toluene.getFluid(6000))
            .duration(120)
            .eut(122880)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.NHydroxysuccinimide.get(OrePrefixes.dust, 13))
            .fluidInputs(MaterialMisc.ACETIC_ANHYDRIDE.getFluidStack(1000))
            .itemOutputs(GTNLMaterials.SuccinimidylAcetate.get(OrePrefixes.dust, 18))
            .fluidOutputs(Materials.AceticAcid.getFluid(1000))
            .duration(80)
            .eut(7680)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 6),
                GTUtility.copyAmountUnsafe(64 + 2, GTNLMaterials.SuccinicAnhydride.get(OrePrefixes.dust, 1)))
            .fluidInputs(
                Materials.Methanol.getFluid(40000),
                Materials.Toluene.getFluid(6000),
                GTNLMaterials.HydroxylamineHydrochloride.getFluidOrGas(6000))
            .itemOutputs(
                GTNLMaterials.NHydroxysuccinimide.get(OrePrefixes.dust, 13),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Salt, 12))
            .fluidOutputs(Materials.Water.getFluid(6000), Materials.Hydrogen.getGas(6000))
            .duration(220)
            .eut(1920)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.HydroxylammoniumSulfate.get(OrePrefixes.dust, 17),
                GTNLMaterials.BariumChloride.get(OrePrefixes.dust, 3))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Barite, 6))
            .fluidOutputs(GTNLMaterials.HydroxylamineHydrochloride.getFluidOrGas(2000), Materials.Water.getFluid(1000))
            .duration(100)
            .eut(480)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Barium, 6))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(2000))
            .itemOutputs(GTNLMaterials.BariumChloride.get(OrePrefixes.dust, 3))
            .fluidOutputs(Materials.Hydrogen.getGas(2000))
            .duration(60)
            .eut(120)
            .addTo(UC);

        RecipeBuilder.builder()
            .fluidInputs(
                new FluidStack(MaterialsElements.getInstance().BROMINE.getFluid(), 1000),
                Materials.Hydrogen.getGas(1000))
            .fluidOutputs(GTNLMaterials.HydrobromicAcid.getFluidOrGas(1000))
            .duration(300)
            .eut(480)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.PotassiumHydroxylaminedisulfonate.get(OrePrefixes.dust, 26))
            .fluidInputs(Materials.Water.getFluid(4000))
            .itemOutputs(
                GTNLMaterials.HydrobromicAcid.get(OrePrefixes.dust, 17),
                GTNLMaterials.PotassiumSulfate.get(OrePrefixes.dust, 14))
            .fluidOutputs(Materials.SulfuricAcid.getFluid(1000))
            .duration(200)
            .eut(1920)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.PotassiumBisulfite.get(OrePrefixes.dust, 12))
            .fluidInputs(GTNLMaterials.NitrousAcid.getFluidOrGas(1000))
            .itemOutputs(GTNLMaterials.PotassiumHydroxylaminedisulfonate.get(OrePrefixes.dust, 13))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .duration(200)
            .eut(1920)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.SodiumNitrite.get(OrePrefixes.dust, 4))
            .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumBisulfate, 7))
            .fluidOutputs(GTNLMaterials.NitrousAcid.getFluidOrGas(1000))
            .duration(80)
            .eut(30)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.CoAcAbCatalyst.get(OrePrefixes.dust, 0))
            .fluidInputs(GTNLMaterials.SodiumNitrateSolution.getFluidOrGas(1000))
            .itemOutputs(GTNLMaterials.SodiumNitrite.get(OrePrefixes.dust, 4))
            .fluidOutputs(Materials.Oxygen.getGas(1000), Materials.Water.getFluid(1000))
            .duration(300)
            .eut(30)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Charcoal, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polybenzimidazole, 1))
            .fluidInputs(Materials.Steam.getGas(1000), MaterialsKevlar.Acetylene.getGas(1000))
            .itemOutputs(GTNLMaterials.CoAcAbCatalyst.get(OrePrefixes.dust, 1))
            .fluidOutputs(Materials.Hydrogen.getGas(4000), Materials.CarbonMonoxide.getGas(1000))
            .duration(20)
            .eut(1966080)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(WerkstoffLoader.PotassiumCarbonate.get(OrePrefixes.dust, 6))
            .fluidInputs(MaterialsKevlar.SulfurDichloride.getFluid(2000), Materials.Water.getFluid(1000))
            .itemOutputs(GTNLMaterials.PotassiumBisulfite.get(OrePrefixes.dust, 12))
            .fluidOutputs(Materials.CarbonDioxide.getGas(1000))
            .duration(160)
            .eut(480)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.Acetonitrile.get(OrePrefixes.dust, 6))
            .fluidInputs(GTNLMaterials.Benzylamine.getFluidOrGas(6000), GTNLMaterials.Glyoxal.getFluidOrGas(3000))
            .itemOutputs(
                GTUtility
                    .copyAmountUnsafe(64 + 38, GTNLMaterials.Hexabenzylhexaazaisowurtzitane.get(OrePrefixes.dust, 1)))
            .duration(100)
            .eut(7680)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .fluidInputs(Materials.NitricAcid.getFluid(2000), Materials.CarbonMonoxide.getGas(2000))
            .fluidOutputs(
                Materials.Water.getFluid(3000),
                GTNLMaterials.Glyoxal.getFluidOrGas(2000),
                Materials.NitrogenDioxide.getGas(1000))
            .duration(120)
            .eut(60)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .fluidInputs(
                Materials.Ammonia.getGas(1000),
                Materials.CarbonMonoxide.getGas(2000),
                Materials.Hydrogen.getGas(4000))
            .itemOutputs(GTNLMaterials.Acetonitrile.get(OrePrefixes.dust, 6))
            .fluidOutputs(Materials.Water.getFluid(2000))
            .duration(200)
            .eut(1966080)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.Hexamethylenetetramine.get(OrePrefixes.dust, 22))
            .fluidInputs(
                Materials.Water.getFluid(6000),
                Materials.HydrochloricAcid.getFluid(2000),
                GTNLMaterials.BenzylChloride.getFluidOrGas(1000))
            .itemOutputs(GTNLMaterials.AmmoniumChloride.get(OrePrefixes.dust, 18))
            .fluidOutputs(GTNLMaterials.Benzylamine.getFluidOrGas(1000), new FluidStack(GTPPFluids.Formaldehyde, 6000))
            .duration(200)
            .eut(7680)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(new FluidStack(GTPPFluids.Formaldehyde, 4000), Materials.Ammonia.getGas(6000))
            .itemOutputs(GTNLMaterials.Hexamethylenetetramine.get(OrePrefixes.dust, 22))
            .fluidOutputs(Materials.Water.getFluid(6000))
            .duration(160)
            .eut(30)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.SuccinicAcid.get(OrePrefixes.dust, 14))
            .fluidInputs(MaterialMisc.ACETIC_ANHYDRIDE.getFluidStack(1000))
            .itemOutputs(GTNLMaterials.SuccinicAnhydride.get(OrePrefixes.dust, 11))
            .fluidOutputs(Materials.AceticAcid.getFluid(2000))
            .duration(20)
            .eut(7680)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(WerkstoffLoader.LuVTierMaterial.get(OrePrefixes.dust, 0))
            .fluidInputs(
                Materials.Water.getFluid(1000),
                Materials.Hydrogen.getGas(1000),
                GTNLMaterials.MaleicAnhydride.getFluidOrGas(2000))
            .itemOutputs(GTNLMaterials.SuccinicAcid.get(OrePrefixes.dust, 14))
            .duration(200)
            .eut(1920)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 0))
            .fluidInputs(Materials.Oxygen.getGas(7000), Materials.Butane.getGas(1000))
            .fluidOutputs(Materials.Water.getFluid(4000), GTNLMaterials.MaleicAnhydride.getFluidOrGas(1000))
            .duration(280)
            .eut(480)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.PyromelliticDianhydride.get(OrePrefixes.dust, 18))
            .fluidInputs(GTNLMaterials.Oxydianiline.getFluidOrGas(1000))
            .fluidOutputs(GTNLMaterials.PloyamicAcid.getFluidOrGas(1000))
            .duration(400)
            .eut(122880)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 0))
            .fluidInputs(new FluidStack(GTPPFluids.Aniline, 2000), Materials.Phenol.getFluid(1000))
            .fluidOutputs(GTNLMaterials.Oxydianiline.getFluidOrGas(1000), Materials.Methane.getGas(2000))
            .duration(150)
            .eut(120)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.Durene.get(OrePrefixes.dust, 24))
            .fluidInputs(Materials.Oxygen.getGas(12000))
            .itemOutputs(GTNLMaterials.PyromelliticDianhydride.get(OrePrefixes.dust, 18))
            .fluidOutputs(Materials.Water.getFluid(6000))
            .duration(150)
            .eut(120)
            .addTo(UC);

        RecipeBuilder.builder()
            .fluidInputs(Materials.Chloromethane.getGas(2000), Materials.Dimethylbenzene.getFluid(1000))
            .itemOutputs(GTNLMaterials.Durene.get(OrePrefixes.dust, 24))
            .fluidOutputs(Materials.HydrochloricAcid.getFluid(2000))
            .duration(150)
            .eut(120)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .fluidInputs(
                GTNLMaterials.RareEarthHydroxides.getFluidOrGas(1000),
                Materials.HydrochloricAcid.getFluid(1000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 3))
            .fluidOutputs(GTNLMaterials.RareEarthChlorides.getFluidOrGas(1000))
            .duration(120)
            .eut(30)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.SodiumNitrite.get(OrePrefixes.dust, 4))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(1000), GTNLMaterials.FluoroboricAcide.getFluidOrGas(2000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Salt, 2))
            .fluidOutputs(
                GTNLMaterials.BenzenediazoniumTetrafluoroborate.getFluidOrGas(1000),
                Materials.Water.getFluid(1000))
            .duration(130)
            .eut(TierEU.RECIPE_LuV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.BlackLight.get(0))
            .fluidInputs(
                GGMaterial.fluoroantimonicAcid.getFluidOrGas(1000),
                Materials.Methane.getGas(1000),
                GTNLMaterials.FluoroBenzene.getFluidOrGas(1000))
            .itemOutputs(GTNLMaterials.AntimonyTrifluoride.get(OrePrefixes.dust, 4))
            .fluidOutputs(GTNLMaterials.Fluorotoluene.getFluidOrGas(1000), Materials.Water.getFluid(4000))
            .duration(150)
            .eut(TierEU.RECIPE_HV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.AntimonyTrioxide, 5))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(6000))
            .itemOutputs(GTNLMaterials.AntimonyTrifluoride.get(OrePrefixes.dust, 8))
            .fluidOutputs(Materials.Water.getFluid(3000))
            .duration(60)
            .eut(TierEU.RECIPE_LV)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.AntimonyTrifluoride.get(OrePrefixes.dust, 4))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(4000))
            .fluidOutputs(GGMaterial.fluoroantimonicAcid.getFluidOrGas(1000), Materials.Hydrogen.getGas(2000))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(
                Materials.Benzene.getFluid(2000),
                Materials.Oxygen.getGas(5000),
                Materials.Propene.getGas(1000))
            .fluidOutputs(
                GTNLMaterials.Resorcinol.getFluidOrGas(1000),
                GTNLMaterials.Hydroquinone.getFluidOrGas(1000),
                Materials.Acetone.getFluid(1000))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.ZnFeAlClCatalyst.get(0))
            .itemOutputs(GTNLMaterials.Difluorobenzophenone.get(OrePrefixes.dust, 24))
            .fluidInputs(
                GTNLMaterials.Fluorotoluene.getFluidOrGas(1000),
                Materials.Chlorine.getGas(6000),
                Materials.Water.getFluid(1000),
                GTNLMaterials.FluoroBenzene.getFluidOrGas(1000))
            .fluidOutputs(Materials.HydrochloricAcid.getFluid(6000))
            .duration(100)
            .eut(TierEU.RECIPE_EV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(
                GTNLMaterials.Difluorobenzophenone.get(OrePrefixes.dust, 24),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodaAsh, 6))
            .itemOutputs(MaterialsFluorides.SODIUM_FLUORIDE.getDust(4))
            .fluidInputs(GTNLMaterials.Hydroquinone.getFluidOrGas(1000))
            .fluidOutputs(
                GTNLMaterials.Polyetheretherketone.getMolten(2592),
                Materials.Water.getFluid(1000),
                Materials.CarbonDioxide.getGas(1000))
            .duration(250)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 6))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodaAsh, 6))
            .fluidInputs(Materials.CarbonDioxide.getGas(1000))
            .fluidOutputs(Materials.Water.getFluid(1000))
            .duration(80)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Beryllium, 1))
            .fluidInputs(Materials.Fluorine.getGas(4000))
            .fluidOutputs(MaterialsNuclides.Li2BeF4.getFluidStack(1008))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(UC);

        RecipeBuilder.builder()
            .fluidInputs(
                Materials.NitricAcid.getFluid(3000),
                Materials.Benzene.getFluid(1000),
                Materials.Hydrogen.getGas(5000))
            .fluidOutputs(Materials.Water.getFluid(4000), GTNLMaterials.SmallBaka.getFluidOrGas(1000))
            .duration(20)
            .eut(TierEU.RECIPE_HV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Plastic, 4))
            .itemOutputs(GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockITNT", 32))
            .fluidInputs(GTNLMaterials.SmallBaka.getFluidOrGas(2000))
            .duration(100)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.UraniumSlag.get(OrePrefixes.dust, 1))
            .itemOutputs(GTNLMaterials.UraniumChlorideSlag.get(OrePrefixes.dust, 1))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(4000))
            .fluidOutputs(Materials.Hydrogen.getGas(4000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 11))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cinnabar, 16))
            .fluidInputs(GTNLMaterials.ToxicMercurySludge.getFluidOrGas(12000))
            .fluidOutputs(GTNLMaterials.PostProcessBeWaste.getFluidOrGas(12000))
            .duration(300)
            .eut(TierEU.RECIPE_MV)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphate, 10))
            .fluidInputs(Materials.Hydrogen.getGas(6000))
            .fluidOutputs(Materials.PhosphoricAcid.getFluid(2000))
            .duration(40)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        RecipeBuilder.builder()
            .itemInputs(
                aeMaterials.singularity()
                    .maybeStack(1)
                    .orNull(),
                aeMaterials.skyDust()
                    .maybeStack(8)
                    .orNull(),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnderPearl, 8))
            .itemOutputs(GTNLItemList.ShatteredSingularity.get(2))
            .fluidInputs(Materials.Lava.getFluid(1000))
            .duration(100)
            .eut(TierEU.RECIPE_EV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 7),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 1))
            .fluidInputs(GGMaterial.thoriumNitrate.getFluidOrGas(1000))
            .fluidOutputs(GTNLMaterials.GlowThorium.getFluidOrGas(1000), Materials.NitrogenDioxide.getGas(2000))
            .duration(40)
            .eut(TierEU.RECIPE_EV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(Materials.Hydrogen.getGas(2000), Materials.Oxygen.getGas(2000))
            .fluidOutputs(new FluidStack(GTPPFluids.HydrogenPeroxide, 1000))
            .duration(40)
            .eut(TierEU.RECIPE_LuV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 6))
            .fluidInputs(Materials.Hydrogen.getGas(6000))
            .fluidOutputs(Materials.Benzene.getFluid(1000))
            .duration(10)
            .eut(TierEU.RECIPE_IV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.NetherStar, 2),
                GTUtility.getIntegratedCircuit(20))
            .fluidInputs(FluidRegistry.getFluidStack("mobessence", 5_000))
            .fluidOutputs(new FluidStack(GTPPFluids.GeneticMutagen, 8_000))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_HV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .fluidInputs(Materials.Ammonia.getGas(1000), Materials.SulfuricAcid.getFluid(1000))
            .fluidOutputs(GTNLMaterials.AmmoniumBisulfate.getFluidOrGas(1000))
            .duration(15 * SECONDS)
            .eut(TierEU.EV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.AmmoniumPersulfate.getFluidOrGas(1000), Materials.Water.getFluid(2000))
            .fluidOutputs(
                GTNLMaterials.AmmoniumBisulfate.getFluidOrGas(2000),
                new FluidStack(GTPPFluids.HydrogenPeroxide, 1000))
            .duration(30 * SECONDS)
            .eut(TierEU.IV)
            .addTo(MCRR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 2))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumOxide, 3))
            .fluidInputs(Materials.Oxygen.getGas(1000))
            .duration(3)
            .eut(TierEU.RECIPE_LV)
            .addTo(UC);
    }
}
