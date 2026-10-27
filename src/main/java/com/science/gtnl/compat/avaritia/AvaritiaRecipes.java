package com.science.gtnl.compat.avaritia;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.util.data.ItemId;
import com.science.gtnl.common.recipe.thaumcraft.TCRecipeTools;

import cpw.mods.fml.common.Optional;
import fox.spiteful.avaritia.items.LudicrousItems;
import gregtech.api.enums.Mods;

public class AvaritiaRecipes {

    private AvaritiaRecipes() {}

    public static void thaumcraftExclusions() {
        if (!Mods.Avaritia.isModLoaded()) return;
        thaumcraftExclusionsRecipes();
    }

    @Optional.Method(modid = "Avaritia")
    private static void thaumcraftExclusionsRecipes() {
        ItemId bigPearl = ItemId.create(new ItemStack(LudicrousItems.bigPearl));
        TCRecipeTools.UNCONSUMED_ITEMS.add(bigPearl);
        TCRecipeTools.PRIMORDIAL_PEARL_RETURN_EXCLUSIONS.add(bigPearl);
        TCRecipeTools.UNCONSUMED_ITEMS.add(ItemId.create(new ItemStack(LudicrousItems.armok_orb)));
    }
}
