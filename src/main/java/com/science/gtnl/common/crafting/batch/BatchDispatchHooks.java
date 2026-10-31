package com.science.gtnl.common.crafting.batch;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import net.minecraft.inventory.InventoryCrafting;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.mixinHelper.LargeInventoryCrafting;
import com.science.gtnl.common.block.blocks.tile.TileEntityMEChisel;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.BatchPlan;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.CommitResult;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.MediumStrategy;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.SessionAllocation;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.SessionConsumption;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.SessionSegment;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.mixins.late.appliedEnergistics.AccessorTaskProgress;
import com.science.gtnl.utils.ChiselPatternDetails;
import com.science.gtnl.utils.DireCraftingPatternDetails;
import com.science.gtnl.utils.ECraftingCPUCluster;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.storage.data.IAEStack;
import appeng.crafting.MECraftingInventory;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.diagnostics.CraftingDiagnosticSessionId;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;

public class BatchDispatchHooks {

    public static final CraftingBatchPlanner BATCH_PLANNER = new CraftingBatchPlannerImpl();

    public static class Context {

        private enum Phase {
            UNPLANNED,
            PLANNED,
            REJECTED,
            COMMITTED
        }

        private final ICraftingPatternDetails details;
        private final AccessorTaskProgress taskProgress;
        private final MediumStrategy mediumStrategy;
        private BatchPlan plan;
        private Phase phase = Phase.UNPLANNED;
        private SessionConsumption<CraftingDiagnosticSessionId> sessionConsumption;

        public Context(ICraftingPatternDetails details, AccessorTaskProgress taskProgress,
            MediumStrategy mediumStrategy) {
            this.details = details;
            this.taskProgress = taskProgress;
            this.mediumStrategy = mediumStrategy;
        }

        public void resetForPlan(BatchPlan plan) {
            this.plan = Objects.requireNonNull(plan);
            this.phase = Phase.PLANNED;
            this.sessionConsumption = null;
        }

        public boolean isPlannedBatch() {
            return this.plan != null && this.plan.isBatched();
        }

        public boolean isRejected() {
            return phase == Phase.REJECTED;
        }

        public boolean isCommitted() {
            return phase == Phase.COMMITTED;
        }

        public long getPlannedCrafts() {
            return Objects.requireNonNull(plan, "dispatch has no plan")
                .getCrafts();
        }

        public boolean hasSessionConsumption() {
            return sessionConsumption != null;
        }

        public void reject() {
            this.phase = Phase.REJECTED;
        }

        private int commit(CommitResult commit) {
            if (phase == Phase.REJECTED || !isPlannedBatch() || taskProgress == null || !commit.isCommitted())
                throw new IllegalStateException("only an accepted planned batch can be committed");
            taskProgress.setValue(commit.getTaskValueBeforeNativeDecrement());
            // AE can reuse the expanded plan for another accepted push during the same task visit.
            sessionConsumption = null;
            phase = Phase.COMMITTED;
            return commit.getRemainingOperationsBeforeNativeDecrement();
        }

        private CraftingDiagnosticSessionId consumeSessions(AccessorTaskProgress progress) {
            if (!isCommitted() || sessionConsumption != null)
                throw new IllegalStateException("diagnostics must consume an accepted batch exactly once");
            sessionConsumption = BATCH_PLANNER
                .consumeSessions(sessionSegments(progress.getDiagnosticSessionCrafts()), plan.getCrafts());
            return sessionConsumption.getFirstSessionId();
        }
    }

    public static Context beginDispatch(ICraftingPatternDetails key, List<ICraftingMedium> media, Object progress) {
        BooleanArrayList compatibility = new BooleanArrayList(media.size());
        for (ICraftingMedium medium : media) {
            compatibility.add(isCompatiblePair(key, medium));
        }

        if (!(progress instanceof AccessorTaskProgress taskProgress)) {
            ScienceNotLeisure.LOG.error("AE crafting task progress is missing the GTNL accessor for pattern {}", key);
            return new Context(key, null, MediumStrategy.NATIVE);
        }
        return new Context(key, taskProgress, BATCH_PLANNER.resolveMediumStrategy(compatibility));
    }

