package com.science.gtnl.mixins.late.bartwork;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.OrePrefixes;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

@Mixin(value = Werkstoff.GenerationFeatures.class, remap = false)
public abstract class MixinWerkstoff {

    @Accessor(remap = false)
    public static Object2IntOpenHashMap<OrePrefixes> getPrefixLogic() {
        throw new AssertionError();
    }

    @Inject(method = "initPrefixLogic", at = @At("TAIL"), remap = false)
    private static void injectAdditionalPrefixLogic(CallbackInfo ci) {
        Object2IntOpenHashMap<OrePrefixes> prefixLogic = getPrefixLogic();
        prefixLogic.put(OrePrefixes.plateSuperdense, 0x200); // TODO: Remove this in 2.9.0-beta2
        prefixLogic.put(OrePrefixes.nanite, 0x200);
    }
}
