/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package com.science.gtnl.common.crafting.quantum.crafting;

import java.util.List;
import java.util.RandomAccess;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.networking.crafting.ICraftingService;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;
import com.science.gtnl.common.crafting.quantum.crafting.inv.ChildCraftingSimulationState;
import com.science.gtnl.common.crafting.quantum.crafting.inv.CraftingSimulationState;
import com.science.gtnl.common.crafting.quantum.util.LongMath;

import appeng.api.config.Actionable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/**
 * A crafting tree process is what represents a pattern in the crafting process. It has a parent node (its output), and
 * a list of child nodes for its inputs.
 */
public class CraftingTreeProcess {

    final IPatternDetails details;
    private final CraftingTreeNode parent;
    private final CraftingCalculation job;
    private final IPatternDetails.IInput[] inputs;
    private final List<GenericStack> outputs;
    boolean possible = true;
    private boolean containerItems;
    private Preview reusablePreview;
    /**
     * If true, we perform this pattern by 1 at the time. This ensures that container items or outputs get reused when
     * possible.
     */
    private boolean limitQty;
    /*
     * Per input slot: how much of a catalyst one craft gives back, and the container a precise input leaves. Both only
     * have to be there for the first craft of a batch, the later ones take back what the earlier ones returned.
     */
    private final CraftingTreeNode[] slotNodes;
    private long[] catalystReturns;
    private @Nullable AEKey[] slotContainers;
    private boolean batchedSlots;

    public CraftingTreeProcess(ICraftingService cc, CraftingCalculation job, IPatternDetails details,
        CraftingTreeNode craftingTreeNode) {
        this.parent = craftingTreeNode;
        this.details = details;
        this.job = job;
        this.inputs = details.getInputs();
        var patternOutputs = details.getOutputs();
        this.outputs = patternOutputs instanceof RandomAccess ? patternOutputs : new ObjectArrayList<>(patternOutputs);
        this.slotNodes = new CraftingTreeNode[this.inputs.length];

        for (int x = 0; x < this.inputs.length; ++x) {
            var input = this.inputs[x];
            var firstInput = input.possibleInputs()[0];
            var node = new CraftingTreeNode(cc, job, firstInput.what(), firstInput.amount(), this, x);
            this.slotNodes[x] = node;
        }
        updateLimitQty();
    }

    /**
     * @see CraftingTreeNode#notRecursive
     */
    boolean notRecursive(IPatternDetails details) {
        return this.parent == null || this.parent.notRecursive(details);
    }

    /**
     * Check if this pattern has one of its outputs as input. If that's the case, update {@code limitQty} to make sure
     * we simulate this pattern one by one. Also check for container items.
     * <p>
     * The catalyst of a precise input, which the pattern gives back, and the container a precise input leaves are
     * batched instead, see {@link #slotUnits}: one at a time, the calculation costs as much as the requested amount.
     * What the request itself is made of, and the inputs of substituting patterns, whose variant decides what comes
     * back, are still crafted one at a time.
     */
    private void updateLimitQty() {
        var requested = this.parent == null ? null : this.parent.getWhat();
        // With substitutions, the variant a craft takes decides what it leaves
        boolean substitutes = this.details.canSubstitute();
        for (int x = 0; x < this.inputs.length; x++) {
            var input = this.inputs[x];
            var primaryInput = input.possibleInputs()[0];
            var key = primaryInput.what();
            boolean precise = input.possibleInputs().length == 1 && !substitutes;
            if (!precise) {
                // A later alternative may return a container even when the encoded/default input does not (for
                // example, fluid versus a filled bucket). Its actual choice determines the next craft's inventory.
                for (var possible : input.possibleInputs()) {
                    if (input.getRemainingKey(possible.what()) != null) {
                        this.containerItems = true;
                        this.limitQty = true;
                    }
                    if (getOutputCount(possible.what()) > 0) {
                        this.limitQty = true;
                    }
                }
            }
            long returned = getOutputCount(key);
            var container = input.getRemainingKey(key);
            if (container != null) {
                this.containerItems = true;
            }

            if (returned > 0) {
                if (precise && container == null && !key.equals(requested) && countSlots(key) == 1) {
                    if (this.catalystReturns == null) this.catalystReturns = new long[this.inputs.length];
                    this.catalystReturns[x] = returned;
                    this.batchedSlots = true;
                } else {
                    this.limitQty = true;
                }
            } else if (container != null) {
                if (precise) {
                    if (this.slotContainers == null) this.slotContainers = new AEKey[this.inputs.length];
                    this.slotContainers[x] = container;
                    this.batchedSlots = true;
                } else {
                    this.limitQty = true;
                }
            }
        }
    }

