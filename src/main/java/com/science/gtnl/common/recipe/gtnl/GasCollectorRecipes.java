package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gtneioreplugin.plugin.block.ModBlocks;

public class GasCollectorRecipes implements IRecipePool {

    public RecipeMap<?> GCR = GTNLRecipeMaps.GasCollectorRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs()
            .circuit(1)
            .fluidOutputs(Materials.Air.getGas(100000))
            .duration(200)
            .eut(TierEU.RECIPE_MV)
            .addTo(GCR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Machine_Multi_VacuumFreezer.get(0))
            .circuit(2)
            .fluidOutputs(Materials.LiquidAir.getFluid(100000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(GCR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(ModBlocks.getBlock("ED"), 0))
            .circuit(3)
            .fluidOutputs(GTNLMaterials.EnderAir.getFluidOrGas(100000))
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(GCR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(ModBlocks.getBlock("ED"), 0), ItemList.Machine_Multi_VacuumFreezer.get(0))
            .circuit(4)
            .fluidOutputs(GTNLMaterials.LiquidEnderAir.getFluidOrGas(100000))
            .duration(200)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(GCR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(ModBlocks.getBlock("Ne"), 0))
            .circuit(5)
            .fluidOutputs(Materials.NetherAir.getFluid(100000))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(GCR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(ModBlocks.getBlock("Ne"), 0), ItemList.Machine_Multi_VacuumFreezer.get(0))
            .circuit(6)
            .fluidOutputs(Materials.NetherSemiFluid.getFluid(100000))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GCR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(ModBlocks.getBlock("Ne"), 0))
            .circuit(7)
            .fluidOutputs(Materials.NefariousGas.getFluid(50000))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(GCR);
    }
}
