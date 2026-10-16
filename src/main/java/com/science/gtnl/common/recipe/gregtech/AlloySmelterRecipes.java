package com.science.gtnl.common.recipe.gregtech;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class AlloySmelterRecipes implements IRecipePool {

    public RecipeMap<?> aSR = RecipeMaps.alloySmelterRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.glowstone_dust, 1), new ItemStack(Items.clay_ball, 1))
            .itemOutputs(GTNLItemList.ClayedGlowstone.get(2))
            .duration(200)
            .eut(16)
            .addTo(aSR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 2L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bronze, 1L))
            .itemOutputs(GTNLMaterials.Stronze.get(OrePrefixes.ingot, 3))
            .duration(150)
            .eut(TierEU.RECIPE_MV)
            .addTo(aSR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 2L),
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Bronze, 1L))
            .itemOutputs(GTNLMaterials.Stronze.get(OrePrefixes.ingot, 3))
            .duration(150)
            .eut(TierEU.RECIPE_MV)
            .addTo(aSR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 2L),
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Bronze, 1L))
            .itemOutputs(GTNLMaterials.Stronze.get(OrePrefixes.ingot, 3))
            .duration(150)
            .eut(TierEU.RECIPE_MV)
            .addTo(aSR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 2L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bronze, 1L))
            .itemOutputs(GTNLMaterials.Stronze.get(OrePrefixes.ingot, 3))
            .duration(150)
            .eut(TierEU.RECIPE_MV)
            .addTo(aSR);
    }
}
