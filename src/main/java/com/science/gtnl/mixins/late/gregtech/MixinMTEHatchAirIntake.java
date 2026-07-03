package com.science.gtnl.mixins.late.gregtech;

import net.minecraftforge.fluids.Fluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.science.gtnl.common.material.GTNLMaterials;

import bartworks.system.material.WerkstoffLoader;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.MTEHatchAirIntake;

@Mixin(value = MTEHatchAirIntake.class, remap = false)
public class MixinMTEHatchAirIntake {

    @Inject(method = "getFluidToGenerate", at = @At("TAIL"), cancellable = true)
    private void injectEndAir(CallbackInfoReturnable<Fluid> cir, @Local(name = "id") int id) {
        if (id == 1) {
            cir.setReturnValue(WerkstoffLoader.fluids.get(GTNLMaterials.EnderAir));
        }
    }
}
