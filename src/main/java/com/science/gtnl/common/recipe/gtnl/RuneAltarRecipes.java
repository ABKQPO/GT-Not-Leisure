package com.science.gtnl.common.recipe.gtnl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.TierEU;
import gregtech.api.objects.OreDictItemStack;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.item.material.ItemRune;

public class RuneAltarRecipes implements IRecipePool {

    public RecipeMap<?> RAR = GTNLRecipeMaps.RuneAltarRecipes;

    @Override
    public void loadRecipes() {
        for (RecipeRuneAltar recipe : BotaniaAPI.runeAltarRecipes) {

            List<Object> inputs = recipe.getInputs();
            boolean hasOreDict = false;

            List<Object> finalInputs = new ArrayList<>();

            List<AbstractMap.SimpleEntry<ItemStack, Integer>> stackCounts = new ArrayList<>();

            for (Object obj : inputs) {
                if (obj instanceof ItemStack stack) {
                    if (stack.getItem() instanceof ItemRune) {
                        ItemStack zeroRune = stack.copy();
                        zeroRune.stackSize = 0;
                        finalInputs.add(zeroRune);
                    } else {
                        boolean merged = false;
                        for (AbstractMap.SimpleEntry<ItemStack, Integer> entry : stackCounts) {
                            if (GTUtility.areStacksEqual(stack, entry.getKey())) {
                                entry.setValue(entry.getValue() + 1);
                                merged = true;
                                break;
                            }
                        }
                        if (!merged) {
                            stackCounts.add(new AbstractMap.SimpleEntry<>(stack.copy(), 1));
                        }
                    }
                } else if (obj instanceof String string) {
                    hasOreDict = true;
                    finalInputs.add(new OreDictItemStack(string, 1));
                }
            }

            for (AbstractMap.SimpleEntry<ItemStack, Integer> entry : stackCounts) {
                ItemStack stack = entry.getKey()
                    .copy();
                stack.stackSize = entry.getValue();
                finalInputs.add(stack);
            }

            RecipeBuilder builder = RecipeBuilder.builder()
                .itemOutputs(recipe.getOutput())
                .fluidInputs(GTNLMaterials.FluidMana.getFluidOrGas(recipe.getManaUsage()))
                .duration(40)
                .eut(TierEU.RECIPE_EV);

            if (hasOreDict) {
                builder.itemInputs(finalInputs.toArray(new Object[0]));
            } else {
                builder.itemInputs(finalInputs.toArray(new ItemStack[0]));
            }
            builder.addTo(RAR);
        }
    }

}
