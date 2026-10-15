package com.science.gtnl.api.topology;

import net.minecraft.item.ItemStack;

import appeng.api.networking.IGrid;

public record BridgeReport(TopologyLinkKind kind, IGrid remote, GridCoord origin, GridCoord remoteEnd, boolean active,
    ItemStack icon) {

    public BridgeReport {
        if (kind == null) throw new IllegalArgumentException("Bridge kind is required");
        if (origin == null) throw new IllegalArgumentException("Bridge origin is required");
    }
}
