package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.machine.multiblock.FOGExtractorModule;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.godforge.ForgeOfGodsGuiUtil;
import tectech.thing.metaTileEntity.multi.godforge.MTEBaseModule;

public class FOGModuleGui extends GTNLMultiBlockBaseGui<MTEBaseModule> {

    private static final String FLUID_MODE_SYNC_KEY = "fluidMode";

    public FOGModuleGui(@NotNull MTEBaseModule multiblock) {
        super(multiblock);
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        if (!(multiblock instanceof FOGExtractorModule extractorModule)) return;
        syncManager.syncValue(
            FLUID_MODE_SYNC_KEY,
            new BooleanSyncValue(extractorModule::isFluidModeOn, extractorModule::setFluidMode).allowC2S());
    }

    @Override
    protected Flow createRightPanelGapRow(ModularPanel parent, PanelSyncManager syncManager) {
        Flow row = super.createRightPanelGapRow(parent, syncManager);
        if (!(multiblock instanceof FOGExtractorModule)) return row;
        return row.child(createFluidModeButton(syncManager));
    }

    private IWidget createFluidModeButton(PanelSyncManager syncManager) {
        BooleanSyncValue fluidModeSyncer = syncManager.findSyncHandler(FLUID_MODE_SYNC_KEY, BooleanSyncValue.class);
        return new ButtonWidget<>().size(16, 16)
            .background(GTGuiTextures.TT_BUTTON_CELESTIAL_32x32)
            .overlay(
                new DynamicDrawable(
                    () -> fluidModeSyncer.getBoolValue() ? GTGuiTextures.TT_OVERLAY_BUTTON_FURNACE_MODE
                        : GTGuiTextures.TT_OVERLAY_BUTTON_FURNACE_MODE_OFF))
            .onMousePressed(mouseButton -> {
                fluidModeSyncer.setBoolValue(!fluidModeSyncer.getBoolValue(), true, true);
                return true;
            })
            .tooltipDynamic(
                tooltip -> tooltip.addLine(
                    translateToLocal(
                        fluidModeSyncer.getBoolValue() ? "fog.button.fluidmode.tooltip.02"
                            : "fog.button.fluidmode.tooltip.01")))
            .tooltipAutoUpdate(true)
            .tooltipShowUpTimer(TOOLTIP_DELAY)
            .clickSound(ForgeOfGodsGuiUtil.getButtonSound());
    }
}
