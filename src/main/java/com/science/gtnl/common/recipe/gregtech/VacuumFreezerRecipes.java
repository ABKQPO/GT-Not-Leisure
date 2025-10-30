package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.config.MainConfig;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
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
        // 半流质下界空气增产 x10
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.NetherAir.getFluid(1_000))
            .fluidOutputs(Materials.NetherSemiFluid.getFluid(1_000))
            .duration(200)
            .eut(TierEU.RECIPE_HV)
            .addTo(CNCR);

        if (MainConfig.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadDeleteRecipe() {
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.NetherAir.getFluid(1_000))
            .fluidOutputs(Materials.NetherSemiFluid.getFluid(100))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(CNCR);
        GTValues.RA.stdBuilder() // Reaction
            .fluidInputs(Materials.PrismaticGas.getFluid(4000), Materials.LiquidNitrogen.getGas(12000))
            .fluidOutputs(Materials.PrismaticAcid.getFluid(16000))
            .duration(25 * SECONDS)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(CNCR);
    }
}
