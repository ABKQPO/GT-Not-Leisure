package com.science.gtnl.common.gui.modularui;

import java.text.NumberFormat;
import java.util.Locale;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.LongSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;

import gregtech.api.metatileentity.BaseTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineBronze;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.recipe.BasicUIProperties;
import gregtech.common.gui.modularui.widget.CircularGaugeDrawable;
import gregtech.common.modularui2.widget.GTProgressWidget;

public class GTNLSteamAssemblerGui<T extends MTEBasicMachineBronze> extends GTNLBasicMachineGui<T> {

    private static final String STEAM_STORED_SYNC_KEY = "steamStored";
    private static final String STEAM_CAPACITY_SYNC_KEY = "steamCapacity";
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US);

    private final boolean steel;
    private final boolean hasFluidInput;

    public GTNLSteamAssemblerGui(T machine, BasicUIProperties properties, boolean steel, boolean hasFluidInput) {
        super(machine, properties, GTNLMui2Textures.PICTURE_GTNL_STEAM_LOGO);
        this.steel = steel;
        this.hasFluidInput = hasFluidInput;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            STEAM_STORED_SYNC_KEY,
            new LongSyncValue(() -> baseMetaTileEntity == null ? 0 : baseMetaTileEntity.getStoredSteam()));
        syncManager.syncValue(
            STEAM_CAPACITY_SYNC_KEY,
            new LongSyncValue(() -> baseMetaTileEntity == null ? 0 : baseMetaTileEntity.getSteamCapacity()));
    }

    @Override
    protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        Flow cornerFlow = super.createBottomLeftCornerFlow(panel, syncManager);
        if (hasFluidInput) {
            cornerFlow.child(createFluidInputSlot());
        }
        return cornerFlow;
    }

    @Override
    protected ProgressWidget createProgressBar() {
        return new GTProgressWidget().neiTransferRect(properties.neiTransferRectId)
            .value(new DoubleSyncValue(this::getProgress))
            .texture(properties.progressBarMUI2, properties.progressBarWidthMUI2)
            .size(properties.progressBarWidthMUI2, properties.progressBarHeightMUI2 / 2)
            .direction(properties.progressBarDirectionMUI2)
            .tooltipShowUpTimer(BaseTileEntity.TOOLTIP_DELAY);
    }

    @Override
    protected FluidSlot createFluidInputSlot() {
        return new FluidSlot().backgroundOverlay(getFluidInputOverlay())
            .syncHandler(new FluidSlotSyncHandler(machine.getFluidTank()) {

                @Override
                protected void onValueChanged() {
                    super.onValueChanged();
                    if (getSyncManager().isClient()) return;
                    baseMetaTileEntity.markInventoryBeenModified();
                }
            });
    }

    @Override
    protected ParentWidget<?> createBottomSection(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createBottomSection(panel, syncManager).child(createSteamGauge(syncManager));
    }

    private Widget<?> createSteamGauge(PanelSyncManager syncManager) {
        LongSyncValue steamStoredSyncer = syncManager.findSyncHandler(STEAM_STORED_SYNC_KEY, LongSyncValue.class);
        LongSyncValue steamCapacitySyncer = syncManager.findSyncHandler(STEAM_CAPACITY_SYNC_KEY, LongSyncValue.class);
        return new ParentWidget<>().size(48, 42)
            .left(-48)
            .top(-8)
            .child(
                new Widget<>().background(steel ? GTGuiTextures.STEAM_GAUGE_BG_STEEL : GTGuiTextures.STEAM_GAUGE_BG)
                    .size(48, 42)
                    .tooltipDynamic(
                        tooltip -> tooltip.addLine(
                            StatCollector.translateToLocalFormatted(
                                "GT5U.machines.steam.amount",
                                NUMBER_FORMAT.format(steamStoredSyncer.getValue() * 2),
                                NUMBER_FORMAT.format(steamCapacitySyncer.getValue() * 2))))
                    .tooltipAutoUpdate(true)
                    .tooltipShowUpTimer(BaseTileEntity.TOOLTIP_DELAY))
            .child(
                new CircularGaugeDrawable(() -> getSteamProgress(steamStoredSyncer, steamCapacitySyncer)).asWidget()
                    .widgetTheme(GTWidgetThemes.STEAM_GAUGE_NEEDLE)
                    .size(18, 4)
                    .left(21)
                    .top(21));
    }

    private double getProgress() {
        int maxProgress = machine.maxProgresstime();
        if (maxProgress == 0) {
            return 0;
        }
        return (double) machine.getProgresstime() / maxProgress;
    }

    private float getSteamProgress(LongSyncValue steamStoredSyncer, LongSyncValue steamCapacitySyncer) {
        long capacity = steamCapacitySyncer.getValue();
        if (capacity == 0) {
            return 0;
        }
        return (float) steamStoredSyncer.getValue() / capacity;
    }

    private IDrawable getFluidInputOverlay() {
        IDrawable overlay = properties.slotOverlaysMUI2.apply(0, true, false, false);
        return overlay == null ? GTGuiTextures.OVERLAY_SLOT_IN_STANDARD : overlay;
    }
}
