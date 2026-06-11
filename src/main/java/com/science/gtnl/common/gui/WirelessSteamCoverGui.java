package com.science.gtnl.common.gui;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.science.gtnl.common.machine.cover.WirelessSteamCover;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.SteamTypes;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.modularui2.CoverGuiData;
import gregtech.api.util.GTModHandler;
import gregtech.common.gui.modularui.cover.base.CoverBaseGui;
import gregtech.common.modularui2.widget.builder.EnumRowBuilder;

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
            cover::setSteamMode);
        syncManager.syncValue("steam_mode", steamModeSyncValue);
        IWidget steamButtons = new EnumRowBuilder<>(SteamTypes.class).value(steamModeSyncValue)
            .overlay(
                new DynamicDrawable(() -> new ItemDrawable(Materials.Steam.getCells(1))),
                new DynamicDrawable(
                    () -> new ItemDrawable(GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "itemCellEmpty", 1, 13))),
                new DynamicDrawable(() -> new ItemDrawable(Materials.DenseSupercriticalSteam.getCells(1))),
                new DynamicDrawable(
                    () -> new ItemDrawable(GTNLMaterials.CompressedSteam.get(OrePrefixes.cellMolten, 1))))
            .tooltip(
                IKey.dynamic(() -> SteamTypes.STEAM.displayName),
                IKey.dynamic(() -> SteamTypes.SH_STEAM.displayName),
                IKey.dynamic(() -> SteamTypes.DSC_STEAM.displayName),
                IKey.dynamic(() -> SteamTypes.CM_STEAM.displayName))
            .build();
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
