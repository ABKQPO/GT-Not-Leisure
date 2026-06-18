package com.science.gtnl.common.recipe.gregtech;

import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
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
            .fluidInputs(GTNLMaterials.SilicaGelBase.getFluidOrGas(1000))
            .itemOutputs(
                GTNLMaterials.SilicaGel.get(OrePrefixes.dust, 3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Salt, 2))
            .duration(130)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(new FluidStack(GTPPFluids.BoricAcid, 2000))
            .itemOutputs(GTNLMaterials.BoronTrioxide.get(OrePrefixes.dust, 5))
            .duration(200)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.PloyamicAcid.getFluidOrGas(144))
            .itemOutputs()
            .fluidOutputs(GTNLMaterials.Polyimide.getMolten(144))
            .duration(270)
            .eut(30)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.LaNdOxidesSolution.getFluidOrGas(4000))
            .itemOutputs(
                WerkstoffMaterialPool.LanthanumOxide.get(OrePrefixes.dust, 5),
                WerkstoffMaterialPool.CeriumIIIOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.PraseodymiumOxide.get(OrePrefixes.dust, 5),
                WerkstoffMaterialPool.NeodymiumOxide.get(OrePrefixes.dust, 5))
            .outputChances(5000, 5000, 5000, 5000)
            .duration(220)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.SmGdOxidesSolution.getFluidOrGas(4000))
            .itemOutputs(
                GTNLMaterials.ScandiumOxide.get(OrePrefixes.dust, 5),
                WerkstoffMaterialPool.SamariumOxide.get(OrePrefixes.dust, 5),
                WerkstoffMaterialPool.EuropiumIIIOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.GadoliniumOxide.get(OrePrefixes.dust, 5))
            .outputChances(5000, 5000, 5000, 5000)
            .duration(220)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.TbHoOxidesSolution.getFluidOrGas(4000))
            .itemOutputs(
                WerkstoffLoader.YttriumOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.TerbiumOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.DysprosiumOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.HolmiumOxide.get(OrePrefixes.dust, 5))
            .outputChances(5000, 5000, 5000, 5000)
            .duration(220)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.ErLuOxidesSolution.getFluidOrGas(4000))
            .itemOutputs(
                GTNLMaterials.ErbiumOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.ThuliumOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.YtterbiumOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.LutetiumOxide.get(OrePrefixes.dust, 5))
            .outputChances(5000, 5000, 5000, 5000)
            .duration(220)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.ErLuOxidesSolution.getFluidOrGas(4000))
            .itemOutputs(
                GTNLMaterials.ErbiumOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.ThuliumOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.YtterbiumOxide.get(OrePrefixes.dust, 5),
                GTNLMaterials.LutetiumOxide.get(OrePrefixes.dust, 5))
            .outputChances(5000, 5000, 5000, 5000)
            .duration(220)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);

        RecipeBuilder.builder()
            .fluidInputs(WerkstoffMaterialPool.SeaweedByproducts.getFluidOrGas(1000))
            .itemOutputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Potassium, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcium, 1))
            .fluidOutputs(Materials.SulfuricAcid.getFluid(400))
            .outputChances(5000, 5000, 5000)
            .duration(200)
            .eut(480)
            .addTo(CDNCR)
            .addTo(CDR);
    }
}
