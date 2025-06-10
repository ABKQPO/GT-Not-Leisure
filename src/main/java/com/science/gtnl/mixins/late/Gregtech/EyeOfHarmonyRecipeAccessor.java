package com.science.gtnl.mixins.late.Gregtech;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import gnu.trove.map.TMap;
import tectech.recipe.EyeOfHarmonyRecipe;
import tectech.util.FluidStackLong;
import tectech.util.ItemStackLong;

@SuppressWarnings("UnusedMixin")
@Mixin(value = EyeOfHarmonyRecipe.class, remap = false)
public interface EyeOfHarmonyRecipeAccessor {

    @Accessor("recipeTriggerItem")
    void setRecipeTriggerItem(ItemStack recipeTriggerItem);

    @Accessor("outputItems")
    void setOutputItems(ArrayList<ItemStackLong> outputItems);

    @Accessor("sumOfItems")
    void setSumOfItems(long sumOfItems);

    @Accessor("outputFluids")
    void setOutputFluids(ArrayList<FluidStackLong> outputFluids);

    @Accessor("rocketTier")
    void setRocketTier(long rocketTier);

    @Accessor("spacetimeCasingTierRequired")
    void setSpacetimeCasingTierRequired(long spacetimeCasingTierRequired);

    @Accessor("euStartCost")
    void setEuStartCost(long euStartCost);

    @Accessor("euOutput")
    void setEuOutput(long euOutput);

    @Accessor("recipeEnergyEfficiency")
    void setRecipeEnergyEfficiency(double recipeEnergyEfficiency);

    @Accessor("hydrogenRequirement")
    void setHydrogenRequirement(long hydrogenRequirement);

    @Accessor("heliumRequirement")
    void setHeliumRequirement(long heliumRequirement);

    @Accessor("miningTimeSeconds")
    void setMiningTimeSeconds(long miningTimeSeconds);

    @Accessor("baseSuccessChance")
    void setBaseSuccessChance(double baseSuccessChance);

    @Accessor("itemStackToProbabilityMap")
    TMap<ItemStack, Double> getItemStackToProbabilityMap();

    @Accessor("itemStackToTrueStackSizeMap")
    TMap<ItemStack, Long> getItemStackToTrueStackSizeMap();
}
