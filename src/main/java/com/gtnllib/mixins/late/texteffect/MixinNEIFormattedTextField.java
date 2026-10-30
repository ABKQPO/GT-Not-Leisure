package com.gtnllib.mixins.late.texteffect;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnllib.text.EffectTextParser;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import codechicken.nei.FormattedTextField;

/** Keeps editable declarations intact and shares the source-aware vanilla text field renderer. */
@Mixin(value = FormattedTextField.class, remap = false)
public abstract class MixinNEIFormattedTextField extends GuiTextField {

    public MixinNEIFormattedTextField(FontRenderer font, int x, int y, int width, int height) {
        super(font, x, y, width, height);
    }

    @WrapOperation(
        method = "setText",
        remap = true,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/EnumChatFormatting;getTextWithoutFormattingCodes(Ljava/lang/String;)Ljava/lang/String;",
            remap = true))
    private String gtnl$preserveEditableEffects(String p_110646_0_, Operation<String> original) {
        return EffectTextParser.containsMarkers(p_110646_0_) ? p_110646_0_ : original.call(p_110646_0_);
    }

    @Inject(method = "drawTextBox", remap = true, at = @At("HEAD"), cancellable = true)
    private void gtnl$drawEffectInput(CallbackInfo ci) {
        if (EffectTextParser.containsMarkers(getText())) {
            super.drawTextBox();
            ci.cancel();
        }
    }
}
