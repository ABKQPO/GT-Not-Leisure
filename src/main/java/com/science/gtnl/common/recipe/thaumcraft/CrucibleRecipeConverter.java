package com.science.gtnl.common.recipe.thaumcraft;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.common.recipe.gtnl.ShapedArcaneCraftingRecipes;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.CrucibleRecipe;

public class CrucibleRecipeConverter {

    private static final int MAX_REJECTION_LOGS = 20;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<CrucibleRecipeData> scanRecipes() {
        List<CrucibleRecipeData> recipes = new ObjectArrayList<>();
        int rejected = 0;

        for (Object entry : new ObjectArrayList<>(ThaumcraftApi.getCraftingRecipes())) {
            if (!(entry instanceof CrucibleRecipe recipe)) {
                continue;
            }

            CrucibleRecipeData data = convertRecipe(recipe);
            if (data != null) {
                recipes.add(data);
                continue;
            }

            rejected++;
            if (rejected <= MAX_REJECTION_LOGS) {
                ScienceNotLeisure.LOG.warn(
                    "CrucibleRecipeConverter: skipping unusable crucible recipe (research={}, hash={}, output={}, aspects={}, catalyst={})",
                    recipe.key,
                    recipe.hash,
                    recipe.getRecipeOutput(),
                    recipe.aspects == null ? "null" : recipe.aspects.size() + " aspects",
                    recipe.catalyst == null ? "null"
                        : recipe.catalyst.getClass()
                            .getSimpleName());
            }
        }

        if (rejected > MAX_REJECTION_LOGS) {
            ScienceNotLeisure.LOG.warn(
                "CrucibleRecipeConverter: {} further unusable crucible recipes were not logged",
                rejected - MAX_REJECTION_LOGS);
        }

        return recipes;
    }

    public static CrucibleRecipeData convertRecipe(CrucibleRecipe recipe) {
        if (recipe == null) {
            return null;
        }

        ItemStack output = recipe.getRecipeOutput();
        if (output == null || output.getItem() == null || output.stackSize <= 0 || recipe.aspects == null) {
            return null;
        }

        for (Aspect aspect : recipe.aspects.getAspects()) {
            if (aspect == null || recipe.aspects.getAmount(aspect) < 0) return null;
        }

        ItemStack[] catalysts = expandCatalyst(recipe.catalyst);
        if (catalysts.length == 0) {
            return null;
        }

        return CrucibleRecipeData.builder()
            .sourceHash(recipe.hash)
            .researchKey(recipe.key == null ? "" : recipe.key)
            .catalysts(catalysts)
            .catalystOreName(ShapedArcaneCraftingRecipes.findOreDictionaryName(recipe.catalyst))
            .output(output.copy())
            .aspects(recipe.aspects.copy())
            .build();
    }

    public static ItemStack[] expandCatalyst(Object catalyst) {
        List<ItemStack> expanded = new ObjectArrayList<>();

        if (catalyst instanceof ItemStack stack) {
            if (isUsableCatalyst(stack)) {
                expanded.add(stack.copy());
            }
        } else if (catalyst instanceof Iterable<?>) {
            for (Object candidate : (Iterable<?>) catalyst) {
                if (candidate instanceof ItemStack && isUsableCatalyst((ItemStack) candidate)) {
                    expanded.add(((ItemStack) candidate).copy());
                }
            }
        }

        return expanded.toArray(new ItemStack[0]);
    }

    private static boolean isUsableCatalyst(ItemStack stack) {
        return stack != null && stack.getItem() != null && stack.stackSize > 0;
    }
}
