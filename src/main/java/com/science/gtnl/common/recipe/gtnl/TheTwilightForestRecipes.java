package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.dreammaster.item.NHItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class TheTwilightForestRecipes implements IRecipePool {

    public RecipeMap<?> TTFR = GTNLRecipeMaps.TheTwilightForestRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.MinotaurBook.get(0))
            .itemOutputs(
                ModsItemlist.MinoshroomTrophy.get(1),
                ModsItemlist.TwilightForestItemSteeleafIngot.get(32),
                ModsItemlist.TwilightForestItemIronwoodIngot.get(32),
                new ItemStack(Items.emerald, 16),
                new ItemStack(Blocks.emerald_block, 1),
                new ItemStack(Items.iron_ingot, 32))
            .outputChances(1000, 10000, 10000, 2500, 1000, 7500)
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.HydraBook.get(0))
            .itemOutputs(
                ModsItemlist.HydraTrophy.get(1),
                ModsItemlist.TwilightForestItemFieryBlood.get(16),
                new ItemStack(Blocks.redstone_block, 2),
                new ItemStack(Blocks.lapis_block, 2),
                new ItemStack(Blocks.iron_block, 2),
                new ItemStack(Blocks.gold_block, 2),
                new ItemStack(Blocks.emerald_block, 2),
                new ItemStack(Blocks.diamond_block, 2))
            .outputChances(1000, 9500, 7500, 7500, 5000, 5000, 2500, 2500)
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.KnightPhantomBook.get(0))
            .itemOutputs(ModsItemlist.KnightPhantomTrophy.get(1), ModsItemlist.TwilightForestItemKnightMetal.get(24))
            .outputChances(1000, 7500)
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.AlphaYetiBook.get(0))
            .itemOutputs(
                ModsItemlist.AlphaYetiTrophy.get(1),
                ModsItemlist.TwilightForestItemAlphaFur.get(16),
                ModsItemlist.TwilightForestItemIceBomb.get(16),
                ModsItemlist.TwilightForestItemArcticFur.get(32))
            .outputChances(1000, 8000, 8000, 7500)
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.GiantBook.get(0))
            .itemOutputs(
                ModsItemlist.TwilightForestTileGiantCobble.get(8),
                ModsItemlist.TwilightForestTileGiantObsidian.get(8),
                ModsItemlist.TwilightForestTileGiantLog.get(8),
                ModsItemlist.TwilightForestTileFluffyCloud.get(32),
                ModsItemlist.TwilightForestTileWispyCloud.get(32),
                ModsItemlist.TwilightForestTileHugeStalk.get(8),
                ModsItemlist.TwilightForestTileHugeGloomBlock.get(8),
                ModsItemlist.ExtraUtilitiesCompressedCobbleEight.get(4))
            .outputChances(7500, 7500, 7500, 7500, 7500, 7500, 7500, 2500)
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.TwilightForestBook.get(0))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(262144, new ItemStack(Items.book, 1)),
                GTUtility.copyAmountUnsafe(262144, new ItemStack(Items.ender_pearl, 1)),
                GTUtility.copyAmountUnsafe(262144, new ItemStack(Blocks.emerald_block, 1)),
                GTUtility.copyAmountUnsafe(262144, new ItemStack(Blocks.diamond_block, 1)),
                GTUtility.copyAmountUnsafe(262144, new ItemStack(Blocks.lapis_block, 1)),
                GTUtility.copyAmountUnsafe(262144, new ItemStack(Blocks.redstone_block, 1)),
                GTUtility.copyAmountUnsafe(262144, new ItemStack(Blocks.gold_block, 1)),
                GTUtility.copyAmountUnsafe(262144, new ItemStack(Blocks.iron_block, 1)),
                GTUtility
                    .copyAmountUnsafe(262144, GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Knightmetal, 1L)),
                GTUtility
                    .copyAmountUnsafe(262144, GTOreDictUnificator.get(OrePrefixes.ingot, Materials.FierySteel, 1L)),
                GTUtility.copyAmountUnsafe(262144, GTOreDictUnificator.get(OrePrefixes.ingot, Materials.IronWood, 1L)),
                GTUtility.copyAmountUnsafe(262144, GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steeleaf, 1L)),
                GTUtility.copyAmountUnsafe(65536, ModsItemlist.TwilightForestItemFieryBlood.get(1)),
                GTUtility.copyAmountUnsafe(65536, ModsItemlist.TwilightForestItemNagaScale.get(0)))
            .duration(200)
            .eut(TierEU.RECIPE_UHV)
            .addTo(TTFR);

        if (Mods.NewHorizonsCoreMod.isModLoaded()) loadNHRecipe();
    }

    @Optional.Method(modid = "dreamcraft")
    public void loadNHRecipe() {
        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.NagaBook.get(0))
            .itemOutputs(
                ModsItemlist.NagaTrophy.get(1),
                ModsItemlist.TwilightForestItemNagaScale.get(32),
                NHItemList.NagaScaleFragment.get(32),
                NHItemList.NagaScaleChip.get(64))
            .outputChances(1000, 10000, 5000, 2500)
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.LichBook.get(0))
            .itemOutputs(
                ModsItemlist.LichTrophy.get(1),
                NHItemList.LichBone.get(32),
                NHItemList.LichBoneFragment.get(32),
                NHItemList.LichBoneChip.get(64),
                new ItemStack(Items.ender_pearl, 32),
                new ItemStack(Items.book, 32),
                new ItemStack(Items.paper, 32))
            .outputChances(1000, 10000, 5000, 2500, 5000, 7500, 7500)
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.UrGhastBook.get(0))
            .itemOutputs(
                ModsItemlist.UrGhastTrophy.get(1),
                ModsItemlist.TwilightForestItemFieryTears.get(12),
                ModsItemlist.TwilightForestItemCarminite.get(16),
                NHItemList.CarminiteFragment.get(32),
                NHItemList.CarminiteChip.get(64),
                ModsItemlist.TwilightForestItemSteeleafIngot.get(16),
                new ItemStack(Blocks.redstone_block, 4))
            .outputChances(1000, 10000, 10000, 5000, 2500, 5000, 7500)
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTNLItemList.SnowQueenBook.get(0))
            .itemOutputs(
                ModsItemlist.SnowQueenTrophy.get(1),
                NHItemList.SnowQueenBlood.get(16),
                NHItemList.SnowQueenBloodDrop.get(32),
                new ItemStack(Blocks.packed_ice, 32),
                new ItemStack(Items.snowball, 64),
                ModsItemlist.TwilightForestAuroraBrick.get(64),
                ModsItemlist.TwilightForestTileAuroraPillar.get(64),
                ModsItemlist.TwilightForestItemIronwoodIngot.get(32),
                ModsItemlist.TwilightForestItemKnightMetal.get(32),
                ModsItemlist.TwilightForestItemArcticFur.get(32))
            .outputChances(1000, 7500, 5000, 8000, 10000, 7500, 7500, 5000, 5000, 8000)
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);
    }

}
