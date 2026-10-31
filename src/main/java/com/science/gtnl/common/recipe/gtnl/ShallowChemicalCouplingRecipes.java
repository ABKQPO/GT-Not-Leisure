package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.compat.forestry.ForestryRecipes;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialMisc;

public class ShallowChemicalCouplingRecipes implements IRecipePool {

    public RecipeMap<?> SCCR = GTNLRecipeMaps.ShallowChemicalCouplingRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 0),
                Materials.Potassiumdichromate.getDust(0),
                Materials.Copper.getDust(0),
                Materials.Zinc.getDust(0),
                GTUtility.copyAmountUnsafe(72, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
            .circuit(1)
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
                Materials.Carbon.getDust(2),
                Materials.Silicon.getDust(1))
            .circuit(2)
            .fluidInputs(Materials.Hydrogen.getGas(8000), Materials.Chlorine.getGas(4000))
            .fluidOutputs(
                Materials.RubberSilicone.getMolten(1296),
                Materials.HydrochloricAcid.getFluid(11520),
                Materials.Water.getFluid(2000))
            .metadata(COIL_HEAT, 8000)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 12),
                Materials.Sulfur.getDust(1))
            .circuit(3)
            .fluidInputs(Materials.Hydrogen.getGas(15000), Materials.Oxygen.getGas(6000))
            .fluidOutputs(Materials.StyreneButadieneRubber.getMolten(1440))
            .metadata(COIL_HEAT, 8000)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 6),
                Materials.Sodium.getDust(2),
                Materials.Sulfur.getDust(1))
            .circuit(4)
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
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 43))
            .circuit(5)
            .fluidInputs(Materials.Hydrogen.getGas(77000), Materials.Oxygen.getGas(49000))
            .fluidOutputs(Materials.GlueAdvanced.getFluid(15000), Materials.Water.getFluid(4000))
            .metadata(COIL_HEAT, 8000)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 21),
                Materials.SodiumHydroxide.getDust(9))
            .circuit(6)
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
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 2))
            .circuit(7)
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
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 6),
                Materials.Sulfur.getDust(1))
            .circuit(8)
            .fluidInputs(Materials.Hydrogen.getGas(18000), Materials.Oxygen.getGas(27000))
            .fluidOutputs(Materials.Polyethylene.getMolten(4608))
            .metadata(COIL_HEAT, 7200)
            .duration(30 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Copper, 40),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polyethylene, 4),
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

        // Single-step polyimide process.
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 0),
                GTUtility.copyAmountUnsafe(220, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
            .circuit(9)
            .fluidInputs(
                Materials.Oxygen.getGas(60000),
                Materials.Nitrogen.getGas(20000),
                Materials.Hydrogen.getGas(120000))
            .fluidOutputs(GTNLMaterials.Polyimide.getMolten(1440))
            .metadata(COIL_HEAT, 10800)
            .duration(400)
            .eut(TierEU.RECIPE_UV)
            .addTo(SCCR);

        // Single-step polyether ether ketone process.
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 0),
                GTNLItemList.ZnFeAlClCatalyst.get(0),
                GTNLItemList.BlackLight.get(0),
                GTNLMaterials.CoAcAbCatalyst.get(OrePrefixes.dust, 0),
                GTUtility.copyAmountUnsafe(400, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
            .circuit(10)
            .fluidInputs(Materials.Oxygen.getGas(60000), Materials.Hydrogen.getGas(240000))
            .fluidOutputs(GTNLMaterials.Polyetheretherketone.getMolten(2880))
            .metadata(COIL_HEAT, 11700)
            .duration(600)
            .eut(TierEU.RECIPE_UHV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Neutronium, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 60))
            .circuit(11)
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
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 45),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 13))
            .circuit(12)
            .fluidInputs(Materials.Hydrogen.getGas(72000))
            .fluidOutputs(Materials.Rubber.getMolten(16848))
            .metadata(COIL_HEAT, 4500)
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 20))
            .circuit(13)
            .fluidInputs(Materials.Hydrogen.getGas(30000), Materials.Chlorine.getGas(10000))
            .fluidOutputs(Materials.PolyvinylChloride.getMolten(12960))
            .metadata(COIL_HEAT, 6300)
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(SCCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Silver, 0),
                ItemList.Spinneret.get(0),
                GTUtility.copyAmountUnsafe(65, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
            .circuit(14)
            .fluidInputs(
                Materials.Hydrogen.getGas(52000),
                Materials.Nitrogen.getGas(8000),
                Materials.Oxygen.getGas(14000))
            .fluidOutputs(
                Materials.LiquidCrystalKevlar.getFluid(1000),
                Materials.PolyurethaneResin.getFluid(1000),
                Materials.Kevlar.getMolten(144))
            .metadata(COIL_HEAT, 11700)
            .duration(200)
            .eut(TierEU.RECIPE_UEV)
            .addTo(SCCR);

        ForestryRecipes.shallowChemicalCoupling();
    }

}
