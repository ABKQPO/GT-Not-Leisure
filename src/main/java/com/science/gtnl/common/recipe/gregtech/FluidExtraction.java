package com.science.gtnl.common.recipe.gregtech;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.core.material.MaterialsElements;

public class FluidExtraction implements IRecipePool {

    public RecipeMap<?> fER = RecipeMaps.fluidExtractionRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(MaterialsElements.getInstance().ZIRCONIUM.getIngot(1))
            .fluidOutputs(new FluidStack(MaterialsElements.getInstance().ZIRCONIUM.getFluid(), 144))
            .duration(4)
            .eut(8)
            .addTo(fER);

        RecipeBuilder.builder()
            .itemInputs(ModsItemlist.GalaxySpaceBarnardaClog.get(64))
            .fluidOutputs(GTNLMaterials.BarnardaCSappy.getFluidOrGas(500))
            .duration(4)
            .eut(491520)
            .addTo(fER);
    }
}
