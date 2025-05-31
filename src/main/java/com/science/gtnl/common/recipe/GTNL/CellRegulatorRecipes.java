package com.science.gtnl.common.recipe.GTNL;

import static gregtech.api.util.GTRecipeBuilder.MINUTES;

import com.science.gtnl.common.item.items.MilledOre;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.Material;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.core.util.minecraft.MaterialUtils;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class CellRegulatorRecipes implements IRecipePool {

    final RecipeMap<?> CRR = RecipeRegister.CellRegulatorRecipes;

    @Override
    public void loadRecipes() {

        Material aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Nickel);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.PotassiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 25000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.NickelFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Platinum);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.SodiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 35000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.PlatinumFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.NaquadahEnriched);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.PotassiumEthylXanthate.get(64), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 140000))
            .fluidOutputs(FluidUtils.getFluidStack(MilledOre.NaquadahEnrichedFlotationFroth, 1000))
            .duration(8 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Almandine);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.SodiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 18000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.AlmandineFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Chalcopyrite);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.PotassiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 12000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.ChalcopyriteFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Grossular);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.PotassiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 28000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.GrossularFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Pyrope);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.SodiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 8000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.PyropeFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Spessartine);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.PotassiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 35000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.SpessartineFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Sphalerite);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.SodiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 14000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.SphaleriteFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Pentlandite);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.PotassiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 14000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.PentlanditeFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Monazite);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.SodiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 30000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.MonaziteFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        aMat = MaterialUtils.generateMaterialFromGtENUM(Materials.Redstone);
        GTValues.RA.stdBuilder()
            .itemInputs(GregtechItemList.SodiumEthylXanthate.get(32), aMat.getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 13000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.RedstoneFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);
    }
}
