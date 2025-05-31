package com.science.gtnl.common.recipe.GregTech;

import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.science.gtnl.common.item.items.MilledOre;
import com.science.gtnl.loader.IRecipePool;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class VacuumFurnaceRecipes implements IRecipePool {

    final RecipeMap<?> VFR = GTPPRecipeMaps.vacuumFurnaceRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 32),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Indium, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 32),
                GGMaterial.extremelyUnstableNaquadah.get(OrePrefixes.dust, 2))
            .fluidInputs(FluidUtils.getFluidStack(MilledOre.NaquadahEnrichedFlotationFroth, 4000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.RedMud, 200), Materials.Water.getFluid(2000))
            .eut(TierEU.RECIPE_ZPM)
            .metadata(COIL_HEAT, 5500)
            .duration(2400)
            .addTo(VFR);
    }
}
