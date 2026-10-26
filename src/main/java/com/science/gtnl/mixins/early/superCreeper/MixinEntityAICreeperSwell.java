package com.science.gtnl.mixins.early.superCreeper;

import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.monster.EntityCreeper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.supercreeper.SuperCreeperHooks;

@Mixin(EntityAICreeperSwell.class)
public abstract class MixinEntityAICreeperSwell {

    @Shadow
    public EntityCreeper swellingCreeper;

    @Unique
    private final SuperCreeperHooks.State gtnl$state = new SuperCreeperHooks.State();

    @Inject(method = "shouldExecute", at = @At("HEAD"), cancellable = true)
    public void shouldExecute(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(SuperCreeperHooks.shouldExecute(swellingCreeper, gtnl$state));
    }

    @Inject(method = "resetTask", at = @At("HEAD"))
    public void resetTask(CallbackInfo ci) {
        SuperCreeperHooks.resetTask(gtnl$state);
    }

    @Inject(method = "updateTask", at = @At("HEAD"), cancellable = true)
    public void updateTask(CallbackInfo ci) {
        if (SuperCreeperHooks.updateTask(swellingCreeper, gtnl$state)) ci.cancel();
    }
}
