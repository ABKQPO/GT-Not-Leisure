package com.science.gtnl.utils.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.item.ItemStack;

import com.science.gtnl.mixins.early.gregtech.AccessorEyeOfHarmonyRecipe;

import gtneioreplugin.plugin.block.BlockDimensionDisplay;
import gtneioreplugin.plugin.block.ModBlocks;
import tectech.recipe.EyeOfHarmonyRecipe;
import tectech.util.FluidStackLong;
import tectech.util.ItemStackLong;

public class EyeOfHarmonyRecipeFactory {

    public static final HashMap<String, EyeOfHarmonyRecipe> customRecipeHashMap = new HashMap<>();

    public static EyeOfHarmonyRecipe createCustomRecipe(ItemStack recipeTriggerItem,
        ArrayList<ItemStackLong> outputItems, ArrayList<FluidStackLong> outputFluids, long rocketTierOfRecipe,
        long euStartCost, long euOutput, long hydrogenRequirement, long heliumRequirement, long miningTimeSeconds,
        double baseSuccessChance) {

        EyeOfHarmonyRecipe instance = new EyeOfHarmonyRecipe(
            new ArrayList<>(),
            (BlockDimensionDisplay) ModBlocks.blocks.get("DD"),
            1.0,
            hydrogenRequirement,
            heliumRequirement,
            miningTimeSeconds,
            rocketTierOfRecipe,
            baseSuccessChance);

        AccessorEyeOfHarmonyRecipe accessor = (AccessorEyeOfHarmonyRecipe) instance;

        accessor.setRecipeTriggerItem(recipeTriggerItem);
        accessor.setOutputItems(outputItems);

        long sumOfItems = outputItems.stream()
            .mapToLong(ItemStackLong::getStackSize)
            .sum();
        accessor.setSumOfItems(sumOfItems);

        accessor.getItemStackToProbabilityMap()
            .clear();
        accessor.getItemStackToTrueStackSizeMap()
            .clear();

        for (ItemStackLong itemStackLong : outputItems) {
            double stackSize = (double) itemStackLong.getStackSize();
            double probability = Math.round(100_000 * stackSize / sumOfItems) / 1000.0;
            accessor.getItemStackToProbabilityMap()
                .put(itemStackLong.itemStack, probability);
            accessor.getItemStackToTrueStackSizeMap()
                .put(itemStackLong.itemStack, itemStackLong.stackSize);
        }

        accessor.setOutputFluids(outputFluids);
        accessor.setRocketTier(rocketTierOfRecipe);
        accessor.setSpacetimeCasingTierRequired(Math.min(8, rocketTierOfRecipe));
        accessor.setEuStartCost(euStartCost);
        accessor.setEuOutput(euOutput);
        accessor.setRecipeEnergyEfficiency((double) euOutput / euStartCost * 100);
        accessor.setHydrogenRequirement(hydrogenRequirement);
        accessor.setHeliumRequirement(heliumRequirement);
        accessor.setMiningTimeSeconds(miningTimeSeconds);
        accessor.setBaseSuccessChance(baseSuccessChance);

        return instance;
    }

    public static void addCustomRecipeEntry(ItemStack recipeTriggerItem, ItemStackLong[] outputItems,
        FluidStackLong[] outputFluids, long rocketTierOfRecipe, long euStartCost, long euOutput,
        long hydrogenRequirement, long heliumRequirement, long miningTimeSeconds, double baseSuccessChance) {
        if (recipeTriggerItem == null) return;

        ArrayList<ItemStackLong> itemsList = new ArrayList<>();
        if (outputItems != null) {
            Collections.addAll(itemsList, outputItems);
        }

        ArrayList<FluidStackLong> fluidsList = new ArrayList<>();
        if (outputFluids != null) {
            Collections.addAll(fluidsList, outputFluids);
        }

        addCustomRecipeEntry(
            recipeTriggerItem,
            itemsList,
            fluidsList,
            rocketTierOfRecipe,
            euStartCost,
            euOutput,
            hydrogenRequirement,
            heliumRequirement,
            miningTimeSeconds,
            baseSuccessChance);
    }

    public static void addCustomRecipeEntry(ItemStack recipeTriggerItem, ArrayList<ItemStackLong> outputItems,
        ArrayList<FluidStackLong> outputFluids, long rocketTierOfRecipe, long euStartCost, long euOutput,
        long hydrogenRequirement, long heliumRequirement, long miningTimeSeconds, double baseSuccessChance) {
        if (recipeTriggerItem == null) return;

        EyeOfHarmonyRecipe recipe = createCustomRecipe(
            recipeTriggerItem,
            outputItems,
            outputFluids,
            rocketTierOfRecipe,
            euStartCost,
            euOutput,
            hydrogenRequirement,
            heliumRequirement,
            miningTimeSeconds,
            baseSuccessChance);

        String key = recipeTriggerItem.getUnlocalizedName() + "_" + recipeTriggerItem.getItemDamage();

        customRecipeHashMap.put(key, recipe);
    }
}
