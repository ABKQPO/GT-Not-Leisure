package com.science.gtnl.mixins.early.superCreeper;

import net.minecraft.entity.monster.EntityCreeper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityCreeper.class)
public interface AccessorEntityCreeper {

    @Accessor("explosionRadius")
    int getExplosionRadius();

    @Accessor("explosionRadius")
    @Mutable
    void setExplosionRadius(int radius);
}