    public static List<IAEStack<?>> planExpandedInputs(CraftingCPUCluster cluster, ICraftingPatternDetails details,
        List<IAEStack<?>> expandedInputs, IEnergyGrid eg, Context context, MECraftingInventory inventory) {
        if (expandedInputs == null || context == null || context.taskProgress == null) return expandedInputs;

        // A quantum CPU only extracts the variants a substituting pattern has budget for
        BatchPlan plan = cluster instanceof ECraftingCPUCluster quantum && quantum.hasVariantBudget(details)
            ? BATCH_PLANNER.plan(
                context.taskProgress.getValue(),
                context.mediumStrategy,
                expandedInputs,
                details.getCondensedAEOutputs(),
                input -> quantum.getBudgetedAvailable(details, input),
                requested -> eg.extractAEPower(requested, Actionable.SIMULATE, PowerMultiplier.CONFIG))
            : BATCH_PLANNER.plan(
                context.taskProgress.getValue(),
                context.mediumStrategy,
                expandedInputs,
                details.getCondensedAEOutputs(),
                inventory,
                requested -> eg.extractAEPower(requested, Actionable.SIMULATE, PowerMultiplier.CONFIG));
        context.resetForPlan(plan);
        if (!plan.isBatched()) return expandedInputs;

        try {
            return BATCH_PLANNER.scaleInputs(expandedInputs, plan.getCrafts());
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

    public static IAEStack<?> validateExtraction(MECraftingInventory craftingInventory, IAEStack<?> request,
        IAEStack<?> extracted, Context context) {
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

    // Returns null when the batch must not be pushed; otherwise the inventory is sized for the batch.
    public static CommitResult prepareCommit(Context context, ICraftingPatternDetails details,
        InventoryCrafting craftingInventory, int remainingOperations) {
        if (context.isRejected()) return null;
        if (!(craftingInventory instanceof LargeInventoryCrafting largeInventory)) {
            ScienceNotLeisure.LOG
                .error("AE supplied a crafting inventory without long batch support for pattern {}", details);
            context.reject();
            return null;
        }

        CommitResult commit;
        try {
            commit = BATCH_PLANNER.commit(context.plan, true, context.taskProgress.getValue(), remainingOperations);
        } catch (RuntimeException exception) {
            ScienceNotLeisure.LOG.error("AE batch commit precondition failed for pattern {}", details, exception);
            context.reject();
            return null;
        }

        largeInventory.setAssemblerSize(context.plan.getCrafts());
        return commit;
    }

    // Returns the remaining operations to store after the push was accepted.
    public static int finishCommit(Context context, CommitResult commit) {
        return context.commit(commit);
    }

    public static CraftingDiagnosticSessionId consumeSessions(Context context, AccessorTaskProgress taskProgress) {
        CraftingDiagnosticSessionId first = context.consumeSessions(taskProgress);
        long consumedCrafts = context.sessionConsumption.getConsumedCrafts();
        if (consumedCrafts > 0 && consumedCrafts < context.plan.getCrafts()) {
            ScienceNotLeisure.LOG.error(
                "AE batch diagnostics session counts ended early for pattern {}: planned {}, consumed {}",
                context.details,
                context.plan.getCrafts(),
                consumedCrafts);
        }
        return first;
    }

    public static void recordExpectedOutput(Context context, IAEStack<?> output,
        BiConsumer<IAEStack<?>, CraftingDiagnosticSessionId> record) {
        long crafts = context.plan.getCrafts();
        if (output.getStackSize() % crafts != 0) {
            ScienceNotLeisure.LOG.error(
                "AE batch diagnostics received a non-divisible output for pattern {}: amount {}, crafts {}",
                context.details,
                output.getStackSize(),
                crafts);
            return;
        }

        long baseOutput = output.getStackSize() / crafts;
        for (SessionAllocation<CraftingDiagnosticSessionId> allocation : context.sessionConsumption.getAllocations()) {
            IAEStack<?> sessionOutput = output.copy();
            sessionOutput.setStackSize(BATCH_PLANNER.checkedMultiply(baseOutput, allocation.getCrafts()));
            record.accept(sessionOutput, allocation.getSessionId());
        }
    }

    public static boolean isCompatiblePair(ICraftingPatternDetails details, ICraftingMedium medium) {
        return (details instanceof DireCraftingPatternDetails && medium instanceof AssemblerMatrix)
            || (details instanceof ChiselPatternDetails && medium instanceof TileEntityMEChisel);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Deque<SessionSegment<CraftingDiagnosticSessionId>> sessionSegments(LinkedList<?> segments) {
        return (Deque) segments;
    }
}
