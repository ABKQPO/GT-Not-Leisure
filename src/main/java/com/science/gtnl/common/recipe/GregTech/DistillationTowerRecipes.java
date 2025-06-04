package com.science.gtnl.common.recipe.GregTech;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;

public class DistillationTowerRecipes implements IRecipePool {

    final RecipeMap<?> DTR = RecipeMaps.distillationTowerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.FluorineCrackedNaquadah.getFluidOrGas(1000))
            .fluidOutputs(
                GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(800),
                Materials.NitricAcid.getFluid(200),
                MaterialPool.EnrichedNaquadahWaste.getFluidOrGas(100),
                Materials.Ammonia.getGas(200),
                Materials.Fluorine.getGas(200))
            .specialValue(0)
            .duration(600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(DTR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Barium, 1))
            .fluidInputs(MaterialPool.EnrichedNaquadahWaste.getFluidOrGas(2000))
            .fluidOutputs(
                Materials.SulfuricAcid.getFluid(500),
                GGMaterial.enrichedNaquadahRichSolution.getFluidOrGas(250),
                GGMaterial.naquadriaRichSolution.getFluidOrGas(100))
            .outputChances(5000)
            .specialValue(0)
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(DTR);

        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.RadonCrackedEnrichedNaquadah.getFluidOrGas(1000))
            .fluidOutputs(
                GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(800),
                MaterialPool.NaquadriaWaste.getFluidOrGas(100),
                Materials.Radon.getGas(200),
                Materials.Fluorine.getGas(200))
            .specialValue(0)
            .duration(300)
            .eut(TierEU.RECIPE_LuV)
            .addTo(DTR);

        RecipeBuilder.builder()
            .itemOutputs(GGMaterial.triniumSulphate.get(OrePrefixes.dust, 1))
            .fluidInputs(MaterialPool.NaquadriaWaste.getFluidOrGas(2000))
            .fluidOutputs(
                GGMaterial.naquadriaRichSolution.getFluidOrGas(250),
                GGMaterial.enrichedNaquadahRichSolution.getFluidOrGas(100))
            .outputChances(5000)
            .specialValue(0)
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(DTR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnderPearl, 1))
            .fluidInputs(MaterialPool.FluidEnderAir.getFluidOrGas(200000))
            .fluidOutputs(
                Materials.NitrogenDioxide.getGas(120000),
                Materials.Deuterium.getGas(40000),
                Materials.Helium.getGas(15000),
                Materials.Helium_3.getGas(12000),
                Materials.Tritium.getGas(10000),
                WerkstoffLoader.Krypton.getFluidOrGas(1000),
                WerkstoffLoader.Xenon.getFluidOrGas(1000),
                Materials.Radon.getGas(1000))
            .outputChances(1000)
            .specialValue(0)
            .duration(2000)
            .eut(TierEU.RECIPE_IV)
            .addTo(DTR);
    }
}
