package com.science.gtnl.common.recipe.GTNL;

import static gregtech.api.enums.Mods.*;

import com.dreammaster.item.ItemList;
import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.GTNLItemList;
import com.science.gtnl.common.recipe.IRecipePool;
import com.science.gtnl.common.recipe.RecipeRegister;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class TheTwilightForestRecipes implements IRecipePool {

    final RecipeMap<?> TTFR = RecipeRegister.TheTwilightForestRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.NagaBook.get(1)))
            .itemOutputs(
                GTModHandler.getModItem(TwilightForest.ID, "item.trophy", 1, 1),
                GTModHandler.getModItem(TwilightForest.ID, "item.nagaScale", 32),
                ItemList.NagaScaleFragment.getIS(32),
                ItemList.NagaScaleChip.getIS(64))
            .outputChances(1000, 10000, 5000, 2500)
            .noOptimize()
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.LichBook.get(1)))
            .itemOutputs(
                GTModHandler.getModItem(TwilightForest.ID, "item.trophy", 1, 2),
                ItemList.LichBone.getIS(32),
                ItemList.LichBoneFragment.getIS(32),
                ItemList.LichBoneChip.getIS(64),
                GTModHandler.getModItem(Minecraft.ID, "ender_pearl", 32),
                GTModHandler.getModItem(Minecraft.ID, "book", 32),
                GTModHandler.getModItem(Minecraft.ID, "paper", 32))
            .outputChances(1000, 10000, 5000, 2500, 5000, 7500, 7500)
            .noOptimize()
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.MinotaurBook.get(1)))
            .itemOutputs(
                GTModHandler.getModItem(TwilightForest.ID, "item.steeleafIngot", 32),
                GTModHandler.getModItem(TwilightForest.ID, "item.ironwoodIngot", 32),
                GTModHandler.getModItem(Minecraft.ID, "emerald", 16),
                GTModHandler.getModItem(Minecraft.ID, "emerald_block", 1),
                GTModHandler.getModItem(Minecraft.ID, "iron_ingot", 32))
            .outputChances(10000, 10000, 2500, 1000, 7500)
            .noOptimize()
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.HydraBook.get(1)))
            .itemOutputs(
                GTModHandler.getModItem(TwilightForest.ID, "item.trophy", 1, 0),
                GTModHandler.getModItem(TwilightForest.ID, "item.fieryBlood", 16),
                GTModHandler.getModItem(Minecraft.ID, "redstone_block", 2),
                GTModHandler.getModItem(Minecraft.ID, "lapis_block", 2),
                GTModHandler.getModItem(Minecraft.ID, "iron_block", 2),
                GTModHandler.getModItem(Minecraft.ID, "gold_block", 2),
                GTModHandler.getModItem(Minecraft.ID, "emerald_block", 2),
                GTModHandler.getModItem(Minecraft.ID, "diamond_block", 2))
            .outputChances(1000, 9500, 7500, 7500, 5000, 5000, 2500, 2500)
            .noOptimize()
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.KnightPhantomBook.get(1)))
            .itemOutputs(
                GTModHandler.getModItem(TwilightForest.ID, "item.trophy", 1, 6),
                GTModHandler.getModItem(TwilightForest.ID, "item.knightMetal", 24))
            .outputChances(1000, 7500)
            .noOptimize()
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.UrGhastBook.get(1)))
            .itemOutputs(
                GTModHandler.getModItem(TwilightForest.ID, "item.trophy", 1, 3),
                GTModHandler.getModItem(TwilightForest.ID, "item.fieryTears", 12),
                GTModHandler.getModItem(TwilightForest.ID, "item.carminite", 16),
                ItemList.CarminiteFragment.getIS(32),
                ItemList.CarminiteChip.getIS(64),
                GTModHandler.getModItem(TwilightForest.ID, "item.steeleafIngot", 16),
                GTModHandler.getModItem(Minecraft.ID, "redstone_block", 4))
            .outputChances(1000, 10000, 10000, 5000, 2500, 5000, 7500)
            .noOptimize()
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.AlphaYetiBook.get(1)))
            .itemOutputs(
                GTModHandler.getModItem(TwilightForest.ID, "item.trophy", 1, 7),
                GTModHandler.getModItem(TwilightForest.ID, "item.alphaFur", 16),
                GTModHandler.getModItem(TwilightForest.ID, "item.iceBomb", 16),
                GTModHandler.getModItem(TwilightForest.ID, "item.arcticFur", 32))
            .outputChances(1000, 8000, 8000, 7500)
            .noOptimize()
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.SnowQueenBook.get(1)))
            .itemOutputs(
                GTModHandler.getModItem(TwilightForest.ID, "item.trophy", 1, 4),
                ItemList.SnowQueenBlood.getIS(16),
                ItemList.SnowQueenBloodDrop.getIS(32),
                GTModHandler.getModItem(Minecraft.ID, "packed_ice", 32),
                GTModHandler.getModItem(Minecraft.ID, "snowball", 64),
                GTModHandler.getModItem(TwilightForest.ID, "tile.TFAuroraBrick", 64),
                GTModHandler.getModItem(TwilightForest.ID, "tile.AuroraPillar", 64),
                GTModHandler.getModItem(TwilightForest.ID, "item.ironwoodIngot", 32),
                GTModHandler.getModItem(TwilightForest.ID, "item.knightMetal", 32),
                GTModHandler.getModItem(TwilightForest.ID, "item.arcticFur", 32))
            .outputChances(1000, 7500, 5000, 8000, 10000, 7500, 7500, 5000, 5000, 8000)
            .noOptimize()
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTNLItemList.GiantBook.get(1)))
            .itemOutputs(
                GTModHandler.getModItem(TwilightForest.ID, "tile.GiantCobble", 8),
                GTModHandler.getModItem(TwilightForest.ID, "tile.GiantObsidian", 8),
                GTModHandler.getModItem(TwilightForest.ID, "tile.GiantLog", 8),
                GTModHandler.getModItem(TwilightForest.ID, "tile.FluffyCloud", 32),
                GTModHandler.getModItem(TwilightForest.ID, "tile.WispyCloud", 32),
                GTModHandler.getModItem(TwilightForest.ID, "tile.HugeStalk", 8),
                GTModHandler.getModItem(TwilightForest.ID, "tile.HugeGloomBlock", 8),
                GTModHandler.getModItem(ExtraUtilities.ID, "cobblestone_compressed", 4, 7))
            .outputChances(7500, 7500, 7500, 7500, 7500, 7500, 7500, 2500)
            .noOptimize()
            .duration(600)
            .eut(1966080)
            .addTo(TTFR);
    }
}
