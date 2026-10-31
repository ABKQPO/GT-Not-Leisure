package com.science.gtnl.common.machine.multiMachineBase;

import com.science.gtnl.common.machine.tooltip.GTNLMultiblockTooltipBuilder;

import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.util.MultiblockTooltipBuilder;

public abstract class EnhancedMultiMachineBase<T extends EnhancedMultiMachineBase<T>>
    extends MTEEnhancedMultiBlockBase<T> {

    public EnhancedMultiMachineBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public EnhancedMultiMachineBase(String aName) {
        super(aName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MultiblockTooltipBuilder createTooltip() {
        GTNLMultiblockTooltipBuilder tt = new GTNLMultiblockTooltipBuilder();
        tt.addAutoTooltip((T) this)
            .toolTipFinisher();
        return tt;
    }
}
