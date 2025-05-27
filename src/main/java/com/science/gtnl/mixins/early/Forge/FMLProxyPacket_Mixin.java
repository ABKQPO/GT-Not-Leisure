package com.science.gtnl.mixins.early.Forge;

import javax.annotation.Nullable;

import net.minecraft.network.INetHandler;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

@SuppressWarnings("UnusedMixin")
@Mixin(value = FMLProxyPacket.class, remap = false)
public class FMLProxyPacket_Mixin {

    @Shadow
    @Nullable
    private NetworkDispatcher dispatcher;

    @Shadow
    private String channel;

    @Inject(
        method = "processPacket",
        at = @At(
            value = "INVOKE",
            target = "Lcpw/mods/fml/common/network/handshake/NetworkDispatcher;rejectHandshake(Ljava/lang/String;)V"),
        cancellable = true)
    private void onRejectHandshake(INetHandler inethandler, CallbackInfo ci) {
        if (dispatcher == null) {
            FMLLog.log(Level.ERROR, "Attempted to reject handshake, but dispatcher is null (channel: %s)", channel);
            ci.cancel();
        }
    }
}
