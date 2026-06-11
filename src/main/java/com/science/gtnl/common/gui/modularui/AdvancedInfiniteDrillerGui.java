package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.science.gtnl.common.machine.multiblock.AdvancedInfiniteDriller;

public class AdvancedInfiniteDrillerGui extends GTNLMultiBlockBaseGui<AdvancedInfiniteDriller> {

    private static final String EXCESS_FUEL_SYNC_KEY = "advancedInfiniteDrillerExcessFuel";

    public AdvancedInfiniteDrillerGui(AdvancedInfiniteDriller multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            EXCESS_FUEL_SYNC_KEY,
            new DoubleSyncValue(multiblock::getExcessFuelForGui, multiblock::setExcessFuelFromGui));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        DoubleSyncValue excessFuelSyncer = syncManager.findSyncHandler(EXCESS_FUEL_SYNC_KEY, DoubleSyncValue.class);
        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.dynamic(
                () -> StatCollector
                    .translateToLocalFormatted("Info_AdvancedInfiniteDriller_00", excessFuelSyncer.getDoubleValue()))
                .asWidget()
                .fullWidth());
    }
}
