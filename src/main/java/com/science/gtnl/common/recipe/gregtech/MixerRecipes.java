package com.science.gtnl.common.recipe.gregtech;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;

import goodgenerator.items.GGMaterial;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsGTNH;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.fluids.GTPPFluids;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class MixerRecipes implements IRecipePool {

    public RecipeMap<?> MNCR = GTPPRecipeMaps.mixerNonCellRecipes;
    public RecipeMap<?> MCR = RecipeMaps.mixerRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SodiumHydroxide, 3L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconDioxide, 3L))
            .itemOutputs()
            .fluidInputs(FluidRegistry.getFluidStack("ic2distilledwater", 1000))
            .fluidOutputs(MaterialPool.SilicaGelBase.getFluidOrGas(1000))
            .duration(130)
            .eut(480)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
            .itemInputs(MaterialMisc.SODIUM_NITRATE.getDust(5))
            .itemOutputs()
            .fluidInputs(Materials.Water.getFluid(1000))
            .fluidOutputs(MaterialPool.SodiumNitrateSolution.getFluidOrGas(1000))
            .duration(80)
            .eut(120)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1L))
            .fluidInputs(Materials.Chlorine.getGas(1000))
            .itemOutputs(MaterialPool.ZnFeAlCl.get(OrePrefixes.dust, 4))
            .duration(250)
            .eut(TierEU.RECIPE_LuV)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 2L))
            .fluidInputs(WerkstoffMaterialPool.AmmoniumNitrate.getFluidOrGas(1000))
            .fluidOutputs(GGMaterial.naquadahSolution.getFluidOrGas(1000))
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
            .duration(200)
            .eut(TierEU.RECIPE_IV)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.RareEarth, 1L))
            .fluidInputs(FluidUtils.getFluidStack(GTPPFluids.RedMud, 1000), Materials.HydrochloricAcid.getFluid(4000))
            .fluidOutputs(MaterialPool.NeutralisedRedMud.getFluidOrGas(2000))
            .duration(100)
            .eut(TierEU.RECIPE_MV)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
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
            .addTo(MCR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1))
            .itemOutputs(MaterialPool.Breel.get(OrePrefixes.dust, 3))
            .duration(5 * SECONDS)
            .eut(12)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.gravel, 16))
            .fluidInputs(Materials.Water.getFluid(1000))
            .fluidOutputs(MaterialPool.GravelSluice.getFluidOrGas(4000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.sand, 16))
            .fluidInputs(Materials.Water.getFluid(1000))
            .fluidOutputs(MaterialPool.SandSluice.getFluidOrGas(4000))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(MNCR);

        GTValues.RA.stdBuilder()
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

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.sand, 1))
            .itemOutputs(new ItemStack(Blocks.soul_sand, 1))
            .fluidInputs(Materials.NefariousGas.getFluid(500))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(MNCR);

        // 海晶石溶液 工业HF降价为HF
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.shard, MaterialsGTNH.Prismarine, 24),
                GTUtility.getIntegratedCircuit(1))
            .fluidInputs(
                Materials.HydrofluoricAcid.getFluid(4000), // HydrofluoricAcid
                FluidRegistry.getFluidStack("fluid.hydrogenperoxide", 4000)) // Hydrogen Peroxide
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.CertusQuartz, 4))
            .fluidOutputs(Materials.PrismarineSolution.getFluid(8000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(MNCR);

        // 海晶石溶液 循环配方
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.shard, MaterialsGTNH.Prismarine, 6),
                GTUtility.getIntegratedCircuit(2))
            .fluidInputs(
                Materials.PrismarineContaminatedHydrogenPeroxide.getFluid(6000),
                FluidRegistry.getFluidStack("fluid.hydrogenperoxide", 2000)) // Hydrogen Peroxide
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NetherQuartz, 1))
            .fluidOutputs(Materials.PrismarineSolution.getFluid(8000))
            .duration(20 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(MNCR);

        if (MainConfig.enableDeleteRecipe) loadDeleteRecipe();
    }

    public void loadDeleteRecipe() {
        // 富集下界废气 降压配方
        GTValues.RA.stdBuilder()
            .setNEIDesc("Remove Change by GTNotLeisure")
            .itemInputs(ItemList.Heavy_Hellish_Mud.get(16))
            .fluidInputs(Materials.PoorNetherWaste.getFluid(16_000))
            .fluidOutputs(Materials.RichNetherWaste.getFluid(16_000))
            .duration(15 * SECONDS)
            .eut(TierEU.RECIPE_IV)
            .addTo(MNCR);
    }
}
