package com.science.gtnl.common.recipe.GregTech;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class ChemicalDehydratorRecipes implements IRecipePool {

    final RecipeMap<?> CDR = GTPPRecipeMaps.chemicalDehydratorRecipes;
    final RecipeMap<?> CDNCR = GTPPRecipeMaps.chemicalDehydratorNonCellRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.LaNdOxidesSolution.getFluidOrGas(4000))
            .itemOutputs(
                WerkstoffMaterialPool.LanthanumOxide.get(OrePrefixes.dust, 5),
                WerkstoffMaterialPool.CeriumIIIOxide.get(OrePrefixes.dust, 5),
                MaterialPool.PraseodymiumOxide.get(OrePrefixes.dust, 5),
                WerkstoffMaterialPool.NeodymiumOxide.get(OrePrefixes.dust, 5))
            .outputChances(5000, 5000, 5000, 5000)
            .specialValue(0)
            .duration(220)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.SmGdOxidesSolution.getFluidOrGas(4000))
            .itemOutputs(
                MaterialPool.ScandiumOxide.get(OrePrefixes.dust, 5),
                WerkstoffMaterialPool.SamariumOxalate.get(OrePrefixes.dust, 5),
                WerkstoffMaterialPool.EuropiumIIIOxide.get(OrePrefixes.dust, 5),
                MaterialPool.GadoliniumOxide.get(OrePrefixes.dust, 5))
            .outputChances(5000, 5000, 5000, 5000)
            .specialValue(0)
            .duration(220)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.TbHoOxidesSolution.getFluidOrGas(4000))
            .itemOutputs(
                WerkstoffLoader.YttriumOxide.get(OrePrefixes.dust, 5),
                MaterialPool.TerbiumOxide.get(OrePrefixes.dust, 5),
                MaterialPool.DysprosiumOxide.get(OrePrefixes.dust, 5),
                MaterialPool.HolmiumOxide.get(OrePrefixes.dust, 5))
            .outputChances(5000, 5000, 5000, 5000)
            .specialValue(0)
            .duration(220)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.ErLuOxidesSolution.getFluidOrGas(4000))
            .itemOutputs(
                MaterialPool.ErbiumOxide.get(OrePrefixes.dust, 5),
                MaterialPool.ThuliumOxide.get(OrePrefixes.dust, 5),
                MaterialPool.YtterbiumOxide.get(OrePrefixes.dust, 5),
                MaterialPool.LutetiumOxide.get(OrePrefixes.dust, 5))
            .outputChances(5000, 5000, 5000, 5000)
            .specialValue(0)
            .duration(220)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);
    }
}
