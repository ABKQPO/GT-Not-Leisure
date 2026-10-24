package com.science.gtnl.api.topology;

import net.minecraft.item.ItemStack;

public record TopologyBridge(int id, TopologyLinkKind kind, int from, int to, GridCoord origin, GridCoord farEnd,
    ItemStack icon, ItemStack dimensionIcon, boolean active) {

    public static final int UNRESOLVED = -1;

    public boolean resolved() {
        return to != UNRESOLVED;
    }

    public boolean loop() {
        return from == to;
    }

    /**
     * True when the two ends of this link sit in different dimensions, which is what the emblem badge marks.
     */
    public boolean crossesDimension() {
        return farEnd != null && origin != null && farEnd.dimension() != origin.dimension();
    }
}
