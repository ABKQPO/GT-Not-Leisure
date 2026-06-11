package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.science.gtnl.common.machine.multiblock.steam.SteamCactusWonder;

public class SteamCactusWonderGui extends GTNLSteamMultiBlockBaseGui {

    private static final String FUELED_AMOUNT_SYNC_KEY = "steamCactusWonderFueledAmount";

    private final SteamCactusWonder cactusWonder;

    public SteamCactusWonderGui(SteamCactusWonder multiblock) {
        super(multiblock);
        this.cactusWonder = multiblock;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            FUELED_AMOUNT_SYNC_KEY,
            new LongSyncValue(cactusWonder::getFueledAmountForGui, cactusWonder::setFueledAmountFromGui));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        LongSyncValue fueledAmountSyncer = syncManager.findSyncHandler(FUELED_AMOUNT_SYNC_KEY, LongSyncValue.class);
        return super.createTerminalTextWidget(syncManager, parent).child(
            IKey.dynamic(
                () -> EnumChatFormatting.WHITE + StatCollector.translateToLocal("Tooltip_SteamCactusWonder_06")
                    + EnumChatFormatting.YELLOW
                    + cactusWonder.formatFueledAmountForGui(fueledAmountSyncer.getLongValue()))
                .asWidget()
                .fullWidth());
    }
}
