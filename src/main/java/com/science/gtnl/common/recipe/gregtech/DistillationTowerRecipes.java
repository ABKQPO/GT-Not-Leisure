package com.science.gtnl.common.recipe.gregtech;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.bop.blocks.BOPBlockRegistrator;

public class DistillationTowerRecipes implements IRecipePool {

    public RecipeMap<?> DTR = RecipeMaps.distillationTowerRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .fluidInputs(MaterialPool.FluorineCrackedNaquadah.getFluidOrGas(1000))
            .fluidOutputs(
                GGMaterial.naquadahBasedFuelMkI.getFluidOrGas(800),
                Materials.NitricAcid.getFluid(200),
                MaterialPool.EnrichedNaquadahWaste.getFluidOrGas(100),
                Materials.Ammonia.getGas(200),
                Materials.Fluorine.getGas(200))
            .duration(600)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(DTR);

        GTValues.RA.stdBuilder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Barium, 1))
            .fluidInputs(MaterialPool.EnrichedNaquadahWaste.getFluidOrGas(2000))
            .fluidOutputs(
                Materials.SulfuricAcid.getFluid(500),
                GGMaterial.enrichedNaquadahRichSolution.getFluidOrGas(250),
                GGMaterial.naquadriaRichSolution.getFluidOrGas(100))
            .outputChances(5000)
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(DTR);

        GTValues.RA.stdBuilder()
            .fluidInputs(MaterialPool.RadonCrackedEnrichedNaquadah.getFluidOrGas(1000))
            .fluidOutputs(
                GGMaterial.naquadahBasedFuelMkII.getFluidOrGas(800),
                MaterialPool.NaquadriaWaste.getFluidOrGas(100),
                Materials.Radon.getGas(200),
                Materials.Fluorine.getGas(200))
            .duration(300)
            .eut(TierEU.RECIPE_LuV)
            .addTo(DTR);

        GTValues.RA.stdBuilder()
            .itemOutputs(GGMaterial.triniumSulphate.get(OrePrefixes.dust, 1))
            .fluidInputs(MaterialPool.NaquadriaWaste.getFluidOrGas(2000))
            .fluidOutputs(
                GGMaterial.naquadriaRichSolution.getFluidOrGas(250),
                GGMaterial.enrichedNaquadahRichSolution.getFluidOrGas(100))
            .outputChances(5000)
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(DTR);

        GTValues.RA.stdBuilder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnderPearl, 1))
            .fluidInputs(MaterialPool.LiquidEnderAir.getFluidOrGas(200000))
            .fluidOutputs(
                Materials.NitrogenDioxide.getGas(120000),
                Materials.Deuterium.getGas(40000),
                Materials.Helium.getGas(15000),
                Materials.Helium_3.getGas(12000),
                Materials.Tritium.getGas(10000),
                WerkstoffLoader.Krypton.getFluidOrGas(1000),
                WerkstoffLoader.Xenon.getFluidOrGas(1000),
                Materials.Radon.getGas(1000))
            .outputChances(1000)
            .duration(2000)
            .eut(TierEU.RECIPE_IV)
            .addTo(DTR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                new ItemStack(BOPBlockRegistrator.leaves_Pine, 64),
                new ItemStack(BOPBlockRegistrator.leaves_Pine, 64))
            .itemOutputs(GTModHandler.getModItem(Mods.Witchery.ID, "ingredient", 8, 18))
            .fluidInputs(GTModHandler.getDistilledWater(128000))
            .fluidOutputs(
                Materials.Steam.getGas(64000),
                FluidUtils.getSuperHeatedSteam(16000),
                Materials.Creosote.getFluid(2000),
                Materials.WoodTar.getFluid(2000),
                new FluidStack(GTPPFluids.PineOil, 32000))
            .outputChances(1500)
            .duration(1200)
            .eut(TierEU.RECIPE_IV)
            .addTo(DTR);
    }
}
