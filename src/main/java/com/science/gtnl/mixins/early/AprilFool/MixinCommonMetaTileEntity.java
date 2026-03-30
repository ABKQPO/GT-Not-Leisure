package com.science.gtnl.mixins.early.AprilFool;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.api.IRenderAngle;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.CommonMetaTileEntity;
import gregtech.api.render.ISBRWorldContext;
import lombok.Getter;
import lombok.Setter;

@Mixin(value = CommonMetaTileEntity.class, remap = false)
public class MixinCommonMetaTileEntity implements IRenderAngle {

    @Getter
    @Setter
    public double renderAngle = 0f;

    @Inject(method = "onPostTick", at = @At("HEAD"))
    private void onPostTick(IGregTechTileEntity baseMetaTileEntity, long tick, CallbackInfo ci) {
        renderAngle++;
    }

    @Inject(method = "renderInWorld", at = @At("HEAD"), cancellable = true)
    private void renderInWorld(ISBRWorldContext ctx, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
