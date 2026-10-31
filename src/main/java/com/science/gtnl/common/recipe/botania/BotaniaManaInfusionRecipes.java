package com.science.gtnl.common.recipe.botania;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;

import vazkii.botania.api.BotaniaAPI;

public class BotaniaManaInfusionRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        ItemStack buildingKit = ModsItemlist.IWillFindYouIfuBuildingKit.getIfLoaded(1);
        if (buildingKit != null) {
            BotaniaAPI.registerManaInfusionRecipe(GTNLItemList.ManaElectricProspectorTool.get(1), buildingKit, 5000);
        }
    }
}
