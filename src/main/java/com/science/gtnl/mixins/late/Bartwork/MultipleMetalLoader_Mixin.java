package com.science.gtnl.mixins.late.Bartwork;

import static gregtech.api.enums.OrePrefixes.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.werkstoff_loaders.recipe.MultipleMetalLoader;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.metadata.CompressionTierKey;

@SuppressWarnings("UnusedMixin")
@Mixin(value = MultipleMetalLoader.class, remap = false)
public abstract class MultipleMetalLoader_Mixin {

    @Inject(method = "run(Lbartworks/system/material/Werkstoff;)V", at = @At("TAIL"), remap = false)
    private void injectSuperdensePlateRecipe(Werkstoff werkstoff, CallbackInfo ci) {
        if (werkstoff.hasItemType(OrePrefixes.plateDense)) {

            final CompressionTierKey COMPRESSION_TIER = CompressionTierKey.INSTANCE;

            GTValues.RA.stdBuilder()
                .itemInputs(werkstoff.get(plate, 64))
                .itemOutputs(werkstoff.get(plateSuperdense, 1))

                .metadata(COMPRESSION_TIER, 1)
                .duration(
                    (int) Math.max(
                        werkstoff.getStats()
                            .getMass(),
                        1L))
                .eut(
                    werkstoff.getStats()
                        .getMass() > 128 ? TierEU.RECIPE_IV : TierEU.RECIPE_EV)
                .addTo(RecipeMaps.compressorRecipes);
        }
    }
}
