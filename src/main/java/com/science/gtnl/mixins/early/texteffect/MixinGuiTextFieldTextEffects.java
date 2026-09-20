package com.science.gtnl.mixins.early.texteffect;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.science.gtnl.client.text.EffectTextFieldView;
import com.science.gtnl.client.text.EffectTextLayout.Layout;
import com.science.gtnl.client.text.EffectTextRenderer;
import com.science.gtnl.utils.text.effect.EffectTextParser;

/** Retains formatting while vanilla scrolls and draws either side of an input cursor. */
@Mixin(GuiTextField.class)
public abstract class MixinGuiTextFieldTextEffects {

    @Shadow
    @Final
    private FontRenderer field_146211_a;

    @Shadow
    private String text;

    @Shadow
    private int lineScrollOffset;

    @Shadow
    private int cursorPosition;

    @Unique
    private EffectTextFieldView gtnl$effectView;

    @Unique
    private EffectTextFieldView gtnl$view() {
        if (!EffectTextParser.containsMarkers(text)) {
            gtnl$effectView = null;
            return null;
        }
        Layout layout = EffectTextRenderer.INSTANCE.layout(field_146211_a, text);
        if (gtnl$effectView == null || !gtnl$effectView.matches(text, layout))
            gtnl$effectView = new EffectTextFieldView(text, layout);
        return gtnl$effectView;
    }

    @WrapOperation(
        method = { "drawTextBox", "mouseClicked", "setSelectionPos" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;trimStringToWidth(Ljava/lang/String;I)Ljava/lang/String;"))
    private String gtnl$trimVisibleRange(FontRenderer font, String value, int width, Operation<String> original) {
        EffectTextFieldView view = gtnl$view();
        return view == null ? original.call(font, value, width)
            : view.trim(lineScrollOffset, lineScrollOffset + value.length(), width, false);
    }

    @WrapOperation(
        method = "setSelectionPos",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;trimStringToWidth(Ljava/lang/String;IZ)Ljava/lang/String;"))
    private String gtnl$trimScrollRange(FontRenderer font, String value, int width, boolean reverse,
        Operation<String> original) {
        EffectTextFieldView view = gtnl$view();
        return view == null ? original.call(font, value, width, reverse) : view.trim(0, value.length(), width, reverse);
    }

    @WrapOperation(
        method = "drawTextBox",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I",
            ordinal = 0))
    private int gtnl$drawBeforeCursor(FontRenderer font, String value, int x, int y, int color,
        Operation<Integer> original) {
        EffectTextFieldView view = gtnl$view();
        return original.call(
            font,
            view == null ? value : view.slice(lineScrollOffset, lineScrollOffset + value.length()),
            x,
            y,
            color);
    }

    @WrapOperation(
        method = "drawTextBox",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I",
            ordinal = 1))
    private int gtnl$drawAfterCursor(FontRenderer font, String value, int x, int y, int color,
        Operation<Integer> original) {
        EffectTextFieldView view = gtnl$view();
        return original.call(
            font,
            view == null ? value : view.slice(cursorPosition, cursorPosition + value.length()),
            x,
            y,
            color);
    }

    @WrapOperation(
        method = "drawTextBox",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I"))
    private int gtnl$measureSelection(FontRenderer font, String value, Operation<Integer> original) {
        EffectTextFieldView view = gtnl$view();
        return view == null ? original.call(font, value)
            : view.width(lineScrollOffset, lineScrollOffset + value.length());
    }
}
