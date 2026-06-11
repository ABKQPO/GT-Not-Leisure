package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Map;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.InteractionSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.Dialog;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.api.IGreenHouse;

import gregtech.api.modularui2.GTGuiTextures;

public class GTNLGreenHouseGui {

    public static final String CONFIGURATION_PANEL_KEY = "gtnl_greenhouse_configuration";

    private static final String SETUP_PHASE_SYNC_KEY = "gtnlGreenHouseSetupPhase";
    private static final String MODE_SYNC_KEY = "gtnlGreenHouseMode";
    private static final String HUMIDITY_SYNC_KEY = "gtnlGreenHouseHumidity";
    private static final int WIDTH = 200;
    private static final int HEIGHT = 100;
    private static final int BUTTON_SIZE = 16;

    private final IGreenHouse greenHouse;

    public GTNLGreenHouseGui(IGreenHouse greenHouse) {
        this.greenHouse = greenHouse;
    }

    public void registerSyncValues(PanelSyncManager syncManager) {
        syncManager
            .syncValue(SETUP_PHASE_SYNC_KEY, new IntSyncValue(greenHouse::getSetupPhase, greenHouse::setSetupPhase));
        syncManager.syncValue(
            MODE_SYNC_KEY,
            new IntSyncValue(
                () -> greenHouse.getMode()
                    .getUIIndex()));
        syncManager.syncValue(HUMIDITY_SYNC_KEY, new IntSyncValue(() -> greenHouse.isUseNoHumidity() ? 1 : 0));
    }

