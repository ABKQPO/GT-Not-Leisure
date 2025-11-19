package com.science.gtnl.common.recipe.gtnl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.loader.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.item.material.ItemRune;

public class RuneAltarRecipes implements IRecipePool {

    public RecipeMap<?> RAR = RecipePool.RuneAltarRecipes;

    @Override
    public void loadRecipes() {
        for (RecipeRuneAltar recipe : BotaniaAPI.runeAltarRecipes) {

            List<Object> inputs = recipe.getInputs();
            List<Object> finalInputs = new ArrayList<>();

            List<AbstractMap.SimpleEntry<ItemStack, Integer>> stackCounts = new ArrayList<>();

            Object2IntMap<String> oredictCount = new Object2IntOpenHashMap<>();

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
                    oredictCount.put(string, oredictCount.getOrDefault(string, 0) + 1);
                }
            }

            for (AbstractMap.SimpleEntry<ItemStack, Integer> entry : stackCounts) {
                ItemStack stack = entry.getKey()
                    .copy();
                stack.stackSize = entry.getValue();
                finalInputs.add(stack);
            }

            oredictCount.forEach((name, count) -> {
                List<ItemStack> oredict = OreDictionary.getOres(name);
                if (oredict.isEmpty()) return;

                ItemStack first = oredict.get(0)
                    .copy();
                if (first.getItem() instanceof ItemRune) {
                    first.stackSize = 0;
                } else {
                    first.stackSize = count;
                }
                finalInputs.add(first);
            });

            GTValues.RA.stdBuilder()
                .itemInputs(finalInputs.toArray(new Object[0]))
                .itemOutputs(recipe.getOutput())
                .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(recipe.getManaUsage()))
                .duration(40)
                .eut(TierEU.RECIPE_EV)
                .addTo(RAR);
        }
    }

}
