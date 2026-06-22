package com.science.gtnl.mixins.late.energymonitor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.monitor.EnergyMonitorRegistry;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.CommonMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachine;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

@Mixin(value = CommonMetaTileEntity.class, remap = false)
public class MixinCommonMetaTileEntityEnergyMonitor {

    @Inject(method = "onFirstTick", at = @At("TAIL"))
    private void gtnl$registerEnergyMonitorEntry(IGregTechTileEntity baseMetaTileEntity, CallbackInfo callbackInfo) {
        MetaTileEntity metaTileEntity = gtnl$resolveTrackedMetaTileEntity();
        if (metaTileEntity != null) {
            EnergyMonitorRegistry.register(metaTileEntity);
        }
    }

    @Inject(method = "onRemoval", at = @At("HEAD"))
    private void gtnl$unregisterEnergyMonitorEntry(CallbackInfo callbackInfo) {
        MetaTileEntity metaTileEntity = gtnl$resolveTrackedMetaTileEntity();
        if (metaTileEntity != null) {
            EnergyMonitorRegistry.unregister(metaTileEntity);
        }
    }

    private MetaTileEntity gtnl$resolveTrackedMetaTileEntity() {
        Object self = this;
        if (self instanceof MTEBasicMachine basicMachine) {
            return basicMachine;
        }
        if (self instanceof MTEHatch hatch) {
            return hatch;
        }
        if (self instanceof MTEMultiBlockBase multiBlockBase) {
            return multiBlockBase;
        }
        return null;
    }
}
