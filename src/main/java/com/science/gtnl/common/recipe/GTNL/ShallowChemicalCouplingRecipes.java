package com.science.gtnl.common.recipe.GTNL;

import static bartworks.system.material.WerkstoffLoader.Roquesit;
import static goodgenerator.items.GGMaterial.indiumPhosphate;
import static gregtech.api.enums.Mods.DraconicEvolution;
import static gregtech.api.enums.Mods.Forestry;
import static gregtech.api.enums.Mods.Gendustry;
import static gregtech.api.enums.Mods.Genetics;
import static gregtech.api.enums.Mods.HardcoreEnderExpansion;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.enums.Mods.Natura;
import static gregtech.api.enums.Mods.TinkerConstruct;
import static gregtech.api.recipe.RecipeMaps.chemicalReactorRecipes;
import static gregtech.api.recipe.RecipeMaps.multiblockChemicalReactorRecipes;
import static gregtech.api.util.GTRecipeBuilder.INGOTS;
import static gregtech.api.util.GTRecipeBuilder.MINUTES;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeBuilder.TICKS;
import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;
import static gregtech.api.util.GTRecipeConstants.UniversalChemical;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.recipe.IRecipePool;
import com.science.gtnl.common.recipe.RecipeRegister;
import gregtech.api.recipe.RecipeMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.gthandler.CustomItemList;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.common.items.CombType;
import gregtech.loaders.misc.GTBees;
import ic2.core.Ic2Items;

public class ShallowChemicalCouplingRecipes implements IRecipePool {

    final RecipeMap<?> SCCR = RecipeRegister.ShallowChemicalCouplingRecipes;

