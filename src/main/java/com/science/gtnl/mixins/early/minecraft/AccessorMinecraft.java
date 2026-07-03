package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Minecraft.class, remap = true)
public interface AccessorMinecraft {

    @Accessor("timer")
    Timer getTimer();

    @Accessor("timer")
    void setTimer(Timer textField);
}
