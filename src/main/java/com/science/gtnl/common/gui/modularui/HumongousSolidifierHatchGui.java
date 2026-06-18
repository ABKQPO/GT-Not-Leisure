package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.HumongousSolidifierHatch;

import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.modularui2.widget.GhostMoldSlotWidget;

public class HumongousSolidifierHatchGui extends MTEHatchBaseGui<HumongousSolidifierHatch> {

    public HumongousSolidifierHatchGui(HumongousSolidifierHatch hatch) {
        super(hatch);
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createContentSection(panel, syncManager).child(
            new Grid().coverChildren()
                .gridOfWidthHeight(3, 3, ($x, $y, index) -> createFluidSlot(index))
                .center())
            .child(createMoldSlot(syncManager).pos(125, 35));
    }

    @Override
    protected UITexture getLogoTexture() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO;
    }

    private FluidSlot createFluidSlot(int index) {
        return new FluidSlot().syncHandler(new FluidSlotSyncHandler(machine.getFluidTanksForGui()[index]));
    }

    private GhostMoldSlotWidget createMoldSlot(PanelSyncManager syncManager) {
        return (GhostMoldSlotWidget) new GhostMoldSlotWidget(machine, syncManager).slot(
            new ModularSlot(machine.inventoryHandler, HumongousSolidifierHatch.moldSlot)
                .filter(stack -> machine.isItemValidForSlot(HumongousSolidifierHatch.moldSlot, stack)));
    }

    @Override
    protected boolean supportsBottomRowOverlap() {
        return true;
    }
}
