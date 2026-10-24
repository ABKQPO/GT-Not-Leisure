package com.science.gtnl.utils.crafting;

import java.util.Deque;
import java.util.List;
import java.util.function.ToLongFunction;

import appeng.api.storage.data.IAEStack;
import appeng.crafting.MECraftingInventory;
import it.unimi.dsi.fastutil.booleans.BooleanList;

/**
 * Plans and commits one exact crafting dispatch batch.
 * <p>
 * The planner exists because AE must use one quantity for energy checks, material extraction, medium submission,
 * diagnostics, and expected outputs. Its API keeps arithmetic and transaction rules independent from mixin injection
 * details so they can be verified directly.
 */
public interface CraftingBatchPlanner {

    /**
     * Describes whether every medium registered for a pattern supports the same batching contract.
     */
    enum MediumStrategy {

        /** All registered media are compatible with exact long-sized batching. */
        BATCH,

        /** At least one registered medium is absent or incompatible, so AE must use its native single-craft path. */
        NATIVE
    }

    /**
     * Identifies the constraint that selected the final craft count, primarily for diagnostics and tests.
     */
    enum LimitingFactor {

        /** The complete medium set does not support batching. */
        MEDIUM,

        /** The task itself has only one craft left. */
        TASK,

        /** An input or output does not satisfy the item-stack batching contract. */
        STACK_CONTRACT,

        /** A checked long multiplication limits the batch. */
        LONG_ARITHMETIC,

        /** The precise, aggregated material inventory limits the batch. */
        MATERIAL,

        /** Available AE energy limits the batch. */
        ENERGY,

        /** No constraint below the requested task count was encountered. */
        REQUESTED
    }

    /**
     * Simulates AE power extraction for a proposed dispatch without changing the energy grid.
     */
    @FunctionalInterface
    interface EnergySimulation {

        /**
         * Returns the power AE would provide for {@code requestedPower} in SIMULATE mode.
         *
         * @param requestedPower exact power required by the proposed expanded input list
         * @return simulated extractable power
         */
        double extract(double requestedPower);
    }

    /**
     * Mutable view of one contiguous diagnostics-session count stored by AE's task progress.
     *
     * @param <S> diagnostics session identifier type
     */
    interface SessionSegment<S> {

        /**
         * Returns the session that owns this contiguous craft-count segment.
         *
         * @return owning diagnostics session
         */
        S getSessionId();

        /**
         * Returns how many crafts remain in this segment.
         *
         * @return positive remaining craft count
         */
        long getRemaining();

        /**
         * Updates the segment after a bulk prefix has been consumed.
         *
         * @param remaining positive unconsumed craft count
         */
        void setRemaining(long remaining);
    }

    /**
     * Immutable result of planning one dispatch.
     */
    class BatchPlan {

        /** Exact number of crafts represented by this dispatch. */
        private final long crafts;

        /** Constraint responsible for the selected count. */
        private final LimitingFactor limitingFactor;

        public BatchPlan(long crafts, LimitingFactor limitingFactor) {
            if (crafts < 1) throw new IllegalArgumentException("A crafting batch must contain at least one craft");
            if (limitingFactor == null) throw new IllegalArgumentException("A crafting batch needs a limiting factor");
            this.crafts = crafts;
            this.limitingFactor = limitingFactor;
        }

        /**
         * Returns the exact multiplier shared by all dispatch stages.
         *
         * @return positive craft count
         */
        public long getCrafts() {
            return crafts;
        }

        /**
         * Returns the constraint that selected {@link #getCrafts()}.
         *
         * @return limiting constraint
         */
        public LimitingFactor getLimitingFactor() {
            return limitingFactor;
        }

        /**
         * Reports whether the dispatch must use the batching path.
         *
         * @return {@code true} only for two or more crafts
         */
        public boolean isBatched() {
            return crafts >= 2;
        }
    }

    /**
     * Describes one expanded input slot for the core quantity planner.
     */
    class BatchRequirement {

        /** Stable identity used to aggregate repeated input slots. */
        private final Object materialKey;

        /** Quantity required by one craft in this slot. */
        private final long amountPerCraft;

        /** Precise quantity currently present in the CPU inventory. */
        private final long availableAmount;

        /** AE amount-per-power-unit used by native energy accounting. */
        private final double amountPerEnergyUnit;

