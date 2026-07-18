package com.science.gtnl.mixins.late.gregtech;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    private static int science$cachedTecTechRecipeCount = -1;
    @Unique
    private static int science$cachedAssemblyLineRecipeCount = -1;
    @Unique
    private static final Multimap<ItemId, GTRecipe.RecipeAssemblyLine> science$recipeLookup = MultimapBuilder.hashKeys()
        .arrayListValues()
        .build();

    @Inject(method = "findALRecipeByOutput", at = @At("HEAD"), cancellable = true)
    private static void science$findRecipeByOutput(ItemStack output,
        CallbackInfoReturnable<Collection<GTRecipe.RecipeAssemblyLine>> cir) {
        if (GTUtility.isStackInvalid(output)) {
            cir.setReturnValue(Collections.emptyList());
            return;
        }

        science$refreshRecipeLookup();
        cir.setReturnValue(science$recipeLookup.get(ItemId.create(output)));
    }

    @Unique
    private static synchronized void science$refreshRecipeLookup() {
        int currentAssemblyLineRecipeCount = GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes.size();
        int currentTecTechRecipeCount = TecTechRecipeMaps.researchableALRecipeList.size();
        if (science$cachedAssemblyLineRecipeCount == currentAssemblyLineRecipeCount
            && science$cachedTecTechRecipeCount == currentTecTechRecipeCount) {
            return;
        }

        science$recipeLookup.clear();
        Set<GTRecipe.RecipeAssemblyLine> addedRecipes = Collections.newSetFromMap(new IdentityHashMap<>());
        science$addRecipesToLookup(GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes, addedRecipes);
        science$addRecipesToLookup(TecTechRecipeMaps.researchableALRecipeList, addedRecipes);

        science$cachedAssemblyLineRecipeCount = currentAssemblyLineRecipeCount;
        science$cachedTecTechRecipeCount = currentTecTechRecipeCount;
    }

    @Unique
    private static void science$addRecipesToLookup(Collection<? extends GTRecipe.RecipeAssemblyLine> recipes,
        Set<GTRecipe.RecipeAssemblyLine> addedRecipes) {
        for (GTRecipe.RecipeAssemblyLine recipe : recipes) {
            if (recipe == null || GTUtility.isStackInvalid(recipe.mOutput)) {
                continue;
            }
            if (!addedRecipes.add(recipe)) {
                continue;
            }
            science$recipeLookup.put(ItemId.create(recipe.mOutput), recipe);
        }
    }
}
