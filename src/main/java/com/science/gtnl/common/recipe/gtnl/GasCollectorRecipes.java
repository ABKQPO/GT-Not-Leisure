package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.common.material.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;
import gtneioreplugin.plugin.block.ModBlocks;

public class GasCollectorRecipes implements IRecipePool {

    public RecipeMap<?> GCR = RecipePool.GasCollectorRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidOutputs(Materials.Air.getGas(10000))
            .duration(200)
            .eut(TierEU.RECIPE_LV)
            .addTo(GCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2), ItemList.Machine_Multi_VacuumFreezer.get(0))
            .fluidOutputs(Materials.LiquidAir.getFluid(10000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(GCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(3), new ItemStack(ModBlocks.getBlock("ED"), 0))
            .fluidOutputs(MaterialPool.EnderAir.getFluidOrGas(10000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(GCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                new ItemStack(ModBlocks.getBlock("ED"), 0),
                ItemList.Machine_Multi_VacuumFreezer.get(0))
            .fluidOutputs(MaterialPool.LiquidEnderAir.getFluidOrGas(10000))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(GCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(5), new ItemStack(ModBlocks.getBlock("Ne"), 0))
            .fluidOutputs(Materials.NetherAir.getFluid(10000))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(GCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(6),
                new ItemStack(ModBlocks.getBlock("Ne"), 0),
                ItemList.Machine_Multi_VacuumFreezer.get(0))
            .fluidOutputs(Materials.NetherSemiFluid.getFluid(10000))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(7), new ItemStack(ModBlocks.getBlock("Ne"), 0))
            .fluidOutputs(Materials.NefariousGas.getFluid(5000))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GCR);
    }
}
