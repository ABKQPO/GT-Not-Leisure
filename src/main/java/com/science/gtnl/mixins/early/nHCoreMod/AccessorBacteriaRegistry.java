package com.science.gtnl.mixins.early.nHCoreMod;

import java.util.LinkedHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.dreammaster.bartworksHandler.BacteriaRegistry;

import bartworks.util.BioCulture;

@Mixin(value = BacteriaRegistry.class, remap = false)
public interface AccessorBacteriaRegistry {

    @Accessor("CultureSet")
    static LinkedHashMap<String, BioCulture> getCultureSet() {
        throw new AssertionError();
    }
}
