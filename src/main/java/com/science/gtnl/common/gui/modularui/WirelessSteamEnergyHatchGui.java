package com.science.gtnl.common.gui.modularui;

import java.util.Arrays;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.drawable.IDrawable;
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
import gregtech.common.modularui2.widget.builder.EnumRowBuilder;

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

        Flow steamSelector = new EnumRowBuilder<>(SteamTypes.class).value(steamModeSyncValue)
            .overlay(createSteamOverlays())
            .build();
        for (int index = 0; index < SteamTypes.VALUES.length; index++) {
            SteamTypes steamType = SteamTypes.VALUES[index];
            ToggleButton selectorButton = (ToggleButton) steamSelector.getChildren()
                .get(index);
            selectorButton.size(18)
                .background(false, GTGuiTextures.BUTTON_STANDARD)
                .background(true, GTGuiTextures.BUTTON_STANDARD_PRESSED)
                .tooltipDynamic(tooltip -> {
                    tooltip.addFromFluid(new FluidStack(steamType.fluid, 1));
                    if (hatch.getSteamMode() == steamType) {
                        tooltip.addLine("§e" + StatCollector.translateToLocal("Info_PipelessSteamCover_02"));
                    }
                })
                .tooltipAutoUpdate(true);
        }

        return super.createBottomSection(panel, syncManager).child(steamSelector.leftRel(0));
    }

    private static IDrawable[] createSteamOverlays() {
        return Arrays.stream(SteamTypes.VALUES)
            .<IDrawable>map(
                steamType -> new FluidDrawable(new FluidStack(steamType.fluid, 1)).asIcon()
                    .size(16))
            .toArray(IDrawable[]::new);
    }
}
