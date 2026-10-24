package com.science.gtnl.api.topology;

import net.minecraft.item.ItemStack;

public record TopologyDevice(int id, int network, int relay, GridCoord position, ItemStack icon, boolean active,
    boolean channelOk) {

    /**
     * Relay value for a device reached without crossing any wireless or quantum hop.
     */
    public static final int DIRECT = TopologyRelay.ROOT;

    public boolean faulted() {
        return !active || !channelOk;
    }
}
