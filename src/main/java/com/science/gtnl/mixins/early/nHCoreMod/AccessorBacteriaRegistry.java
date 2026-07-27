package com.science.gtnl.mixins.early.nHCoreMod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.dreammaster.bartworksHandler.BacteriaRegistry;

@Mixin(value = BacteriaRegistry.class, remap = false)
public interface AccessorBacteriaRegistry {

    @Invoker("runAllPostinit")
    static void invokeRunAllPostinit() {
        throw new AssertionError();
    }
}
