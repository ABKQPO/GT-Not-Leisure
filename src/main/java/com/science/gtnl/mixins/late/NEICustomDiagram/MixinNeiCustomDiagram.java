package com.science.gtnl.mixins.late.NEICustomDiagram;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.dcysteine.neicustomdiagram.main.NeiCustomDiagram;

import cpw.mods.fml.common.event.FMLLoadCompleteEvent;

@Mixin(value = NeiCustomDiagram.class, remap = false)
public class MixinNeiCustomDiagram {

    @Inject(method = "onLoadComplete", at = @At("HEAD"), cancellable = true)
    public void onLoadComplete(FMLLoadCompleteEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
