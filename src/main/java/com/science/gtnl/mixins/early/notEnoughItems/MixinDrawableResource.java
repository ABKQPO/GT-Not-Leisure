package com.science.gtnl.mixins.early.notEnoughItems;

import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.api.mixinHelper.IDrawableResourceAccessor;
import com.science.gtnl.common.render.WrenchSpecialRender;

import codechicken.nei.Image;
import codechicken.nei.drawable.DrawableResource;

@Mixin(value = DrawableResource.class, remap = false)
public abstract class MixinDrawableResource extends Image
    implements IDrawableResourceAccessor, AccessorDrawableResource {

    @Unique
    private WrenchSpecialRender gtnl$specialRender = new WrenchSpecialRender();

    @Final
    @Shadow
    private ResourceLocation resourceLocation;
    @Final
    @Shadow
    private int textureWidth;
    @Final
    @Shadow
    private int textureHeight;

    @Final
    @Shadow
    private int paddingTop;
    @Final
    @Shadow
    private int paddingLeft;

    public MixinDrawableResource(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ResourceLocation resourceLocation, int u, int v, int width, int height, int paddingTop,
        int paddingBottom, int paddingLeft, int paddingRight, int textureWidth, int textureHeight, CallbackInfo ci) {}

    @Unique
    public void gtnl$draw(int xOffset, int yOffset, int special) {
        final int textureX = this.x;
        final int textureY = this.y;
        gtnl$specialRender.draw(
            xOffset,
            yOffset,
            special,
            resourceLocation,
            paddingLeft,
            paddingTop,
            textureX,
            textureY,
            width,
            height,
            textureWidth,
            textureHeight);
    }
}
