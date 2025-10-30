package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeConstants.DISSOLUTION_TANK_RATIO;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.config.MainConfig;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsGTNH;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;

public class DissolutionTankRecipes implements IRecipePool {

    public RecipeMap<?> DTR = LanthanidesRecipeMaps.dissolutionTankRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.RareEarth, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 30))
            .fluidInputs(Materials.Water.getFluid(9000), Materials.NitricAcid.getFluid(1000))
            .fluidOutputs(MaterialPool.RareEarthHydroxides.getFluidOrGas(10000))
            .metadata(DISSOLUTION_TANK_RATIO, 9)
            .specialValue(0)
            .duration(50)
            .eut(480)
            .addTo(DTR);

        if (MainConfig.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadDeleteRecipe() {
        GTValues.RA.stdBuilder() // Leaching
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.shard, MaterialsGTNH.Prismarine, 24))
            .fluidInputs(
                FluidUtils.getHydrofluoricAcid(4000), // Industrial Strength Hydrofluoric Acid
                FluidRegistry.getFluidStack("fluid.hydrogenperoxide", 4000)) // Hydrogen Peroxide
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartz, 4))
            .fluidOutputs(Materials.PrismarineSolution.getFluid(8000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(DISSOLUTION_TANK_RATIO, 1)
            .addTo(DTR);

        GTValues.RA.stdBuilder() // Looped Leaching
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.shard, MaterialsGTNH.Prismarine, 6))
            .fluidInputs(
                Materials.PrismarineContaminatedHydrogenPeroxide.getFluid(6000),
                FluidRegistry.getFluidStack("fluid.hydrogenperoxide", 2000)) // Hydrogen Peroxide
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherQuartz, 1))
            .fluidOutputs(Materials.PrismarineSolution.getFluid(8000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .metadata(DISSOLUTION_TANK_RATIO, 3)
            .addTo(DTR);
    }
}
