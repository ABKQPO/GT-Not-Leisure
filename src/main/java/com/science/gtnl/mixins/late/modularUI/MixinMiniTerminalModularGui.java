package com.science.gtnl.mixins.late.modularUI;

import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.modularui.common.internal.wrapper.ModularGui;
import com.science.gtnl.client.meminiterminal.MiniTerminalHostLayer;

@Mixin(value = ModularGui.class, remap = false)
public abstract class MixinMiniTerminalModularGui {

    @Inject(method = "shouldRenderOurTooltip", at = @At("HEAD"), cancellable = true)
    private void gtnl$ignoreHoverUnderMiniTerminal(CallbackInfoReturnable<Boolean> cir) {
        if (MiniTerminalHostLayer.mouseOverTerminal((GuiScreen) (Object) this)) cir.setReturnValue(false);
    }
}
