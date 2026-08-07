package com.science.gtnl.common.gui.modularui;

import static gregtech.api.metatileentity.BaseTileEntity.TOOLTIP_DELAY;
import static net.minecraft.util.StatCollector.translateToLocal;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.DynamicDrawable;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.science.gtnl.common.machine.multiblock.FOGExtractorModule;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.multiblock.godforge.ForgeOfGodsGuiUtil;

public class FOGExtractorModuleGui extends FOGModuleGui {

    private static final String FLUID_MODE_SYNC_KEY = "fluidMode";

    private final FOGExtractorModule extractorModule;

    public FOGExtractorModuleGui(@NotNull FOGExtractorModule multiblock) {
        super(multiblock);
        this.extractorModule = multiblock;
    }

    @Override
    protected void registerSyncValues(PanelSyncManager syncManager) {
        super.registerSyncValues(syncManager);
        syncManager.syncValue(
            FLUID_MODE_SYNC_KEY,
            new BooleanSyncValue(extractorModule::isFluidModeOn, extractorModule::setFluidMode).allowC2S());
    }

    @Override
    protected boolean usesExtraButton() {
        return true;
    }

    @Override
    protected IWidget createExtraButton() {
        BooleanSyncValue fluidModeSyncer = hypervisor.getSyncManager(getMainPanel())
            .findSyncHandler(FLUID_MODE_SYNC_KEY, BooleanSyncValue.class);
        return new ButtonWidget<>().size(16)
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
