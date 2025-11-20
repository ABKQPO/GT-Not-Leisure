package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.enums.GTValues.RA;
import static gregtech.api.util.GTRecipeBuilder.TICKS;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.recipes.SteamFusionTierKey;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class SteamFusionReactorRecipes implements IRecipePool {

    public RecipeMap<?> SFRR = RecipePool.SteamFusionReactorRecipes;

    @Override
    public void loadRecipes() {
        RA.stdBuilder()
            .fluidInputs(Materials.Steam.getGas(16000), Materials.Creosote.getFluid(4000))
            .fluidOutputs(FluidUtils.getSuperHeatedSteam(16000))
            .duration(10 * TICKS)
            .eut(0)
            .addTo(SFRR);

        RA.stdBuilder()
            .fluidInputs(FluidUtils.getSuperHeatedSteam(16000), Materials.Creosote.getFluid(4000))
            .fluidOutputs(Materials.DenseSupercriticalSteam.getGas(16000))
            .duration(10 * TICKS)
            .eut(0)
            .addTo(SFRR);

        RA.stdBuilder()
            .fluidInputs(Materials.Water.getFluid(100), Materials.Lava.getFluid(125))
            .fluidOutputs(Materials.DenseSupercriticalSteam.getGas(16000))
            .duration(10 * TICKS)
            .eut(0)
            .metadata(SteamFusionTierKey.INSTANCE, 1)
            .addTo(SFRR);
    }
}
