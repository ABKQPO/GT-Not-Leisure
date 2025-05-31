package com.science.gtnl.common.recipe.GregTech;

import static gregtech.api.enums.GTValues.RA;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class MixerRecipes implements IRecipePool {

    final RecipeMap<?> MNCR = GTPPRecipeMaps.mixerNonCellRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 3L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconDioxide, 3L))
            .itemOutputs()
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 1000))
            .fluidOutputs(MaterialPool.SilicaGelBase.getFluidOrGas(1000))
            .specialValue(0)
            .duration(130)
            .eut(480)
            .addTo(MNCR);

        RecipeBuilder.builder()
            .itemInputs(MaterialMisc.SODIUM_NITRATE.getDust(5))
            .itemOutputs()
            .fluidInputs(Materials.Water.getFluid(1000))
            .fluidOutputs(MaterialPool.SodiumNitrateSolution.getFluidOrGas(1000))
            .specialValue(0)
            .duration(80)
            .eut(120)
            .addTo(MNCR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1L))
            .fluidInputs(Materials.Chlorine.getGas(1000))
            .itemOutputs(MaterialPool.ZnFeAlCl.get(OrePrefixes.dust, 4))
            .specialValue(0)
            .duration(250)
            .eut(TierEU.RECIPE_LuV)
            .addTo(MNCR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 2L))
            .fluidInputs(WerkstoffMaterialPool.AmmoniumNitrate.getFluidOrGas(1000))
            .fluidOutputs(GGMaterial.naquadahSolution.getFluidOrGas(1000))
            .specialValue(0)
            .duration(400)
            .eut(TierEU.RECIPE_LuV)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.FierySteel, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 8L),
                new ItemStack(Items.bone, 16),
                new ItemStack(Items.fire_charge, 4),
                new ItemStack(Items.blaze_powder, 16),
                new ItemStack(Items.blaze_rod, 8))
            .itemOutputs(GTNLItemList.BlazeCube.get(1))
            .fluidInputs(FluidRegistry.getFluidStack("pyrotheum", 2000))
            .specialValue(0)
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(MNCR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.RareEarth, 1L))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RedMud, 1000), Materials.HydrochloricAcid.getFluid(4000))
            .fluidOutputs(MaterialPool.NeutralisedRedMud.getFluidOrGas(2000))
            .specialValue(0)
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(MNCR);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Machine_Multi_BlastFurnace.get(6),
                GregtechItemList.Industrial_Sifter.get(3),
                ItemList.Distillation_Tower.get(2),
                ItemList.LargeFluidExtractor.get(1),
                ItemList.Machine_Multi_Solidifier.get(2),
                GregtechItemList.Controller_IndustrialFluidHeater.get(1),
                GregtechItemList.Industrial_Electrolyzer.get(4),
                GregtechItemList.Industrial_Mixer.get(3),
                ItemList.Machine_Multi_LargeChemicalReactor.get(25))
            .itemOutputs(GTNLItemList.PlatinumBasedTreatment.get(1))
            .fluidInputs(
                MaterialsAlloy.MARAGING250.getFluidStack(288),
                MaterialsAlloy.INCONEL_792.getFluidStack(288),
                Materials.Titanium.getMolten(1152),
                Materials.StainlessSteel.getMolten(1728),
                Materials.Iridium.getMolten(9216),
                Materials.Osmium.getMolten(9216))
            .specialValue(0)
            .duration(27648000)
            .eut(TierEU.RECIPE_LV)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1))
            .itemOutputs(MaterialPool.Breel.get(OrePrefixes.dust, 3))
            .duration(5 * SECONDS)
            .eut(12)
            .addTo(MNCR);

        RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.gravel, 16))
            .fluidInputs(Materials.Water.getFluid(1000))
            .fluidOutputs(MaterialPool.GravelSluice.getFluidOrGas(4000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(MNCR);

        RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.sand, 16))
            .fluidInputs(Materials.Water.getFluid(1000))
            .fluidOutputs(MaterialPool.SandSluice.getFluidOrGas(4000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(MNCR);

        RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.obsidian, 16))
            .fluidInputs(Materials.Water.getFluid(1000))
            .fluidOutputs(MaterialPool.ObsidianSluice.getFluidOrGas(4000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ruby, 4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Salt, 4))
            .fluidInputs(Materials.Water.getFluid(1000))
            .fluidOutputs(MaterialPool.GemSluice.getFluidOrGas(4000))
            .duration(15 * SECONDS)
            .eut(300)
            .addTo(MNCR);

    }
}
