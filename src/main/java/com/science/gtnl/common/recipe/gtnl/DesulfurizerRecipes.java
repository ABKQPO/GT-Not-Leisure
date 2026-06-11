package com.science.gtnl.common.recipe.gtnl;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.fluids.GTPPFluids;

public class DesulfurizerRecipes implements IRecipePool {

    public RecipeMap<?> DesR = GTNLRecipeMaps.DesulfurizerRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1))
            .fluidInputs(Materials.SulfuricGas.getGas(12000))
            .fluidOutputs(Materials.Gas.getGas(12000))
            .duration(120)
            .eut(30)
            .addTo(DesR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1))
            .fluidInputs(Materials.SulfuricNaphtha.getFluid(12000))
            .fluidOutputs(Materials.Naphtha.getFluid(12000))
            .duration(120)
            .eut(30)
            .addTo(DesR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1))
            .fluidInputs(Materials.SulfuricLightFuel.getFluid(12000))
            .fluidOutputs(Materials.LightFuel.getFluid(12000))
            .duration(120)
            .eut(30)
            .addTo(DesR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1))
            .fluidInputs(Materials.SulfuricHeavyFuel.getFluid(12000))
            .fluidOutputs(Materials.HeavyFuel.getFluid(12000))
            .duration(120)
            .eut(30)
            .addTo(DesR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1))
            .fluidInputs(new FluidStack(GTPPFluids.SulfuricCoalTarOil, 12000))
            .fluidOutputs(new FluidStack(GTPPFluids.Naphthalene, 12000))
            .duration(120)
            .eut(30)
            .addTo(DesR);

        RecipeBuilder.builder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1))
            .fluidInputs(Materials.NaturalGas.getGas(12000))
            .fluidOutputs(Materials.Gas.getGas(12000))
            .duration(120)
            .eut(30)
            .addTo(DesR);
    }
}
