package com.science.gtnl.mixins.early.notEnoughItems;

import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import codechicken.nei.drawable.DrawableBuilder;

@Mixin(value = DrawableBuilder.class, remap = false)
public interface AccessorDrawableBuilder {

    @Accessor("resourceLocation")
    ResourceLocation getResourceLocation();

    @Accessor("u")
    int getU();

    @Accessor("u")
    void setU(int u);

    @Accessor("v")
    int getV();

    @Accessor("v")
    void setV(int v);

    @Accessor("width")
    int getWidth();

    @Accessor("width")
    void setWidth(int width);

    @Accessor("height")
    int getHeight();

    @Accessor("height")
    void setHeight(int height);

    @Accessor("textureWidth")
    int getTextureWidth();

    @Accessor("textureWidth")
    void setTextureWidth(int textureWidth);

    @Accessor("textureHeight")
    int getTextureHeight();

    @Accessor("textureHeight")
    void setTextureHeight(int textureHeight);

    @Accessor("paddingTop")
    int getPaddingTop();

    @Accessor("paddingTop")
    void setPaddingTop(int paddingTop);

    @Accessor("paddingBottom")
    int getPaddingBottom();

    @Accessor("paddingBottom")
    void setPaddingBottom(int paddingBottom);

    @Accessor("paddingLeft")
    int getPaddingLeft();

    @Accessor("paddingLeft")
    void setPaddingLeft(int paddingLeft);

    @Accessor("paddingRight")
    int getPaddingRight();

    @Accessor("paddingRight")
    void setPaddingRight(int paddingRight);
}
