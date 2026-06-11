package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.science.gtnl.common.machine.multiblock.PCBFactory;

public class PCBFactoryGui extends GTNLControllerUpgradeGui<PCBFactory> {

    private static final String MACHINE_TIER_SYNC_KEY = "pcbFactoryMachineTier";

    public PCBFactoryGui(PCBFactory multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            MACHINE_TIER_SYNC_KEY,
            new IntSyncValue(multiblock::getMachineTierForGui, multiblock::setMachineTierFromGui));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        IntSyncValue machineTierSyncer = syncManager.findSyncHandler(MACHINE_TIER_SYNC_KEY, IntSyncValue.class);
        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.dynamic(() -> StatCollector.translateToLocal("Info_PCBFactory_00") + machineTierSyncer.getIntValue())
                .asWidget()
                .fullWidth());
    }
}
