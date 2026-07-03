package com.science.gtnl.mixins.early.minecraft;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = EntityLivingBase.class, remap = true)
public interface AccessorEntityLivingBase {

    @Accessor("isJumping")
    boolean getJumping();

    @Accessor("isJumping")
    @Mutable
    void setJumping(boolean jumping);
}
