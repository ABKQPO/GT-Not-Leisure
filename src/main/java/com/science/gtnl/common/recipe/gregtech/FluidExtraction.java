package com.science.gtnl.common.recipe.gregtech;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;

import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gtPlusPlus.core.material.MaterialsElements;

public class FluidExtraction implements IRecipePool {

    public RecipeMap<?> fER = RecipeMaps.fluidExtractionRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(MaterialsElements.getInstance().ZIRCONIUM.getIngot(1))
            .fluidOutputs(new FluidStack(MaterialsElements.getInstance().ZIRCONIUM.getFluid(), 144))
            .duration(4)
            .eut(8)
            .addTo(fER);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem("GalaxySpace", "barnardaClog", 64, 0))
            .fluidOutputs(MaterialPool.BarnardaCSappy.getFluidOrGas(500))
            .duration(4)
            .eut(491520)
            .addTo(fER);
    }
}
