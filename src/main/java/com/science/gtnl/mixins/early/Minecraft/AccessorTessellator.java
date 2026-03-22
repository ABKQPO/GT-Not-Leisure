package com.science.gtnl.mixins.early.Minecraft;

import net.minecraft.client.renderer.Tessellator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Tessellator.class)
public interface AccessorTessellator {

    @Accessor("isDrawing")
    boolean getIsDrawing();
}
