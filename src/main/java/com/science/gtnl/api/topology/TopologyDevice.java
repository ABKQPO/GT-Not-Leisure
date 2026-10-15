package com.science.gtnl.api.topology;

import net.minecraft.item.ItemStack;

public record TopologyDevice(int id, int network, GridCoord position, ItemStack icon, boolean active,
    boolean channelOk) {

    public boolean faulted() {
        return !active || !channelOk;
    }
}
