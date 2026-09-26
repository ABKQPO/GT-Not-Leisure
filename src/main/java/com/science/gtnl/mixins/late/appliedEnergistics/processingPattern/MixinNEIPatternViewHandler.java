package com.science.gtnl.mixins.late.appliedEnergistics.processingPattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.utils.appliedEnergistics.processingPattern.ProcessingPatternCapacity;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEStack;
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
        if (!ProcessingPatternCapacity.isEnabled() || this.patternDetails == null
            || this.patternDetails.isCraftable()) {
            return;
        }

        int inputCount = gtnl$countStacks(this.patternDetails.getAEInputs());
        int outputCount = gtnl$countStacks(this.patternDetails.getAEOutputs());
        int normalHeight = Math.max(gtnl$rows(inputCount, 4), gtnl$rows(outputCount, 1));
        int reversedHeight = Math.max(gtnl$rows(inputCount, 1), gtnl$rows(outputCount, 4));

        if (normalHeight < reversedHeight && this.inputsCols != 4) {
            this.setupProcessingLayout();
        } else if (reversedHeight < normalHeight && this.outputsCols != 4) {
            this.setupReversedLayout();
        }

        this.inputsRows = gtnl$rows(inputCount, this.inputsCols);
        this.outputsRows = gtnl$rows(outputCount, this.outputsCols);
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

    private static int gtnl$countStacks(IAEStack<?>[] stacks) {
        int count = 0;
        for (IAEStack<?> stack : stacks) {
            if (stack != null) {
                count++;
            }
        }
        return count;
    }

    private static int gtnl$rows(int count, int columns) {
        return Math.max(8, (count + columns - 1) / columns);
    }
}
