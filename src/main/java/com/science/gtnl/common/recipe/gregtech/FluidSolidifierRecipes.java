package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class FluidSolidifierRecipes implements IRecipePool {

    public RecipeMap<?> FSR = RecipeMaps.fluidSolidifierRecipes;

    @Override

    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.block, Materials.BorosilicateGlass, 1))
            .fluidInputs(MaterialsUEVplus.QuarkGluonPlasma.getFluid(1152))
            .itemOutputs(GTNLItemList.QuarkGluonPlasmaReinforcedBoronSilicateGlass.get(1))
            .duration(800)
            .eut(TierEU.RECIPE_UXV)
            .addTo(FSR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.block, Materials.BorosilicateGlass, 1))
            .fluidInputs(GGMaterial.shirabon.getMolten(1152))
            .itemOutputs(GTNLItemList.ShirabonReinforcedBoronSilicateGlass.get(1))
            .duration(800)
            .eut(TierEU.RECIPE_UMV)
            .addTo(FSR);
    }
}
