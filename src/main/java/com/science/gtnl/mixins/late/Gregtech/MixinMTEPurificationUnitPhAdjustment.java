package com.science.gtnl.mixins.late.Gregtech;

import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputHatch;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;

import gregtech.api.interfaces.IHatchElement;
import gregtech.common.tileentities.machines.multi.purification.MTEPurificationUnitPhAdjustment;

@Mixin(value = MTEPurificationUnitPhAdjustment.class, remap = false)
public class MixinMTEPurificationUnitPhAdjustment {

    @Inject(method = "getAllowedHatches", at = @At("HEAD"), cancellable = true)
    private void injectCustomHatches(
        CallbackInfoReturnable<List<IHatchElement<? super MTEPurificationUnitPhAdjustment>>> cir) {
        cir.setReturnValue(ImmutableList.of(InputHatch, OutputHatch, InputBus));
    }
}
