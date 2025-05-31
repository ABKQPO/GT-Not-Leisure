package com.science.gtnl.common.recipe.GregTech;

import com.science.gtnl.loader.IRecipePool;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsKevlar;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.metadata.PCBFactoryTierKey;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.material.MaterialsElements;

public class PCBFactoryRecipes implements IRecipePool {

    final RecipeMap<?> PCBFR = RecipeMaps.pcbFactoryRecipes;
    private static final PCBFactoryTierKey TIER = PCBFactoryTierKey.INSTANCE;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, MaterialsKevlar.Kevlar, 1),
                WerkstoffLoader.Rhodium.get(OrePrefixes.foil, 32),
                MaterialsElements.getInstance().RUTHENIUM.getFoil(32))
            .itemOutputs(ItemList.Circuit_Board_Optical.get(1))
            .fluidInputs(
                Materials.SulfuricAcid.getFluid(1000),
                Materials.Iron.getPlasma(100),
                Materials.IronIIIChloride.getFluid(4000))
            .duration(300)
            .metadata(TIER, 1)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(PCBFR);
    }
}
