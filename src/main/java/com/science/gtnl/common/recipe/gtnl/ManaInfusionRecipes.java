package com.science.gtnl.common.recipe.gtnl;

import static gregtech.api.enums.Mods.*;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.science.gtnl.api.IRecipePool;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.loader.RecipePool;
import com.science.gtnl.utils.item.ItemUtils;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class ManaInfusionRecipes implements IRecipePool {

    public RecipeMap<?> MIR = RecipePool.ManaInfusionRecipes;

    @Override
    public void loadRecipes() {
        ItemStack daybloom = ItemUtils.getSpecialFlower("daybloom");
        ItemStack daybloomDecor = ItemUtils.getSpecialFlower("daybloomDecor");
        ItemStack hydroangeas = ItemUtils.getSpecialFlower("hydroangeas");
        ItemStack hydroangeasDecor = ItemUtils.getSpecialFlower("hydroangeasDecor");
        ItemStack nightshade = ItemUtils.getSpecialFlower("nightshade");
        ItemStack nightshadeDecor = ItemUtils.getSpecialFlower("nightshadeDecor");
        ItemStack bellethorn = ItemUtils.getSpecialFlower("bellethorn");
        ItemStack bellethornChibi = ItemUtils.getSpecialFlower("bellethornChibi");
        ItemStack agricarnation = ItemUtils.getSpecialFlower("agricarnation");
        ItemStack agricarnationChibi = ItemUtils.getSpecialFlower("agricarnationChibi");
        ItemStack hopperhock = ItemUtils.getSpecialFlower("hopperhock");
        ItemStack hopperhockChibi = ItemUtils.getSpecialFlower("hopperhockChibi");
        ItemStack rannuncarpus = ItemUtils.getSpecialFlower("rannuncarpus");
        ItemStack rannuncarpusChibi = ItemUtils.getSpecialFlower("rannuncarpusChibi");
        ItemStack clayconia = ItemUtils.getSpecialFlower("clayconia");
        ItemStack clayconiaChibi = ItemUtils.getSpecialFlower("clayconiaChibi");
        ItemStack marimorphosis = ItemUtils.getSpecialFlower("marimorphosis");
        ItemStack marimorphosisChibi = ItemUtils.getSpecialFlower("marimorphosisChibi");
        ItemStack bubbell = ItemUtils.getSpecialFlower("bubbell");
        ItemStack bubbellChibi = ItemUtils.getSpecialFlower("bubbellChibi");
        ItemStack solegnolia = ItemUtils.getSpecialFlower("solegnolia");
        ItemStack solegnoliaChibi = ItemUtils.getSpecialFlower("solegnoliaChibi");

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)), daybloom)
            .itemOutputs(daybloomDecor)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)), hydroangeas)
            .itemOutputs(hydroangeasDecor)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)), nightshade)
            .itemOutputs(nightshadeDecor)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)), bellethorn)
            .itemOutputs(bellethornChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                agricarnation)
            .itemOutputs(agricarnationChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)), hopperhock)
            .itemOutputs(hopperhockChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                rannuncarpus)
            .itemOutputs(rannuncarpusChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)), clayconia)
            .itemOutputs(clayconiaChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                marimorphosis)
            .itemOutputs(marimorphosisChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)), bubbell)
            .itemOutputs(bubbellChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)), solegnolia)
            .itemOutputs(solegnoliaChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Blocks.tallgrass, 1))
            .itemOutputs(new ItemStack(Blocks.tallgrass, 2, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Blocks.leaves, 1, 0))
            .itemOutputs(new ItemStack(Blocks.leaves, 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Blocks.leaves, 1))
            .itemOutputs(new ItemStack(Blocks.leaves, 2, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Blocks.leaves, 1, 2))
            .itemOutputs(new ItemStack(Blocks.leaves, 2, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Blocks.leaves, 1, 3))
            .itemOutputs(new ItemStack(Blocks.leaves, 2, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Blocks.leaves2, 1))
            .itemOutputs(new ItemStack(Blocks.leaves2, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Blocks.leaves2, 1, 1))
            .itemOutputs(new ItemStack(Blocks.leaves2, 2, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Blocks.gravel, 1))
            .itemOutputs(new ItemStack(Blocks.gravel, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(720))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Blocks.soul_sand, 1))
            .itemOutputs(new ItemStack(Blocks.soul_sand, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(4500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Blocks.netherrack, 1, 0))
            .itemOutputs(new ItemStack(Blocks.netherrack, 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Items.snowball, 1, 0))
            .itemOutputs(new ItemStack(Items.snowball, 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Items.coal, 1, 0))
            .itemOutputs(new ItemStack(Items.coal, 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(7500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Items.quartz, 1, 0))
            .itemOutputs(new ItemStack(Items.quartz, 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(7500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Items.glowstone_dust, 1, 0))
            .itemOutputs(new ItemStack(Items.glowstone_dust, 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(5000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "conjurationCatalyst", 1)),
                new ItemStack(Items.redstone, 1, 0))
            .itemOutputs(new ItemStack(Items.redstone, 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(5000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.double_plant, 1, 5))
            .itemOutputs(new ItemStack(Blocks.yellow_flower, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.yellow_flower, 1, 0))
            .itemOutputs(new ItemStack(Blocks.red_flower, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.red_flower, 1, 0))
            .itemOutputs(new ItemStack(Blocks.red_flower, 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.red_flower, 1, 1))
            .itemOutputs(new ItemStack(Blocks.red_flower, 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.red_flower, 1, 2))
            .itemOutputs(new ItemStack(Blocks.red_flower, 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.red_flower, 1, 3))
            .itemOutputs(new ItemStack(Blocks.red_flower, 1, 4))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.red_flower, 1, 4))
            .itemOutputs(new ItemStack(Blocks.red_flower, 1, 5))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.red_flower, 1, 5))
            .itemOutputs(new ItemStack(Blocks.red_flower, 1, 6))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.red_flower, 1, 6))
            .itemOutputs(new ItemStack(Blocks.red_flower, 1, 7))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.red_flower, 1, 7))
            .itemOutputs(new ItemStack(Blocks.red_flower, 1, 8))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.red_flower, 1, 8))
            .itemOutputs(new ItemStack(Blocks.double_plant, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.double_plant, 1, 0))
            .itemOutputs(new ItemStack(Blocks.double_plant, 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.double_plant, 1, 1))
            .itemOutputs(new ItemStack(Blocks.double_plant, 1, 4))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.double_plant, 1, 4))
            .itemOutputs(new ItemStack(Blocks.double_plant, 1, 5))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.tallgrass, 1))
            .itemOutputs(new ItemStack(Blocks.tallgrass, 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.deadbush, 1, 0))
            .itemOutputs(new ItemStack(Blocks.tallgrass, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.tallgrass, 1, 2))
            .itemOutputs(new ItemStack(Blocks.deadbush, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                GTModHandler.getModItem(Botania.ID, "stone", 1, 2))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "stone", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                GTModHandler.getModItem(Botania.ID, "stone", 1, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "stone", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                GTModHandler.getModItem(Botania.ID, "stone", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "stone", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                GTModHandler.getModItem(Botania.ID, "stone", 1, 3))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "stone", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.quartz, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 10))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.dirt, 1, 0))
            .itemOutputs(new ItemStack(Blocks.dirt, 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.brick_block, 1, 0))
            .itemOutputs(new ItemStack(Items.brick, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.clay, 1, 0))
            .itemOutputs(new ItemStack(Items.clay_ball, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.hardened_clay, 1, 0))
            .itemOutputs(new ItemStack(Blocks.sand, 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(50))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.cobblestone, 1, 0))
            .itemOutputs(new ItemStack(Blocks.sand, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(50))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.redstone, 1, 0))
            .itemOutputs(new ItemStack(Items.glowstone_dust, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(300))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.glowstone_dust, 1, 0))
            .itemOutputs(new ItemStack(Items.redstone, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(300))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.glowstone_dust, 1, 0))
            .itemOutputs(new ItemStack(Items.redstone, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(300))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.ghast_tear, 1, 0))
            .itemOutputs(new ItemStack(Items.ender_pearl, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(8000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.slime_ball, 1, 0))
            .itemOutputs(new ItemStack(Blocks.cactus, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.cactus, 1, 0))
            .itemOutputs(new ItemStack(Items.slime_ball, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        for (int meta = 15; meta >= 0; meta--) {
            GTValues.RA.stdBuilder()
                .itemInputs(
                    GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                    new ItemStack(Blocks.wool, 1, meta))
                .itemOutputs(new ItemStack(Items.string, 3, 0))
                .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
                .duration(20)
                .eut(2048)
                .addTo(MIR);
        }

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.writable_book, 1, 0))
            .itemOutputs(new ItemStack(Items.name_tag, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(4000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.flint, 1, 0))
            .itemOutputs(new ItemStack(Items.gunpowder, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.flint, 1, 0))
            .itemOutputs(new ItemStack(Items.gunpowder, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(4000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.gunpowder, 1, 0))
            .itemOutputs(new ItemStack(Items.flint, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(4000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.gunpowder, 1, 0))
            .itemOutputs(new ItemStack(Items.flint, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.blaze_rod, 1, 0))
            .itemOutputs(new ItemStack(Blocks.nether_wart, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(4000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.poisonous_potato, 1, 0))
            .itemOutputs(new ItemStack(Items.potato, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.pumpkin_seeds, 1, 0))
            .itemOutputs(new ItemStack(Items.dye, 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.melon_seeds, 1, 0))
            .itemOutputs(new ItemStack(Items.pumpkin_seeds, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.carrot, 1, 0))
            .itemOutputs(new ItemStack(Items.melon_seeds, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.potato, 1, 0))
            .itemOutputs(new ItemStack(Items.carrot, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.wheat, 1, 0))
            .itemOutputs(new ItemStack(Items.potato, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.dye, 1, 3))
            .itemOutputs(new ItemStack(Items.wheat_seeds, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.fish, 1, 3))
            .itemOutputs(new ItemStack(Items.fish, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.fish, 1, 2))
            .itemOutputs(new ItemStack(Items.fish, 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.fish, 1, 1))
            .itemOutputs(new ItemStack(Items.fish, 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.fish, 1, 0))
            .itemOutputs(new ItemStack(Items.fish, 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.vine, 1, 0))
            .itemOutputs(new ItemStack(Blocks.waterlily, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(320))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.waterlily, 1, 0))
            .itemOutputs(new ItemStack(Blocks.vine, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.waterlily, 1, 0))
            .itemOutputs(new ItemStack(Blocks.vine, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.snow, 1, 0))
            .itemOutputs(new ItemStack(Blocks.ice, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2250))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.stonebrick, 1, 0))
            .itemOutputs(new ItemStack(Blocks.stonebrick, 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(150))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                GTModHandler.getModItem(Botania.ID, "quartzTypeElf", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "quartz", 4, 5))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                GTModHandler.getModItem(Botania.ID, "quartzTypeRed", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "quartz", 4, 4))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                GTModHandler.getModItem(Botania.ID, "quartzTypeLavender", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "quartz", 4, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                GTModHandler.getModItem(Botania.ID, "quartzTypeBlaze", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "quartz", 4, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                GTModHandler.getModItem(Botania.ID, "quartzTypeMana", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "quartz", 4, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                GTModHandler.getModItem(Botania.ID, "quartzTypeDark", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "quartz", 4, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.quartz_block, 1, 0))
            .itemOutputs(new ItemStack(Items.quartz, 4, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.glowstone, 1, 0))
            .itemOutputs(new ItemStack(Items.glowstone_dust, 4, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.sapling, 1, 5))
            .itemOutputs(new ItemStack(Blocks.sapling, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.sapling, 1, 4))
            .itemOutputs(new ItemStack(Blocks.sapling, 1, 5))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.sapling, 1, 3))
            .itemOutputs(new ItemStack(Blocks.sapling, 1, 4))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.sapling, 1, 2))
            .itemOutputs(new ItemStack(Blocks.sapling, 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.sapling, 1, 1))
            .itemOutputs(new ItemStack(Blocks.sapling, 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.sapling, 1, 0))
            .itemOutputs(new ItemStack(Blocks.sapling, 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.log2, 1, 0))
            .itemOutputs(new ItemStack(Blocks.log2, 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.log, 1, 3))
            .itemOutputs(new ItemStack(Blocks.log2, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.log, 1, 2))
            .itemOutputs(new ItemStack(Blocks.log, 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.log, 1, 1))
            .itemOutputs(new ItemStack(Blocks.log, 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.log, 1, 0))
            .itemOutputs(new ItemStack(Blocks.log, 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Blocks.log2, 1, 1))
            .itemOutputs(new ItemStack(Blocks.log, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "alchemyCatalyst", 1)),
                new ItemStack(Items.rotten_flesh, 1, 0))
            .itemOutputs(new ItemStack(Items.leather, 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(600))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(3000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(Thaumcraft.ID, "ItemResource", 1, 2))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(3000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.block, Materials.Steel, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "storage", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(27000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.block, Materials.Thaumium, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "storage", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(27000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.gunpowder, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.redstone, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.sugar, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.glowstone_dust, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(750))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thaumium, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(250))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(150))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem("DraconicEvolution", "draconiumDust", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(50))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        for (int meta = 15; meta >= 0; meta--) {
            GTValues.RA.stdBuilder()
                .itemInputs(GTModHandler.getModItem(Botania.ID, "dye", 1, meta))
                .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 23))
                .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
                .duration(20)
                .eut(2048)
                .addTo(MIR);
        }

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Diamond, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(10000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.gemFlawless, Materials.Diamond, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(20000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.diamond, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.diamond_block, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "storage", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(360000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), new ItemStack(Blocks.tallgrass, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "grassSeeds", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.deadbush, 1, 1))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "grassSeeds", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.red_mushroom, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "grassSeeds", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(6500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.brown_mushroom, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "grassSeeds", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(6500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.quartz, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "quartz", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.glass, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaGlass", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(250))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.string, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 16))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(5000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.glass_bottle, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaBottle", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(10000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.ender_pearl, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(5000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Blocks.piston, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "pistonRelay", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(15000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.cookie, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaCookie", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(20000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(new ItemStack(Items.potato, 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "tinyPotato", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(31337))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(PamsHarvestCraft.ID, "wovencottonItem", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 22))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(15000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(PamsHarvestCraft.ID, "wovencottonItem", 1, 0))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 22))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(15000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(ForbiddenMagic.ID, "WandCaps", 1, 4))
            .itemOutputs(GTModHandler.getModItem(ForbiddenMagic.ID, "WandCaps", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem(ForbiddenMagic.ID, "WandCores", 1, 12))
            .itemOutputs(GTModHandler.getModItem(ForbiddenMagic.ID, "WandCores", 1, 11))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(10000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem(Botania.ID, "terraPlate", 1)),
                GTModHandler.getModItem(Botania.ID, "manaResource", 1, 0),
                GTModHandler.getModItem(Botania.ID, "manaResource", 1, 1),
                GTModHandler.getModItem(Botania.ID, "manaResource", 1, 2))
            .itemOutputs(GTModHandler.getModItem(Botania.ID, "manaResource", 1, 4))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(500000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);
    }
}
