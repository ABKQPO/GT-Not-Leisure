package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.science.gtnl.common.machine.multiblock.steam.FurnaceArray;

public class FurnaceArrayGui extends GTNLSteamMultiBlockBaseGui {

    private static final String FURNACE_COUNT_SYNC_KEY = "furnaceArrayFurnaceCount";
    private static final String COAL_COUNT_SYNC_KEY = "furnaceArrayCoalCount";

    private final FurnaceArray furnaceArray;

    public FurnaceArrayGui(FurnaceArray multiblock) {
        super(multiblock);
        this.furnaceArray = multiblock;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            FURNACE_COUNT_SYNC_KEY,
            new LongSyncValue(furnaceArray::getFurnaceCountForGui, furnaceArray::setFurnaceCountFromGui));
        syncManager.syncValue(
            COAL_COUNT_SYNC_KEY,
            new LongSyncValue(furnaceArray::getCoalCountForGui, furnaceArray::setCoalCountFromGui));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        LongSyncValue furnaceCountSyncer = syncManager.findSyncHandler(FURNACE_COUNT_SYNC_KEY, LongSyncValue.class);
        LongSyncValue coalCountSyncer = syncManager.findSyncHandler(COAL_COUNT_SYNC_KEY, LongSyncValue.class);
        return super.createTerminalTextWidget(syncManager, parent)
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocalFormatted(
                        "gtnl.machine.furnace_array.furnace_count",
                        furnaceCountSyncer.getLongValue()))
                    .asWidget()
                    .fullWidth())
            .child(
                IKey.dynamic(
                    () -> StatCollector.translateToLocalFormatted(
                        "gtnl.machine.furnace_array.coal_count",
                        coalCountSyncer.getLongValue()))
                    .asWidget()
                    .fullWidth());
    }
}
