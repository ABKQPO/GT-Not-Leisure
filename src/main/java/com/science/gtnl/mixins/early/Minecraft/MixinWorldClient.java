package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.item.items.TimeStopPocketWatch;

@Mixin(value = WorldClient.class, remap = true)
public class MixinWorldClient {

    @Shadow
    private NetHandlerPlayClient sendQueue;
    @Shadow
    private ChunkProviderClient clientChunkProvider;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void mixin$tick(CallbackInfo ci) {
        boolean isStop = TimeStopPocketWatch.isTimeStopped();
        if (isStop) {
            ci.cancel();
        } else return;

        ((WorldClient) ((Object) this)).theProfiler.startSection("reEntryProcessing");
        ((WorldClient) ((Object) this)).theProfiler.endStartSection("connection");
        sendQueue.onNetworkTick();
        ((WorldClient) ((Object) this)).theProfiler.endStartSection("chunkCache");
        clientChunkProvider.unloadQueuedChunks();
        ((WorldClient) ((Object) this)).theProfiler.endStartSection("blocks");
        ((WorldClient) ((Object) this)).func_147456_g();
        ((WorldClient) ((Object) this)).theProfiler.endSection();
    }
}
