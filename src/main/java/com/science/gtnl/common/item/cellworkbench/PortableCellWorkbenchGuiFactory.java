package com.science.gtnl.common.item.cellworkbench;

import net.minecraft.entity.player.InventoryPlayer;

import appeng.container.AEBaseContainer;
import appeng.container.ContainerOpenContext;
import appeng.container.implementations.ContainerCellRestriction;
import appeng.container.implementations.ContainerOreFilter;

public class PortableCellWorkbenchGuiFactory {

    public static ContainerCellRestriction createCellRestriction(InventoryPlayer playerInventory,
        PortableCellWorkbenchHost host) {
        ContainerCellRestriction container = new ContainerCellRestriction(playerInventory, host);
        configureSubGui(container, host);
        return container;
    }

    public static ContainerOreFilter createOreFilter(InventoryPlayer playerInventory, PortableCellWorkbenchHost host) {
        ContainerOreFilter container = new ContainerOreFilter(playerInventory, host);
        configureSubGui(container, host);
        return container;
    }

    public static void configureSubGui(AEBaseContainer container, PortableCellWorkbenchHost host) {
        container.setOpenContext(new ContainerOpenContext(host));
        container.setPrimaryGui(new PortableCellWorkbenchPrimaryGui(host));
    }
}
