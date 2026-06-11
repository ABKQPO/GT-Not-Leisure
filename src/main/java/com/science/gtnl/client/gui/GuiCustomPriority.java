package com.science.gtnl.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.ICustomGui;
import com.science.gtnl.common.packet.SwitchToCustomGuiPacket;
import com.science.gtnl.utils.enums.GuiType;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiPriority;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.container.AEBaseContainer;
import appeng.helpers.IPriorityHost;
import appeng.parts.PartBasicState;

public class GuiCustomPriority extends GuiPriority {

    private final GuiType customReturnGui;
    private final ItemStack returnGuiIcon;
    private GuiTabButton returnGuiButton;

    public GuiCustomPriority(InventoryPlayer inventoryPlayer, IPriorityHost te, GuiType returnGui) {
        super(inventoryPlayer, te);
        this.customReturnGui = returnGui;
        this.returnGuiIcon = te instanceof ICustomGui customGui ? customGui.getOriginGuiIcon() : null;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (this.customReturnGui != null && this.returnGuiIcon != null) {
            this.buttonList.add(
                this.returnGuiButton = new GuiTabButton(
                    this.guiLeft + 154,
                    this.guiTop,
                    this.returnGuiIcon,
                    this.returnGuiIcon.getDisplayName(),
                    itemRender));
        }
    }

    @Override
    public void actionPerformed(GuiButton btn) {
        if (btn == this.returnGuiButton && this.customReturnGui != null) {
            AEBaseGui.setSwitchingGuis(true);

            ForgeDirection side = ForgeDirection.UNKNOWN;
            if (this.inventorySlots instanceof AEBaseContainer bc) {
                if (bc.getTarget() instanceof PartBasicState part) {
                    side = part.getSide();
                }
            }

            ScienceNotLeisure.network.sendToServer(new SwitchToCustomGuiPacket(this.customReturnGui, side));
            return;
        }
        super.actionPerformed(btn);
    }
}
