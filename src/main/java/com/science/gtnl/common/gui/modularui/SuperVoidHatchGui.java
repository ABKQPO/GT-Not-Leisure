package com.science.gtnl.common.gui.modularui;

import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.science.gtnl.common.gui.GTNLMui2Textures;
import com.science.gtnl.common.machine.hatch.SuperVoidHatch;

import gregtech.api.modularui2.GTGuiTextures;
import gregtech.api.modularui2.GTGuis;

public class SuperVoidHatchGui {

    private static final int SLOT_COLUMNS = 10;
    private static final int SLOT_ROWS = 10;

    private final SuperVoidHatch hatch;

    public SuperVoidHatchGui(SuperVoidHatch hatch) {
        this.hatch = hatch;
    }

    public ModularPanel build(PosGuiData guiData, PanelSyncManager syncManager, UISettings uiSettings) {
        return GTGuis.mteTemplatePanelBuilder(hatch, guiData, syncManager, uiSettings)
            .setWidth(hatch.getGUIWidth())
            .setHeight(hatch.getGUIHeight())
            .doesAddGregTechLogo(false)
            .doesBindPlayerInventory(false)
            .build()
            .child(createFilterScroll())
            .child(createLogo());
    }

    private IWidget createFilterScroll() {
        return new ListWidget<>().scrollDirection(new VerticalScrollData())
            .size(18 * SLOT_COLUMNS + 4, 72)
            .pos(20, 9)
            .child(createFilterGrid());
    }

    private IWidget createFilterGrid() {
        return new Grid().coverChildren()
            .gridOfWidthHeight(SLOT_COLUMNS, SLOT_ROWS, ($x, $y, index) -> createFluidFilterSlot(index));
    }

    private IWidget createFluidFilterSlot(int index) {
        return new FluidSlot().syncHandler(
            new FluidSlotSyncHandler(new LockedFluidFilter(index)).phantom(true)
                .controlsAmount(false))
            .background(GTGuiTextures.SLOT_FLUID_TANK)
            .tooltipDynamic(tooltip -> {
                FluidStack fluidStack = getFluidStack(index);
                if (fluidStack != null) {
                    tooltip.addFromFluid(fluidStack);
                } else {
                    tooltip.addLine(StatCollector.translateToLocal("gt.interact.desc.FluidFilter.Empty"));
                }
            });
    }

    private IWidget createLogo() {
        return GTNLMui2Textures.PICTURE_GTNL_LOGO.asWidget()
            .size(18)
            .pos(195, 83);
    }

    private FluidStack getFluidStack(int index) {
        String fluidName = hatch.getLockedFluidNames(index);
        if (fluidName == null || fluidName.isEmpty()) return null;
        return FluidRegistry.getFluidStack(fluidName, 1);
    }

    private class LockedFluidFilter implements IFluidTank {

        private final int index;

        private LockedFluidFilter(int index) {
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
            if (!hatch.acceptsFluidsLock(fluidName, index)) return 0;
            if (doFill) {
                hatch.setLockedFluidNames(index, fluidName);
                hatch.lockFluids(true, index);
            }
            return 1;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            FluidStack fluidStack = getFluid();
            if (fluidStack == null) return null;
            if (doDrain) {
                hatch.lockFluids(false, index);
            }
            return fluidStack;
        }
    }
}
