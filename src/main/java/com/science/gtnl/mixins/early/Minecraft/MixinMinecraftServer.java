package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.science.gtnl.asm.GTNLEarlyCoreMod;

@Mixin(value = MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @ModifyConstant(method = "run", constant = @Constant(longValue = 50L))
    long modifyTickrate(long constant) {
        return GTNLEarlyCoreMod.MILISECONDS_PER_TICK;
    }
}