    private int countSlots(AEKey key) {
        int count = 0;
        for (var input : this.inputs) {
            if (input.possibleInputs()[0].what()
                .equals(key)) {
                count++;
            }
        }
        return count;
    }

    /**
     * How much of the input of a slot a batch of crafts requests, in units of its node.
     */
    private long slotUnits(int slot, long multiplier, long times) {
        if (this.catalystReturns == null || this.catalystReturns[slot] <= 0 || times <= 0) {
            return LongMath.saturatedMultiply(multiplier, times);
        }
        long unit = this.inputs[slot].possibleInputs()[0].amount();
        long perCraft = LongMath.saturatedMultiply(unit, multiplier);
        long lost = Math.max(0, perCraft - this.catalystReturns[slot]);
        long items = LongMath.saturatedAdd(perCraft, LongMath.saturatedMultiply(lost, times - 1));
        return LongMath.saturatedAdd(items / unit, items % unit == 0 ? 0 : 1);
    }

    /**
     * The most crafts of a batch that the given units of the input of a slot allow.
     */
    private long slotTimes(int slot, long multiplier, long units) {
        if (this.catalystReturns == null || this.catalystReturns[slot] <= 0) {
            return units / multiplier;
        }
        long unit = this.inputs[slot].possibleInputs()[0].amount();
        long perCraft = LongMath.saturatedMultiply(unit, multiplier);
        long items = LongMath.saturatedMultiply(units, unit);
        if (items < perCraft) {
            return 0;
        }
        long lost = perCraft - this.catalystReturns[slot];
        return lost <= 0 ? Long.MAX_VALUE : LongMath.saturatedAdd(1, (items - perCraft) / lost);
    }

    /**
     * The catalyst of a slot left after a batch, from what the batch requested and what its crafts gave back.
     */
    private long catalystReturned(int slot, long times) {
        long unit = this.inputs[slot].possibleInputs()[0].amount();
        long multiplier = this.inputs[slot].getMultiplier();
        long perCraft = LongMath.saturatedMultiply(unit, multiplier);
        long requested = LongMath.saturatedMultiply(slotUnits(slot, multiplier, times), unit);
        long net = LongMath.saturatedMultiply(this.catalystReturns[slot] - perCraft, times);
        return Math.max(0, LongMath.saturatedAdd(requested, net));
    }

