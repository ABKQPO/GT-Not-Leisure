package com.science.gtnl.common.recipe.gregtech;

import static gtnhlanth.api.recipe.LanthanidesRecipeMaps.TARGET_CHAMBER_METADATA;

import net.minecraft.item.ItemStack;

import com.dreammaster.gthandler.CustomItemList;
import com.dreammaster.item.NHItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTOreDictUnificator;
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

        if (Mods.NewHorizonsCoreMod.isModLoaded()) loadNHRecipe();
    }

    @Optional.Method(modid = "dreamcraft")
    public void loadNHRecipe() {
        ItemStack focusItem = new ItemStack(LanthItemList.maskMap.get(MaskList.CSOC), 0);

        RecipeBuilder.builder()
            .itemInputs(focusItem, ItemList.IC2_EnergyCrystal.getWildcard(1L))
            .itemOutputs(GTUtility.copyAmountUnsafe(512, CustomItemList.EngravedEnergyChip.get(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(4, 10, 1)
                    .minFocus(45)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_IV)
            .addTo(TCR);

        RecipeBuilder.builder()
            .itemInputs(focusItem, GTOreDictUnificator.get(OrePrefixes.plate, Materials.Diamond, 1))
            .itemOutputs(GTUtility.copyAmountUnsafe(512, CustomItemList.EngravedDiamondCrystalChip.get(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(5, 10, 1)
                    .minFocus(45)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_LuV)
            .addTo(TCR);

        RecipeBuilder.builder()
            .itemInputs(focusItem, GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.GarnetYellow, 1))
            .itemOutputs(GTUtility.copyAmountUnsafe(512, CustomItemList.EngravedQuantumChip.get(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(6, 12, 1)
                    .minFocus(45)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_LuV)
            .addTo(TCR);

        RecipeBuilder.builder()
            .itemInputs(focusItem, GTOreDictUnificator.get(OrePrefixes.plate, Materials.Gold, 1))
            .itemOutputs(GTUtility.copyAmountUnsafe(1024, NHItemList.EngravedGoldChip.getIS(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(5, 12, 1)
                    .minFocus(35)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_IV)
            .addTo(TCR);

        RecipeBuilder.builder()
            .itemInputs(focusItem, CustomItemList.ManyullynCrystal.get(1))
            .itemOutputs(GTUtility.copyAmountUnsafe(512, NHItemList.EngravedManyullynCrystalChip.getIS(1)))
            .metadata(
                TARGET_CHAMBER_METADATA,
                TargetChamberMetadata.builder(focusItem)
                    .particleID(1)
                    .amount(2)
                    .energy(6, 10, 1)
                    .minFocus(45)
                    .build())
            .duration(1)
            .eut(TierEU.RECIPE_LuV)
            .addTo(TCR);
    }

}
