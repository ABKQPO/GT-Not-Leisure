package com.science.gtnl.mixins.late.modularUI2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.theme.ThemeAPI;
import com.science.gtnllib.gui.theme.PaletteThemes;

@Mixin(value = ThemeAPI.class, remap = false)
public abstract class MixinThemeAPI {

    @Inject(
        method = "getThemeForScreen(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/cleanroommc/modularui/api/ITheme;",
        at = @At("RETURN"),
        cancellable = true)
    private void gtnl$livePaletteTheme(CallbackInfoReturnable<ITheme> cir) {
        cir.setReturnValue(PaletteThemes.forScreen(cir.getReturnValue()));
    }
}
