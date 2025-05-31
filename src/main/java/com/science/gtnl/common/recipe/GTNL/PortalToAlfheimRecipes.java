package com.science.gtnl.common.recipe.GTNL;

import static com.science.gtnl.Utils.item.TextHandler.texter;
import static com.science.gtnl.loader.IScriptLoader.missing;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.util.GTModHandler.getModItem;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class PortalToAlfheimRecipes implements IRecipePool {

    final RecipeMap<?> PTAR = RecipeRegister.PortalToAlfheimRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.bread, 1))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(
                    2147483647,
                    getModItem(IndustrialCraft2.ID, "blockNuke", 1)
                        .setStackDisplayName(texter("Elves don't like bread.", "PTARRecipes.1"))))
            .duration(1200)
            .eut(0)
            .addTo(PTAR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(AdvancedSolarPanel.ID, "asp_crafting_items", 1, 9, missing))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 8, missing))
            .duration(20)
            .eut(2048)
            .addTo(PTAR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.IV, 1L))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 9, missing))
            .duration(20)
            .eut(2048)
            .addTo(PTAR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem(IndustrialCraft2.ID, "blockAlloyGlass", 1, 0, missing))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "elfGlass", 1, 0, missing))
            .duration(20)
            .eut(2048)
            .addTo(PTAR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.quartz, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "quartz", 1, 5, missing))
            .duration(20)
            .eut(2048)
            .addTo(PTAR);

        RecipeBuilder.builder()
            .itemInputs(ItemList.Casing_CleanStainlessSteel.get(1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "dreamwood", 1, 0, missing))
            .duration(20)
            .eut(2048)
            .addTo(PTAR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.TungstenSteel, 1L))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 7, missing))
            .duration(20)
            .eut(2048)
            .addTo(PTAR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(256, getModItem(IndustrialCraft2.ID, "blockITNT", 1)),
                GTUtility.copyAmount(0, new ItemStack(Blocks.beacon, 1)),
                GTUtility.copyAmount(0, GTNLItemList.ActivatedGaiaPylon.get(1)),
                GTModHandler.getModItem(Botania.ID, "manaResource", 1, 14, missing))
            .itemOutputs(
                GTModHandler.getModItem(Botania.ID, "manaResource", 16, 5, missing),
                GTModHandler.getModItem(Botania.ID, "dice", 1, 0, missing),
                GTModHandler.getModItem(Botania.ID, "blackLotus", 1, 0, missing),
                GTModHandler.getModItem(Botania.ID, "blackLotus", 1, 1, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 0, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 1, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 2, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 3, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 4, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 5, missing),
                GTModHandler.getModItem(Botania.ID, "overgrowthSeed", 1, 3, missing),
                GTModHandler.getModItem(Botania.ID, "manaResource", 16, 0, missing),
                GTModHandler.getModItem(Botania.ID, "manaResource", 8, 1, missing),
                GTModHandler.getModItem(Botania.ID, "manaResource", 4, 2, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 0, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 1, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 2, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 3, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 4, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 5, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 6, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 7, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 8, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 9, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 10, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 11, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 12, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 13, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 14, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 15, missing),
                GTModHandler.getModItem(Botania.ID, "pinkinator", 1, 0, missing),
                GTModHandler.getModItem(Botania.ID, "recordGaia2", 1, 0, missing),
                new ItemStack(Items.record_13, 1),
                new ItemStack(Items.record_wait, 1),
                GTModHandler.getModItem(Botania.ID, "gaiaHead", 1, 0, missing))
            .outputChances(
                10000,
                10000,
                650,
                560,
                930,
                1667,
                1667,
                1667,
                1667,
                1667,
                2500,
                9000,
                7000,
                5000,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                2000,
                5720,
                139,
                139,
                1)
            .duration(1200)
            .eut(122880)
            .addTo(PTAR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Avaritia.ID, "Infinity_Sword", 1, 0, missing)),
                GTModHandler.getModItem(Botania.ID, "manaResource", 1, 14, missing))
            .itemOutputs(
                GTModHandler.getModItem(Botania.ID, "manaResource", 16, 5, missing),
                GTModHandler.getModItem(Botania.ID, "dice", 1, 0, missing),
                GTModHandler.getModItem(Botania.ID, "blackLotus", 1, 0, missing),
                GTModHandler.getModItem(Botania.ID, "blackLotus", 1, 1, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 0, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 1, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 2, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 3, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 4, missing),
                GTModHandler.getModItem(Botania.ID, "ancientWill", 1, 5, missing),
                GTModHandler.getModItem(Botania.ID, "overgrowthSeed", 1, 3, missing),
                GTModHandler.getModItem(Botania.ID, "manaResource", 16, 0, missing),
                GTModHandler.getModItem(Botania.ID, "manaResource", 8, 1, missing),
                GTModHandler.getModItem(Botania.ID, "manaResource", 4, 2, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 0, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 1, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 2, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 3, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 4, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 5, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 6, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 7, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 8, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 9, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 10, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 11, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 12, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 13, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 14, missing),
                GTModHandler.getModItem(Botania.ID, "rune", 2, 15, missing),
                GTModHandler.getModItem(Botania.ID, "pinkinator", 1, 0, missing),
                GTModHandler.getModItem(Botania.ID, "recordGaia2", 1, 0, missing),
                new ItemStack(Items.record_13, 1),
                new ItemStack(Items.record_wait, 1),
                GTModHandler.getModItem(Botania.ID, "gaiaHead", 1, 0, missing))
            .outputChances(
                10000,
                10000,
                650,
                560,
                930,
                1667,
                1667,
                1667,
                1667,
                1667,
                2500,
                9000,
                7000,
                5000,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                1875,
                2000,
                5720,
                139,
                139,
                1)
            .duration(100)
            .eut(7864320)
            .addTo(PTAR);
    }
}
