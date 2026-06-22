package com.science.gtnl.mixins.late.energymonitor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.machine.monitor.EnergyMonitorRegistry;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;

@Mixin(value = BaseMetaTileEntity.class, remap = false)
public class MixinBaseMetaTileEntityEnergyMonitor {

    @Inject(method = "invalidate", at = @At("HEAD"))
    private void gtnl$unregisterEnergyMonitorEntry(CallbackInfo callbackInfo) {
        MetaTileEntity metaTileEntity = gtnl$resolveTrackedMetaTileEntity();
        if (metaTileEntity != null) {
            EnergyMonitorRegistry.unregister(metaTileEntity);
        }
    }

    @Inject(method = "onUnload", at = @At("HEAD"))
    private void gtnl$unregisterEnergyMonitorEntryOnUnload(CallbackInfo callbackInfo) {
        MetaTileEntity metaTileEntity = gtnl$resolveTrackedMetaTileEntity();
        if (metaTileEntity != null) {
            EnergyMonitorRegistry.unregister(metaTileEntity);
        }
    }

    private MetaTileEntity gtnl$resolveTrackedMetaTileEntity() {
        if (!(this instanceof IGregTechTileEntity gregTechTileEntity)) {
            return null;
        }
        IMetaTileEntity metaTileEntity = gregTechTileEntity.getMetaTileEntity();
        return metaTileEntity instanceof MetaTileEntity trackedMetaTileEntity ? trackedMetaTileEntity : null;
    }
}
