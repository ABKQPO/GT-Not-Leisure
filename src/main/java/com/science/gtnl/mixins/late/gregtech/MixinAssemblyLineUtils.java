package com.science.gtnl.mixins.late.gregtech;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import gregtech.api.util.AssemblyLineUtils;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.GTUtility.ItemId;
import tectech.recipe.TecTechRecipeMaps;

@Mixin(value = AssemblyLineUtils.class, remap = false)
public abstract class MixinAssemblyLineUtils {

    private static int science$cachedTecTechRecipeCount = -1;
    private static final Multimap<ItemId, GTRecipe.RecipeAssemblyLine> science$tectechRecipeLookup = MultimapBuilder
        .hashKeys()
        .arrayListValues()
        .build();

    @Inject(method = "findALRecipeByOutput", at = @At("RETURN"), cancellable = true)
    private static void science$findTecTechRecipeByOutput(ItemStack output,
        CallbackInfoReturnable<Collection<GTRecipe.RecipeAssemblyLine>> cir) {
        Collection<GTRecipe.RecipeAssemblyLine> original = cir.getReturnValue();
        if (GTUtility.isStackInvalid(output) || original != null && !original.isEmpty()) return;

        Collection<GTRecipe.RecipeAssemblyLine> fallbackRecipes = science$getTecTechRecipesByOutput(output);
        if (!fallbackRecipes.isEmpty()) {
            cir.setReturnValue(fallbackRecipes);
        }
    }

    private static Collection<GTRecipe.RecipeAssemblyLine> science$getTecTechRecipesByOutput(ItemStack output) {
        if (GTUtility.isStackInvalid(output)) {
            return Collections.emptyList();
        }
        science$refreshTecTechRecipeLookup();
        return science$tectechRecipeLookup.get(ItemId.create(output));
    }

    private static synchronized void science$refreshTecTechRecipeLookup() {
        List<? extends GTRecipe.RecipeAssemblyLine> recipes = TecTechRecipeMaps.researchableALRecipeList;
        int currentRecipeCount = recipes.size();
        if (science$cachedTecTechRecipeCount == currentRecipeCount) {
            return;
        }

        science$tectechRecipeLookup.clear();
        for (GTRecipe.RecipeAssemblyLine recipe : recipes) {
            if (recipe == null || GTUtility.isStackInvalid(recipe.mOutput)) {
                continue;
            }
            science$tectechRecipeLookup.put(ItemId.create(recipe.mOutput), recipe);
        }
        science$cachedTecTechRecipeCount = currentRecipeCount;
    }
}
