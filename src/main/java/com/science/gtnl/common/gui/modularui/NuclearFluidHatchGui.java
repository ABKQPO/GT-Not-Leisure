package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.science.gtnl.common.machine.hatch.NuclearFluidHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;

public class NuclearFluidHatchGui extends MTEHatchBaseGui<NuclearFluidHatch> {

    private static final int COLOR_INPUT_BAR = 0xFF4D7BFF;

    public NuclearFluidHatchGui(NuclearFluidHatch hatch) {
        super(hatch);
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        Flow row = Flow.row()
            .coverChildren()
            .childPadding(1)
            .child(bar(COLOR_INPUT_BAR))
            .child(
                new FluidSlot().background(GTGuiTextures.SLOT_FLUID_STANDARD)
                    .syncHandler(new FluidSlotSyncHandler(machine.getFluidTank())));

        return super.createContentSection(panel, syncManager).child(row.center());
    }

    private Widget<?> bar(int color) {
        return new IDrawable.DrawableWidget(new Rectangle().color(color)).size(2, SLOT_SIZE);
    }
}
