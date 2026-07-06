package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;

import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;

@Mixin(value = MTEHatchSteamBusOutput.class)
public abstract class MixinMTEHatchSteamBusOutput extends MTEHatchOutputBus {

    public MixinMTEHatchSteamBusOutput(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    @Override
    public boolean isFilteredToItem(GTUtility.ItemId id) {
        if (lockedItem == null) return false;

        return id.matches(lockedItem);
    }
}
