package com.science.gtnl.common.recipe.thaumcraft;

import static com.reavaritia.common.ItemLoader.ChronarchsClock;
import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import java.util.Objects;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.item.ItemUtils;

import appeng.api.AEApi;
import bartworks.system.material.WerkstoffLoader;
import fox.spiteful.avaritia.items.LudicrousItems;
import goodgenerator.util.ItemRefer;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TCAspects;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import ic2.core.Ic2Items;
import tectech.thing.CustomItemList;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

public class TCResearches {

    public static final ResourceLocation BACKGROUND = Mods.ThaumicInsurgence.isModLoaded()
        ? new ResourceLocation(Mods.ThaumicInsurgence.ID, "textures/gui/eldritch_bg.png")
        : new ResourceLocation(Mods.Thaumcraft.ID, "textures/gui/gui_researchback.png");

    public static void register() {
        loadResearchTab();
        loadResearchesAndRecipes();
    }

    public static void loadResearchTab() {
        ResearchCategories.registerCategory(
            "gtnl",
            new ResourceLocation(RESOURCE_ROOT_ID, "textures/items/TestItem.png"),
            BACKGROUND);
    }

    public static ShapedArcaneRecipe getShapedArcaneRecipe(ItemStack res) {
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (!(r instanceof ShapedArcaneRecipe recipe)) continue;

            ItemStack output = recipe.getRecipeOutput();
            if (output != null && output.isItemEqual(res)) {
                return recipe;
            }
        }
        return null;
    }

    public static void loadResearchesAndRecipes() {
        new ResearchItem("gtnl.welcome", "gtnl", new AspectList(), 0, 0, 0, GTNLItemList.TestItem.get(1))
            .setAutoUnlock()
            .registerResearchItem()
            .setPages(new ResearchPage("tc.research_text.gtnl.welcome.1"))
            .setSpecial();

        new ResearchItem(
            "gtnl.timeStopPocketWatch",
            "gtnl",
            new AspectList().merge(Mods.MagicBees.isModLoaded() ? Aspect.getAspect("tempus") : Aspect.MAGIC, 2)
                .merge(Aspect.ORDER, 1)
                .merge(Aspect.MAGIC, 1)
                .merge(Aspect.TRAVEL, 1)
                .merge(Aspect.ENERGY, 1)
                .merge(Aspect.VOID, 1),
            -1,
            -2,
            5,
            GTNLItemList.TimeStopPocketWatch.get(1)).setPages(
                new ResearchPage("tc.research_text.gtnl.timeStopPocketWatch.0"),
                new ResearchPage(
                    ThaumcraftApi.addInfusionCraftingRecipe(
                        "gtnl.timeStopPocketWatch",
                        GTNLItemList.TimeStopPocketWatch.get(1),
                        500,
                        new AspectList().merge(Aspect.getAspect("terminus"), Integer.MAX_VALUE)
                            .merge(Aspect.MAGIC, Integer.MAX_VALUE)
                            .merge(
                                Mods.ThaumicBoots.isModLoaded() ? Aspect.getAspect("tabernus") : Aspect.VOID,
                                Integer.MAX_VALUE)
                            .merge(Aspect.getAspect("custom3"), Integer.MAX_VALUE)
                            .merge(Aspect.EXCHANGE, Integer.MAX_VALUE)
                            .merge(
                                Mods.ThaumicBoots.isModLoaded() ? Aspect.getAspect("caelum") : Aspect.ORDER,
                                Integer.MAX_VALUE)
                            .merge(
                                Mods.MagicBees.isModLoaded() ? Aspect.getAspect("tempus") : Aspect.DEATH,
                                Integer.MAX_VALUE)
                            .merge(Aspect.ELDRITCH, Integer.MAX_VALUE)
                            .merge(Aspect.ENERGY, Integer.MAX_VALUE),
                        new ItemStack(ChronarchsClock),
                        new ItemStack[] {
                            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.Universium, 1),
                            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, MaterialsUEVplus.SpaceTime, 1),
                            GTOreDictUnificator.get(OrePrefixes.gem, MaterialsUEVplus.GravitonShard, 1),
                            GTOreDictUnificator.get(OrePrefixes.nanite, MaterialsUEVplus.MagMatter, 1),
                            ItemList.Timepiece.get(1), ItemList.Transdimensional_Alignment_Matrix.get(1),
                            ItemList.EnergisedTesseract.get(1), ItemList.Field_Generator_UXV.get(1),
                            ItemList.GigaChad.get(1),
                            ItemUtils
                                .getItemStack(ItemList.ZPM6.get(1), "{GT.ItemCharge:" + Long.MAX_VALUE + "}", null),
                            CustomItemList.astralArrayFabricator.get(1),
                            CustomItemList.Machine_Multi_EyeOfHarmony.get(1),
                            CustomItemList.Machine_Multi_ForgeOfGods.get(1),
                            ItemList.SpaceElevatorModuleAssemblerT3.get(1), ItemList.SpaceElevatorModulePumpT3.get(1),
                            new ItemStack(Mods.Avaritia.isModLoaded() ? LudicrousItems.bigPearl : Items.ender_pearl, 1),
                            ItemUtils.getItemStack(
                                Mods.EnderIO.ID,
                                "blockCapBank",
                                1,
                                0,
                                "{storedEnergyRF:2500000,type:\"CREATIVE\"}",
                                null),
                            Mods.TaintedMagic.isModLoaded()
                                ? GTModHandler.getModItem(Mods.TaintedMagic.ID, "ItemFocusTime", 1)
                                : GTModHandler.getModItem(Mods.Thaumcraft.ID, "FocusPrimal", 1),
                            GregtechItemList.SynchrotronCapableCatalyst.get(1),
                            GTNLItemList.UMVParallelControllerCore.get(1),
                            GTModHandler.getModItem(Mods.AE2FluidCraft.ID, "fluid_storage.Universe", 1),
                            AEApi.instance()
                                .definitions()
                                .items()
                                .cellUniverse()
                                .maybeStack(1)
                                .orNull(),
                            Mods.SGCraft.isModLoaded() ? GTModHandler.getModItem(Mods.SGCraft.ID, "ic2Capacitor", 1)
                                : new ItemStack(Blocks.dirt),
                            Mods.Computronics.isModLoaded()
                                ? GTModHandler.getModItem(Mods.Computronics.ID, "computronics.ocSpecialParts", 1)
                                : new ItemStack(Items.feather) })))
                .setParents("gtnl.welcome")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.largeEssentiaGenerator",
            "gtnl",
            new AspectList().add(Aspect.MECHANISM, 10)
                .add(Aspect.MAGIC, 10)
                .add(Aspect.ENERGY, 10)
                .add(Aspect.EXCHANGE, 10),
            -4,
            3,
            3,
            GTNLItemList.LargeEssentiaGenerator.get(1))
                .setPages(
                    new ResearchPage("tc.research_text.gtnl.largeEssentiaGenerator.0"),
                    new ResearchPage(
                        ThaumcraftApi.addInfusionCraftingRecipe(
                            "gtnl.largeEssentiaGenerator",
                            GTNLItemList.LargeEssentiaGenerator.get(1),
                            6,
                            new AspectList().add(Aspect.ENERGY, 64)
                                .add(Aspect.MAGIC, 32)
                                .add(Aspect.EXCHANGE, 32),
                            ItemList.Hull_HV.get(1),
                            new ItemStack[] { GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Advanced, 1L),
                                new ItemStack(ConfigBlocks.blockJar, 1),
                                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Thaumium, 1L),
                                GTOreDictUnificator.get(OrePrefixes.gem, Materials.Emerald, 1L),
                                new ItemStack(ConfigBlocks.blockWoodenDevice, 1),
                                GTOreDictUnificator.get(OrePrefixes.spring, Materials.Manyullyn, 1L),
                                Ic2Items.teslaCoil, ItemList.Sensor_MV.get(1) })),
                    new ResearchPage(
                        ThaumcraftApi.addInfusionCraftingRecipe(
                            "gtnl.largeEssentiaGenerator",
                            GTNLItemList.EssentiaHatch.get(1),
                            6,
                            new AspectList().add(Aspect.WATER, 128)
                                .add(Aspect.VOID, 64)
                                .add(Aspect.EXCHANGE, 32)
                                .add(Aspect.MIND, 32),
                            ItemList.Hatch_Input_HV.get(1),
                            new ItemStack[] { new ItemStack(ConfigBlocks.blockJar, 1), ItemRefer.Magic_Casing.get(1),
                                new ItemStack(ConfigBlocks.blockTube, 1), ItemList.Electric_Pump_MV.get(1L) })),
                    new ResearchPage(ThaumcraftApi.getInfusionRecipe(ItemRefer.Essentia_Cell_T1.get(1))),
                    new ResearchPage(Objects.requireNonNull(getShapedArcaneRecipe(ItemRefer.Magic_Casing.get(1)))),
                    new ResearchPage("tc.research_text.gtnl.largeEssentiaGenerator.1"),
                    new ResearchPage("tc.research_text.gtnl.largeEssentiaGenerator.2"),
                    new ResearchPage("tc.research_text.gtnl.largeEssentiaGenerator.3"))
                .setParents("gtnl.welcome")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.essentiaUpgradeEmpty",
            "gtnl",
            new AspectList().add(Aspect.AURA, 10)
                .add(Aspect.EXCHANGE, 10)
                .add(Aspect.TOOL, 10)
                .add(Aspect.ENERGY, 10),
            -4,
            4,
            2,
            GTNLItemList.EssentiaUpgradeEmpty.get(1))
                .setPages(
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeEmpty.0"),
                    new ResearchPage(
                        ThaumcraftApi.addArcaneCraftingRecipe(
                            "gtnl.essentiaUpgradeEmpty",
                            GTNLItemList.EssentiaUpgradeEmpty.get(1),
                            new AspectList().add(Aspect.AIR, 80)
                                .add(Aspect.ENTROPY, 50)
                                .add(Aspect.ORDER, 50)
                                .add(Aspect.WATER, 80),
                            "AMB",
                            "CZD",
                            "EIF",
                            'A',
                            GTOreDictUnificator.get(OrePrefixes.screw, Materials.InfusedAir, 1),
                            'B',
                            GTOreDictUnificator.get(OrePrefixes.screw, Materials.InfusedEarth, 1),
                            'C',
                            GTOreDictUnificator.get(OrePrefixes.screw, Materials.InfusedFire, 1),
                            'D',
                            GTOreDictUnificator.get(OrePrefixes.screw, Materials.InfusedWater, 1),
                            'E',
                            GTOreDictUnificator.get(OrePrefixes.screw, Materials.InfusedOrder, 1),
                            'F',
                            GTOreDictUnificator.get(OrePrefixes.screw, Materials.InfusedEntropy, 1),
                            'M',
                            new ItemStack(ConfigItems.itemResource, 1, 10),
                            'Z',
                            com.dreammaster.gthandler.CustomItemList.ArcaneSlate.get(1),
                            'I',
                            GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.PulsatingIron, 1))))
                .setParents("gtnl.largeEssentiaGenerator")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.essentiaUpgradeAir",
            "gtnl",
            new AspectList().add(Aspect.AIR, 10)
                .add(Aspect.EXCHANGE, 10)
                .add(Aspect.TOOL, 10)
                .add(Aspect.ENERGY, 10),
            -4,
            5,
            1,
            GTNLItemList.EssentiaUpgradeAir.get(1))
                .setPages(
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeAir.0"),
                    new ResearchPage(
                        ThaumcraftApi.addInfusionCraftingRecipe(
                            "gtnl.essentiaUpgradeAir",
                            GTNLItemList.EssentiaUpgradeAir.get(1),
                            5,
                            new AspectList().add(Aspect.AIR, 128)
                                .add(Aspect.FLIGHT, 128)
                                .add(Aspect.MOTION, 128)
                                .add(Aspect.AURA, 128),
                            GTNLItemList.EssentiaUpgradeEmpty.get(1),
                            new ItemStack[] { GTOreDictUnificator.get(OrePrefixes.cell, Materials.LiquidAir, 1),
                                GTOreDictUnificator.get(OrePrefixes.cell, Materials.Hydrogen, 1),
                                GTOreDictUnificator.get(OrePrefixes.cell, Materials.Helium, 1),
                                GTOreDictUnificator.get(OrePrefixes.cell, Materials.Argon, 1),
                                WerkstoffLoader.Neon.get(OrePrefixes.cell, 1),
                                WerkstoffLoader.Krypton.get(OrePrefixes.cell, 1) })),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeAir.1"))
                .setParents("gtnl.essentiaUpgradeEmpty")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.essentiaUpgradeThermal",
            "gtnl",
            new AspectList().add(Aspect.FIRE, 10)
                .add(Aspect.EXCHANGE, 10)
                .add(Aspect.TOOL, 10)
                .add(Aspect.ENERGY, 10),
            -5,
            5,
            1,
            GTNLItemList.EssentiaUpgradeThermal.get(1))
                .setPages(
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeThermal.0"),
                    new ResearchPage(
                        ThaumcraftApi.addInfusionCraftingRecipe(
                            "gtnl.essentiaUpgradeThermal",
                            GTNLItemList.EssentiaUpgradeThermal.get(1),
                            5,
                            new AspectList().add(Aspect.FIRE, 1024),
                            GTNLItemList.EssentiaUpgradeEmpty.get(1),
                            new ItemStack[] { GTOreDictUnificator.get(OrePrefixes.lens, Materials.Firestone, 1),
                                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Netherrack, 1),
                                Ic2Items.reactorPlatingHeat, ItemList.Casing_Coil_Nichrome.get(1),
                                new ItemStack(ConfigItems.itemResource, 1, 1),
                                new ItemStack(ConfigItems.itemResource, 1, 0) })),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeThermal.1"),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeThermal.2"))
                .setParents("gtnl.essentiaUpgradeEmpty")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.essentiaUpgradeUnstable",
            "gtnl",
            new AspectList().add(Aspect.ENTROPY, 10)
                .add(Aspect.EXCHANGE, 10)
                .add(Aspect.TOOL, 10)
                .add(Aspect.ENERGY, 10),
            -6,
            5,
            1,
            GTNLItemList.EssentiaUpgradeUnstable.get(1))
                .setPages(
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeUnstable.0"),
                    new ResearchPage(
                        ThaumcraftApi.addInfusionCraftingRecipe(
                            "gtnl.essentiaUpgradeUnstable",
                            GTNLItemList.EssentiaUpgradeUnstable.get(1),
                            6,
                            new AspectList().add(Aspect.ENTROPY, 128)
                                .add(Aspect.VOID, 128)
                                .add(Aspect.POISON, 128)
                                .add(Aspect.WEAPON, 128),
                            GTNLItemList.EssentiaUpgradeEmpty.get(1),
                            new ItemStack[] { GTOreDictUnificator.get(OrePrefixes.cell, Materials.GasolinePremium, 1),
                                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Unstable, 1),
                                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Void, 1),
                                GTOreDictUnificator.get(OrePrefixes.gem, Materials.InfusedEntropy, 1),
                                Ic2Items.industrialTnt, ItemList.Field_Generator_MV.get(1) })),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeUnstable.1"),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeUnstable.2"))
                .setParents("gtnl.essentiaUpgradeEmpty")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.essentiaUpgradeVictus",
            "gtnl",
            new AspectList().add(Aspect.LIFE, 10)
                .add(Aspect.EXCHANGE, 10)
                .add(Aspect.TOOL, 10)
                .add(Aspect.ENERGY, 10),
            -7,
            5,
            1,
            GTNLItemList.EssentiaUpgradeVictus.get(1))
                .setPages(
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeVictus.0"),
                    new ResearchPage(
                        ThaumcraftApi.addInfusionCraftingRecipe(
                            "gtnl.essentiaUpgradeVictus",
                            GTNLItemList.EssentiaUpgradeVictus.get(1),
                            5,
                            new AspectList().add(Aspect.LIFE, 128)
                                .add(Aspect.SOUL, 128)
                                .add(Aspect.HEAL, 128)
                                .add(Aspect.FLESH, 128)
                                .add(Aspect.MAN, 128),
                            GTNLItemList.EssentiaUpgradeEmpty.get(1),
                            new ItemStack[] { GTModHandler.getModItem(Mods.NewHorizonsCoreMod.ID, "GTNHBioItems", 1, 2),
                                ItemList.Food_Dough_Sugar.get(1),
                                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Calcium, 1),
                                new ItemStack(Items.rotten_flesh, 1), new ItemStack(ConfigItems.itemResource, 1, 4),
                                new ItemStack(ConfigBlocks.blockMetalDevice, 1, 8) })),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeVictus.1"),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeVictus.2"))
                .setParents("gtnl.essentiaUpgradeEmpty")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.essentiaUpgradeTainted",
            "gtnl",
            new AspectList().add(Aspect.DEATH, 10)
                .add(Aspect.EXCHANGE, 10)
                .add(Aspect.TOOL, 10)
                .add(Aspect.ENERGY, 10),
            -8,
            5,
            1,
            GTNLItemList.EssentiaUpgradeTainted.get(1))
                .setPages(
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeTainted.0"),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeTainted.1"),
                    new ResearchPage(
                        ThaumcraftApi.addInfusionCraftingRecipe(
                            "gtnl.essentiaUpgradeTainted",
                            GTNLItemList.EssentiaUpgradeTainted.get(1),
                            7,
                            new AspectList().add(Aspect.DEATH, 128)
                                .add(Aspect.ELDRITCH, 128)
                                .add(Aspect.UNDEAD, 128)
                                .add(Aspect.TAINT, 128),
                            GTNLItemList.EssentiaUpgradeEmpty.get(1),
                            new ItemStack[] { new ItemStack(ConfigBlocks.blockTaintFibres, 1, 0),
                                new ItemStack(ConfigBlocks.blockTaintFibres, 1, 2),
                                new ItemStack(ConfigItems.itemResource, 1, 11),
                                GTOreDictUnificator.get(OrePrefixes.spring, Materials.NaquadahEnriched, 1),
                                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.EndSteel, 1),
                                new ItemStack(Blocks.beacon, 1) })),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeTainted.2"),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeTainted.3"))
                .setParents("gtnl.essentiaUpgradeEmpty")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.essentiaUpgradeMechanics",
            "gtnl",
            new AspectList().add(Aspect.MECHANISM, 10)
                .add(Aspect.EXCHANGE, 10)
                .add(Aspect.TOOL, 10)
                .add(Aspect.ENERGY, 10),
            -9,
            5,
            1,
            GTNLItemList.EssentiaUpgradeMechanics.get(1))
                .setPages(
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeMechanics.0"),
                    new ResearchPage(
                        ThaumcraftApi.addInfusionCraftingRecipe(
                            "gtnl.essentiaUpgradeMechanics",
                            GTNLItemList.EssentiaUpgradeMechanics.get(1),
                            5,
                            new AspectList().add(Aspect.TRAVEL, 128)
                                .add(Aspect.GREED, 128)
                                .add(Aspect.MECHANISM, 128),
                            GTNLItemList.EssentiaUpgradeEmpty.get(1),
                            new ItemStack[] { new ItemStack(ConfigBlocks.blockTube, 1, 4),
                                new ItemStack(ConfigBlocks.blockTube, 1, 2),
                                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.VividAlloy, 1),
                                GTOreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Polybenzimidazole, 1),
                                ItemList.Electric_Motor_IV.get(1), ItemList.Electric_Pump_IV.get(1) })),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeMechanics.1"),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeMechanics.2"))
                .setParents("gtnl.essentiaUpgradeEmpty")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.essentiaUpgradeSpirit",
            "gtnl",
            new AspectList().add(Aspect.MIND, 10)
                .add(Aspect.EXCHANGE, 10)
                .add(Aspect.TOOL, 10)
                .add(Aspect.ENERGY, 10),
            -10,
            5,
            1,
            GTNLItemList.EssentiaUpgradeSpirit.get(1))
                .setPages(
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeSpirit.0"),
                    new ResearchPage(
                        ThaumcraftApi.addInfusionCraftingRecipe(
                            "gtnl.essentiaUpgradeSpirit",
                            GTNLItemList.EssentiaUpgradeSpirit.get(1),
                            5,
                            new AspectList().add(Aspect.MIND, 128)
                                .add(Aspect.SENSES, 128)
                                .add(Aspect.GREED, 128)
                                .add((Aspect) TCAspects.STRONTIO.mAspect, 128)
                                .add((Aspect) TCAspects.NEBRISUM.mAspect, 128),
                            GTNLItemList.EssentiaUpgradeEmpty.get(1),
                            new ItemStack[] { new ItemStack(ConfigBlocks.blockJar, 1, 1),
                                GTOreDictUnificator.get(OrePrefixes.food, Materials.Cheese, 1),
                                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Shadow, 1),
                                GTOreDictUnificator.get(OrePrefixes.spring, Materials.FierySteel, 1),
                                GTOreDictUnificator.get(OrePrefixes.cell, Materials.Milk, 1),
                                ItemList.Machine_EV_Scanner.get(1) })),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeSpirit.1"),
                    new ResearchPage("tc.research_text.gtnl.essentiaUpgradeSpirit.2"))
                .setParents("gtnl.essentiaUpgradeEmpty")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.essentiaUpgradeRadiation",
            "gtnl",
            new AspectList().add(Aspect.ENERGY, 10)
                .add(Aspect.EXCHANGE, 10)
                .add(Aspect.TOOL, 10)
                .add(Aspect.ENERGY, 10),
            -11,
            5,
            1,
            GTNLItemList.EssentiaUpgradeRadiation.get(1)).setPages(
                new ResearchPage("tc.research_text.gtnl.essentiaUpgradeRadiation.0"),
                new ResearchPage(
                    ThaumcraftApi.addInfusionCraftingRecipe(
                        "gtnl.essentiaUpgradeRadiation",
                        GTNLItemList.EssentiaUpgradeRadiation.get(1),
                        8,
                        new AspectList().add((Aspect) TCAspects.RADIO.mAspect, 1024),
                        GTNLItemList.EssentiaUpgradeEmpty.get(1),
                        new ItemStack[] { ItemRefer.High_Density_Plutonium.get(1),
                            ItemRefer.High_Density_Uranium.get(1), ItemRefer.High_Density_Thorium.get(1),
                            Ic2Items.UranFuel, Ic2Items.MOXFuel, WerkstoffLoader.Tiberium.get(OrePrefixes.gem, 1) })),
                new ResearchPage("tc.research_text.gtnl.essentiaUpgradeRadiation.1"),
                new ResearchPage("tc.research_text.gtnl.essentiaUpgradeRadiation.2"))
                .setParents("gtnl.essentiaUpgradeEmpty")
                .registerResearchItem();

        new ResearchItem(
            "gtnl.essentiaUpgradeElectric",
            "gtnl",
            new AspectList().add(Aspect.EXCHANGE, 10)
                .add(Aspect.TOOL, 10)
                .add(Aspect.ENERGY, 10),
            -7,
            7,
            1,
            GTNLItemList.EssentiaUpgradeElectric.get(1)).setPages(
                new ResearchPage("tc.research_text.gtnl.essentiaUpgradeElectric.0"),
                new ResearchPage(
                    ThaumcraftApi.addInfusionCraftingRecipe(
                        "gtnl.essentiaUpgradeElectric",
                        GTNLItemList.EssentiaUpgradeElectric.get(1),
                        10,
                        new AspectList().add(Aspect.ENERGY, 32768),
                        GTNLItemList.EssentiaUpgradeEmpty.get(1),
                        new ItemStack[] { GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorMV, 1),
                            GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorHV, 1),
                            GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorEV, 1),
                            GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorIV, 1),
                            GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorLuV, 1),
                            GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorZPM, 1) })),
                new ResearchPage("tc.research_text.gtnl.essentiaUpgradeElectric.1"))
                .setParents(
                    "gtnl.essentiaUpgradeAir",
                    "gtnl.essentiaUpgradeThermal",
                    "gtnl.essentiaUpgradeUnstable",
                    "gtnl.essentiaUpgradeVictus",
                    "gtnl.essentiaUpgradeTainted",
                    "gtnl.essentiaUpgradeMechanics",
                    "gtnl.essentiaUpgradeSpirit",
                    "gtnl.essentiaUpgradeRadiation")
                .registerResearchItem();
    }
}
