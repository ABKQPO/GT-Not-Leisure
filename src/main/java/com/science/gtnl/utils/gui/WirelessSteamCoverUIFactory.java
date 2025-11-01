package com.science.gtnl.utils.gui;

import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.drawable.ItemDrawable;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.TextWidget;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.utils.enums.SteamTypes;

import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.gui.modularui.CoverUIBuildContext;
import gregtech.api.util.GTModHandler;
import gregtech.common.gui.modularui.widget.CoverDataControllerWidget;
import gregtech.common.gui.modularui.widget.CoverDataFollowerToggleButtonWidget;
import gregtech.common.gui.mui1.cover.CoverLegacyDataUIFactory;

public class WirelessSteamCoverUIFactory extends CoverLegacyDataUIFactory {

    private static final int startX = 10;
    private static final int startY = 25;
    private static final int spaceX = 18;
    private static final int spaceY = 18;

    public WirelessSteamCoverUIFactory(CoverUIBuildContext buildContext) {
        super(buildContext);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public void addUIWidgets(ModularWindow.Builder builder) {
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
                                .setPos(spaceX * 0, spaceY * 0))
                        .addToggleButton(
                            1,
                            CoverDataFollowerToggleButtonWidget.ofDisableable(),
                            widget -> widget
                                .setStaticTexture(
                                    new ItemDrawable(
                                        GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "itemCellEmpty", 1, 13)))
                                .addTooltip(SteamTypes.SH_STEAM.displayName)
                                .setPos(spaceX * 1, spaceY * 0))
                        .addToggleButton(
                            2,
                            CoverDataFollowerToggleButtonWidget.ofDisableable(),
                            widget -> widget
                                .setStaticTexture(new ItemDrawable(Materials.DenseSupercriticalSteam.getCells(1)))
                                .addTooltip(SteamTypes.DSC_STEAM.displayName)
                                .setPos(spaceX * 2, spaceY * 0))
                        .addToggleButton(
                            3,
                            CoverDataFollowerToggleButtonWidget.ofDisableable(),
                            widget -> widget
                                .setStaticTexture(
                                    new ItemDrawable(MaterialPool.CompressedSteam.get(OrePrefixes.cellMolten, 1)))
                                .addTooltip(SteamTypes.CM_STEAM.displayName)
                                .setPos(spaceX * 3, spaceY * 0))
                        .setPos(startX, startY))
            .widget(
                new TextWidget(StatCollector.translateToLocal("Info_PipelessSteamCover_01"))
                    .setDefaultColor(COLOR_TEXT_GRAY.get())
                    .setPos(3 + startX + spaceX * 5, 4 + startY + spaceY * 0));
    }

    private int updateCoverVariableOnClick(int buttonId) {
        return buttonId;
    }

    private boolean isButtonClickable(int buttonId, int currentVariable) {
        if (currentVariable < 0 || currentVariable > 3) return false;

        return buttonId != currentVariable;
    }
}
