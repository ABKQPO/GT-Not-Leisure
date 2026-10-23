package com.science.gtnl.client.quantum;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.crafting.cycle.RecursivePatternFinder.Cycle;

import appeng.api.storage.data.IAEStack;

/**
 * A crafting tree that marks the crafting cycles found for its request and can show one of its cycles as a ring, see
 * {@link CraftingRingView}.
 */
public interface ICraftingTreeCycles {

    void gtnl$setCycles(CraftingCycleIndex index);

    @Nullable
    CraftingRingView gtnl$ringView();

    /**
     * Shows a cycle found in the patterns as a ring, going through the item if it can.
     *
     * @return whether it could be made of the patterns
     */
    boolean gtnl$openCycle(Cycle cycle, @Nullable IAEStack<?> start);

    void gtnl$closeRingView();

    /**
     * The cycle patterns of what is under the mouse: a craft, a folded cycle, or in a ring the whole cycle.
     */
    List<CraftingCycleIndex.Match> gtnl$hoveredPatterns();
}
