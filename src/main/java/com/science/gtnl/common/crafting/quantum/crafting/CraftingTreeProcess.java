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
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

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
    // Use linked hashmap to ensure deterministic ordering of subcrafts
    private final Object2LongLinkedOpenHashMap<CraftingTreeNode> nodes = new Object2LongLinkedOpenHashMap<>();
    boolean possible = true;
    private boolean containerItems;
    private Preview reusablePreview;
    /**
     * If true, we perform this pattern by 1 at the time. This ensures that container items or outputs get reused when
     * possible.
     */
    private boolean limitQty;

    public CraftingTreeProcess(ICraftingService cc, CraftingCalculation job, IPatternDetails details,
        CraftingTreeNode craftingTreeNode) {
        this.parent = craftingTreeNode;
        this.details = details;
        this.job = job;
        this.inputs = details.getInputs();
        this.outputs = details.getOutputs();
        updateLimitQty();

        for (int x = 0; x < this.inputs.length; ++x) {
            var input = this.inputs[x];
            var firstInput = input.possibleInputs()[0];
            this.nodes.put(
                new CraftingTreeNode(cc, job, firstInput.what(), firstInput.amount(), this, x),
                input.getMultiplier());
        }
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
     */
    private void updateLimitQty() {
        // TODO: consider checking substitute inputs as well?
        for (IPatternDetails.IInput input : this.inputs) {
            var primaryInput = input.possibleInputs()[0];
            boolean isAnInput = false;

            if (this.outputs instanceof RandomAccess) {
                for (int i = 0, size = this.outputs.size(); i < size; i++) {
                    if (this.outputs.get(i)
                        .what()
                        .matches(primaryInput)) {
                        isAnInput = true;
                        break;
                    }
                }
            } else {
                for (var output : this.outputs) {
                    if (output.what()
                        .matches(primaryInput)) {
                        isAnInput = true;
                        break;
                    }
                }
            }

            if (isAnInput) {
                this.limitQty = true;
            }

            if (input.getRemainingKey(primaryInput.what()) != null) {
                this.limitQty = this.containerItems = true;
            }
        }
    }

    boolean limitsQuantity() {
        return this.limitQty;
    }

    boolean canReusePreview() {
        return !this.job.isLocalUnitMode() && !this.limitQty && !this.containerItems;
    }

    private static KeyCounter copyPositiveDelta(KeyCounter after, KeyCounter before) {
        var delta = new KeyCounter();
        for (var entry : after) {
            long valueDelta = entry.getLongValue() - before.get(entry.getKey());
            if (valueDelta > 0) {
                delta.add(entry.getKey(), valueDelta);
            }
        }
        return delta;
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
            if (canReusePreview() && this.nodes.size() == 1) {
                var entry = this.nodes.object2LongEntrySet()
                    .first();
                long requiredPerPattern = entry.getLongValue();
                long availableInputs = entry.getKey()
                    .extractAvailableForCrafting(
                        sharedInputs,
                        LongMath.saturatedMultiply(requiredPerPattern, craftableTimes));
                craftableTimes = availableInputs / requiredPerPattern;
                if (craftableTimes > 0) {
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

            for (Object2LongMap.Entry<CraftingTreeNode> entry : this.nodes.object2LongEntrySet()) {
                long requiredPerPattern = entry.getLongValue();
                long availableInputs = entry.getKey()
                    .extractAvailableForCrafting(
                        sharedInputs,
                        LongMath.saturatedMultiply(requiredPerPattern, craftableTimes));
                craftableTimes = Math.min(craftableTimes, availableInputs / requiredPerPattern);
                if (craftableTimes == 0) {
                    return 0;
                }
            }
            if (canReusePreview() && craftableTimes > 0) {
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
                    "process-max-craftable inputs=" + this.nodes.size(),
                    System.nanoTime() - start);
            }
        }
    }

    private void cacheReusablePreview(CraftingSimulationState parent, ChildCraftingSimulationState state, long times,
        long intermediateFinalOutputMarker, KeyCounter recursiveMissingSeedsMarker,
        Set<AEKey> realSeededRecursiveRequestsMarker, Set<AEKey> realRecursiveSeedsMarker,
        Set<AEKey> realSeededRecursiveKeysMarker) {
        var recursiveMissingSeeds = copyPositiveDelta(
            this.job.getRecursiveMissingSeedsMarker(),
            recursiveMissingSeedsMarker);
        var clearedRecursiveMissingSeeds = copyPositiveDelta(
            recursiveMissingSeedsMarker,
            this.job.getRecursiveMissingSeedsMarker());
        var realSeededRecursiveRequests = this.job.getRealSeededRecursiveRequestsMarker();
        realSeededRecursiveRequests.removeAll(realSeededRecursiveRequestsMarker);
        var realRecursiveSeeds = this.job.getRealRecursiveSeedsMarker();
        realRecursiveSeeds.removeAll(realRecursiveSeedsMarker);
        var realSeededRecursiveKeys = this.job.getRealSeededRecursiveKeysMarker();
        realSeededRecursiveKeys.removeAll(realSeededRecursiveKeysMarker);
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

            // request and remove inputs...
            requestInputs(inv, times, containerItems, null);

            // by now we must have succeeded, otherwise an exception would have been thrown by request() above

            // add container items
            if (containerItems != null) {
                for (var stack : containerItems) {
                    inv.insert(stack.getKey(), stack.getLongValue(), Actionable.MODULATE);
                    inv.addStackBytes(stack.getKey(), stack.getLongValue(), 1);
                }
            }

            // add crafting results.
            for (int i = 0, size = this.outputs.size(); i < size; i++) {
                var out = this.outputs.get(i);
                inv.insert(out.what(), LongMath.saturatedMultiply(out.amount(), times), Actionable.MODULATE);
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

    private void requestInputs(CraftingSimulationState inv, long times, @Nullable KeyCounter containerItems,
        @Nullable AEKey skippedInput) throws CraftBranchFailure, InterruptedException {
        for (Object2LongMap.Entry<CraftingTreeNode> entry : this.nodes.object2LongEntrySet()) {
            var node = entry.getKey();
            if (node.getWhat()
                .equals(skippedInput)) {
                continue;
            }
            node.request(inv, LongMath.saturatedMultiply(entry.getLongValue(), times), containerItems);
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
        if (skippedInput == null || this.nodes.size() <= 1) {
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
        for (CraftingTreeNode node : this.nodes.keySet()) {
            if (this.job.isRecursiveFinalOutputInput(node.getWhat())) {
                return node.getWhat();
            }
        }
        return null;
    }

    long getNodeCount() {
        long tot = 0;

        for (CraftingTreeNode node : this.nodes.keySet()) {
            tot += node.getNodeCount();
        }

        return tot;
    }

    int getDepth() {
        int depth = 1;
        for (CraftingTreeNode node : this.nodes.keySet()) {
            depth = Math.max(depth, 1 + node.getDepth());
        }
        return depth;
    }

    long getPatternNodeCount() {
        long total = 0;
        for (CraftingTreeNode node : this.nodes.keySet()) {
            total += node.getPatternNodeCount();
        }
        return total;
    }

    long getOutputCount(AEKey what) {
        long tot = 0;

        if (this.outputs instanceof RandomAccess) {
            for (int i = 0, size = this.outputs.size(); i < size; i++) {
                var output = this.outputs.get(i);
                if (what.matches(output)) {
                    tot += output.amount();
                }
            }
        } else {
            for (var output : this.outputs) {
                if (what.matches(output)) {
                    tot += output.amount();
                }
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
        if (this.outputs instanceof RandomAccess) {
            for (int i = 0, size = this.outputs.size(); i < size; i++) {
                var output = this.outputs.get(i);
                netByKey.add(output.what(), output.amount());
            }
        } else {
            for (var output : this.outputs) {
                netByKey.add(output.what(), output.amount());
            }
        }
        for (Object2LongMap.Entry<CraftingTreeNode> entry : this.nodes.object2LongEntrySet()) {
            var node = entry.getKey();
            netByKey.add(
                node.getWhat(),
                LongMath
                    .saturatedSubtract(0, LongMath.saturatedMultiply(node.getTemplateAmount(), entry.getLongValue())));
        }
    }

    void accumulateInputKeys(Set<AEKey> inputKeys) {
        for (CraftingTreeNode node : this.nodes.keySet()) {
            inputKeys.add(node.getWhat());
        }
    }

    boolean hasMultiplePaths() {
        for (Object2LongMap.Entry<CraftingTreeNode> entry : nodes.object2LongEntrySet()) {
            if (entry.getKey()
                .hasMultiplePaths()) {
                return true;
            }
        }
        return false;
    }

    void resetPossible() {
        this.possible = true;
        this.reusablePreview = null;
        for (CraftingTreeNode node : this.nodes.keySet()) {
            node.resetPossible();
        }
    }

    public IPatternDetails getDetails() {
        return this.details;
    }

    public Object2LongLinkedOpenHashMap<CraftingTreeNode> getNodes() {
        return nodes;
    }

    private record Preview(CraftingSimulationState parent, ChildCraftingSimulationState state, long times,
        long intermediateFinalOutputAmount, KeyCounter recursiveMissingSeeds, KeyCounter clearedRecursiveMissingSeeds,
        Set<AEKey> realSeededRecursiveRequests, Set<AEKey> realRecursiveSeeds, Set<AEKey> realSeededRecursiveKeys) {}
}
