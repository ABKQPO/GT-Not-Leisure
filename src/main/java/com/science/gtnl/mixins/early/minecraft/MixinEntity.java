package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.entity.GlitchEntityCrashInfo;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;

import Forge.NullPointerException;

@Mixin(value = Entity.class, remap = true)
public class MixinEntity {

    @Inject(method = "moveEntity", at = @At("HEAD"), cancellable = true)
    public void mixin$moveEntity(double x, double y, double z, CallbackInfo ci) {
        if (TimeStopPocketWatch.isTimeStopped() && !(((Entity) ((Object) this)) instanceof EntityPlayer)) {
            ci.cancel();
        }
    }

    @Inject(method = "addEntityCrashInfo", at = @At("HEAD"), cancellable = true)
    public void addEntityCrashInfo(CrashReportCategory category, CallbackInfo ci) {
        if ((Object) this instanceof NullPointerException) {
            GlitchEntityCrashInfo.write(category);
            ci.cancel();
        }
    }
}
