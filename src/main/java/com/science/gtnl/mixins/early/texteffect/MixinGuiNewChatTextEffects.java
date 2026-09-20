package com.science.gtnl.mixins.early.texteffect;

import net.minecraft.client.gui.GuiNewChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.science.gtnl.client.text.EffectTextLayout;
import com.science.gtnl.utils.text.effect.EffectTextParser;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChatTextEffects {

    @WrapOperation(
        method = "func_146237_a",
        at = @At(value = "INVOKE", target = "Ljava/lang/String;substring(I)Ljava/lang/String;", remap = false))
    private String gtnl$continueEffect(String text, int start, Operation<String> original) {
        String suffix = original.call(text, start);
        if (suffix.isEmpty() || !EffectTextParser.containsMarkers(text)) return suffix;
        return EffectTextLayout.continuation(text.substring(0, start)) + suffix;
    }

    @WrapOperation(
        method = "func_146237_a",
        at = @At(value = "INVOKE", target = "Ljava/lang/String;lastIndexOf(Ljava/lang/String;)I", remap = false))
    private int gtnl$visibleWordBoundary(String text, String delimiter, Operation<Integer> original) {
        return delimiter.equals(" ") && EffectTextParser.containsMarkers(text) ? EffectTextLayout.lastSpace(text)
            : original.call(text, delimiter);
    }
}
