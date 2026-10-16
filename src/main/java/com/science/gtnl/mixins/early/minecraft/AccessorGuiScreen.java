package com.science.gtnl.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiScreen.class)
public interface AccessorGuiScreen {

    @Invoker(value = "drawHoveringText", remap = false)
    void gtnl$drawHoveringText(List<?> lines, int x, int y, FontRenderer font);
}
