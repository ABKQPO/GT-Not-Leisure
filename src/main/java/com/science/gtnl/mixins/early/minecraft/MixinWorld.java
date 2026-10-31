package com.science.gtnl.mixins.early.minecraft;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.common.tickrate.TimeStopHooks;

@Mixin(value = World.class, remap = true)
public abstract class MixinWorld {

    @Shadow
    protected List<Entity> unloadedEntityList;
    @Shadow
    protected IChunkProvider chunkProvider;

    @Inject(method = "updateEntities", at = @At("HEAD"), cancellable = true)
    public void mixin$updateEntities(CallbackInfo ci) {
        if (!TimeStopPocketWatch.isTimeStopped()) return;
        TimeStopHooks.updatePlayersOnly((World) (Object) this, unloadedEntityList, chunkProvider);
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void mixin$tick(CallbackInfo ci) {
        boolean isStop = TimeStopPocketWatch.isTimeStopped();
        if (isStop) {
            ci.cancel();
        }
    }
}
