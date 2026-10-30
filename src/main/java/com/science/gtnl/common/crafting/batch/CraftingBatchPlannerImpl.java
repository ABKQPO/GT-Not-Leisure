package com.science.gtnl.common.crafting.batch;

import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToLongFunction;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.crafting.MECraftingInventory;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * Exact AE crafting batch planner used by the CPU cluster mixin.
 */
public class CraftingBatchPlannerImpl implements CraftingBatchPlanner {

    private static final double AE_POWER_TOLERANCE = 0.01D;

    @Override
    public MediumStrategy resolveMediumStrategy(BooleanList compatibleMedia) {
        if (compatibleMedia == null || compatibleMedia.isEmpty()) return MediumStrategy.NATIVE;
        for (int i = 0; i < compatibleMedia.size(); i++) {
            if (!compatibleMedia.getBoolean(i)) return MediumStrategy.NATIVE;
        }
        return MediumStrategy.BATCH;
    }

    @Override
    public BatchPlan plan(long requestedCrafts, MediumStrategy mediumStrategy, List<IAEStack<?>> expandedInputs,
        IAEStack<?>[] condensedOutputs, MECraftingInventory inventory, EnergySimulation energySimulation) {
        Objects.requireNonNull(mediumStrategy, "mediumStrategy");
        Objects.requireNonNull(expandedInputs, "expandedInputs");
        Objects.requireNonNull(condensedOutputs, "condensedOutputs");
        Objects.requireNonNull(inventory, "inventory");
        return plan(requestedCrafts, mediumStrategy, expandedInputs, condensedOutputs, input -> {
            IAEStack<?> available = getAvailableItem(
                inventory,
                input.copy()
                    .setStackSize(1));
            return available == null ? 0 : Math.max(0, available.getStackSize());
        }, energySimulation);
    }

    @Override
    public BatchPlan plan(long requestedCrafts, MediumStrategy mediumStrategy, List<IAEStack<?>> expandedInputs,
        IAEStack<?>[] condensedOutputs, ToLongFunction<IAEStack<?>> availability, EnergySimulation energySimulation) {
        Objects.requireNonNull(mediumStrategy, "mediumStrategy");
        Objects.requireNonNull(expandedInputs, "expandedInputs");
        Objects.requireNonNull(condensedOutputs, "condensedOutputs");
        Objects.requireNonNull(availability, "availability");
        Objects.requireNonNull(energySimulation, "energySimulation");

        if (!hasValidItemStacks(expandedInputs) || !hasValidItemStacks(condensedOutputs)) {
            return new BatchPlan(1, LimitingFactor.STACK_CONTRACT);
        }

        List<BatchRequirement> requirements = new ObjectArrayList<>();
        for (IAEStack<?> input : expandedInputs) {
            if (input == null) continue;
            IAEStack<?> key = input.copy()
                .setStackSize(1);
            requirements.add(
                new BatchRequirement(
                    key,
                    input.getStackSize(),
                    Math.max(0, availability.applyAsLong(input)),
                    input.getAmountPerUnit()));
        }
        long[] outputAmounts = new long[condensedOutputs.length];
        for (int i = 0; i < condensedOutputs.length; i++) {
            outputAmounts[i] = condensedOutputs[i].getStackSize();
        }
        return planRequirements(requestedCrafts, mediumStrategy, requirements, outputAmounts, energySimulation);
    }

    @Override
    public BatchPlan planRequirements(long requestedCrafts, MediumStrategy mediumStrategy,
        List<BatchRequirement> requirements, long[] outputAmounts, EnergySimulation energySimulation) {
        Objects.requireNonNull(mediumStrategy, "mediumStrategy");
        Objects.requireNonNull(requirements, "requirements");
        Objects.requireNonNull(outputAmounts, "outputAmounts");
        Objects.requireNonNull(energySimulation, "energySimulation");

        if (mediumStrategy != MediumStrategy.BATCH) return new BatchPlan(1, LimitingFactor.MEDIUM);
        if (requestedCrafts < 2) return new BatchPlan(1, LimitingFactor.TASK);
        if (requirements.isEmpty() || outputAmounts.length == 0) {
            return new BatchPlan(1, LimitingFactor.STACK_CONTRACT);
        }

        Map<Object, AggregatedDemand> aggregatedDemand;
        try {
            aggregatedDemand = aggregateRequirements(requirements);
        } catch (ArithmeticException exception) {
            return new BatchPlan(1, LimitingFactor.LONG_ARITHMETIC);
        }
        for (long outputAmount : outputAmounts) {
            if (outputAmount <= 0) return new BatchPlan(1, LimitingFactor.STACK_CONTRACT);
        }

        long crafts = requestedCrafts;
        LimitingFactor limitingFactor = LimitingFactor.REQUESTED;

        long arithmeticLimit = arithmeticLimit(aggregatedDemand, outputAmounts);
        if (arithmeticLimit < crafts) {
            crafts = arithmeticLimit;
            limitingFactor = LimitingFactor.LONG_ARITHMETIC;
        }
        if (crafts < 2) return new BatchPlan(1, limitingFactor);

        long materialLimit = materialLimit(aggregatedDemand);
        if (materialLimit < crafts) {
            crafts = materialLimit;
            limitingFactor = LimitingFactor.MATERIAL;
        }
        if (crafts < 2) return new BatchPlan(1, limitingFactor);

        long energyLimit = energyLimit(requirements, crafts, energySimulation);
        if (energyLimit < crafts) {
            crafts = energyLimit;
            limitingFactor = LimitingFactor.ENERGY;
        }
        return new BatchPlan(Math.max(1, crafts), limitingFactor);
    }

