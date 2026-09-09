package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import appeng.api.AEApi;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class MatterFabricatorRecipes implements IRecipePool {

    public RecipeMap<?> MFR = GTNLRecipeMaps.MatterFabricatorRecipes;

    public static final ItemStack PAINT_BALL = AEApi.instance()
        .definitions()
        .materials()
        .matterBall()
        .maybeStack(1)
        .orNull();

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("gtnl.nei.matter_fabricator_recipes.0")))
            .itemOutputs(GTUtility.copyAmountUnsafe(640, PAINT_BALL))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("gtnl.nei.matter_fabricator_recipes.0")))
            .fluidOutputs(Materials.UUAmplifier.getFluid(2000))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Emerald, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("gtnl.nei.matter_fabricator_recipes.1")))
            .itemOutputs(GTUtility.copyAmountUnsafe(640, PAINT_BALL))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Emerald, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("gtnl.nei.matter_fabricator_recipes.1")))
            .fluidOutputs(Materials.UUAmplifier.getFluid(2000))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Iron, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("gtnl.nei.matter_fabricator_recipes.2")))
            .itemOutputs(GTUtility.copyAmountUnsafe(640 * 9, PAINT_BALL))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.block, Materials.Iron, 1L)
                    .setStackDisplayName(StatCollector.translateToLocal("gtnl.nei.matter_fabricator_recipes.2")))
            .fluidOutputs(Materials.UUAmplifier.getFluid(20000))
            .eut(120)
            .duration(200)
            .fake()
            .addTo(MFR);
    }
}
