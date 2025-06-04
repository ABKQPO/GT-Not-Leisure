package com.science.gtnl.common.recipe.GregTech;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;

public class multiDehydratorRecipes implements IRecipePool {

    final RecipeMap<?> mD = GTPPRecipeMaps.chemicalDehydratorNonCellRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.SilicaGelBase.getFluidOrGas(1000))
            .itemOutputs(
                MaterialPool.SilicaGel.get(OrePrefixes.dust, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Salt, 2))
            .specialValue(0)
            .duration(130)
            .eut(480)
            .addTo(mD);

        RecipeBuilder.builder()
            .fluidInputs(new FluidStack(GTPPFluids.BoricAcid, 2000))
            .itemOutputs(MaterialPool.BoronTrioxide.get(OrePrefixes.dust, 5))
            .specialValue(0)
            .duration(200)
            .eut(480)
            .addTo(mD);

        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.PloyamicAcid.getFluidOrGas(144))
            .itemOutputs()
            .fluidOutputs(MaterialPool.Polyimide.getMolten(144))
            .specialValue(0)
            .duration(270)
            .eut(30)
            .addTo(mD);
    }
}
