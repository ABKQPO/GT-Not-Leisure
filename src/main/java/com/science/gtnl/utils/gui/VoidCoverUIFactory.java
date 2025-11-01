package com.science.gtnl.utils.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.science.gtnl.common.machine.cover.VoidCover;
import com.science.gtnl.utils.machine.FluidsLockWidget;

import gregtech.api.gui.modularui.CoverUIBuildContext;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.gui.modularui.GUITextureSet;
import gregtech.api.gui.widgets.PhantomItemButton;
import gregtech.common.gui.mui1.cover.CoverLegacyDataUIFactory;

public class VoidCoverUIFactory extends CoverLegacyDataUIFactory {

    public VoidCoverUIFactory(CoverUIBuildContext buildContext) {
        super(buildContext);
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder) {
        if (!(getCover() instanceof VoidCover cover)) return;
        SlotGroup slotGroup = SlotGroup.ofItemHandler(cover.lockedInventoryHandler, 10)
            .startFromSlot(0)
            .endAtSlot(19)
            .background(PhantomItemButton.FILTER_BACKGROUND)
            .phantom(true)
            .slotCreator(index -> new BaseSlot(cover.lockedInventoryHandler, index, true))
            .build();
        builder.widget(
            slotGroup.setSize(18 * 10 + 4, 36)
                .setPos(5, 24));

        SlotGroup slotGroupFluid = new SlotGroup();
        int x = 0, y = 0;
        for (int i = 0; i < 20; i++) {
            FluidsLockWidget widget = new FluidsLockWidget(cover, i);
            widget.setPos(new Pos2d(x * 18, y * 18))
                .setBackground(GUITextureSet.DEFAULT.getFluidSlot());
            slotGroupFluid.addChild(widget);

            if (++x == 10) {
                x = 0;
                y++;
            }
        }
        builder.widget(
            slotGroupFluid.setSize(18 * 10 + 4, 36)
                .setPos(5, 60));

        builder.widget(
            new ButtonWidget().setOnClick((clickData, widget) -> cover.isInputMode = !cover.isInputMode())
                .setPlayClickSound(true)
                .setBackground(() -> {
                    List<IDrawable> ret = new ArrayList<>();
                    if (cover.isInputMode) {
                        ret.add(GTUITextures.BUTTON_STANDARD_PRESSED);
                        ret.add(GTUITextures.OVERLAY_BUTTON_WHITELIST);
                    } else {
                        ret.add(GTUITextures.BUTTON_STANDARD);
                        ret.add(GTUITextures.OVERLAY_BUTTON_BLACKLIST);
                    }
                    return ret.toArray(new IDrawable[0]);
                })
                .attachSyncer(
                    new FakeSyncWidget.BooleanSyncer(() -> cover.isInputMode, v -> cover.isInputMode = v),
                    builder)
                .addTooltip(StatCollector.translateToLocal("Info_VoidCover_00"))
                .setPos(78, 5)
                .setSize(16, 16));
    }

    @Override
    public int getGUIWidth() {
        return 190;
    }

    @Override
    public int getGUIHeight() {
        return 125;
    }
}
