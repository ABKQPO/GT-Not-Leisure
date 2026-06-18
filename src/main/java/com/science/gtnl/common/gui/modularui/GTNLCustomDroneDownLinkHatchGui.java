package com.science.gtnl.common.gui.modularui;

import java.util.List;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.DynamicSyncHandler;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.DynamicSyncedWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.CustomDroneDownLinkHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.util.GTUtility;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.gui.modularui.multiblock.dronecentre.DroneCentreGuiUtil;
import gregtech.common.gui.modularui.multiblock.dronecentre.sync.DroneConnectionListSyncHandler;
import gregtech.common.tileentities.machines.multi.drone.DroneConnection;

public class GTNLCustomDroneDownLinkHatchGui extends MTEHatchBaseGui<CustomDroneDownLinkHatch> {

    private static final String DRONE_CONNECTIONS_SYNC_KEY = "droneConnections";
    private static final String KEY_SYNC_KEY = "setkey";
    private static final String CONFIG_TIME_SYNC_KEY = "configTime";

    private DroneConnectionListSyncHandler droneConnectionListSyncHandler;

    public GTNLCustomDroneDownLinkHatchGui(CustomDroneDownLinkHatch hatch) {
        super(hatch);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        registerSyncValues(syncManager);
        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(machine, guiData, syncManager, uiSettings)
            .setWidth(176)
            .setHeight(machine.isConfiguration() ? 95 : 50)
            .doesAddCoverTabs(false)
            .doesBindPlayerInventory(false)
            .doesAddGregTechLogo(false)
            .build()
            .coverChildrenHeight();

        ParentWidget<?> mainSection = new ParentWidget<>().coverChildrenHeight()
            .fullWidth()
            .padding(4)
            .child(createKeyButton(syncManager, panel))
            .child(createGTNLLogo());

        Flow mainColumn = Flow.column()
            .childPadding(4)
            .coverChildren()
            .horizontalCenter()
            .child(
                IKey.lang("GT5U.gui.text.drone_custom_name")
                    .asWidget())
            .child(createDynamicTextWidget(syncManager));

        if (machine.isConfiguration()) {
            mainColumn.child(createConfigurationLabel())
                .child(createConfigurationField(syncManager));
        }

        mainSection.child(mainColumn);
        return panel.child(mainSection);
    }

    private IWidget createKeyButton(PanelSyncManager syncManager, ModularPanel parent) {
        IPanelHandler keyPanel = syncManager.syncedPanel(
            "keyPanel",
            true,
            (panelSyncManager, syncHandler) -> DroneCentreGuiUtil.createConnectionKeyPanel(syncManager, parent));
        return new ButtonWidget<>().size(12)
            .topRel(0)
            .rightRel(0)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_RECIPE_LOCKED)
            .onMousePressed(mouseButton -> {
                if (!keyPanel.isPanelOpen()) {
                    keyPanel.openPanel();
                } else {
                    keyPanel.closePanel();
                }
                return true;
            })
            .addTooltipLine(GTUtility.translate("GT5U.gui.button.drone_key_panel"));
    }

    private IWidget createGTNLLogo() {
        return getLogoTexture().asWidget()
            .size(18)
            .pos(125, machine.isConfiguration() ? 72 : 27);
    }

    private IWidget createDynamicTextWidget(PanelSyncManager syncManager) {
        DynamicSyncHandler customNameHandler = new DynamicSyncHandler()
            .widgetProvider((dynamicSyncManager, packet) -> createTextArea(dynamicSyncManager))
            .allowC2S();

        droneConnectionListSyncHandler.setChangeListener(() -> customNameHandler.notifyUpdate(packet -> {}));
        customNameHandler.notifyUpdate(packet -> {});

        return new DynamicSyncedWidget<>().coverChildren()
            .syncHandler(customNameHandler);
    }

    private IWidget createTextArea(PanelSyncManager syncManager) {
        Flow column = Flow.column()
            .coverChildren()
            .childPadding(3);
        List<DroneConnection> clientConnections = droneConnectionListSyncHandler.getValue();
        if (clientConnections.isEmpty()) {
            column.child(
                IKey.lang("GT5U.gui.text.drone_no_connection")
                    .asWidget());
            return column;
        }
        for (DroneConnection connection : clientConnections) {
            StringSyncValue nameSyncValue = syncManager.getOrCreateSyncHandler(
                connection.uuid.toString(),
                StringSyncValue.class,
                () -> new StringSyncValue(
                    () -> droneConnectionListSyncHandler.getValue()
                        .stream()
                        .filter(clientConnection -> clientConnection.uuid.equals(connection.uuid))
                        .findFirst()
                        .map(DroneConnection::getCustomName)
                        .orElse(""),
                    value -> machine.findConnection(connection.uuid)
                        .ifPresent(droneConnection -> droneConnection.setCustomName(value))).allowC2S());
            column.child(
                Flow.row()
                    .coverChildren()
                    .childPadding(3)
                    .child(DroneCentreGuiUtil.createHighLightButton(connection, syncManager))
                    .child(
                        new TextFieldWidget().value(nameSyncValue)
                            .setValidator(value -> value.substring(0, Math.min(value.length(), 50)))
                            .size(140, 16)));
        }
        return column;
    }

    private IWidget createConfigurationLabel() {
        return IKey.lang("Info_ConfigurationMaintenanceHatch_00")
            .asWidget()
            .textAlign(Alignment.Center)
            .size(150, 14);
    }

    private IWidget createConfigurationField(PanelSyncManager syncManager) {
        IntSyncValue configTimeSyncer = syncManager.findSyncHandler(CONFIG_TIME_SYNC_KEY, IntSyncValue.class);
        return new TextFieldWidget().value(configTimeSyncer)
            .numbersInt(machine.getMinConfigTime(), machine.getMaxConfigTime())
            .scrollValues(1, 2, 5, 10)
            .setTextAlignment(Alignment.Center)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .size(70, 18);
    }

    @Override
    public void registerSyncValues(PanelSyncManager syncManager) {
        droneConnectionListSyncHandler = new DroneConnectionListSyncHandler(machine::getConnections);
        syncManager.syncValue(DRONE_CONNECTIONS_SYNC_KEY, droneConnectionListSyncHandler);
        syncManager.syncValue(KEY_SYNC_KEY, new StringSyncValue(machine::getKey, machine::setKey).allowC2S());
        syncManager.syncValue(
            CONFIG_TIME_SYNC_KEY,
            new IntSyncValue(machine::getConfigTime, machine::setConfigTimeFromGui).allowC2S());
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }
}
