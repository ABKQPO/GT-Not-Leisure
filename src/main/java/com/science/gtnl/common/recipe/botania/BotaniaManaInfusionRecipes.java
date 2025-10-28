package com.science.gtnl.common.recipe.botania;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.enums.Mods;
import gregtech.api.util.GTModHandler;
import vazkii.botania.api.BotaniaAPI;

public class BotaniaManaInfusionRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        if (Mods.IWillFindYou.isModLoaded()) {
            BotaniaAPI.registerManaInfusionRecipe(
                GTNLItemList.ManaElectricProspectorTool.get(1),
                GTModHandler.getModItem(Mods.IWillFindYou.ID, "ifu_buildingKit", 1),
                10000);
        }
    }
}
