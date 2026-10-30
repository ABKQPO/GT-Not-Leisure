package com.gtnllib.mixins.early.texteffect.angelica;

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.angelica.client.font.BatchingFontRenderer;
import com.gtnewhorizons.angelica.config.FontConfig;
import com.gtnewhorizons.angelica.glsm.GLStateManager;
import com.gtnllib.client.text.DeferredTextEffects;
import com.gtnllib.client.text.EffectTextRenderer;
import com.gtnllib.client.text.compat.AngelicaTextAdapter;
import com.gtnllib.client.text.compat.FontBatchBridge;

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
    private void gtnl$directEffectText(float anchorX, float anchorY, int inputColor, boolean enableShadow,
        boolean unicodeFlag, CharSequence string, int stringOffset, int stringLength,
        CallbackInfoReturnable<Float> cir) {
        String text = EffectTextRenderer.markedText(string, stringOffset, stringLength);
        if (text == null) return;
        float width = EffectTextRenderer.INSTANCE
            .drawWithUnicodeFlag(underlying, text, anchorX, anchorY, inputColor, enableShadow, unicodeFlag);
        cir.setReturnValue(width + (enableShadow ? ((BatchingFontRenderer) (Object) this).getShadowOffset() : 0));
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

    @ModifyArg(
        method = { "flushBatchInner", "setupFontDrawState" },
        at = @At(
            value = "INVOKE",
            target = "Lcom/gtnewhorizons/angelica/glsm/GLStateManager;tryBlendFuncSeparate(IIII)V",
            ordinal = 0),
        index = 3,
        remap = false,
        require = 1)
    private int gtnl$compositeMaskCoverage(int destinationAlpha) {
        // Bold copies must accumulate coverage instead of erasing strokes with their translucent edges.
        return EffectTextRenderer.isCapturing() ? GL11.GL_ONE_MINUS_SRC_ALPHA : destinationAlpha;
    }

    @Redirect(
        method = { "flushBatchInner", "setupFontDrawState" },
        at = @At(
            value = "FIELD",
            target = "Lcom/gtnewhorizons/angelica/config/FontConfig;fontAAMode:I",
            opcode = Opcodes.GETSTATIC),
        remap = false,
        require = 3)
    private int gtnl$sharpMaskCoverage() {
        // Keep the comparison, cached mode and uniform consistent across both Angelica method layouts.
        return EffectTextRenderer.isCapturing() && !AngelicaTextAdapter.usesCustomFont(underlying) ? 0
            : FontConfig.fontAAMode;
    }

    // The shader is shared by all font instances, while Angelica caches its mode per instance.
    @Inject(method = "flushBatch", at = @At("RETURN"))
    private void gtnl$restoreFontAntialiasing(CallbackInfo ci) {
        int configuredMode = FontConfig.fontAAMode;
        if (!EffectTextRenderer.isCapturing() || fontAAModeLast == configuredMode) return;
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
