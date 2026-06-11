package com.science.gtnl.common.gui.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import gregtech.api.objects.GTItemStack;
import gregtech.api.recipe.RecipeMapBackend;
import gregtech.api.recipe.RecipeMapBackendPropertiesBuilder;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SteamGateAssemblerBackend extends RecipeMapBackend {

    private final List<GTRecipe> steamGateRecipes = new ArrayList<>();
    private final Map<GTItemStack, List<GTRecipe>> recipesByAnchor = new HashMap<>();

    public SteamGateAssemblerBackend(RecipeMapBackendPropertiesBuilder propertiesBuilder) {
        super(propertiesBuilder);
    }

    @Override
    public GTRecipe compileRecipe(GTRecipe recipe) {
        boolean fakeRecipe = recipe.mFakeRecipe;
        recipe.mFakeRecipe = true;
        GTRecipe compiledRecipe = super.compileRecipe(recipe);
        recipe.mFakeRecipe = fakeRecipe;
        steamGateRecipes.add(compiledRecipe);
        addRecipeAnchor(compiledRecipe);
        return compiledRecipe;
    }

    @Override
    public void reInit() {
        rebuildRecipeAnchors();
    }

    @Override
    public boolean doesOverwriteFindRecipe() {
        return true;
    }

    @Override
    protected @Nullable GTRecipe overwriteFindRecipe(ItemStack[] items, FluidStack[] fluids,
        @Nullable ItemStack specialSlot, @Nullable GTRecipe cachedRecipe) {
        if (isValidSteamGateRecipe(cachedRecipe, items, fluids)) {
            return cachedRecipe;
        }

        Set<GTRecipe> candidates = findCandidateRecipes(items);
        for (GTRecipe recipe : candidates) {
            if (isValidSteamGateRecipe(recipe, items, fluids)) {
                return recipe;
            }
        }

        for (GTRecipe recipe : steamGateRecipes) {
            if (candidates.contains(recipe)) {
                continue;
            }
            if (isValidSteamGateRecipe(recipe, items, fluids)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public void clearRecipes() {
        super.clearRecipes();
        steamGateRecipes.clear();
        recipesByAnchor.clear();
    }

    @Override
    public void removeRecipes(Collection<? extends GTRecipe> recipesToRemove) {
        if (recipesToRemove.isEmpty()) {
            return;
        }

        Set<GTRecipe> recipesToRemoveByIdentity = Collections.newSetFromMap(new IdentityHashMap<>());
        recipesToRemoveByIdentity.addAll(recipesToRemove);

        List<GTRecipe> keptRecipes = new ArrayList<>(steamGateRecipes.size());
        boolean removed = false;
        for (GTRecipe recipe : steamGateRecipes) {
            if (recipesToRemoveByIdentity.contains(recipe)) {
                removed = true;
            } else {
                keptRecipes.add(recipe);
            }
        }

        if (!removed) {
            return;
        }

        super.clearRecipes();
        steamGateRecipes.clear();
        recipesByAnchor.clear();
        for (GTRecipe recipe : keptRecipes) {
            compileRecipe(recipe);
        }
    }

    @Override
    public void removeRecipe(GTRecipe recipe) {
        removeRecipes(Collections.singleton(recipe));
    }

    public List<GTRecipe> getSteamGateRecipes() {
        return Collections.unmodifiableList(steamGateRecipes);
    }

    private boolean isValidSteamGateRecipe(@Nullable GTRecipe recipe, ItemStack[] items, FluidStack[] fluids) {
        return recipe != null && recipe.mEnabled
            && !recipe.mFakeRecipe
            && recipe.isRecipeInputEqual(false, false, fluids, items);
    }

    private Set<GTRecipe> findCandidateRecipes(ItemStack[] items) {
        if (items == null || items.length == 0 || recipesByAnchor.isEmpty()) {
            return Collections.emptySet();
        }

        Set<GTRecipe> candidates = new LinkedHashSet<>();
        for (ItemStack item : items) {
            if (item == null) {
                continue;
            }
            addCandidates(candidates, item);
            addCandidates(candidates, GTOreDictUnificator.get_nocopy(true, item));
        }
        return candidates;
    }

    private void addCandidates(Set<GTRecipe> candidates, @Nullable ItemStack item) {
        if (item == null) {
            return;
        }

        List<GTRecipe> recipes = recipesByAnchor.get(new GTItemStack(item));
        if (recipes != null) {
            candidates.addAll(recipes);
        }
    }

    private void rebuildRecipeAnchors() {
        recipesByAnchor.clear();
        for (GTRecipe recipe : steamGateRecipes) {
            addRecipeAnchor(recipe);
        }
    }

    private void addRecipeAnchor(GTRecipe recipe) {
        ItemStack anchor = selectAnchor(recipe);
        if (anchor == null) {
            return;
        }

        addRecipeAnchor(anchor, recipe);
        addRecipeAnchor(GTOreDictUnificator.get_nocopy(true, anchor), recipe);
    }

    private void addRecipeAnchor(@Nullable ItemStack anchor, GTRecipe recipe) {
        if (anchor == null) {
            return;
        }

        recipesByAnchor.computeIfAbsent(new GTItemStack(anchor), key -> new ArrayList<>())
            .add(recipe);
    }

    private @Nullable ItemStack selectAnchor(GTRecipe recipe) {
        if (recipe.mInputs == null || recipe.mInputs.length == 0) {
            return null;
        }

        Map<GTItemStack, Integer> inputCounts = new HashMap<>();
        Map<GTItemStack, ItemStack> representativeInputs = new HashMap<>();
        for (ItemStack input : recipe.mInputs) {
            if (input == null) {
                continue;
            }

            GTItemStack key = new GTItemStack(input);
            inputCounts.merge(key, 1, Integer::sum);
            representativeInputs.putIfAbsent(key, input);
        }

        GTItemStack bestKey = null;
        int bestCount = Integer.MAX_VALUE;
        for (Map.Entry<GTItemStack, Integer> entry : inputCounts.entrySet()) {
            int count = entry.getValue();
            if (count < bestCount) {
                bestCount = count;
                bestKey = entry.getKey();
            }
        }
        return bestKey == null ? null : representativeInputs.get(bestKey);
    }
}
