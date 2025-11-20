package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.util.GTRecipeBuilder.*;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.item.items.MilledOre;
import com.science.gtnl.common.material.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.core.util.minecraft.MaterialUtils;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class CellRegulatorRecipes implements IRecipePool {

    public RecipeMap<?> CRR = RecipePool.CellRegulatorRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.PotassiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Nickel)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 25000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.NickelFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.SodiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Platinum)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 35000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.PlatinumFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.PotassiumEthylXanthate.get(64),
                MaterialUtils.generateMaterialFromGtENUM(Materials.NaquadahEnriched)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 140000))
            .fluidOutputs(FluidUtils.getFluidStack(MilledOre.NaquadahEnrichedFlotationFroth, 1000))
            .duration(8 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.SodiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Almandine)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 18000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.AlmandineFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.PotassiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Chalcopyrite)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 12000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.ChalcopyriteFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.PotassiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Grossular)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 28000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.GrossularFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.SodiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Pyrope)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 8000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.PyropeFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.PotassiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Spessartine)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 35000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.SpessartineFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.SodiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Sphalerite)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 14000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.SphaleriteFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.PotassiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Pentlandite)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 14000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.PentlanditeFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.SodiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Monazite)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 30000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.MonaziteFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_LuV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GregtechItemList.SodiumEthylXanthate.get(32),
                MaterialUtils.generateMaterialFromGtENUM(Materials.Redstone)
                    .getMilled(64))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.PineOil, 13000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.RedstoneFlotationFroth, 1000))
            .duration(4 * MINUTES)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                MaterialUtils.generateMaterialFromGtENUM(Materials.Netherrack)
                    .getMilled(64))
            .fluidInputs(Materials.NefariousOil.getFluid(2000))
            .fluidOutputs(FluidUtils.getFluidStack(GTPPFluids.NetherrackFlotationFroth, 1000))
            .duration(30 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(CRR);
    }
}
