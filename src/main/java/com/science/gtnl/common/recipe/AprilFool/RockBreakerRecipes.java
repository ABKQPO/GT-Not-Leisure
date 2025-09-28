package com.science.gtnl.common.recipe.AprilFool;

import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTRecipeBuilder.TICKS;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.loader.RecipePool;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class RockBreakerRecipes implements IRecipePool {

    public RecipeMap<?> RBR = RecipePool.RockBreakerRecipes;

    @Override
    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1))
            .itemOutputs(new ItemStack(Blocks.cobblestone, 1))
            .duration(16 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(2))
            .itemOutputs(new ItemStack(Blocks.stone, 1))
            .duration(16 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L))
            .itemOutputs(new ItemStack(Blocks.obsidian, 1))
            .duration(6 * SECONDS + 8 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(6),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1L))
            .itemOutputs(new ItemStack(Blocks.netherrack, 1))
            .duration(16 * TICKS)
            .eut(TierEU.RECIPE_LV)
            .addTo(RBR);

        if (Mods.EtFuturumRequiem.isModLoaded()) {
            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(4),
                    GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "blue_ice", 0, 0),
                    new ItemStack(Blocks.soul_sand, 0))
                .itemOutputs(GTOreDictUnificator.get(OrePrefixes.stone, Materials.Basalt, 1L))
                .duration(16 * TICKS)
                .eut(TierEU.RECIPE_LV)
                .addTo(RBR);

            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.getIntegratedCircuit(5),
                    GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "magma", 0, 0),
                    new ItemStack(Blocks.soul_sand, 0))
                .itemOutputs(GTModHandler.getModItem(Mods.EtFuturumRequiem.ID, "cobbled_deepslate", 1, 0))
                .duration(16 * TICKS)
                .eut(TierEU.RECIPE_LV)
                .addTo(RBR);
        }
    }
}
