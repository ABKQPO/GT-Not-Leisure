package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchSteamBusOutput;

@Mixin(value = MTEHatchSteamBusOutput.class, remap = false)
public abstract class MixinMTEHatchSteamBusOutput extends MTEHatchOutputBus {

    public MixinMTEHatchSteamBusOutput(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    @Override
    public boolean pushOutputInventory() {
        return true;
    }

    @Override
    public boolean isFiltered() {
        return isLocked();
    }

    @Unique
    @Override
    public boolean isFilteredToItem(GTUtility.ItemId id) {
        if (lockedItem == null) return false;

        return id.matches(lockedItem);
    }
}
