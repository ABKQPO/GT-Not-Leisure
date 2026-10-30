package com.science.gtnl.compat.storagedrawers;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.common.material.GTNLMaterials;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.ModsItemlist;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTOreDictUnificator;

public class StorageDrawersRecipes {

    public static void steamManufacturer() {
        if (!Mods.StorageDrawers.isModLoaded()) return;
        // Drawer template
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.piston, 1), GTOreDictUnificator.get("drawerBasic", 1))
            .itemOutputs(ModsItemlist.StorageDrawersUpgradeTemplate.get(3))
            .duration(10 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(GTNLRecipeMaps.SteamManufacturerRecipes);

        // Drawer controller
        RecipeBuilder.builder()
            .itemInputs(
                ModsItemlist.StorageDrawersUpgradeTemplate.get(1),
                GTOreDictUnificator.get("drawerBasic", 1),
                GTNLMaterials.Breel.get(OrePrefixes.gearGt, 2))
            .itemOutputs(ModsItemlist.StorageDrawersController.get(1))
            .duration(5 * SECONDS)
            .eut(TierEU.RECIPE_LV)
            .addTo(GTNLRecipeMaps.SteamManufacturerRecipes);
    }
}