        public BatchRequirement(Object materialKey, long amountPerCraft, long availableAmount,
            double amountPerEnergyUnit) {
            if (materialKey == null) throw new IllegalArgumentException("A batch requirement needs a material key");
            if (amountPerCraft <= 0) throw new IllegalArgumentException("Required material amount must be positive");
            if (availableAmount < 0) throw new IllegalArgumentException("Available material amount cannot be negative");
            if (!Double.isFinite(amountPerEnergyUnit) || amountPerEnergyUnit <= 0) {
                throw new IllegalArgumentException("Amount per energy unit must be finite and positive");
            }
            this.materialKey = materialKey;
            this.amountPerCraft = amountPerCraft;
            this.availableAmount = availableAmount;
            this.amountPerEnergyUnit = amountPerEnergyUnit;
        }

        /**
         * Returns the identity used to combine duplicate expanded slots.
         *
         * @return stable material key
         */
        public Object getMaterialKey() {
            return materialKey;
        }

        /**
         * Returns this slot's one-craft quantity.
         *
         * @return positive required amount
         */
        public long getAmountPerCraft() {
            return amountPerCraft;
        }

        /**
         * Returns the precise CPU inventory amount for this material.
         *
         * @return non-negative available amount
         */
        public long getAvailableAmount() {
            return availableAmount;
        }

        /**
         * Returns AE's amount represented by one unit of crafting energy.
         *
         * @return finite positive conversion value
         */
        public double getAmountPerEnergyUnit() {
            return amountPerEnergyUnit;
        }
    }

    /**
     * Immutable state to apply immediately after a medium accepts a pattern and before AE performs native decrements.
     */
    class CommitResult {

        /** Whether the accepted push represents a batched commit. */
        private final boolean committed;

        /** Task value after pre-deducting all but AE's native one-craft decrement. */
        private final long taskValueBeforeNativeDecrement;

        /** Operation budget after pre-deducting all but AE's native one-operation decrement. */
        private final int remainingOperationsBeforeNativeDecrement;

        public CommitResult(boolean committed, long taskValueBeforeNativeDecrement,
            int remainingOperationsBeforeNativeDecrement) {
            this.committed = committed;
            this.taskValueBeforeNativeDecrement = taskValueBeforeNativeDecrement;
            this.remainingOperationsBeforeNativeDecrement = remainingOperationsBeforeNativeDecrement;
        }

        /**
         * Reports whether state changes must be applied.
         *
         * @return {@code true} only after an accepted batched push
         */
        public boolean isCommitted() {
            return committed;
        }

        /**
         * Returns the task value that leaves one decrement to AE.
         *
         * @return task value before AE's native decrement
         */
        public long getTaskValueBeforeNativeDecrement() {
            return taskValueBeforeNativeDecrement;
        }

        /**
         * Returns the operation budget that leaves one decrement to AE without narrowing a long multiplier to int.
         *
         * @return operation budget before AE's native decrement
         */
        public int getRemainingOperationsBeforeNativeDecrement() {
            return remainingOperationsBeforeNativeDecrement;
        }
    }

    /**
     * Immutable diagnostics allocation for one session represented in a committed batch.
     *
     * @param <S> diagnostics session identifier type
     */
    class SessionAllocation<S> {

        /** Session receiving expected outputs. */
        private final S sessionId;

        /** Number of crafts assigned to the session. */
        private final long crafts;

        public SessionAllocation(S sessionId, long crafts) {
            if (sessionId == null) throw new IllegalArgumentException("A session allocation needs a session id");
            if (crafts < 1) throw new IllegalArgumentException("A session allocation must contain crafts");
            this.sessionId = sessionId;
            this.crafts = crafts;
        }

        /**
         * Returns the diagnostics session receiving this allocation.
         *
         * @return diagnostics session identifier
         */
        public S getSessionId() {
            return sessionId;
        }

        /**
         * Returns the number of crafts represented for this session.
         *
         * @return positive craft count
         */
        public long getCrafts() {
            return crafts;
        }
    }

    /**
     * Result of consuming a batch-sized prefix from AE diagnostics session segments.
     *
     * @param <S> diagnostics session identifier type
     */
    class SessionConsumption<S> {

        /** Session allocations in original task order. */
        private final List<SessionAllocation<S>> allocations;

        /** Total number of session-tagged crafts consumed. */
        private final long consumedCrafts;

        public SessionConsumption(List<SessionAllocation<S>> allocations, long consumedCrafts) {
            this.allocations = allocations;
            this.consumedCrafts = consumedCrafts;
        }

        /**
         * Returns ordered per-session craft allocations.
         *
         * @return immutable allocation list
         */
        public List<SessionAllocation<S>> getAllocations() {
            return allocations;
        }

