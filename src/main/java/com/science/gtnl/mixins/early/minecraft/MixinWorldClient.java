package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.client.tickrate.TimeStopClientHooks;

@Mixin(value = WorldClient.class, remap = true)
public class MixinWorldClient {

    @Shadow
    private NetHandlerPlayClient sendQueue;
    @Shadow
    private ChunkProviderClient clientChunkProvider;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void mixin$tick(CallbackInfo ci) {
        if (TimeStopClientHooks.tickStoppedWorld((WorldClient) (Object) this, sendQueue, clientChunkProvider))
            ci.cancel();
    }
}
