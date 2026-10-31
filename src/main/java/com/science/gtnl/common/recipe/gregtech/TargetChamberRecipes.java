package com.science.gtnl.common.recipe.gregtech;

import static gtnhlanth.api.recipe.LanthanidesRecipeMaps.TARGET_CHAMBER_METADATA;

import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.compat.newhorizons.NHCoreRecipes;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTUtility;
import gtnhlanth.api.recipe.LanthanidesRecipeMaps;
import gtnhlanth.common.item.MaskList;
import gtnhlanth.common.register.LanthItemList;
import gtnhlanth.common.tileentity.recipe.beamline.TargetChamberMetadata;

public class TargetChamberRecipes implements IRecipePool {

    public RecipeMap<?> TCR = LanthanidesRecipeMaps.targetChamberRecipes;

    @Override
    public void loadRecipes() {
        ItemStack focusItem = new ItemStack(LanthItemList.maskMap.get(MaskList.CSOC), 0);

        RecipeBuilder.builder()
            .itemInputs(focusItem, ItemList.IC2_LapotronCrystal.getWildcard(1L))
            .itemOutputs(GTUtility.copyAmountUnsafe(512, ItemList.Circuit_Parts_Crystal_Chip_Master.get(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(3, 8, 1)
                    .minFocus(35)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_IV)
            .addTo(TCR);

        NHCoreRecipes.targetChamber();
    }

}
