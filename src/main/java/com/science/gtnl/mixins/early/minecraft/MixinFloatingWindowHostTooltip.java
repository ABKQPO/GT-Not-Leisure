package com.science.gtnl.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.client.floatingwindow.FloatingWindowLayer;

@Mixin(value = GuiScreen.class, priority = 900)
public abstract class MixinFloatingWindowHostTooltip {

    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    private void gtnl$dropFloatingWindowHostItemTooltip(ItemStack stack, int x, int y, CallbackInfo ci) {
        if (FloatingWindowLayer.dropTooltip((GuiScreen) (Object) this, x, y)) ci.cancel();
    }

    @Inject(method = "drawHoveringText", at = @At("HEAD"), cancellable = true, remap = false)
    private void gtnl$deferFloatingWindowHostTooltip(List<?> lines, int x, int y, FontRenderer font, CallbackInfo ci) {
        if (FloatingWindowLayer.deferTooltip((GuiScreen) (Object) this, lines, x, y, font)) ci.cancel();
    }
}