    @Override
    public List<IAEStack<?>> scaleInputs(List<IAEStack<?>> inputs, long crafts) {
        Objects.requireNonNull(inputs, "inputs");
        List<IAEStack<?>> scaled = new ObjectArrayList<>(inputs.size());
        for (IAEStack<?> input : inputs) {
            scaled.add(input == null ? null : scaleStack(input, crafts));
        }
        return scaled;
    }

    @Override
    public IAEStack<?>[] scaleOutputs(IAEStack<?>[] outputs, long crafts) {
        Objects.requireNonNull(outputs, "outputs");
        IAEStack<?>[] scaled = new IAEStack<?>[outputs.length];
        for (int i = 0; i < outputs.length; i++) {
            scaled[i] = outputs[i] == null ? null : scaleStack(outputs[i], crafts);
        }
        return scaled;
    }

    @Override
    public long checkedMultiply(long amount, long crafts) {
        if (amount <= 0) throw new ArithmeticException("One-craft stack amount must be positive: " + amount);
        if (crafts <= 0) throw new ArithmeticException("Craft multiplier must be positive: " + crafts);
        return Math.multiplyExact(amount, crafts);
    }

    @Override
    public CommitResult commit(BatchPlan plan, boolean pushAccepted, long taskValue, int remainingOperations) {
        Objects.requireNonNull(plan, "plan");
        if (!pushAccepted || !plan.isBatched()) {
            return new CommitResult(false, taskValue, remainingOperations);
        }

        long crafts = plan.getCrafts();
        if (taskValue < crafts) {
            throw new IllegalStateException(
                "Planned crafts exceed current task progress: crafts=" + crafts + ", task=" + taskValue);
        }
        if (remainingOperations <= 0) {
            throw new IllegalStateException("Cannot commit a craft with no remaining operation budget");
        }

        long taskBeforeNativeDecrement = Math.subtractExact(taskValue, crafts - 1);
        int operationsBeforeNativeDecrement;
        if (crafts >= remainingOperations) {
            operationsBeforeNativeDecrement = 1;
        } else {
            operationsBeforeNativeDecrement = remainingOperations - (int) (crafts - 1);
        }
        return new CommitResult(true, taskBeforeNativeDecrement, operationsBeforeNativeDecrement);
    }

    @Override
    public <S> SessionConsumption<S> consumeSessions(Deque<SessionSegment<S>> segments, long crafts) {
        Objects.requireNonNull(segments, "segments");
        if (crafts <= 0) throw new IllegalArgumentException("Consumed craft count must be positive");

        long remainingCrafts = crafts;
        long consumedCrafts = 0;
        List<SessionAllocation<S>> allocations = new ObjectArrayList<>();
        while (remainingCrafts > 0 && !segments.isEmpty()) {
            SessionSegment<S> segment = segments.peekFirst();
            if (segment == null || segment.getSessionId() == null || segment.getRemaining() <= 0) {
                throw new IllegalStateException("Invalid diagnostics session segment");
            }

            long consumedFromSegment = Math.min(remainingCrafts, segment.getRemaining());
            addAllocation(allocations, segment.getSessionId(), consumedFromSegment);
            remainingCrafts -= consumedFromSegment;
            consumedCrafts = Math.addExact(consumedCrafts, consumedFromSegment);

            long segmentRemainder = segment.getRemaining() - consumedFromSegment;
            if (segmentRemainder == 0) {
                segments.removeFirst();
            } else {
                segment.setRemaining(segmentRemainder);
            }
        }

        return new SessionConsumption<>(Collections.unmodifiableList(allocations), consumedCrafts);
    }

