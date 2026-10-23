package com.science.gtnl.common.recipe.gtnl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipeTools;

import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.objects.OreDictItemStack;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.SimpleRecipeMetadataKey;
import thaumcraft.api.aspects.AspectList;

public class ShapedArcaneCraftingRecipes implements IRecipePool {

    public static final RecipeMetadataKey<AspectList> ARCANE_VIS = SimpleRecipeMetadataKey
        .create(AspectList.class, "gtnl_arcane_vis");
    public static final RecipeMetadataKey<String> ARCANE_RESEARCH = SimpleRecipeMetadataKey
        .create(String.class, "gtnl_arcane_research");

    private static final List<String> BLACKLISTED_OREDICT_NAMES = Arrays.asList(
        "craftingToolScrewdriver",
        "craftingToolHardHammer",
        "craftingToolSaw",
        "craftingSoftHammer",
        "craftingToolWrench",
        "craftingToolFile",
        "craftingToolCrowbar",
        "craftingToolWireCutter",
        "craftingToolBlade");

    @Override
    public void loadRecipes() {
        TCRecipeTools.getShapedArcaneCraftingRecipe();
        TCRecipeTools.getShapelessArcaneCraftingRecipe();

        IRecipeMap IAA = GTNLRecipeMaps.IndustrialShapedArcaneCraftingRecipes;

        // Shaped
        for (TCRecipeTools.ShapedArcaneCraftingRecipe recipe : TCRecipeTools.ShapedAR) {
            if (TCRecipeTools.shouldSkipThaumcraftItem(
                recipe.getOutput()
                    .getItem())) {
                continue;
            }

            ItemStack output = recipe.getOutput()
                .copy();
            output.stackSize = 1;

            for (Object[] inputs : createInputVariants(Arrays.asList(recipe.getInputItems()))) {
                registerRecipe(IAA, inputs, output, recipe.getInputAspects(), recipe.getResearch(), inputs.length == 1);
            }
        }

        // Shapeless
        for (TCRecipeTools.ShapelessArcaneCraftingRecipe recipe : TCRecipeTools.ShaplessAR) {
            if (TCRecipeTools.shouldSkipThaumcraftItem(
                recipe.getOutput()
                    .getItem())) {
                continue;
            }

            ItemStack output = recipe.getOutput()
                .copy();
            output.stackSize = 1;

            for (Object[] inputs : createInputVariants(recipe.getInputItems())) {
                registerRecipe(IAA, inputs, output, recipe.getInputAspects(), recipe.getResearch(), false);
            }
        }
    }

    private void registerRecipe(IRecipeMap map, Object[] inputs, ItemStack output, AspectList aspects, String research,
        boolean needsCircuit) {
        Object[] recipeInputs = Arrays.stream(inputs)
            .map(input -> input instanceof ItemStack stack ? stack.copy() : input)
            .toArray();
        int duration = TCRecipeTools
            .computeAspectDurationFromCopy(aspects, TCRecipeTools.ARCANE_DURATION_TICKS_PER_ASPECT);
        if (needsCircuit) {
            TCRecipeTools.addArcaneRecipeWithCircuit(
                map,
                recipeInputs,
                new ItemStack[] { output.copy() },
                aspects,
                research,
                ARCANE_VIS,
                ARCANE_RESEARCH,
                duration,
                TierEU.RECIPE_LV,
                1);
        } else {
            TCRecipeTools.addArcaneRecipe(
                map,
                recipeInputs,
                new ItemStack[] { output.copy() },
                aspects,
                research,
                ARCANE_VIS,
                ARCANE_RESEARCH,
                duration,
                TierEU.RECIPE_LV);
        }
    }

    private List<Object[]> createInputVariants(Iterable<?> inputs) {
        List<Object[]> variants = new ArrayList<>();
        variants.add(new Object[0]);

        for (Object input : inputs) {
            List<Object> alternatives = convertInput(input);
            if (alternatives == null) return Collections.emptyList();
            if (alternatives.isEmpty()) continue;

            // Only explicit alternatives are expanded. Ore dictionary slots keep their original ID.
            List<Object[]> expanded = new ArrayList<>();
            for (Object[] variant : variants) {
                for (Object alternative : alternatives) {
                    Object[] next = Arrays.copyOf(variant, variant.length + 1);
                    next[variant.length] = alternative;
                    expanded.add(next);
                }
            }
            variants = expanded;
        }
        return variants;
    }

    private List<Object> convertInput(Object input) {
        if (input == null) return Collections.emptyList();
        if (input instanceof ItemStack stack) {
            return stack.getItem() == null ? null : Collections.singletonList(normalizeInput(stack));
        }
        if (!(input instanceof List<?>candidates) || candidates.isEmpty()) return null;

        String oreName = findOreDictionaryName(input);
        if (oreName != null) {
            if (BLACKLISTED_OREDICT_NAMES.contains(oreName)) return Collections.emptyList();
            return Collections.singletonList(new OreDictItemStack(oreName, 1));
        }

        List<Object> alternatives = new ArrayList<>();
        for (Object candidate : candidates) {
            if (!(candidate instanceof ItemStack stack) || stack.getItem() == null) continue;
            if (alternatives.isEmpty() && isOreDictBlacklisted(stack)) return Collections.emptyList();
            alternatives.add(normalizeInput(stack));
        }
        return alternatives.isEmpty() ? null : alternatives;
    }

    private ItemStack normalizeInput(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.stackSize = 1;
        return TCRecipeTools.checkInputSpecial(copy)[0];
    }

    private boolean isOreDictBlacklisted(ItemStack itemStack) {
        int[] oreIDs = OreDictionary.getOreIDs(itemStack);
        for (int oreID : oreIDs) {
            String oreName = OreDictionary.getOreName(oreID);
            if (BLACKLISTED_OREDICT_NAMES.contains(oreName)) {
                return true;
            }
        }
        return false;
    }

    public static String findOreDictionaryName(Object input) {
        if (!(input instanceof Iterable<?>candidates)) return null;

        // Thaumcraft stores Forge's live list in place of the original ore name. Match that
        // exact list, not a shared ore tag that could broaden an explicit set of alternatives.
        for (Object candidate : candidates) {
            if (!(candidate instanceof ItemStack stack) || stack.getItem() == null) continue;

            for (int oreId : OreDictionary.getOreIDs(stack)) {
                String oreName = OreDictionary.getOreName(oreId);
                if (OreDictionary.getOres(oreName, false) == input) {
                    return oreName;
                }
            }
            return null;
        }
        return null;
    }
}
