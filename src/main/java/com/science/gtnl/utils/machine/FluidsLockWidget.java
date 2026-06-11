package com.science.gtnl.utils.machine;

import com.gtnewhorizons.modularui.common.widget.FluidNameHolderWidget;
import com.science.gtnl.api.IFluidsLockable;

@Deprecated
public class FluidsLockWidget extends FluidNameHolderWidget {

    // TODO: Replace this mui1 multi-fluid name holder with native mui2 phantom fluid slots.
    public FluidsLockWidget(IFluidsLockable fluidsLockable, int index) {
        super(() -> fluidsLockable.getLockedFluidNames(index), name -> {
            if (fluidsLockable.acceptsFluidsLock(name, index)) {
                fluidsLockable.setLockedFluidNames(index, name);
                fluidsLockable.lockFluids(name != null, index);
            }
        });
    }
}
