package com.science.gtnl.common.recipe.GregTech;

import static gregtech.api.enums.Materials.*;
import static gregtech.api.util.GTRecipeConstants.SIEVERT;

import com.dreammaster.gthandler.CustomItemList;
import com.science.gtnl.loader.IRecipePool;

import bartworks.API.recipe.BartWorksRecipeMaps;
import bartworks.util.BWUtil;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.recipe.Sievert;

public class BacterialVatRecipes implements IRecipePool {

    final RecipeMap<?> BVR = BartWorksRecipeMaps.bacterialVatRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Chip_Stemcell.get(64),
                GTOreDictUnificator.get(OrePrefixes.dust, NaquadahEnriched, 1L))
            .itemOutputs(ItemList.Circuit_Chip_Biocell.get(64))
            .fluidInputs(Materials.BioMediumSterilized.getFluid(1000))
            .eut(TierEU.RECIPE_ZPM)
            .metadata(SIEVERT, new Sievert(BWUtil.calculateSv(Materials.NaquadahEnriched), false))
            .duration(800)
            .addTo(BVR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Circuit_Chip_Stemcell.get(64),
                CustomItemList.TCetiESeaweedExtract.get(16L),
                GTOreDictUnificator.get(OrePrefixes.dust, Tritanium, 1L))
            .fluidInputs(Materials.GrowthMediumRaw.getFluid(10))
            .fluidOutputs(Materials.BioMediumRaw.getFluid(10))
            .eut(TierEU.RECIPE_EV)
            .metadata(SIEVERT, new Sievert(BWUtil.calculateSv(Materials.NaquadahEnriched), false))
            .duration(1200)
            .addTo(BVR);

    }
}
