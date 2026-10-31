package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.common.machine.hatch.MaintenanceHooks;

import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.ParallelHelper;

@Mixin(value = ProcessingLogic.class, remap = false)
public class MixinProcessingLogic {

    @Shadow
    protected IVoidable machine;

    @Shadow
    protected int duration;

    @Inject(method = "applyRecipe", at = @At("TAIL"))
    private void modifyRecipeBeforeValidation(GTRecipe recipe, ParallelHelper helper, OverclockCalculator calculator,
        CheckRecipeResult result, CallbackInfoReturnable<CheckRecipeResult> cir) {
        if (machine instanceof MTEMultiBlockBase mte) duration = MaintenanceHooks.configuredDuration(mte, duration);
    }

}
