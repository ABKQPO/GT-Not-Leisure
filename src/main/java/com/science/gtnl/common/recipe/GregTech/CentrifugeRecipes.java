package com.science.gtnl.common.recipe.GregTech;

import static gregtech.api.enums.Mods.DraconicEvolution;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class CentrifugeRecipes implements IRecipePool {

    final RecipeMap<?> CNCR = GTPPRecipeMaps.centrifugeNonCellRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.NeutralisedRedMud.getFluidOrGas(2000))
            .fluidOutputs(
                FluidUtils.getFluidStack(GTPPFluids.RedMud, 1000),
                MaterialPool.FerricReeChloride.getFluidOrGas(1000),
                Materials.Hydrogen.getGas(4000))
            .specialValue(0)
            .duration(100)
            .eut(120)
            .addTo(CNCR);

        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.FerricReeChloride.getFluidOrGas(2000))
            .fluidOutputs(
                MaterialPool.RareEarthChlorides.getFluidOrGas(1000),
                Materials.IronIIIChloride.getFluid(1000),
                Materials.Water.getFluid(3000))
            .specialValue(0)
            .duration(320)
            .eut(480)
            .addTo(CNCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem(DraconicEvolution.ID, "magnet", 1, 1)))
            .fluidInputs(MaterialPool.RareEarthChlorides.getFluidOrGas(2000))
            .fluidOutputs(
                MaterialPool.LaNdOxidesSolution.getFluidOrGas(250),
                MaterialPool.SmGdOxidesSolution.getFluidOrGas(250),
                MaterialPool.TbHoOxidesSolution.getFluidOrGas(250),
                MaterialPool.ErLuOxidesSolution.getFluidOrGas(250),
                Materials.HydrochloricAcid.getFluid(1000))
            .specialValue(0)
            .duration(200)
            .eut(480)
            .addTo(CNCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialPool.UraniumChlorideSlag.get(OrePrefixes.dust, 3))
            .itemOutputs(
                MaterialPool.BariumChloride.get(OrePrefixes.dust, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 1),
                MaterialPool.RadiumChloride.get(OrePrefixes.dust, 1))
            .outputChances(10000, 10000, 10000, 500)
            .specialValue(0)
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(CNCR);

        RecipeBuilder.builder()
            .fluidInputs(MaterialPool.EnderAir.getFluidOrGas(10000))
            .fluidOutputs(
                Materials.NitrogenDioxide.getGas(3000),
                Materials.Deuterium.getGas(1000),
                Materials.Helium_3.getGas(1000))
            .duration(400)
            .eut(TierEU.RECIPE_HV)
            .addTo(CNCR);
    }
}
