package com.science.gtnl.api.topology;

import net.minecraft.item.ItemStack;

public record TopologyBridge(int id, TopologyLinkKind kind, int from, int to, GridCoord origin, GridCoord farEnd,
    ItemStack icon, boolean active) {

    public static final int UNRESOLVED = -1;

    public boolean resolved() {
        return to != UNRESOLVED;
    }

    public boolean loop() {
        return from == to;
    }
}
