package com.science.gtnl.common.recipe.GTNL;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;

public class SteamCrackerRecipes implements IRecipePool {

    final RecipeMap<?> SCR = RecipeRegister.SteamCrackerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(FluidRegistry.getFluidStack("gas_sulfuricgas", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("lightlysteamcracked.gas", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(FluidRegistry.getFluidStack("gas_sulfuricgas", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("moderatelysteamcracked.gas", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(FluidRegistry.getFluidStack("gas_sulfuricgas", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("severelysteamcracked.gas", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sulfuricnaphtha", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("lightlysteamcracked.naphtha", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sulfuricnaphtha", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("moderatelysteamcracked.naphtha", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sulfuricnaphtha", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("severelysteamcracked.naphtha", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sufluriclight_fuel", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("lightlysteamcracked.lightfuel", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sufluriclight_fuel", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("moderatelysteamcracked.lightfuel", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sufluriclight_fuel", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("severelysteamcracked.lightfuel", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sulfuricheavy_fuel", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("lightlysteamcracked.heavyfuel", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sulfuricheavy_fuel", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("moderatelysteamcracked.heavyfuel", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3))
            .fluidInputs(FluidRegistry.getFluidStack("liquid_sulfuricheavy_fuel", 1000))
            .fluidOutputs(FluidRegistry.getFluidStack("severelysteamcracked.heavyfuel", 400))
            .specialValue(0)
            .duration(200)
            .eut(30)
            .addTo(SCR);
    }
}
