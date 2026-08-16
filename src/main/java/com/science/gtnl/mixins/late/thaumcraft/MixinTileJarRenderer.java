package com.science.gtnl.mixins.late.thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.tile.TileJarRenderer;

@Mixin(value = TileJarRenderer.class, remap = false)
public class MixinTileJarRenderer {

    @Redirect(
        method = "renderTileEntityAt",
        at = @At(
            value = "INVOKE",
            target = "Lthaumcraft/client/lib/UtilsFX;drawTag(IILthaumcraft/api/aspects/Aspect;)V"))
    private void gtnl$drawColorTag(int x, int y, Aspect aspect) {
        UtilsFX.drawTag(x, y, aspect, 0.0F, 0, 0.0D);
    }
}