        /**
         * Returns how many crafts had diagnostics session metadata.
         *
         * @return consumed session-tagged craft count
         */
        public long getConsumedCrafts() {
            return consumedCrafts;
        }

        /**
         * Returns the first valid session, matching AE's single-craft method contract.
         *
         * @return first session or {@code null} when no session segment exists
         */
        public S getFirstSessionId() {
            return allocations.isEmpty() ? null
                : allocations.get(0)
                    .getSessionId();
        }
    }

    /**
     * Resolves the all-or-nothing medium policy for one pattern.
     *
     * @param compatibleMedia one compatibility result for every medium registered for the pattern
     * @return batching only when the list is non-empty and every entry is compatible
     */
    MediumStrategy resolveMediumStrategy(BooleanList compatibleMedia);

    /**
     * Computes the exact dispatch multiplier from task, arithmetic, material, and energy constraints.
     *
     * @param requestedCrafts  remaining task count
     * @param mediumStrategy   all-medium compatibility decision
     * @param expandedInputs   AE's unmodified expanded slot input list for one craft
     * @param condensedOutputs AE's unmodified condensed output array for one craft
     * @param inventory        actual CPU crafting inventory used by the subsequent extraction
     * @param energySimulation SIMULATE-only energy query for proposed expanded quantities
     * @return exact plan; values below two select the fully native path
     */
    BatchPlan plan(long requestedCrafts, MediumStrategy mediumStrategy, List<IAEStack<?>> expandedInputs,
        IAEStack<?>[] condensedOutputs, MECraftingInventory inventory, EnergySimulation energySimulation);

    // For a CPU whose extraction does not simply take the precise stack
    BatchPlan plan(long requestedCrafts, MediumStrategy mediumStrategy, List<IAEStack<?>> expandedInputs,
        IAEStack<?>[] condensedOutputs, ToLongFunction<IAEStack<?>> availability, EnergySimulation energySimulation);

    /**
     * Runs the core batch calculation from explicit quantities. The AE-facing overload converts real stacks and its
     * real CPU inventory to this model, while logic tests can exercise the same algorithm without starting FML.
     *
     * @param requestedCrafts  remaining task count
     * @param mediumStrategy   all-medium compatibility decision
     * @param requirements     expanded input slots, including duplicates
     * @param outputAmounts    positive one-craft quantities for every condensed output
     * @param energySimulation SIMULATE-only energy query
     * @return exact plan; values below two select the native path
     */
    BatchPlan planRequirements(long requestedCrafts, MediumStrategy mediumStrategy, List<BatchRequirement> requirements,
        long[] outputAmounts, EnergySimulation energySimulation);

    /**
     * Returns a slot-preserving, checked-multiply copy of an expanded input list.
     *
     * @param inputs original AE expanded input list
     * @param crafts exact planned multiplier
     * @return independent list whose non-null stacks are independent copies
     * @throws ArithmeticException if the supplied multiplier violates the prior plan
     */
    List<IAEStack<?>> scaleInputs(List<IAEStack<?>> inputs, long crafts);

    /**
     * Returns a checked-multiply copy of every condensed output.
     *
     * @param outputs original AE condensed output array
     * @param crafts  exact committed multiplier
     * @return independent output array and stack copies
     * @throws ArithmeticException if the supplied multiplier violates the prior plan
     */
    IAEStack<?>[] scaleOutputs(IAEStack<?>[] outputs, long crafts);

    /**
     * Performs exact positive long multiplication shared by stack and diagnostics quantities.
     *
     * @param amount one-craft amount
     * @param crafts craft multiplier
     * @return exact product
     * @throws ArithmeticException when the product cannot be represented as a positive long
     */
    long checkedMultiply(long amount, long crafts);

    /**
     * Calculates state to apply at the pushPattern commit boundary while leaving AE's native decrement intact.
     *
     * @param plan                dispatch plan
     * @param pushAccepted        whether the medium accepted the pattern
     * @param taskValue           current task progress value
     * @param remainingOperations current int operation budget
     * @return unchanged state for rejected/native pushes, or pre-decrement state for an accepted batch
     */
    CommitResult commit(BatchPlan plan, boolean pushAccepted, long taskValue, int remainingOperations);

    /**
     * Consumes a batch-sized prefix from diagnostics session segments in O(number of crossed segments).
     *
     * @param segments mutable ordered AE session-count segments
     * @param crafts   committed batch size
     * @param <S>      diagnostics session identifier type
     * @return ordered allocations and the total number of session-tagged crafts consumed
     */
    <S> SessionConsumption<S> consumeSessions(Deque<SessionSegment<S>> segments, long crafts);
}
