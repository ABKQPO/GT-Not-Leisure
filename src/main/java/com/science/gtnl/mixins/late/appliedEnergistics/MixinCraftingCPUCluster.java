package com.science.gtnl.mixins.late.appliedEnergistics;

import java.util.List;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.science.gtnl.common.crafting.batch.BatchDispatchHooks;
import com.science.gtnl.common.crafting.batch.BatchDispatchHooks.Context;
import com.science.gtnl.utils.crafting.CraftingBatchPlanner.CommitResult;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.storage.data.IAEStack;
import appeng.crafting.MECraftingInventory;
import appeng.me.cache.CraftingGridCache;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.diagnostics.CraftingDiagnosticSessionId;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class MixinCraftingCPUCluster {

    @Shadow
    protected int remainingOperations;

    @Shadow
    protected MECraftingInventory inventory;

    /**
     * Starts one dispatch context after AE resolves every medium registered for the pattern.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cache/CraftingGridCache;getMediums(Lappeng/api/networking/crafting/ICraftingPatternDetails;)Ljava/util/List;"),
        require = 1)
    private List<ICraftingMedium> gtnl$beginDispatch(CraftingGridCache cache, ICraftingPatternDetails key,
        Operation<List<ICraftingMedium>> original,
        @Local(name = "craftingEntry") Map.Entry<ICraftingPatternDetails, ?> craftingEntry,
        @Share("gtnl$batchDispatch") LocalRef<Context> contextRef) {
        List<ICraftingMedium> media = original.call(cache, key);
        contextRef.set(BatchDispatchHooks.beginDispatch(key, media, craftingEntry.getValue()));
        return media;
    }

    /**
     * Plans from AE's expanded one-craft inputs and returns independent stacks scaled to the exact batch.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;getExpandedInputs(Lappeng/api/networking/crafting/ICraftingPatternDetails;Lappeng/me/cache/CraftingGridCache;)Ljava/util/List;"),
        require = 1)
    private List<IAEStack<?>> gtnl$planExpandedInputs(CraftingCPUCluster cluster, ICraftingPatternDetails details,
        CraftingGridCache cache, Operation<List<IAEStack<?>>> original, IEnergyGrid eg,
        @Share("gtnl$batchDispatch") LocalRef<Context> contextRef) {
        List<IAEStack<?>> expandedInputs = original.call(cluster, details, cache);
        return BatchDispatchHooks.planExpandedInputs(
            (CraftingCPUCluster) (Object) this,
            details,
            expandedInputs,
            eg,
            contextRef.get(),
            this.inventory);
    }

    /**
     * Rejects a planned dispatch if the actual extraction no longer matches the expanded request.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/crafting/MECraftingInventory;extractItems(Lappeng/api/storage/data/IAEStack;Lappeng/api/config/Actionable;)Lappeng/api/storage/data/IAEStack;"),
        require = 1)
    private IAEStack<?> gtnl$validateActualExtraction(MECraftingInventory craftingInventory, IAEStack<?> request,
        Actionable mode, Operation<IAEStack<?>> original, @Share("gtnl$batchDispatch") LocalRef<Context> contextRef) {
        IAEStack<?> extracted = original.call(craftingInventory, request, mode);
        Context context = contextRef.get();
        if (context == null || !context.isPlannedBatch() || mode != Actionable.MODULATE) return extracted;
        return BatchDispatchHooks.validateExtraction(craftingInventory, request, extracted, context);
    }

    /**
     * Treats pushPattern as the transaction boundary for task, operation-budget, and diagnostics state.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingMedium;pushPattern(Lappeng/api/networking/crafting/ICraftingPatternDetails;Lnet/minecraft/inventory/InventoryCrafting;)Z"),
        require = 1)
    private boolean gtnl$commitAcceptedBatch(ICraftingMedium medium, ICraftingPatternDetails details,
        InventoryCrafting craftingInventory, Operation<Boolean> original,
        @Share("gtnl$batchDispatch") LocalRef<Context> contextRef) {
        Context context = contextRef.get();
        if (context == null || !context.isPlannedBatch()) {
            return original.call(medium, details, craftingInventory);
        }
        CommitResult commit = BatchDispatchHooks
            .prepareCommit(context, details, craftingInventory, this.remainingOperations);
        if (commit == null || !original.call(medium, details, craftingInventory)) return false;
        this.remainingOperations = BatchDispatchHooks.finishCommit(context, commit);
        return true;
    }

    /**
     * Consumes diagnostics counts by session segments instead of invoking AE's one-craft session method repeatedly.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster$TaskProgress;consumeCraftSession()Lappeng/me/diagnostics/CraftingDiagnosticSessionId;"),
        require = 1)
    private CraftingDiagnosticSessionId gtnl$consumeBatchSessions(CraftingCPUCluster.TaskProgress taskProgress,
        Operation<CraftingDiagnosticSessionId> original, @Share("gtnl$batchDispatch") LocalRef<Context> contextRef) {
        Context context = contextRef.get();
        if (context == null || !context.isCommitted()) return original.call(taskProgress);
        return BatchDispatchHooks.consumeSessions(context, (AccessorTaskProgress) taskProgress);
    }

    /**
     * Returns checked output copies so diagnostics, postChange, waitingFor, and status observe one batch quantity.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/networking/crafting/ICraftingPatternDetails;getCondensedAEOutputs()[Lappeng/api/storage/data/IAEStack;"),
        require = 1)
    private IAEStack<?>[] gtnl$scaleCommittedOutputs(ICraftingPatternDetails details, Operation<IAEStack<?>[]> original,
        @Share("gtnl$batchDispatch") LocalRef<Context> contextRef) {
        IAEStack<?>[] outputs = original.call(details);
        Context context = contextRef.get();
        if (context == null || !context.isCommitted()) return outputs;
        return BatchDispatchHooks.BATCH_PLANNER.scaleOutputs(outputs, context.crafts());
    }

    /**
     * Splits a scaled expected output across the session segments consumed by this committed batch.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCpuDiagnostics;recordExpectedOutput(Lappeng/api/storage/data/IAEStack;JLappeng/me/diagnostics/CraftingDiagnosticSessionId;)V"),
        require = 1)
    private void gtnl$recordBatchExpectedOutput(@Coerce Object diagnostics, IAEStack<?> output,
        long outputObservedAtTick, CraftingDiagnosticSessionId diagnosticSessionId, Operation<Void> original,
        @Share("gtnl$batchDispatch") LocalRef<Context> contextRef) {
        Context context = contextRef.get();
        if (context == null || !context.isCommitted() || !context.hasSessionConsumption()) {
            original.call(diagnostics, output, outputObservedAtTick, diagnosticSessionId);
            return;
        }
        BatchDispatchHooks.recordExpectedOutput(
            context,
            output,
            (sessionOutput, sessionId) -> original.call(diagnostics, sessionOutput, outputObservedAtTick, sessionId));
    }
}
