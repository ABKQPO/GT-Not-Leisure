package com.science.gtnl.mixins.early.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import gregtech.nei.RecipeDisplayInfo;

@Mixin(value = RecipeDisplayInfo.class, remap = false)
public interface AccessorRecipeDisplayInfo {

    @Accessor("yPos")
    int getYPos();

    @Accessor("yPos")
    void setYPos(int value);

    @Accessor("neiTextColorOverride")
    int getNeiTextColorOverride();

    @Accessor("neiTextColorOverride")
    void setNeiTextColorOverride(int value);
}
