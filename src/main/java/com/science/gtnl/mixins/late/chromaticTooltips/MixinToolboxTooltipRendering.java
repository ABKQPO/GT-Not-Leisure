package com.science.gtnl.mixins.late.chromaticTooltips;

import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.science.gtnl.client.metoolbox.ToolboxClient;

@Pseudo
@Mixin(targets = "com.slprime.chromatictooltips.ClientProxy", remap = false)
public abstract class MixinToolboxTooltipRendering {

    @WrapOperation(
        method = "onScreenPostDraw",
        at = @At(value = "INVOKE", target = "Lcom/slprime/chromatictooltips/TooltipHandler;drawLastTooltip()V"),
        require = 1)
    private void gtnl$deferToolboxTooltip(Operation<Void> original, DrawScreenEvent.Post event) {
        if (!ToolboxClient.deferChromaticTooltip(event, () -> original.call())) original.call();
    }
}
