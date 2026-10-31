package com.science.gtnl.common.crafting.batch;

import static com.gtnltest.TestSupport.check;
import static com.gtnltest.TestSupport.rejects;

import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.science.gtnl.common.crafting.batch.BatchDispatchHooks.Context;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.BatchPlan;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.CommitResult;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.LimitingFactor;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.MediumStrategy;
import com.science.gtnl.common.crafting.batch.CraftingBatchPlanner.SessionSegment;
import com.science.gtnl.mixins.late.appliedEnergistics.AccessorTaskProgress;

import appeng.api.storage.data.IAEItemStack;
import appeng.me.diagnostics.CraftingDiagnosticSessionId;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@Tag("machine")
public final class BatchDispatchTest {

    @Test
    void commitsOnlyFullyPlannedBatches() {
        Progress progress = new Progress();
        Context context = new Context(null, progress, MediumStrategy.BATCH);
        BatchPlan plan = new BatchPlan(3, LimitingFactor.REQUESTED);
        context.resetForPlan(plan);
        CommitResult refused = BatchDispatchHooks.BATCH_PLANNER.commit(plan, false, progress.value, 8);
        check(
            !refused.isCommitted() && progress.value == 10 && !context.isCommitted(),
            "a refused medium does not consume task progress or mark a batch committed");
        check(rejects(() -> BatchDispatchHooks.finishCommit(context, refused)), "a refused push cannot be committed");

        context.reject();
        check(
            BatchDispatchHooks.prepareCommit(context, null, null, 8) == null && progress.value == 10,
            "a rejected extraction cannot reach the medium or consume progress");
        check(
            rejects(() -> BatchDispatchHooks.consumeSessions(context, progress)),
            "uncommitted work cannot consume diagnostic sessions");

        context.resetForPlan(plan);
        check(!context.isRejected() && !context.isCommitted(), "replanning clears the failed attempt");
        CommitResult accepted = BatchDispatchHooks.BATCH_PLANNER.commit(plan, true, progress.value, 8);
        int remaining = BatchDispatchHooks.finishCommit(context, accepted);
        check(
            context.isCommitted() && progress.value == 8 && remaining == 6,
            "accepted batch leaves exactly one task and operation decrement to AE");

        CraftingDiagnosticSessionId first = CraftingDiagnosticSessionId.of(1);
        CraftingDiagnosticSessionId second = CraftingDiagnosticSessionId.of(2);
        progress.sessions.add(new Segment(first, 2));
        progress.sessions.add(new Segment(second, 4));
        check(
            first.equals(BatchDispatchHooks.consumeSessions(context, progress)),
            "batch diagnostics start at first owner");
        Map<CraftingDiagnosticSessionId, Long> outputs = new Object2ObjectOpenHashMap<>();
        BatchDispatchHooks.recordExpectedOutput(
            context,
            stack(6),
            (output, session) -> outputs.merge(session, output.getStackSize(), Long::sum));
        check(
            outputs.get(first) == 4 && outputs.get(second) == 2,
            "two outputs per craft are split over the exact session counts");
        check(
            rejects(() -> BatchDispatchHooks.consumeSessions(context, progress)),
            "one accepted push consumes diagnostics only once");

        // AE caches expanded inputs for a task visit and may push that plan to more than one medium.
        progress.value--;
        BatchDispatchHooks
            .finishCommit(context, BatchDispatchHooks.BATCH_PLANNER.commit(plan, true, progress.value, 5));
        check(!context.hasSessionConsumption(), "another accepted push starts fresh diagnostics for the cached plan");
        check(
            second.equals(BatchDispatchHooks.consumeSessions(context, progress)) && progress.sessions.isEmpty(),
            "the next push consumes only the remaining session crafts");
        context.resetForPlan(new BatchPlan(1, LimitingFactor.MEDIUM));
        check(
            !context.isPlannedBatch() && !context.isCommitted() && !context.hasSessionConsumption(),
            "native fallback inherits neither batch scaling nor diagnostics");
    }

    private static IAEItemStack stack(long amount) {
        long[] size = { amount };
        return (IAEItemStack) Proxy.newProxyInstance(
            BatchDispatchTest.class.getClassLoader(),
            new Class<?>[] { IAEItemStack.class },
            (proxy, method, args) -> switch (method.getName()) {
            case "getStackSize" -> size[0];
            case "copy" -> stack(size[0]);
            case "setStackSize" -> {
            size[0] = (Long) args[0];
            yield proxy;
            }
            default -> throw new UnsupportedOperationException(method.getName());
            });
    }

    private static final class Progress implements AccessorTaskProgress {

        private long value = 10;
        // AccessorTaskProgress exposes this concrete AE2 type, so the fixture must preserve its signature.
        private final LinkedList<Segment> sessions = new LinkedList<>();

        @Override
        public long getValue() {
            return value;
        }

        @Override
        public void setValue(long value) {
            this.value = value;
        }

        @Override
        public LinkedList<?> getDiagnosticSessionCrafts() {
            return sessions;
        }
    }

    private static final class Segment implements SessionSegment<CraftingDiagnosticSessionId> {

        private final CraftingDiagnosticSessionId id;
        private long remaining;

        private Segment(CraftingDiagnosticSessionId id, long remaining) {
            this.id = id;
            this.remaining = remaining;
        }

        @Override
        public CraftingDiagnosticSessionId getSessionId() {
            return id;
        }

        @Override
        public long getRemaining() {
            return remaining;
        }

        @Override
        public void setRemaining(long remaining) {
            this.remaining = remaining;
        }
    }
}
