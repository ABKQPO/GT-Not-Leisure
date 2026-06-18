package com.science.gtnl.mixins.early.Minecraft;

import java.util.Map;

import net.minecraft.util.StringTranslate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = StringTranslate.class, remap = true)
public interface AccessorStringTranslate {

    @Accessor("languageList")
    Map getLanguageList();

    @Accessor("instance")
    static StringTranslate getInstance() {
        throw new AssertionError();
    }
}
