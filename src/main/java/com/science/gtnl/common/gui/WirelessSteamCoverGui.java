package com.science.gtnl.common.gui;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.FluidDrawable;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.science.gtnl.common.machine.cover.WirelessSteamCover;
import com.science.gtnl.utils.enums.SteamTypes;

import gregtech.api.modularui2.CoverGuiData;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.cover.base.CoverBaseGui;

public class WirelessSteamCoverGui extends CoverBaseGui<WirelessSteamCover> {

    public WirelessSteamCoverGui(WirelessSteamCover cover) {
        super(cover);
    }

    @Override
    protected String getGuiId() {
        return "cover.wireless_steam";
    }

    @Override
    public void addUIWidgets(PanelSyncManager syncManager, Flow column, CoverGuiData data) {

        EnumSyncValue<SteamTypes, ?> steamModeSyncValue = new EnumSyncValue<>(
            SteamTypes.class,
            cover::getSteamMode,
            cover::setSteamMode).allowC2S();
        syncManager.syncValue("steam_mode", steamModeSyncValue);
        Flow steamButtons = Flow.row()
            .coverChildren()
            .childPadding(0);
        for (SteamTypes steamType : SteamTypes.NETWORK_CONVERTIBLE_TYPES) {
            steamButtons.child(
                new ToggleButton().valueWrapped(steamModeSyncValue, steamType.ordinal())
                    .size(18)
                    .background(false, GTGuiTextures.BUTTON_STANDARD)
                    .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
                    .overlay(
                        new FluidDrawable(new FluidStack(steamType.fluid, 1)).asIcon()
                            .size(16))
                    .tooltipDynamic(tooltip -> {
                        tooltip.addFromFluid(new FluidStack(steamType.fluid, 1));
                        if (cover.getSteamMode() == steamType) {
                            tooltip.addLine("§e" + StatCollector.translateToLocal("Info_PipelessSteamCover_02"));
                        }
                    })
                    .tooltipAutoUpdate(true));
        }
        IWidget steamLabel = IKey.str(StatCollector.translateToLocal("Info_PipelessSteamCover_01"))
            .asWidget();

        column.child(
            new Grid().marginLeft(WIDGET_MARGIN)
                .coverChildren()
                .minElementMarginRight(WIDGET_MARGIN)
                .minElementMarginBottom(1)
                .minElementMarginTop(0)
                .minElementMarginLeft(0)
                .alignment(Alignment.CenterLeft)
                .row(steamButtons, steamLabel));
    }
}
