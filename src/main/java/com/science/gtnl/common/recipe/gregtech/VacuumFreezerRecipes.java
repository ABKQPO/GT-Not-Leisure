package com.science.gtnl.common.recipe.gregtech;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.TierEU;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;

public class VacuumFreezerRecipes implements IRecipePool {

    public RecipeMap<?> CNCR = RecipeMaps.vacuumFreezerRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .fluidInputs(MaterialPool.EnderAir.getFluidOrGas(4000))
            .fluidOutputs(MaterialPool.LiquidEnderAir.getFluidOrGas(4000))
            .specialValue(0)
            .duration(80)
            .eut(TierEU.RECIPE_HV)
            .addTo(CNCR);
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.NetherAir.getFluid(1_000))
            .fluidOutputs(Materials.NetherSemiFluid.getFluid(1_000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(CNCR);
    }
}
