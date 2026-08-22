package com.science.gtnl.common.recipe.gtnl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipeTools;

import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMetadataKey;
import gregtech.api.recipe.metadata.SimpleRecipeMetadataKey;
import gregtech.api.util.GTUtility;
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

            List<ItemStack> inputItems = new ArrayList<>();
            for (Object input : recipe.getInputItems()) {
                if (input instanceof ItemStack) {
                    ItemStack copy = ((ItemStack) input).copy();
                    copy.stackSize = 1;
                    inputItems.add(copy);
                } else if (input instanceof List) {
                    List<ItemStack> oreDictItems = (List<ItemStack>) input;
                    if (!oreDictItems.isEmpty() && !isOreDictBlacklisted(oreDictItems.get(0))) {
                        ItemStack copy = oreDictItems.get(0)
                            .copy();
                        copy.stackSize = 1;
                        inputItems.add(copy);
                    }
                }
            }

            if (inputItems.size() == 1) inputItems.add(GTUtility.getIntegratedCircuit(1));

            ItemStack output = recipe.getOutput()
                .copy();
            output.stackSize = 1;

            TCRecipeTools.addArcaneRecipe(
                IAA,
                TCRecipeTools.checkInputSpecial(inputItems.toArray(new ItemStack[0])),
                new ItemStack[] { output },
                recipe.getInputAspects(),
                recipe.getResearch(),
                ARCANE_VIS,
                ARCANE_RESEARCH);
        }

        // Shapeless
        for (TCRecipeTools.ShapelessArcaneCraftingRecipe recipe : TCRecipeTools.ShaplessAR) {
            if (TCRecipeTools.shouldSkipThaumcraftItem(
                recipe.getOutput()
                    .getItem())) {
                continue;
            }

            List<ItemStack> inputItems = new ArrayList<>();
            for (Object input : recipe.getInputItems()) {
                if (input instanceof ItemStack) {
                    ItemStack copy = ((ItemStack) input).copy();
                    copy.stackSize = 1;
                    inputItems.add(copy);
                } else if (input instanceof List) {
                    List<ItemStack> oreDictItems = (List<ItemStack>) input;
                    if (!oreDictItems.isEmpty() && !isOreDictBlacklisted(oreDictItems.get(0))) {
                        ItemStack copy = oreDictItems.get(0)
                            .copy();
                        copy.stackSize = 1;
                        inputItems.add(copy);
                    }
                }
            }

            ItemStack output = recipe.getOutput()
                .copy();
            output.stackSize = 1;

            TCRecipeTools.addArcaneRecipe(
                IAA,
                TCRecipeTools.checkInputSpecial(inputItems.toArray(new ItemStack[0])),
                new ItemStack[] { output },
                recipe.getInputAspects(),
                recipe.getResearch(),
                ARCANE_VIS,
                ARCANE_RESEARCH);
        }
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
}
