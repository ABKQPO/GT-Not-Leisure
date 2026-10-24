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
        // freezeDiff transfers its freshly computed delta; the other counters still belong to the live child.
        this(
            copy(requiredExtract),
            inventoryDelta,
            copy(emittedItems),
            copy(pseudoItems),
            new Object2LongOpenHashMap<>(crafts),
            bytes,
            true);
    }

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
        // No copy of the parent to roll back: once the check above passed, applying can only fail on an internal
        // error, which aborts the whole calculation, and the copy made every applied diff cost as much as the entire
        // simulation state.
        parent.applyDiff(this);
        this.applied = true;
    }

    public boolean canApplyTo(CraftingSimulationState parent) {
        // Each delta entry is a distinct exact key; testing one cannot consume another entry's inventory.
        // A child inventory here copied whole fuzzy groups solely to check amounts that the parent already knows.
        for (var entry : inventoryDelta) {
            if (entry.getLongValue() < 0 && parent.extract(entry.getKey(), -entry.getLongValue(), Actionable.SIMULATE)
                != -entry.getLongValue()) {
                return false;
            }
        }
        return true;
    }

    public double getBytes() {
        return this.bytes;
    }

    /**
     * A memoized snapshot remains frozen; each application receives its own single-use diff.
     */
    public CraftingSimulationDiff copy() {
        // The constructor froze these counters and application only reads them. Only the applied flag is per-use.
        return new CraftingSimulationDiff(
            requiredExtract,
            inventoryDelta,
            emittedItems,
            pseudoItems,
            crafts,
            bytes,
            true);
    }

    private static KeyCounter copy(KeyCounter source) {
        var result = new KeyCounter();
        result.addAll(source);
        return result;
    }
}
