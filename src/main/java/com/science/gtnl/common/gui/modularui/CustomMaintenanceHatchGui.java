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
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;

public class CustomMaintenanceHatchGui extends MTEHatchBaseGui<CustomMaintenanceHatch> {

    public static final String CONFIG_TIME_SYNC_KEY = "configTime";

    public CustomMaintenanceHatchGui(CustomMaintenanceHatch hatch) {
        super(hatch);
    }

    @Override
    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        registerSyncValues(syncManager);
        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(machine, guiData, syncManager, uiSettings)
            .setWidth(176)
            .setHeight(machine.isConfiguration() ? 85 : 40)
            .doesAddGregTechLogo(false)
            .doesBindPlayerInventory(false)
            .build();
        if (machine.isConfiguration()) {
            panel.child(createConfigurationLabel())
                .child(createConfigurationField(syncManager));
        }
        return panel.child(
            GTNLMui2Textures.PICTURE_GTNL_LOGO.asWidget()
                .size(18)
                .pos(151, machine.isConfiguration() ? 62 : 17));
    }

    @Override
    public void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            CONFIG_TIME_SYNC_KEY,
            new IntSyncValue(machine::getConfigTime, machine::setConfigTimeFromGui).allowC2S());
    }

    public IWidget createConfigurationLabel() {
        return IKey.lang("Info_ConfigurationMaintenanceHatch_00")
            .asWidget()
            .pos(49, 18)
            .size(81, 14);
    }

    public TextFieldWidget createConfigurationField(PanelSyncManager syncManager) {
        IntSyncValue configTimeSyncer = syncManager.findSyncHandler(CONFIG_TIME_SYNC_KEY, IntSyncValue.class);
        return new TextFieldWidget().value(configTimeSyncer)
            .numbersInt(machine.getMinConfigTime(), machine.getMaxConfigTime())
            .scrollValues(1, 2, 5, 10)
            .setTextAlignment(Alignment.Center)
            .background(GTGuiTextures.BACKGROUND_TEXT_FIELD)
            .pos(54, 36)
            .size(70, 18);
    }
}
