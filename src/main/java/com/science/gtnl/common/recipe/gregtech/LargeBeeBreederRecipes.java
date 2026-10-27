// SPDX-License-Identifier: MIT
package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.recipe.RecipeMaps.assemblerRecipes;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public final class LargeBeeBreederRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        GTRecipeBuilder.builder()
            .itemInputs(
                ItemList.Hull_MV.get(1L),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 8),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.StainlessSteel, 1),
                new Object[] { OrePrefixes.circuit.get(Materials.MV), 4 },
                ItemList.Electric_Motor_MV.get(2L),
                ItemList.Electric_Piston_MV.get(2L),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(Materials.SolderingAlloy.getMolten(576))
            .itemOutputs(GTNLItemList.LargeBeeBreeder.get(1))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_MV)
            .addTo(assemblerRecipes);
    }
}
