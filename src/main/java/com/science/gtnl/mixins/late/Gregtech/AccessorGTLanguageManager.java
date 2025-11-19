package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import gregtech.api.util.GTLanguageManager;

@Mixin(value = GTLanguageManager.class, remap = false)
public interface AccessorGTLanguageManager {

    @Invoker("storeTranslation")
    static String callStoreTranslation(String trimmedKey, String english) {
        throw new AssertionError();
    }

    @Invoker("writeToLangFile")
    static String callWriteToLangFile(String trimmedKey, String aEnglish) {
        throw new AssertionError();
    }
}
