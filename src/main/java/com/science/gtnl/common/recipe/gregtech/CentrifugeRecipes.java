package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.enums.Mods.DraconicEvolution;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.loader.BlockLoader;
import com.science.gtnl.utils.recipes.RecipeBuilder;

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

    public RecipeMap<?> CNCR = GTPPRecipeMaps.centrifugeNonCellRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.NeutralisedRedMud.getFluidOrGas(2000))
            .fluidOutputs(
                FluidUtils.getFluidStack(GTPPFluids.RedMud, 1000),
                GTNLMaterials.FerricReeChloride.getFluidOrGas(1000),
                Materials.Hydrogen.getGas(4000))
            .duration(100)
            .eut(120)
            .addTo(CNCR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.FerricReeChloride.getFluidOrGas(2000))
            .fluidOutputs(
                GTNLMaterials.RareEarthChlorides.getFluidOrGas(1000),
                Materials.IronIIIChloride.getFluid(1000),
                Materials.Water.getFluid(3000))
            .duration(320)
            .eut(480)
            .addTo(CNCR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(DraconicEvolution.ID, "magnet", 0, 1))
            .fluidInputs(GTNLMaterials.RareEarthChlorides.getFluidOrGas(2000))
            .fluidOutputs(
                GTNLMaterials.LaNdOxidesSolution.getFluidOrGas(250),
                GTNLMaterials.SmGdOxidesSolution.getFluidOrGas(250),
                GTNLMaterials.TbHoOxidesSolution.getFluidOrGas(250),
                GTNLMaterials.ErLuOxidesSolution.getFluidOrGas(250),
                Materials.HydrochloricAcid.getFluid(1000))
            .duration(200)
            .eut(480)
            .addTo(CNCR);

        RecipeBuilder.builder()
            .itemInputs(GTNLMaterials.UraniumChlorideSlag.get(OrePrefixes.dust, 3))
            .itemOutputs(
                GTNLMaterials.BariumChloride.get(OrePrefixes.dust, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 1),
                GTNLMaterials.RadiumChloride.get(OrePrefixes.dust, 1))
            .outputChances(10000, 10000, 10000, 500)
            .duration(300)
            .eut(TierEU.RECIPE_HV)
            .addTo(CNCR);

        RecipeBuilder.builder()
            .fluidInputs(GTNLMaterials.EnderAir.getFluidOrGas(10000))
            .fluidOutputs(
                Materials.NitrogenDioxide.getGas(3000),
                Materials.Deuterium.getGas(1000),
                Materials.Helium_3.getGas(1000))
            .duration(400)
            .eut(TierEU.RECIPE_HV)
            .addTo(CNCR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .fluidInputs(FluidRegistry.getFluidStack("for.honey", 1000))
            .fluidOutputs(new FluidStack(BlockLoader.honeyFluid, 1000))
            .duration(300)
            .eut(TierEU.RECIPE_ULV)
            .addTo(CNCR);

        RecipeBuilder.builder()
            .itemOutputs(Materials.Beryllium.getDust(24))
            .fluidInputs(GTNLMaterials.PostProcessBeWaste.getFluidOrGas(24000))
            .fluidOutputs(Materials.DilutedSulfuricAcid.getFluid(8000))
            .duration(200)
            .eut(TierEU.RECIPE_MV)
            .addTo(CNCR);
    }
}
