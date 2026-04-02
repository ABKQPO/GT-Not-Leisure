package com.science.gtnl.common.recipe.gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.xmod.bop.blocks.BOPBlockRegistrator;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class DistilleryRecipes implements IRecipePool {

    public RecipeMap<?> DR = RecipeMaps.distilleryRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                ItemUtils.getItemStack(Mods.Forestry.ID, "leaves", 1, 0, "{species:\"forestry.treePine\"}", null))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 250))
            .duration(2000)
            .eut(2048)
            .requireMods(Mods.Forestry)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(
                ItemUtils.getItemStack(Mods.Forestry.ID, "leaves", 1, 0, "{species:\"forestry.treePine\"}", null))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(1000)
            .eut(2048)
            .requireMods(Mods.Forestry)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.Forestry.ID, "logs", 1, 20))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(4000)
            .eut(2048)
            .requireMods(Mods.Forestry)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.Forestry.ID, "logs", 1, 20))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 1000))
            .duration(3000)
            .eut(2048)
            .requireMods(Mods.Forestry)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.log_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(4000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.BiomesOPlenty.ID, "logs4", 1, 0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(4000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.log_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 1000))
            .duration(3000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.BiomesOPlenty.ID, "logs4", 1, 0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 1000))
            .duration(3000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.leaves_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 250))
            .duration(2000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.BiomesOPlenty.ID, "colorizedLeaves2", 1, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 250))
            .duration(2000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.leaves_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.BiomesOPlenty.ID, "colorizedLeaves2", 1, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.sapling_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 100))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.BiomesOPlenty.ID, "colorizedSaplings", 1, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 100))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.sapling_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 200))
            .duration(500)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.BiomesOPlenty.ID, "colorizedSaplings", 1, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 200))
            .duration(500)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CrushedPineMaterials.get(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 50))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.BiomesOPlenty.ID, "misc", 1, 13))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 50))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CrushedPineMaterials.get(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 100))
            .duration(500)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(Mods.BiomesOPlenty.ID, "misc", 1, 13))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 100))
            .duration(500)
            .eut(2048)
            .addTo(DR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(GTNLMaterials.BenzenediazoniumTetrafluoroborate.getFluidOrGas(1000))
            .fluidOutputs(GTNLMaterials.FluoroBenzene.getFluidOrGas(1000))
            .duration(100)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(DR);
    }
}
