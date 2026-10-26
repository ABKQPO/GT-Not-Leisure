package com.science.gtnl.common.crafting.quantum.crafting.graph;

import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongMaps;

/**
 * @param takenByCycle for a ring, of each node, how much of its output a node of the ring applied before it takes
 */
public record SccPlan(Reference2LongMap<CraftingGraphNode> plannedOutputs, boolean ring,
    Reference2LongMap<CraftingGraphNode> takenByCycle) {

    public SccPlan(Reference2LongMap<CraftingGraphNode> plannedOutputs) {
        this(plannedOutputs, false, Reference2LongMaps.emptyMap());
    }

    /**
     * A ring planned natively, see DemandPropagation#solveRing.
     */
    public static SccPlan ringPlan(Reference2LongMap<CraftingGraphNode> plannedOutputs,
        Reference2LongMap<CraftingGraphNode> takenByCycle) {
        return new SccPlan(plannedOutputs, true, takenByCycle);
    }

    public long plannedOutput(CraftingGraphNode node) {
        return this.plannedOutputs.getLong(node);
    }

    /**
     * For a ring, how much of the node's output is taken back by the ring before it leaves the craft.
     */
    public long takenByCycle(CraftingGraphNode node) {
        return this.takenByCycle.getLong(node);
    }
}
