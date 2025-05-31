package com.science.gtnl.common.materials;

import static gregtech.api.recipe.RecipeMaps.extruderRecipes;
import static gregtech.api.recipe.RecipeMaps.fluidSolidifierRecipes;
import static gregtech.api.util.GTRecipeBuilder.TICKS;

import net.minecraft.item.ItemStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.metatileentity.implementations.MTEFluidPipe;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.material.Material;

public class MaterialUtils {

    public static void generateGTFluidPipes(final Materials material, final int startID, final int transferRatePerSec,
        final int heatResistance, final boolean isGasProof) {
        final int transferRatePerTick = transferRatePerSec / 20;
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeTiny.get(material),
            new MTEFluidPipe(
                startID,
                "GT_Pipe_" + material.mDefaultLocalName + "_Tiny",
                "Tiny " + material.mDefaultLocalName + " Fluid Pipe",
                0.25F,
                material,
                transferRatePerTick * 2,
                heatResistance,
                isGasProof).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeSmall.get(material),
            new MTEFluidPipe(
                startID + 1,
                "GT_Pipe_" + material.mDefaultLocalName + "_Small",
                "Small " + material.mDefaultLocalName + " Fluid Pipe",
                0.375F,
                material,
                transferRatePerTick * 4,
                heatResistance,
                isGasProof).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeMedium.get(material),
            new MTEFluidPipe(
                startID + 2,
                "GT_Pipe_" + material.mDefaultLocalName,
                material.mDefaultLocalName + " Fluid Pipe",
                0.5F,
                material,
                transferRatePerTick * 12,
                heatResistance,
                isGasProof).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeLarge.get(material),
            new MTEFluidPipe(
                startID + 3,
                "GT_Pipe_" + material.mDefaultLocalName + "_Large",
                "Large " + material.mDefaultLocalName + " Fluid Pipe",
                0.75F,
                material,
                transferRatePerTick * 24,
                heatResistance,
                isGasProof).getStackForm(1L));
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeHuge.get(material),
            new MTEFluidPipe(
                startID + 4,
                "GT_Pipe_" + material.mDefaultLocalName + "_Huge",
                "Huge " + material.mDefaultLocalName + " Fluid Pipe",
                0.875F,
                material,
                transferRatePerTick * 48,
                heatResistance,
                isGasProof).getStackForm(1L));
    }

    private static MTEFluidPipe createMTEFluid(int ID, String unlocalizedName, String localizedName, float thickness,
        Material material, int flow, int temp, boolean gas) {
        return new MTEFluidPipe(
            ID,
            unlocalizedName,
            localizedName,
            thickness,
            material.tryFindGregtechMaterialEquivalent(),
            flow,
            temp,
            gas);
    }

    public static void registerPipeGTPP(int ID, Material material, int flow, int temp, boolean gas) {
        String unName = material.getUnlocalizedName()
            .replace(" ", "_");
        String Name = material.getLocalizedName();

        // Register Tiny Pipe
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeTiny.get(material.tryFindGregtechMaterialEquivalent()),
            createMTEFluid(
                ID,
                "GT_Pipe_" + unName + "_Tiny",
                "Tiny " + Name + " Fluid Pipe",
                0.25F,
                material,
                flow / 6,
                temp,
                gas).getStackForm(1L));

        // Register Small Pipe
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeSmall.get(material.tryFindGregtechMaterialEquivalent()),
            createMTEFluid(
                ID + 1,
                "GT_Pipe_" + unName + "_Small",
                "Small " + Name + " Fluid Pipe",
                0.375F,
                material,
                flow / 3,
                temp,
                gas).getStackForm(1L));

        // Register Medium Pipe
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeMedium.get(material.tryFindGregtechMaterialEquivalent()),
            createMTEFluid(ID + 2, "GT_Pipe_" + unName, Name + " Fluid Pipe", 0.5F, material, flow, temp, gas)
                .getStackForm(1L));

        // Register Large Pipe
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeLarge.get(material.tryFindGregtechMaterialEquivalent()),
            createMTEFluid(
                ID + 3,
                "GT_Pipe_" + unName + "_Large",
                "Large " + Name + " Fluid Pipe",
                0.75F,
                material,
                flow * 2,
                temp,
                gas).getStackForm(1L));

        // Register Huge Pipe
        GTOreDictUnificator.registerOre(
            OrePrefixes.pipeHuge.get(material.tryFindGregtechMaterialEquivalent()),
            createMTEFluid(
                ID + 4,
                "GT_Pipe_" + unName + "_Huge",
                "Huge " + Name + " Fluid Pipe",
                0.875F,
                material,
                flow * 4,
                temp,
                gas).getStackForm(1L));

        // Register Extruder Recipes
        GTValues.RA.stdBuilder()
            .itemInputs(material.getIngot(1), ItemList.Shape_Extruder_Pipe_Tiny.get(0))
            .itemOutputs(getPipeTiny(material, 2))
            .duration(material.getMass() * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(extruderRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(material.getIngot(1), ItemList.Shape_Extruder_Pipe_Small.get(0))
            .itemOutputs(getPipeSmall(material, 1))
            .duration(material.getMass() * 2 * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(extruderRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(material.getIngot(3), ItemList.Shape_Extruder_Pipe_Medium.get(0))
            .itemOutputs(getPipeMedium(material, 1))
            .duration(material.getMass() * 6 * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(extruderRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(material.getIngot(6), ItemList.Shape_Extruder_Pipe_Large.get(0))
            .itemOutputs(getPipeLarge(material, 1))
            .duration(material.getMass() * 12 * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(extruderRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(material.getIngot(12), ItemList.Shape_Extruder_Pipe_Huge.get(0))
            .itemOutputs(getPipeHuge(material, 1))
            .duration(material.getMass() * 24 * TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(extruderRecipes);

        // Register Fluid Solidifier Recipes
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Pipe_Tiny.get(0))
            .fluidInputs(material.getFluidStack(72))
            .itemOutputs(getPipeTiny(material, 1))
            .duration(material.getMass() * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(fluidSolidifierRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Pipe_Small.get(0))
            .fluidInputs(material.getFluidStack(144))
            .itemOutputs(getPipeSmall(material, 1))
            .duration(material.getMass() * 2 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(fluidSolidifierRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Pipe_Medium.get(0))
            .fluidInputs(material.getFluidStack(432))
            .itemOutputs(getPipeMedium(material, 1))
            .duration(material.getMass() * 6 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(fluidSolidifierRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Pipe_Large.get(0))
            .fluidInputs(material.getFluidStack(864))
            .itemOutputs(getPipeLarge(material, 1))
            .duration(material.getMass() * 12 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(fluidSolidifierRecipes);

        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Shape_Mold_Pipe_Huge.get(0))
            .fluidInputs(material.getFluidStack(1728))
            .itemOutputs(getPipeHuge(material, 1))
            .duration(material.getMass() * 24 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(fluidSolidifierRecipes);
    }

    // Helper methods to get pipe ItemStacks
    private static ItemStack getPipeTiny(Material material, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.pipeTiny, material.tryFindGregtechMaterialEquivalent(), amount);
    }

    private static ItemStack getPipeSmall(Material material, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.pipeSmall, material.tryFindGregtechMaterialEquivalent(), amount);
    }

    private static ItemStack getPipeMedium(Material material, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.pipeMedium, material.tryFindGregtechMaterialEquivalent(), amount);
    }

    private static ItemStack getPipeLarge(Material material, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.pipeLarge, material.tryFindGregtechMaterialEquivalent(), amount);
    }

    private static ItemStack getPipeHuge(Material material, int amount) {
        return GTOreDictUnificator.get(OrePrefixes.pipeHuge, material.tryFindGregtechMaterialEquivalent(), amount);
    }
}
