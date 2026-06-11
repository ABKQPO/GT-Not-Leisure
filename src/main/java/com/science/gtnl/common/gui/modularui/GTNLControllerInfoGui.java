package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;

import java.util.Map;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.api.IControllerInfo;

import gregtech.api.modularui2.GTGuiTextures;

public class GTNLControllerInfoGui {

    public static final String MACHINE_INFO_PANEL_KEY = "gtnl_machine_info";

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private static final int BUTTON_SIZE = 16;

    private final IControllerInfo controllerInfo;

    public GTNLControllerInfoGui(IControllerInfo controllerInfo) {
        this.controllerInfo = controllerInfo;
    }

    public IWidget createMachineInfoButton(IPanelHandler panelHandler) {
        return new ButtonWidget<>().size(BUTTON_SIZE, BUTTON_SIZE)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.TT_OVERLAY_BUTTON_POWER_PANEL)
            .onMousePressed(mouseButton -> {
                if (!controllerInfo.supportsMachineInfo()) return false;
                if (panelHandler != null) panelHandler.openPanel();
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("gt.blockmachines.multimachine.FOG.clickhere")))
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .setEnabledIf(widget -> controllerInfo.supportsMachineInfo());
    }

    public ModularPanel createMachineInfoPanel(ModularPanel parent) {
        Dialog<?> panel = new Dialog<>(MACHINE_INFO_PANEL_KEY, null);
        panel.relative(parent)
            .size(WIDTH, HEIGHT)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);

        panel.child(ButtonWidget.panelCloseButton());
        panel.child(createMachineInfoScroll());
        return panel;
    }

    private IWidget createMachineInfoScroll() {
        return new ScrollWidget<>().size(WIDTH - 8, HEIGHT - 8)
            .pos(4, 4)
            .child(
                Flow.column()
                    .fullWidth()
                    .coverChildrenHeight(0)
                    .crossAxisAlignment(Alignment.CrossAxis.START)
                    .child(
                        IKey.str("")
                            .style(EnumChatFormatting.DARK_PURPLE)
                            .asWidget()
                            .fullWidth()
                            .height(15)
                            .textAlign(Alignment.TopCenter))
                    .child(
                        IKey.lang("")
                            .style(EnumChatFormatting.GOLD)
                            .asWidget()
                            .fullWidth()
                            .height(50)
                            .textAlign(Alignment.CenterLeft)));
    }

    public void registerPanel(ModularPanel parent, PanelSyncManager syncManager, Map<String, IPanelHandler> panelMap) {
        panelMap.put(
            MACHINE_INFO_PANEL_KEY,
            syncManager.syncedPanel(
                MACHINE_INFO_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createMachineInfoPanel(parent)));
    }
}
