package com.science.gtnl.mixins.early.gregtech;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import gnu.trove.map.TMap;
import tectech.recipe.EyeOfHarmonyRecipe;
import tectech.util.FluidStackLong;
import tectech.util.ItemStackLong;

@Mixin(value = EyeOfHarmonyRecipe.class, remap = false)
public interface AccessorEyeOfHarmonyRecipe {

    @Mutable
    @Accessor("recipeTriggerItem")
    void setRecipeTriggerItem(ItemStack recipeTriggerItem);

    @Mutable
    @Accessor("outputItems")
    void setOutputItems(ArrayList<ItemStackLong> outputItems);

    @Mutable
    @Accessor("sumOfItems")
    void setSumOfItems(long sumOfItems);

    @Mutable
    @Accessor("outputFluids")
    void setOutputFluids(ArrayList<FluidStackLong> outputFluids);

    @Mutable
    @Accessor("rocketTier")
    void setRocketTier(long rocketTier);

    @Mutable
    @Accessor("spacetimeCasingTierRequired")
    void setSpacetimeCasingTierRequired(long spacetimeCasingTierRequired);

    @Mutable
    @Accessor("euStartCost")
    void setEuStartCost(long euStartCost);

    @Mutable
    @Accessor("euOutput")
    void setEuOutput(long euOutput);

    @Mutable
    @Accessor("recipeEnergyEfficiency")
    void setRecipeEnergyEfficiency(double recipeEnergyEfficiency);

    @Mutable
    @Accessor("hydrogenRequirement")
    void setHydrogenRequirement(long hydrogenRequirement);

    @Mutable
    @Accessor("heliumRequirement")
    void setHeliumRequirement(long heliumRequirement);

    @Mutable
    @Accessor("miningTimeSeconds")
    void setMiningTimeSeconds(long miningTimeSeconds);

    @Mutable
    @Accessor("baseSuccessChance")
    void setBaseSuccessChance(double baseSuccessChance);

    @Accessor("itemStackToProbabilityMap")
    TMap<ItemStack, Double> getItemStackToProbabilityMap();

    @Accessor("itemStackToTrueStackSizeMap")
    TMap<ItemStack, Long> getItemStackToTrueStackSizeMap();
}
