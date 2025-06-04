package com.science.gtnl.common.recipe.GregTech;

import static com.science.gtnl.Utils.Utils.setStackSize;
import static gregtech.api.enums.Mods.IndustrialCraft2;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.nuclear.MaterialsFluorides;
import gtPlusPlus.core.material.nuclear.MaterialsNuclides;

public class ChemicalRecipes implements IRecipePool {

    final IRecipeMap UC = GTRecipeConstants.UniversalChemical;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialPool.CrudeHexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 36),
                MaterialPool.SilicaGel.get(OrePrefixes.dust, 3))
            .fluidInputs(MaterialPool.Ethylenediamine.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.Hexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 36))
            .specialValue(0)
            .duration(100)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialPool.CrudeHexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 36),
                GTUtility.copyAmount(0, GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2052)))
            .fluidInputs(FluidRegistry.getFluidStack("ammonia", 1000), MaterialPool.Ethanolamine.getFluidOrGas(1000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.Ethylenediamine.getFluidOrGas(1000), FluidRegistry.getFluidStack("water", 1000))
            .specialValue(0)
            .duration(180)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                FluidRegistry.getFluidStack("ethyleneoxide", 1000),
                FluidRegistry.getFluidStack("ammonia", 1000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.Ethanolamine.getFluidOrGas(1000))
            .specialValue(0)
            .duration(60)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialPool.Tetraacetyldinitrohexaazaisowurtzitane.get(OrePrefixes.dust, 46),
                MaterialPool.NitroniumTetrafluoroborate.get(OrePrefixes.dust, 48))
            .fluidInputs(FluidRegistry.getFluidStack("water", 4000))
            .itemOutputs(
                MaterialPool.CrudeHexanitrohexaazaisowurtzitane.get(OrePrefixes.dust, 46),
                MaterialPool.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 14))
            .fluidOutputs(
                MaterialPool.FluoroboricAcide.getFluidOrGas(4000),
                FluidRegistry.getFluidStack("aceticacid", 4000))
            .specialValue(0)
            .duration(200)
            .eut(491520)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                new FluidStack(GTPPFluids.BoricAcid, 1000),
                FluidRegistry.getFluidStack("hydrofluoricacid_gt5u", 4000))
            .itemOutputs()
            .fluidOutputs(
                MaterialPool.FluoroboricAcide.getFluidOrGas(1000),
                FluidRegistry.getFluidStack("water", 13000))
            .specialValue(0)
            .duration(150)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                MaterialPool.BoronFluoride.getFluidOrGas(1000),
                FluidRegistry.getFluidStack("hydrofluoricacid_gt5u", 1000),
                FluidRegistry.getFluidStack("nitricacid", 1000))
            .itemOutputs(MaterialPool.NitroniumTetrafluoroborate.get(OrePrefixes.dust, 8))
            .fluidOutputs(FluidRegistry.getFluidStack("water", 1000))
            .specialValue(0)
            .duration(40)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                MaterialPool.SodiumTetrafluoroborate.get(OrePrefixes.dust, 6))
            .itemOutputs(GTModHandler.getModItem("miscutils", "itemDustSodiumFluoride", 2))
            .fluidOutputs(MaterialPool.BoronFluoride.getFluidOrGas(1000))
            .specialValue(0)
            .duration(120)
            .eut(125)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.BoronTrioxide.get(OrePrefixes.dust, 5))
            .fluidInputs(FluidRegistry.getFluidStack("hydrofluoricacid_gt5u", 6000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.BoronFluoride.getFluidOrGas(2000))
            .specialValue(0)
            .duration(200)
            .eut(480)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(MaterialPool.Dibenzyltetraacetylhexaazaisowurtzitane.get(OrePrefixes.dust, 1), 64 + 6),
                MaterialPool.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 42))
            .fluidInputs(FluidRegistry.getFluidStack("water", 14000))
            .itemOutputs(MaterialPool.Tetraacetyldinitrohexaazaisowurtzitane.get(OrePrefixes.dust, 46))
            .fluidOutputs(
                FluidRegistry.getFluidStack("hydrofluoricacid_gt5u", 24000),
                new FluidStack(GTPPFluids.BoricAcid, 6000),
                FluidRegistry.getFluidStack("nitricoxide", 4000),
                MaterialPool.Benzaldehyde.getFluidOrGas(2000))
            .specialValue(0)
            .duration(140)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                MaterialPool.BoronFluoride.getFluidOrGas(2000),
                FluidRegistry.getFluidStack("hydrofluoricacid_gt5u", 2000),
                FluidRegistry.getFluidStack("dinitrogentetroxide", 2000))
            .itemOutputs(MaterialPool.NitronsoniumTetrafluoroborate.get(OrePrefixes.dust, 7))
            .fluidOutputs(FluidRegistry.getFluidStack("nitricacid", 1000))
            .specialValue(0)
            .duration(120)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(MaterialPool.SuccinimidylAcetate.get(OrePrefixes.dust, 1), 64 + 8),
                setStackSize(MaterialPool.Hexabenzylhexaazaisowurtzitane.get(OrePrefixes.dust, 1), 64 + 38))
            .fluidInputs(MaterialPool.HydrobromicAcid.getFluidOrGas(100), FluidRegistry.getFluidStack("hydrogen", 8000))
            .itemOutputs(
                setStackSize(MaterialPool.Dibenzyltetraacetylhexaazaisowurtzitane.get(OrePrefixes.dust, 1), 64 + 6))
            .fluidOutputs(FluidRegistry.getFluidStack("liquid_toluene", 6000))
            .specialValue(0)
            .duration(120)
            .eut(122880)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.NHydroxysuccinimide.get(OrePrefixes.dust, 13))
            .fluidInputs(MaterialMisc.ACETIC_ANHYDRIDE.getFluidStack(1000))
            .itemOutputs(MaterialPool.SuccinimidylAcetate.get(OrePrefixes.dust, 18))
            .fluidOutputs(Materials.AceticAcid.getFluid(1000))
            .specialValue(0)
            .duration(80)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 6, 2017),
                setStackSize(MaterialPool.SuccinicAnhydride.get(OrePrefixes.dust, 1), 64 + 2))
            .fluidInputs(
                FluidRegistry.getFluidStack("methanol", 40000),
                FluidRegistry.getFluidStack("liquid_toluene", 6000),
                MaterialPool.HydroxylamineHydrochloride.getFluidOrGas(6000))
            .itemOutputs(
                MaterialPool.NHydroxysuccinimide.get(OrePrefixes.dust, 13),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 12, 2817))
            .fluidOutputs(FluidRegistry.getFluidStack("water", 6000), FluidRegistry.getFluidStack("hydrogen", 6000))
            .specialValue(0)
            .duration(220)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialPool.HydroxylammoniumSulfate.get(OrePrefixes.dust, 17),
                MaterialPool.BariumChloride.get(OrePrefixes.dust, 3))
            .fluidInputs()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 6, 2904))
            .fluidOutputs(
                MaterialPool.HydroxylamineHydrochloride.getFluidOrGas(2000),
                FluidRegistry.getFluidStack("water", 1000))
            .specialValue(0)
            .duration(100)
            .eut(480)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 6, 2063))
            .fluidInputs(FluidRegistry.getFluidStack("hydrochloricacid_gt5u", 2000))
            .itemOutputs(MaterialPool.BariumChloride.get(OrePrefixes.dust, 3))
            .fluidOutputs(FluidRegistry.getFluidStack("hydrogen", 2000))
            .specialValue(0)
            .duration(60)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.bromine", 1000),
                FluidRegistry.getFluidStack("hydrogen", 1000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.HydrobromicAcid.getFluidOrGas(1000))
            .specialValue(0)
            .duration(300)
            .eut(480)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.PotassiumHydroxylaminedisulfonate.get(OrePrefixes.dust, 26))
            .fluidInputs(FluidRegistry.getFluidStack("water", 4000))
            .itemOutputs(
                MaterialPool.HydrobromicAcid.get(OrePrefixes.dust, 17),
                MaterialPool.PotassiumSulfate.get(OrePrefixes.dust, 14))
            .fluidOutputs(FluidRegistry.getFluidStack("sulfuricacid", 1000))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.PotassiumBisulfite.get(OrePrefixes.dust, 12))
            .fluidInputs(MaterialPool.NitrousAcid.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.PotassiumHydroxylaminedisulfonate.get(OrePrefixes.dust, 13))
            .fluidOutputs(FluidRegistry.getFluidStack("water", 1000))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.SodiumNitrite.get(OrePrefixes.dust, 4))
            .fluidInputs(FluidRegistry.getFluidStack("sulfuricacid", 1000))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 7, 2630))
            .fluidOutputs(MaterialPool.NitrousAcid.getFluidOrGas(1000))
            .specialValue(0)
            .duration(80)
            .eut(30)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, MaterialPool.CoAcAbCatalyst.get(OrePrefixes.dust, 1)))
            .fluidInputs(MaterialPool.SodiumNitrateSolution.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.SodiumNitrite.get(OrePrefixes.dust, 4))
            .fluidOutputs(FluidRegistry.getFluidStack("oxygen", 1000), FluidRegistry.getFluidStack("water", 1000))
            .specialValue(0)
            .duration(300)
            .eut(30)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 2, 2536),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2033),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 17599))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 1000), FluidRegistry.getFluidStack("acetylene", 1000))
            .itemOutputs(MaterialPool.CoAcAbCatalyst.get(OrePrefixes.dust, 1))
            .fluidOutputs(
                FluidRegistry.getFluidStack("hydrogen", 4000),
                FluidRegistry.getFluidStack("carbonmonoxide", 1000))
            .specialValue(0)
            .duration(20)
            .eut(1966080)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 6, 101))
            .fluidInputs(FluidRegistry.getFluidStack("sulfurdioxide", 2000), FluidRegistry.getFluidStack("water", 1000))
            .itemOutputs(MaterialPool.PotassiumBisulfite.get(OrePrefixes.dust, 12))
            .fluidOutputs(FluidRegistry.getFluidStack("carbondioxide", 1000))
            .specialValue(0)
            .duration(160)
            .eut(480)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.Acetonitrile.get(OrePrefixes.dust, 6))
            .fluidInputs(MaterialPool.Benzylamine.getFluidOrGas(6000), MaterialPool.Glyoxal.getFluidOrGas(3000))
            .itemOutputs(setStackSize(MaterialPool.Hexabenzylhexaazaisowurtzitane.get(OrePrefixes.dust, 1), 64 + 38))
            .specialValue(0)
            .duration(100)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                FluidRegistry.getFluidStack("nitricacid", 2000),
                FluidRegistry.getFluidStack("carbondioxide", 2000))
            .itemOutputs()
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 3000),
                MaterialPool.Glyoxal.getFluidOrGas(2000),
                FluidRegistry.getFluidStack("nitrogendioxide", 1000))
            .specialValue(0)
            .duration(120)
            .eut(60)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                FluidRegistry.getFluidStack("ammonia", 1000),
                FluidRegistry.getFluidStack("carbonmonoxide", 2000),
                FluidRegistry.getFluidStack("hydrogen", 4000))
            .itemOutputs(MaterialPool.Acetonitrile.get(OrePrefixes.dust, 6))
            .fluidOutputs(FluidRegistry.getFluidStack("water", 2000))
            .specialValue(0)
            .duration(200)
            .eut(1966080)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.Hexamethylenetetramine.get(OrePrefixes.dust, 22))
            .fluidInputs(
                FluidRegistry.getFluidStack("water", 6000),
                FluidRegistry.getFluidStack("hydrochloricacid_gt5u", 2000),
                MaterialPool.BenzylChloride.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.AmmoniumChloride.get(OrePrefixes.dust, 18))
            .fluidOutputs(
                MaterialPool.Benzylamine.getFluidOrGas(1000),
                FluidRegistry.getFluidStack("fluid.formaldehyde", 6000))
            .specialValue(0)
            .duration(200)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(
                FluidRegistry.getFluidStack("fluid.formaldehyde", 4000),
                FluidRegistry.getFluidStack("ammonia", 6000))
            .itemOutputs(MaterialPool.Hexamethylenetetramine.get(OrePrefixes.dust, 22))
            .fluidOutputs(FluidRegistry.getFluidStack("water", 6000))
            .specialValue(0)
            .duration(160)
            .eut(30)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.SuccinicAcid.get(OrePrefixes.dust, 14))
            .fluidInputs(FluidRegistry.getFluidStack("molten.aceticanhydride", 1000))
            .itemOutputs(MaterialPool.SuccinicAnhydride.get(OrePrefixes.dust, 11))
            .fluidOutputs(FluidRegistry.getFluidStack("aceticacid", 2000))
            .specialValue(0)
            .duration(20)
            .eut(7680)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 88)))
            .fluidInputs(
                FluidRegistry.getFluidStack("water", 1000),
                FluidRegistry.getFluidStack("hydrogen", 1000),
                MaterialPool.MaleicAnhydride.getFluidOrGas(2000))
            .itemOutputs(MaterialPool.SuccinicAcid.get(OrePrefixes.dust, 14))
            .specialValue(0)
            .duration(200)
            .eut(1920)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2090)))
            .fluidInputs(FluidRegistry.getFluidStack("oxygen", 7000), FluidRegistry.getFluidStack("butane", 1000))
            .itemOutputs()
            .fluidOutputs(FluidRegistry.getFluidStack("water", 4000), MaterialPool.MaleicAnhydride.getFluidOrGas(1000))
            .specialValue(0)
            .duration(280)
            .eut(480)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.PyromelliticDianhydride.get(OrePrefixes.dust, 18))
            .fluidInputs(MaterialPool.Oxydianiline.getFluidOrGas(1000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.PloyamicAcid.getFluidOrGas(1000))
            .specialValue(0)
            .duration(400)
            .eut(122880)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2057)))
            .fluidInputs(FluidRegistry.getFluidStack("aniline", 2000), FluidRegistry.getFluidStack("phenol", 1000))
            .itemOutputs()
            .fluidOutputs(MaterialPool.Oxydianiline.getFluidOrGas(1000), FluidRegistry.getFluidStack("methane", 2000))
            .specialValue(0)
            .duration(150)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.Durene.get(OrePrefixes.dust, 24))
            .fluidInputs(FluidRegistry.getFluidStack("oxygen", 12000))
            .itemOutputs(MaterialPool.PyromelliticDianhydride.get(OrePrefixes.dust, 18))
            .fluidOutputs(FluidRegistry.getFluidStack("water", 6000))
            .specialValue(0)
            .duration(150)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs()
            .fluidInputs(
                FluidRegistry.getFluidStack("chloromethane", 2000),
                FluidRegistry.getFluidStack("dimethylbenzene", 1000))
            .itemOutputs(MaterialPool.Durene.get(OrePrefixes.dust, 24))
            .fluidOutputs(FluidRegistry.getFluidStack("hydrochloricacid_gt5u", 2000))
            .specialValue(0)
            .duration(150)
            .eut(120)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .fluidInputs(
                MaterialPool.RareEarthHydroxides.getFluidOrGas(1000),
                Materials.HydrochloricAcid.getFluid(1000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 3))
            .fluidOutputs(MaterialPool.RareEarthChlorides.getFluidOrGas(1000))
            .specialValue(0)
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
            .specialValue(0)
            .duration(130)
            .eut(TierEU.RECIPE_LuV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.BlackLight.get(1)))
            .fluidInputs(
                GGMaterial.fluoroantimonicAcid.getFluidOrGas(1000),
                Materials.Methane.getGas(1000),
                MaterialPool.FluoroBenzene.getFluidOrGas(1000))
            .itemOutputs(MaterialPool.AntimonyTrifluoride.get(OrePrefixes.dust, 4))
            .fluidOutputs(MaterialPool.Fluorotoluene.getFluidOrGas(1000), Materials.Water.getFluid(4000))
            .specialValue(0)
            .duration(150)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.AntimonyTrioxide, 5))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(6000))
            .itemOutputs(MaterialPool.AntimonyTrifluoride.get(OrePrefixes.dust, 8))
            .fluidOutputs(Materials.Water.getFluid(3000))
            .specialValue(0)
            .duration(60)
            .eut(TierEU.RECIPE_LV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.AntimonyTrifluoride.get(OrePrefixes.dust, 4))
            .fluidInputs(Materials.HydrofluoricAcid.getFluid(4000))
            .fluidOutputs(GGMaterial.fluoroantimonicAcid.getFluidOrGas(1000), Materials.Hydrogen.getGas(2000))
            .specialValue(0)
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.AntimonyTrifluoride.get(OrePrefixes.dust, 4))
            .fluidInputs(Materials.Fluorine.getGas(2000))
            .fluidOutputs(GGMaterial.antimonyPentafluoride.getFluidOrGas(1000))
            .specialValue(0)
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
            .specialValue(0)
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.ZnFeAlClCatalyst.get(1)))
            .itemOutputs(MaterialPool.Difluorobenzophenone.get(OrePrefixes.dust, 24))
            .fluidInputs(
                MaterialPool.Fluorotoluene.getFluidOrGas(1000),
                Materials.Chlorine.getGas(6000),
                Materials.Water.getFluid(1000),
                MaterialPool.FluoroBenzene.getFluidOrGas(1000))
            .fluidOutputs(Materials.HydrochloricAcid.getFluid(6000))
            .specialValue(0)
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
            .specialValue(0)
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
            .specialValue(0)
            .duration(80)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Beryllium, 1))
            .fluidInputs(Materials.Fluorine.getGas(4000))
            .fluidOutputs(MaterialsNuclides.Li2BeF4.getFluidStack(1008))
            .specialValue(0)
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
            .specialValue(0)
            .duration(20)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Plastic, 1))
            .itemOutputs(GTModHandler.getModItem(IndustrialCraft2.ID, "blockITNT", 32))
            .fluidInputs(MaterialPool.SmallBaka.getFluidOrGas(2000))
            .specialValue(0)
            .duration(100)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialPool.UraniumSlag.get(OrePrefixes.dust, 1))
            .itemOutputs(MaterialPool.UraniumChlorideSlag.get(OrePrefixes.dust, 1))
            .fluidInputs(Materials.HydrochloricAcid.getFluid(4000))
            .fluidOutputs(Materials.Hydrogen.getGas(4000))
            .specialValue(0)
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(UC);
    }
}
