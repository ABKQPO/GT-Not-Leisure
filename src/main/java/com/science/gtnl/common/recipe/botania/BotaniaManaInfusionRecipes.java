package com.science.gtnl.common.recipe.botania;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.ModsItemlist;

import gregtech.api.enums.Mods;
import vazkii.botania.api.BotaniaAPI;

public class BotaniaManaInfusionRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        if (Mods.IWillFindYou.isModLoaded()) {
            BotaniaAPI.registerManaInfusionRecipe(
                GTNLItemList.ManaElectricProspectorTool.get(1),
                ModsItemlist.IWillFindYouIfuBuildingKit.get(1),
                5000);
        }
    }
}
