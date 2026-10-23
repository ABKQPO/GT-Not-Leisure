package com.science.gtnl.common.crafting.quantum.crafting.graph;

import it.unimi.dsi.fastutil.objects.Reference2LongMap;

public record SccPlan(Reference2LongMap<CraftingGraphNode> plannedOutputs) {

    public long plannedOutput(CraftingGraphNode node) {
        return this.plannedOutputs.getLong(node);
    }
}
