package com.science.gtnl.mixins.late.energymonitormodule;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.science.gtnl.common.machine.multiblock.FOGSolarMuonCatalystModule;

@Mixin(value = FOGSolarMuonCatalystModule.class, remap = false)
public interface AccessorFOGSolarMuonCatalystModuleEnergyMonitor {

    @Accessor("EUt")
    long gtnl$getEut();
}
