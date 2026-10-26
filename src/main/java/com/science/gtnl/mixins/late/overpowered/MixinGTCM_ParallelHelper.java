package com.science.gtnl.mixins.late.overpowered;

import java.util.OptionalDouble;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.Nxer.TwistSpaceTechnology.common.machine.multiMachineClasses.processingLogics.GTCM_ParallelHelper;
import com.gtnllib.mod.ModList;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.recipes.ChanceBonusManager;
import com.science.gtnl.utils.recipes.GTNLParallelHelper;

import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ParallelHelper;

// Original code from Overpowered Mod. MIT License. 2025/4/18
@Mixin(value = GTCM_ParallelHelper.class, remap = false)
public abstract class MixinGTCM_ParallelHelper extends ParallelHelper {

    /**
     * Inject at the start of determineParallel to apply voltage-based chance bonus.
     */
    @Inject(method = "determineParallel", at = @At("HEAD"), remap = false)
    private void opDetermineParallel(CallbackInfo ci) {
        if (!ModList.Overpowered.isModLoaded() && MainConfig.machine.enableRecipeOutputChance) {
            // Compute optional bonus based on machine and current EU/t
            OptionalDouble bonusOptional = ChanceBonusManager
                .getChanceBonusOptional(machine, GTUtility.getTier(recipe.mEUt), chanceMultiplier, recipe);
            // If present, apply bonus by copying the recipe with increased chances
            if (bonusOptional.isPresent()) {
                recipe = ChanceBonusManager.copyAndBonusChance(recipe, bonusOptional.getAsDouble());
            }
        }
    }

    @Inject(method = "determineParallel", at = @At("RETURN"), remap = false)
    private void determineParallelZero(CallbackInfo ci) {
        if (result != CheckRecipeResultRegistry.INTERNAL_ERROR) return;
        result = currentParallel == 0 ? GTNLParallelHelper.PARALLEL_ZERO : GTNLParallelHelper.PARALLEL_OVERFLOW;
    }
}
