package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.metadata.CompressionTierKey;
import gregtech.api.util.GTModHandler;

public class CompressorRecipes implements IRecipePool {

    final CompressionTierKey COMPRESSION_TIER = CompressionTierKey.INSTANCE;
    public RecipeMap<?> NCR = RecipeMaps.neutroniumCompressorRecipes;
    public RecipeMap<?> CR = RecipeMaps.compressorRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.CompressedStargateTier9.get(7296))
            .itemOutputs(GTNLItemList.StargateSingularity.get(1))
            .duration(120 * SECONDS)
            .eut(TierEU.RECIPE_MAX)
            .metadata(COMPRESSION_TIER, 2)
            .addTo(NCR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Ingot.get(0))
            .itemOutputs(GTNLMaterials.CompressedSteam.get(OrePrefixes.ingot, 1))
            .fluidInputs(Materials.Steam.getGas(100000))
            .duration(80)
            .metadata(COMPRESSION_TIER, 2)
            .eut(512)
            .addTo(CR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Shape_Mold_Ingot.get(0))
            .itemOutputs(GTNLMaterials.CompressedSteam.get(OrePrefixes.ingot, 1))
            .fluidInputs(Materials.DenseSupercriticalSteam.getGas(2000))
            .duration(80)
            .metadata(COMPRESSION_TIER, 2)
            .eut(512)
            .addTo(CR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.BlazeCube.get(9))
            .itemOutputs(GTNLItemList.BlazeCubeBlock.get(1))
            .duration(300)
            .eut(TierEU.RECIPE_LV)
            .addTo(CR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.cobblestone, 64))
            .itemOutputs(GTModHandler.getModItem(Mods.TwilightForest.ID, "tile.GiantCobble", 1))
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(CR);
    }
}
