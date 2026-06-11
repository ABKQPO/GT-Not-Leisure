package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.EnumChatFormatting;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.science.gtnl.common.machine.multiblock.steam.MegaSolarBoiler;

public class MegaSolarBoilerGui extends GTNLSteamMultiBlockBaseGui {

    private static final String STATE_SYNC_KEY = "megaSolarBoilerState";

    private final MegaSolarBoiler boiler;

    public MegaSolarBoilerGui(MegaSolarBoiler multiblock) {
        super(multiblock);
        this.boiler = multiblock;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(STATE_SYNC_KEY, new StringSyncValue(boiler::getStateForGui, boiler::setStateFromGui));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        StringSyncValue stateSyncer = syncManager.findSyncHandler(STATE_SYNC_KEY, StringSyncValue.class);
        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.dynamic(() -> EnumChatFormatting.GREEN + stateSyncer.getStringValue())
                .asWidget()
                .fullWidth());
    }
}
