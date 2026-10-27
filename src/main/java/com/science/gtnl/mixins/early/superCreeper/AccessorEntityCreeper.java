package com.science.gtnl.mixins.early.superCreeper;

import net.minecraft.entity.monster.EntityCreeper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityCreeper.class)
public interface AccessorEntityCreeper {

    @Invoker("func_146077_cc")
    void callExplode();

    @Accessor("explosionRadius")
    int getExplosionRadius();

    @Accessor("explosionRadius")
    @Mutable
    void setExplosionRadius(int radius);
}
