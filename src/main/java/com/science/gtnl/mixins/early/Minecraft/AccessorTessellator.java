package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.client.renderer.Tessellator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Tessellator.class, remap = true)
public interface AccessorTessellator {

    @Accessor("isDrawing")
    boolean getIsDrawing();
}
