package com.science.gtnl.common.gui;

import java.util.List;

import net.minecraft.util.StatCollector;

import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.FluidNameHolderWidget;
import com.gtnewhorizons.modularui.common.widget.SlotGroup;
import com.science.gtnl.api.IFluidsLockable;
import com.science.gtnl.common.machine.cover.VoidCover;

import gregtech.api.gui.modularui.CoverUIBuildContext;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.gui.modularui.GUITextureSet;
import gregtech.api.gui.widgets.PhantomItemButton;
import gregtech.common.covers.Cover;
import gregtech.common.gui.mui1.cover.CoverUIFactory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class VoidCoverUIFactory extends CoverUIFactory<VoidCover> {

    public VoidCoverUIFactory(CoverUIBuildContext buildContext) {
        super(buildContext);
    }

    @Override
    protected VoidCover adaptCover(Cover cover) {
        return cover instanceof VoidCover voidCover ? voidCover : null;
    }

    @Override
    protected void addUIWidgets(ModularWindow.Builder builder) {
        VoidCover cover = getCover();
        if (cover == null) return;
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
                    List<IDrawable> ret = new ObjectArrayList<>();
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
                .addTooltip(StatCollector.translateToLocal("gtnl.cover.void.mode.input"))
                .setPos(78, 5)
                .setSize(16, 16));
    }

    @Override
    protected int getGUIWidth() {
        return 190;
    }

    @Override
    protected int getGUIHeight() {
        return 125;
    }

    public static class FluidsLockWidget extends FluidNameHolderWidget {

        public FluidsLockWidget(IFluidsLockable fluidsLockable, int index) {
            super(() -> fluidsLockable.getLockedFluidNames(index), name -> {
                if (fluidsLockable.acceptsFluidsLock(name, index)) {
                    fluidsLockable.setLockedFluidNames(index, name);
                    fluidsLockable.lockFluids(name != null, index);
                }
            });
        }
    }
}
