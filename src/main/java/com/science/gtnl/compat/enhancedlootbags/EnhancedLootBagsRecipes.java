package com.science.gtnl.compat.enhancedlootbags;

import net.minecraft.enchantment.Enchantment;

import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class EnhancedLootBagsRecipes {

    private EnhancedLootBagsRecipes() {}

    public static void craftingTable() {
        if (!Mods.EnhancedLootBags.isModLoaded()) return;
        GTModHandler.addCraftingRecipe(
            GTNLItemList.LootBagRedemption.get(1),
            new Object[] { "ABA", "CDC", "EFE", 'A', GTOreDictUnificator.get(OrePrefixes.screw, Materials.Steel, 1L),
                'B', ItemUtils.getEnchantedBook(Enchantment.fortune, 3), 'C', OrePrefixes.circuit.get(Materials.LV),
                'D', ItemList.Hull_LV.get(1), 'E', GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 1L), 'F',
                ModsItemlist.IronChestsBlockIronChest.get(1) });
    }
}
