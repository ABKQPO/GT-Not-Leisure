package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.reavaritia.client.render.CustomEntityRenderer;
import com.science.gtnl.client.tickrate.TimeStopClientHooks;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.utils.ClientUtils;

@Mixin(value = Minecraft.class, remap = true)
public abstract class MixinMinecraft {

    @Shadow
    public EntityClientPlayerMP thePlayer;
    @Shadow
    public WorldClient theWorld;

    @Redirect(
        method = "startGame",
        at = @At(value = "NEW", target = "Lnet/minecraft/client/renderer/EntityRenderer;", ordinal = 0))
    private EntityRenderer redirectEntityRenderer(Minecraft mc, IResourceManager resourceManager) {
        return new CustomEntityRenderer(mc, resourceManager);
    }

    @Inject(method = "runTick", at = @At("HEAD"), cancellable = true)
    private void mixin$runTick(CallbackInfo ci) {
        if (!TimeStopPocketWatch.isTimeStopped()) return;
        ci.cancel();
        TimeStopClientHooks.runTick((Minecraft) (Object) this);
    }

    @Inject(method = "runGameLoop", at = @At("HEAD"), cancellable = true)
    private void mixin$runGameLoop(CallbackInfo ci) {
        if (!TimeStopPocketWatch.isTimeStopped()) return;
        ci.cancel();
        TimeStopClientHooks.runGameLoop((Minecraft) (Object) this);
    }

    @Inject(
        method = "func_147112_ai",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/ForgeHooks;onPickBlock(Lnet/minecraft/util/MovingObjectPosition;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;)Z"),
        cancellable = true,
        remap = false)
    private void onBeforePickBlock(CallbackInfo ci) {
        if (ClientUtils.onBeforePickBlock(this.thePlayer, this.theWorld, false)) {
            ci.cancel();
        }
    }
}
