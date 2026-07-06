package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.science.gtnl.api.TickrateAPI;

@Mixin(value = MinecraftServer.class, remap = true)
public abstract class MixinMinecraftServer {

    @ModifyConstant(method = "run", constant = @Constant(longValue = 50L))
    long modifyTickrate(long constant) {
        return TickrateAPI.MILISECONDS_PER_TICK;
    }
}
