package com.science.gtnl.common.gui.modularui.widget;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.gtnewhorizons.modularui.common.widget.FluidNameHolderWidget;

import gregtech.api.interfaces.metatileentity.IFluidLockableMui2;

@Deprecated
public class FluidLockWidget extends FluidNameHolderWidget {

    // TODO: Replace this mui1 fluid name holder with a native mui2 fluid lock slot.
    public FluidLockWidget(IFluidLockableMui2 fluidLockable) {
        super(
            () -> fluidLockable.getLockedFluid() == null ? null
                : fluidLockable.getLockedFluid()
                    .getName(),
            name -> {
                Fluid fluid = name == null ? null : FluidRegistry.getFluid(name);
                if (fluid == null || fluidLockable.acceptsFluidLock(fluid)) {
                    fluidLockable.setLockedFluid(fluid);
                    fluidLockable.lockFluid(fluid != null);
                }
            });
    }
}
