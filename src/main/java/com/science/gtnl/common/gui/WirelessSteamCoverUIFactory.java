package com.science.gtnl.common.gui;

import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.utils.enums.SteamTypes;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.gui.modularui.CoverUIBuildContext;
import gregtech.api.util.GTModHandler;
import gregtech.common.gui.modularui.widget.CoverDataControllerWidget;
import gregtech.common.gui.modularui.widget.CoverDataFollowerToggleButtonWidget;
import gregtech.common.gui.mui1.cover.CoverLegacyDataUIFactory;

@Deprecated
public class WirelessSteamCoverUIFactory extends CoverLegacyDataUIFactory {

    private static final int START_X = 10;
    private static final int START_Y = 25;
    private static final int SPACE_X = 18;
    private static final int SPACE_Y = 18;

    public WirelessSteamCoverUIFactory(CoverUIBuildContext buildContext) {
        super(buildContext);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public void addUIWidgets(ModularWindow.Builder builder) {
        // TODO: Remove this mui1 cover factory after the mui2 WirelessSteamCoverGui is fully wired.
        builder
            .widget(
                new CoverDataControllerWidget.CoverDataIndexedControllerWidget_ToggleButtons<>(
                    this::getCover,
                    (id, coverData) -> !isButtonClickable(id, coverData.getVariable()),
                    (id, coverData) -> coverData.setVariable(updateCoverVariableOnClick(id)),
                    getUIBuildContext())
                        .addToggleButton(
                            0,
                            CoverDataFollowerToggleButtonWidget.ofDisableable(),
                            widget -> widget.setStaticTexture(new ItemDrawable(Materials.Steam.getCells(1)))
                                .addTooltip(SteamTypes.STEAM.displayName)
                                .setPos(SPACE_X * 0, SPACE_Y * 0))
                        .addToggleButton(
                            1,
                            CoverDataFollowerToggleButtonWidget.ofDisableable(),
                            widget -> widget
                                .setStaticTexture(
                                    new ItemDrawable(
                                        GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "itemCellEmpty", 1, 13)))
                                .addTooltip(SteamTypes.SH_STEAM.displayName)
                                .setPos(SPACE_X * 1, SPACE_Y * 0))
                        .addToggleButton(
                            2,
                            CoverDataFollowerToggleButtonWidget.ofDisableable(),
                            widget -> widget
                                .setStaticTexture(new ItemDrawable(Materials.DenseSupercriticalSteam.getCells(1)))
                                .addTooltip(SteamTypes.DSC_STEAM.displayName)
                                .setPos(SPACE_X * 2, SPACE_Y * 0))
                        .setPos(START_X, START_Y))
            .widget(
                new TextWidget(StatCollector.translateToLocal("gtnl.gui.wireless_steam.type"))
                    .setDefaultColor(COLOR_TEXT_GRAY.get())
                    .setPos(3 + START_X + SPACE_X * 4, 4 + START_Y + SPACE_Y * 0));
    }

    private int updateCoverVariableOnClick(int buttonId) {
        return buttonId;
    }

    private boolean isButtonClickable(int buttonId, int currentVariable) {
        return buttonId != SteamTypes.fromNetworkTypeId(currentVariable)
            .ordinal();
    }
}
