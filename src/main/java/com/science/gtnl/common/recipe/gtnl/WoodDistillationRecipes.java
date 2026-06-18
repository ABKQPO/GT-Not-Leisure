package com.science.gtnl.common.recipe.gtnl;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.objects.OreDictItemStack;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.core.fluids.GTPPFluids;

public class WoodDistillationRecipes implements IRecipePool {

    public RecipeMap<?> WDR = GTNLRecipeMaps.WoodDistillationRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(new OreDictItemStack("logWood", 16))
            .itemOutputs(Materials.AshDark.getDust(4))
            .fluidInputs(Materials.Nitrogen.getGas(1000))
            .fluidOutputs(
                Materials.Water.getFluid(400),
                Materials.Methanol.getFluid(240),
                Materials.Benzene.getFluid(175),
                Materials.CarbonMonoxide.getGas(170),
                Materials.Creosote.getFluid(150),
                Materials.Dimethylbenzene.getFluid(120),
                Materials.AceticAcid.getFluid(80),
                Materials.Methane.getGas(60),
                Materials.Acetone.getFluid(40),
                Materials.Phenol.getFluid(35),
                Materials.Toluene.getFluid(35),
                Materials.Ethylene.getGas(10),
                Materials.Hydrogen.getGas(10),
                Materials.MethylAcetate.getFluid(8),
                new FluidStack(GTPPFluids.CoalGas, 8),
                Materials.Ethanol.getFluid(8))
            .duration(200)
            .eut(120)
            .addTo(WDR);
    }
}
