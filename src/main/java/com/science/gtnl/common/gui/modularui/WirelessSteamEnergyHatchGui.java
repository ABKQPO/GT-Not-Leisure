package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.drawable.FluidDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.science.gtnl.common.machine.hatch.WirelessSteamEnergyHatch;
import com.science.gtnl.utils.enums.SteamTypes;

import gregtech.api.modularui2.GTGuiTextures;

public class WirelessSteamEnergyHatchGui extends CustomFluidHatchGui {

    public WirelessSteamEnergyHatchGui(WirelessSteamEnergyHatch hatch) {
        super(hatch);
    }

    @Override
    protected ParentWidget<?> createBottomSection(ModularPanel panel, PanelSyncManager syncManager) {
        WirelessSteamEnergyHatch hatch = (WirelessSteamEnergyHatch) machine;
        EnumSyncValue<SteamTypes, ?> steamModeSyncValue = new EnumSyncValue<>(
            SteamTypes.class,
            hatch::getSteamMode,
            hatch::setSteamMode).allowC2S();
        syncManager.syncValue("steam_mode", steamModeSyncValue);

        Flow steamSelector = Flow.row()
            .coverChildren()
            .childPadding(0);
        for (SteamTypes steamType : SteamTypes.NETWORK_CONVERTIBLE_TYPES) {
            steamSelector.child(
                new ToggleButton().valueWrapped(steamModeSyncValue, steamType.ordinal())
                    .size(18)
                    .background(false, GTGuiTextures.BUTTON_STANDARD)
                    .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
                    .overlay(
                        new FluidDrawable(new FluidStack(steamType.fluid, 1)).asIcon()
                            .size(16))
                    .tooltipDynamic(tooltip -> {
                        tooltip.addFromFluid(new FluidStack(steamType.fluid, 1));
                        if (hatch.getSteamMode() == steamType) {
                            tooltip.addLine("§e" + StatCollector.translateToLocal("Info_PipelessSteamCover_02"));
                        }
                    })
                    .tooltipAutoUpdate(true));
        }

        return super.createBottomSection(panel, syncManager).child(steamSelector.leftRel(0));
    }
}
