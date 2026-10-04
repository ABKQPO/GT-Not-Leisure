package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.dreammaster.item.NHItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class FishingGroundRecipes implements IRecipePool {

    public RecipeMap<?> FGR = GTNLRecipeMaps.FishingGroundRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), new ItemStack(Items.fishing_rod, 0))
            .itemOutputs(
                new ItemStack(Items.fish, 16, 0),
                new ItemStack(Items.fish, 16, 1),
                new ItemStack(Items.fish, 16, 2),
                new ItemStack(Items.fish, 8, 3))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(2500, 2500, 2500, 1000)
            .duration(200)
            .eut(TierEU.RECIPE_MV)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                new ItemStack(Items.fishing_rod, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatRaw, 16L))
            .itemOutputs(
                new ItemStack(Items.fish, 32, 0),
                new ItemStack(Items.fish, 32, 1),
                new ItemStack(Items.fish, 32, 2),
                new ItemStack(Items.fish, 16, 3))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(7500, 7500, 7500, 5000)
            .duration(200)
            .eut(TierEU.RECIPE_MV)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                new ItemStack(Items.fishing_rod, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatCooked, 16L))
            .itemOutputs(
                new ItemStack(Items.fish, 64, 0),
                new ItemStack(Items.fish, 64, 1),
                new ItemStack(Items.fish, 64, 2),
                new ItemStack(Items.fish, 64, 3))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(9000, 9000, 9000, 9000)
            .duration(200)
            .eut(TierEU.RECIPE_MV)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(7), ItemList.ActivatedCarbonFilterMesh.get(1))
            .itemOutputs(
                new ItemStack(Items.rotten_flesh, 16),
                new ItemStack(Items.bone, 16),
                new ItemStack(Items.stick, 16),
                new ItemStack(Items.string, 16),
                new ItemStack(Items.dye, 16, 0),
                new ItemStack(Items.feather, 12),
                new ItemStack(Items.gunpowder, 12),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Stone, 12L),
                new ItemStack(Blocks.tripwire_hook, 6),
                new ItemStack(Items.bowl, 6),
                new ItemStack(Items.leather, 6),
                new ItemStack(Items.book, 4),
                new ItemStack(Items.fishing_rod, 1),
                new ItemStack(Items.bow, 1),
                new ItemStack(Items.leather_boots, 1),
                new ItemStack(Items.name_tag, 1),
                new ItemStack(Items.potionitem, 1, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 4L),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.QuartzSand, 4L),
                new ItemStack(Items.clay_ball, 4),
                new ItemStack(Items.coal, 4, 0),
                new ItemStack(Items.sugar, 4),
                new ItemStack(Items.redstone, 4),
                new ItemStack(Items.dye, 4, 4),
                GTOreDictUnificator.get(OrePrefixes.nugget, Materials.PolyvinylChloride, 2L),
                new ItemStack(Items.gold_nugget, 2),
                new ItemStack(Items.diamond, 1),
                new ItemStack(Items.emerald, 1),
                new ItemStack(Items.golden_apple, 1),
                ItemUtils.getItemStack(ItemList.ZPM.get(1), "{GT.ItemCharge:2000000000000L}", null))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(
                8000,
                8000,
                8000,
                8000,
                8000,
                7500,
                7500,
                7500,
                6000,
                6000,
                5500,
                5000,
                3000,
                3000,
                3000,
                1500,
                3000,
                4000,
                4000,
                4000,
                4000,
                4000,
                3500,
                4000,
                3000,
                3500,
                2000,
                2500,
                500,
                500,
                100,
                1)
            .duration(400)
            .eut(TierEU.RECIPE_EV)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTUtility.copyAmount(0, ItemList.ActivatedCarbonFilterMesh.get(1)))
            .itemOutputs(
                new ItemStack(Blocks.waterlily, 32),
                new ItemStack(Blocks.vine, 32),
                ModsItemlist.TwilightForestTileHugeLilyPad.get(32),
                ModsItemlist.BiomesOPlentyLilyBop.get(32),
                ModsItemlist.BiomesOPlentyMediumLilyPad.get(32),
                ModsItemlist.BiomesOPlentySmallLilyPad.get(32),
                ModsItemlist.BiomesOPlentyPinkCoral.get(16),
                ModsItemlist.BiomesOPlentyOrangeCoral.get(16),
                ModsItemlist.BiomesOPlentyBlueCoral.get(16),
                ModsItemlist.BiomesOPlentyGlowingCoral.get(16),
                ModsItemlist.PamsHarvestCraftSeaweedItem.get(64),
                ModsItemlist.PamsHarvestCraftWaterchestnutItem.get(16),
                ModsItemlist.PamsHarvestCraftRiceItem.get(16),
                ModsItemlist.PamsHarvestCraftCranberryItem.get(16))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .outputChances(6000, 6000, 3000, 4000, 4000, 4000, 2500, 2500, 2500, 2500, 7500, 5000, 5000, 5000)
            .duration(500)
            .eut(TierEU.RECIPE_EV)
            .addTo(FGR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 32L),
                ModsItemlist.PamsHarvestCraftSeaweedItem.get(64))
            .itemOutputs(
                ModsItemlist.GalaxySpaceCetiESeaweedFormI.get(64),
                ModsItemlist.GalaxySpaceCetiESeaweedFormII.get(64),
                ModsItemlist.GalaxySpaceCetiESeaweedFormIII.get(64),
                ModsItemlist.GalaxySpaceCetiESeaweedFormIV.get(64),
                ModsItemlist.GalaxySpaceCetiESeaweedFormV.get(64),
                ModsItemlist.GalaxySpaceCetiESeaweedFormVI.get(64))
            .fluidInputs(FluidRegistry.getFluidStack("unknownnutrientagar", 1000))
            .duration(1000)
            .eut(TierEU.RECIPE_LuV)
            .addTo(FGR);

        if (Mods.PamsHarvestCraft.isModLoaded()) {

            RecipeBuilder.builder()
                .itemInputs(GTUtility.getIntegratedCircuit(4), new ItemStack(Items.fishing_rod, 0))
                .itemOutputs(
                    ModsItemlist.PamsHarvestCraftAnchovyrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftBassrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftCalamarirawItem.get(16),
                    ModsItemlist.PamsHarvestCraftCarprawItem.get(16),
                    ModsItemlist.PamsHarvestCraftCatfishrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftCharrrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftClamrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftCrabrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftCrayfishrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftEelrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftFrograwItem.get(16),
                    ModsItemlist.PamsHarvestCraftGreenheartfishItem.get(16),
                    ModsItemlist.PamsHarvestCraftGrouperrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftHerringrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftJellyfishrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftMudfishrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftOctopusrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftPerchrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftScalloprawItem.get(16),
                    ModsItemlist.PamsHarvestCraftShrimprawItem.get(16),
                    ModsItemlist.PamsHarvestCraftSnailrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftSnapperrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftTilapiarawItem.get(16),
                    ModsItemlist.PamsHarvestCraftTroutrawItem.get(16),
                    ModsItemlist.PamsHarvestCraftTunarawItem.get(16),
                    ModsItemlist.PamsHarvestCraftTurtlerawItem.get(16),
                    ModsItemlist.PamsHarvestCraftWalleyerawItem.get(16))
                .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
                .outputChances(
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500,
                    2500)
                .duration(300)
                .eut(TierEU.RECIPE_HV)
                .addTo(FGR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(5),
                    new ItemStack(Items.fishing_rod, 0),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatRaw, 16L))
                .itemOutputs(
                    ModsItemlist.PamsHarvestCraftAnchovyrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftBassrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftCalamarirawItem.get(32),
                    ModsItemlist.PamsHarvestCraftCarprawItem.get(32),
                    ModsItemlist.PamsHarvestCraftCatfishrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftCharrrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftClamrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftCrabrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftCrayfishrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftEelrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftFrograwItem.get(32),
                    ModsItemlist.PamsHarvestCraftGreenheartfishItem.get(32),
                    ModsItemlist.PamsHarvestCraftGrouperrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftHerringrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftJellyfishrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftMudfishrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftOctopusrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftPerchrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftScalloprawItem.get(32),
                    ModsItemlist.PamsHarvestCraftShrimprawItem.get(32),
                    ModsItemlist.PamsHarvestCraftSnailrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftSnapperrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftTilapiarawItem.get(32),
                    ModsItemlist.PamsHarvestCraftTroutrawItem.get(32),
                    ModsItemlist.PamsHarvestCraftTunarawItem.get(32),
                    ModsItemlist.PamsHarvestCraftTurtlerawItem.get(32),
                    ModsItemlist.PamsHarvestCraftWalleyerawItem.get(32))
                .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
                .outputChances(
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500,
                    7500)
                .duration(300)
                .eut(TierEU.RECIPE_HV)
                .addTo(FGR);

            RecipeBuilder.builder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(6),
                    new ItemStack(Items.fishing_rod, 0),
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatCooked, 16L))
                .itemOutputs(
                    ModsItemlist.PamsHarvestCraftAnchovyrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftBassrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftCalamarirawItem.get(64),
                    ModsItemlist.PamsHarvestCraftCarprawItem.get(64),
                    ModsItemlist.PamsHarvestCraftCatfishrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftCharrrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftClamrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftCrabrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftCrayfishrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftEelrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftFrograwItem.get(64),
                    ModsItemlist.PamsHarvestCraftGreenheartfishItem.get(64),
                    ModsItemlist.PamsHarvestCraftGrouperrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftHerringrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftJellyfishrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftMudfishrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftOctopusrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftPerchrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftScalloprawItem.get(64),
                    ModsItemlist.PamsHarvestCraftShrimprawItem.get(64),
                    ModsItemlist.PamsHarvestCraftSnailrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftSnapperrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftTilapiarawItem.get(64),
                    ModsItemlist.PamsHarvestCraftTroutrawItem.get(64),
                    ModsItemlist.PamsHarvestCraftTunarawItem.get(64),
                    ModsItemlist.PamsHarvestCraftTurtlerawItem.get(64),
                    ModsItemlist.PamsHarvestCraftWalleyerawItem.get(64))
                .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
                .outputChances(
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000,
                    9000)
                .duration(300)
                .eut(TierEU.RECIPE_HV)
                .addTo(FGR);

            if (Mods.NewHorizonsCoreMod.isModLoaded()) loadNHRecipe();
        }
    }

    @Optional.Method(modid = "dreamcraft")
    public void loadNHRecipe() {
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(9), NHItemList.MaceratedPlantmass.get(16))
            .itemOutputs(
                GregtechItemList.AlgaeBiomass.get(64),
                GregtechItemList.GreenAlgaeBiomass.get(64),
                GregtechItemList.BrownAlgaeBiomass.get(64),
                GregtechItemList.GoldenBrownAlgaeBiomass.get(64),
                GregtechItemList.RedAlgaeBiomass.get(64))
            .fluidInputs(FluidRegistry.getFluidStack("water", 10000))
            .duration(200)
            .eut(TierEU.RECIPE_EV)
            .addTo(FGR);
    }
}
