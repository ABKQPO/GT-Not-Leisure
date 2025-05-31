package com.science.gtnl.common.recipe.GregTech.ServerStart;

import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.science.gtnl.loader.IRecipePool;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class VacuumFurnaceRecipes implements IRecipePool {

    final RecipeMap<?> VFR = GTPPRecipeMaps.vacuumFurnaceRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 32),
                WerkstoffLoader.Rhodium.get(OrePrefixes.dust, 32),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tellurium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 48),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 32))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PlatinumFlotationFroth, 4000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.RedMud, 200), Materials.Water.getFluid(2000))
            .eut(TierEU.RECIPE_LuV)
            .metadata(COIL_HEAT, 5500)
            .duration(2400)
            .addTo(VFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Erbium, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 48),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 32),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lutetium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 8))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.MonaziteFlotationFroth, 4000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.RedMud, 200), Materials.Water.getFluid(2000))
            .eut(TierEU.RECIPE_ZPM)
            .metadata(COIL_HEAT, 5500)
            .duration(2400)
            .addTo(VFR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 64),
                MaterialsElements.getInstance().RUTHENIUM.getDust(48),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Promethium, 32),
                MaterialsElements.getInstance().HAFNIUM.getDust(16))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PentlanditeFlotationFroth, 4000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.RedMud, 200), Materials.Water.getFluid(2000))
            .eut(TierEU.RECIPE_LuV)
            .metadata(COIL_HEAT, 5500)
            .duration(2400)
            .addTo(VFR);
    }
}
