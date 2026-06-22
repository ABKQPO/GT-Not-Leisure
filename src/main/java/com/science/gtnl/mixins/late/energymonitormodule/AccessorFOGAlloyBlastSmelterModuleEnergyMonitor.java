package com.science.gtnl.mixins.late.energymonitormodule;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.science.gtnl.common.machine.multiblock.FOGAlloyBlastSmelterModule;

@Mixin(value = FOGAlloyBlastSmelterModule.class, remap = false)
public interface AccessorFOGAlloyBlastSmelterModuleEnergyMonitor {

    @Accessor("EUt")
    long gtnl$getEut();
}
