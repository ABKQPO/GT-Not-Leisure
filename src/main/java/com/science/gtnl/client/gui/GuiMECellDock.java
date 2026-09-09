package com.science.gtnl.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.SwitchToCustomGuiPacket;
import com.science.gtnl.common.part.PartMECellDock;
import com.science.gtnl.container.ContainerMECellDock;
import com.science.gtnl.utils.enums.GuiType;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.core.localization.ColorUtils;
import appeng.core.localization.GuiText;

public class GuiMECellDock extends AEBaseGui {

    private final PartMECellDock dock;
    private GuiTabButton priorityButton;

    public GuiMECellDock(InventoryPlayer playerInventory, PartMECellDock dock) {
        super(new ContainerMECellDock(playerInventory, dock));
        this.dock = dock;
        ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        priorityButton = new GuiTabButton(guiLeft + 154, guiTop, 2 + 4 * 16, GuiText.Priority.getLocal(), itemRender);
        buttonList.add(priorityButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button == priorityButton) {
            AEBaseGui.setSwitchingGuis(true);
            ScienceNotLeisure.network
                .sendToServer(new SwitchToCustomGuiPacket(GuiType.CustomPriorityGUI, dock.getSide()));
        }
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        fontRendererObj.drawString(
            getGuiDisplayName(StatCollector.translateToLocal("gtnl.gui.me_cell_dock.name")),
            8,
            6,
            ColorUtils.guiTextColorGray.getColor());
        fontRendererObj
            .drawString(GuiText.inventory.getLocal(), 8, ySize - 96 + 3, ColorUtils.guiTextColorGray.getColor());
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        bindTexture("guis/chest.png");
        drawTexturedModalRect(offsetX, offsetY, 0, 0, xSize, ySize);
    }
}
