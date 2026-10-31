package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.compat.pamsharvestcraft.PamsHarvestCraftRecipes;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class FishingGroundRecipes implements IRecipePool {

    public RecipeMap<?> FGR = GTNLRecipeMaps.FishingGroundRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.fishing_rod, 0))
            .circuit(1)
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
                new ItemStack(Items.fishing_rod, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatRaw, 16L))
            .circuit(2)
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
                new ItemStack(Items.fishing_rod, 0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.MeatCooked, 16L))
            .circuit(3)
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
            .itemInputs(ItemList.ActivatedCarbonFilterMesh.get(1))
            .circuit(7)
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
            .itemInputs(GTUtility.copyAmount(0, ItemList.ActivatedCarbonFilterMesh.get(1)))
            .circuit(8)
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
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Mytryl, 32L),
                ModsItemlist.PamsHarvestCraftSeaweedItem.get(64))
            .circuit(10)
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

        PamsHarvestCraftRecipes.fishingGround();
    }

}