    private boolean isCatalyst(AEKey key) {
        if (this.catalystReturns == null) return false;
        for (int x = 0; x < this.inputs.length; x++) {
            if (this.catalystReturns[x] > 0 && this.slotNodes[x].getWhat()
                .equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Puts the containers the earlier crafts of a batch leave for the later ones into the inventory.
     */
    private @Nullable KeyCounter creditContainers(CraftingSimulationState inv, long times) {
        if (times <= 1 || this.slotContainers == null) {
            return null;
        }
        KeyCounter credited = null;
        for (int x = 0; x < this.inputs.length; x++) {
            var container = this.slotContainers[x];
            if (container == null) continue;
            long amount = LongMath.saturatedMultiply(this.inputs[x].getMultiplier(), times - 1);
            inv.insert(container, amount, Actionable.MODULATE);
            if (credited == null) credited = new KeyCounter();
            credited.add(container, amount);
        }
        return credited;
    }

    boolean limitsQuantity() {
        return this.limitQty;
    }

    boolean canReusePreview() {
        return !this.job.isLocalUnitMode() && !this.limitQty && !this.containerItems && !this.batchedSlots;
    }

    long getMaximumCraftableTimes(CraftingSimulationState inv, long maxTimes) throws InterruptedException {
        this.job.handlePausing();

        boolean trackPerformance = this.job.isPerformanceTrackingEnabled();
        long start = trackPerformance ? System.nanoTime() : 0;
        long intermediateFinalOutputMarker = this.job.getIntermediateFinalOutputMarker();
        var recursiveMissingSeedsMarker = this.job.getRecursiveMissingSeedsMarker();
        var realSeededRecursiveRequestsMarker = this.job.getRealSeededRecursiveRequestsMarker();
        var realRecursiveSeedsMarker = this.job.getRealRecursiveSeedsMarker();
        var realSeededRecursiveKeysMarker = this.job.getRealSeededRecursiveKeysMarker();
        var sharedInputs = new ChildCraftingSimulationState(inv);
        this.job.pushProcess(this);
        try {
            this.reusablePreview = null;
            long craftableTimes = maxTimes;
            if (canReusePreview() && this.slotNodes.length == 1) {
                long requiredPerPattern = this.inputs[0].getMultiplier();
                long availableInputs = this.slotNodes[0].extractAvailableForCrafting(
                    sharedInputs,
                    LongMath.saturatedMultiply(requiredPerPattern, craftableTimes));
                craftableTimes = availableInputs / requiredPerPattern;
                // Availability may include an incomplete final craft. Reusing its diff would consume that
                // remainder as well, hiding usable stock from subsequent crafts and overstating missing inputs.
                if (craftableTimes > 0 && availableInputs % requiredPerPattern == 0) {
                    cacheReusablePreview(
                        inv,
                        sharedInputs,
                        craftableTimes,
                        intermediateFinalOutputMarker,
                        recursiveMissingSeedsMarker,
                        realSeededRecursiveRequestsMarker,
                        realRecursiveSeedsMarker,
                        realSeededRecursiveKeysMarker);
                }
                return craftableTimes;
            }

            if (this.batchedSlots) {
                return getBatchCraftableTimes(inv, maxTimes, () -> {
                    this.job.restoreIntermediateFinalOutputMarker(intermediateFinalOutputMarker);
                    this.job.restoreRecursiveMissingSeedsMarker(recursiveMissingSeedsMarker);
                    this.job.restoreRealSeededRecursiveRequestsMarker(realSeededRecursiveRequestsMarker);
                    this.job.restoreRealRecursiveSeedsMarker(realRecursiveSeedsMarker);
                    this.job.restoreRealSeededRecursiveKeysMarker(realSeededRecursiveKeysMarker);
                });
            }
            for (int slot = 0; slot < this.slotNodes.length; slot++) {
                long requiredPerPattern = this.inputs[slot].getMultiplier();
                long availableInputs = this.slotNodes[slot].extractAvailableForCrafting(
                    sharedInputs,
                    LongMath.saturatedMultiply(requiredPerPattern, craftableTimes));
                craftableTimes = Math.min(craftableTimes, availableInputs / requiredPerPattern);
                if (craftableTimes == 0) {
                    return 0;
                }
            }
            // A later input may have reduced the batch after earlier inputs were already taken for maxTimes.
            // That preview would consume too much; request the smaller batch afresh instead of applying its diff.
            if (canReusePreview() && craftableTimes > 0 && craftableTimes == maxTimes) {
                cacheReusablePreview(
                    inv,
                    sharedInputs,
                    craftableTimes,
                    intermediateFinalOutputMarker,
                    recursiveMissingSeedsMarker,
                    realSeededRecursiveRequestsMarker,
                    realRecursiveSeedsMarker,
                    realSeededRecursiveKeysMarker);
            }
            return craftableTimes;
        } finally {
            this.job.popProcess();
            this.job.restoreIntermediateFinalOutputMarker(intermediateFinalOutputMarker);
            this.job.restoreRecursiveMissingSeedsMarker(recursiveMissingSeedsMarker);
            this.job.restoreRealSeededRecursiveRequestsMarker(realSeededRecursiveRequestsMarker);
            this.job.restoreRealRecursiveSeedsMarker(realRecursiveSeedsMarker);
            this.job.restoreRealSeededRecursiveKeysMarker(realSeededRecursiveKeysMarker);
            if (trackPerformance) {
                this.job.recordPerformanceStage(
                    "process-max-craftable inputs=" + this.slotNodes.length,
                    System.nanoTime() - start);
            }
        }
    }

    /**
     * The credited containers depend on the batch size, so a smaller batch is checked again until it holds.
     */
    private long getBatchCraftableTimes(CraftingSimulationState inv, long maxTimes, Runnable restoreMarkers)
        throws InterruptedException {
        long times = maxTimes;
        for (int attempt = 0; attempt < 8; attempt++) {
            long craftable = batchCraftableTimes(inv, times);
            if (craftable >= times || craftable == 0) {
                return Math.min(craftable, times);
            }
            restoreMarkers.run();
            times = craftable;
        }
        return Math.min(1, batchCraftableTimes(inv, 1));
    }

    private long batchCraftableTimes(CraftingSimulationState inv, long times) throws InterruptedException {
        var sharedInputs = new ChildCraftingSimulationState(inv);
        creditContainers(sharedInputs, times);
        long craftable = times;
        for (int x = 0; x < this.slotNodes.length; x++) {
            long multiplier = this.inputs[x].getMultiplier();
            long available = this.slotNodes[x]
                .extractAvailableForCrafting(sharedInputs, slotUnits(x, multiplier, craftable));
            craftable = Math.min(craftable, slotTimes(x, multiplier, available));
            if (craftable == 0) {
                return 0;
            }
        }
        return craftable;
    }

    private void cacheReusablePreview(CraftingSimulationState parent, ChildCraftingSimulationState state, long times,
        long intermediateFinalOutputMarker, KeyCounter recursiveMissingSeedsMarker,
        Set<AEKey> realSeededRecursiveRequestsMarker, Set<AEKey> realRecursiveSeedsMarker,
        Set<AEKey> realSeededRecursiveKeysMarker) {
        var recursiveMissingSeeds = this.job.getAddedRecursiveMissingSeeds(recursiveMissingSeedsMarker);
        var clearedRecursiveMissingSeeds = this.job.getClearedRecursiveMissingSeeds(recursiveMissingSeedsMarker);
        var realSeededRecursiveRequests = this.job
            .getRealSeededRecursiveRequestsDelta(realSeededRecursiveRequestsMarker);
        var realRecursiveSeeds = this.job.getRealRecursiveSeedsDelta(realRecursiveSeedsMarker);
        var realSeededRecursiveKeys = this.job.getRealSeededRecursiveKeysDelta(realSeededRecursiveKeysMarker);
        this.reusablePreview = new Preview(
            parent,
            state,
            times,
            this.job.getIntermediateFinalOutputMarker() - intermediateFinalOutputMarker,
            recursiveMissingSeeds,
            clearedRecursiveMissingSeeds,
            realSeededRecursiveRequests,
            realRecursiveSeeds,
            realSeededRecursiveKeys);
    }

    boolean applyReusablePreview(CraftingSimulationState inv, long times) {
        var preview = this.reusablePreview;
        if (preview == null || preview.parent() != inv || preview.times() != times) {
            return false;
        }

        for (int i = 0, size = this.outputs.size(); i < size; i++) {
            var out = this.outputs.get(i);
            preview.state()
                .insert(out.what(), LongMath.saturatedMultiply(out.amount(), times), Actionable.MODULATE);
        }

        preview.state()
            .addCrafting(details, times);
        preview.state()
            .addBytes(times);
        preview.state()
            .applyDiff(inv);
        if (preview.intermediateFinalOutputAmount() > 0) {
            this.job.addIntermediateFinalOutput(preview.intermediateFinalOutputAmount());
        }
        this.job
            .applyRecursiveMissingSeedPreview(preview.clearedRecursiveMissingSeeds(), preview.recursiveMissingSeeds());
        this.job.addRealSeededRecursiveRequests(preview.realSeededRecursiveRequests());
        this.job.addRealRecursiveSeeds(preview.realRecursiveSeeds());
        this.job.addRealSeededRecursiveKeys(preview.realSeededRecursiveKeys());
        this.reusablePreview = null;
        return true;
    }

    long getReusablePreviewRecursiveMissingSeedAmount(AEKey what) {
        return this.reusablePreview == null ? 0
            : this.reusablePreview.recursiveMissingSeeds()
                .get(what);
    }

    void applyReusablePreviewRecursiveMissingSeedsOnly() {
        var preview = this.reusablePreview;
        if (preview == null) {
            return;
        }

        this.job
            .applyRecursiveMissingSeedPreview(preview.clearedRecursiveMissingSeeds(), preview.recursiveMissingSeeds());
        this.reusablePreview = null;
    }

    void request(CraftingSimulationState inv, long times) throws CraftBranchFailure, InterruptedException {
        this.job.handlePausing();

        this.job.pushProcess(this);
        try {
            var containerItems = this.containerItems ? new KeyCounter() : null;
            long intermediateFinalOutputMarker = this.job.getIntermediateFinalOutputMarker();
            var credited = creditContainers(inv, times);

            // request and remove inputs...
            requestInputs(inv, times, containerItems, null);

            // by now we must have succeeded, otherwise an exception would have been thrown by request() above

            // add container items, except those already credited to the batch
            if (containerItems != null) {
                containerItems.forEach((entryKey, entryAmount) -> {
                    long amount = entryAmount;
                    if (credited != null) {
                        amount -= credited.get(entryKey);
                    }
                    if (amount > 0) {
                        inv.insert(entryKey, amount, Actionable.MODULATE);
                    }
                    inv.addStackBytes(entryKey, entryAmount, 1);
                });
            }
            if (credited != null) {
                withdrawUnreturnedCredit(inv, credited, containerItems);
            }

            // add crafting results.
            for (int outputIndex = 0; outputIndex < this.outputs.size(); outputIndex++) {
                var out = this.outputs.get(outputIndex);
                if (!isCatalyst(out.what())) {
                    inv.insert(out.what(), LongMath.saturatedMultiply(out.amount(), times), Actionable.MODULATE);
                }
            }
            for (int x = 0; x < this.inputs.length; x++) {
                if (this.catalystReturns != null && this.catalystReturns[x] > 0) {
                    inv.insert(this.slotNodes[x].getWhat(), catalystReturned(x, times), Actionable.MODULATE);
                }
            }

            inv.addCrafting(details, times);
            inv.addBytes(times);
            requestIntermediateFinalOutputReplacements(
                inv,
                this.job.getIntermediateFinalOutputMarker() - intermediateFinalOutputMarker);
        } finally {
            this.job.popProcess();
        }
    }

    /**
     * The crafts may have taken other variants than the credited containers were left for. What they did not leave is
     * taken back, and what the crafts below already used of it is missing.
     */
    private void withdrawUnreturnedCredit(CraftingSimulationState inv, KeyCounter credited,
        @Nullable KeyCounter containerItems) {
        credited.forEach((entryKey, entryAmount) -> {
            long returned = containerItems == null ? 0 : containerItems.get(entryKey);
            long excess = entryAmount - returned;
            if (excess <= 0) return;
            long withdrawn = inv.extract(entryKey, excess, Actionable.MODULATE);
            if (withdrawn < excess) {
                this.job.addMissing(entryKey, excess - withdrawn);
            }
        });
    }

    private void requestInputs(CraftingSimulationState inv, long times, @Nullable KeyCounter containerItems,
        @Nullable AEKey skippedInput) throws CraftBranchFailure, InterruptedException {
        for (int x = 0; x < this.slotNodes.length; x++) {
            var node = this.slotNodes[x];
            if (node.getWhat()
                .equals(skippedInput)) {
                continue;
            }
            long multiplier = this.inputs[x].getMultiplier();
            long units = skippedInput == null ? slotUnits(x, multiplier, times)
                : LongMath.saturatedMultiply(multiplier, times);
            node.request(inv, units, containerItems);
        }
    }

    private void requestIntermediateFinalOutputReplacements(CraftingSimulationState inv, long intermediateAmount)
        throws CraftBranchFailure, InterruptedException {
        long outputCount = getOutputCount(this.job.getOutput());
        if (intermediateAmount <= 0 || outputCount <= 0
            || !this.parent.getWhat()
                .equals(this.job.getOutput())) {
            return;
        }
        var skippedInput = getSkippedRecursiveFinalOutputInput();
        if (skippedInput == null || this.slotNodes.length <= 1) {
            return;
        }

        long extraTimes = (intermediateAmount + outputCount - 1) / outputCount;
        long marker = this.job.getIntermediateFinalOutputMarker();
        requestInputs(inv, extraTimes, null, skippedInput);
        this.job.restoreIntermediateFinalOutputMarker(marker);
        inv.addCrafting(details, extraTimes);
        inv.addBytes(extraTimes);
    }

    @Nullable
    private AEKey getSkippedRecursiveFinalOutputInput() {
        for (CraftingTreeNode node : this.slotNodes) {
            if (this.job.isRecursiveFinalOutputInput(node.getWhat())) {
                return node.getWhat();
            }
        }
        return null;
    }

    long getNodeCount() {
        long tot = 0;

        for (CraftingTreeNode node : this.slotNodes) {
            tot += node.getNodeCount();
        }

        return tot;
    }

    int getDepth() {
        int depth = 1;
        for (CraftingTreeNode node : this.slotNodes) {
            depth = Math.max(depth, 1 + node.getDepth());
        }
        return depth;
    }

    long getPatternNodeCount() {
        long total = 0;
        for (CraftingTreeNode node : this.slotNodes) {
            total += node.getPatternNodeCount();
        }
        return total;
    }

    long getOutputCount(AEKey what) {
        long tot = 0;

        for (int i = 0, size = this.outputs.size(); i < size; i++) {
            var output = this.outputs.get(i);
            if (what.matches(output)) {
                tot += output.amount();
            }
        }

        return tot;
    }

    long getEffectiveOutputCount(AEKey what) {
        long output = getOutputCount(what);
        long input = getInputCount(what);
        if (input <= 0 || output <= input) {
            return output;
        }
        return output - input;
    }

    long getInputCount(AEKey what) {
        long total = 0;

        for (var input : this.inputs) {
            for (var possibleInput : input.possibleInputs()) {
                if (what.matches(possibleInput)) {
                    total = LongMath
                        .saturatedAdd(total, LongMath.saturatedMultiply(possibleInput.amount(), input.getMultiplier()));
                    break;
                }
            }
        }

        return total;
    }

    void accumulateNet(KeyCounter netByKey) {
        accumulateNet(netByKey, 1);
    }

    void accumulateNet(KeyCounter netByKey, long times) {
        for (int i = 0, size = this.outputs.size(); i < size; i++) {
            var output = this.outputs.get(i);
            netByKey.add(output.what(), LongMath.saturatedMultiply(output.amount(), times));
        }
        for (int slot = 0; slot < this.slotNodes.length; slot++) {
            var node = this.slotNodes[slot];
            netByKey.add(
                node.getWhat(),
                LongMath.saturatedSubtract(
                    0,
                    LongMath.saturatedMultiply(
                        node.getTemplateAmount(),
                        LongMath.saturatedMultiply(this.inputs[slot].getMultiplier(), times))));
        }
    }

    void accumulateInputKeys(Set<AEKey> inputKeys) {
        for (CraftingTreeNode node : this.slotNodes) {
            inputKeys.add(node.getWhat());
        }
    }

    boolean hasMultiplePaths() {
        for (var node : this.slotNodes) {
            if (node.hasMultiplePaths()) {
                return true;
            }
        }
        return false;
    }

    void resetPossible() {
        this.possible = true;
        this.reusablePreview = null;
        for (CraftingTreeNode node : this.slotNodes) {
            node.resetPossible();
        }
    }

    public IPatternDetails getDetails() {
        return this.details;
    }

    /**
     * The key of the node this process crafts for, or null if it has none.
     */
    @Nullable
    AEKey getRequestedKey() {
        return this.parent == null ? null : this.parent.getWhat();
    }

    private record Preview(CraftingSimulationState parent, ChildCraftingSimulationState state, long times,
        long intermediateFinalOutputAmount, KeyCounter recursiveMissingSeeds, KeyCounter clearedRecursiveMissingSeeds,
        Set<AEKey> realSeededRecursiveRequests, Set<AEKey> realRecursiveSeeds, Set<AEKey> realSeededRecursiveKeys) {}
}
