package com.science.gtnl.common.recipe.oreDictionary;

import static gregtech.api.recipe.RecipeMaps.latheRecipes;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeBuilder.TICKS;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IOreRecipeRegistrator;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class SteamCarpenterOreRecipe implements IOreRecipeRegistrator {

    public RecipeMap<?> SCR = GTNLRecipeMaps.SteamCarpenterRecipes;

    public SteamCarpenterOreRecipe() {
        OrePrefixes.log.add(this);
    }

    @Override
    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName,
        ItemStack aStack) {
        if (aOreDictName.equals("logWood")) {

            for (int i = 0; i < 32767; i++) {
                if ((GTUtility.areStacksEqual(
                    GTModHandler.getSmeltingOutput(new ItemStack(aStack.getItem(), 1, i), false, null),
                    new ItemStack(Items.coal, 1, 1)))) {
                    GTModHandler.removeFurnaceSmelting(new ItemStack(aStack.getItem(), 1, i));
                }

                ItemStack tStack = GTModHandler.getRecipeOutput(new ItemStack(aStack.getItem(), 1, i));
                if (tStack == null) {
                    if (i >= 16) {
                        break;
                    }
                } else {
                    ItemStack tPlanks = GTUtility.copyOrNull(tStack);
                    if (tPlanks != null) {
                        tPlanks.stackSize = (tPlanks.stackSize * 3 / 2);

                        RecipeBuilder.builder()
                            .itemInputs(new ItemStack(aStack.getItem(), 1, i))
                            .itemOutputs(
                                GTUtility.copyOrNull(tPlanks),
                                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Wood, 1L))
                            .duration(10 * SECONDS)
                            .eut(8)
                            .addTo(SCR);
                    }
                }
            }
        }
        Item gtPlank = GTOreDictUnificator.get(OrePrefixes.plate, Materials.Wood, 1L)
            .getItem();
        if (aOreDictName.startsWith("plankWood") && aStack.getItem() != gtPlank) {
            RecipeBuilder.builder()
                .itemInputs(GTUtility.copyAmount(1, aStack), GTUtility.getIntegratedCircuit(1))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stick, Materials.Wood, 2L))
                .duration(10 * TICKS)
                .eut(8)
                .addTo(latheRecipes);
            RecipeBuilder.builder()
                .itemInputs(GTUtility.copyAmount(1, aStack), GTUtility.getIntegratedCircuit(1))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stick, Materials.Wood, 2L))
                .duration(2 * SECONDS)
                .eut(8)
                .addTo(SCR);
            RecipeBuilder.builder()
                .itemInputs(GTUtility.copyAmount(1, aStack), GTUtility.getIntegratedCircuit(2))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.slab, Materials.Wood, 2L))
                .duration(2 * SECONDS)
                .eut(8)
                .addTo(SCR);

            RecipeBuilder.builder()
                .itemInputs(GTUtility.copyAmount(8, aStack), GTUtility.getIntegratedCircuit(8))
                .itemOutputs(new ItemStack(Blocks.chest, 1))
                .duration(2 * SECONDS)
                .eut(4)
                .addTo(SCR);

        }
    }
}
