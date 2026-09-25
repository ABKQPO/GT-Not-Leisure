package com.science.gtnl.mixins.late.twistSpaceTechnology;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.Nxer.TwistSpaceTechnology.common.recipeMap.GTCMRecipe;
import com.Nxer.TwistSpaceTechnology.loader.RecipeLoader;
import com.Nxer.TwistSpaceTechnology.recipe.machineRecipe.expanded.CircuitAssemblyLineWithoutImprintRecipePool;
import com.science.gtnl.ScienceNotLeisure;

@Mixin(value = RecipeLoader.class, remap = false)
public abstract class MixinRecipeLoader {

    @Redirect(
        method = "loadRecipesServerStarted",
        at = @At(
            value = "INVOKE",
            target = "Lcom/Nxer/TwistSpaceTechnology/recipe/machineRecipe/expanded/CircuitAssemblyLineWithoutImprintRecipePool;loadRecipes()V"))
    private static void redirectCircuitAssemblyLineWithoutImprintLoadRecipes() {
        ScienceNotLeisure.LOG.info(
            "[GTNL] Detected TwistSpaceTechnology, intercept AdvCircuitAssemblyLine recipe loader to server start");
        GTCMRecipe.AdvCircuitAssemblyLineRecipeMap.getBackend()
            .clearRecipes();
        CircuitAssemblyLineWithoutImprintRecipePool.loadRecipes();
        System.out.println("[GTNL] Register TwistSpaceTechnology AdvCircuitAssemblyLine recipes");
    }

    @Redirect(
        method = "loadRecipes",
        at = @At(
            value = "INVOKE",
            target = "Lcom/Nxer/TwistSpaceTechnology/recipe/machineRecipe/expanded/AssemblyLineWithoutResearchRecipePool;loadRecipes()V"))
    private static void redirectAssemblyLineWithoutResearchLoadRecipes() {
        ScienceNotLeisure.LOG.info(
            "[GTNL] Detected TwistSpaceTechnology, intercept Assembly Line Without Research recipe loader to server start");
    }
}
