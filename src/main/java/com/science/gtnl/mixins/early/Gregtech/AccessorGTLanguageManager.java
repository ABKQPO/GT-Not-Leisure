package com.science.gtnl.mixins.early.Gregtech;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import gregtech.api.util.GTLanguageManager;

@Mixin(value = GTLanguageManager.class, remap = false)
public interface AccessorGTLanguageManager {

    @Invoker("writeToLangFile")
    static String callWriteToLangFile(String trimmedKey, String aEnglish) {
        throw new AssertionError();
    }

    @Accessor("TEMPMAP")
    static HashMap<String, String> getTempMap() {
        throw new AssertionError();
    }

    @Accessor("LANGMAP")
    static Map<String, String> getLangMap() {
        throw new AssertionError();
    }

    @Accessor("hasUnsavedEntry")
    static boolean getHasUnsavedEntry() {
        throw new AssertionError();
    }

    @Accessor("hasUnsavedEntry")
    static void setHasUnsavedEntry(boolean value) {
        throw new AssertionError();
    }

    @Invoker("markFileDirty")
    static void callMarkFileDirty() {
        throw new AssertionError();
    }
}
