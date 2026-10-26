package com.science.gtnl.mixins.late.appliedEnergistics.quantum;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingJob;
import com.science.gtnl.common.crafting.quantum.ae2.QuantumCraftingMode;
import com.science.gtnl.common.machine.multiblock.QuantumComputer;

import appeng.api.config.CraftingMode;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingCallback;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.data.IAEStack;
import appeng.crafting.CraftingLink;
import appeng.me.cache.CraftingGridCache;
import appeng.me.cluster.implementations.CraftingCPUCluster;

@Mixin(value = CraftingGridCache.class, remap = false)
public abstract class MixinCraftingGridCache {

    @Shadow
    @Final
    protected IGrid grid;

    @Shadow
    @Final
    protected Set<CraftingCPUCluster> craftingCPUClusters;

    @Shadow
    public abstract void addLink(final CraftingLink link);

    @Inject(method = "updateCPUClusters()V", at = @At("RETURN"), require = 1)
    private void injectUpdateCPUClusters(final CallbackInfo ci) {
        for (final IGridNode ecNode : grid.getMachines(QuantumComputer.class)) {
            final var ec = (QuantumComputer) ecNode.getMachine();
            ec.forEachCPU(cpu -> {
                this.craftingCPUClusters.add(cpu);

                final CraftingLink craftingLink = (CraftingLink) cpu.getLastCraftingLink();
                if (craftingLink != null) {
                    this.addLink(craftingLink);
                }
            });
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Inject(
        method = "beginCraftingJob(Lnet/minecraft/world/World;Lappeng/api/networking/IGrid;Lappeng/api/networking/security/BaseActionSource;Lappeng/api/storage/data/IAEStack;Lappeng/api/config/CraftingMode;ZLappeng/api/networking/crafting/ICraftingCallback;)Ljava/util/concurrent/Future;",
        at = @At("HEAD"),
        cancellable = true,
        require = 1)
    private void gtnl$beginQuantumCraftingJob(World world, IGrid grid, BaseActionSource actionSrc, IAEStack<?> stack,
        CraftingMode craftingMode, boolean liteMode, ICraftingCallback cb,
        CallbackInfoReturnable<Future<ICraftingJob>> cir) {
        if (world == null || grid == null || stack == null || !QuantumCraftingMode.shouldUse(grid, actionSrc)) {
            return;
        }
        cir.setReturnValue(
            (Future) new QuantumCraftingJob(world, grid, actionSrc, (IAEStack) stack, craftingMode, cb).schedule());
    }

    // The automatic CPU choice gives up when its first candidate refuses the job
    @WrapOperation(
        method = "submitJob(Lappeng/api/networking/crafting/ICraftingJob;Lappeng/api/networking/crafting/ICraftingRequester;Lappeng/api/networking/crafting/ICraftingCPU;ZLappeng/api/networking/security/BaseActionSource;Z)Lappeng/api/networking/crafting/ICraftingLink;",
        at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"),
        require = 1)
    private boolean gtnl$skipCPUsRefusingQuantumJob(List<Object> candidates, Object cpu, Operation<Boolean> original,
        @Local(argsOnly = true) ICraftingJob job) {
        if (job instanceof QuantumCraftingJob && !job.supportsCPUCluster((ICraftingCPU) cpu)) {
            return false;
        }
        return original.call(candidates, cpu);
    }
}
