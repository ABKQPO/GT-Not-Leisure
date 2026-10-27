package com.science.gtnl.compat.randomthings;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;

import gregtech.api.enums.Mods;
import gregtech.api.enums.ToolDictNames;
import gregtech.api.util.GTModHandler;

public class RandomThingsRecipes {

    private RandomThingsRecipes() {}

    public static void craftingTable() {
        if (!Mods.RandomThings.isModLoaded()) return;
        ItemStack ectoplasm = ModsItemlist.RandomThingsSpirit.get(1);

        GTModHandler.addCraftingRecipe(
            GTNLItemList.SoulCardboardHelmet.get(1),
            new Object[] { "ABA", "CDC", 'A', ectoplasm, 'B', new ItemStack(Items.leather_helmet, 1), 'C',
                GTNLItemList.CardboardBox.get(1), 'D', ToolDictNames.craftingToolHardHammer });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.SoulCardboardChestplate.get(1),
            new Object[] { "FBF", "CDC", "AEA", 'A', GTNLItemList.CardboardBox.get(1), 'B',
                ToolDictNames.craftingToolHardHammer, 'C', new ItemStack(Blocks.wool, 1, OreDictionary.WILDCARD_VALUE),
                'D', new ItemStack(Items.leather_chestplate, 1), 'E', ectoplasm, 'F', new ItemStack(Blocks.web, 1) });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.SoulCardboardLeggings.get(1),
            new Object[] { "ABA", "CDC", "E E", 'A', new ItemStack(Blocks.web, 1), 'B',
                new ItemStack(Items.leather_leggings, 1), 'C', GTNLItemList.CardboardBox.get(1), 'D',
                ToolDictNames.craftingToolHardHammer, 'E', ectoplasm });

        GTModHandler.addCraftingRecipe(
            GTNLItemList.SoulCardboardBoots.get(1),
            new Object[] { "ABA", "CDC", 'A', ectoplasm, 'B', new ItemStack(Items.leather_boots, 1), 'C',
                GTNLItemList.CardboardBox.get(1), 'D', ToolDictNames.craftingToolHardHammer });
    }
}
