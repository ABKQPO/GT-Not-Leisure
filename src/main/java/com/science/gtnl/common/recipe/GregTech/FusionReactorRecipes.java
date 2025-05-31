package com.science.gtnl.common.recipe.GregTech;

import static gregtech.api.util.GTRecipeConstants.FUSION_THRESHOLD;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.core.material.MaterialsElements;

public class FusionReactorRecipes implements IRecipePool {

    final RecipeMap<?> fR = RecipeMaps.fusionRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("hydrogen", 144), FluidRegistry.getFluidStack("molten.boron", 144))
            .fluidOutputs(FluidRegistry.getFluidStack("plasma.carbon", 144))
            .duration(10)
            .eut(TierEU.RECIPE_IV)
            .metadata(FUSION_THRESHOLD, 20000000L)
            .addTo(fR);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.DraconiumAwakened.getMolten(432), Materials.Radon.getPlasma(144))
            .fluidOutputs(MaterialsElements.STANDALONE.DRAGON_METAL.getFluidStack(288))
            .duration(10)
            .eut(491520)
            .metadata(FUSION_THRESHOLD, 1000000000L)
            .addTo(fR);

        GTValues.RA.stdBuilder()
            .fluidInputs(
                Materials.Arsenic.getMolten(32),
                new FluidStack(MaterialsElements.getInstance().RUTHENIUM.getFluid(), 16))
            .fluidOutputs(MaterialPool.Darmstadtium.getMolten(16))
            .duration(32)
            .eut(TierEU.RECIPE_LuV)
            .metadata(FUSION_THRESHOLD, 200000000L)
            .addTo(fR);

        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.Molybdenum.getMolten(144), Materials.Palladium.getMolten(144))
            .fluidOutputs(new FluidStack(MaterialsElements.getInstance().RADIUM.getFluid(), 288))
            .duration(32)
            .eut(TierEU.RECIPE_LuV)
            .metadata(FUSION_THRESHOLD, 200000000L)
            .addTo(fR);
    }
}
