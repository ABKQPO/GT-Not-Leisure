package com.science.gtnl.mixins.late.Bartwork;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.werkstoff_loaders.recipe.MultipleMetalLoader;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.metadata.CompressionTierKey;

@Mixin(value = MultipleMetalLoader.class, remap = false)
public abstract class MixinMultipleMetalLoader {

    @Inject(method = "run(Lbartworks/system/material/Werkstoff;)V", at = @At("TAIL"), remap = false)
    private void injectSuperdensePlateRecipe(Werkstoff werkstoff, CallbackInfo ci) {
        if (werkstoff.hasItemType(OrePrefixes.plateDense)) {

            final CompressionTierKey COMPRESSION_TIER = CompressionTierKey.INSTANCE;

            RecipeBuilder.builder()
                .itemInputs(werkstoff.get(OrePrefixes.plate, 64))
                .itemOutputs(werkstoff.get(OrePrefixes.plateSuperdense, 1))

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
