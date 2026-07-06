package com.science.gtnl.mixins.early.gregtech;

import java.io.File;
import java.util.Map;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import gregtech.api.GregTechAPI;
import gregtech.api.util.GTLanguageManager;

@Mixin(value = GTLanguageManager.class, remap = false)
public class MixinGTLanguageManager {

    @Inject(method = "reloadLanguage", at = @At("HEAD"), cancellable = true)
    private static void gtnl$reloadLanguageWithEnglishWhenCurrentLanguageIsMissing(Map<String, String> languageMap,
        CallbackInfo ci) {
        if (!GregTechAPI.sFullLoadFinished || GTLanguageManager.sEnglishFile == null) return;

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.getLanguageManager() == null
            || minecraft.getLanguageManager()
                .getCurrentLanguage() != null) {
            return;
        }

        File configFile = GTLanguageManager.sEnglishFile.getConfigFile();
        if (configFile == null) {
            ci.cancel();
            return;
        }

        GTLanguageManager.LanguageCode = "en_US";
        GTLanguageManager.reloadLanguageWithEnglish(configFile.getParentFile(), languageMap);
        ci.cancel();
    }
}
