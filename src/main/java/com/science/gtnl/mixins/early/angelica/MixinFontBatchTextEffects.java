package com.science.gtnl.mixins.early.angelica;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gtnewhorizons.angelica.client.font.BatchingFontRenderer;
import com.science.gtnl.client.text.EffectTextRenderer;
import com.science.gtnl.client.text.compat.FontBatchBridge;

/** Flushes the current batch before temporary render-target changes. */
@Mixin(value = BatchingFontRenderer.class, remap = false)
public abstract class MixinFontBatchTextEffects implements FontBatchBridge {

    @Shadow
    private int batchDepth;

    @Shadow
    private static BatchingFontRenderer arenaOwner;

    @Shadow
    private void flushBatch() {}

    @Shadow
    public static void flushDeferredText() {}

    @Override
    public int gtnl$suspendBatch() {
        flushDeferredText();
        if (arenaOwner instanceof FontBatchBridge owner) owner.gtnl$flushBatch();
        flushBatch();
        int depth = batchDepth;
        batchDepth = 0;
        return depth;
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
}
