package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.science.gtnl.common.machine.multiblock.steam.SteamItemVault;

public class SteamItemVaultGui extends GTNLSteamMultiBlockBaseGui {

    private static final String TYPE_COUNT_SYNC_KEY = "steamItemVaultTypeCounts";
    private static final String TYPE_COUNT_TRANSLATION_KEY = "Info_SteamItemVault_TypeCount";

    private final SteamItemVault vault;

    public SteamItemVaultGui(SteamItemVault multiblock) {
        super(multiblock);
        this.vault = multiblock;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            TYPE_COUNT_SYNC_KEY,
            new StringSyncValue(vault::getTypeCountPayloadForGui, vault::setTypeCountPayloadFromGui));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        StringSyncValue typeCountSyncer = syncManager.findSyncHandler(TYPE_COUNT_SYNC_KEY, StringSyncValue.class);
        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.dynamic(
                () -> VaultTypeCountFormatter
                    .createTypeCountText(typeCountSyncer.getValue(), TYPE_COUNT_TRANSLATION_KEY))
                .asWidget()
                .fullWidth());
    }
}
