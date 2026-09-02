package com.science.gtnl.container;

import net.minecraft.entity.player.InventoryPlayer;

import appeng.container.implementations.ContainerPriority;
import appeng.helpers.IPriorityHost;

public class ContainerCustomPriority extends ContainerPriority {

    private final IPriorityHost priorityHost;

    public ContainerCustomPriority(InventoryPlayer ip, IPriorityHost host) {
        super(ip, host);
        this.priorityHost = host;
    }

    @Override
    public Object getTarget() {
        return priorityHost;
    }
}
