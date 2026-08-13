package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.machine.basicMachine.HydraulicSuperBuffer;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.singleblock.base.MTEBufferBaseGui;

public class HydraulicSuperBufferGui extends MTEBufferBaseGui<HydraulicSuperBuffer> {

    public HydraulicSuperBufferGui(HydraulicSuperBuffer machine) {
        super(machine);
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createContentSection(panel, syncManager).child(
            GTGuiTextures.PICTURE_SUPER_BUFFER.asWidget()
                .size(54)
                .horizontalCenter());
    }

    @Override
    protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createBottomLeftCornerFlow(panel, syncManager).child(
            GTGuiTextures.PICTURE_ARROW_22_RED.asWidget()
                .size(50, 22)
                .marginLeft(11));
    }

    @Override
    protected int getBasePanelHeight() {
        return super.getBasePanelHeight() + 4;
    }

    @Override
    protected boolean supportsEmitEnergy() {
        return false;
    }
}
