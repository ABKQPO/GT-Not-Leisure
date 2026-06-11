package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.CustomMaintenanceHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;

public class CustomMaintenanceHatchGui {

    private static final String CONFIG_TIME_SYNC_KEY = "configTime";

    private final CustomMaintenanceHatch hatch;

    public CustomMaintenanceHatchGui(CustomMaintenanceHatch hatch) {
        this.hatch = hatch;
    }

    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        registerSyncValues(syncManager);
        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(hatch, guiData, syncManager, uiSettings)
            .setWidth(176)
            .setHeight(hatch.isConfiguration() ? 85 : 40)
            .doesAddGregTechLogo(false)
            .doesBindPlayerInventory(false)
            .build();
        if (hatch.isConfiguration()) {
            panel.child(createConfigurationLabel())
                .child(createConfigurationField(syncManager));
        }
        return panel.child(
            GTNLMui2Textures.PICTURE_GTNL_LOGO.asWidget()
                .size(18)
                .pos(151, hatch.isConfiguration() ? 62 : 17));
    }

    private void registerSyncValues(PanelSyncManager syncManager) {
        syncManager.syncValue(
            CONFIG_TIME_SYNC_KEY,
            new IntSyncValue(hatch::getConfigTime, hatch::setConfigTimeFromGui).allowC2S());
    }

    private IWidget createConfigurationLabel() {
        return IKey.lang("Info_ConfigurationMaintenanceHatch_00")
            .asWidget()
            .pos(49, 18)
            .size(81, 14);
    }

    private TextFieldWidget createConfigurationField(PanelSyncManager syncManager) {
        IntSyncValue configTimeSyncer = syncManager.findSyncHandler(CONFIG_TIME_SYNC_KEY, IntSyncValue.class);
        return new TextFieldWidget().value(configTimeSyncer)
            .numbersInt(hatch.getMinConfigTime(), hatch.getMaxConfigTime())
            .setScrollValues(1, 2, 5)
            .setTextAlignment(Alignment.Center)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .pos(54, 36)
            .size(70, 18);
    }
}
