package com.science.gtnl.Utils.recipes;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;

import com.science.gtnl.mixins.late.Gregtech.EyeOfHarmonyRecipeAccessor;

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

        EyeOfHarmonyRecipeAccessor accessor = (EyeOfHarmonyRecipeAccessor) instance;

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
        accessor.setRecipeEnergyEfficiency((double) euOutput / euStartCost);
        accessor.setHydrogenRequirement(hydrogenRequirement);
        accessor.setHeliumRequirement(heliumRequirement);
        accessor.setMiningTimeSeconds(miningTimeSeconds);
        accessor.setBaseSuccessChance(baseSuccessChance);

        return instance;
    }

    public static void addCustomRecipeEntry(ItemStack recipeTriggerItem, ArrayList<Pair<ItemStack, Long>> outputItems,
        ArrayList<Pair<FluidStack, Long>> outputFluids, long rocketTierOfRecipe, long euStartCost, long euOutput,
        long hydrogenRequirement, long heliumRequirement, long miningTimeSeconds, double baseSuccessChance) {

        ArrayList<ItemStackLong> outputItemsLong = new ArrayList<>();
        for (Pair<ItemStack, Long> pair : outputItems) {
            outputItemsLong.add(new ItemStackLong(pair.getLeft(), pair.getRight()));
        }

        ArrayList<FluidStackLong> outputFluidsLong = new ArrayList<>();
        for (Pair<FluidStack, Long> pair : outputFluids) {
            outputFluidsLong.add(new FluidStackLong(pair.getLeft(), pair.getRight()));
        }

        EyeOfHarmonyRecipe recipe = createCustomRecipe(
            recipeTriggerItem,
            outputItemsLong,
            outputFluidsLong,
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
