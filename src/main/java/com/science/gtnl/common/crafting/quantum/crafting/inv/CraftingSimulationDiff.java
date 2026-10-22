package com.science.gtnl.common.crafting.quantum.crafting.inv;

import com.science.gtnl.common.crafting.quantum.api.crafting.IPatternDetails;
import com.science.gtnl.common.crafting.quantum.api.stacks.KeyCounter;

import appeng.api.config.Actionable;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

/**
 * Frozen, single-use changes produced by an isolated crafting simulation.
 */
public final class CraftingSimulationDiff {

    final KeyCounter requiredExtract;
    final KeyCounter inventoryDelta;
    final KeyCounter emittedItems;
    final KeyCounter pseudoItems;
    final Object2LongMap<IPatternDetails> crafts;
    final double bytes;
    private boolean applied;

    CraftingSimulationDiff(KeyCounter requiredExtract, KeyCounter inventoryDelta, KeyCounter emittedItems,
        KeyCounter pseudoItems, Object2LongMap<IPatternDetails> crafts, double bytes) {
        this(
            copy(requiredExtract),
            inventoryDelta,
            copy(emittedItems),
            copy(pseudoItems),
            new Object2LongOpenHashMap<>(crafts),
            bytes,
            true);
    }

    // Without copies, for a diff applied right away
    private CraftingSimulationDiff(KeyCounter requiredExtract, KeyCounter inventoryDelta, KeyCounter emittedItems,
        KeyCounter pseudoItems, Object2LongMap<IPatternDetails> crafts, double bytes, boolean ignored) {
        this.requiredExtract = requiredExtract;
        this.inventoryDelta = inventoryDelta;
        this.emittedItems = emittedItems;
        this.pseudoItems = pseudoItems;
        this.crafts = crafts;
        this.bytes = bytes;
    }

    static CraftingSimulationDiff shared(KeyCounter requiredExtract, KeyCounter inventoryDelta, KeyCounter emittedItems,
        KeyCounter pseudoItems, Object2LongMap<IPatternDetails> crafts, double bytes) {
        return new CraftingSimulationDiff(
            requiredExtract,
            inventoryDelta,
            emittedItems,
            pseudoItems,
            crafts,
            bytes,
            true);
    }

    public void applyTo(CraftingSimulationState parent) {
        if (this.applied) {
            throw new IllegalStateException("Crafting simulation diff was already applied");
        }
        if (!canApplyTo(parent)) {
            throw new IllegalStateException("Crafting simulation diff cannot be applied to parent");
        }
        // No rollback copy: past the check above, only an internal error aborting the calculation can fail here
        parent.applyDiff(this);
        this.applied = true;
    }

    public boolean canApplyTo(CraftingSimulationState parent) {
        var test = new ChildCraftingSimulationState(parent);
        for (var entry : inventoryDelta) {
            if (entry.getLongValue() < 0
                && test.extract(entry.getKey(), -entry.getLongValue(), Actionable.MODULATE) != -entry.getLongValue()) {
                return false;
            }
        }
        return true;
    }

    public double getBytes() {
        return this.bytes;
    }

    private static KeyCounter copy(KeyCounter source) {
        var result = new KeyCounter();
        result.addAll(source);
        return result;
    }
}
