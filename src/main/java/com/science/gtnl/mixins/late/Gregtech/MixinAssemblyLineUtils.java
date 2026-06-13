package com.science.gtnl.mixins.late.Gregtech;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.ScienceNotLeisure;

import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import tectech.recipe.TecTechRecipeMaps;

@Mixin(value = AssemblyLineUtils.class, remap = false)
public abstract class MixinAssemblyLineUtils {

    @Inject(method = "findALRecipeByOutput", at = @At("RETURN"), cancellable = true)
    private static void science$findTecTechRecipeByOutput(ItemStack output,
        CallbackInfoReturnable<Collection<GTRecipe.RecipeAssemblyLine>> cir) {
        Collection<GTRecipe.RecipeAssemblyLine> original = cir.getReturnValue();
        if (GTUtility.isStackInvalid(output) || original != null && !original.isEmpty()) return;

        // Fall back to TecTech researchable assembly line recipes when standard GT lookup returns nothing.
        List<GTRecipe.RecipeAssemblyLine> fallbackRecipes = new ArrayList<>();
        for (GTRecipe.RecipeAssemblyLine recipe : TecTechRecipeMaps.researchableALRecipeList) {
            if (recipe == null || GTUtility.isStackInvalid(recipe.mOutput)) continue;
            if (GTUtility.areStacksEqual(recipe.mOutput, output, true)) {
                fallbackRecipes.add(recipe);
            }
        }

        if (!fallbackRecipes.isEmpty()) {
            ScienceNotLeisure.LOG.info("Resolved AssemblyLine data stick via TecTech fallback: {}", output);
            cir.setReturnValue(fallbackRecipes);
        }
    }
}