    private static boolean hasValidItemStacks(Iterable<IAEStack<?>> stacks) {
        for (IAEStack<?> stack : stacks) {
            if (stack != null && (!(stack instanceof IAEItemStack) || stack.getStackSize() <= 0)) return false;
        }
        return true;
    }

    private static boolean hasValidItemStacks(IAEStack<?>[] stacks) {
        if (stacks.length == 0) return false;
        for (IAEStack<?> stack : stacks) {
            if (!(stack instanceof IAEItemStack) || stack.getStackSize() <= 0) return false;
        }
        return true;
    }

    private static long arithmeticLimit(Map<Object, AggregatedDemand> requirements, long[] outputAmounts) {
        long limit = Long.MAX_VALUE;
        for (AggregatedDemand requirement : requirements.values()) {
            limit = Math.min(limit, Long.MAX_VALUE / requirement.amountPerCraft);
        }
        for (long outputAmount : outputAmounts) {
            limit = Math.min(limit, Long.MAX_VALUE / outputAmount);
        }
        return limit;
    }

    private static long materialLimit(Map<Object, AggregatedDemand> requirements) {
        long limit = Long.MAX_VALUE;
        for (AggregatedDemand requirement : requirements.values()) {
            limit = Math.min(limit, requirement.availableAmount / requirement.amountPerCraft);
        }
        return limit;
    }

    private static IAEStack<?> getAvailableItem(MECraftingInventory inventory, IAEStack<?> key) {
        return inventory.getAvailableItem(key);
    }

    private static long energyLimit(List<BatchRequirement> requirements, long upperBound,
        EnergySimulation energySimulation) {
        double energyPerCraft = 0;
        for (BatchRequirement requirement : requirements) {
            energyPerCraft += requirement.getAmountPerCraft() / requirement.getAmountPerEnergyUnit();
        }
        if (!Double.isFinite(energyPerCraft) || energyPerCraft < 0) return 1;
        if (energyPerCraft == 0) return upperBound;
        if (hasEnergy(energyPerCraft, upperBound, energySimulation)) return upperBound;

        long low = 1;
        long high = upperBound - 1;
        long best = 1;
        while (low <= high) {
            long middle = low + ((high - low) >>> 1);
            if (hasEnergy(energyPerCraft, middle, energySimulation)) {
                best = middle;
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return best;
    }

    private static boolean hasEnergy(double energyPerCraft, long crafts, EnergySimulation energySimulation) {
        double required = energyPerCraft * crafts;
        if (!Double.isFinite(required)) return false;
        double available = energySimulation.extract(required);
        return Double.isFinite(available) && available >= required - AE_POWER_TOLERANCE;
    }

    private IAEStack<?> scaleStack(IAEStack<?> stack, long crafts) {
        IAEStack<?> copy = stack.copy();
        copy.setStackSize(checkedMultiply(stack.getStackSize(), crafts));
        return copy;
    }

    private static <S> void addAllocation(List<SessionAllocation<S>> allocations, S sessionId, long crafts) {
        if (!allocations.isEmpty()) {
            SessionAllocation<S> last = allocations.get(allocations.size() - 1);
            if (Objects.equals(last.getSessionId(), sessionId)) {
                allocations.set(
                    allocations.size() - 1,
                    new SessionAllocation<>(sessionId, Math.addExact(last.getCrafts(), crafts)));
                return;
            }
        }
        allocations.add(new SessionAllocation<>(sessionId, crafts));
    }

    private static Map<Object, AggregatedDemand> aggregateRequirements(List<BatchRequirement> requirements) {
        Map<Object, AggregatedDemand> aggregated = new Object2ObjectLinkedOpenHashMap<>();
        for (BatchRequirement requirement : requirements) {
            AggregatedDemand demand = aggregated.get(requirement.getMaterialKey());
            if (demand == null) {
                aggregated.put(
                    requirement.getMaterialKey(),
                    new AggregatedDemand(requirement.getAmountPerCraft(), requirement.getAvailableAmount()));
            } else {
                demand.amountPerCraft = Math.addExact(demand.amountPerCraft, requirement.getAmountPerCraft());
                demand.availableAmount = Math.min(demand.availableAmount, requirement.getAvailableAmount());
            }
        }
        return aggregated;
    }

    private static class AggregatedDemand {

        private long amountPerCraft;
        private long availableAmount;

        private AggregatedDemand(long amountPerCraft, long availableAmount) {
            this.amountPerCraft = amountPerCraft;
            this.availableAmount = availableAmount;
        }
    }
}
