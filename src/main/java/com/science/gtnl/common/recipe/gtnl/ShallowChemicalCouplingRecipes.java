package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;
import gtPlusPlus.core.material.MaterialMisc;

public class ShallowChemicalCouplingRecipes implements IRecipePool {

    public RecipeMap<?> SCCR = GTNLRecipeMaps.ShallowChemicalCouplingRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 0),
                GTUtility.getIntegratedCircuit(1),
                Materials.Potassiumdichromate.getDust(0),
                Materials.Copper.getDust(0),
                Materials.Zinc.getDust(0),
                GTUtility.copyAmountUnsafe(72, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
            .fluidInputs(
                Materials.Oxygen.getGas(54000),
                Materials.Hydrogen.getGas(144000),
                Materials.Nitrogen.getGas(18000),
                Materials.SulfuricAcid.getFluid(9000),
                Materials.Chlorobenzene.getFluid(18000),
                Materials.NitrationMixture.getFluid(36000))
            .fluidOutputs(Materials.Polybenzimidazole.getMolten(11520), Materials.HydrochloricAcid.getFluid(18000))
            .metadata(COIL_HEAT, 9900)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTUtility.getIntegratedCircuit(2),
                Materials.Carbon.getDust(2),
                Materials.Silicon.getDust(1))
            .fluidInputs(Materials.Hydrogen.getGas(8000), Materials.Chlorine.getGas(4000))
            .fluidOutputs(
                Materials.Silicone.getMolten(1296),
                Materials.HydrochloricAcid.getFluid(11520),
                Materials.Water.getFluid(2000))
            .metadata(COIL_HEAT, 8000)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 12),
                Materials.Sulfur.getDust(1))
            .fluidInputs(Materials.Hydrogen.getGas(15000), Materials.Oxygen.getGas(6000))
            .fluidOutputs(Materials.StyreneButadieneRubber.getMolten(1440))
            .metadata(COIL_HEAT, 8000)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTUtility.getIntegratedCircuit(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 6),
                Materials.Sodium.getDust(2),
                Materials.Sulfur.getDust(1))
            .fluidInputs(
                Materials.Chlorine.getGas(4000),
                Materials.Hydrogen.getGas(6000),
                Materials.Oxygen.getGas(8000))
            .itemOutputs(Materials.Salt.getDust(4))
            .fluidOutputs(Materials.PolyphenyleneSulfide.getMolten(2592), Materials.HydrochloricAcid.getFluid(2000))
            .metadata(COIL_HEAT, 9900)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTUtility.getIntegratedCircuit(5),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 43))
            .fluidInputs(Materials.Hydrogen.getGas(77000), Materials.Oxygen.getGas(49000))
            .fluidOutputs(Materials.AdvancedGlue.getFluid(15000), Materials.Water.getFluid(4000))
            .metadata(COIL_HEAT, 8000)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTUtility.getIntegratedCircuit(6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 21),
                Materials.SodiumHydroxide.getDust(9))
            .fluidInputs(
                Materials.Hydrogen.getGas(24000),
                Materials.Chlorine.getGas(8000),
                Materials.Water.getFluid(5000))
            .fluidOutputs(
                Materials.Epoxid.getMolten(1000),
                Materials.HydrochloricAcid.getFluid(3000),
                Materials.SaltWater.getFluid(4000))
            .metadata(COIL_HEAT, 9900)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTUtility.getIntegratedCircuit(7),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 2))
            .fluidInputs(
                Materials.Oxygen.getGas(7000),
                Materials.Hydrogen.getGas(12000),
                Materials.Fluorine.getGas(4000),
                Materials.Chlorine.getGas(12000),
                Materials.Methane.getGas(2000))
            .fluidOutputs(Materials.Polytetrafluoroethylene.getMolten(2592), Materials.HydrochloricAcid.getFluid(12000))
            .metadata(COIL_HEAT, 9900)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTUtility.getIntegratedCircuit(8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 6),
                Materials.Sulfur.getDust(1))
            .fluidInputs(Materials.Hydrogen.getGas(18000), Materials.Oxygen.getGas(27000))
            .fluidOutputs(Materials.Plastic.getMolten(4608))
            .metadata(COIL_HEAT, 7200)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Copper, 40),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Plastic, 4),
                Materials.Sulfur.getDust(2),
                Materials.Iron.getDust(1))
            .fluidInputs(Materials.Oxygen.getGas(6000), Materials.Water.getFluid(2000), Materials.Chlorine.getGas(3000))
            .itemOutputs(ItemList.Circuit_Board_Plastic_Advanced.get(4L))
            .metadata(COIL_HEAT, 5400)
            .duration(24 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Copper, 32),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.PolyvinylChloride, 1),
                Materials.Sulfur.getDust(1),
                Materials.Iron.getDust(1))
            .fluidInputs(Materials.Oxygen.getGas(3000), Materials.Water.getFluid(1000), Materials.Chlorine.getGas(3000))
            .itemOutputs(ItemList.Circuit_Board_Plastic_Advanced.get(4L))
            .metadata(COIL_HEAT, 5400)
            .duration(24 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Copper, 56),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 2),
                Materials.Sulfur.getDust(1),
                Materials.Iron.getDust(2))
            .fluidInputs(Materials.Oxygen.getGas(3000), Materials.Water.getFluid(1000), Materials.Chlorine.getGas(6000))
            .itemOutputs(ItemList.Circuit_Board_Plastic_Advanced.get(8L))
            .metadata(COIL_HEAT, 5400)
            .duration(24 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTUtility.copyAmountUnsafe(104, GTOreDictUnificator.get(OrePrefixes.foil, Materials.Copper, 1)),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polybenzimidazole, 2),
                Materials.Sulfur.getDust(1),
                Materials.Iron.getDust(4))
            .fluidInputs(
                Materials.Oxygen.getGas(3000),
                Materials.Water.getFluid(1000),
                Materials.Chlorine.getGas(12000))
            .itemOutputs(ItemList.Circuit_Board_Plastic_Advanced.get(16L))
            .metadata(COIL_HEAT, 3800)
            .duration(24 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(SCCR);

        // 单步聚酰亚胺
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 0),
                GTUtility.getIntegratedCircuit(9),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 0),
                GTUtility.copyAmountUnsafe(220, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
            .fluidInputs(
                Materials.Oxygen.getGas(60000),
                Materials.Nitrogen.getGas(20000),
                Materials.Hydrogen.getGas(120000))
            .fluidOutputs(GTNLMaterials.Polyimide.getMolten(1440))
            .metadata(COIL_HEAT, 10800)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SCCR);

        // 单步聚醚醚酮
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 0),
                GTUtility.getIntegratedCircuit(10),
                GTNLItemList.ZnFeAlClCatalyst.get(0),
                GTNLItemList.BlackLight.get(0),
                GTNLMaterials.CoAcAbCatalyst.get(OrePrefixes.dust, 0),
                GTUtility.copyAmountUnsafe(400, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
            .fluidInputs(Materials.Oxygen.getGas(60000), Materials.Hydrogen.getGas(240000))
            .fluidOutputs(GTNLMaterials.Polyetheretherketone.getMolten(2880))
            .metadata(COIL_HEAT, 11700)
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 0),
                GTUtility.getIntegratedCircuit(11),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 60))
            .fluidInputs(
                Materials.Oxygen.getGas(20000),
                Materials.Hydrogen.getGas(70000),
                Materials.Nitrogen.getGas(10000))
            .fluidOutputs(MaterialMisc.ETHYL_CYANOACRYLATE.getFluidStack(10000))
            .metadata(COIL_HEAT, 10800)
            .duration(200)
            .eut(TierEU.RECIPE_UV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTUtility.getIntegratedCircuit(12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 45),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 13))
            .fluidInputs(Materials.Hydrogen.getGas(72000))
            .fluidOutputs(Materials.Rubber.getMolten(16848))
            .metadata(COIL_HEAT, 4500)
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTUtility.getIntegratedCircuit(13),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 20))
            .fluidInputs(Materials.Hydrogen.getGas(30000), Materials.Chlorine.getGas(10000))
            .fluidOutputs(Materials.PolyvinylChloride.getMolten(12960))
            .metadata(COIL_HEAT, 6300)
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 0),
                GTUtility.getIntegratedCircuit(14),
                ItemList.Spinneret.get(0),
                GTUtility.copyAmountUnsafe(65, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
            .fluidInputs(
                Materials.Hydrogen.getGas(52000),
                Materials.Nitrogen.getGas(8000),
                Materials.Oxygen.getGas(14000))
            .fluidOutputs(
                MaterialsKevlar.LiquidCrystalKevlar.getFluid(1000),
                MaterialsKevlar.PolyurethaneResin.getFluid(1000),
                MaterialsKevlar.Kevlar.getMolten(144))
            .metadata(COIL_HEAT, 11700)
            .duration(200)
            .eut(TierEU.RECIPE_UEV)
            .addTo(SCCR);

        if (Mods.Forestry.isModLoaded()) loadCombsRecipes();
    }

    public void loadCombsRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LIGNIE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lignite, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lignite, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.COAL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.OIL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oilsands, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oilsands, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.APATITE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Apatite, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Apatite, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.AMBER, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Amber, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Amber, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MERCURY, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cinnabar, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cinnabar, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.STONE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Soapstone, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Soapstone, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.CERTUS, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartz, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartzCharged, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.REDSTONE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LAPIS, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.RUBY, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ruby, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ruby, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.REDGARNET, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetRed, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetRed, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.YELLOWGARNET, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetYellow, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetYellow, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.SAPPHIRE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sapphire, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sapphire, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.DIAMOND, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.OLIVINE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Olivine, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Olivine, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.EMERALD, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.FIRESTONE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.PYROPE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Pyrope, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Pyrope, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.GROSSULAR, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Grossular, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Grossular, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.COPPER, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.TIN, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LEAD, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.NICKEL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ZINC, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.SILVER, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.CRYOLITE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cryolite, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cryolite, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.GOLD, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.SULFUR, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.GALLIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ARSENIC, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.IRON, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.BAUXITE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bauxite, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bauxite, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ALUMINIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MANGANESE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MAGNESIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MOLYBDENUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ALMANDINE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Almandine, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Almandine, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.NEODYMIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3602)
            .duration(4 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_HV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LITHIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3602)
            .duration(4 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_HV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ELECTROTINE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrotine, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrotine, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3602)
            .duration(4 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_HV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.THORIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3602)
            .duration(4 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MITHRIL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mithril, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mithril, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.SHADOWMETAL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Shadow, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Shadow, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.DIVIDED, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Unstable, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Unstable, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.CHROME, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ENDIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.HeeEndium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.HeeEndium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.METEORICIRON, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeteoricIron, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeteoricIron, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.PLATINUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 2L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.BEDROCKIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.SPARKLING, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.TITANIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.URANIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.PLUTONIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.DESH, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Desh, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Desh, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LEDOX, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ledox, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ledox, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.TUNGSTEN, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.PALLADIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.DRACONIC, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.NAQUADAH, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LUTETIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.CALLISTOICE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CallistoIce, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CallistoIce, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MYTRYL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.QUANTIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Quantium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Quantium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ORIHARUKON, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.INFUSEDGOLD, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedGold, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedGold, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.IRIDIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 2L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.OSMIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.INDIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Indium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Indium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.EUROPIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 6100)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.NAQUADRIA, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 6100)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.AMERICIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 6100)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MYSTERIOUSCRYSTAL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MysteriousCrystal, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MysteriousCrystal, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 6100)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.BLACKPLUTONIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 6100)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.AWAKENEDDRACONIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 7200)
            .duration(8 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.TRINIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 7200)
            .duration(8 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.NEUTRONIUM, 4))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 4L))
            .fluidOutputs(Materials.Neutronium.getMolten(1152))
            .outputChances(5000)
            .metadata(COIL_HEAT, 8000)
            .duration(8 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(SCCR);
    }
}
