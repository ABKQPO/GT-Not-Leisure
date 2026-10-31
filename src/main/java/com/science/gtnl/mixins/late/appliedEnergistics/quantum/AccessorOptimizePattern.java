package com.science.gtnl.mixins.late.appliedEnergistics.quantum;

import java.util.HashSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEStack;

@Mixin(targets = "appeng.container.implementations.ContainerOptimizePatterns$Pattern", remap = false)
public interface AccessorOptimizePattern {

    @Accessor("patternDetails")
    HashSet<ICraftingPatternDetails> gtnl$getPatternDetails();

    @Accessor("requestedCrafts")
    long gtnl$getRequestedCrafts();

    @Invoker("addCraftingTask")
    void gtnl$addCraftingTask(ICraftingPatternDetails pattern, long totalCrafts);

    @Invoker("getCraftAmountForItem")
    long gtnl$getCraftAmountForItem(IAEStack<?> stack);

    @Invoker("getPattern")
    ICraftingPatternDetails gtnl$getPattern();

    @Invoker("getMaxBitMultiplier")
    int gtnl$getMaxBitMultiplier();
}
