package com.science.gtnl.common.materials;

import static bartworks.util.BWUtil.subscriptNumbers;

import org.apache.commons.lang3.tuple.Pair;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TextureSet;
import gtPlusPlus.core.client.CustomTextureSet;

public class MaterialPool implements Runnable {

    public static final int offsetID_01 = 25_000;

    public static final Werkstoff Hexanitrohexaazaisowurtzitane = new Werkstoff(
        new short[] { 47, 53, 57 },
        "Hexanitrohexaazaisowurtzitane",
        subscriptNumbers("C6H6N12O12"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 1,
        TextureSet.SET_SHINY);

    public static final Werkstoff CrudeHexanitrohexaazaisowurtzitane = new Werkstoff(
        new short[] { 20, 71, 88 },
        "CrudeHexanitrohexaazaisowurtzitane",
        subscriptNumbers("C6H6N12O12"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 2,
        TextureSet.SET_SHINY);

    public static final Werkstoff SilicaGel = new Werkstoff(
        new short[] { 77, 173, 202 },
        "SilicaGel",
        subscriptNumbers(""),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 3,
        TextureSet.SET_SHINY);

    public static final Werkstoff Ethylenediamine = new Werkstoff(
        new short[] { 26, 90, 113 },
        "Ethylenediamine",
        subscriptNumbers("C2H8N2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 4,
        TextureSet.SET_FLUID);

    public static final Werkstoff Ethanolamine = new Werkstoff(
        new short[] { 27, 92, 115 },
        "Ethanolamine",
        subscriptNumbers("C2H7NO"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 5,
        TextureSet.SET_FLUID);

    public static final Werkstoff SilicaGelBase = new Werkstoff(
        new short[] { 55, 146, 119 },
        "SilicaGelBase",
        subscriptNumbers(""),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 6,
        TextureSet.SET_FLUID);

    public static final Werkstoff FluoroboricAcide = new Werkstoff(
        new short[] { 139, 183, 139 },
        "FluoroboricAcide",
        subscriptNumbers("HBF4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 7,
        TextureSet.SET_FLUID);

    public static final Werkstoff Tetraacetyldinitrohexaazaisowurtzitane = new Werkstoff(
        new short[] { 57, 3, 52 },
        "Tetraacetyldinitrohexaazaisowurtzitane",
        subscriptNumbers("C14H18N8O6"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 8,
        TextureSet.SET_SHINY);

    public static final Werkstoff NitroniumTetrafluoroborate = new Werkstoff(
        new short[] { 53, 56, 57 },
        "NitroniumTetrafluoroborate",
        subscriptNumbers("NO2BF4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 9,
        TextureSet.SET_SHINY);

    public static final Werkstoff NitronsoniumTetrafluoroborate = new Werkstoff(
        new short[] { 53, 56, 57 },
        "NitronsoniumTetrafluoroborate",
        subscriptNumbers("NOBF4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 10,
        TextureSet.SET_SHINY);

    public static final Werkstoff BoronFluoride = new Werkstoff(
        new short[] { 200, 196, 202 },
        "BoronFluoride",
        subscriptNumbers("BF3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 11,
        TextureSet.SET_FLUID);

    public static final Werkstoff SodiumTetrafluoroborate = new Werkstoff(
        new short[] { 147, 89, 13 },
        "SodiumTetrafluoroborate",
        subscriptNumbers("NaBF4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 12,
        TextureSet.SET_SHINY);

    public static final Werkstoff BoronTrioxide = new Werkstoff(
        new short[] { 127, 147, 161 },
        "BoronTrioxide",
        subscriptNumbers("B2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 13,
        TextureSet.SET_SHINY);

    public static final Werkstoff Dibenzyltetraacetylhexaazaisowurtzitane = new Werkstoff(
        new short[] { 89, 99, 68 },
        "Dibenzyltetraacetylhexaazaisowurtzitane",
        subscriptNumbers("C28H32N6O4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 14,
        TextureSet.SET_SHINY);

    public static final Werkstoff Benzaldehyde = new Werkstoff(
        new short[] { 142, 89, 27 },
        "Benzaldehyde",
        subscriptNumbers("C7H6O"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 15,
        TextureSet.SET_FLUID);

    public static final Werkstoff HydrobromicAcid = new Werkstoff(
        new short[] { 160, 86, 62 },
        "HydrobromicAcid",
        subscriptNumbers("HBr"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 16,
        TextureSet.SET_FLUID);

    public static final Werkstoff SuccinimidylAcetate = new Werkstoff(
        new short[] { 89, 99, 68 },
        "SuccinimidylAcetate",
        subscriptNumbers("C6H7NO4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 17,
        TextureSet.SET_SHINY);

    public static final Werkstoff Hexabenzylhexaazaisowurtzitane = new Werkstoff(
        new short[] { 89, 99, 68 },
        "Hexabenzylhexaazaisowurtzitane",
        subscriptNumbers("C48H48N6"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 18,
        TextureSet.SET_SHINY);

    public static final Werkstoff NHydroxysuccinimide = new Werkstoff(
        new short[] { 109, 100, 113 },
        "N-Hydroxysuccinimide",
        subscriptNumbers("C4H5NO3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 19,
        TextureSet.SET_SHINY);

    public static final Werkstoff SuccinicAnhydride = new Werkstoff(
        new short[] { 57, 15, 19 },
        "SuccinicAnhydride",
        subscriptNumbers("C4H4O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 20,
        TextureSet.SET_SHINY);

    public static final Werkstoff HydroxylamineHydrochloride = new Werkstoff(
        new short[] { 66, 49, 23 },
        "HydroxylamineHydrochloride",
        subscriptNumbers("H4NOCl"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 21,
        TextureSet.SET_FLUID);

    public static final Werkstoff BariumChloride = new Werkstoff(
        new short[] { 207, 99, 84 },
        "BariumChloride",
        subscriptNumbers("BaCl2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 22,
        TextureSet.SET_SHINY);

    public static final Werkstoff HydroxylammoniumSulfate = new Werkstoff(
        new short[] { 117, 114, 104 },
        "HydroxylammoniumSulfate",
        subscriptNumbers("N2H8SO6"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 23,
        TextureSet.SET_SHINY);

    public static final Werkstoff AcrylonitrileButadieneStyrene = new Werkstoff(
        new short[] { 100, 100, 100 },
        "AcrylonitrileButadieneStyrene",
        subscriptNumbers("C8H8·C4H6·C3H3N"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 24,
        TextureSet.SET_DULL);

    public static final Werkstoff PotassiumHydroxylaminedisulfonate = new Werkstoff(
        new short[] { 117, 114, 104 },
        "PotassiumHydroxylaminedisulfonate",
        subscriptNumbers("K2NHS2O7"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 25,
        TextureSet.SET_SHINY);

    public static final Werkstoff PotassiumSulfate = new Werkstoff(
        new short[] { 208, 152, 46 },
        "PotassiumHydroxylaminedisulfonate",
        subscriptNumbers("K2SO4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 26,
        TextureSet.SET_SHINY);

    public static final Werkstoff PotassiumBisulfite = new Werkstoff(
        new short[] { 114, 111, 101 },
        "PotassiumBisulfite",
        subscriptNumbers("KSHO3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 27,
        TextureSet.SET_SHINY);

    public static final Werkstoff NitrousAcid = new Werkstoff(
        new short[] { 156, 194, 246 },
        "NitrousAcid",
        subscriptNumbers("HNO2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 28,
        TextureSet.SET_FLUID);

    public static final Werkstoff SodiumNitrite = new Werkstoff(
        new short[] { 114, 111, 101 },
        "SodiumNitrite",
        subscriptNumbers("NaNO2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 29,
        TextureSet.SET_SHINY);

    public static final Werkstoff CoAcAbCatalyst = new Werkstoff(
        new short[] { 68, 55, 28 },
        "Co/Ac-AbCatalyst",
        subscriptNumbers(""),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 30,
        TextureSet.SET_SHINY);

    public static final Werkstoff SodiumNitrateSolution = new Werkstoff(
        new short[] { 156, 194, 246 },
        "SodiumNitrateSolution",
        subscriptNumbers("(NaNO3)(H2O)"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 31,
        TextureSet.SET_FLUID);

    public static final Werkstoff Benzylamine = new Werkstoff(
        new short[] { 88, 96, 96 },
        "Benzylamine",
        subscriptNumbers("C7H9N"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 32,
        TextureSet.SET_FLUID);

    public static final Werkstoff Glyoxal = new Werkstoff(
        new short[] { 233, 231, 75 },
        "Glyoxal",
        subscriptNumbers("C2H2O2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 33,
        TextureSet.SET_FLUID);

    public static final Werkstoff Acetonitrile = new Werkstoff(
        new short[] { 80, 86, 86 },
        "Acetonitrile",
        subscriptNumbers("C2H3N"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 34,
        TextureSet.SET_SHINY);

    public static final Werkstoff AmmoniumChloride = new Werkstoff(
        new short[] { 80, 86, 86 },
        "AmmoniumChloride",
        subscriptNumbers("NH4Cl"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 35,
        TextureSet.SET_SHINY);

    public static final Werkstoff Hexamethylenetetramine = new Werkstoff(
        new short[] { 80, 87, 86 },
        "Hexamethylenetetramine",
        subscriptNumbers("C6H12N4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 36,
        TextureSet.SET_SHINY);

    public static final Werkstoff BenzylChloride = new Werkstoff(
        new short[] { 155, 239, 244 },
        "BenzylChloride",
        subscriptNumbers("C7H7Cl"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 37,
        TextureSet.SET_FLUID);

    public static final Werkstoff SuccinicAcid = new Werkstoff(
        new short[] { 80, 87, 86 },
        "SuccinicAcid",
        subscriptNumbers("C4H6O4"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 38,
        TextureSet.SET_SHINY);

    public static final Werkstoff MaleicAnhydride = new Werkstoff(
        new short[] { 155, 239, 244 },
        "MaleicAnhydride",
        subscriptNumbers("C4H2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 39,
        TextureSet.SET_FLUID);

    public static final Werkstoff SuperMutatedLivingSolder = new Werkstoff(
        new short[] { 177, 95, 248 },
        "SuperMutatedLivingSolder",
        subscriptNumbers("??HeOSnCBe??"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 40,
        TextureSet.SET_FLUID);

    public static final Werkstoff Polyimide = new Werkstoff(
        new short[] { 248, 100, 47 },
        "Polyimide",
        subscriptNumbers("C22H12N2O6"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 41,
        TextureSet.SET_FLUID);

    public static final Werkstoff PloyamicAcid = new Werkstoff(
        new short[] { 231, 206, 93 },
        "PloyamicAcid",
        subscriptNumbers("C22H14N2O7"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 42,
        TextureSet.SET_FLUID);

    public static final Werkstoff Oxydianiline = new Werkstoff(
        new short[] { 252, 212, 0 },
        "Oxydianiline",
        subscriptNumbers("C12H12N2O"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 43,
        TextureSet.SET_FLUID);

    public static final Werkstoff PyromelliticDianhydride = new Werkstoff(
        new short[] { 99, 114, 128 },
        "PyromelliticDianhydride",
        subscriptNumbers("C10H2O6"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 44,
        TextureSet.SET_SHINY);

    public static final Werkstoff Durene = new Werkstoff(
        new short[] { 99, 114, 128 },
        "Durene",
        subscriptNumbers("C10H14"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 45,
        TextureSet.SET_SHINY);

    public static final Werkstoff Germaniumtungstennitride = new Werkstoff(
        new short[] { 111, 11, 160 },
        "Germaniumtungstennitride",
        subscriptNumbers("Ge3W3N10"),
        new Werkstoff.Stats().setCentrifuge(true)
            .setBlastFurnace(true)
            .setMeltingPoint(8200)
            .setMeltingVoltage(30720),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 46,
        TextureSet.SET_SHINY);

    public static final Werkstoff Polyetheretherketone = new Werkstoff(
        new short[] { 50, 50, 75 },
        "Polyetheretherketone",
        subscriptNumbers("C20H12O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 47,
        TextureSet.SET_DULL);

    public static final Werkstoff FluidMana = new Werkstoff(
        new short[] { 98, 183, 227 },
        "FluidMana",
        subscriptNumbers("❃"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 48,
        TextureSet.SET_FLUID);

    public static final Werkstoff ExcitedNaquadahFuel = new Werkstoff(
        new short[] { 15, 2, 2 },
        "ExcitedNaquadahFuel",
        subscriptNumbers("❖◈❖"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 49,
        TextureSet.SET_FLUID);

    public static final Werkstoff RareEarthHydroxides = new Werkstoff(
        new short[] { 114, 114, 9 },
        "RareEarthHydroxides",
        subscriptNumbers("?OH"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 50,
        TextureSet.SET_FLUID);

    public static final Werkstoff RareEarthChlorides = new Werkstoff(
        new short[] { 165, 161, 103 },
        "RareEarthChlorides",
        subscriptNumbers("?Cl"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 51,
        TextureSet.SET_FLUID);

    public static final Werkstoff RareEarthOxide = new Werkstoff(
        new short[] { 166, 166, 79 },
        "RareEarthOxide",
        subscriptNumbers("?O"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 52,
        TextureSet.SET_FLUID);

    public static final Werkstoff RareEarthMetal = new Werkstoff(
        new short[] { 148, 148, 148 },
        "RareEarthMetal",
        subscriptNumbers("?"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().addPrefix(OrePrefixes.dust)
            .addPrefix(OrePrefixes.oreBasalt)
            .addPrefix(OrePrefixes.oreBlackgranite)
            .addPrefix(OrePrefixes.oreDense)
            .addPrefix(OrePrefixes.oreEndstone)
            .addPrefix(OrePrefixes.oreEnd)
            .addPrefix(OrePrefixes.oreNetherrack)
            .addPrefix(OrePrefixes.oreNormal)
            .addPrefix(OrePrefixes.orePoor)
            .addPrefix(OrePrefixes.oreRedgranite)
            .addPrefix(OrePrefixes.rawOre)
            .addPrefix(OrePrefixes.oreMarble)
            .addPrefix(OrePrefixes.oreRich)
            .addPrefix(OrePrefixes.oreSmall)
            .addPrefix(OrePrefixes.dustImpure)
            .addPrefix(OrePrefixes.dustPure)
            .addPrefix(OrePrefixes.dustRefined)
            .addPrefix(OrePrefixes.dustTiny)
            .addPrefix(OrePrefixes.ore),
        offsetID_01 + 53,
        TextureSet.SET_FLUID);

    public static final Werkstoff BarnardaCSappy = new Werkstoff(
        new short[] { 49, 49, 100 },
        "BarnardaCSappy",
        subscriptNumbers("?"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 54,
        TextureSet.SET_FLUID);

    public static final Werkstoff NeutralisedRedMud = new Werkstoff(
        new short[] { 147, 40, 3 },
        "NeutralisedRedMud",
        subscriptNumbers("Fe??"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 55,
        TextureSet.SET_FLUID);

    public static final Werkstoff FerricReeChloride = new Werkstoff(
        new short[] { 101, 101, 13 },
        "FerricReeChloride",
        subscriptNumbers("Fe?"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 56,
        TextureSet.SET_FLUID);

    public static final Werkstoff LaNdOxidesSolution = new Werkstoff(
        new short[] { 152, 221, 213 },
        "LaNdOxidesSolution",
        subscriptNumbers("(La2O3)(Pr2O3)(Nd2O3)(Ce2O3)"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 57,
        TextureSet.SET_FLUID);

    public static final Werkstoff SmGdOxidesSolution = new Werkstoff(
        new short[] { 248, 248, 149 },
        "SmGdOxidesSolution",
        subscriptNumbers("(Sc2O3)(Eu2O3)(Gd2O3)(Sm2O3)"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 58,
        TextureSet.SET_FLUID);

    public static final Werkstoff TbHoOxidesSolution = new Werkstoff(
        new short[] { 149, 248, 149 },
        "TbHoOxidesSolution",
        subscriptNumbers("(Y2O3)(Tb2O3)(Dy2O3)(Ho2O3)"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 59,
        TextureSet.SET_FLUID);

    public static final Werkstoff ErLuOxidesSolution = new Werkstoff(
        new short[] { 248, 174, 248 },
        "ErLuOxidesSolution",
        subscriptNumbers("(Er2O3)(Tm2O3)(Yb2O3)(Lu2O3)"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 60,
        TextureSet.SET_FLUID);

    public static final Werkstoff PraseodymiumOxide = new Werkstoff(
        new short[] { 144, 221, 154 },
        "PraseodymiumOxide",
        subscriptNumbers("Pr2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 61,
        TextureSet.SET_FLUID);

    public static final Werkstoff ScandiumOxide = new Werkstoff(
        new short[] { 223, 223, 223 },
        "ScandiumOxide",
        subscriptNumbers("Sc2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 62,
        TextureSet.SET_FLUID);

    public static final Werkstoff GadoliniumOxide = new Werkstoff(
        new short[] { 126, 234, 100 },
        "GadoliniumOxide",
        subscriptNumbers("Gd2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 63,
        TextureSet.SET_FLUID);

    public static final Werkstoff TerbiumOxide = new Werkstoff(
        new short[] { 233, 233, 233 },
        "TerbiumOxide",
        subscriptNumbers("Tb2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 64,
        TextureSet.SET_FLUID);

    public static final Werkstoff DysprosiumOxide = new Werkstoff(
        new short[] { 155, 243, 134 },
        "DysprosiumOxide",
        subscriptNumbers("Dy2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 65,
        TextureSet.SET_FLUID);

    public static final Werkstoff HolmiumOxide = new Werkstoff(
        new short[] { 168, 163, 222 },
        "HolmiumOxide",
        subscriptNumbers("Ho2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 66,
        TextureSet.SET_FLUID);

    public static final Werkstoff ErbiumOxide = new Werkstoff(
        new short[] { 201, 184, 136 },
        "ErbiumOxide",
        subscriptNumbers("Er2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 67,
        TextureSet.SET_FLUID);

    public static final Werkstoff ThuliumOxide = new Werkstoff(
        new short[] { 141, 153, 213 },
        "ThuliumOxide",
        subscriptNumbers("Tm2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 68,
        TextureSet.SET_FLUID);

    public static final Werkstoff YtterbiumOxide = new Werkstoff(
        new short[] { 110, 217, 135 },
        "YtterbiumOxide",
        subscriptNumbers("Yb2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 69,
        TextureSet.SET_FLUID);

    public static final Werkstoff LutetiumOxide = new Werkstoff(
        new short[] { 209, 123, 217 },
        "LutetiumOxide",
        subscriptNumbers("Lu2O3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 70,
        TextureSet.SET_FLUID);

    public static final Werkstoff MolybdenumDisilicide = new Werkstoff(
        new short[] { 82, 74, 125 },
        "MolybdenumDisilicide",
        subscriptNumbers("MoSi2"),
        new Werkstoff.Stats().setCentrifuge(true)
            .setBlastFurnace(true)
            .setMeltingPoint(2301)
            .setMeltingVoltage(1920),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()
            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes()
            .addMixerRecipes((short) 2),
        offsetID_01 + 71,
        TextureSet.SET_SHINY,
        Pair.of(Materials.Molybdenum, 1),
        Pair.of(Materials.Silicon, 2));

    public static final Werkstoff HSLASteel = new Werkstoff(
        new short[] { 96, 98, 101 },
        "HSLASteel",
        subscriptNumbers("(Fe2Ni)2VTiMo"),
        new Werkstoff.Stats().setCentrifuge(true)
            .setBlastFurnace(true)
            .setMeltingPoint(1711)
            .setMeltingVoltage(480)
            .setToxic(true),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes()
            .addMixerRecipes((short) 4),
        offsetID_01 + 72,
        TextureSet.SET_SHINY,
        Pair.of(Materials.Invar, 2),
        Pair.of(Materials.Vanadium, 1),
        Pair.of(Materials.Titanium, 1),
        Pair.of(Materials.Molybdenum, 1));

    public static final Werkstoff Actinium = new Werkstoff(
        new short[] { 135, 151, 186 },
        "Actinium",
        subscriptNumbers("Ac"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 73,
        TextureSet.SET_SHINY);

    public static final Werkstoff Rutherfordium = new Werkstoff(
        new short[] { 84, 76, 68 },
        "Rutherfordium",
        subscriptNumbers("Rf"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 74,
        TextureSet.SET_SHINY);

    public static final Werkstoff Dubnium = new Werkstoff(
        new short[] { 135, 151, 186 },
        "Dubnium",
        subscriptNumbers("Db"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 75,
        TextureSet.SET_SHINY);

    public static final Werkstoff Seaborgium = new Werkstoff(
        new short[] { 20, 154, 200 },
        "Seaborgium",
        subscriptNumbers("Sg"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 76,
        TextureSet.SET_SHINY);

    public static final Werkstoff Technetium = new Werkstoff(
        new short[] { 91, 38, 176 },
        "Technetium",
        subscriptNumbers("Tc"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 77,
        TextureSet.SET_SHINY);

    public static final Werkstoff Bohrium = new Werkstoff(
        new short[] { 174, 81, 200 },
        "Bohrium",
        subscriptNumbers("Bh"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 78,
        TextureSet.SET_SHINY);

    public static final Werkstoff Hassium = new Werkstoff(
        new short[] { 90, 106, 105 },
        "Hassium",
        subscriptNumbers("Hs"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 79,
        TextureSet.SET_SHINY);

    public static final Werkstoff Meitnerium = new Werkstoff(
        new short[] { 62, 47, 102 },
        "Meitnerium",
        subscriptNumbers("Mt"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 80,
        TextureSet.SET_SHINY);

    public static final Werkstoff Darmstadtium = new Werkstoff(
        new short[] { 68, 100, 77 },
        "Darmstadtium",
        subscriptNumbers("Ds"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 81,
        TextureSet.SET_SHINY);

    public static final Werkstoff Roentgenium = new Werkstoff(
        new short[] { 44, 110, 56 },
        "Roentgenium",
        subscriptNumbers("Rg"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 82,
        TextureSet.SET_SHINY);

    public static final Werkstoff Copernicium = new Werkstoff(
        new short[] { 67, 72, 73 },
        "Copernicium",
        subscriptNumbers("Cn"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 83,
        TextureSet.SET_SHINY);

    public static final Werkstoff Moscovium = new Werkstoff(
        new short[] { 33, 21, 50 },
        "Moscovium",
        subscriptNumbers("Mc"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 84,
        TextureSet.SET_SHINY);

    public static final Werkstoff Livermorium = new Werkstoff(
        new short[] { 115, 115, 115 },
        "Livermorium",
        subscriptNumbers("Lv"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 85,
        TextureSet.SET_SHINY);

    public static final Werkstoff Astatine = new Werkstoff(
        new short[] { 64, 23, 53 },
        "Astatine",
        subscriptNumbers("At"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 86,
        TextureSet.SET_SHINY);

    public static final Werkstoff Tennessine = new Werkstoff(
        new short[] { 92, 71, 150 },
        "Tennessine",
        subscriptNumbers("Ts"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 87,
        TextureSet.SET_SHINY);

    public static final Werkstoff Francium = new Werkstoff(
        new short[] { 133, 133, 133 },
        "Francium",
        subscriptNumbers("Fr"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 88,
        TextureSet.SET_SHINY);

    public static final Werkstoff Berkelium = new Werkstoff(
        new short[] { 78, 70, 106 },
        "Berkelium",
        subscriptNumbers("Bk"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 89,
        TextureSet.SET_SHINY);

    public static final Werkstoff Einsteinium = new Werkstoff(
        new short[] { 161, 124, 0 },
        "Einsteinium",
        subscriptNumbers("Es"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 90,
        TextureSet.SET_SHINY);

    public static final Werkstoff Mendelevium = new Werkstoff(
        new short[] { 23, 57, 159 },
        "Mendelevium",
        subscriptNumbers("Md"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 91,
        TextureSet.SET_SHINY);

    public static final Werkstoff Nobelium = new Werkstoff(
        new short[] { 48, 56, 69 },
        "Nobelium",
        subscriptNumbers("No"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 92,
        TextureSet.SET_SHINY);

    public static final Werkstoff Lawrencium = new Werkstoff(
        new short[] { 73, 92, 92 },
        "Lawrencium",
        subscriptNumbers("Lr"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 93,
        TextureSet.SET_SHINY);

    public static final Werkstoff Nihonium = new Werkstoff(
        new short[] { 39, 45, 68 },
        "Nihonium",
        subscriptNumbers("Nh"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 94,
        TextureSet.SET_SHINY);

    public static final Werkstoff ZnFeAlCl = new Werkstoff(
        new short[] { 150, 59, 123 },
        "ZnFeAlCl",
        subscriptNumbers("ZnFeAlCl"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 95,
        TextureSet.SET_FLUID);

    public static final Werkstoff BenzenediazoniumTetrafluoroborate = new Werkstoff(
        new short[] { 110, 143, 110 },
        "BenzenediazoniumTetrafluoroborate",
        subscriptNumbers("C6H5BF4N2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 96,
        TextureSet.SET_FLUID);

    public static final Werkstoff FluoroBenzene = new Werkstoff(
        new short[] { 110, 143, 110 },
        "FluoroBenzene",
        subscriptNumbers("C6H5F"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 97,
        TextureSet.SET_FLUID);

    public static final Werkstoff AntimonyTrifluoride = new Werkstoff(
        new short[] { 199, 194, 180 },
        "AntimonyTrifluoride",
        subscriptNumbers("SbF3"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 98,
        TextureSet.SET_FLUID);

    public static final Werkstoff Fluorotoluene = new Werkstoff(
        new short[] { 167, 161, 102 },
        "Fluorotoluene",
        subscriptNumbers("C7H7F"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 99,
        TextureSet.SET_FLUID);

    public static final Werkstoff Resorcinol = new Werkstoff(
        new short[] { 114, 38, 21 },
        "Resorcinol",
        subscriptNumbers("C6H6O2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 100,
        TextureSet.SET_FLUID);

    public static final Werkstoff Hydroquinone = new Werkstoff(
        new short[] { 114, 38, 21 },
        "Hydroquinone",
        subscriptNumbers("C6H6O2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 101,
        TextureSet.SET_FLUID);

    public static final Werkstoff Difluorobenzophenone = new Werkstoff(
        new short[] { 150, 59, 126 },
        "Difluorobenzophenone",
        subscriptNumbers("F2C13H8O"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 102,
        TextureSet.SET_FLUID);

    public static final Werkstoff FluorineCrackedNaquadah = new Werkstoff(
        new short[] { 57, 68, 72 },
        "FluorineCrackedNaquadah",
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 103,
        TextureSet.SET_FLUID);

    public static final Werkstoff EnrichedNaquadahWaste = new Werkstoff(
        new short[] { 67, 100, 71 },
        "EnrichedNaquadahWaste",
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 104,
        TextureSet.SET_FLUID);

    public static final Werkstoff RadonCrackedEnrichedNaquadah = new Werkstoff(
        new short[] { 67, 100, 71 },
        "RadonCrackedEnrichedNaquadah",
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 105,
        TextureSet.SET_FLUID);

    public static final Werkstoff NaquadriaWaste = new Werkstoff(
        new short[] { 61, 84, 69 },
        "NaquadriaWaste",
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 106,
        TextureSet.SET_FLUID);

    public static final Werkstoff SmallBaka = new Werkstoff(
        new short[] { 65, 105, 225 },
        "SmallBaka",
        subscriptNumbers("C6H5N3O2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 107,
        TextureSet.SET_FLUID);

    public static final Werkstoff LargeBaka = new Werkstoff(
        new short[] { 106, 90, 205 },
        "LargeBaka",
        subscriptNumbers("C6H5-NDs2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 108,
        TextureSet.SET_FLUID);

    public static final Werkstoff CompressedSteam = new Werkstoff(
        new short[] { 211, 211, 211 },
        "CompressedSteam",
        subscriptNumbers("H2O"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 109,
        TextureSet.SET_SHINY);

    public static final Werkstoff Stronze = new Werkstoff(
        new short[] { 89, 61, 45 },
        "Stronze",
        subscriptNumbers("(SnCu3)(Fe50C)2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 110,
        TextureSet.SET_SHINY);

    public static final Werkstoff Breel = new Werkstoff(
        new short[] { 69, 60, 55 },
        "Breel",
        subscriptNumbers("(Fe50C)(SnCu3)2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 111,
        TextureSet.SET_SHINY);

    public static final Werkstoff PitchblendeSlag = new Werkstoff(
        new short[] { 68, 55, 28 },
        "PitchblendeSlag",
        subscriptNumbers("??ThPbCaSO4??"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 112,
        TextureSet.SET_SHINY);

    public static final Werkstoff UraniumSlag = new Werkstoff(
        new short[] { 68, 100, 77 },
        "UraniumSlag",
        subscriptNumbers("??ThPbBa??"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 113,
        TextureSet.SET_SHINY);

    public static final Werkstoff UraniumChlorideSlag = new Werkstoff(
        new short[] { 234, 243, 245 },
        "UraniumChlorideSlag",
        subscriptNumbers("??ThPbBaCl4??"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 114,
        CustomTextureSet.TextureSets.NUCLEAR.get());

    public static final Werkstoff RadiumChloride = new Werkstoff(
        new short[] { 223, 223, 223 },
        "RadiumChloride",
        subscriptNumbers("RaCl2"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MATERIAL,
        new Werkstoff.GenerationFeatures().disable()
            .onlyDust(),
        offsetID_01 + 115,
        CustomTextureSet.TextureSets.NUCLEAR.get());

    public static final Werkstoff GravelSluice = new Werkstoff(
        new short[] { 91, 91, 91 },
        "GravelSluice",
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 116,
        TextureSet.SET_FLUID);

    public static final Werkstoff SandSluice = new Werkstoff(
        new short[] { 248, 250, 130 },
        "SandSluice",
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 117,
        TextureSet.SET_FLUID);

    public static final Werkstoff ObsidianSluice = new Werkstoff(
        new short[] { 53, 40, 61 },
        "ObsidianSluice",
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 118,
        TextureSet.SET_FLUID);

    public static final Werkstoff GemSluice = new Werkstoff(
        new short[] { 120, 153, 113 },
        "GemSluice",
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 119,
        TextureSet.SET_FLUID);

    public static final Werkstoff EnderAir = new Werkstoff(
        new short[] { 57, 66, 89 },
        "EnderAir",
        new Werkstoff.Stats().setGas(true),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 120,
        TextureSet.SET_FLUID);

    public static final Werkstoff FluidEnderAir = new Werkstoff(
        new short[] { 61, 71, 98 },
        "FluidEnderAir",
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 121,
        TextureSet.SET_FLUID);

    // Special

    public static final Werkstoff Periodicium = new Werkstoff(
        new short[] { 60, 74, 243 },
        "Periodicium",
        subscriptNumbers(
            "((H37C67N32O35F61P52S48C64Se53Br56I52)" + "(He32Ne58Ar48Kr35Xe62Rn54)"
                + "(B56Si38Ge30As64Sb68Te31At38)"
                + "(Al35Ga56In66Sn42Tl43Pb46Bi64Po68)"
                + "((Ti49V59Cr60Mn48Fe61Co50Ni66Cu32Zn31)"
                + "(Zr65Nb46Mo36Tc60Ru57Rh50Pd38Ag30Cd47)"
                + "(Hf45Ta37W68Re66Os58Ir43Pt33Au55Hg48))"
                + "(Be44Mg51Ca38Sr35Ba68Ra52)"
                + "(Sc64Y47(La30Ce42Pr63Nd68Pm52Sm56Eu38Gd64))"
                + "(Tb62Dy46Ho35Er66Tm48Yb36Lu55)"
                + "(Li67Na49K68Rb57Cs50Fr52)"
                + "((Bk61Cf43Es38Fm48Md58No30Lr56)"
                + "(Rf45Db53Sg51Bh65Hs31Mt46Ds57Rg54Cn48Nh64Fl44Mc33Lv52Ts41Og58))"
                + "((Ke41Rp56Nq35Nq+54Nq*38Su60Ad66(SpPu)67(SpNt)39D55Oh50De63Qt35D*30(IcMa)65If*38Nt42Hy66En64)"
                + "(Gs39TsЖ66TtЖ67〄62Fs⚶45Hy⚶53✢64En⦼53M⎋65⸎46✦◆✦48✧◇✧52Og*52Sh⏧68Tn57)"
                + "((⌘☯𓍰𓍱𓍲𓍳𓍴𓍵𓍶𓍷𓍸☯⌘)66(⚷⚙⚷Ni4Ti6)40Fc⚙37҈30҉33«»67Rt*51۞47Rc62Si*68)"
                + "(Fs61Ef30Ut69Tr66Ms40If53He*43Ai32Or55Vy36⬟⯂⬢⬣⯃⯄32Tt33Cc56Vu58Ao52))???144)"),
        new Werkstoff.Stats().setToxic(true)
            .setMass(2147483647),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 200,
        TextureSet.SET_SHINY);

    public static final Werkstoff Stargate = new Werkstoff(
        new short[] { 148, 182, 189 },
        "Stargate",
        subscriptNumbers("⨷"),
        new Werkstoff.Stats().setToxic(true),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addSimpleMetalWorkingItems()

            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 201,
        TextureSet.SET_SHINY);

    public static final Werkstoff.GenerationFeatures gf = new Werkstoff.GenerationFeatures();

    @Override
    public void run() {
        for (var prefix : OrePrefixes.values()) {
            gf.addPrefix(prefix);
        }
        gf.removePrefix(OrePrefixes.ore);
    }
}
