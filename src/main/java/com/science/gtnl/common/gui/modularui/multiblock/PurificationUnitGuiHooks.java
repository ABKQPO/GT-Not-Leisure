package com.science.gtnl.common.gui.modularui.multiblock;

import static net.minecraft.util.StatCollector.translateToLocal;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.IntSyncValue;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.api.mixinHelper.IWirelessMode;
import com.science.gtnl.config.MainConfig;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBase;

public class PurificationUnitGuiHooks {

    public static final String LONG_PARALLEL_SYNC_KEY = "gtnlMaximumParallelsLong";
    public static final String PARALLEL_SYNC_KEY = "maximumParallels";
    public static final int LONG_PARALLEL_PANEL_WIDTH = 158;
    public static final int LONG_PARALLEL_PANEL_HEIGHT = 52;
    public static final int LONG_PARALLEL_PANEL_PADDING = 4;
    public static final int PARALLEL_PANEL_WIDTH = 120;
    public static final int PARALLEL_PANEL_HEIGHT = 50;
    public static final int PARALLEL_PANEL_PADDING = 4;

    private PurificationUnitGuiHooks() {}

    public static IWidget parallelButton(MTEPurificationUnitBase<?> multiblock, PanelSyncManager syncManager,
        ModularPanel parent) {
        if (!MainConfig.machine.enablePurificationPlantBuff) return null;

        if (!(multiblock instanceof IWirelessMode wirelessMode && wirelessMode.isGtnl$wirelessMode())) {
            IPanelHandler parallelSelectPanel = syncManager.syncedPanel(
                "parallelSelectPanel",
                true,
                (panelSyncManager, syncHandler) -> standardParallelPanel(syncManager, parent));

            return new ButtonWidget<>().overlay(GTGuiTextures.OVERLAY_BUTTON_BATCH_MODE_ON)
                .tooltip(t -> {
                    t.addLine(translateToLocal("GT5U.tpm.parallelwindow"));
                    t.addLine(translateToLocal("gtnl.gui.purification_unit.not_wireless_mode"));
                })
                .onMousePressed(mouseButton -> {
                    parallelSelectPanel.togglePanel();
                    return true;
                });
        }

        IPanelHandler parallelSelectPanel = syncManager.syncedPanel(
            "parallelSelectPanel",
            true,
            (panelSyncManager, syncHandler) -> longParallelPanel(syncManager, parent));

        return new ButtonWidget<>().overlay(GTGuiTextures.OVERLAY_BUTTON_BATCH_MODE_ON)
            .tooltip(t -> t.addLine(translateToLocal("GT5U.tpm.parallelwindow")))
            .onMousePressed(mouseButton -> {
                parallelSelectPanel.togglePanel();
                return true;
            });
    }

    private static ModularPanel standardParallelPanel(PanelSyncManager syncManager, ModularPanel parent) {
        ModularPanel panel = new ModularPanel("parallelSelectPanel").size(PARALLEL_PANEL_WIDTH, PARALLEL_PANEL_HEIGHT)
            .relative(parent)
            .leftRel(1)
            .topRel(0.8f);

        IntSyncValue parallelSyncer = syncManager.findSyncHandler(PARALLEL_SYNC_KEY, IntSyncValue.class);
        Flow column = Flow.column()
            .full()
            .paddingTop(4);
        column.child(
            IKey.lang("GTPP.CC.parallel")
                .asWidget()
                .marginBottom(4));
        column.child(
            new TextFieldWidget().formatAsInteger(true)
                .numbersInt(1, Integer.MAX_VALUE)
                .setTextAlignment(Alignment.CENTER)
                .defaultNumber(1)
                .value(parallelSyncer)
                .size(PARALLEL_PANEL_WIDTH - PARALLEL_PANEL_PADDING * 2, 18));

        return panel.child(column);
    }

    private static ModularPanel longParallelPanel(PanelSyncManager syncManager, ModularPanel parent) {
        ModularPanel panel = new ModularPanel("parallelSelectPanel")
            .size(LONG_PARALLEL_PANEL_WIDTH, LONG_PARALLEL_PANEL_HEIGHT)
            .relative(parent)
            .leftRel(1)
            .topRel(0.8f);

        LongSyncValue longParallelSyncer = syncManager.findSyncHandler(LONG_PARALLEL_SYNC_KEY, LongSyncValue.class);
        Flow column = Flow.column()
            .full()
            .paddingTop(4);
        column.child(
            IKey.lang("GTPP.CC.parallel")
                .asWidget()
                .marginBottom(4));
        column.child(
            new TextFieldWidget().formatAsInteger(true)
                .numbersLong(() -> 1L, () -> Long.MAX_VALUE)
                .setTextAlignment(Alignment.CENTER)
                .defaultNumber(1)
                .scrollValues(1, 1024, 65536, 1048576)
                .value(longParallelSyncer)
                .size(LONG_PARALLEL_PANEL_WIDTH - LONG_PARALLEL_PANEL_PADDING * 2, 18));

        return panel.child(column);
    }
}
