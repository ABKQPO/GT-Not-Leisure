package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;

import gregtech.api.metatileentity.implementations.MTEBasicMachineBronze;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.common.gui.modularui.singleblock.MTEBasicMachineBronzeGui;

public class GTNLSteamBasicMachineGui extends MTEBasicMachineBronzeGui {

    public GTNLSteamBasicMachineGui(MTEBasicMachineBronze machine, BasicUIProperties properties) {
        super(machine, properties);
    }

    @Override
    protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        Flow cornerFlow = super.createBottomLeftCornerFlow(panel, syncManager);
        if (properties.maxFluidInputs <= 0) {
            return cornerFlow;
        }
        return cornerFlow.child(createFluidInputSlot().marginLeft(SLOT_SIZE / 2));
    }
}
