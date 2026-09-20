package com.science.gtnl.common.gui.modularui;

import java.util.function.Supplier;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.StringSyncValue;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.science.gtnl.common.machine.multiblock.SiphonTurbine;

/**
 * 虹吸涡轮主机 GUI：终端区只显示两个数——机器缓存里的 EU，以及过去 6.4s（一个循环）灌进动力仓的输出总量。
 */
public class SiphonTurbineGui extends GTNLMultiBlockBaseGui<SiphonTurbine> {

    private static final String EU_CACHE_KEY = "siphonTurbineEuCache";
    private static final String OUTPUT_KEY = "siphonTurbineOutputLastCycle";

    public SiphonTurbineGui(SiphonTurbine multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager
            .syncValue(EU_CACHE_KEY, new StringSyncValue(multiblock::getEuCacheForGui, multiblock::setInfoFromGui));
        syncManager.syncValue(
            OUTPUT_KEY,
            new StringSyncValue(multiblock::getOutputLastCycleForGui, multiblock::setInfoFromGui));
    }

    @Override
    protected ListWidget<IWidget, ?> createTerminalTextWidget(PanelSyncManager syncManager, ModularPanel parent) {
        StringSyncValue euCacheSyncer = syncManager.findSyncHandler(EU_CACHE_KEY, StringSyncValue.class);
        StringSyncValue outputSyncer = syncManager.findSyncHandler(OUTPUT_KEY, StringSyncValue.class);

        return super.createTerminalTextWidget(syncManager, parent).child(
            text(
                () -> StatCollector
                    .translateToLocalFormatted("gtnl.machine.siphon_turbine.gui.eu_cache", euCacheSyncer.getValue())))
            .child(
                text(
                    () -> StatCollector
                        .translateToLocalFormatted("gtnl.machine.siphon_turbine.gui.output", outputSyncer.getValue())));
    }

    private IWidget text(Supplier<String> supplier) {
        return IKey.dynamic(supplier)
            .asWidget()
            .fullWidth();
    }
}
