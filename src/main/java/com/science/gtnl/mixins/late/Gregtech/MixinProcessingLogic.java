package com.science.gtnl.mixins.late.Gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IConfigurationMaintenance;

import gregtech.api.interfaces.tileentity.IVoidable;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchMaintenance;
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
        if (machine instanceof MTEMultiBlockBase mte) {
            try {
                for (MTEHatchMaintenance maintenance : mte.mMaintenanceHatches) {
                    if (maintenance instanceof IConfigurationMaintenance customMaintenance
                        && customMaintenance.isConfiguration()) {
                        duration = (int) Math.max(1, duration * customMaintenance.getConfigTime() / 100.0);
                        return;
                    }
                }
            } catch (Exception e) {
                ScienceNotLeisure.LOG.warn("Error reading IConfigurationMaintenance from MTE: {}", mte, e);
            }
        }
    }

}
