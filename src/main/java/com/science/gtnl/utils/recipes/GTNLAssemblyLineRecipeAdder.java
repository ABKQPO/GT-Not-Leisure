package com.science.gtnl.utils.recipes;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.ScienceNotLeisure;

import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.recipe.Scanning;
import tectech.recipe.TTRecipeAdder;

public final class GTNLAssemblyLineRecipeAdder {

    private GTNLAssemblyLineRecipeAdder() {}

    public static boolean addResearchableAssemblylineRecipe(ItemStack aResearchItem, int totalComputationRequired,
        int computationRequiredPerSec, int researchEUt, int researchAmperage, ItemStack[] aInputs,
        FluidStack[] aFluidInputs, ItemStack aOutput, int assDuration, int assEUt) {
        // Keep TecTech research registration, but also mirror the recipe into standard GT AssemblyLine.
        boolean registered = TTRecipeAdder.addResearchableAssemblylineRecipe(
            aResearchItem,
            totalComputationRequired,
            computationRequiredPerSec,
            researchEUt,
            researchAmperage,
            aInputs,
            aFluidInputs,
            aOutput,
            assDuration,
            assEUt);
        if (registered) {
            science$registerStandardAssemblyLine(
                aResearchItem,
                totalComputationRequired,
                computationRequiredPerSec,
                researchEUt,
                aInputs,
                aFluidInputs,
                aOutput,
                assDuration,
                assEUt);
        }
        return registered;
    }

    public static boolean addResearchableAssemblylineRecipe(ItemStack aResearchItem, int totalComputationRequired,
        int computationRequiredPerSec, int researchEUt, int researchAmperage, Object[] aInputs,
        FluidStack[] aFluidInputs, ItemStack aOutput, int assDuration, int assEUt) {
        boolean registered = TTRecipeAdder.addResearchableAssemblylineRecipe(
            aResearchItem,
            totalComputationRequired,
            computationRequiredPerSec,
            researchEUt,
            researchAmperage,
            aInputs,
            aFluidInputs,
            aOutput,
            assDuration,
            assEUt);
        if (registered) {
            science$registerStandardAssemblyLine(
                aResearchItem,
                totalComputationRequired,
                computationRequiredPerSec,
                researchEUt,
                aInputs,
                aFluidInputs,
                aOutput,
                assDuration,
                assEUt);
        }
        return registered;
    }

    private static void science$registerStandardAssemblyLine(ItemStack aResearchItem, int totalComputationRequired,
        int computationRequiredPerSec, int researchEUt, ItemStack[] aInputs, FluidStack[] aFluidInputs,
        ItemStack aOutput, int assDuration, int assEUt) {
        Object[] convertedInputs = new Object[aInputs == null ? 0 : aInputs.length];
        if (aInputs != null && aInputs.length > 0) {
            System.arraycopy(aInputs, 0, convertedInputs, 0, aInputs.length);
        }
        science$registerStandardAssemblyLine(
            aResearchItem,
            totalComputationRequired,
            computationRequiredPerSec,
            researchEUt,
            convertedInputs,
            aFluidInputs,
            aOutput,
            assDuration,
            assEUt);
    }

    private static void science$registerStandardAssemblyLine(ItemStack aResearchItem, int totalComputationRequired,
        int computationRequiredPerSec, int researchEUt, Object[] aInputs, FluidStack[] aFluidInputs, ItemStack aOutput,
        int assDuration, int assEUt) {
        if (aResearchItem == null || aOutput == null) return;
        if (aInputs == null) aInputs = new Object[0];

        int scanningTimeSeconds = computationRequiredPerSec > 0
            ? Math.max(1, (totalComputationRequired + computationRequiredPerSec - 1) / computationRequiredPerSec)
            : Math.max(1, totalComputationRequired);
        long scanningTicks = (long) scanningTimeSeconds * GTRecipeBuilder.SECONDS;
        int scanningTime = scanningTicks > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) scanningTicks;

        try {
            // Register a matching standard AssemblyLine entry so data sticks and GT machines can resolve it.
            RecipeBuilder.builder()
                .metadata(GTRecipeConstants.RESEARCH_ITEM, aResearchItem.copy())
                .metadata(GTRecipeConstants.SCANNING, new Scanning(scanningTime, researchEUt))
                .itemInputs(Arrays.copyOf(aInputs, aInputs.length))
                .fluidInputs(science$sanitizeFluids(aFluidInputs))
                .itemOutputs(aOutput.copy())
                .duration(assDuration)
                .eut(assEUt)
                .addTo(GTRecipeConstants.AssemblyLine);
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn("Failed to mirror GTNL assembly line recipe into GT AssemblyLine: {}", aOutput, e);
        }
    }

    private static FluidStack[] science$sanitizeFluids(FluidStack[] fluidInputs) {
        if (fluidInputs == null || fluidInputs.length == 0) return null;

        int validCount = 0;
        for (FluidStack fluidInput : fluidInputs) {
            if (fluidInput != null) validCount++;
        }
        if (validCount == 0) return null;

        FluidStack[] sanitized = new FluidStack[validCount];
        int index = 0;
        for (FluidStack fluidInput : fluidInputs) {
            if (fluidInput != null) {
                sanitized[index++] = fluidInput.copy();
            }
        }
        return sanitized;
    }
}
