package com.science.gtnl.common.recipe.GTNL;

import static thaumcraft.common.config.ConfigItems.itemJarNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.common.recipe.Thaumcraft.TCRecipeTools;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;

public class ShapedArcaneCraftingRecipes implements IRecipePool {

    protected Set<Item> skips;

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

    protected boolean shouldSkip(Item item) {
        if (null == skips) {
            skips = new HashSet<>();
            skips.add(itemJarNode);
            if (Mods.ThaumicBases.isModLoaded()) {
                Item revolver = GameRegistry.findItem(Mods.ThaumicBases.ID, "revolver");
                if (null != revolver) {
                    skips.add(revolver);
                }
            }
            if (Mods.Gadomancy.isModLoaded()) {
                Item itemEtherealFamiliar = GameRegistry.findItem(Mods.Gadomancy.ID, "ItemEtherealFamiliar");
                if (null != itemEtherealFamiliar) {
                    skips.add(itemEtherealFamiliar);
                }
            }
        }

        return skips.contains(item);
    }

    @Override
    public void loadRecipes() {
        TCRecipeTools.getShapedArcaneCraftingRecipe();
        TCRecipeTools.getShapelessArcaneCraftingRecipe();

        final IRecipeMap IAA = RecipeRegister.IndustrialShapedArcaneCraftingRecipes;

        for (TCRecipeTools.ShapedArcaneCraftingRecipe recipe : TCRecipeTools.ShapedAR) {
            if (shouldSkip(
                recipe.getOutput()
                    .getItem())) {
                continue;
            }

            List<ItemStack> inputItems = new ArrayList<>();
            for (Object input : recipe.getInputItems()) {
                if (input instanceof ItemStack) {
                    inputItems.add((ItemStack) input);
                } else if (input instanceof List) {
                    List<ItemStack> oreDictItems = (List<ItemStack>) input;
                    if (!oreDictItems.isEmpty() && !isOreDictBlacklisted(oreDictItems.get(0))) {
                        inputItems.add(oreDictItems.get(0));
                    }
                }
            }

            GTValues.RA.stdBuilder()
                .ignoreCollision()
                .clearInvalid()
                .itemInputsUnified(inputItems.toArray(new ItemStack[0]))
                .itemOutputs(recipe.getOutput())
                .duration(20)
                .eut(TierEU.RECIPE_LV)
                .addTo(IAA);
        }

        for (TCRecipeTools.ShapelessArcaneCraftingRecipe recipe : TCRecipeTools.ShaplessAR) {
            if (shouldSkip(
                recipe.getOutput()
                    .getItem())) {
                continue;
            }

            List<ItemStack> inputItems = new ArrayList<>();
            for (Object input : recipe.getInputItems()) {
                if (input instanceof ItemStack) {
                    inputItems.add((ItemStack) input);
                } else if (input instanceof List) {
                    List<ItemStack> oreDictItems = (List<ItemStack>) input;
                    if (!oreDictItems.isEmpty() && !isOreDictBlacklisted(oreDictItems.get(0))) {
                        inputItems.add(oreDictItems.get(0));
                    }
                }
            }

            GTValues.RA.stdBuilder()
                .ignoreCollision()
                .clearInvalid()
                .itemInputsUnified(inputItems.toArray(new ItemStack[0]))
                .itemOutputs(recipe.getOutput())
                .duration(20)
                .eut(TierEU.RECIPE_LV)
                .addTo(IAA);
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
