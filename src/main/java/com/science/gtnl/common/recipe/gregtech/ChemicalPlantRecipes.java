package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeConstants.CHEMPLANT_CASING_TIER;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class ChemicalPlantRecipes implements IRecipePool {

    public RecipeMap<?> CPR = GTPPRecipeMaps.chemicalPlantRecipes;

    @Override
    public void loadRecipes() {
        // 海晶石溶液单步结晶
        RecipeBuilder.builder()
            .itemInputs(MaterialMisc.STRONTIUM_HYDROXIDE.getDust(48))
            .itemOutputs(ItemList.Prismarine_Precipitate.get(8))
            .fluidInputs(Materials.PrismarineSolution.getFluid(8000))
            .fluidOutputs(Materials.PrismarineContaminatedHydrogenPeroxide.getFluid(6000))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CPR);

        RecipeBuilder.builder()
            .itemInputs(GregtechItemList.BrownMetalCatalyst.get(0))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1))
            .fluidInputs(
                new FluidStack(GTPPFluids.CoalTar, 1000),
                Materials.Hydrogen.getGas(4000),
                Materials.Steam.getGas(1000))
            .fluidOutputs(
                new FluidStack(GTPPFluids.Naphthalene, 1000),
                new FluidStack(GTPPFluids.Anthracene, 100),
                Materials.Naphtha.getFluid(200))
            .duration(300)
            .metadata(CHEMPLANT_CASING_TIER, 4)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CPR);

    }
}
