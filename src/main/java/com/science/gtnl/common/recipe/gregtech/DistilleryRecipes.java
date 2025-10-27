package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.enums.Mods.*;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
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

        if (Forestry.isModLoaded()) {
            GTValues.RA.stdBuilder()
                .itemInputs(
                    ItemUtils.getItemStack(Forestry.ID, "leaves", 1, 0, "{species:\"forestry.treePine\"}", null))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
                .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
                .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 250))
                .duration(2000)
                .eut(2048)
                .addTo(DR);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    ItemUtils.getItemStack(Forestry.ID, "leaves", 1, 0, "{species:\"forestry.treePine\"}", null))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
                .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
                .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
                .duration(1000)
                .eut(2048)
                .addTo(DR);

            GTValues.RA.stdBuilder()
                .itemInputs(GTModHandler.getModItem(Forestry.ID, "logs", 1, 20))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
                .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
                .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
                .duration(4000)
                .eut(2048)
                .addTo(DR);

            GTValues.RA.stdBuilder()
                .itemInputs(GTModHandler.getModItem(Forestry.ID, "logs", 1, 20))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
                .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
                .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 1000))
                .duration(3000)
                .eut(2048)
                .addTo(DR);
        }

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.log_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(4000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(BiomesOPlenty.ID, "logs4", 1, 0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(4000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.log_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 1000))
            .duration(3000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(BiomesOPlenty.ID, "logs4", 1, 0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 1000))
            .duration(3000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.leaves_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 250))
            .duration(2000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(BiomesOPlenty.ID, "colorizedLeaves2", 1, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 250))
            .duration(2000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.leaves_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(BiomesOPlenty.ID, "colorizedLeaves2", 1, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.sapling_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 100))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(BiomesOPlenty.ID, "colorizedSaplings", 1, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 100))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(BOPBlockRegistrator.sapling_Pine, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 200))
            .duration(500)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(BiomesOPlenty.ID, "colorizedSaplings", 1, 5))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 200))
            .duration(500)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.CrushedPineMaterials.get(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 50))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(BiomesOPlenty.ID, "misc", 1, 13))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 50))
            .duration(1000)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.CrushedPineMaterials.get(1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 100))
            .duration(500)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(BiomesOPlenty.ID, "misc", 1, 13))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 100))
            .duration(500)
            .eut(2048)
            .addTo(DR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(MaterialPool.BenzenediazoniumTetrafluoroborate.getFluidOrGas(1000))
            .fluidOutputs(MaterialPool.FluoroBenzene.getFluidOrGas(1000))
            .duration(100)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(DR);
    }
}
