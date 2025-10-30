package com.science.gtnl.common.recipe.gregtech;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import bartworks.system.material.WerkstoffLoader;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class ChemicalDehydratorRecipes implements IRecipePool {

    public RecipeMap<?> CDR = GTPPRecipeMaps.chemicalDehydratorRecipes;
    public RecipeMap<?> CDNCR = GTPPRecipeMaps.chemicalDehydratorNonCellRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.SilicaGelBase.getFluidOrGas(1000))
            .itemOutputs(
                MaterialPool.SilicaGel.get(OrePrefixes.dust, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Salt, 2))
            .duration(130)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(new FluidStack(GTPPFluids.BoricAcid, 2000))
            .itemOutputs(MaterialPool.BoronTrioxide.get(OrePrefixes.dust, 5))
            .duration(200)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.PloyamicAcid.getFluidOrGas(144))
            .itemOutputs()
            .fluidOutputs(MaterialPool.Polyimide.getMolten(144))
            .duration(270)
            .eut(30)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.LaNdOxidesSolution.getFluidOrGas(4000))
            .itemOutputs(
                WerkstoffMaterialPool.LanthanumOxide.get(OrePrefixes.dust, 5),
                WerkstoffMaterialPool.CeriumIIIOxide.get(OrePrefixes.dust, 5),
                MaterialPool.PraseodymiumOxide.get(OrePrefixes.dust, 5),
                WerkstoffMaterialPool.NeodymiumOxide.get(OrePrefixes.dust, 5))
            .outputChances(5000, 5000, 5000, 5000)
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
            .duration(220)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);
    }
}
