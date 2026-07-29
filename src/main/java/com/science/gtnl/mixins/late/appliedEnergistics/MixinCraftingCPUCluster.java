package com.science.gtnl.mixins.late.appliedEnergistics;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.ChiselPatternDetails;
import com.science.gtnl.utils.DireCraftingPatternDetails;
import com.science.gtnl.utils.LargeInventoryCrafting;
import com.science.gtnl.utils.Utils;
import com.science.gtnl.utils.crafting.CraftingBatchPlanner;
import com.science.gtnl.utils.crafting.CraftingBatchPlanner.BatchPlan;
import com.science.gtnl.utils.crafting.CraftingBatchPlanner.CommitResult;
import com.science.gtnl.utils.crafting.CraftingBatchPlanner.MediumStrategy;
import com.science.gtnl.utils.crafting.CraftingBatchPlanner.SessionAllocation;
import com.science.gtnl.utils.crafting.CraftingBatchPlanner.SessionConsumption;
import com.science.gtnl.utils.crafting.CraftingBatchPlanner.SessionSegment;
import com.science.gtnl.utils.crafting.CraftingBatchPlannerImpl;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
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

    @Unique
    private static final CraftingBatchPlanner GTNL$BATCH_PLANNER = new CraftingBatchPlannerImpl();

    @Shadow
    private int remainingOperations;

    @Shadow
    private MECraftingInventory inventory;

    /**
     * Starts one dispatch context after AE resolves every medium registered for the pattern.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cache/CraftingGridCache;getMediums(Lappeng/api/networking/crafting/ICraftingPatternDetails;)Ljava/util/List;"),
        require = 1)
    private List<ICraftingMedium> gtnl$beginDispatch(CraftingGridCache cache, ICraftingPatternDetails details,
        Operation<List<ICraftingMedium>> original,
        @Local(name = "craftingEntry") Map.Entry<ICraftingPatternDetails, ?> craftingEntry,
        @Share("gtnl$batchDispatch") LocalRef<BatchDispatchContext> contextRef) {
        List<ICraftingMedium> media = original.call(cache, details);
        List<Boolean> compatibility = new ArrayList<>(media.size());
        for (ICraftingMedium medium : media) {
            compatibility.add(gtnl$isCompatiblePair(details, medium));
        }

        Object progress = craftingEntry.getValue();
        if (!(progress instanceof AccessorTaskProgress taskProgress)) {
            ScienceNotLeisure.LOG
                .error("AE crafting task progress is missing the GTNL accessor for pattern {}", details);
            contextRef.set(new BatchDispatchContext(details, null, MediumStrategy.NATIVE));
            return media;
        }

        contextRef.set(
            new BatchDispatchContext(details, taskProgress, GTNL$BATCH_PLANNER.resolveMediumStrategy(compatibility)));
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
        @Share("gtnl$batchDispatch") LocalRef<BatchDispatchContext> contextRef) {
        List<IAEStack<?>> expandedInputs = original.call(cluster, details, cache);
        BatchDispatchContext context = contextRef.get();
        if (expandedInputs == null || context == null || context.taskProgress == null) return expandedInputs;

        BatchPlan plan = GTNL$BATCH_PLANNER.plan(
            context.taskProgress.getValue(),
            context.mediumStrategy,
            expandedInputs,
            details.getCondensedAEOutputs(),
            this.inventory,
            requested -> eg.extractAEPower(requested, Actionable.SIMULATE, PowerMultiplier.CONFIG));
        context.resetForPlan(plan);
        if (!plan.isBatched()) return expandedInputs;

        try {
            return GTNL$BATCH_PLANNER.scaleInputs(expandedInputs, plan.getCrafts());
        } catch (ArithmeticException exception) {
            ScienceNotLeisure.LOG.error(
                "AE batch input scaling violated its checked plan for pattern {} and {} crafts",
                details,
                plan.getCrafts(),
                exception);
            context.reject();
            return expandedInputs;
        }
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
        Actionable mode, Operation<IAEStack<?>> original,
        @Share("gtnl$batchDispatch") LocalRef<BatchDispatchContext> contextRef) {
        IAEStack<?> extracted = original.call(craftingInventory, request, mode);
        BatchDispatchContext context = contextRef.get();
        if (context == null || !context.isPlannedBatch() || mode != Actionable.MODULATE) return extracted;
        if (extracted != null && extracted.getStackSize() == request.getStackSize()) return extracted;

        ScienceNotLeisure.LOG.error(
            "AE batch extraction contract changed after planning for pattern {}: requested {}, extracted {}",
            context.details,
            request.getStackSize(),
            extracted == null ? 0 : extracted.getStackSize());
        context.reject();
        if (extracted != null) craftingInventory.injectItems(extracted, Actionable.MODULATE);
        return null;
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
        @Share("gtnl$batchDispatch") LocalRef<BatchDispatchContext> contextRef) {
        BatchDispatchContext context = contextRef.get();
        if (context == null || !context.isPlannedBatch()) {
            return original.call(medium, details, craftingInventory);
        }
        if (context.rejected) return false;
        if (!(craftingInventory instanceof LargeInventoryCrafting largeInventory)) {
            ScienceNotLeisure.LOG
                .error("AE supplied a crafting inventory without long batch support for pattern {}", details);
            context.reject();
            return false;
        }

        CommitResult commit;
        try {
            commit = GTNL$BATCH_PLANNER
                .commit(context.plan, true, context.taskProgress.getValue(), this.remainingOperations);
        } catch (RuntimeException exception) {
            ScienceNotLeisure.LOG.error("AE batch commit precondition failed for pattern {}", details, exception);
            context.reject();
            return false;
        }

        largeInventory.setAssemblerSize(context.plan.getCrafts());
        boolean accepted = original.call(medium, details, craftingInventory);
        if (!accepted) return false;

        context.taskProgress.setValue(commit.getTaskValueBeforeNativeDecrement());
        this.remainingOperations = commit.getRemainingOperationsBeforeNativeDecrement();
        context.committed = true;
        return true;
    }

    /**
     * Consumes diagnostics counts by session segments instead of invoking AE's synthetic one-craft accessor N times.
     */
    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster$TaskProgress;access$300(Lappeng/me/cluster/implementations/CraftingCPUCluster$TaskProgress;)Lappeng/me/diagnostics/CraftingDiagnosticSessionId;"),
        require = 1)
    private CraftingDiagnosticSessionId gtnl$consumeBatchSessions(@Coerce Object taskProgress,
        Operation<CraftingDiagnosticSessionId> original,
        @Share("gtnl$batchDispatch") LocalRef<BatchDispatchContext> contextRef) {
        BatchDispatchContext context = contextRef.get();
        if (context == null || !context.committed) return original.call(taskProgress);

        AccessorTaskProgress accessor = (AccessorTaskProgress) taskProgress;
        context.sessionConsumption = GTNL$BATCH_PLANNER
            .consumeSessions(gtnl$sessionSegments(accessor.getDiagnosticSessionCrafts()), context.plan.getCrafts());
        long consumedCrafts = context.sessionConsumption.getConsumedCrafts();
        if (consumedCrafts > 0 && consumedCrafts < context.plan.getCrafts()) {
            ScienceNotLeisure.LOG.error(
                "AE batch diagnostics session counts ended early for pattern {}: planned {}, consumed {}",
                context.details,
                context.plan.getCrafts(),
                consumedCrafts);
        }
        return context.sessionConsumption.getFirstSessionId();
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
        @Share("gtnl$batchDispatch") LocalRef<BatchDispatchContext> contextRef) {
        IAEStack<?>[] outputs = original.call(details);
        BatchDispatchContext context = contextRef.get();
        if (context == null || !context.committed) return outputs;
        return GTNL$BATCH_PLANNER.scaleOutputs(outputs, context.plan.getCrafts());
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
    private void gtnl$recordBatchExpectedOutput(@Coerce Object diagnostics, IAEStack<?> scaledOutput,
        long outputObservedAtTick, CraftingDiagnosticSessionId nativeSession, Operation<Void> original,
        @Share("gtnl$batchDispatch") LocalRef<BatchDispatchContext> contextRef) {
        BatchDispatchContext context = contextRef.get();
        if (context == null || !context.committed || context.sessionConsumption == null) {
            original.call(diagnostics, scaledOutput, outputObservedAtTick, nativeSession);
            return;
        }

        long crafts = context.plan.getCrafts();
        if (scaledOutput.getStackSize() % crafts != 0) {
            ScienceNotLeisure.LOG.error(
                "AE batch diagnostics received a non-divisible output for pattern {}: amount {}, crafts {}",
                context.details,
                scaledOutput.getStackSize(),
                crafts);
            return;
        }

        long baseOutput = scaledOutput.getStackSize() / crafts;
        for (SessionAllocation<CraftingDiagnosticSessionId> allocation : context.sessionConsumption.getAllocations()) {
            IAEStack<?> sessionOutput = scaledOutput.copy();
            sessionOutput.setStackSize(GTNL$BATCH_PLANNER.checkedMultiply(baseOutput, allocation.getCrafts()));
            original.call(diagnostics, sessionOutput, outputObservedAtTick, allocation.getSessionId());
        }
    }

    @Unique
    private static boolean gtnl$isCompatiblePair(ICraftingPatternDetails details, ICraftingMedium medium) {
        return (details instanceof DireCraftingPatternDetails && medium instanceof AssemblerMatrix)
            || (details instanceof ChiselPatternDetails && medium instanceof TileEntityMEChisel);
    }

    @Unique
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Deque<SessionSegment<CraftingDiagnosticSessionId>> gtnl$sessionSegments(LinkedList<?> segments) {
        return (Deque) segments;
    }

    @Inject(method = "translateFromNetwork", at = @At("HEAD"), cancellable = true)
    private static void injectTranslateFromNetwork(String name, CallbackInfoReturnable<String> cir) {
        if (!MainConfig.machine.enableHatchInterfaceTerminalEnhance) return;
        if (name == null) return;
        cir.setReturnValue(Utils.getExtraInterfaceName(name));
    }

    @Unique
    private static final class BatchDispatchContext {

        private final ICraftingPatternDetails details;
        private final AccessorTaskProgress taskProgress;
        private final MediumStrategy mediumStrategy;
        private BatchPlan plan;
        private boolean rejected;
        private boolean committed;
        private SessionConsumption<CraftingDiagnosticSessionId> sessionConsumption;

        private BatchDispatchContext(ICraftingPatternDetails details, AccessorTaskProgress taskProgress,
            MediumStrategy mediumStrategy) {
            this.details = details;
            this.taskProgress = taskProgress;
            this.mediumStrategy = mediumStrategy;
        }

        private void resetForPlan(BatchPlan plan) {
            this.plan = plan;
            this.rejected = false;
            this.committed = false;
            this.sessionConsumption = null;
        }

        private boolean isPlannedBatch() {
            return this.plan != null && this.plan.isBatched();
        }

        private void reject() {
            this.rejected = true;
        }
    }
}
