package com.science.gtnl.mixins.late.energymonitormodule;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.science.gtnl.common.machine.multiblock.FOGAlloySmelterModule;

@Mixin(value = FOGAlloySmelterModule.class, remap = false)
public interface AccessorFOGAlloySmelterModuleEnergyMonitor {

    @Accessor("EUt")
    long gtnl$getEut();
}
