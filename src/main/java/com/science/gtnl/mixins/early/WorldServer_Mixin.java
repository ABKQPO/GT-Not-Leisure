package com.science.gtnl.mixins.early;

import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.item.TimeStopManager;

@SuppressWarnings("UnusedMixin")
@Mixin(WorldServer.class)
public abstract class WorldServer_Mixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onServerWorldTick(CallbackInfo ci) {
        if (TimeStopManager.isTimeStopped()) {
            ci.cancel();
        }
    }
}
