package com.science.gtnl.common.recipe.GregTech;

import static com.science.gtnl.Utils.enums.Mods.ScienceNotLeisure;
import static gregtech.api.util.GTModHandler.getModItem;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.metadata.CompressionTierKey;
import gregtech.api.util.GTUtility;

public class CompressorRecipes implements IRecipePool {

    final CompressionTierKey COMPRESSION_TIER = CompressionTierKey.INSTANCE;
    final RecipeMap<?> NCR = RecipeMaps.neutroniumCompressorRecipes;
    final RecipeMap<?> CR = RecipeMaps.compressorRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(GTUtility.copyAmountUnsafe(7296, getModItem(ScienceNotLeisure.ID, "StargateTier9", 1, 0)))
            .itemOutputs(GTNLItemList.StargateSingularity.get(1))
            .duration(120 * SECONDS)
            .eut(TierEU.RECIPE_MAX)
            .metadata(COMPRESSION_TIER, 2)
            .addTo(NCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, ItemList.Shape_Mold_Ingot.get(1)))
            .itemOutputs(MaterialPool.CompressedSteam.get(OrePrefixes.ingot, 1))
            .fluidInputs(Materials.Steam.getGas(100000))
            .duration(80)
            .metadata(COMPRESSION_TIER, 2)
            .eut(512)
            .addTo(CR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, ItemList.Shape_Mold_Ingot.get(1)))
            .itemOutputs(MaterialPool.CompressedSteam.get(OrePrefixes.ingot, 1))
            .fluidInputs(FluidRegistry.getFluidStack("supercriticalsteam", 2000))
            .duration(80)
            .metadata(COMPRESSION_TIER, 2)
            .eut(512)
            .addTo(CR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTNLItemList.BlazeCube.get(9))
            .itemOutputs(GTNLItemList.BlazeCubeBlock.get(1))
            .duration(300)
            .eut(TierEU.RECIPE_LV)
            .addTo(CR);
    }
}
