package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.machine.multiblock.EyeOfHarmonyInjector;

public class EyeOfHarmonyInjectorGui extends GTNLTTMultiBlockBaseGui<EyeOfHarmonyInjector> {

    private final EyeOfHarmonyInjectorStatusPanel statusPanel;

    public EyeOfHarmonyInjectorGui(EyeOfHarmonyInjector multiblock) {
        super(multiblock);
        this.statusPanel = new EyeOfHarmonyInjectorStatusPanel(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        statusPanel.registerSyncValues(syncManager);
    }

    @Override
    protected void initPanelMap(ModularPanel parent, PanelSyncManager syncManager) {
        super.initPanelMap(parent, syncManager);
        statusPanel.registerPanel(parent, syncManager, panelMap);
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        Flow row = super.createRightPanelGapRow(parent, syncManager);
        row.child(statusPanel.createStatusPanelButton(panelMap.get(EyeOfHarmonyInjectorStatusPanel.STATUS_PANEL_KEY)));
        return row;
    }
}
