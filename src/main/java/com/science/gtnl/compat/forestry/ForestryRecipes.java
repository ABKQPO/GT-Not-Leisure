package com.science.gtnl.compat.forestry;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class ForestryRecipes {

    public static void autoclave() {
        if (!Mods.Forestry.isModLoaded()) return;
        RecipeBuilder.builder() // Prismarine + Comb
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(GregtechItemList.RedAlgaeBiomass.get(32), GTBees.combs.getStackForType(CombType.PRISMATIC, 32))
            .fluidInputs(Materials.Grade1PurifiedWater.getFluid(8000))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.shard, Materials.Prismarine, 32))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(RecipeMaps.autoclaveRecipes);
    }

    public static void blastFurnace() {
        if (!Mods.Forestry.isModLoaded()) return;
        RecipeBuilder.builder()
            .itemInputs(ItemList.Brittle_Netherite_Scrap.get(1), GTBees.combs.getStackForType(CombType.NETHERITE, 32))
            .circuit(2)
            .fluidInputs(Materials.HellishMetal.getMolten(1 * GTRecipeBuilder.INGOTS))
            .itemOutputs(ItemList.Intensely_Bonded_Netherite_Nanoparticles.get(2))
            .fluidOutputs(Materials.Thaumium.getMolten(2 * GTRecipeBuilder.NUGGETS))
            .duration(15 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .metadata(GTRecipeConstants.COIL_HEAT, 7500)
            .addTo(RecipeMaps.blastFurnaceRecipes);
    }

    public static void shallowChemicalCoupling() {
        if (!Mods.Forestry.isModLoaded()) return;
        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LIGNIE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lignite, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lignite, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.COAL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.OIL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oilsands, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oilsands, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.APATITE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Apatite, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Apatite, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.AMBER, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Amber, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Amber, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MERCURY, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cinnabar, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cinnabar, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.STONE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Soapstone, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Soapstone, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.CERTUS, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartz, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartzCharged, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.REDSTONE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LAPIS, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.RUBY, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ruby, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ruby, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.REDGARNET, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetRed, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetRed, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.YELLOWGARNET, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetYellow, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetYellow, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.SAPPHIRE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sapphire, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sapphire, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.DIAMOND, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.OLIVINE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Olivine, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Olivine, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.EMERALD, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.FIRESTONE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.PYROPE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Pyrope, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Pyrope, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.GROSSULAR, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Grossular, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Grossular, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.COPPER, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.TIN, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LEAD, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.NICKEL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ZINC, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.SILVER, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.CRYOLITE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cryolite, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cryolite, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.GOLD, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.SULFUR, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.GALLIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ARSENIC, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.IRON, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.BAUXITE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bauxite, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bauxite, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ALUMINIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MANGANESE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MAGNESIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MOLYBDENUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ALMANDINE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Almandine, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Almandine, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3500)
            .duration(2 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.NEODYMIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3602)
            .duration(4 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LITHIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3602)
            .duration(4 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ELECTROTINE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrotine, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrotine, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3602)
            .duration(4 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_HV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.THORIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3602)
            .duration(4 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MITHRIL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mithril, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mithril, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.SHADOWMETAL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Shadow, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Shadow, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.DIVIDED, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Unstable, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Unstable, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.CHROME, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ENDIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Endium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Endium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.METEORICIRON, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeteoricIron, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeteoricIron, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.PLATINUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 2L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 3900)
            .duration(6 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.BEDROCKIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.SPARKLING, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.TITANIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.URANIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.PLUTONIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.DESH, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Desh, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Desh, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LEDOX, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ledox, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ledox, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 4300)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.TUNGSTEN, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.PALLADIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.DRACONIC, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.NAQUADAH, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.LUTETIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.CALLISTOICE, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CallistoIce, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.CallistoIce, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MYTRYL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.QUANTIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Quantium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Quantium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.ORIHARUKON, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.INFUSEDGOLD, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedGold, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedGold, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.IRIDIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 2L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.OSMIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.INDIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Indium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Indium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 5200)
            .duration(7 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_IV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.EUROPIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 6100)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.NAQUADRIA, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 6100)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.AMERICIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 6100)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.MYSTERIOUSCRYSTAL, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MysteriousCrystal, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MysteriousCrystal, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 6100)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.BLACKPLUTONIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 6100)
            .duration(8 * GTRecipeBuilder.SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.AWAKENEDDRACONIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 7200)
            .duration(8 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.TRINIUM, 4))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 12L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 4L))
            .outputChances(10000, 7500)
            .metadata(COIL_HEAT, 7200)
            .duration(8 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);

        RecipeBuilder.builder()
            .itemInputs(GTBees.combs.getStackForType(CombType.NEUTRONIUM, 4))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 4L))
            .fluidOutputs(Materials.Neutronium.getMolten(1152))
            .outputChances(5000)
            .metadata(COIL_HEAT, 8000)
            .duration(8 * GTRecipeBuilder.SECONDS + 10 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GTNLRecipeMaps.ShallowChemicalCouplingRecipes);
    }
}
