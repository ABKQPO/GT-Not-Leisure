package com.science.gtnl.common.recipe.GregTech;

import static gregtech.api.util.GTRecipeConstants.COIL_HEAT;

import com.science.gtnl.common.item.items.MilledOre;
import com.science.gtnl.common.recipe.IRecipePool;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.item.chemistry.AgriculturalChem;
import gtPlusPlus.core.item.chemistry.MilledOreProcessing;
import gtPlusPlus.core.material.MaterialsElements;
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
            .fluidOutputs(FluidUtils.getFluidStack(AgriculturalChem.RedMud, 200), FluidUtils.getWater(2000))
            .noOptimize()
            .eut(TierEU.RECIPE_ZPM)
            .metadata(COIL_HEAT, 5500)
            .duration(2400)
            .addTo(VFR);

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
            .fluidInputs(FluidUtils.getFluidStack(MilledOreProcessing.PlatinumFlotationFroth, 4000))
            .fluidOutputs(FluidUtils.getFluidStack(AgriculturalChem.RedMud, 200), FluidUtils.getWater(2000))
            .noOptimize()
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
            .fluidInputs(FluidUtils.getFluidStack(MilledOreProcessing.MonaziteFlotationFroth, 4000))
            .fluidOutputs(FluidUtils.getFluidStack(AgriculturalChem.RedMud, 200), FluidUtils.getWater(2000))
            .noOptimize()
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
                WerkstoffLoader.Ruthenium.get(OrePrefixes.dust, 48),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Promethium, 32),
                MaterialsElements.getInstance().HAFNIUM.getDust(16))
            .fluidInputs(FluidUtils.getFluidStack(MilledOreProcessing.PentlanditeFlotationFroth, 4000))
            .fluidOutputs(FluidUtils.getFluidStack(AgriculturalChem.RedMud, 200), FluidUtils.getWater(2000))
            .noOptimize()
            .eut(TierEU.RECIPE_LuV)
            .metadata(COIL_HEAT, 5500)
            .duration(2400)
            .addTo(VFR);
    }
}
