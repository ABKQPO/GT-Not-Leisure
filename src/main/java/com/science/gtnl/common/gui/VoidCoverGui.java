package com.science.gtnl.common.gui;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import com.science.gtnl.common.machine.cover.VoidCover;

import gregtech.api.modularui2.CoverGuiData;
import gregtech.api.modularui2.GTGuiTextures;
import gregtech.common.gui.modularui.cover.base.CoverBaseGui;

public class VoidCoverGui extends CoverBaseGui<VoidCover> {

    private static final int FILTER_COLUMNS = 10;
    private static final int FILTER_ROWS = 2;

    public VoidCoverGui(VoidCover cover) {
        super(cover);
    }

    @Override
    protected String getGuiId() {
        return "cover.void_filter";
    }

    @Override
    public void addUIWidgets(PanelSyncManager syncManager, Flow column, CoverGuiData data) {
        BooleanSyncValue inputModeSyncer = new BooleanSyncValue(cover::isInputMode, cover::setInputMode).allowC2S();
        syncManager.syncValue("input_mode", inputModeSyncer);

        column.child(
            Flow.column()
                .coverChildren()
                .crossAxisAlignment(Alignment.CrossAxis.START)
                .marginLeft(WIDGET_MARGIN)
                .child(createModeRow(inputModeSyncer))
                .child(
                    IKey.lang("gt.interact.desc.Item_Filter.Filter")
                        .asWidget()
                        .marginTop(WIDGET_MARGIN))
                .child(createItemFilterGrid())
                .child(createFluidFilterGrid()));
    }

    private IWidget createModeRow(BooleanSyncValue inputModeSyncer) {
        return Flow.row()
            .coverChildren()
            .childPadding(WIDGET_MARGIN)
            .child(
                new ToggleButton().value(inputModeSyncer)
                    .overlay(false, GTGuiTextures.OVERLAY_BUTTON_BLACKLIST)
                    .overlay(true, GTGuiTextures.OVERLAY_BUTTON_WHITELIST)
                    .tooltipDynamic(tooltip -> tooltip.addLine(StatCollector.translateToLocal("Info_VoidCover_00"))))
            .child(
                IKey.dynamic(() -> StatCollector.translateToLocal("Info_VoidCover_00"))
                    .asWidget());
    }

    private IWidget createItemFilterGrid() {
        return SlotGroupWidget.builder()
            .matrix("IIIIIIIIII", "IIIIIIIIII")
            .key(
                'I',
                index -> new PhantomItemSlot().slot(new ModularSlot(cover.lockedInventoryHandler, index))
                    .background(GTGuiTextures.SLOT_ITEM_STANDARD)
                    .backgroundOverlay(GTGuiTextures.OVERLAY_SLOT_FILTER))
            .build()
            .marginTop(WIDGET_MARGIN);
    }

    private IWidget createFluidFilterGrid() {
        return SlotGroupWidget.builder()
            .matrix("FFFFFFFFFF", "FFFFFFFFFF")
            .key(
                'F',
                index -> new FluidSlot()
                    .syncHandler(
                        new FluidSlotSyncHandler(new VoidCoverFluidFilter(index)).phantom(true)
                            .controlsAmount(false))
                    .tooltipDynamic(tooltip -> {
                        FluidStack fluidStack = getFluidStack(index);
                        if (fluidStack != null) {
                            tooltip.addFromFluid(fluidStack);
                        } else {
                            tooltip.addLine(StatCollector.translateToLocal("gt.interact.desc.FluidFilter.Empty"));
                        }
                    }))
            .build()
            .marginTop(WIDGET_MARGIN);
    }

    private FluidStack getFluidStack(int index) {
        String fluidName = cover.getLockedFluidNames(index);
        if (fluidName == null || fluidName.isEmpty()) return null;
        return FluidRegistry.getFluidStack(fluidName, 1);
    }

    private class VoidCoverFluidFilter implements IFluidTank {

        private final int index;

        private VoidCoverFluidFilter(int index) {
            this.index = index;
        }

        @Override
        public FluidStack getFluid() {
            return getFluidStack(index);
        }

        @Override
        public int getFluidAmount() {
            return getFluid() == null ? 0 : 1;
        }

        @Override
        public int getCapacity() {
            return 1;
        }

        @Override
        public FluidTankInfo getInfo() {
            FluidStack fluidStack = getFluid();
            return fluidStack == null ? null : new FluidTankInfo(fluidStack, getCapacity());
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (resource == null || resource.getFluid() == null) return 0;
            String fluidName = resource.getFluid()
                .getName();
            if (!cover.acceptsFluidsLock(fluidName, index)) return 0;
            if (doFill) {
                cover.setLockedFluidNames(index, fluidName);
                cover.lockFluids(true, index);
            }
            return 1;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            FluidStack fluidStack = getFluid();
            if (fluidStack == null) return null;
            if (doDrain) {
                cover.setLockedFluidNames(index, null);
                cover.lockFluids(false, index);
            }
            return fluidStack;
        }
    }
}
