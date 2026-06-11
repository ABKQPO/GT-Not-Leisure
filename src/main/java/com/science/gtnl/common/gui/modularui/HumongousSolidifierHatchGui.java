package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.HumongousSolidifierHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;
import gregtech.common.modularui2.widget.GhostMoldSlotWidget;

public class HumongousSolidifierHatchGui {

    private static final int[][] FLUID_POSITIONS = { { 61, 16 }, { 79, 16 }, { 97, 16 }, { 61, 34 }, { 79, 34 },
        { 97, 34 }, { 61, 52 }, { 79, 52 }, { 97, 52 } };

    private final HumongousSolidifierHatch hatch;

    public HumongousSolidifierHatchGui(HumongousSolidifierHatch hatch) {
        this.hatch = hatch;
    }

    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        ModularPanel panel = GTGuis.mteTemplatePanelBuilder(hatch, guiData, syncManager, uiSettings)
            .doesBindPlayerInventory(false)
            .doesAddGregTechLogo(false)
            .build();
        for (int i = 0; i < hatch.getFluidTanksForGui().length && i < FLUID_POSITIONS.length; i++) {
            panel.child(createFluidSlot(i).pos(FLUID_POSITIONS[i][0], FLUID_POSITIONS[i][1]));
        }
        panel.child(createMoldSlot(syncManager));
        return panel.child(
            GTNLMui2Textures.PICTURE_GTNL_LOGO.asWidget()
                .size(18)
                .pos(151, 62));
    }

    private FluidSlot createFluidSlot(int index) {
        return new FluidSlot().syncHandler(new FluidSlotSyncHandler(hatch.getFluidTanksForGui()[index]))
            .background(GTGuiTextures.SLOT_FLUID_TANK);
    }

    private GhostMoldSlotWidget createMoldSlot(PanelSyncManager syncManager) {
        return (GhostMoldSlotWidget) new GhostMoldSlotWidget(hatch, syncManager)
            .slot(
                new ModularSlot(hatch.inventoryHandler, HumongousSolidifierHatch.moldSlot)
                    .filter(stack -> hatch.isItemValidForSlot(HumongousSolidifierHatch.moldSlot, stack)))
            .pos(125, 35);
    }
}
