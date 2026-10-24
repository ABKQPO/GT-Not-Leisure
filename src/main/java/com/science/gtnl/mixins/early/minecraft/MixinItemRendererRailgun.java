package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.common.render.item.ItemElectromagneticRailgunRender;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRendererRailgun {

    // Why: Backhand resets the hand pose when a right click starts using an item, replaying the draw animation.
    // Keep idle items and the separate offhand renderer on their normal reset path.
    @Inject(method = { "resetEquippedProgress", "resetEquippedProgress2" }, at = @At("HEAD"), cancellable = true)
    private void gtnl$keepRailgunRaised(CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityRenderer view = minecraft.entityRenderer;
        if (view != null && ItemElectromagneticRailgunRender
            .keepsRaised((ItemRenderer) (Object) this, view.itemRenderer, minecraft.thePlayer)) ci.cancel();
    }
}
