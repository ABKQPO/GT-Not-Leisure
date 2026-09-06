package com.science.gtnl.client.gui.portableWorkbench;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.item.cellworkbench.PortableCellWorkbenchHost;
import com.science.gtnl.common.item.cellworkbench.PortableCellWorkbenchSubGui;
import com.science.gtnl.common.packet.OpenPortableCellWorkbenchSubGuiPacket;

import appeng.api.config.ActionItems;
import appeng.api.config.Settings;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiCellWorkbench;
import appeng.client.gui.widgets.GuiImgButton;

public class GuiPortableCellWorkbench extends GuiCellWorkbench {

    public GuiPortableCellWorkbench(InventoryPlayer playerInventory, PortableCellWorkbenchHost host) {
        super(playerInventory, host);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof GuiImgButton imageButton && imageButton.getSetting() == Settings.ACTIONS) {
            if (imageButton.getCurrentValue() == ActionItems.ORE_FILTER) {
                openSubGui(PortableCellWorkbenchSubGui.ORE_FILTER);
                return;
            }
            if (imageButton.getCurrentValue() == ActionItems.CELL_RESTRICTION) {
                openSubGui(PortableCellWorkbenchSubGui.CELL_RESTRICTION);
                return;
            }
        }
        super.actionPerformed(button);
    }

    private void openSubGui(PortableCellWorkbenchSubGui subGui) {
        AEBaseGui.setSwitchingGuis(true);
        ScienceNotLeisure.network.sendToServer(new OpenPortableCellWorkbenchSubGuiPacket(subGui));
    }
}
