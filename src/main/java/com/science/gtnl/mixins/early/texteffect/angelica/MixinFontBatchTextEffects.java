package com.science.gtnl.mixins.early.texteffect.angelica;

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.angelica.client.font.BatchingFontRenderer;
import com.gtnewhorizons.angelica.config.FontConfig;
import com.gtnewhorizons.angelica.glsm.GLStateManager;
import com.science.gtnl.client.text.DeferredTextEffects;
import com.science.gtnl.client.text.EffectTextRenderer;
import com.science.gtnl.client.text.compat.FontBatchBridge;
import com.science.gtnl.utils.text.effect.EffectTextParser;

/** Flushes the current batch before temporary render-target changes. */
@Mixin(value = BatchingFontRenderer.class, remap = false)
public abstract class MixinFontBatchTextEffects implements FontBatchBridge {

    @Shadow
    protected FontRenderer underlying;

    @Shadow
    private int batchDepth;

    @Shadow
    private static BatchingFontRenderer arenaOwner;

    @Shadow
    @Final
    private int fontShaderId;

    @Shadow
    @Final
    private int AAMode;

    @Shadow
    private int fontAAModeLast;

    @Shadow
    private void flushBatch() {}

    @Shadow
    public static void flushDeferredText() {}

    @Inject(method = "drawString", at = @At("HEAD"), cancellable = true)
    private void gtnl$directEffectText(float x, float y, int color, boolean shadow, boolean unicode,
        CharSequence source, int offset, int length, CallbackInfoReturnable<Float> cir) {
        if (EffectTextRenderer.isBypassingEffects() || source == null) return;
        int start = Math.max(0, Math.min(offset, source.length()));
        int end = start + Math.max(0, Math.min(length, source.length() - start));
        if (!EffectTextParser.containsMarkers(source, start, end)) return;
        String text = start == 0 && end == source.length() ? source.toString()
            : source.subSequence(start, end)
                .toString();
        boolean previousUnicode = underlying.getUnicodeFlag();
        underlying.setUnicodeFlag(unicode);
        try {
            EffectTextRenderer.INSTANCE.draw(underlying, text, x, y, color, shadow);
            cir.setReturnValue(
                x + EffectTextRenderer.INSTANCE.layout(underlying, text)
                    .width() + (shadow ? ((BatchingFontRenderer) (Object) this).getShadowOffset() : 0));
        } finally {
            underlying.setUnicodeFlag(previousUnicode);
        }
    }

    @Override
    public int gtnl$suspendBatch() {
        flushDeferredText();
        if (arenaOwner instanceof FontBatchBridge owner) owner.gtnl$flushBatch();
        flushBatch();
        int depth = batchDepth;
        batchDepth = 0;
        return depth;
    }

    @Inject(method = "flushDeferredText", at = @At("RETURN"))
    private static void gtnl$drawDeferredEffects(CallbackInfo ci) {
        DeferredTextEffects.flush();
    }

    @Override
    public void gtnl$resumeBatch(int depth) {
        batchDepth = depth;
    }

    @Override
    public void gtnl$flushBatch() {
        flushBatch();
    }

    @Inject(method = "shouldDeferNow", at = @At("HEAD"), cancellable = true)
    private static void gtnl$immediateMask(CallbackInfoReturnable<Boolean> cir) {
        if (EffectTextRenderer.isCapturing()) cir.setReturnValue(false);
    }

    @Inject(method = "shouldDrawThroughPipeline", at = @At("HEAD"), cancellable = true)
    private void gtnl$localMaskTarget(CallbackInfoReturnable<Boolean> cir) {
        if (EffectTextRenderer.isCapturing()) cir.setReturnValue(false);
    }

    @Redirect(
        method = { "flushBatchInner", "setupFontDrawState" },
        at = @At(value = "FIELD", target = "Lcom/gtnewhorizons/angelica/config/FontConfig;fontAAMode:I"),
        remap = false,
        require = 3)
    private int gtnl$sharpMaskCoverage() {
        // Keep the comparison, cached mode and uniform consistent across both Angelica method layouts.
        return EffectTextRenderer.isCapturing() ? 0 : FontConfig.fontAAMode;
    }

    @Inject(method = "flushBatch", at = @At("RETURN"))
    private void gtnl$restoreFontAntialiasing(CallbackInfo ci) {
        int configuredMode = FontConfig.fontAAMode;
        if (!EffectTextRenderer.isCapturing() || fontAAModeLast == configuredMode) return;
        // The shader is shared by all font instances, while Angelica caches its mode per instance.
        int previousProgram = GLStateManager.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        GLStateManager.glUseProgram(fontShaderId);
        try {
            GLStateManager.glUniform1i(AAMode, configuredMode);
            fontAAModeLast = configuredMode;
        } finally {
            GLStateManager.glUseProgram(previousProgram);
        }
    }
}
