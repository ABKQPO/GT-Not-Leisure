package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import appeng.api.AEApi;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
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
            .itemInputs()
            .circuit(1)
            .itemOutputs(new ItemStack(Blocks.cobblestone, 1))
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        RecipeBuilder.builder()
            .itemInputs()
            .circuit(2)
            .itemOutputs(new ItemStack(Blocks.stone, 1))
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L))
            .circuit(3)
            .itemOutputs(new ItemStack(Blocks.obsidian, 1))
            .duration(6 * GTRecipeBuilder.SECONDS + 8 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        if (Mods.EtFuturumRequiem.isModLoaded()) {
            RecipeBuilder.builder()
                .itemInputs(ModsItemlist.EtFuturumRequiemBlueIce.get(0), new ItemStack(Blocks.soul_sand, 0))
                .circuit(4)
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stone, Materials.Basalt, 1L))
                .duration(16 * GTRecipeBuilder.TICKS)
                .eut(TierEU.RECIPE_LV)
                .addTo(RBR);

            RecipeBuilder.builder()
                .itemInputs(ModsItemlist.EtFuturumRequiemMagma.get(0), new ItemStack(Blocks.soul_sand, 0))
                .circuit(5)
                .itemOutputs(ModsItemlist.EtFuturumRequiemCobbledDeepslate.get(1))
                .duration(16 * GTRecipeBuilder.TICKS)
                .eut(TierEU.RECIPE_LV)
                .addTo(RBR);
        }

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1L))
            .circuit(6)
            .itemOutputs(new ItemStack(Blocks.netherrack, 1))
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(
                    0,
                    aeBlocks.skyStone()
                        .maybeStack(1)
                        .orNull()))
            .circuit(7)
            .itemOutputs(
                aeBlocks.skyStone()
                    .maybeStack(1)
                    .orNull())
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_EV)
            .addTo(RBR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.end_stone, 0))
            .circuit(8)
            .itemOutputs(new ItemStack(Blocks.end_stone, 1))
            .duration(16 * GTRecipeBuilder.TICKS)
            .eut(TierEU.RECIPE_MV)
            .addTo(RBR);
    }
}
