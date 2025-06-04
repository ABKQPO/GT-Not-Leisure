package com.science.gtnl.common.recipe.GregTech;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTUtility;

public class TranscendentPlasmaMixerRecipes implements IRecipePool {

    final RecipeMap<?> TPMP = RecipeMaps.transcendentPlasmaMixerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(6))
            .fluidInputs(
                FluidRegistry.getFluidStack("temporalfluid", 710),
                FluidRegistry.getFluidStack("spatialfluid", 710),
                FluidRegistry.getFluidStack("naquadah based liquid fuel mki", 1000),
                FluidRegistry.getFluidStack("naquadah based liquid fuel mkii", 1000),
                FluidRegistry.getFluidStack("naquadah based liquid fuel mkiii", 1000),
                FluidRegistry.getFluidStack("naquadah based liquid fuel mkiv", 1000),
                FluidRegistry.getFluidStack("naquadah based liquid fuel mkv", 1000),
                FluidRegistry.getFluidStack("naquadah based liquid fuel mkvi", 1000),
                FluidRegistry.getFluidStack("liquidair", 85200))
            .fluidOutputs(MaterialPool.ExcitedNaquadahFuel.getFluidOrGas(1000))
            .duration(20)
            .eut(31457280)
            .addTo(TPMP);
    }
}