    @Override
    public void loadRecipes() {

        if (Forestry.isModLoaded()) {
            RecipeBuilder.builder()
                .itemInputs(
                    GTBees.combs.getStackForType(CombType.LIGNIE, 4))
                .itemOutputs(
                        GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lignite, 12L),
                        GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lignite, 4L)
                )
                .outputChances(10000,7500)
                .specialValue(3500)
                .noOptimize()
                .metadata(COIL_HEAT, 3500)
                .duration(6 * SECONDS + 10 * TICKS)
                .eut(103)
                .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.COAL, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.OIL, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oilsands, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oilsands, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.APATITE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Apatite, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Apatite, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.AMBER, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Amber, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Amber, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.MERCURY, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cinnabar, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cinnabar, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.STONE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Soapstone, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Soapstone, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.CERTUS, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartz, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartzCharged, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.REDSTONE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.LAPIS, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.RUBY, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ruby, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ruby, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.REDGARNET, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetRed, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetRed, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.YELLOWGARNET, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetYellow, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetYellow, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.SAPPHIRE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sapphire, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sapphire, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.DIAMOND, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.OLIVINE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Olivine, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Olivine, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.EMERALD, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.FIRESTONE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.PYROPE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Pyrope, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Pyrope, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.GROSSULAR, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Grossular, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Grossular, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.COPPER, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.TIN, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.LEAD, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.NICKEL, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.ZINC, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.SILVER, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.CRYOLITE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cryolite, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cryolite, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.GOLD, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.SULFUR, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.GALLIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.ARSENIC, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.IRON, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.BAUXITE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bauxite, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bauxite, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.ALUMINIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.MANGANESE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.MAGNESIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.MOLYBDENUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.ALMANDINE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Almandine, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Almandine, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3500)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3500)
                    .duration(6 * SECONDS + 10 * TICKS)
                    .eut(103)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.NEODYMIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3602)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3602)
                    .duration(9 * SECONDS + 10 * TICKS)
                    .eut(391)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.LITHIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3602)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3602)
                    .duration(9 * SECONDS + 10 * TICKS)
                    .eut(391)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.ELECTROTINE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrotine, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrotine, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3602)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3602)
                    .duration(9 * SECONDS + 10 * TICKS)
                    .eut(391)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.THORIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3602)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3602)
                    .duration(9 * SECONDS + 10 * TICKS)
                    .eut(391)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.MITHRIL, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mithril, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mithril, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3900)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3900)
                    .duration(11 * SECONDS + 10 * TICKS)
                    .eut(1543)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.SHADOWMETAL, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Shadow, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Shadow, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3900)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3900)
                    .duration(11 * SECONDS + 10 * TICKS)
                    .eut(1543)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.DIVIDED, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Unstable, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Unstable, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3900)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3900)
                    .duration(11 * SECONDS + 10 * TICKS)
                    .eut(1543)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.CHROME, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3900)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3900)
                    .duration(11 * SECONDS + 10 * TICKS)
                    .eut(1543)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.ENDIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.HeeEndium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.HeeEndium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3900)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3900)
                    .duration(11 * SECONDS + 10 * TICKS)
                    .eut(1543)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.METEORICIRON, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeteoricIron, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeteoricIron, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3900)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3900)
                    .duration(11 * SECONDS + 10 * TICKS)
                    .eut(1543)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.PLATINUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 4L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 2L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(3900)
                    .noOptimize()
                    .metadata(COIL_HEAT, 3900)
                    .duration(11 * SECONDS + 10 * TICKS)
                    .eut(1543)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.BEDROCKIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(4300)
                    .noOptimize()
                    .metadata(COIL_HEAT, 4300)
                    .duration(13 * SECONDS)
                    .eut(6151)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.SPARKLING, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherStar, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(4300)
                    .noOptimize()
                    .metadata(COIL_HEAT, 4300)
                    .duration(13 * SECONDS)
                    .eut(6151)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.TITANIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(4300)
                    .noOptimize()
                    .metadata(COIL_HEAT, 4300)
                    .duration(13 * SECONDS)
                    .eut(6151)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.URANIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(4300)
                    .noOptimize()
                    .metadata(COIL_HEAT, 4300)
                    .duration(13 * SECONDS)
                    .eut(6151)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.PLUTONIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(4300)
                    .noOptimize()
                    .metadata(COIL_HEAT, 4300)
                    .duration(13 * SECONDS)
                    .eut(6151)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.DESH, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Desh, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Desh, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(4300)
                    .noOptimize()
                    .metadata(COIL_HEAT, 4300)
                    .duration(13 * SECONDS)
                    .eut(6151)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.LEDOX, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ledox, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ledox, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(4300)
                    .noOptimize()
                    .metadata(COIL_HEAT, 4300)
                    .duration(13 * SECONDS)
                    .eut(6151)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.TUNGSTEN, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.PALLADIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.DRACONIC, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.NAQUADAH, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.LUTETIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.CALLISTOICE, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.CallistoIce, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.CallistoIce, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.MYTRYL, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.QUANTIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Quantium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Quantium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.ORIHARUKON, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.INFUSEDGOLD, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedGold, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedGold, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.IRIDIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 4L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 2L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.OSMIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 4L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.INDIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Indium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Indium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(5200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 5200)
                    .duration(14 * SECONDS + 10 * TICKS)
                    .eut(24583)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.EUROPIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(6100)
                    .noOptimize()
                    .metadata(COIL_HEAT, 6100)
                    .duration(16 * SECONDS + 10 * TICKS)
                    .eut(98311)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.NAQUADRIA, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(6100)
                    .noOptimize()
                    .metadata(COIL_HEAT, 6100)
                    .duration(16 * SECONDS + 10 * TICKS)
                    .eut(98311)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.AMERICIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(6100)
                    .noOptimize()
                    .metadata(COIL_HEAT, 6100)
                    .duration(16 * SECONDS + 10 * TICKS)
                    .eut(98311)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.MYSTERIOUSCRYSTAL, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.MysteriousCrystal, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.MysteriousCrystal, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(6100)
                    .noOptimize()
                    .metadata(COIL_HEAT, 6100)
                    .duration(16 * SECONDS + 10 * TICKS)
                    .eut(98311)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.BLACKPLUTONIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(6100)
                    .noOptimize()
                    .metadata(COIL_HEAT, 6100)
                    .duration(16 * SECONDS + 10 * TICKS)
                    .eut(98311)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.AWAKENEDDRACONIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(7200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 7200)
                    .duration(18 * SECONDS + 10 * TICKS)
                    .eut(473611)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.TRINIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(7200)
                    .noOptimize()
                    .metadata(COIL_HEAT, 7200)
                    .duration(18 * SECONDS + 10 * TICKS)
                    .eut(473611)
                    .addTo(SCCR);

            RecipeBuilder.builder()
                    .itemInputs(
                            GTBees.combs.getStackForType(CombType.NEUTRONIUM, 4))
                    .itemOutputs(
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 12L),
                            GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 4L)
                    )
                    .outputChances(10000,7500)
                    .specialValue(8000)
                    .noOptimize()
                    .metadata(COIL_HEAT, 8000)
                    .duration(60 * SECONDS + 10 * TICKS)
                    .eut(2004322)
                    .addTo(SCCR);


        }
    }
}
