package com.science.gtnl.common.recipe.AprilFool;

import static gregtech.api.enums.GTValues.RA;
import static gregtech.api.util.GTRecipeBuilder.TICKS;

import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.recipes.SteamFusionTierKey;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class SteamFusionReactorRecipes implements IRecipePool {

    final RecipeMap<?> SFRR = RecipeRegister.SteamFusionReactorRecipes;

    @Override
    public void loadRecipes() {
        RA.stdBuilder()
            .fluidInputs(GTModHandler.getSteam(16000), Materials.Creosote.getFluid(4000))
            .fluidOutputs(FluidUtils.getSuperHeatedSteam(16000))
            .duration(10 * TICKS)
            .eut(0)
            .addTo(SFRR);

        RA.stdBuilder()
            .fluidInputs(FluidUtils.getSuperHeatedSteam(16000), Materials.Creosote.getFluid(4000))
            .fluidOutputs(FluidRegistry.getFluidStack("supercriticalsteam", 16000))
            .duration(10 * TICKS)
            .eut(0)
            .addTo(SFRR);

        RA.stdBuilder()
            .fluidInputs(Materials.Water.getFluid(100), Materials.Lava.getFluid(125))
            .fluidOutputs(FluidRegistry.getFluidStack("supercriticalsteam", 16000))
            .duration(10 * TICKS)
            .eut(0)
            .metadata(SteamFusionTierKey.INSTANCE, 1)
            .addTo(SFRR);
    }
}
