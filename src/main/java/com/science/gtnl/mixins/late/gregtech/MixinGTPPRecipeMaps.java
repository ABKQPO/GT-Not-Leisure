package com.science.gtnl.mixins.late.gregtech;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.science.gtnl.common.gui.recipe.ExtendQFTFrontend;

import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.api.recipe.RecipeMapFrontend;
import gregtech.api.recipe.RecipeMaps;

@Mixin(value = RecipeMaps.class, remap = false)
public class MixinGTPPRecipeMaps {

    @Redirect(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lgregtech/api/recipe/RecipeMapBuilder;frontend(Lgregtech/api/recipe/RecipeMapFrontend$FrontendCreator;)Lgregtech/api/recipe/RecipeMapBuilder;",
            ordinal = 25))
    private static RecipeMapBuilder<?> redirectFrontend(RecipeMapBuilder<?> instance,
        RecipeMapFrontend.FrontendCreator frontendCreator) {
        return instance.frontend(ExtendQFTFrontend::new);
    }
}
