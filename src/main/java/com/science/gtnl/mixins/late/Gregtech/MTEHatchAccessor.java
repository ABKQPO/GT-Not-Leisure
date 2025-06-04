package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import gregtech.api.metatileentity.implementations.MTEHatch;

@SuppressWarnings("UnusedMixin")
@Mixin(value = MTEHatch.class, remap = false)
public interface MTEHatchAccessor {

    @Accessor("texturePage")
    int getTexturePage();

    @Accessor("texturePage")
    void setTexturePage(int texturePage);

    @Accessor("textureIndex")
    int getTextureIndex();

    @Accessor("textureIndex")
    void setTextureIndex(int textureIndex);
}
