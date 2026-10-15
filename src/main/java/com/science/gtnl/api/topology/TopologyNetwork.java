package com.science.gtnl.api.topology;

import net.minecraft.item.ItemStack;

import appeng.api.networking.pathing.ControllerState;

public record TopologyNetwork(int id, int dimension, GridCoord pivot, ItemStack icon, ControllerState controller,
    boolean booting, boolean powered, int nodes, int devices, int online, int starving, double storedPower,
    double maxPower, double idleDrain, int craftingCpu, int depth) {

    public boolean isMain() {
        return depth == 0;
    }

    public boolean settled() {
        return !booting && controller != ControllerState.CONTROLLER_CONFLICT;
    }
}
