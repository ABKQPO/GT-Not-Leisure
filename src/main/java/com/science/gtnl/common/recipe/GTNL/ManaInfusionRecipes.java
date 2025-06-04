package com.science.gtnl.common.recipe.GTNL;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.Utils.recipes.RecipeBuilder;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.loader.IRecipePool;
import com.science.gtnl.loader.RecipeRegister;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;

public class ManaInfusionRecipes implements IRecipePool {

    final RecipeMap<?> MIR = RecipeRegister.ManaInfusionRecipes;

    @Override
    public void loadRecipes() {

        ItemStack daybloom = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound daybloomType = daybloom.getTagCompound();
        if (daybloomType != null) {
            daybloomType.setString("type", "daybloom");
        } else {
            daybloomType = new NBTTagCompound();
            daybloomType.setString("type", "daybloom");
            daybloom.setTagCompound(daybloomType);
        }

        ItemStack daybloomDecor = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound daybloomDecorType = daybloomDecor.getTagCompound();
        if (daybloomDecorType != null) {
            daybloomDecorType.setString("type", "daybloomDecor");
        } else {
            daybloomDecorType = new NBTTagCompound();
            daybloomDecorType.setString("type", "daybloomDecor");
            daybloomDecor.setTagCompound(daybloomDecorType);
        }

        ItemStack hydroangeas = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound hydroangeasType = hydroangeas.getTagCompound();
        if (hydroangeasType != null) {
            hydroangeasType.setString("type", "hydroangeas");
        } else {
            hydroangeasType = new NBTTagCompound();
            hydroangeasType.setString("type", "hydroangeas");
            hydroangeas.setTagCompound(hydroangeasType);
        }

        ItemStack hydroangeasDecor = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound hydroangeasDecorType = hydroangeasDecor.getTagCompound();
        if (hydroangeasDecorType != null) {
            hydroangeasDecorType.setString("type", "hydroangeasDecor");
        } else {
            hydroangeasDecorType = new NBTTagCompound();
            hydroangeasDecorType.setString("type", "hydroangeasDecor");
            hydroangeasDecor.setTagCompound(hydroangeasDecorType);
        }

        ItemStack nightshade = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound nightshadeType = nightshade.getTagCompound();
        if (nightshadeType != null) {
            nightshadeType.setString("type", "nightshade");
        } else {
            nightshadeType = new NBTTagCompound();
            nightshadeType.setString("type", "nightshade");
            nightshade.setTagCompound(nightshadeType);
        }

        ItemStack nightshadeDecor = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound nightshadeDecorType = nightshadeDecor.getTagCompound();
        if (nightshadeDecorType != null) {
            nightshadeDecorType.setString("type", "nightshadeDecor");
        } else {
            nightshadeDecorType = new NBTTagCompound();
            nightshadeDecorType.setString("type", "nightshadeDecor");
            nightshadeDecor.setTagCompound(nightshadeDecorType);
        }

        ItemStack bellethorn = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound bellethornType = bellethorn.getTagCompound();
        if (bellethornType != null) {
            bellethornType.setString("type", "bellethorn");
        } else {
            bellethornType = new NBTTagCompound();
            bellethornType.setString("type", "bellethorn");
            bellethorn.setTagCompound(bellethornType);
        }

        ItemStack bellethornChibi = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound bellethornChibiType = bellethornChibi.getTagCompound();
        if (bellethornChibiType != null) {
            bellethornChibiType.setString("type", "bellethornChibi");
        } else {
            bellethornChibiType = new NBTTagCompound();
            bellethornChibiType.setString("type", "bellethornChibi");
            bellethornChibi.setTagCompound(bellethornChibiType);
        }

        ItemStack agricarnation = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound agricarnationType = agricarnation.getTagCompound();
        if (agricarnationType != null) {
            agricarnationType.setString("type", "agricarnation");
        } else {
            agricarnationType = new NBTTagCompound();
            agricarnationType.setString("type", "agricarnation");
            agricarnation.setTagCompound(agricarnationType);
        }

        ItemStack agricarnationChibi = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound agricarnationChibiType = agricarnationChibi.getTagCompound();
        if (agricarnationChibiType != null) {
            agricarnationChibiType.setString("type", "agricarnationChibi");
        } else {
            agricarnationChibiType = new NBTTagCompound();
            agricarnationChibiType.setString("type", "agricarnationChibi");
            agricarnationChibi.setTagCompound(agricarnationChibiType);
        }

        ItemStack hopperhock = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound hopperhockType = hopperhock.getTagCompound();
        if (hopperhockType != null) {
            hopperhockType.setString("type", "hopperhock");
        } else {
            hopperhockType = new NBTTagCompound();
            hopperhockType.setString("type", "hopperhock");
            hopperhock.setTagCompound(hopperhockType);
        }

        ItemStack hopperhockChibi = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound hopperhockChibiType = hopperhockChibi.getTagCompound();
        if (hopperhockChibiType != null) {
            hopperhockChibiType.setString("type", "hopperhockChibi");
        } else {
            hopperhockChibiType = new NBTTagCompound();
            hopperhockChibiType.setString("type", "hopperhockChibi");
            hopperhockChibi.setTagCompound(hopperhockChibiType);
        }

        ItemStack rannuncarpus = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound rannuncarpusType = rannuncarpus.getTagCompound();
        if (rannuncarpusType != null) {
            rannuncarpusType.setString("type", "rannuncarpus");
        } else {
            rannuncarpusType = new NBTTagCompound();
            rannuncarpusType.setString("type", "rannuncarpus");
            rannuncarpus.setTagCompound(rannuncarpusType);
        }

        ItemStack rannuncarpusChibi = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound rannuncarpusChibiType = rannuncarpusChibi.getTagCompound();
        if (rannuncarpusChibiType != null) {
            rannuncarpusChibiType.setString("type", "rannuncarpusChibi");
        } else {
            rannuncarpusChibiType = new NBTTagCompound();
            rannuncarpusChibiType.setString("type", "rannuncarpusChibi");
            rannuncarpusChibi.setTagCompound(rannuncarpusChibiType);
        }

        ItemStack clayconia = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound clayconiaType = clayconia.getTagCompound();
        if (clayconiaType != null) {
            clayconiaType.setString("type", "clayconia");
        } else {
            clayconiaType = new NBTTagCompound();
            clayconiaType.setString("type", "clayconia");
            clayconia.setTagCompound(clayconiaType);
        }

        ItemStack clayconiaChibi = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound clayconiaChibiType = clayconiaChibi.getTagCompound();
        if (clayconiaChibiType != null) {
            clayconiaChibiType.setString("type", "clayconiaChibi");
        } else {
            clayconiaChibiType = new NBTTagCompound();
            clayconiaChibiType.setString("type", "clayconiaChibi");
            clayconiaChibi.setTagCompound(clayconiaChibiType);
        }

        ItemStack marimorphosis = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound marimorphosisType = marimorphosis.getTagCompound();
        if (marimorphosisType != null) {
            marimorphosisType.setString("type", "marimorphosis");
        } else {
            marimorphosisType = new NBTTagCompound();
            marimorphosisType.setString("type", "marimorphosis");
            marimorphosis.setTagCompound(marimorphosisType);
        }

        ItemStack marimorphosisChibi = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound marimorphosisChibiType = marimorphosisChibi.getTagCompound();
        if (marimorphosisChibiType != null) {
            marimorphosisChibiType.setString("type", "marimorphosisChibi");
        } else {
            marimorphosisChibiType = new NBTTagCompound();
            marimorphosisChibiType.setString("type", "marimorphosisChibi");
            marimorphosisChibi.setTagCompound(marimorphosisChibiType);
        }

        ItemStack bubbell = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound bubbellType = bubbell.getTagCompound();
        if (bubbellType != null) {
            bubbellType.setString("type", "bubbell");
        } else {
            bubbellType = new NBTTagCompound();
            bubbellType.setString("type", "bubbell");
            bubbell.setTagCompound(bubbellType);
        }

        ItemStack bubbellChibi = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound bubbellChibiType = bubbellChibi.getTagCompound();
        if (bubbellChibiType != null) {
            bubbellChibiType.setString("type", "bubbellChibi");
        } else {
            bubbellChibiType = new NBTTagCompound();
            bubbellChibiType.setString("type", "bubbellChibi");
            bubbellChibi.setTagCompound(bubbellChibiType);
        }

        ItemStack solegnolia = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound solegnoliaType = solegnolia.getTagCompound();
        if (solegnoliaType != null) {
            solegnoliaType.setString("type", "solegnolia");
        } else {
            solegnoliaType = new NBTTagCompound();
            solegnoliaType.setString("type", "solegnolia");
            solegnolia.setTagCompound(solegnoliaType);
        }

        ItemStack solegnoliaChibi = GTModHandler.getModItem("Botania", "specialFlower", 1);
        NBTTagCompound solegnoliaChibiType = solegnoliaChibi.getTagCompound();
        if (solegnoliaChibiType != null) {
            solegnoliaChibiType.setString("type", "solegnoliaChibi");
        } else {
            solegnoliaChibiType = new NBTTagCompound();
            solegnoliaChibiType.setString("type", "solegnoliaChibi");
            solegnoliaChibi.setTagCompound(solegnoliaChibiType);
        }

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)), daybloom)
            .itemOutputs(daybloomDecor)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)), hydroangeas)
            .itemOutputs(hydroangeasDecor)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)), nightshade)
            .itemOutputs(nightshadeDecor)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)), bellethorn)
            .itemOutputs(bellethornChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                agricarnation)
            .itemOutputs(agricarnationChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)), hopperhock)
            .itemOutputs(hopperhockChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)), rannuncarpus)
            .itemOutputs(rannuncarpusChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)), clayconia)
            .itemOutputs(clayconiaChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                marimorphosis)
            .itemOutputs(marimorphosisChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)), bubbell)
            .itemOutputs(bubbellChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)), solegnolia)
            .itemOutputs(solegnoliaChibi)
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "tallgrass", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "tallgrass", 2, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "leaves", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "leaves", 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "leaves", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "leaves", 2, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "leaves", 1, 2))
            .itemOutputs(GTModHandler.getModItem("minecraft", "leaves", 2, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "leaves", 1, 3))
            .itemOutputs(GTModHandler.getModItem("minecraft", "leaves", 2, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "leaves2", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "leaves2", 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "leaves2", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "leaves2", 2, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "gravel", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "gravel", 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(720))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "soul_sand", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "soul_sand", 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(4500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "netherrack", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "netherrack", 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "snowball", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "snowball", 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "coal", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "coal", 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(7500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "quartz", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "quartz", 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(7500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "glowstone_dust", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "glowstone_dust", 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(5000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "conjurationCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "redstone", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "redstone", 2, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(5000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "double_plant", 1, 5))
            .itemOutputs(GTModHandler.getModItem("minecraft", "yellow_flower", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "yellow_flower", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "red_flower", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "red_flower", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "red_flower", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "red_flower", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "red_flower", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "red_flower", 1, 2))
            .itemOutputs(GTModHandler.getModItem("minecraft", "red_flower", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "red_flower", 1, 3))
            .itemOutputs(GTModHandler.getModItem("minecraft", "red_flower", 1, 4))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "red_flower", 1, 4))
            .itemOutputs(GTModHandler.getModItem("minecraft", "red_flower", 1, 5))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "red_flower", 1, 5))
            .itemOutputs(GTModHandler.getModItem("minecraft", "red_flower", 1, 6))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "red_flower", 1, 6))
            .itemOutputs(GTModHandler.getModItem("minecraft", "red_flower", 1, 7))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "red_flower", 1, 7))
            .itemOutputs(GTModHandler.getModItem("minecraft", "red_flower", 1, 8))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "red_flower", 1, 8))
            .itemOutputs(GTModHandler.getModItem("minecraft", "double_plant", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "double_plant", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "double_plant", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "double_plant", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "double_plant", 1, 4))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "double_plant", 1, 4))
            .itemOutputs(GTModHandler.getModItem("minecraft", "double_plant", 1, 5))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(400))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "tallgrass", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "tallgrass", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "deadbush", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "tallgrass", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "tallgrass", 1, 2))
            .itemOutputs(GTModHandler.getModItem("minecraft", "deadbush", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("Botania", "stone", 1, 2))
            .itemOutputs(GTModHandler.getModItem("Botania", "stone", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("Botania", "stone", 1, 1))
            .itemOutputs(GTModHandler.getModItem("Botania", "stone", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("Botania", "stone", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "stone", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("Botania", "stone", 1, 3))
            .itemOutputs(GTModHandler.getModItem("Botania", "stone", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "quartz", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 10))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "dirt", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "dirt", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "brick_block", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "brick", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "clay", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "clay_ball", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "hardened_clay", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "sand", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(50))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "cobblestone", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "sand", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(50))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "redstone", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "glowstone_dust", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(300))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "glowstone_dust", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "redstone", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(300))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "glowstone_dust", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "redstone", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(300))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "ghast_tear", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "ender_pearl", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(8000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "slime_ball", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "cactus", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "cactus", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "slime_ball", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 15))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 14))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 13))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 12))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 11))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 10))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 9))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 8))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 7))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 6))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 5))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 4))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 3))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 2))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wool", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "string", 3, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(100))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "writable_book", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "name_tag", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(4000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "flint", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "gunpowder", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "flint", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "gunpowder", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(4000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "gunpowder", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "flint", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(4000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "gunpowder", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "flint", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "blaze_rod", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "nether_wart", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(4000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "poisonous_potato", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "potato", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "pumpkin_seeds", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "dye", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "melon_seeds", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "pumpkin_seeds", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "carrot", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "melon_seeds", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "potato", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "carrot", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "wheat", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "potato", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "dye", 1, 3))
            .itemOutputs(GTModHandler.getModItem("minecraft", "wheat_seed", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(16000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "fish", 1, 3))
            .itemOutputs(GTModHandler.getModItem("minecraft", "fish", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "fish", 1, 2))
            .itemOutputs(GTModHandler.getModItem("minecraft", "fish", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "fish", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "fish", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "fish", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "fish", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "vine", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "waterlily", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(320))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "waterlily", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "vine", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "waterlily", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "vine", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(200))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "snow", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "ice", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2250))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "stonebrick", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "stonebrick", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(150))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("Botania", "quartzTypeElf", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "quartz", 4, 5))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("Botania", "quartzTypeRed", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "quartz", 4, 4))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("Botania", "quartzTypeLavender", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "quartz", 4, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("Botania", "quartzTypeBlaze", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "quartz", 4, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("Botania", "quartzTypeMana", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "quartz", 4, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("Botania", "quartzTypeDark", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "quartz", 4, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "quartz_block", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "quartz", 4, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "glowstone", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "glowstone_dust", 4, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(25))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "sapling", 1, 5))
            .itemOutputs(GTModHandler.getModItem("minecraft", "sapling", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "sapling", 1, 4))
            .itemOutputs(GTModHandler.getModItem("minecraft", "sapling", 1, 5))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "sapling", 1, 3))
            .itemOutputs(GTModHandler.getModItem("minecraft", "sapling", 1, 4))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "sapling", 1, 2))
            .itemOutputs(GTModHandler.getModItem("minecraft", "sapling", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "sapling", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "sapling", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "sapling", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "sapling", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(120))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "log2", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "log2", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "log", 1, 3))
            .itemOutputs(GTModHandler.getModItem("minecraft", "log2", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "log", 1, 2))
            .itemOutputs(GTModHandler.getModItem("minecraft", "log", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "log", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "log", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "log", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "log", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "log2", 1, 1))
            .itemOutputs(GTModHandler.getModItem("minecraft", "log", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "alchemyCatalyst", 1)),
                GTModHandler.getModItem("minecraft", "rotten_flesh", 1, 0))
            .itemOutputs(GTModHandler.getModItem("minecraft", "leather", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(600))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 11305))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(3000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Thaumcraft", "ItemResource", 1, 2))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(3000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.blockmetal6", 1, 13))
            .itemOutputs(GTModHandler.getModItem("Botania", "storage", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(27000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.blockmetal7", 1, 4))
            .itemOutputs(GTModHandler.getModItem("Botania", "storage", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(27000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "gunpowder", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "redstone", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "sugar", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "glowstone_dust", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(750))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2330))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(250))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2529))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(150))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("DraconicEvolution", "draconiumDust", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(50))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 1))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 2))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 3))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 4))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 5))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 6))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 7))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 8))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 9))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 10))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 11))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 12))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 13))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 14))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("Botania", "dye", 1, 15))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 23))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.02", 1, 30500))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(10000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("gregtech", "gt.metaitem.02", 1, 29500))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(20000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "diamond", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(40000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "diamond_block", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "storage", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(360000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "tallgrass", 1, 1))
            .itemOutputs(GTModHandler.getModItem("Botania", "grassSeeds", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "deadbush", 1, 1))
            .itemOutputs(GTModHandler.getModItem("Botania", "grassSeeds", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(2500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "red_mushroom", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "grassSeeds", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(6500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "brown_mushroom", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "grassSeeds", 1, 2))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(6500))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "quartz", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "quartz", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "glass", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaGlass", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(250))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "string", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 16))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(5000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "glass_bottle", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaBottle", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(10000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "ender_pearl", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 1))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(5000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "piston", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "pistonRelay", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(15000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "cookie", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaCookie", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(20000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("minecraft", "potato", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "tinyPotato", 1, 0))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(31337))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("harvestcraft", "wovencottonItem", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 22))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(15000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("harvestcraft", "wovencottonItem", 1, 0))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 22))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(15000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("ForbiddenMagic", "Cores", 1, 8))
            .itemOutputs(GTModHandler.getModItem("ForbiddenMagic", "Cores", 1, 7))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(10000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("ForbiddenMagic", "WandCaps", 1, 4))
            .itemOutputs(GTModHandler.getModItem("ForbiddenMagic", "WandCaps", 1, 3))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(1000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(GTModHandler.getModItem("ForbiddenMagic", "WandCores", 1, 12))
            .itemOutputs(GTModHandler.getModItem("ForbiddenMagic", "WandCores", 1, 11))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(10000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);

        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.copyAmount(0, GTModHandler.getModItem("Botania", "terraPlate", 1)),
                GTModHandler.getModItem("Botania", "manaResource", 1, 0),
                GTModHandler.getModItem("Botania", "manaResource", 1, 1),
                GTModHandler.getModItem("Botania", "manaResource", 1, 2))
            .itemOutputs(GTModHandler.getModItem("Botania", "manaResource", 1, 4))
            .fluidInputs(MaterialPool.FluidMana.getFluidOrGas(500000))
            .duration(20)
            .eut(2048)
            .addTo(MIR);
    }
}
