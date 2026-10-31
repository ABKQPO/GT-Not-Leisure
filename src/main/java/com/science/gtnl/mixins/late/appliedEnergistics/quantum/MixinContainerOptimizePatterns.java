package com.science.gtnl.mixins.late.appliedEnergistics.quantum;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.entity.player.InventoryPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingJob;
import com.science.gtnl.common.crafting.quantum.hooks.OptimizePatternsHooks;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.storage.data.IAEStack;
import appeng.container.ContainerSubGui;
import appeng.container.implementations.ContainerOptimizePatterns;

@Mixin(value = ContainerOptimizePatterns.class, remap = false)
public abstract class MixinContainerOptimizePatterns extends ContainerSubGui {

    @Shadow
    private ICraftingJob result;

    @Shadow
    HashMap<IAEStack<?>, Object> patterns;

    public MixinContainerOptimizePatterns(InventoryPlayer ip, Object anchor) {
        super(ip, anchor);
    }

    @Shadow
    private IGrid getGrid() {
        throw new AssertionError();
    }

    @Shadow
    private Set<IGrid> collectReachableGrids(IGrid grid) {
        throw new AssertionError();
    }

    @Inject(method = "setResult", at = @At("HEAD"), cancellable = true)
    private void gtnl$setQuantumResult(ICraftingJob job, CallbackInfo ci) {
        if (!(job instanceof QuantumCraftingJob<?>quantumJob)) return;
        ci.cancel();

        this.result = job;
        this.patterns.clear();
        if (OptimizePatternsHooks
            .collectPatterns(quantumJob, this.patterns, getGrid(), grid -> collectReachableGrids(grid))) {
            OptimizePatternsHooks.sendPatterns(this.patterns, this.crafters);
        }
    }
}
