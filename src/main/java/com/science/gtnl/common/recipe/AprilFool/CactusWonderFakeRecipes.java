package com.science.gtnl.common.recipe.AprilFool;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.recipes.SteamAmountSpecialValue;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class CactusWonderFakeRecipes implements IRecipePool {

    private static final SteamAmountSpecialValue OFFER_VALUE = SteamAmountSpecialValue.INSTANCE;
    final RecipeMap<?> CWFR = RecipeRegister.CactusWonderFakeRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.CactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 8000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.BlockCactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 90000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.CompressedCactusCharcoal.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 1012500L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.DoubleCompressedCactusCharcoal.get(1))
            .fluidOutputs(FluidUtils.getSuperHeatedSteam(128000))
            .metadata(OFFER_VALUE, 11390625L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.TripleCompressedCactusCharcoal.get(1))
            .fluidOutputs(FluidUtils.getSuperHeatedSteam(128000))
            .metadata(OFFER_VALUE, 128144531L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.QuadrupleCompressedCactusCharcoal.get(1))
            .fluidOutputs(FluidRegistry.getFluidStack("supercriticalsteam", 512000))
            .metadata(OFFER_VALUE, 1441625977L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.QuintupleCompressedCactusCharcoal.get(1))
            .fluidOutputs(FluidRegistry.getFluidStack("supercriticalsteam", 512000))
            .metadata(OFFER_VALUE, 16218292236L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.CactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 16000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.BlockCactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 180000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.CompressedCactusCoke.get(1))
            .fluidOutputs(Materials.Steam.getGas(64000))
            .metadata(OFFER_VALUE, 2025000L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.DoubleCompressedCactusCoke.get(1))
            .fluidOutputs(FluidUtils.getSuperHeatedSteam(128000))
            .metadata(OFFER_VALUE, 22781250L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.TripleCompressedCactusCoke.get(1))
            .fluidOutputs(FluidUtils.getSuperHeatedSteam(128000))
            .metadata(OFFER_VALUE, 256289063L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.QuadrupleCompressedCactusCoke.get(1))
            .fluidOutputs(FluidRegistry.getFluidStack("supercriticalsteam", 512000))
            .metadata(OFFER_VALUE, 2883251953L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.QuintupleCompressedCactusCoke.get(1))
            .fluidOutputs(FluidRegistry.getFluidStack("supercriticalsteam", 512000))
            .metadata(OFFER_VALUE, 32436584473L)
            .duration(20)
            .eut(0)
            .fake()
            .addTo(CWFR);

    }
}
