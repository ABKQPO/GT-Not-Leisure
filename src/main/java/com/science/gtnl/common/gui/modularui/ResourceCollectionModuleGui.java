package com.science.gtnl.common.gui.modularui;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.machine.multiblock.ResourceCollectionModule;

public class ResourceCollectionModuleGui extends GTNLTileEntityModuleBaseGui<ResourceCollectionModule> {

    public ResourceCollectionModuleGui(@NotNull ResourceCollectionModule multiblock) {
        super(multiblock);
    }

    @Override
    protected Flow createLeftPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        return Flow.row()
            .coverChildrenWidth()
            .fullHeight();
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        Flow row = super.createRightPanelGapRow(parent, syncManager);
        if (machineModeIcons.isEmpty()) return row;
        return row.child(createModeSwitchButton(syncManager));
    }
}
