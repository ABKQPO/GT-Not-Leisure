package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.science.gtnl.common.gui.recipe.ExtendQFTFrontend;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBuilder;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

@Mixin(value = GTPPRecipeMaps.class, remap = false)
public class MixinGTPPRecipeMaps {

    @Redirect(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/recipe/RecipeMapBuilder;build()Lgregtech/api/recipe/RecipeMap;",
            ordinal = 3))
    private static RecipeMap<?> redirectBuild(RecipeMapBuilder<?> instance) {
        return instance.frontend(ExtendQFTFrontend::new)
            .maxIO(16, 16, 8, 8)
            .neiHandlerInfo(
                builder -> builder.setDisplayStack(GregtechItemList.QuantumForceTransformer.get(1))
                    .setMaxRecipesPerPage(1))
            .build();
    }
}
