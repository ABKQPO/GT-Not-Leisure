package com.science.gtnl.common.recipe.gtnl;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Mods;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeElvenTrade;

public class PortalToAlfheimRecipes implements IRecipePool {

    public RecipeMap<?> PTAR = GTNLRecipeMaps.PortalToAlfheimRecipes;

    @Override
    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.bread, 1))
            .itemOutputs(
                GTUtility.copyAmountUnsafe(
                    Integer.MAX_VALUE,
                    GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockNuke", 1)
                        .setStackDisplayName(StatCollector.translateToLocal("PTARRecipes.1"))))
            .duration(1200)
            .eut(0)
            .fake()
            .addTo(PTAR);

        for (RecipeElvenTrade recipe : BotaniaAPI.elvenTradeRecipes) {

            List<Object> originInputs = recipe.getInputs();
            ItemStack output = recipe.getOutput()
                .copy();

            List<List<ItemStack>> combinations = new ArrayList<>();
            combinations.add(new ArrayList<>());

            for (Object input : originInputs) {

                List<List<ItemStack>> nextCombinations = new ArrayList<>();

                if (input instanceof String oreName) {
                    List<ItemStack> ores = OreDictionary.getOres(oreName);

                    for (ItemStack oreStack : ores) {
                        ItemStack stack = oreStack.copy();
                        if (stack.getItemDamage() == Short.MAX_VALUE) {
                            stack.setItemDamage(0);
                        }

                        for (List<ItemStack> prev : combinations) {
                            List<ItemStack> next = new ArrayList<>(prev);
                            next.add(stack);
                            nextCombinations.add(next);
                        }
                    }

                } else if (input instanceof ItemStack itemStack) {
                    ItemStack stack = itemStack.copy();
                    if (stack.getItemDamage() == Short.MAX_VALUE) {
                        stack.setItemDamage(0);
                    }

                    for (List<ItemStack> prev : combinations) {
                        List<ItemStack> next = new ArrayList<>(prev);
                        next.add(stack);
                        nextCombinations.add(next);
                    }
                }

                combinations = nextCombinations;
            }

            for (List<ItemStack> inputs : combinations) {
                RecipeBuilder.builder()
                    .itemInputs(inputs)
                    .itemOutputs(output)
                    .duration(20)
                    .eut(2048)
                    .addTo(PTAR);
            }
        }

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmountUnsafe(256, GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockITNT", 1)),
                new ItemStack(Blocks.beacon, 0),
                GTNLItemList.ActivatedGaiaPylon.get(0),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 1, 14))
            .itemOutputs(
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 16, 5),
                GTModHandler.getModItem(Mods.Botania.ID, "dice", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "blackLotus", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "blackLotus", 1, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 2),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 3),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 4),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 5),
                GTModHandler.getModItem(Mods.Botania.ID, "overgrowthSeed", 1, 3),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 16, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 8, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 4, 2),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 2),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 3),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 5),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 6),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 7),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 8),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 9),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 10),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 11),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 12),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 13),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 14),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 15),
                GTModHandler.getModItem(Mods.Botania.ID, "pinkinator", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "recordGaia2", 1, 0),
                new ItemStack(Items.record_13, 1),
                new ItemStack(Items.record_wait, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "gaiaHead", 1, 0))
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
                GTModHandler.getModItem(Mods.Avaritia.ID, "Infinity_Sword", 0, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 1, 14))
            .itemOutputs(
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 16, 5),
                GTModHandler.getModItem(Mods.Botania.ID, "dice", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "blackLotus", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "blackLotus", 1, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 2),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 3),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 4),
                GTModHandler.getModItem(Mods.Botania.ID, "ancientWill", 1, 5),
                GTModHandler.getModItem(Mods.Botania.ID, "overgrowthSeed", 1, 3),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 16, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 8, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "manaResource", 4, 2),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 2),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 3),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 4),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 5),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 6),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 7),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 8),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 9),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 10),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 11),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 12),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 13),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 14),
                GTModHandler.getModItem(Mods.Botania.ID, "rune", 2, 15),
                GTModHandler.getModItem(Mods.Botania.ID, "pinkinator", 1, 0),
                GTModHandler.getModItem(Mods.Botania.ID, "recordGaia2", 1, 0),
                new ItemStack(Items.record_13, 1),
                new ItemStack(Items.record_wait, 1),
                GTModHandler.getModItem(Mods.Botania.ID, "gaiaHead", 1, 0))
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
