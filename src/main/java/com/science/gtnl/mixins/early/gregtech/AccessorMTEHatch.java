package com.science.gtnl.mixins.early.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import gregtech.api.metatileentity.implementations.MTEHatch;

@Mixin(value = MTEHatch.class, remap = false)
public interface AccessorMTEHatch {

    @Accessor("texturePage")
    int getTexturePage();

    @Accessor("texturePage")
    void setTexturePage(int texturePage);

    @Accessor("textureIndex")
    int getTextureIndex();

    @Accessor("textureIndex")
    void setTextureIndex(int textureIndex);
}
