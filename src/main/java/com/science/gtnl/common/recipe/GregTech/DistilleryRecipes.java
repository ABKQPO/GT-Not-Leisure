package com.science.gtnl.common.recipe.GregTech;

import static gregtech.api.enums.Mods.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

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

    final RecipeMap<?> DR = RecipeMaps.distilleryRecipes;

    @Override
    public void loadRecipes() {

        ItemStack PineLeaves = GTModHandler.getModItem(Forestry.ID, "leaves", 1);
        NBTTagCompound PineLeavesType = PineLeaves.getTagCompound();
        if (PineLeavesType != null) {
            PineLeavesType.setString("species", "forestry.treePine");
        } else {
            PineLeavesType = new NBTTagCompound();
            PineLeavesType.setString("species", "forestry.treePine");
            PineLeaves.setTagCompound(PineLeavesType);
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(Forestry.ID, "logs", 1, 20))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(4000)
            .eut(2048)
            .addTo(DR);

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
            .itemInputs(GTModHandler.getModItem(Forestry.ID, "logs", 1, 20))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 1000))
            .duration(3000)
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
            .itemInputs(PineLeaves)
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("steam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 250))
            .duration(2000)
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
            .itemInputs(PineLeaves)
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, 1L))
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 5000))
            .fluidOutputs(new FluidStack(GTPPFluids.PineOil, 500))
            .duration(1000)
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
