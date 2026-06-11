package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.science.gtnl.common.machine.multiblock.SpaceAssembler;

public class SpaceAssemblerGui extends GTNLControllerUpgradeGui<SpaceAssembler> {

    private static final String UPGRADE_CONSUMED_SYNC_KEY = "gtnlUpgradeConsumed";

    public SpaceAssemblerGui(SpaceAssembler multiblock) {
        super(multiblock);
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        BooleanSyncValue upgradeSyncer = syncManager.findSyncHandler(UPGRADE_CONSUMED_SYNC_KEY, BooleanSyncValue.class);
        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.dynamic(
                () -> StatCollector.translateToLocal(
                    upgradeSyncer.getBoolValue() ? "Info_SpaceAssembler_02" : "Info_SpaceAssembler_01"))
                .asWidget()
                .fullWidth());
    }
}
