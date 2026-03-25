package com.science.gtnl.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Mouse;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.packet.SwitchToCustomGuiPacket;
import com.science.gtnl.common.part.PartActiveFormationPlane;
import com.science.gtnl.container.ContainerActiveFormationPlane;
import com.science.gtnl.utils.enums.GuiType;

import appeng.api.config.FuzzyMode;
import appeng.api.config.SchedulingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiUpgradeable;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.container.implementations.ContainerFormationPlane;
import appeng.core.localization.GuiColors;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketConfigButton;
import appeng.parts.AEBasePart;

public class GuiActiveFormationPlane extends GuiUpgradeable {

    public GuiTabButton priority;
    public GuiImgButton placeMode;

    public GuiActiveFormationPlane(InventoryPlayer inventoryPlayer, PartActiveFormationPlane te) {
        super(new ContainerActiveFormationPlane(inventoryPlayer, te));
        this.ySize = 251;
    }

    @Override
    public void addButtons() {
        this.priority = new GuiTabButton(
            this.guiLeft + 154,
            this.guiTop,
            2 + 4 * 16,
            GuiText.Priority.getLocal(),
            itemRender);
        this.placeMode = new GuiImgButton(this.guiLeft - 18, this.guiTop + 28, Settings.PLACE_BLOCK, YesNo.YES);
        this.fuzzyMode = new GuiImgButton(
            this.guiLeft - 18,
            this.guiTop + 48,
            Settings.FUZZY_MODE,
            FuzzyMode.IGNORE_ALL);
        this.schedulingMode = new GuiImgButton(
            this.guiLeft - 18,
            this.guiTop + 68,
            Settings.SCHEDULING_MODE,
            SchedulingMode.DEFAULT);

        this.buttonList.add(this.priority);
        this.buttonList.add(this.placeMode);
        this.buttonList.add(this.fuzzyMode);
        this.buttonList.add(this.schedulingMode);
    }

    @Override
    public void handleButtonVisibility() {
        super.handleButtonVisibility();
        if (this.schedulingMode != null) {
            this.schedulingMode.setVisibility(true);
        }
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.fontRendererObj.drawString(
            this.getGuiDisplayName(GuiText.FormationPlane.getLocal()),
            8,
            6,
            GuiColors.FormationPlaneTitle.getColor());
        this.fontRendererObj.drawString(
            GuiText.inventory.getLocal(),
            8,
            this.ySize - 96 + 3,
            GuiColors.FormationPlaneInventory.getColor());

        if (this.fuzzyMode != null) {
            this.fuzzyMode.set(this.cvb.getFuzzyMode());
        }
        if (this.placeMode != null) {
            this.placeMode.set(((ContainerFormationPlane) this.cvb).getPlaceMode());
        }
        if (this.schedulingMode != null) {
            this.schedulingMode.set(this.cvb.getSchedulingMode());
        }
    }

    @Override
    public String getBackground() {
        return "guis/storagebus.png";
    }

    @Override
    public void actionPerformed(final GuiButton btn) {
        super.actionPerformed(btn);

        final boolean backwards = Mouse.isButtonDown(1);

        if (btn == this.priority) {
            ForgeDirection side = ForgeDirection.UNKNOWN;
            if (this.bc instanceof AEBasePart part) {
                side = part.getSide();
            }
            AEBaseGui.setSwitchingGuis(true);
            ScienceNotLeisure.network.sendToServer(new SwitchToCustomGuiPacket(GuiType.CustomPriorityGUI, side));
        } else if (btn == this.placeMode) {
            NetworkHandler.instance.sendToServer(new PacketConfigButton(this.placeMode.getSetting(), backwards));
        }
    }
}
