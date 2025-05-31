package com.science.gtnl.common.recipe.GTNL;

import static com.science.gtnl.Utils.Utils.setStackSize;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.recipes.IsaMillTierKey;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.util.minecraft.MaterialUtils;

public class IsaMillRecipes implements IRecipePool {

    final IsaMillTierKey ISAMILL_TIER = IsaMillTierKey.INSTANCE;
    final RecipeMap<?> IsaMR = RecipeRegister.IsaMillRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Nickel, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Nickel)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Nickel, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Nickel)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Nickel, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Nickel)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Nickel, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Nickel)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Platinum, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Platinum)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Platinum, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Platinum)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Platinum, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Platinum)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Platinum, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Platinum)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Almandine, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Almandine)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Almandine, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Almandine)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Almandine, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Almandine)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Almandine, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Almandine)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Chalcopyrite, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Chalcopyrite)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Chalcopyrite, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Chalcopyrite)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Chalcopyrite, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Chalcopyrite)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Chalcopyrite, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Chalcopyrite)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Grossular, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Grossular)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Grossular, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Grossular)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Grossular, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Grossular)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Grossular, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Grossular)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrope, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Pyrope)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pyrope, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Pyrope)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Pyrope, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Pyrope)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Pyrope, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Pyrope)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Spessartine, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Spessartine)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Spessartine, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Spessartine)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Spessartine, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Spessartine)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Spessartine, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Spessartine)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Sphalerite, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Sphalerite)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Sphalerite, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Sphalerite)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Sphalerite, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Sphalerite)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Sphalerite, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Sphalerite)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pentlandite, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Pentlandite)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Pentlandite, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Pentlandite)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Pentlandite, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Pentlandite)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Pentlandite, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Pentlandite)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Monazite, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Monazite)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Monazite, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Monazite)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Monazite, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Monazite)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Monazite, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Monazite)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Redstone, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Redstone)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.Redstone, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Redstone)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Redstone, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Redstone)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.Redstone, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.Redstone)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.NaquadahEnriched, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.NaquadahEnriched)
                        .getMilled(1),
                    96))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 2)
            .duration(4800)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.ore, Materials.NaquadahEnriched, 1L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.NaquadahEnriched)
                        .getMilled(1),
                    72))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 100))
            .metadata(ISAMILL_TIER, 1)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.NaquadahEnriched, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.NaquadahEnriched)
                        .getMilled(1),
                    48))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 2)
            .duration(2400)
            .eut(1920)
            .addTo(IsaMR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.rawOre, Materials.NaquadahEnriched, 16L))
            .itemOutputs(
                setStackSize(
                    MaterialUtils.generateMaterialFromGtENUM(Materials.NaquadahEnriched)
                        .getMilled(1),
                    36))
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 50))
            .metadata(ISAMILL_TIER, 1)
            .duration(1200)
            .eut(1920)
            .addTo(IsaMR);
    }
}
