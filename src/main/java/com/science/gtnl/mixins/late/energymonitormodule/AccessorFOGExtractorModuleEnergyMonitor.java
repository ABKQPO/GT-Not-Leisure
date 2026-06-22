package com.science.gtnl.mixins.late.energymonitormodule;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.science.gtnl.common.machine.multiblock.FOGExtractorModule;

@Mixin(value = FOGExtractorModule.class, remap = false)
public interface AccessorFOGExtractorModuleEnergyMonitor {

    @Accessor("EUt")
    long gtnl$getEut();
}
