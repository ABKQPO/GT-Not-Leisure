package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;
import com.science.gtnl.utils.recipes.metadata.SteamAmountMetadata;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class CactusWonderFakeRecipes implements IRecipePool {

    private static final SteamAmountMetadata OFFER_VALUE = SteamAmountMetadata.INSTANCE;
    public RecipeMap<?> CWFR = GTNLRecipeMaps.CactusWonderFakeRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 8000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.BlockCactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 90000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CompressedCactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 1012500L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.DoubleCompressedCactusCharcoal.get(1))
            .fluidOutputs(GTModHandler.getSuperHeatedSteam(128000))
            .metadata(OFFER_VALUE, 11390625L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.TripleCompressedCactusCharcoal.get(1))
            .fluidOutputs(GTModHandler.getSuperHeatedSteam(128000))
            .metadata(OFFER_VALUE, 128144531L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.QuadrupleCompressedCactusCharcoal.get(1))
            .fluidOutputs(Materials.DenseSupercriticalSteam.getGas(512000))
            .metadata(OFFER_VALUE, 1441625977L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.QuintupleCompressedCactusCharcoal.get(1))
            .fluidOutputs(Materials.DenseSupercriticalSteam.getGas(512000))
            .metadata(OFFER_VALUE, 16218292236L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 16000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.BlockCactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 180000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.CompressedCactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 2025000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.DoubleCompressedCactusCoke.get(1))
            .fluidOutputs(GTModHandler.getSuperHeatedSteam(128000))
            .metadata(OFFER_VALUE, 22781250L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.TripleCompressedCactusCoke.get(1))
            .fluidOutputs(GTModHandler.getSuperHeatedSteam(128000))
            .metadata(OFFER_VALUE, 256289063L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.QuadrupleCompressedCactusCoke.get(1))
            .fluidOutputs(Materials.DenseSupercriticalSteam.getGas(512000))
            .metadata(OFFER_VALUE, 2883251953L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.QuintupleCompressedCactusCoke.get(1))
            .fluidOutputs(Materials.DenseSupercriticalSteam.getGas(512000))
            .metadata(OFFER_VALUE, 32436584473L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

    }
}
