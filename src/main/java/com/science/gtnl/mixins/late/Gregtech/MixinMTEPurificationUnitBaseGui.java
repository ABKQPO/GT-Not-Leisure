package com.science.gtnl.mixins.late.Gregtech;

import static net.minecraft.util.StatCollector.translateToLocal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;
import com.science.gtnl.api.mixinHelper.IPurificationUnitLongParallel;
import com.science.gtnl.config.MainConfig;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.MTEPurificationUnitBaseGui;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitBase;

@Mixin(value = MTEPurificationUnitBaseGui.class, remap = false)
public abstract class MixinMTEPurificationUnitBaseGui extends MTEMultiBlockBaseGui<MTEPurificationUnitBase<?>> {

    @Unique
    private static final String GTNL_LONG_PARALLEL_SYNC_KEY = "gtnlMaximumParallelsLong";

    @Unique
    private static final int GTNL_LONG_PARALLEL_PANEL_WIDTH = 158;

    @Unique
    private static final int GTNL_LONG_PARALLEL_PANEL_HEIGHT = 52;

    @Unique
    private static final int GTNL_LONG_PARALLEL_PANEL_PADDING = 4;

    @Unique
    private long gtnl$fallbackMaxParallelLong = 1;

    public MixinMTEPurificationUnitBaseGui(MTEPurificationUnitBase<?> multiblock) {
        super(multiblock);
    }

    @Inject(method = "registerSyncValues", at = @At("TAIL"))
    private void gtnl$registerLongParallelSync(PanelSyncManager syncManager, CallbackInfo ci) {
        LongSyncValue longParallelSyncer = new LongSyncValue(
            this::gtnl$getMaxParallelLong,
            this::gtnl$setMaxParallelLong).allowC2S();
        syncManager.syncValue(GTNL_LONG_PARALLEL_SYNC_KEY, longParallelSyncer);
    }

    @Inject(method = "createParallelButton", at = @At("HEAD"), cancellable = true)
    private void gtnl$createLongParallelButton(PanelSyncManager syncManager, ModularPanel parent,
        CallbackInfoReturnable<IWidget> cir) {
        if (!MainConfig.machine.enablePurificationPlantBuff) return;

        IPanelHandler parallelSelectPanel = syncManager.syncedPanel(
            "parallelSelectPanel",
            true,
            (panelSyncManager, syncHandler) -> gtnl$openLongParallelSelectPanel(syncManager, parent));

        cir.setReturnValue(
            new ButtonWidget<>().overlay(GTGuiTextures.OVERLAY_BUTTON_BATCH_MODE_ON)
                .tooltip(t -> t.addLine(translateToLocal("GT5U.tpm.parallelwindow")))
                .onMousePressed(mouseButton -> {
                    parallelSelectPanel.togglePanel();
                    return true;
                }));
    }

    @Unique
    private ModularPanel gtnl$openLongParallelSelectPanel(PanelSyncManager syncManager, ModularPanel parent) {
        ModularPanel panel = new ModularPanel("parallelSelectPanel")
            .size(GTNL_LONG_PARALLEL_PANEL_WIDTH, GTNL_LONG_PARALLEL_PANEL_HEIGHT)
            .relative(parent)
            .leftRel(1)
            .topRel(0.8f);

        LongSyncValue longParallelSyncer = syncManager
            .findSyncHandler(GTNL_LONG_PARALLEL_SYNC_KEY, LongSyncValue.class);
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
                .size(GTNL_LONG_PARALLEL_PANEL_WIDTH - GTNL_LONG_PARALLEL_PANEL_PADDING * 2, 18));

        return panel.child(column);
    }

    @Unique
    private long gtnl$getMaxParallelLong() {
        if (multiblock instanceof IPurificationUnitLongParallel longParallel) {
            return longParallel.gtnl$getMaxParallelLong();
        }
        return gtnl$fallbackMaxParallelLong;
    }

    @Unique
    private void gtnl$setMaxParallelLong(long value) {
        long maxParallel = Math.max(1, value);
        if (multiblock instanceof IPurificationUnitLongParallel longParallel) {
            longParallel.gtnl$setMaxParallelLong(maxParallel);
        } else {
            gtnl$fallbackMaxParallelLong = maxParallel;
        }
    }
}
