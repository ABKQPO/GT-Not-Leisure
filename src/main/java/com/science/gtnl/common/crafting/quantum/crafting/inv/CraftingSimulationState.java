/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
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

package com.science.gtnl.common.crafting.quantum.crafting.inv;

import java.util.Collections;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterables;
import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.AEKey;
import com.science.gtnl.common.crafting.quantum.api.stacks.GenericStack;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingCalculation;
import com.science.gtnl.common.crafting.quantum.crafting.CraftingPlan;
import com.science.gtnl.common.crafting.quantum.util.LongMath;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public abstract class CraftingSimulationState implements ICraftingSimulationState {

    /**
     * Partial cache of the parent's items, never modified.
     */
    private final KeyCounter unmodifiedCache;
    /**
     * Partial cache of the parent's items, but modifiable. The different between this cache and the unmodified cache is
     * the items that were injected/extracted.
     */
    private final KeyCounter modifiableCache;
    /**
     * Primary keys whose fuzzy variants have been loaded into the local caches.
     */
    private final ReferenceOpenHashSet<Object> loadedFuzzyGroups;
    /**
     * List of items to emit.
     */
    private final KeyCounter emittedItems;
    /**
     * Virtual outputs produced by pseudo patterns during planning. They may satisfy later pseudo-pattern inputs, but do
     * not count as real simulated inventory or as network extraction requirements.
     */
    private final KeyCounter pseudoItems;
    /**
     * Inputs supplied by graph boundaries while previewing a local compatibility unit.
     */
    private final KeyCounter boundaryItems;
    private final Object2LongOpenHashMap<IPatternDetails> crafts = new Object2LongOpenHashMap<>();
    /**
     * Minimum amount of each item that needs to be extracted from the network. This is the maximum of (unmodified -
     * modifiable).
     */
    private final KeyCounter requiredExtract;
    /**
     * Byte count.
     */
    private double bytes = 0;

    protected CraftingSimulationState() {
        this.unmodifiedCache = new KeyCounter();
        this.modifiableCache = new KeyCounter();
        this.loadedFuzzyGroups = new ReferenceOpenHashSet<>();
        this.emittedItems = new KeyCounter();
        this.pseudoItems = new KeyCounter();
        this.boundaryItems = new KeyCounter();
        this.requiredExtract = new KeyCounter();
        this.crafts.defaultReturnValue(0);
    }

    public static CraftingPlan buildCraftingPlan(CraftingSimulationState state, CraftingCalculation calculation,
        long calculatedAmount) {
        return new CraftingPlan(
            new GenericStack(calculation.getOutput(), calculatedAmount),
            (long) Math.ceil(state.bytes),
            calculation.isSimulation(),
            state.requiredExtract,
            state.emittedItems,
            calculation.getMissingItems(),
            state.crafts,
            null);
    }

    protected abstract long simulateExtractParent(AEKey what);

    protected abstract Iterable<AEKey> findFuzzyParent(AEKey input);

    private void cacheFuzzy(AEKey what) {
        if (!loadedFuzzyGroups.add(what.getPrimaryKey())) {
            return;
        }

        for (var keyToCache : findFuzzyParent(what)) {
            var extracted = simulateExtractParent(keyToCache);
            modifiableCache.add(keyToCache, extracted);
            unmodifiedCache.add(keyToCache, extracted);
        }
    }

    @Override
    public void insert(AEKey what, long amount, Actionable mode) {
        cacheFuzzy(what);

        if (mode == Actionable.MODULATE) {
            modifiableCache.add(what, amount);
        }
    }

    private void updateRequiredExtract(AEKey key, long delta) {
        if (delta > 0) {
            long max = Math.max(delta, this.requiredExtract.get(key));
            this.requiredExtract.set(key, max);
        }
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode) {
        long boundaryExtracted = Math.min(this.boundaryItems.get(what), amount);
        if (mode == Actionable.MODULATE && boundaryExtracted > 0) {
            this.boundaryItems.remove(what, boundaryExtracted);
        }
        if (boundaryExtracted == amount) {
            return boundaryExtracted;
        }

        cacheFuzzy(what);

        var cachedAmount = modifiableCache.get(what);
        if (cachedAmount == 0) return boundaryExtracted;

        long extracted = Math.min(cachedAmount, amount - boundaryExtracted);
        if (mode == Actionable.MODULATE) {
            modifiableCache.remove(what, extracted);
        }

        if (mode == Actionable.MODULATE) {
            updateRequiredExtract(what, unmodifiedCache.get(what) - modifiableCache.get(what));
        }

        return boundaryExtracted + extracted;
    }

    @Nullable
    @Override
    public Iterable<AEKey> findFuzzyTemplates(AEKey input) {
        if (input == null) return Collections.emptyList();
        cacheFuzzy(input);

        return Iterables.transform(
            Iterables.filter(modifiableCache.findFuzzy(input, FuzzyMode.IGNORE_ALL), entry -> entry.getLongValue() > 0),
            Object2LongMap.Entry::getKey);
    }

    @Override
    public void emitItems(AEKey what, long amount) {
        this.emittedItems.add(what, amount);
    }

    public void insertPseudo(AEKey what, long amount, Actionable mode) {
        if (mode == Actionable.MODULATE) {
            this.pseudoItems.add(what, amount);
        }
    }

    public void insertBoundary(AEKey what, long amount) {
        if (amount > 0) {
            this.boundaryItems.add(what, amount);
        }
    }

    /**
     * Extracts virtual pseudo outputs. This intentionally does not update requiredExtract because pseudo outputs are
     * never pulled from network storage.
     */
    public long extractPseudo(AEKey what, long amount, Actionable mode) {
        var available = this.pseudoItems.get(what);
        if (available == 0) {
            return 0;
        }

        long extracted = Math.min(available, amount);
        if (mode == Actionable.MODULATE) {
            this.pseudoItems.remove(what, extracted);
        }
        return extracted;
    }

    public double getBytes() {
        return bytes;
    }

    @Override
    public void addBytes(double bytes) {
        this.bytes += bytes;
    }

    @Override
    public void addCrafting(IPatternDetails details, long crafts) {
        this.crafts.addTo(details, crafts);
    }

    public long getOriginalAmount(AEKey what) {
        cacheFuzzy(what);
        return this.unmodifiedCache.get(what);
    }

    public long getRequiredExtractAmount(AEKey what) {
        return this.requiredExtract.get(what);
    }

    public long getAvailableAmount(AEKey what) {
        cacheFuzzy(what);
        return LongMath.saturatedAdd(this.modifiableCache.get(what), this.boundaryItems.get(what));
    }

    public long getAvailableNonProducedAmount(AEKey what) {
        cacheFuzzy(what);
        return Math.max(0, this.unmodifiedCache.get(what) - this.requiredExtract.get(what));
    }

    public long returnExtractedForReserve(AEKey what, long amount) {
        cacheFuzzy(what);
        long returned = Math.min(amount, this.requiredExtract.get(what));
        if (returned <= 0) {
            return 0;
        }

        this.requiredExtract.remove(what, returned);
        this.modifiableCache.add(what, returned);
        return returned;
    }

    /**
     * Amount of the key the planned crafts produce beyond what they consume themselves. A catalyst a pattern returns
     * is not produced: it has to exist before the first craft.
     */
    public long getNetCraftedAmount(AEKey what) {
        long amount = 0;
        for (Object2LongMap.Entry<IPatternDetails> entry : this.crafts.object2LongEntrySet()) {
            final IPatternDetails pattern = entry.getKey();
            long net = 0;
            for (var output : pattern.getOutputs()) {
                if (what.matches(output)) {
                    net = LongMath.saturatedAdd(net, output.amount());
                }
            }
            if (net <= 0) {
                continue;
            }
            for (var input : pattern.getInputs()) {
                var possibleInputs = input.possibleInputs();
                if (possibleInputs.length > 0 && what.matches(possibleInputs[0])) {
                    net = LongMath.saturatedSubtract(
                        net,
                        LongMath.saturatedMultiply(possibleInputs[0].amount(), input.getMultiplier()));
                }
            }
            if (net > 0) {
                amount = LongMath.saturatedAdd(amount, LongMath.saturatedMultiply(net, entry.getLongValue()));
            }
        }
        return amount;
    }

    public void ignore(AEKey stack) {
        cacheFuzzy(stack);
        unmodifiedCache.set(stack, 0);
        modifiableCache.set(stack, 0);
    }

    public void applyDiff(CraftingSimulationState parent) {
        CraftingSimulationDiff.shared(requiredExtract, inventoryDelta(), emittedItems, pseudoItems, crafts, bytes)
            .applyTo(parent);
    }

    public CraftingSimulationDiff freezeDiff() {
        return new CraftingSimulationDiff(requiredExtract, inventoryDelta(), emittedItems, pseudoItems, crafts, bytes);
    }

    private KeyCounter inventoryDelta() {
        var inventoryDelta = new KeyCounter();
        for (var entry : modifiableCache) {
            long delta = entry.getLongValue() - unmodifiedCache.get(entry.getKey());
            if (delta != 0) {
                inventoryDelta.add(entry.getKey(), delta);
            }
        }
        return inventoryDelta;
    }

    void applyDiff(CraftingSimulationDiff diff) {
        // It's important to apply this here to ensure that the extract below doesn't make us count some stacks twice.
        for (var entry : diff.requiredExtract) {
            var key = entry.getKey();
            // To compute the new parent max difference during the processing of the child's queries:
            // Take current parent difference, and add required extract (= max difference observed in the child).
            long delta = this.unmodifiedCache.get(key) - this.modifiableCache.get(key) + entry.getLongValue();
            this.updateRequiredExtract(key, delta);
        }

        for (var entry : diff.inventoryDelta) {
            long sizeDelta = entry.getLongValue();

            if (sizeDelta > 0) {
                this.insert(entry.getKey(), sizeDelta, Actionable.MODULATE);
            } else if (sizeDelta < 0) {
                long newStackSize = -sizeDelta;
                var reallyExtracted = this.extract(entry.getKey(), newStackSize, Actionable.MODULATE);

                if (reallyExtracted != -sizeDelta) {
                    throw new IllegalStateException("Failed to extract from parent. This is a bug!");
                }
            }
        }

        for (var toEmit : diff.emittedItems) {
            this.emitItems(toEmit.getKey(), toEmit.getLongValue());
        }

        for (var pseudoItem : diff.pseudoItems) {
            this.insertPseudo(pseudoItem.getKey(), pseudoItem.getLongValue(), Actionable.MODULATE);
        }

        this.addBytes(diff.bytes);

        for (Object2LongMap.Entry<IPatternDetails> entry : diff.crafts.object2LongEntrySet()) {
            this.addCrafting(entry.getKey(), entry.getLongValue());
        }
    }

    public Object2LongMap<IPatternDetails> getCrafts() {
        return crafts;
    }

    public KeyCounter getExtractedItems() {
        return requiredExtract;
    }

    // 新增 getter：供 MemoRecorder 录制副作用
    public KeyCounter getEmittedItems() {
        return emittedItems;
    }

    public KeyCounter getPseudoItems() {
        return pseudoItems;
    }

    public KeyCounter getModifiableCache() {
        return modifiableCache;
    }
}
