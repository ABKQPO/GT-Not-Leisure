package com.science.gtnl.common.recipe.GregTech;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class AlloySmelterRecipes implements IRecipePool {

    final RecipeMap<?> aSR = RecipeMaps.alloySmelterRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.glowstone_dust, 1), new ItemStack(Items.clay_ball, 1))
            .itemOutputs(GTNLItemList.ClayedGlowstone.get(2))
            .specialValue(0)
            .duration(200)
            .eut(16)
            .addTo(aSR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 2L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bronze, 1L))
            .itemOutputs(MaterialPool.Stronze.get(OrePrefixes.ingot, 3))
            .specialValue(0)
            .duration(150)
            .eut(128)
            .addTo(aSR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 2L),
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Bronze, 1L))
            .itemOutputs(MaterialPool.Stronze.get(OrePrefixes.ingot, 3))
            .specialValue(0)
            .duration(150)
            .eut(128)
            .addTo(aSR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 2L),
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Bronze, 1L))
            .itemOutputs(MaterialPool.Stronze.get(OrePrefixes.ingot, 3))
            .specialValue(0)
            .duration(150)
            .eut(128)
            .addTo(aSR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 2L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bronze, 1L))
            .itemOutputs(MaterialPool.Stronze.get(OrePrefixes.ingot, 3))
            .specialValue(0)
            .duration(150)
            .eut(128)
            .addTo(aSR);
    }
}
