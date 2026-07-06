package com.science.gtnl.mixins.late.noNHU;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.science.gtnl.api.ITileEntityTickAcceleration;

import crazypants.enderio.machine.AbstractPoweredMachineEntity;
import crazypants.enderio.power.ICapacitor;

@Mixin(value = AbstractPoweredMachineEntity.class, remap = false)
public class MixinAbstractPoweredMachineEntity {

    @Redirect(
        method = "getMaxEnergyRecieved",
        at = @At(value = "INVOKE", target = "Lcrazypants/enderio/power/ICapacitor;getMaxEnergyReceived()I"))
    private int GTNL$modifyMaxEnergyReceivedValue(ICapacitor instance) {
        if (this instanceof ITileEntityTickAcceleration tileEntityITEA) {
            int tickAcceleratedRate = tileEntityITEA.getTickAcceleratedRate();
            return instance.getMaxEnergyReceived() * tickAcceleratedRate;
        }
        return instance.getMaxEnergyReceived();
    }
}
