package com.science.gtnl.mixins.late.AprilFool;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.render.tile.BallRenderer;

import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.common.render.BaseMetaTileEntityRenderer;

@Mixin(value = BaseMetaTileEntityRenderer.class, remap = false)
public class MixinBaseMetaTileEntityRenderer {

    @Inject(method = "renderTileEntityAt", at = @At("HEAD"), cancellable = true)
    private void onPostTick(TileEntity te, double x, double y, double z, float timeSinceLastTick, CallbackInfo ci) {
        if (!(te instanceof BaseMetaTileEntity baseTE)) return;
        BallRenderer.renderTileEntity(baseTE.getMetaTileEntity(), x, y, z, timeSinceLastTick);
        ci.cancel();
    }
}
