package com.science.gtnl.common.item.cellworkbench;

import net.minecraft.entity.player.InventoryPlayer;

import appeng.api.config.FuzzyMode;
import appeng.container.implementations.ContainerCellWorkbench;

public class PortableCellWorkbenchContainer extends ContainerCellWorkbench {

    private final PortableCellWorkbenchHost host;

    public PortableCellWorkbenchContainer(InventoryPlayer playerInventory, PortableCellWorkbenchHost host) {
        super(playerInventory, host);
        this.host = host;
        PortableCellWorkbenchGuiFactory.configureSubGui(this, host);
    }

    @Override
    public void setFuzzy(FuzzyMode fuzzyMode) {
        super.setFuzzy(fuzzyMode);
        host.saveChanges();
    }
}
