package com.science.gtnl.mixins.late.assembler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.client.gui.widgets.GuiImgButton;

@Mixin(value = GuiMEMonitorable.class, remap = false)
public class MixinGuiMEMonitorable {

    @Shadow
    private GuiImgButton pinsStateButton;

    @Inject(method = "initGui", at = @At("TAIL"), remap = true)
    private void onInit(CallbackInfo ci) {
        this.pinsStateButton.yPosition += 20;
    }
}
