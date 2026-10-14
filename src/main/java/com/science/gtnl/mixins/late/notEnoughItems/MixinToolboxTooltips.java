package com.science.gtnl.mixins.late.notEnoughItems;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.client.metoolbox.ToolboxClient;
import com.science.gtnl.client.metoolbox.ToolboxInventoryPanel;

import codechicken.nei.guihook.GuiContainerManager;

@Mixin(value = GuiContainerManager.class, priority = 1100, remap = false)
public abstract class MixinToolboxTooltips {

    @Shadow
    public GuiContainer window;

    @Inject(method = "renderToolTips", at = @At("HEAD"), cancellable = true)
    private void gtnl$deferToolboxTooltips(int mouseX, int mouseY, CallbackInfo ci) {
        ToolboxInventoryPanel overlay = ToolboxClient.overlay();
        if (overlay != null && window == overlay.inventory()
            && overlay.deferNativeTooltip()) ci.cancel();
    }
}
