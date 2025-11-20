package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.dreammaster.gthandler.CustomItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.RecipePool;
import com.science.gtnl.utils.enums.GTNLItemList;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class PrimitiveBrickKilnRecipes implements IRecipePool {

    public RecipeMap<?> PBKR = RecipePool.PrimitiveBrickKilnRecipes;

    @Override
    public void loadRecipes() {

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                new ItemStack(Blocks.brick_block, 1))
            .itemOutputs(ItemList.Casing_BronzePlatedBricks.get(1))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 3),
                new ItemStack(Blocks.brick_block, 2))
            .itemOutputs(ItemList.Hull_Bronze_Bricks.get(1))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.WroughtIron, 3),
                new ItemStack(Blocks.brick_block, 2))
            .itemOutputs(ItemList.Hull_HP_Bricks.get(1))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 2),
                new ItemStack(Blocks.brick_block, 2))
            .itemOutputs(GTNLItemList.BronzeBrickCasing.get(2))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CrudeSteel, 2),
                new ItemStack(Blocks.brick_block, 2))
            .itemOutputs(GTNLItemList.SteelBrickCasing.get(2))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Firebrick.get(4),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gypsum, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcite, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Clay, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Stone, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.QuartzSand, 1))
            .itemOutputs(ItemList.Casing_Firebricks.get(1))
            .fluidInputs(Materials.Water.getFluid(1000))
            .duration(300)
            .eut(16)
            .addTo(PBKR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Netherrack, 4))
            .itemOutputs(new ItemStack(Blocks.nether_brick, 1))
            .duration(100)
            .eut(16)
            .addTo(PBKR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.netherbrick, 3))
            .itemOutputs(new ItemStack(Blocks.nether_brick, 1))
            .duration(100)
            .eut(16)
            .addTo(PBKR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.clay_ball, 1))
            .itemOutputs(new ItemStack(Items.brick, 1))
            .duration(40)
            .eut(16)
            .addTo(PBKR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.clay, 1))
            .itemOutputs(new ItemStack(Blocks.brick_block, 1))
            .duration(100)
            .eut(16)
            .addTo(PBKR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.WroughtIron, 1))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        if (Mods.Railcraft.isModLoaded()) {
            GTValues.RA.stdBuilder()
                .itemInputs(CustomItemList.CokeOvenBrick.get(3))
                .itemOutputs(GTModHandler.getModItem(Mods.Railcraft.ID, "machine.alpha", 1, 7))
                .duration(200)
                .eut(16)
                .addTo(PBKR);
            GTValues.RA.stdBuilder()
                .itemInputs(CustomItemList.AdvancedCokeOvenBrick.get(3))
                .itemOutputs(GTModHandler.getModItem(Mods.Railcraft.ID, "machine.alpha", 1, 12))
                .duration(200)
                .eut(16)
                .addTo(PBKR);
        }

    }
}