    public IWidget createConfigurationButton(IPanelHandler panelHandler) {
        return new ButtonWidget<>().size(BUTTON_SIZE, BUTTON_SIZE)
            .background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_CYCLIC)
            .onMousePressed(mouseButton -> {
                if (panelHandler != null) panelHandler.openPanel();
                return true;
            })
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("Info_EdenGarden_Configuration")))
            .tooltipShowUpTimer(TOOLTIP_DELAY);
    }

    public ModularPanel createConfigurationPanel(ModularPanel parent, PanelSyncManager syncManager) {
        Dialog<?> panel = new Dialog<>(CONFIGURATION_PANEL_KEY, null);
        panel.relative(parent)
            .size(WIDTH, HEIGHT)
            .background(GTGuiTextures.BACKGROUND_POPUP_STANDARD);
        panel.setDisablePanelsBelow(false)
            .setCloseOnOutOfBoundsClick(false)
            .setDraggable(true);

        panel.child(ButtonWidget.panelCloseButton());
        panel.child(
            GTGuiTextures.OVERLAY_BUTTON_CYCLIC.asWidget()
                .pos(5, 5)
                .size(16, 16));
        panel.child(
            IKey.lang("Info_EdenGarden_Configuration")
                .asWidget()
                .pos(25, 9)
                .size(150, 12));

        panel.child(createControls(syncManager).pos(10, 30));
        panel.child(createLabels().pos(80, 30));
        panel.child(
            GTGuiTextures.OVERLAY_BUTTON_CROSS.asWidget()
                .size(18, 18)
                .pos(10, 30)
                .tooltipBuilder(
                    tooltip -> tooltip.addLine(
                        IKey.str("Can't change configuration when running !")
                            .style(EnumChatFormatting.RED)))
                .setEnabledIf(
                    widget -> greenHouse.getBaseMetaTileEntity()
                        .isActive()));
        return panel;
    }

    private Flow createControls(PanelSyncManager syncManager) {
        return Flow.column()
            .coverChildren()
            .child(createSetupPhaseButton(syncManager))
            .child(createModeButton(syncManager))
            .child(createHumidityButton(syncManager))
            .setEnabledIf(
                widget -> !greenHouse.getBaseMetaTileEntity()
                    .isActive());
    }

    private IWidget createSetupPhaseButton(PanelSyncManager syncManager) {
        IntSyncValue syncValue = syncManager.findSyncHandler(SETUP_PHASE_SYNC_KEY, IntSyncValue.class);
        return new ButtonWidget<>().background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(new DynamicDrawable(() -> createSetupPhaseText(syncValue.getIntValue())))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                greenHouse.tryChangeSetupPhase(syncManager.getPlayer());
                syncValue.setIntValue(greenHouse.getSetupPhase(), false, true);
            }))
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("Info_EdenGarden_SetupMode")))
            .size(70, 18);
    }

    private IWidget createModeButton(PanelSyncManager syncManager) {
        IntSyncValue syncValue = syncManager.findSyncHandler(MODE_SYNC_KEY, IntSyncValue.class);
        return new ButtonWidget<>().background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(new DynamicDrawable(() -> createEnabledText(syncValue.getIntValue() == 1)))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                greenHouse.tryChangeMode(syncManager.getPlayer());
                syncValue.setIntValue(
                    greenHouse.getMode()
                        .getUIIndex(),
                    false,
                    true);
            }))
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("Info_EdenGarden_IC2Mode")))
            .size(70, 18);
    }

    private IWidget createHumidityButton(PanelSyncManager syncManager) {
        IntSyncValue syncValue = syncManager.findSyncHandler(HUMIDITY_SYNC_KEY, IntSyncValue.class);
        return new ButtonWidget<>().background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(new DynamicDrawable(() -> createEnabledText(syncValue.getIntValue() == 1)))
            .syncHandler(new InteractionSyncHandler().setOnMousePressed(mouseData -> {
                greenHouse.tryChangeHumidityMode(syncManager.getPlayer());
                syncValue.setIntValue(greenHouse.isUseNoHumidity() ? 1 : 0, false, true);
            }))
            .tooltipBuilder(tooltip -> tooltip.addLine(IKey.lang("Info_EdenGarden_NoHumidityMode")))
            .size(70, 18);
    }

    private IKey createSetupPhaseText(int setupPhase) {
        return switch (setupPhase) {
            case 0 -> createStateText("Info_EdenGarden_Operating", EnumChatFormatting.GREEN);
            case 1 -> createStateText("Info_EdenGarden_Input", EnumChatFormatting.YELLOW);
            case 2 -> createStateText("Info_EdenGarden_Output", EnumChatFormatting.YELLOW);
            default -> createStateText("Info_EdenGarden_SetupPhase_Invalid", EnumChatFormatting.RED);
        };
    }

    private IKey createEnabledText(boolean enabled) {
        return enabled ? createStateText("Info_EdenGarden_Enabled", EnumChatFormatting.GREEN)
            : createStateText("Info_EdenGarden_Disabled", EnumChatFormatting.RED);
    }

    private IKey createStateText(String langKey, EnumChatFormatting color) {
        return IKey.str(translateToLocal(langKey))
            .style(color)
            .scale(0.6f);
    }

    private Flow createLabels() {
        return Flow.column()
            .coverChildren()
            .child(createLabel("Info_EdenGarden_SetupMode"))
            .child(createLabel("Info_EdenGarden_IC2Mode"))
            .child(createLabel("Info_EdenGarden_NoHumidityMode"))
            .setEnabledIf(
                widget -> !greenHouse.getBaseMetaTileEntity()
                    .isActive());
    }

    private IWidget createLabel(String langKey) {
        return IKey.lang(langKey)
            .asWidget()
            .size(100, 18)
            .textAlign(Alignment.CenterLeft);
    }

    public void registerPanel(ModularPanel parent, PanelSyncManager syncManager, Map<String, IPanelHandler> panelMap) {
        panelMap.put(
            CONFIGURATION_PANEL_KEY,
            syncManager.syncedPanel(
                CONFIGURATION_PANEL_KEY,
                true,
                (panelSyncManager, panelHandler) -> createConfigurationPanel(parent, panelSyncManager)));
    }
}
