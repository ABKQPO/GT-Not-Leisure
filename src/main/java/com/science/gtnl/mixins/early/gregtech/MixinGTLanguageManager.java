package com.science.gtnl.mixins.early.gregtech;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.text.LanguageLoader;

import gregtech.api.util.GTLanguageManager;

@Mixin(value = GTLanguageManager.class, remap = false)
public class MixinGTLanguageManager {

    @Inject(method = "reloadLanguage", at = @At("HEAD"), cancellable = true)
    private static void gtnl$reloadLanguageWithEnglishWhenCurrentLanguageIsMissing(Map<String, String> languageMap,
        CallbackInfo ci) {
        if (LanguageLoader.reloadGTLanguageInEnglishIfNoneSelected(languageMap)) ci.cancel();
    }
}
