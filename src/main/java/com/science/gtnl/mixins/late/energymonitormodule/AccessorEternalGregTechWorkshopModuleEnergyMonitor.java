package com.science.gtnl.mixins.late.energymonitormodule;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.EternalGregTechWorkshopModule;

@Mixin(value = EternalGregTechWorkshopModule.class, remap = false)
public interface AccessorEternalGregTechWorkshopModuleEnergyMonitor {

    @Accessor("EUt")
    long gtnl$getEut();
}
