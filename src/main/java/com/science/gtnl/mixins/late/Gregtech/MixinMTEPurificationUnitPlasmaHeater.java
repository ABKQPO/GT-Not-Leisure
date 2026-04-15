package com.science.gtnl.mixins.late.Gregtech;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;

import gregtech.api.enums.HatchElement;
import gregtech.api.interfaces.IHatchElement;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitPlasmaHeater;

@Mixin(value = MTEPurificationUnitPlasmaHeater.class, remap = false)
public class MixinMTEPurificationUnitPlasmaHeater {

    @Inject(method = "getAllowedHatches", at = @At("HEAD"), cancellable = true)
    private void injectCustomHatches(
        CallbackInfoReturnable<List<IHatchElement<? super MTEPurificationUnitPlasmaHeater>>> cir) {
        cir.setReturnValue(ImmutableList.of(HatchElement.InputHatch, HatchElement.OutputHatch, HatchElement.InputBus));
    }
}
