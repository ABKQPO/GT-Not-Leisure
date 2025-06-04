package com.science.gtnl.common.recipe.GTNL;

import static fox.spiteful.avaritia.items.LudicrousItems.bigPearl;
import static thaumcraft.common.config.ConfigItems.*;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.Utils;
import com.science.gtnl.common.recipe.Thaumcraft.TCRecipeTools;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;

public class InfusionCraftingRecipes implements IRecipePool {

    protected ItemStack[] itemsUnconsumed = new ItemStack[] { new ItemStack(bigPearl) };

    protected ItemStack[] checkInputSpecial(ItemStack... itemStacks) {
        baseLoop: for (ItemStack i : itemStacks) {
            for (ItemStack u : itemsUnconsumed) {
                if (Utils.metaItemEqual(i, u)) {
                    i.stackSize = 0;
                    break baseLoop;
                }
            }
        }
        return itemStacks;
    }

    protected Set<Item> skips;

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
        TCRecipeTools.getInfusionCraftingRecipe();

        final IRecipeMap IIC = RecipeRegister.IndustrialInfusionCraftingRecipes;
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
