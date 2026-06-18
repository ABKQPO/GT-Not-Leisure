package com.science.gtnl.common.recipe.gtnl;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.dreammaster.item.NHItemList;
import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.GTNLRecipeMaps;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.recipes.RecipeBuilder;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class PrimitiveBrickKilnRecipes implements IRecipePool {

    public RecipeMap<?> PBKR = GTNLRecipeMaps.PrimitiveBrickKilnRecipes;

    @Override
    public void loadRecipes() {

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 4),
                new ItemStack(Blocks.brick_block, 1))
            .itemOutputs(ItemList.Casing_BronzePlatedBricks.get(1))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 3),
                new ItemStack(Blocks.brick_block, 2))
            .itemOutputs(ItemList.Hull_Bronze_Bricks.get(1))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.CastIron, 3),
                new ItemStack(Blocks.brick_block, 2))
            .itemOutputs(ItemList.Hull_HP_Bricks.get(1))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Bronze, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.ClayCompound, 2),
                new ItemStack(Blocks.brick_block, 2))
            .itemOutputs(GTNLItemList.BronzeBrickCasing.get(2))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 2),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.ClayCompound, 2),
                new ItemStack(Blocks.brick_block, 2))
            .itemOutputs(GTNLItemList.SteelBrickCasing.get(2))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(
                ItemList.Firebrick.get(8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gypsum, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcite, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Clay, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Stone, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.QuartzSand, 1))
            .itemOutputs(ItemList.Casing_Firebricks.get(2))
            .fluidInputs(Materials.Water.getFluid(1000))
            .duration(300)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Netherrack, 4))
            .itemOutputs(new ItemStack(Blocks.nether_brick, 1))
            .duration(100)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.netherbrick, 4))
            .itemOutputs(new ItemStack(Blocks.nether_brick, 1))
            .duration(50)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.clay_ball, 1))
            .itemOutputs(new ItemStack(Items.brick, 1))
            .duration(40)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.clay, 1))
            .itemOutputs(new ItemStack(Blocks.brick_block, 1))
            .duration(100)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.CastIron, 1))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Clay, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Stone, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Flint, 8))
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.ClayCompound, 16))
            .duration(200)
            .eut(16)
            .addTo(PBKR);

        if (Mods.NewHorizonsCoreMod.isModLoaded() && Mods.Railcraft.isModLoaded()) loadNHRecipe();
    }

    @Optional.Method(modid = "dreamcraft")
    public void loadNHRecipe() {
        RecipeBuilder.builder()
            .itemInputs(NHItemList.CokeOvenBrick.get(4))
            .itemOutputs(ItemList.CokeOvenCasing.get(1))
            .duration(200)
            .eut(16)
            .addTo(PBKR);
        RecipeBuilder.builder()
            .itemInputs(NHItemList.AdvancedCokeOvenBrick.get(4))
            .itemOutputs(GTModHandler.getModItem(Mods.Railcraft.ID, "machine.alpha", 1, 12))
            .duration(200)
            .eut(16)
            .addTo(PBKR);
    }
}
