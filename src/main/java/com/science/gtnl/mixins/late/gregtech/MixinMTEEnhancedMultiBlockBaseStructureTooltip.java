package com.science.gtnl.mixins.late.gregtech;

import static com.science.gtnl.utils.structure.StructureTooltipHooks.UNLIMITED;

import java.util.List;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.utils.structure.StructureTooltipHooks;

import gregtech.api.enums.HatchElement;
import gregtech.api.metatileentity.implementations.MTEEnhancedMultiBlockBase;
import gregtech.api.structure.error.StructureError;

// Why: the automatic structure tooltip reads pieces and requirements by dry-running the prototype's own code.
@Mixin(value = MTEEnhancedMultiBlockBase.class, remap = false)
public abstract class MixinMTEEnhancedMultiBlockBaseStructureTooltip {

    @Inject(method = "buildPiece", at = @At("HEAD"), cancellable = true)
    private void gtnl$recordBuildPiece(String piece, ItemStack trigger, boolean hintOnly, int horizontalOffset,
        int verticalOffset, int depthOffset, CallbackInfoReturnable<Boolean> cir) {
        if (StructureTooltipHooks.onBuildPiece(this, piece, horizontalOffset, verticalOffset, depthOffset)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "checkPiece(Ljava/lang/String;IIILjava/util/List;)Z", at = @At("HEAD"), cancellable = true)
    private void gtnl$skipCheckPiece(String piece, int horizontalOffset, int verticalOffset, int depthOffset,
        List<StructureError> errors, CallbackInfoReturnable<Boolean> cir) {
        if (StructureTooltipHooks.onCheckPiece(this)) cir.setReturnValue(true);
    }

    @Inject(method = "checkCasingMin", at = @At("HEAD"), cancellable = true)
    private void gtnl$recordCasingMin(List<StructureError> errors, int current, int required, CallbackInfo ci) {
        if (StructureTooltipHooks.onCheckCasingMin(this, required)) ci.cancel();
    }

    @Inject(method = "checkHatchMin", at = @At("HEAD"), cancellable = true)
    private void gtnl$recordHatchMin(List<StructureError> errors, HatchElement element, int min, CallbackInfo ci) {
        if (StructureTooltipHooks.onCheckHatches(this, min, UNLIMITED, element)) ci.cancel();
    }

    @Inject(method = "checkHatchExact", at = @At("HEAD"), cancellable = true)
    private void gtnl$recordHatchExact(List<StructureError> errors, HatchElement element, int target, CallbackInfo ci) {
        if (StructureTooltipHooks.onCheckHatches(this, target, target, element)) ci.cancel();
    }

    @Inject(method = "checkHatchMax", at = @At("HEAD"), cancellable = true)
    private void gtnl$recordHatchMax(List<StructureError> errors, HatchElement element, int max, CallbackInfo ci) {
        if (StructureTooltipHooks.onCheckHatches(this, 0, max, element)) ci.cancel();
    }

    @Inject(method = "checkHasAnyInput", at = @At("HEAD"), cancellable = true)
    private void gtnl$recordAnyInput(List<StructureError> errors, CallbackInfo ci) {
        if (StructureTooltipHooks.onCheckHatches(this, 1, UNLIMITED, HatchElement.InputBus, HatchElement.InputHatch)) {
            ci.cancel();
        }
    }

    @Inject(method = "checkHasAnyOutput", at = @At("HEAD"), cancellable = true)
    private void gtnl$recordAnyOutput(List<StructureError> errors, CallbackInfo ci) {
        if (StructureTooltipHooks.onCheckHatches(this, 1, UNLIMITED, HatchElement.OutputBus, HatchElement.OutputHatch))
            ci.cancel();
    }

    @Inject(method = "checkHasAnyEnergy", at = @At("HEAD"), cancellable = true)
    private void gtnl$recordAnyEnergy(List<StructureError> errors, CallbackInfo ci) {
        if (StructureTooltipHooks.onCheckHatches(this, 1, UNLIMITED, HatchElement.Energy, HatchElement.ExoticEnergy))
            ci.cancel();
    }

    @Inject(method = "checkHasAnyDynamo", at = @At("HEAD"), cancellable = true)
    private void gtnl$recordAnyDynamo(List<StructureError> errors, CallbackInfo ci) {
        if (StructureTooltipHooks.onCheckHatches(this, 1, UNLIMITED, HatchElement.Dynamo, HatchElement.ExoticDynamo))
            ci.cancel();
    }

    @Inject(method = "checkOneEnergyHatchMaybeExotic", at = @At("HEAD"), cancellable = true)
    private void gtnl$recordOneEnergy(List<StructureError> errors, CallbackInfo ci) {
        if (StructureTooltipHooks.onCheckHatches(this, 1, 1, HatchElement.Energy, HatchElement.ExoticEnergy)) {
            ci.cancel();
        }
    }
}
