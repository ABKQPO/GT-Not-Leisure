package com.science.gtnl.common.recipe.GTNL;

import static gregtech.api.enums.Mods.Forestry;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeBuilder.TICKS;
import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;
import gtPlusPlus.core.util.minecraft.ItemUtils;

public class ShallowChemicalCouplingRecipes implements IRecipePool {

    final RecipeMap<?> SCCR = RecipeRegister.ShallowChemicalCouplingRecipes;

    @Override
    public void loadRecipes() {

        if (Forestry.isModLoaded()) {
            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.LIGNIE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lignite, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lignite, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.COAL, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.OIL, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oilsands, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oilsands, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.APATITE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Apatite, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Apatite, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.AMBER, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Amber, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Amber, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.MERCURY, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cinnabar, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cinnabar, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.STONE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Soapstone, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Soapstone, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.CERTUS, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartz, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartzCharged, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.REDSTONE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.LAPIS, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.RUBY, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ruby, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ruby, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.REDGARNET, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetRed, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetRed, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.YELLOWGARNET, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetYellow, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetYellow, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.SAPPHIRE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sapphire, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sapphire, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.DIAMOND, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.OLIVINE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Olivine, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Olivine, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.EMERALD, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.FIRESTONE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.PYROPE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Pyrope, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Pyrope, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.GROSSULAR, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Grossular, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Grossular, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.COPPER, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.TIN, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.LEAD, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.NICKEL, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.ZINC, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.SILVER, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.CRYOLITE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cryolite, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cryolite, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.GOLD, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.SULFUR, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.GALLIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.ARSENIC, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.IRON, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.BAUXITE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bauxite, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bauxite, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.ALUMINIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.MANGANESE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.MAGNESIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.MOLYBDENUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.ALMANDINE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Almandine, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Almandine, 4L))
                .outputChances(10000, 7500)
                .specialValue(3500)
                .metadata(COIL_HEAT, 3500)
                .duration(2 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.NEODYMIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 4L))
                .outputChances(10000, 7500)
                .specialValue(3602)
                .metadata(COIL_HEAT, 3602)
                .duration(4 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_HV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.LITHIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 4L))
                .outputChances(10000, 7500)
                .specialValue(3602)
                .metadata(COIL_HEAT, 3602)
                .duration(4 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_HV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.ELECTROTINE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrotine, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrotine, 4L))
                .outputChances(10000, 7500)
                .specialValue(3602)
                .metadata(COIL_HEAT, 3602)
                .duration(4 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_HV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.THORIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 4L))
                .outputChances(10000, 7500)
                .specialValue(3602)
                .metadata(COIL_HEAT, 3602)
                .duration(4 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_MV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.MITHRIL, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mithril, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mithril, 4L))
                .outputChances(10000, 7500)
                .specialValue(3900)
                .metadata(COIL_HEAT, 3900)
                .duration(6 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_EV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.SHADOWMETAL, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Shadow, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Shadow, 4L))
                .outputChances(10000, 7500)
                .specialValue(3900)
                .metadata(COIL_HEAT, 3900)
                .duration(6 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_EV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.DIVIDED, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Unstable, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Unstable, 4L))
                .outputChances(10000, 7500)
                .specialValue(3900)
                .metadata(COIL_HEAT, 3900)
                .duration(6 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_EV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.CHROME, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 4L))
                .outputChances(10000, 7500)
                .specialValue(3900)
                .metadata(COIL_HEAT, 3900)
                .duration(6 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_EV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.ENDIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.HeeEndium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.HeeEndium, 4L))
                .outputChances(10000, 7500)
                .specialValue(3900)
                .metadata(COIL_HEAT, 3900)
                .duration(6 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_EV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.METEORICIRON, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeteoricIron, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeteoricIron, 4L))
                .outputChances(10000, 7500)
                .specialValue(3900)
                .metadata(COIL_HEAT, 3900)
                .duration(6 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_EV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.PLATINUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 4L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 2L))
                .outputChances(10000, 7500)
                .specialValue(3900)
                .metadata(COIL_HEAT, 3900)
                .duration(6 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_EV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.BEDROCKIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 4L))
                .outputChances(10000, 7500)
                .specialValue(4300)
                .metadata(COIL_HEAT, 4300)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.SPARKLING, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 4L))
                .outputChances(10000, 7500)
                .specialValue(4300)
                .metadata(COIL_HEAT, 4300)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.TITANIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 4L))
                .outputChances(10000, 7500)
                .specialValue(4300)
                .metadata(COIL_HEAT, 4300)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.URANIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 4L))
                .outputChances(10000, 7500)
                .specialValue(4300)
                .metadata(COIL_HEAT, 4300)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.PLUTONIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 4L))
                .outputChances(10000, 7500)
                .specialValue(4300)
                .metadata(COIL_HEAT, 4300)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.DESH, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Desh, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Desh, 4L))
                .outputChances(10000, 7500)
                .specialValue(4300)
                .metadata(COIL_HEAT, 4300)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.LEDOX, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ledox, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ledox, 4L))
                .outputChances(10000, 7500)
                .specialValue(4300)
                .metadata(COIL_HEAT, 4300)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.TUNGSTEN, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.PALLADIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.DRACONIC, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.NAQUADAH, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.LUTETIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.CALLISTOICE, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.CallistoIce, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.CallistoIce, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.MYTRYL, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.QUANTIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Quantium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Quantium, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.ORIHARUKON, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.INFUSEDGOLD, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedGold, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedGold, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.IRIDIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 4L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 2L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.OSMIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 4L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.INDIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Indium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Indium, 4L))
                .outputChances(10000, 7500)
                .specialValue(5200)
                .metadata(COIL_HEAT, 5200)
                .duration(7 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_IV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.EUROPIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 4L))
                .outputChances(10000, 7500)
                .specialValue(6100)
                .metadata(COIL_HEAT, 6100)
                .duration(8 * SECONDS)
                .eut(TierEU.RECIPE_LuV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.NAQUADRIA, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 4L))
                .outputChances(10000, 7500)
                .specialValue(6100)
                .metadata(COIL_HEAT, 6100)
                .duration(8 * SECONDS)
                .eut(TierEU.RECIPE_LuV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.AMERICIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 4L))
                .outputChances(10000, 7500)
                .specialValue(6100)
                .metadata(COIL_HEAT, 6100)
                .duration(8 * SECONDS)
                .eut(TierEU.RECIPE_LuV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.MYSTERIOUSCRYSTAL, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.MysteriousCrystal, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.MysteriousCrystal, 4L))
                .outputChances(10000, 7500)
                .specialValue(6100)
                .metadata(COIL_HEAT, 6100)
                .duration(8 * SECONDS)
                .eut(TierEU.RECIPE_LuV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.BLACKPLUTONIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 4L))
                .outputChances(10000, 7500)
                .specialValue(6100)
                .metadata(COIL_HEAT, 6100)
                .duration(8 * SECONDS)
                .eut(TierEU.RECIPE_LuV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.AWAKENEDDRACONIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 4L))
                .outputChances(10000, 7500)
                .specialValue(7200)
                .metadata(COIL_HEAT, 7200)
                .duration(8 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_LuV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.TRINIUM, 4))
                .itemOutputs(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 12L),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 4L))
                .outputChances(10000, 7500)
                .specialValue(7200)
                .metadata(COIL_HEAT, 7200)
                .duration(8 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_LuV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTBees.combs.getStackForType(CombType.NEUTRONIUM, 4))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 4L))
                .fluidOutputs(Materials.Neutronium.getMolten(1152))
                .outputChances(5000)
                .specialValue(8000)
                .metadata(COIL_HEAT, 8000)
                .duration(8 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)),
                    Materials.Potassiumdichromate.getDust(1),
                    Materials.Copper.getDust(1),
                    Materials.Zinc.getDust(9))
                .fluidInputs(
                    Materials.Oxygen.getGas(54000),
                    Materials.Dimethylbenzene.getFluid(9000),
                    Materials.SulfuricAcid.getFluid(9000),
                    Materials.Ammonia.getGas(18000),
                    Materials.Chlorobenzene.getFluid(18000),
                    Materials.NitrationMixture.getFluid(36000))
                .itemOutputs()
                .fluidOutputs(Materials.Polybenzimidazole.getMolten(9000), Materials.HydrochloricAcid.getFluid(18000))
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(30 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)),
                    Materials.Carbon.getDust(2),
                    Materials.Silicon.getDust(1))
                .fluidInputs(
                    Materials.Hydrogen.getGas(8000),
                    Materials.Chlorine.getGas(4000),
                    Materials.Water.getFluid(1000))
                .itemOutputs()
                .fluidOutputs(
                    Materials.HydrochloricAcid.getFluid(6000),
                    Materials.DilutedHydrochloricAcid.getFluid(6000),
                    Materials.Silicone.getMolten(1296))
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(30 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)),
                    Materials.Sulfur.getDust(1))
                .fluidInputs(
                    Materials.Benzene.getFluid(216),
                    Materials.Ethylene.getGas(216),
                    Materials.Butadiene.getGas(648),
                    Materials.Oxygen.getGas(6000))
                .itemOutputs()
                .fluidOutputs(Materials.StyreneButadieneRubber.getMolten(1296), Materials.Hydrogen.getGas(144))
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(30 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)),
                    Materials.Sodium.getDust(2),
                    Materials.Sulfur.getDust(1))
                .fluidInputs(
                    Materials.Chlorine.getGas(4000),
                    Materials.Benzene.getFluid(1000),
                    Materials.Oxygen.getGas(8000))
                .itemOutputs(Materials.Salt.getDust(4))
                .fluidOutputs(Materials.HydrochloricAcid.getFluid(2000), Materials.PolyphenyleneSulfide.getMolten(1500))
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(30 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)))
                .fluidInputs(
                    Materials.Oxygen.getGas(20000),
                    Materials.Ethylene.getGas(4000),
                    Materials.Acetone.getFluid(4500))
                .itemOutputs()
                .fluidOutputs(Materials.AdvancedGlue.getFluid(7500), Materials.Water.getFluid(2000))
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(30 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)),
                    Materials.SodiumHydroxide.getDust(9))
                .fluidInputs(
                    Materials.Acetone.getFluid(1000),
                    Materials.Phenol.getFluid(2000),
                    Materials.Propene.getGas(2000),
                    Materials.Chlorine.getGas(8000),
                    Materials.Water.getFluid(2000))
                .itemOutputs()
                .fluidOutputs(
                    Materials.Epoxid.getMolten(1000),
                    Materials.HydrochloricAcid.getFluid(3000),
                    Materials.SaltWater.getFluid(4000))
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(30 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)))
                .fluidInputs(
                    Materials.Oxygen.getGas(7000),
                    Materials.Hydrogen.getGas(4000),
                    Materials.Fluorine.getGas(4000),
                    Materials.Chlorine.getGas(12000),
                    Materials.Methane.getGas(2000))
                .itemOutputs()
                .fluidOutputs(
                    Materials.HydrochloricAcid.getFluid(12000),
                    Materials.Polytetrafluoroethylene.getMolten(1500))
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(30 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)),
                    Materials.Sulfur.getDust(1))
                .fluidInputs(Materials.Ethanol.getFluid(3000), Materials.Oxygen.getGas(24000))
                .itemOutputs()
                .fluidOutputs(Materials.Plastic.getMolten(4500))
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(30 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)))
                .fluidInputs(Materials.Ethanol.getFluid(2000))
                .itemOutputs()
                .fluidOutputs(GGMaterial.ether.getFluidOrGas(1000), Materials.Water.getFluid(1000))
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(30 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)),
                    ItemUtils.getItemStackOfAmountFromOreDict("foilCopper", 40),
                    ItemUtils.getItemStackOfAmountFromOreDict("platePlastic", 4),
                    Materials.Sulfur.getDust(2),
                    Materials.Iron.getDust(1))
                .fluidInputs(
                    Materials.Oxygen.getGas(6000),
                    Materials.Water.getFluid(2000),
                    Materials.Chlorine.getGas(3000))
                .itemOutputs(ItemList.Circuit_Board_Plastic_Advanced.get(4L))
                .fluidOutputs()
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(24 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)),
                    ItemUtils.getItemStackOfAmountFromOreDict("foilCopper", 32),
                    GTOreDictUnificator.get(OrePrefixes.plate, Materials.PolyvinylChloride, 1),
                    Materials.Sulfur.getDust(1),
                    Materials.Iron.getDust(1))
                .fluidInputs(
                    Materials.Oxygen.getGas(3000),
                    Materials.Water.getFluid(1000),
                    Materials.Chlorine.getGas(3000))
                .itemOutputs(ItemList.Circuit_Board_Plastic_Advanced.get(4L))
                .fluidOutputs()
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(24 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)),
                    ItemUtils.getItemStackOfAmountFromOreDict("foilCopper", 56),
                    GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polytetrafluoroethylene, 2),
                    Materials.Sulfur.getDust(1),
                    Materials.Iron.getDust(2))
                .fluidInputs(
                    Materials.Oxygen.getGas(3000),
                    Materials.Water.getFluid(1000),
                    Materials.Chlorine.getGas(6000))
                .itemOutputs(ItemList.Circuit_Board_Plastic_Advanced.get(8L))
                .fluidOutputs()
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(24 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Carbon, 1)),
                    ItemUtils.getItemStackOfAmountFromOreDict("foilCopper", 104),
                    GTOreDictUnificator.get(OrePrefixes.plate, Materials.Polybenzimidazole, 2),
                    Materials.Sulfur.getDust(1),
                    Materials.Iron.getDust(4))
                .fluidInputs(
                    Materials.Oxygen.getGas(3000),
                    Materials.Water.getFluid(1000),
                    Materials.Chlorine.getGas(12000))
                .itemOutputs(ItemList.Circuit_Board_Plastic_Advanced.get(16L))
                .fluidOutputs()
                .specialValue(3800)
                .metadata(COIL_HEAT, 3800)
                .duration(24 * SECONDS + 10 * TICKS)
                .eut(TierEU.RECIPE_ZPM)
                .addTo(SCCR);

            // 单步聚酰亚胺
            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(7),
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1)),
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1)),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 22))
                .fluidInputs(
                    Materials.Oxygen.getGas(6000),
                    Materials.Nitrogen.getGas(2000),
                    Materials.Hydrogen.getGas(12000))
                .fluidOutputs(MaterialPool.Polyimide.getMolten(144))
                .specialValue(9500)
                .metadata(COIL_HEAT, 9500)
                .duration(20)
                .eut(TierEU.RECIPE_UV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(17),
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1)),
                    GTUtility.copyAmount(0, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1)),
                    GTUtility.copyAmountUnsafe(2750, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
                .fluidInputs(
                    Materials.Oxygen.getGas(750000),
                    Materials.Nitrogen.getGas(250000),
                    Materials.Hydrogen.getGas(1500000))
                .fluidOutputs(MaterialPool.Polyimide.getMolten(18000))
                .specialValue(10700)
                .metadata(COIL_HEAT, 10700)
                .duration(200)
                .eut(TierEU.RECIPE_UHV)
                .addTo(SCCR);

            // 单步聚醚醚酮
            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(7),
                    GTUtility.copyAmount(0, GTNLItemList.ZnFeAlClCatalyst.get(1)),
                    GTUtility.copyAmount(0, (GTNLItemList.BlackLight.get(1))),
                    GTUtility.copyAmount(0, (MaterialPool.CoAcAbCatalyst.get(OrePrefixes.dust, 1))),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 45))
                .fluidInputs(Materials.Oxygen.getGas(54000), Materials.Hydrogen.getGas(216000))
                .fluidOutputs(MaterialPool.Polyetheretherketone.getMolten(2592))
                .specialValue(9500)
                .metadata(COIL_HEAT, 9500)
                .duration(200)
                .eut(TierEU.RECIPE_UHV)
                .addTo(SCCR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(17),
                    GTUtility.copyAmount(0, GTNLItemList.ZnFeAlClCatalyst.get(1)),
                    GTUtility.copyAmount(0, (GTNLItemList.BlackLight.get(1))),
                    GTUtility.copyAmount(0, (MaterialPool.CoAcAbCatalyst.get(OrePrefixes.dust, 1))),
                    GTUtility.copyAmountUnsafe(1440, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1)))
                .fluidInputs(Materials.Oxygen.getGas(1728000), Materials.Hydrogen.getGas(6912000))
                .fluidOutputs(MaterialPool.Polyetheretherketone.getMolten(82944))
                .specialValue(10700)
                .metadata(COIL_HEAT, 10700)
                .duration(400)
                .eut(TierEU.RECIPE_UEV)
                .addTo(SCCR);
        }
    }
}
