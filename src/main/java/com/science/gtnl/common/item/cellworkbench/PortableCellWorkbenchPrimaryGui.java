package com.science.gtnl.common.item.cellworkbench;

import net.minecraft.entity.player.EntityPlayer;

import com.science.gtnl.common.gui.GTNLGuiHandler;
import com.science.gtnl.utils.enums.GuiType;

import appeng.container.PrimaryGui;

public class PortableCellWorkbenchPrimaryGui extends PrimaryGui {

    private final PortableCellWorkbenchHost host;

    public PortableCellWorkbenchPrimaryGui(PortableCellWorkbenchHost host) {
        super(null, host.getPrimaryGuiIcon(), null, null);
        this.host = host;
    }

    @Override
    public void open(EntityPlayer player) {
        if (host.isValid()) {
            GTNLGuiHandler.openGui(
                player,
                GuiType.PortableCellWorkbenchGUI,
                null,
                player.worldObj,
                host.getInventorySlot(),
                0,
                0);
        }
    }
}
