package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.NinefoldInputHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;

public class NinefoldInputHatchGui {

    private static final int[][] POSITIONS = { { 61, 16 }, { 79, 16 }, { 97, 16 }, { 61, 34 }, { 79, 34 }, { 97, 34 },
        { 61, 52 }, { 79, 52 }, { 97, 52 } };

    private final NinefoldInputHatch hatch;

    public NinefoldInputHatchGui(NinefoldInputHatch hatch) {
        this.hatch = hatch;
    }

    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(hatch, guiData, syncManager, uiSettings)
            .doesBindPlayerInventory(false)
            .doesAddGregTechLogo(false)
            .build();
        for (int i = 0; i < hatch.getFluidTanksForGui().length && i < POSITIONS.length; i++) {
            panel.child(createFluidSlot(i).pos(POSITIONS[i][0], POSITIONS[i][1]));
        }
        return panel.child(
            GTNLMui2Textures.PICTURE_GTNL_LOGO.asWidget()
                .size(18)
                .pos(151, 62));
    }

    private FluidSlot createFluidSlot(int index) {
        return new FluidSlot().syncHandler(new FluidSlotSyncHandler(hatch.getFluidTanksForGui()[index]))
            .background(GTGuiTextures.SLOT_FLUID_TANK);
    }
}
