package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gregtech.api.enums.OrePrefixes;

@Deprecated
@Mixin(value = OrePrefixes.class, remap = false)
public abstract class MixinOrePrefixes {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onClInitTail(CallbackInfo ci) {

    }
}
