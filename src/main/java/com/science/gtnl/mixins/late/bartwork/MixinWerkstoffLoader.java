package com.science.gtnl.mixins.late.bartwork;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import bartworks.system.material.BWMetaGeneratedItems;
import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.OrePrefixes;

@Mixin(value = WerkstoffLoader.class, remap = false)
public abstract class MixinWerkstoffLoader {

    @Inject(
        method = "addItemsForGeneration()V",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/HashMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 41,
            remap = false))
    private static void injectSuperdenseAndNaniteItems(CallbackInfo ci) {
        WerkstoffLoader.items.put(OrePrefixes.plateSuperdense, new BWMetaGeneratedItems(OrePrefixes.plateSuperdense));
        WerkstoffLoader.items.put(OrePrefixes.nanite, new BWMetaGeneratedItems(OrePrefixes.nanite));
    }
}
