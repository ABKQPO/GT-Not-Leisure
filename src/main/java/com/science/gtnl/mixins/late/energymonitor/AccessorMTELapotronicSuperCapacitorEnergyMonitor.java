package com.science.gtnl.mixins.late.energymonitor;

import java.math.BigInteger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import kekztech.common.tileentities.MTELapotronicSuperCapacitor;

@Mixin(value = MTELapotronicSuperCapacitor.class, remap = false)
public interface AccessorMTELapotronicSuperCapacitorEnergyMonitor {

    @Accessor("capacity")
    BigInteger gtnl$getCapacity();

    @Accessor("stored")
    BigInteger gtnl$getStored();

    @Accessor("inputLastTick")
    long gtnl$getInputLastTick();

    @Accessor("outputLastTick")
    long gtnl$getOutputLastTick();

    @Accessor("wireless_mode")
    boolean gtnl$isWirelessMode();
}
