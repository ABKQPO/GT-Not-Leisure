package com.science.gtnl.common.gui.modularui;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.DualInputHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

public class DualInputHatchGui extends MTEHatchBaseGui<DualInputHatch> {

    public static final String ITEM_SLOT_GROUP = "gtnl_dual_input_hatch_item_inv";

    public DualInputHatchGui(DualInputHatch hatch) {
        super(hatch);
    }

    @Override
    protected int getBasePanelWidth() {
        SlotLayout layout = SlotLayout.of(machine);
        return super.getBasePanelWidth() + Math.max(0, SLOT_SIZE * (layout.totalColumns() - 9));
    }

    @Override
    protected int getBasePanelHeight() {
        SlotLayout layout = SlotLayout.of(machine);
        return super.getBasePanelHeight() + (layout.visibleRows() > 4 ? (layout.visibleRows() - 3) * SLOT_SIZE : 0);
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        SlotLayout layout = SlotLayout.of(machine);
        return super.createContentSection(panel, syncManager).child(createSlotGroup(syncManager, layout).center());
    }

    public Flow createSlotGroup(PanelSyncManager syncManager, SlotLayout layout) {
        return Flow.row()
            .coverChildren()
            .child(createItemSlots(syncManager, layout))
            .child(createFluidSlots(layout));
    }

    public Grid createItemSlots(PanelSyncManager syncManager, SlotLayout layout) {
        return new ItemSlotGridBuilder(machine.inventoryHandler, syncManager).size(layout.itemColumns, layout.itemRows)
            .slotGroupKey(ITEM_SLOT_GROUP)
            .itemSlotSupplier(() -> new ItemSlot().background(GTGuiTextures.SLOT_ITEM_STANDARD))
            .build();
    }

    public Grid createFluidSlots(SlotLayout layout) {
        return new Grid().coverChildren()
            .gridOfWidthHeight(
                1,
                layout.fluidRows,
                ($x, $y, index) -> new FluidSlot()
                    .syncHandler(new FluidSlotSyncHandler(machine.getFluidTanksForGui()[index])));
    }

    @Override
    protected boolean supportsBottomRowOverlap() {
        return SlotLayout.of(machine)
            .visibleRows() <= 4;
    }

    @Override
    protected Widget<?> makeLogoWidget() {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_LOGO).size(SLOT_SIZE);
    }

    public record SlotLayout(int itemColumns, int itemRows, int fluidRows) {

        public static SlotLayout of(DualInputHatch hatch) {
            int itemColumns = Math.max(1, hatch.mTier);
            int itemRows = Math.max(1, hatch.mTier);
            int fluidRows = hatch.getFluidTanksForGui().length;
            return new SlotLayout(itemColumns, itemRows, fluidRows);
        }

        public int totalColumns() {
            return itemColumns + (fluidRows > 0 ? 1 : 0);
        }

        public int visibleRows() {
            return Math.max(itemRows, fluidRows);
        }
    }
}
