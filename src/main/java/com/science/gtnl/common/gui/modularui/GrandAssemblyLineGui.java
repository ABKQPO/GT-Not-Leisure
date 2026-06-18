package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.machine.multiblock.GrandAssemblyLine;

import gregtech.api.modularui2.GTGuiTextures;

public class GrandAssemblyLineGui extends GTNLMultiBlockBaseGui<GrandAssemblyLine> {

    private static final String MIN_RECIPE_TIME_SYNC_KEY = "grandAssemblyLineMinRecipeTime";
    private static final String MIN_RECIPE_TIME_PANEL_KEY = "grandAssemblyLineMinRecipeTimePanel";
    private static final int PANEL_WIDTH = 158;
    private static final int PANEL_HEIGHT = 52;

    public GrandAssemblyLineGui(GrandAssemblyLine multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            MIN_RECIPE_TIME_SYNC_KEY,
            new IntSyncValue(multiblock::getMinRecipeTimeForGui, multiblock::setMinRecipeTimeFromGui).allowC2S());
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return super.createRightPanelGapRow(parent, syncManager).child(createMinRecipeTimeButton(parent, syncManager));
    }

    private IWidget createMinRecipeTimeButton(ModularPanel parent, PanelSyncManager syncManager) {
        IPanelHandler panel = syncManager.syncedPanel(
            MIN_RECIPE_TIME_PANEL_KEY,
            true,
            (panelSyncManager, panelHandler) -> createMinRecipeTimePanel(parent, panelSyncManager));

        return new ButtonWidget<>().size(16, 16)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_BATCH_MODE_ON)
            .onMousePressed(mouseButton -> {
                if (panel.isPanelOpen()) {
                    panel.closePanel();
                } else {
                    panel.openPanel();
                }
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(translateToLocal("Info_GrandAssemblyLine_00")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    private ModularPanel createMinRecipeTimePanel(ModularPanel parent, PanelSyncManager syncManager) {
        IntSyncValue minRecipeTimeSyncer = syncManager.findSyncHandler(MIN_RECIPE_TIME_SYNC_KEY, IntSyncValue.class);
        Dialog<?> panel = new Dialog<>(MIN_RECIPE_TIME_PANEL_KEY, null);

        panel.relative(parent)
            .leftRel(1)
            .topRel(0.8f)
            .size(PANEL_WIDTH, PANEL_HEIGHT)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);

        panel.child(ButtonWidget.panelCloseButton());
        panel.child(
            IKey.lang("Info_GrandAssemblyLine_00")
                .asWidget()
                .pos(3, 4)
                .size(PANEL_WIDTH - 8, 18)
                .textAlign(Alignment.Center));
        panel.child(
            new TextFieldWidget().formatAsInteger(true)
                .numbersInt(1, Integer.MAX_VALUE)
                .scrollValues(1, 4, 64, 256)
                .setTextAlignment(Alignment.Center)
                .setTextColor(Color.WHITE.main)
                .defaultNumber(1)
                .value(minRecipeTimeSyncer)
                .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
                .pos(4, 25)
                .size(PANEL_WIDTH - 8, 18));
        return panel;
    }
}
