package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.config.MainConfig;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.material.MaterialMisc;

public class ChemicalPlantRecipes implements IRecipePool {

    public RecipeMap<?> CPR = GTPPRecipeMaps.chemicalPlantRecipes;

    @Override
    public void loadRecipes() {
        // 海晶石溶液单步结晶
        GTValues.RA.stdBuilder()
            .itemInputs(MaterialMisc.STRONTIUM_HYDROXIDE.getDust(48))
            .itemOutputs(ItemList.Prismarine_Precipitate.get(8))
            .fluidInputs(Materials.PrismarineSolution.getFluid(8000))
            .fluidOutputs(Materials.PrismarineContaminatedHydrogenPeroxide.getFluid(6000))
            .duration(200)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CPR);

        if (MainConfig.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadDeleteRecipe() {
        GTValues.RA.stdBuilder() // Precipitation
            .itemInputs(MaterialMisc.STRONTIUM_HYDROXIDE.getDust(48))
            .itemOutputs(ItemList.Prismarine_Precipitate.get(8))
            .fluidInputs(Materials.PrismarineRichNitrobenzeneSolution.getFluid(16000))
            .fluidOutputs(
                Materials.PrismarineContaminatedNitrobenzeSolution.getFluid(12000),
                new FluidStack(FluidRegistry.getFluid("nitrobenzene"), 4000))
            .duration(40 * SECONDS)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CPR);
    }
}
