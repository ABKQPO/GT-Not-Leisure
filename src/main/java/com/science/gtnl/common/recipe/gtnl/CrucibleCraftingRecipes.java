package com.science.gtnl.common.recipe.gtnl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.thaumcraft.CrucibleRecipeConverter;
import com.science.gtnl.common.recipe.thaumcraft.CrucibleRecipeData;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipeTools;

import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.SimpleRecipeMetadataKey;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class CrucibleCraftingRecipes implements IRecipePool {

    public static final RecipeMetadataKey<AspectList> CRUCIBLE_ASPECTS = SimpleRecipeMetadataKey
        .create(AspectList.class, "gtnl_crucible_aspects");
    public static final RecipeMetadataKey<String> CRUCIBLE_RESEARCH = SimpleRecipeMetadataKey
        .create(String.class, "gtnl_crucible_research");

    private static final int MAX_SELECTOR_CIRCUIT = 24;

    private static final Map<String, Pending> REGISTERED = new LinkedHashMap<>();

    @Override
    public void loadRecipes() {
        Set<String> seen = new HashSet<>();

        for (CrucibleRecipeData recipe : CrucibleRecipeConverter.scanRecipes()) {
            String identity;
            try {
                identity = identityOf(recipe);
            } catch (RuntimeException e) {
                ScienceNotLeisure.LOG
                    .warn("CrucibleCraftingRecipes: cannot compute the identity of a crucible recipe, skipping it", e);
                continue;
            }

            if (identity == null || !seen.add(identity) || REGISTERED.containsKey(identity)) {
                continue;
            }

            int circuit = findSelectorCircuit(recipe);
            if (circuit == 0) {
                ScienceNotLeisure.LOG.warn(
                    "CrucibleCraftingRecipes: no free selector circuit for {}, skipping recipe with overlapping catalysts",
                    recipe.output);
                continue;
            }

            registerRecipe(new Pending(recipe, identity, circuit));
        }
    }

    private void registerRecipe(Pending pending) {
        Object[] inputs = createInputs(pending.recipe, pending.circuit);
        if (inputs.length == 0) {
            return;
        }

        try {
            TCRecipeTools.addArcaneRecipe(
                GTNLRecipeMaps.IndustrialCrucibleRecipes,
                inputs,
                new ItemStack[] { pending.recipe.output },
                pending.recipe.aspects,
                pending.recipe.researchKey,
                CRUCIBLE_ASPECTS,
                CRUCIBLE_RESEARCH,
                TCRecipeTools
                    .computeAspectDuration(pending.recipe.aspects, TCRecipeTools.ARCANE_DURATION_TICKS_PER_ASPECT),
                TierEU.RECIPE_LV);
        } catch (RuntimeException e) {
            ScienceNotLeisure.LOG.warn(
                "CrucibleCraftingRecipes: failed to register a crucible recipe, it will be retried on the next load",
                e);
            return;
        }

        REGISTERED.put(pending.identity, pending);
    }

    private Object[] createInputs(CrucibleRecipeData recipe, int circuit) {
        if (recipe == null || recipe.catalysts == null || recipe.catalysts.length == 0) {
            return new Object[0];
        }

        List<ItemStack> alternatives = new ArrayList<>(recipe.catalysts.length);
        for (ItemStack catalyst : recipe.catalysts) {
            if (catalyst == null || catalyst.getItem() == null || catalyst.stackSize <= 0) {
                continue;
            }
            alternatives.add(catalyst.copy());
        }

        if (alternatives.isEmpty()) {
            return new Object[0];
        }

        return new Object[] { alternatives.toArray(new ItemStack[0]), GTUtility.getIntegratedCircuit(circuit) };
    }

    private static int findSelectorCircuit(CrucibleRecipeData recipe) {
        boolean[] used = new boolean[MAX_SELECTOR_CIRCUIT + 1];
        for (Pending registered : REGISTERED.values()) {
            if (catalystsOverlap(recipe.catalysts, registered.recipe.catalysts)) {
                used[registered.circuit] = true;
            }
        }
        for (int circuit = 1; circuit <= MAX_SELECTOR_CIRCUIT; circuit++) {
            if (!used[circuit]) return circuit;
        }
        return 0;
    }

    private static boolean catalystsOverlap(ItemStack[] left, ItemStack[] right) {
        for (ItemStack leftStack : left) {
            for (ItemStack rightStack : right) {
                // Matching ignores count/NBT and accepts wildcard damage and ore-unified equivalents.
                if (GTUtility.areStacksEqual(leftStack, rightStack, true) || GTUtility.areStacksEqual(
                    GTOreDictUnificator.get_nocopy(true, leftStack),
                    GTOreDictUnificator.get_nocopy(true, rightStack),
                    true)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String identityOf(CrucibleRecipeData recipe) {
        if (recipe == null || recipe.output == null) {
            return null;
        }

        List<String> aspectParts = new ArrayList<>();
        AspectList aspects = recipe.aspects;
        if (aspects != null) {
            for (Aspect aspect : aspects.getAspectsSortedAmount()) {
                if (aspect == null) {
                    continue;
                }
                aspectParts.add(aspect.getTag() + ':' + aspects.getAmount(aspect));
            }
        }
        Collections.sort(aspectParts);

        List<String> catalystParts = new ArrayList<>();
        if (recipe.catalysts != null) {
            for (ItemStack catalyst : recipe.catalysts) {
                if (catalyst == null) {
                    continue;
                }
                catalystParts.add(stackIdentity(catalyst) + 'x' + catalyst.stackSize);
            }
        }
        Collections.sort(catalystParts);

        StringBuilder builder = new StringBuilder();
        builder.append(recipe.researchKey)
            .append('|')
            .append(stackIdentity(recipe.output))
            .append('x')
            .append(recipe.output.stackSize);
        for (String part : aspectParts) {
            builder.append('|')
                .append(part);
        }
        for (String part : catalystParts) {
            builder.append('|')
                .append(part);
        }
        return builder.toString();
    }

    private static String stackIdentity(ItemStack stack) {
        try {
            return Base64.getEncoder()
                .encodeToString(CompressedStreamTools.compress(stack.writeToNBT(new NBTTagCompound())));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot encode industrial crucible recipe stack", e);
        }
    }

    private static final class Pending {

        private final CrucibleRecipeData recipe;
        private final String identity;
        private final int circuit;

        private Pending(CrucibleRecipeData recipe, String identity, int circuit) {
            this.recipe = recipe;
            this.identity = identity;
            this.circuit = circuit;
        }
    }
}
