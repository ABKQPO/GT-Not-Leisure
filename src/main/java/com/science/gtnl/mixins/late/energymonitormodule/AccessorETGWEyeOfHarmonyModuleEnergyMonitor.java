package com.science.gtnl.mixins.late.energymonitormodule;

import java.math.BigInteger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.ETGWEyeOfHarmonyModule;

@Mixin(value = ETGWEyeOfHarmonyModule.class, remap = false)
public interface AccessorETGWEyeOfHarmonyModuleEnergyMonitor {

    @Accessor("usedEU")
    BigInteger gtnl$getUsedEU();
}
