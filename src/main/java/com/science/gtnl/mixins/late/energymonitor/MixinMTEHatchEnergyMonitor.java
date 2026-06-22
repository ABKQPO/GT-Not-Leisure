package com.science.gtnl.mixins.late.energymonitor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.monitor.EnergyMonitorRegistry;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;

@Mixin(value = MTEHatch.class, remap = false)
public class MixinMTEHatchEnergyMonitor {

    @Inject(method = "onFirstTick", at = @At("TAIL"))
    private void gtnl$registerEnergyMonitorEntry(IGregTechTileEntity baseMetaTileEntity, CallbackInfo callbackInfo) {
        EnergyMonitorRegistry.register((MTEHatch) (Object) this);
    }

    @Inject(method = "onRemoval", at = @At("HEAD"))
    private void gtnl$unregisterEnergyMonitorEntry(CallbackInfo callbackInfo) {
        EnergyMonitorRegistry.unregister((MTEHatch) (Object) this);
    }
}
