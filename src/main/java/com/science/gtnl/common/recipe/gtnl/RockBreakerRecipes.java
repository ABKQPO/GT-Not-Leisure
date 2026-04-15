package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import appeng.api.AEApi;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public class RockBreakerRecipes implements IRecipePool {

    public RecipeMap<?> RBR = GTNLRecipeMaps.RockBreakerRecipes;

    @Override
    public void loadRecipes() {
        var aeBlocks = AEApi.instance()
            .definitions()
            .blocks();

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .itemOutputs(new ItemStack(Blocks.cobblestone, 1))
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .itemOutputs(new ItemStack(Blocks.stone, 1))
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L))
            .itemOutputs(new ItemStack(Blocks.obsidian, 1))
            .duration(6 * GTRecipeBuilder.SECONDS + 8 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        if (Mods.EtFuturumRequiem.isModLoaded()) {
            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(4),
                    GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "blue_ice", 0, 0),
                    new ItemStack(Blocks.soul_sand, 0))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stone, Materials.Basalt, 1L))
                .duration(16 * GTRecipeBuilder.TICKS)
                .eut(TierEU.RECIPE_LV)
                .addTo(RBR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(5),
                    GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "magma", 0, 0),
                    new ItemStack(Blocks.soul_sand, 0))
                .itemOutputs(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "cobbled_deepslate", 1, 0))
                .duration(16 * GTRecipeBuilder.TICKS)
                .eut(TierEU.RECIPE_LV)
                .addTo(RBR);
        }

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1L))
            .itemOutputs(new ItemStack(Blocks.netherrack, 1))
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                GTUtility.copyAmountUnsafe(
                    0,
                    aeBlocks.skyStone()
                        .maybeStack(1)
                        .orNull()))
            .itemOutputs(
                aeBlocks.skyStone()
                    .maybeStack(1)
                    .orNull())
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RBR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(8), new ItemStack(Blocks.end_stone, 0))
            .itemOutputs(new ItemStack(Blocks.end_stone, 1))
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RBR);
    }
}
