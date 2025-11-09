package com.science.gtnl.common.recipe.gtnl;

import static fox.spiteful.avaritia.items.LudicrousItems.bigPearl;
import static thaumcraft.common.config.ConfigItems.*;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipeTools;
import com.science.gtnl.loader.RecipePool;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GTUtility;

public class InfusionCraftingRecipes implements IRecipePool {

    public ItemStack[] itemsUnconsumed = new ItemStack[] { new ItemStack(bigPearl) };

    public ItemStack[] checkInputSpecial(ItemStack... itemStacks) {
        baseLoop: for (ItemStack i : itemStacks) {
            for (ItemStack u : itemsUnconsumed) {
                if (GTUtility.areStacksEqual(i, u)) {
                    i.stackSize = 0;
                    break baseLoop;
                }
            }
        }
        return itemStacks;
    }

    public Set<Item> skips;

    public boolean shouldSkip(Item item) {
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
        TCRecipeTools.getInfusionCraftingRecipe();

        IRecipeMap IIC = RecipePool.IndustrialInfusionCraftingRecipes;
        for (TCRecipeTools.InfusionCraftingRecipe Recipe : TCRecipeTools.ICR) {
            if (shouldSkip(
                Recipe.getOutput()
                    .getItem()))
                continue;

            GTValues.RA.stdBuilder()
                .ignoreCollision()
                .clearInvalid()
                .itemInputsUnified(checkInputSpecial(Recipe.getInputItem()))
                .itemOutputs((Recipe.getOutput()))
                .fluidInputs()
                .fluidOutputs()
                .duration(20)
                .eut(TierEU.RECIPE_LV)
                .addTo(IIC);
        }
    }

}
