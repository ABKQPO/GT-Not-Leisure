package com.science.gtnl.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.api.widget.Widget;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;
import com.gtnewhorizons.modularui.common.widget.Scrollable;
import com.gtnewhorizons.modularui.common.widget.TextWidget;

import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.metatileentity.BaseTileEntity;
import tectech.thing.gui.TecTechUITextures;

public interface IControllerInfo {

    int MACHINE_INFO_WINDOW_ID = 9;

    boolean supportsMachineInfo();

    default int getMachineInfoWindowID() {
        return MACHINE_INFO_WINDOW_ID;
    }

    default Pos2d getMachineInfoButtonPos() {
        return new Pos2d(172, 67);
    }

    default ModularWindow createMachineInfo(final EntityPlayer player) {
        final Scrollable scrollable = new Scrollable().setVerticalScroll();
        final int WIDTH = 300;
        final int HEIGHT = 300;
        ModularWindow.Builder builder = ModularWindow.builder(WIDTH, HEIGHT);

        builder.setDraggable(true);
        scrollable.widget(
            new TextWidget("").setDefaultColor(EnumChatFormatting.DARK_PURPLE)
                .setTextAlignment(Alignment.TopCenter)
                .setPos(7, 13)
                .setSize(280, 15))
            .widget(
                new TextWidget(StatCollector.translateToLocal("")).setDefaultColor(EnumChatFormatting.GOLD)
                    .setTextAlignment(Alignment.CenterLeft)
                    .setPos(7, 30)
                    .setSize(280, 50));

        builder.widget(
            new DrawableWidget().setDrawable(TecTechUITextures.BACKGROUND_GLOW_WHITE)
                .setPos(0, 0)
                .setSize(300, 300))
            .widget(
                scrollable.setSize(292, 292)
                    .setPos(4, 4))
            .widget(
                ButtonWidget.closeWindowButton(true)
                    .setPos(284, 4));
        return builder.build();
    }

    default ButtonWidget createMachineInfoButton(IWidgetBuilder<?> builder) {
        Widget button = new ButtonWidget().setOnClick((clickData, widget) -> {
            if (supportsMachineInfo()) {
                if (!widget.isClient()) widget.getContext()
                    .openSyncedWindow(getMachineInfoWindowID());
            }
        })
            .setPlayClickSound(true)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(GTUITextures.BUTTON_STANDARD);
                ret.add(GTUITextures.OVERLAY_BUTTON_POWER_PANEL);
                return ret.toArray(new IDrawable[0]);
            })
            .addTooltip(StatCollector.translateToLocal("gt.blockmachines.multimachine.FOG.clickhere"))
            .setTooltipShowUpDelay(BaseTileEntity.TOOLTIP_DELAY)
            .setPos(getMachineInfoButtonPos())
            .setSize(16, 16);
        return (ButtonWidget) button;
    }
}
