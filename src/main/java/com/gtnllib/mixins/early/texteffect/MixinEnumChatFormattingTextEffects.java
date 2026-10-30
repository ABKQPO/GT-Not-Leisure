package com.gtnllib.mixins.early.texteffect;

import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.gtnllib.text.EffectTextParser;
import com.gtnllib.text.TextEffects;

/** Removes effect declarations wherever vanilla explicitly requests unformatted text. */
@Mixin(EnumChatFormatting.class)
public abstract class MixinEnumChatFormattingTextEffects {

    @ModifyVariable(method = "getTextWithoutFormattingCodes", at = @At("HEAD"), argsOnly = true)
    private static String gtnl$stripEffects(String text) {
        return EffectTextParser.containsMarkers(text) ? TextEffects.plainText(text) : text;
    }
}
