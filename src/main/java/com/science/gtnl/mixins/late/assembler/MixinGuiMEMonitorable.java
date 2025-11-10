package com.science.gtnl.mixins.late.assembler;

import org.spongepowered.asm.mixin.Final;
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

    @Shadow
    @Final
    public boolean hasPinHost;

    @Inject(method = "initGui", at = @At("RETURN"), remap = true)
    private void onInit(CallbackInfo ci) {
        if (hasPinHost) {
            this.pinsStateButton.yPosition += 25;
        }
    }
}
