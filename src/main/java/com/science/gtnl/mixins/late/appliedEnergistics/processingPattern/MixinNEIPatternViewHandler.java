package com.science.gtnl.mixins.late.appliedEnergistics.processingPattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.me.processingPattern.ProcessingPatternCapacity;
import com.science.gtnl.common.me.processingPattern.ProcessingPatternInventoryData;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.integration.modules.NEIHelpers.NEIPatternViewHandler;

@Mixin(value = NEIPatternViewHandler.class, remap = false)
public abstract class MixinNEIPatternViewHandler {

    @Shadow
    public ICraftingPatternDetails patternDetails;

    @Shadow
    public int inputsCols;

    @Shadow
    public int inputsRows;

    @Shadow
    public int outputsCols;

    @Shadow
    public int outputsRows;

    @Shadow
    public int arrowCenterY;

    @Shadow
    public abstract void setupProcessingLayout();

    @Shadow
    public abstract void setupReversedLayout();

    @Inject(method = "initializeLayout", at = @At("TAIL"))
    private void gtnl$resizeProcessingLayout(CallbackInfo callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled() || this.patternDetails == null || this.patternDetails.isCraftable())
            return;
        int inputCount = ProcessingPatternInventoryData.countStacks(this.patternDetails.getAEInputs());
        int outputCount = ProcessingPatternInventoryData.countStacks(this.patternDetails.getAEOutputs());
        int layout = ProcessingPatternInventoryData.shorterViewLayout(inputCount, outputCount);
        if (layout > 0 && this.inputsCols != 4) this.setupProcessingLayout();
        else if (layout < 0 && this.outputsCols != 4) this.setupReversedLayout();
        this.inputsRows = ProcessingPatternInventoryData.viewRows(inputCount, this.inputsCols);
        this.outputsRows = ProcessingPatternInventoryData.viewRows(outputCount, this.outputsCols);
        this.arrowCenterY = NEIPatternViewHandler.SLOTS_OFFSET_Y + Math.max(this.inputsRows, this.outputsRows) * 9 - 16;
    }

    @Inject(method = "calculateRecipeHeight", at = @At("RETURN"), cancellable = true)
    private void gtnl$includeOutputRows(CallbackInfoReturnable<Integer> callbackInfo) {
        if (!ProcessingPatternCapacity.isEnabled() || this.patternDetails == null
            || this.patternDetails.isCraftable()) {
            return;
        }
        callbackInfo
            .setReturnValue(callbackInfo.getReturnValue() + Math.max(0, this.outputsRows - this.inputsRows) * 18);
    }
}
