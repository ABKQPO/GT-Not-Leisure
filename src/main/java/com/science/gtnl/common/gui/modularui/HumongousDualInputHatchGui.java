package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.StatCollector;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.HumongousDualInputHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.hatch.base.MTEHatchBaseGui;
import gregtech.common.modularui2.widget.builder.ItemSlotGridBuilder;

public class HumongousDualInputHatchGui extends MTEHatchBaseGui<HumongousDualInputHatch> {

    public HumongousDualInputHatchGui(HumongousDualInputHatch hatch) {
        super(hatch);
    }

    @Override
    protected int getBasePanelWidth() {
        DualInputHatchGui.SlotLayout layout = DualInputHatchGui.SlotLayout.of(machine);
        return super.getBasePanelWidth() + Math.max(0, SLOT_SIZE * (layout.totalColumns() - 9));
    }

    @Override
    protected int getBasePanelHeight() {
        DualInputHatchGui.SlotLayout layout = DualInputHatchGui.SlotLayout.of(machine);
        return super.getBasePanelHeight() + (layout.visibleRows() > 4 ? (layout.visibleRows() - 3) * SLOT_SIZE : 0);
    }

    @Override
    protected ParentWidget<?> createContentSection(ModularPanel panel, PanelSyncManager syncManager) {
        DualInputHatchGui.SlotLayout layout = DualInputHatchGui.SlotLayout.of(machine);
        return super.createContentSection(panel, syncManager).child(createSlotGroup(syncManager, layout).center());
    }

    public Flow createSlotGroup(PanelSyncManager syncManager, DualInputHatchGui.SlotLayout layout) {
        return Flow.row()
            .coverChildren()
            .child(createItemSlots(syncManager, layout))
            .child(createFluidSlots(layout));
    }

    public Grid createItemSlots(PanelSyncManager syncManager, DualInputHatchGui.SlotLayout layout) {
        return new ItemSlotGridBuilder(machine.inventoryHandler, syncManager)
            .size(layout.itemColumns(), layout.itemRows())
            .slotGroupKey("humongous_item_inv")
            .itemSlotSupplier(() -> new ItemSlot().background(GTGuiTextures.SLOT_ITEM_STANDARD))
            .build();
    }

    public Grid createFluidSlots(DualInputHatchGui.SlotLayout layout) {
        return new Grid().coverChildren()
            .gridOfWidthHeight(
                1,
                layout.fluidRows(),
                ($x, $y, index) -> new FluidSlot()
                    .syncHandler(new FluidSlotSyncHandler(machine.getFluidTanksForGui()[index])));
    }

    @Override
    protected Flow createBottomLeftCornerFlow(ModularPanel panel, PanelSyncManager syncManager) {
        return super.createBottomLeftCornerFlow(panel, syncManager)
            .child(createRefundButton(syncManager).marginLeft(1));
    }

    @Override
    protected boolean supportsBottomRowOverlap() {
        return DualInputHatchGui.SlotLayout.of(machine)
            .visibleRows() <= 4;
    }

    @Override
    protected Widget<?> makeLogoWidget() {
        return new IDrawable.DrawableWidget(GTNLMui2Textures.PICTURE_GTNL_LOGO).size(SLOT_SIZE);
    }

    public ButtonWidget<?> createRefundButton(PanelSyncManager syncManager) {
        return new ButtonWidget<>().background(GTGuiTextures.BUTTON_STANDARD)
            .overlay(GTGuiTextures.OVERLAY_BUTTON_EXPORT)
            .addTooltipLine(StatCollector.translateToLocal("Button_Tooltip_HumongousDualInputHatch_00"))
            .onMousePressed(mouseButton -> {
                syncManager.callSyncedAction("refund_all", buffer -> {});
                return true;
            })
            .size(16, 16);
    }
}
