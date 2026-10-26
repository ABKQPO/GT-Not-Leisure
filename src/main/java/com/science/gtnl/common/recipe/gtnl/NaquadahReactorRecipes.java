package com.science.gtnl.common.recipe.gtnl;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;
import com.science.gtnl.utils.recipes.metadata.NaquadahReactorMetadata;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import it.unimi.dsi.fastutil.ints.IntLongPair;

public class NaquadahReactorRecipes implements IRecipePool {

    public NaquadahReactorMetadata REACTOR_TIER = NaquadahReactorMetadata.INSTANCE;
    public RecipeMap<?> NRR = GTNLRecipeMaps.NaquadahReactorRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(16), Materials.Hydrogen.getGas(1600))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIDepleted.getFluidOrGas(16))
            .duration(1000)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(0, TierEU.RECIPE_UV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(100), Materials.Oxygen.getPlasma(40))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIDepleted.getFluidOrGas(100))
            .duration(10000)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(0, TierEU.RECIPE_UEV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(16), Materials.Hydrogen.getGas(1600))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIIDepleted.getFluidOrGas(16))
            .duration(1000)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(0, TierEU.RECIPE_UEV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(125), Materials.Nitrogen.getPlasma(80))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIIDepleted.getFluidOrGas(125))
            .duration(10000)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(0, TierEU.RECIPE_UMV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkIII.getFluidOrGas(16), Materials.Iron.getPlasma(40))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIIIDepleted.getFluidOrGas(16))
            .duration(1400)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(1, TierEU.RECIPE_UMV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkIII.getFluidOrGas(125), Materials.Radon.getPlasma(100))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIIIDepleted.getFluidOrGas(125))
            .duration(14000)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(1, TierEU.RECIPE_MAX))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkIV.getFluidOrGas(16), Materials.DTR.getFluid(100))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIVDepleted.getFluidOrGas(16))
            .duration(1800)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(1, TierEU.RECIPE_UXV))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkIV.getFluidOrGas(150), Materials.ExcitedDTEC.getFluid(150))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkIVDepleted.getFluidOrGas(150))
            .duration(18000)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(1, TierEU.RECIPE_MAX * 4))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkV.getFluidOrGas(16), GGMaterial.shirabon.getMolten(36))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkVDepleted.getFluidOrGas(16))
            .duration(2000)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(2, TierEU.RECIPE_MAX))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkV.getFluidOrGas(175), Materials.ExcitedDTSC.getFluid(80))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkVDepleted.getFluidOrGas(175))
            .duration(20000)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(2, TierEU.RECIPE_MAX * 16))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkVI.getFluidOrGas(16), Materials.Protomatter.getFluid(200))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkVIDepleted.getFluidOrGas(16))
            .duration(2200)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(2, TierEU.RECIPE_MAX * 4))
            .addTo(NRR);

        RecipeBuilder.builder()
            .fluidInputs(GGMaterial.naquadahBasedFuelMkVI.getFluidOrGas(175), Materials.QuarkGluonPlasma.getFluid(4))
            .fluidOutputs(GGMaterial.naquadahBasedFuelMkVIDepleted.getFluidOrGas(175))
            .duration(22000)
            .eut(0)
            .metadata(REACTOR_TIER, IntLongPair.of(2, TierEU.RECIPE_MAX * 64))
            .addTo(NRR);

    }
}
