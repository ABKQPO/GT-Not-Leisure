package com.science.gtnl.common.recipe.GTNL;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;

public class SteamCrackerRecipes implements IRecipePool {

    final RecipeMap<?> SCR = RecipeRegister.SteamCrackerRecipes;

    @Override
    public void loadRecipes() {
        Materials[][] matPairs = { { Materials.SulfuricGas, Materials.Gas },
            { Materials.SulfuricNaphtha, Materials.Naphtha }, { Materials.SulfuricLightFuel, Materials.LightFuel },
            { Materials.SulfuricHeavyFuel, Materials.HeavyFuel }, };

        for (Materials[] pair : matPairs) {
            Materials inputMat = pair[0];
            Materials outputMat = pair[1];

            for (int circuit = 1; circuit <= 3; circuit++) {
                GTValues.RA.stdBuilder()
                    .itemInputs(GTUtility.getIntegratedCircuit(circuit))
                    .fluidInputs(inputMat.getGas(1000))
                    .fluidOutputs(getSteamCrackedFluid(outputMat, circuit, 400))
                    .specialValue(0)
                    .duration(200)
                    .eut(30)
                    .addTo(SCR);
            }
        }
    }

    private static FluidStack getSteamCrackedFluid(Materials base, int circuit, int amount) {
        return switch (circuit) {
            case 1 -> base.getLightlySteamCracked(amount);
            case 2 -> base.getModeratelySteamCracked(amount);
            case 3 -> base.getSeverelySteamCracked(amount);
            default -> throw new IllegalArgumentException("Unsupported circuit: " + circuit);
        };
    }
}
