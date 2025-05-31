package com.science.gtnl.loader;

import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.addItemTooltip;
import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.text;
import static com.science.gtnl.Utils.item.TextHandler.texter;
import static com.science.gtnl.common.item.BasicItems.MetaItem;
import static com.science.gtnl.common.item.ItemAdder.initItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.Utils.text.AnimatedText;
import com.science.gtnl.common.item.ItemRecord;
import com.science.gtnl.common.item.items.CircuitIntegratedPlus;
import com.science.gtnl.common.item.items.FakeItemSiren;
import com.science.gtnl.common.item.items.FuelRod.FuelRod;
import com.science.gtnl.common.item.items.FuelRod.FuelRodDepleted;
import com.science.gtnl.common.item.items.KFCFamily;
import com.science.gtnl.common.item.items.PhysicsCape;
import com.science.gtnl.common.item.items.RejectionRing;
import com.science.gtnl.common.item.items.SatietyRing;
import com.science.gtnl.common.item.items.SuperReachRing;
import com.science.gtnl.common.item.items.TestItem;
import com.science.gtnl.common.item.items.TimeStopPocketWatch;
import com.science.gtnl.common.item.items.TwilightSword;

import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTOreDictUnificator;

public class ItemLoader {

    public static Item FakeItemSiren = new FakeItemSiren();
    public static Item TestItem = new TestItem();
    public static Item KFCFamily = new KFCFamily(20, 20, true);
    public static Item SatietyRing = new SatietyRing();
    public static Item RejectionRing = new RejectionRing();
    public static Item TwilightSword = new TwilightSword();
    public static Item PhysicsCape = new PhysicsCape();
    public static Item CircuitIntegratedPlus = new CircuitIntegratedPlus();
    public static Item TimeStopPocketWatch = new TimeStopPocketWatch();
    public static Item SuperReachRing = new SuperReachRing();
    public static Item RecordSus = new ItemRecord("sus");
    public static Item RecordNewHorizons = new ItemRecord("newhorizons");

    public static Item InfinityFuelRodDepleted = new FuelRodDepleted("InfinityFuelRodDepleted", 2000);
    public static Item InfinityFuelRod = new FuelRod(
        "InfinityFuelRod",
        1,
        491520,
        500,
        15000,
        160000,
        70F,
        new ItemStack(InfinityFuelRodDepleted, 1));

    public static void IRegistry(Item item, String name) {
        GameRegistry.registerItem(item, name);
    }

    public static void registryItems() {
        Item[] itemsToReg = { MetaItem };

        for (Item item : itemsToReg) {
            GameRegistry.registerItem(item, item.getUnlocalizedName());
        }

        GTNLItemList.RecordSus.set(new ItemStack(RecordSus, 1));
        GTNLItemList.RecordNewHorizons.set(new ItemStack(RecordNewHorizons, 1));
        GTNLItemList.InfinityFuelRodDepleted.set(new ItemStack(InfinityFuelRodDepleted, 1));
        GTNLItemList.InfinityFuelRod.set(new ItemStack(InfinityFuelRod, 1));

        IRegistry(FakeItemSiren, "FakeItemSiren");
        IRegistry(TestItem, "TestItem");
        IRegistry(KFCFamily, "KFCFamily");
        IRegistry(SatietyRing, "SatietyRing");
        IRegistry(RejectionRing, "RejectionRing");
        IRegistry(TwilightSword, "TwilightSword");
        IRegistry(PhysicsCape, "PhysicsCape");
        IRegistry(TimeStopPocketWatch, "TimeStopPocketWatch");
        IRegistry(RecordSus, "RecordSus");
        IRegistry(RecordNewHorizons, "RecordNewHorizons");
        IRegistry(CircuitIntegratedPlus, "CircuitIntegratedPlus");

        IRegistry(InfinityFuelRodDepleted, "InfinityFuelRodDepleted");
        IRegistry(InfinityFuelRod, "InfinityFuelRod");

    }

    public static void registryItemContainers() {
        GTNLItemList.TrollFace
            .set(initItem("TrollFace", 0, new String[] { texter("Never Gonna Give You Up~", "tooltips.TrollFace") }));
        GTNLItemList.DepletedExcitedNaquadahFuelRod.set(
            initItem(
                "§bDepleted Excited Naquadah Fuel Rod",
                1,
                new String[] {
                    texter("§oUltimate Form of Naquadah Fuel.", "tooltips.DepletedExcitedNaquadahFuelRod") }));
        GTNLItemList.BlazeCube
            .set(initItem("§eBlaze Cube", 2, new String[] { texter("§8§oBlaze Storm.", "tooltips.BlazeCube") }));
        GTNLItemList.EnhancementCore.set(
            initItem(
                "Enhancement Core",
                3,
                new String[] { texter("§8§oThe road to completion!", "tooltips.EnhancementCore") }));
        GTNLItemList.StellarConstructionFrameMaterial.set(
            initItem(
                "Stellar Construction Frame Material",
                4,
                new String[] { texter("A test item, no use.", "tooltips.TestItem0.line1") }));
        GTNLItemList.ActivatedGaiaPylon.set(initItem("Activated Gaia Pylon", 5));
        GTNLItemList.PrecisionSteamMechanism.set(initItem("Precision Steam Mechanism", 6));
        GTNLItemList.MeteorMinerSchematic1.set(
            initItem(
                "Meteor Miner Tier 1 Schematic",
                7,
                new String[] {
                    texter("Schematic needed to enable the Tier 1 Meteor Miner", "tooltips.MeteorMinerSchematic1") }));
        GTNLItemList.MeteorMinerSchematic2.set(
            initItem(
                "Meteor Miner Tier 2 Schematic",
                8,
                new String[] {
                    texter("Schematic needed to enable the Tier 2 Meteor Miner", "tooltips.MeteorMinerSchematic2") }));
        GTNLItemList.CircuitResonaticULV.set(
            initItem(
                "Circuit Resonatic ULV",
                9,
                new String[] { texter("An Original Circuit", "tooltips.CircuitResonaticULV_00"),
                    texter("§fULV-Tier", "tooltips.CircuitResonaticULV_01") }));
        GTNLItemList.CircuitResonaticLV.set(
            initItem(
                "Circuit Resonatic LV",
                10,
                new String[] { texter("An Basic Circuit", "tooltips.CircuitResonaticLV_00"),
                    texter("LV-Tier", "tooltips.CircuitResonaticLV_01") }));
        GTNLItemList.CircuitResonaticMV.set(
            initItem(
                "Circuit Resonatic MV",
                11,
                new String[] { texter("An Good Circuit", "tooltips.CircuitResonaticMV_00"),
                    texter("§6MV-Tier", "tooltips.CircuitResonaticMV_01") }));
        GTNLItemList.CircuitResonaticHV.set(
            initItem(
                "Circuit Resonatic HV",
                12,
                new String[] { texter("An Adavanced Circuit", "tooltips.CircuitResonaticHV_00"),
                    texter("§eHV-Tier", "tooltips.CircuitResonaticHV_01") }));
        GTNLItemList.CircuitResonaticEV.set(
            initItem(
                "Circuit Resonatic EV",
                13,
                new String[] { texter("An Data Circuit", "tooltips.CircuitResonaticEV_00"),
                    texter("§8EV-Tier", "tooltips.CircuitResonaticEV_01") }));
        GTNLItemList.CircuitResonaticIV.set(
            initItem(
                "Circuit Resonatic IV",
                14,
                new String[] { texter("An Elite Circuit", "tooltips.CircuitResonaticIV_00"),
                    texter("§aIV-Tier", "tooltips.CircuitResonaticIV_01") }));
        GTNLItemList.CircuitResonaticLuV.set(
            initItem(
                "Circuit Resonatic LuV",
                15,
                new String[] { texter("An Master Circuit", "tooltips.CircuitResonaticLuV_00"),
                    texter("§dLuV-Tier", "tooltips.CircuitResonaticLuV_01") }));
        GTNLItemList.CircuitResonaticZPM.set(
            initItem(
                "Circuit Resonatic ZPM",
                16,
                new String[] { texter("An Ultimate Circuit", "tooltips.CircuitResonaticZPM_00"),
                    texter("§bZPM-Tier", "tooltips.CircuitResonaticZPM_01") }));
        GTNLItemList.CircuitResonaticUV.set(
            initItem(
                "Circuit Resonatic UV",
                17,
                new String[] { texter("An Superconductor Circuit", "tooltips.CircuitResonaticUV_00"),
                    texter("§2UV-Tier", "tooltips.CircuitResonaticUV_01") }));
        GTNLItemList.CircuitResonaticUHV.set(
            initItem(
                "Circuit Resonatic UHV",
                18,
                new String[] { texter("An Infinite Circuit", "tooltips.CircuitResonaticUHV_00"),
                    texter("§4UHV-Tier", "tooltips.CircuitResonaticUHV_01") }));
        GTNLItemList.CircuitResonaticUEV.set(
            initItem(
                "Circuit Resonatic UEV",
                19,
                new String[] { texter("An Bio Circuit", "tooltips.CircuitResonaticUEV_00"),
                    texter("§5UEV-Tier", "tooltips.CircuitResonaticUEV_01") }));
        GTNLItemList.CircuitResonaticUIV.set(
            initItem(
                "Circuit Resonatic UIV",
                20,
                new String[] { texter("An Optical Circuit", "tooltips.CircuitResonaticUIV_00"),
                    texter("§l§1UIV-Tier", "tooltips.CircuitResonaticUIV_01") }));
        GTNLItemList.VerySimpleCircuit.set(
            initItem(
                "Very Simple Circuit",
                21,
                new String[] { texter("An Very Simple Circuit", "tooltips.VerySimpleCircuit_00"),
                    texter("§fULV-Tier", "tooltips.VerySimpleCircuit_01") }));
        GTNLItemList.SimpleCircuit.set(
            initItem(
                "Simple Circuit",
                22,
                new String[] { texter("An Simple Circuit", "tooltips.SimpleCircuit_00"),
                    texter("LV-Tier", "tooltips.SimpleCircuit_01") }));
        GTNLItemList.BasicCircuit.set(
            initItem(
                "Basic Circuit",
                23,
                new String[] { texter("An Basic Circuit", "tooltips.BasicCircuit_00"),
                    texter("§6MV-Tier", "tooltips.BasicCircuit_01") }));
        GTNLItemList.AdvancedCircuit.set(
            initItem(
                "Advanced Circuit",
                24,
                new String[] { texter("An Advanced Circuit", "tooltips.AdvancedCircuit_00"),
                    texter("§eHV-Tier", "tooltips.AdvancedCircuit_01") }));
        GTNLItemList.EliteCircuit.set(
            initItem(
                "Elite Circuit",
                25,
                new String[] { texter("An Elite Circuit", "tooltips.EliteCircuit_00"),
                    texter("§8EV-Tier", "tooltips.EliteCircuit_01") }));
        GTNLItemList.StargateSingularity.set(initItem("Stargate Singularity", 26));
        GTNLItemList.StargateCompressedSingularity.set(initItem("Stargate Compressed Singularity", 27));
        GTNLItemList.BiowareSMDCapacitor.set(initItem("Bioware SMD Capacitor", 28));
        GTNLItemList.BiowareSMDDiode.set(initItem("Bioware SMD Diode", 29));
        GTNLItemList.BiowareSMDInductor.set(initItem("Bioware SMD Inductor", 30));
        GTNLItemList.BiowareSMDResistor.set(initItem("Bioware SMD Resistor", 31));
        GTNLItemList.BiowareSMDTransistor.set(initItem("Bioware SMD Transistor", 32));
        GTNLItemList.CosmicSMDCapacitor.set(initItem("Cosmic SMD Capacitor", 33));
        GTNLItemList.CosmicSMDDiode.set(initItem("Cosmic SMD Diode", 34));
        GTNLItemList.CosmicSMDInductor.set(initItem("Cosmic SMD Inductor", 35));
        GTNLItemList.CosmicSMDResistor.set(initItem("Cosmic SMD Resistor", 36));
        GTNLItemList.CosmicSMDTransistor.set(initItem("Cosmic SMD Transistor", 37));
        GTNLItemList.ExoticSMDCapacitor.set(initItem("Exotic SMD Capacitor", 38));
        GTNLItemList.ExoticSMDDiode.set(initItem("Exotic SMD Diode", 39));
        GTNLItemList.ExoticSMDInductor.set(initItem("Exotic SMD Inductor", 40));
        GTNLItemList.ExoticSMDResistor.set(initItem("Exotic SMD Resistor", 41));
        GTNLItemList.ExoticSMDTransistor.set(initItem("Exotic SMD Transistor", 42));
        GTNLItemList.TemporallySMDCapacitor.set(initItem("Temporally SMD Capacitor", 43));
        GTNLItemList.TemporallySMDDiode.set(initItem("Temporally SMD Diode", 44));
        GTNLItemList.TemporallySMDInductor.set(initItem("Temporally SMD Inductor", 45));
        GTNLItemList.TemporallySMDResistor.set(initItem("Temporally SMD Resistor", 46));
        GTNLItemList.TemporallySMDTransistor.set(initItem("Temporally SMD Transistor", 47));
        GTNLItemList.LVParallelControllerCore.set(
            initItem(
                "LV Parallel Controller Core",
                48,
                new String[] { texter("§dSpeed: +1%%", "tooltips.LVParallelControllerCore_00"),
                    texter("§5EU Usage: -2%%", "tooltips.LVParallelControllerCore_01"),
                    texter("§bParallel: 0", "tooltips.LVParallelControllerCore_02") }));

        GTNLItemList.MVParallelControllerCore.set(
            initItem(
                "MV Parallel Controller Core",
                49,
                new String[] { texter("§dSpeed: +2%%", "tooltips.MVParallelControllerCore_00"),
                    texter("§5EU Usage: -4%%", "tooltips.MVParallelControllerCore_01"),
                    texter("§bParallel: 1", "tooltips.MVParallelControllerCore_02") }));

        GTNLItemList.HVParallelControllerCore.set(
            initItem(
                "HV Parallel Controller Core",
                50,
                new String[] { texter("§dSpeed: +4%%", "tooltips.HVParallelControllerCore_00"),
                    texter("§5EU Usage: -6%%", "tooltips.HVParallelControllerCore_01"),
                    texter("§bParallel: 4", "tooltips.HVParallelControllerCore_02") }));

        GTNLItemList.EVParallelControllerCore.set(
            initItem(
                "EV Parallel Controller Core",
                51,
                new String[] { texter("§dSpeed: +6%%", "tooltips.EVParallelControllerCore_00"),
                    texter("§5EU Usage: -8%%", "tooltips.EVParallelControllerCore_01"),
                    texter("§bParallel: 16", "tooltips.EVParallelControllerCore_02") }));

        GTNLItemList.IVParallelControllerCore.set(
            initItem(
                "IV Parallel Controller Core",
                52,
                new String[] { texter("§dSpeed: +8%%", "tooltips.IVParallelControllerCore_00"),
                    texter("§5EU Usage: -10%%", "tooltips.IVParallelControllerCore_01"),
                    texter("§bParallel: 64", "tooltips.IVParallelControllerCore_02") }));

        GTNLItemList.LuVParallelControllerCore.set(
            initItem(
                "LuV Parallel Controller Core",
                53,
                new String[] { texter("§dSpeed: +10%%", "tooltips.LuVParallelControllerCore_00"),
                    texter("§5EU Usage: -12%%", "tooltips.LuVParallelControllerCore_01"),
                    texter("§bParallel: 256", "tooltips.LuVParallelControllerCore_02") }));

        GTNLItemList.ZPMParallelControllerCore.set(
            initItem(
                "ZPM Parallel Controller Core",
                54,
                new String[] { texter("§dSpeed: +12%%", "tooltips.ZPMParallelControllerCore_00"),
                    texter("§5EU Usage: -14%%", "tooltips.ZPMParallelControllerCore_01"),
                    texter("§bParallel: 1024", "tooltips.ZPMParallelControllerCore_02") }));

        GTNLItemList.UVParallelControllerCore.set(
            initItem(
                "UV Parallel Controller Core",
                55,
                new String[] { texter("§dSpeed: +14%%", "tooltips.UVParallelControllerCore_00"),
                    texter("§5EU Usage: -16%%", "tooltips.UVParallelControllerCore_01"),
                    texter("§bParallel: 4096", "tooltips.UVParallelControllerCore_02") }));

        GTNLItemList.UHVParallelControllerCore.set(
            initItem(
                "UHV Parallel Controller Core",
                56,
                new String[] { texter("§dSpeed: +16%%", "tooltips.UHVParallelControllerCore_00"),
                    texter("§5EU Usage: -18%%", "tooltips.UHVParallelControllerCore_01"),
                    texter("§bParallel: 16384", "tooltips.UHVParallelControllerCore_02") }));

        GTNLItemList.UEVParallelControllerCore.set(
            initItem(
                "UEV Parallel Controller Core",
                57,
                new String[] { texter("§dSpeed: +18%%", "tooltips.UEVParallelControllerCore_00"),
                    texter("§5EU Usage: -20%%", "tooltips.UEVParallelControllerCore_01"),
                    texter("§bParallel: 65536", "tooltips.UEVParallelControllerCore_02") }));

        GTNLItemList.UIVParallelControllerCore.set(
            initItem(
                "UIV Parallel Controller Core",
                58,
                new String[] { texter("§dSpeed: +20%%", "tooltips.UIVParallelControllerCore_00"),
                    texter("§5EU Usage: -22%%", "tooltips.UIVParallelControllerCore_01"),
                    texter("§bParallel: 262144", "tooltips.UIVParallelControllerCore_02") }));

        GTNLItemList.UMVParallelControllerCore.set(
            initItem(
                "UMV Parallel Controller Core",
                59,
                new String[] { texter("§dSpeed: +22%%", "tooltips.UMVParallelControllerCore_00"),
                    texter("§5EU Usage: -24%%", "tooltips.UMVParallelControllerCore_01"),
                    texter("§bParallel: 1048576", "tooltips.UMVParallelControllerCore_02") }));

        GTNLItemList.UXVParallelControllerCore.set(
            initItem(
                "UXV Parallel Controller Core",
                60,
                new String[] { texter("§dSpeed: +24%%", "tooltips.UXVParallelControllerCore_00"),
                    texter("§5EU Usage: -26%%", "tooltips.UXVParallelControllerCore_01"),
                    texter("§bParallel: 4194304", "tooltips.UXVParallelControllerCore_02") }));

        GTNLItemList.MAXParallelControllerCore.set(
            initItem(
                "MAX Parallel Controller Core",
                61,
                new String[] { texter("§dSpeed: +26%%", "tooltips.MAXParallelControllerCore_00"),
                    texter("§5EU Usage: -28%%", "tooltips.MAXParallelControllerCore_01"),
                    texter("§bParallel: 16777216", "tooltips.MAXParallelControllerCore_02") }));

        GTNLItemList.NagaBook.set(initItem("§7Canard - Naga Book", 62));
        GTNLItemList.TwilightForestBook.set(initItem("§bUrban legends - Twilight Forest Book", 63));
        GTNLItemList.LichBook.set(initItem("§6Urban Myth - Lich Book", 64));
        GTNLItemList.MinotaurBook.set(initItem("§9Urban Legend - Minotaur Book", 65));
        GTNLItemList.HydraBook.set(initItem("§9Urban Legend - Hydra Book", 66));
        GTNLItemList.KnightPhantomBook.set(initItem("§aUrban Plague - Knight Phantom Book", 67));
        GTNLItemList.UrGhastBook.set(initItem("§aUrban Plague - Ur-Ghast Book", 68));
        GTNLItemList.AlphaYetiBook.set(initItem("§aUrban Plague - Alpha Yeti Book", 69));
        GTNLItemList.SnowQueenBook.set(initItem("§5Urban Nightmare - Snow Queen Book", 70));
        GTNLItemList.FinalBook.set(initItem("§cImpurity - Final Book", 71));
        GTNLItemList.GiantBook.set(initItem("§5Urban Nightmare - Giant Book", 72));
        GTNLItemList.ClayedGlowstone.set(initItem("Clayed Glowstone", 73));
        GTNLItemList.QuantumDisk.set(initItem("Quantum Disk", 74));
        GTNLItemList.NeutroniumBoule.set(
            initItem("Neutronium Boule", 75, new String[] { texter("Raw Circuit", "tooltips.NeutroniumBoule_00") }));
        GTNLItemList.NeutroniumWafer.set(
            initItem("Neutronium Wafer", 76, new String[] { texter("Raw Circuit", "tooltips.NeutroniumWafer_00") }));
        GTNLItemList.HighlyAdvancedSocWafer.set(
            initItem(
                "Highly Advanced Soc Wafer",
                77,
                new String[] { texter("Raw Circuit", "tooltips.HighlyAdvancedSocWafer_00") }));
        GTNLItemList.HighlyAdvancedSoc.set(
            initItem(
                "Highly Advanced Soc",
                78,
                new String[] { texter("Highly Advanced SoC", "tooltips.HighlyAdvancedSoc_00") }));
        GTNLItemList.ZnFeAlClCatalyst.set(initItem("Zn-Fe-Al-Cl Catalyst", 79));
        GTNLItemList.BlackLight.set(
            initItem(
                "Black Light",
                80,
                new String[] { texter("§8long-wave §dultraviolet §8light source", "tooltips.BlackLight_00") }));
        GTNLItemList.SteamgateDialingDevice.set(
            initItem(
                "Steamgate Dialing Device",
                81,
                new String[] { texter("Links steamgates for teleportation.", "tooltips.SteamgateDialingDevice_00") }));
        GTNLItemList.SteamgateChevron.set(initItem("Steamgate Chevron", 82));
        GTNLItemList.SteamgateChevronUpgrade.set(initItem("Steamgate Chevron Upgrade", 83));
        GTNLItemList.SteamgateIrisBlade.set(initItem("Steamgate Iris Blade", 84));
        GTNLItemList.SteamgateIrisUpgrade.set(initItem("Steamgate Iris Upgrade", 85));
        GTNLItemList.SteamgateHeatContainmentPlate.set(
            initItem(
                "Steamgate Heat Containment Plate",
                86,
                new String[] {
                    texter("Perfect temperature stability...", "tooltips.SteamgateHeatContainmentPlate_00") }));
        GTNLItemList.SteamgateFrame.set(
            initItem(
                "Steamgate Frame",
                87,
                new String[] { texter("Innumerable interlocking gears...", "tooltips.SteamgateFrame_00") }));
        GTNLItemList.SteamgateCoreCrystal.set(
            initItem(
                "Steamgate Core Crystal",
                88,
                new String[] {
                    texter("A crystal made of pure condensed staem...", "tooltips.SteamgateCoreCrystal_00") }));
        GTNLItemList.HydraulicMotor.set(initItem("Hydraulic Motor", 89));
        GTNLItemList.HydraulicPiston.set(initItem("Hydraulic Piston", 90));
        GTNLItemList.HydraulicPump.set(
            initItem(
                "Hydraulic Pump",
                91,
                new String[] { texter("1,048,576 L/t (20,971,520 L/s) as Cover", "tooltips.HydraulicPump_00") }));
        GTNLItemList.HydraulicArm.set(initItem("Hydraulic Arm", 92));
        GTNLItemList.HydraulicConveyor.set(
            initItem(
                "Hydraulic Conveyor",
                93,
                new String[] { texter("16 stacks every 1/20 sec (as Cover)", "tooltips.HydraulicConveyor_00") }));
        GTNLItemList.HydraulicRegulator.set(
            initItem(
                "Hydraulic Regulator",
                94,
                new String[] {
                    texter("Configurable up to 20,971,520 L/sec (as Cover)", "tooltips.HydraulicRegulator_00"),
                    texter(
                        "Rightclick/Screwdriver-rightclick/Shift-screwdriver-rightclick",
                        "tooltips.HydraulicRegulator_01"),
                    texter(
                        "to adjust the pump speed by 1/16/256 L/sec per click",
                        "tooltips.HydraulicRegulator_02") }));
        GTNLItemList.HydraulicVaporGenerator.set(initItem("Hydraulic Vapor Generator", 95));
        GTNLItemList.HydraulicSteamJetSpewer.set(initItem("Hydraulic Steam Jet Spewer", 96));
        GTNLItemList.HydraulicSteamReceiver.set(initItem("Hydraulic Steam Receiver", 97));
        GTNLItemList.HydraulicSteamValve.set(
            initItem(
                "Hydraulic Steam Valve",
                98,
                new String[] {
                    texter("16,777,216 L/t (335,544,320 L/s) as Cover", "tooltips.HydraulicSteamValve_00") }));
        addItemTooltip(
            GTNLItemList.HydraulicSteamValve.get(1),
            AnimatedText.buildTextWithAnimatedEnd(text("Tips: 瑶光Alkaid要的")));
        GTNLItemList.HydraulicSteamRegulator.set(
            initItem(
                "Hydraulic Steam Regulator",
                99,
                new String[] {
                    texter("16,777,216 L/t (335,544,320 L/s) as Cover", "tooltips.HydraulicSteamRegulator_00") }));
        addItemTooltip(
            GTNLItemList.HydraulicSteamRegulator.get(1),
            AnimatedText.buildTextWithAnimatedEnd(text("Tips: 瑶光Alkaid要的")));
        GTNLItemList.SadBapyCatToken.set(
            initItem("SadBapyCat Token", 100, new String[] { texter(":sadbapycat:", "tooltips.SadBapyCatToken_00") }));
        GTNLItemList.CompressedSteamTurbine.set(
            initItem(
                "Compressed Steam Turbine",
                101,
                new String[] { texter("Infinite Throughtput", "tooltips.CompressedSteamTurbine_00") }));
        GTNLItemList.SteelTurbine.set(
            initItem("Steel Turbine", 102, new String[] { texter("Stable like a Table", "tooltips.SteelTurbine_00") }));

    }

    public static void registry() {
        registryItems();
        registryItemContainers();

        addItemTooltip(
            new ItemStack(ItemLoader.SatietyRing, 1),
            AnimatedText.buildTextWithAnimatedEnd(text("Most machine recipe by zero_CM")));
    }

    public static void registryOreDictionary() {

        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.ULV), GTNLItemList.CircuitResonaticULV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.LV), GTNLItemList.CircuitResonaticLV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.MV), GTNLItemList.CircuitResonaticMV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.HV), GTNLItemList.CircuitResonaticHV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.EV), GTNLItemList.CircuitResonaticEV.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.IV), GTNLItemList.CircuitResonaticIV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.LuV), GTNLItemList.CircuitResonaticLuV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.ZPM), GTNLItemList.CircuitResonaticZPM.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.UV), GTNLItemList.CircuitResonaticUV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UHV), GTNLItemList.CircuitResonaticUHV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UEV), GTNLItemList.CircuitResonaticUEV.get(1));
        GTOreDictUnificator
            .registerOre(OrePrefixes.circuit.get(Materials.UIV), GTNLItemList.CircuitResonaticUIV.get(1));

        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.ULV), GTNLItemList.VerySimpleCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.LV), GTNLItemList.SimpleCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.MV), GTNLItemList.BasicCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.HV), GTNLItemList.AdvancedCircuit.get(1));
        GTOreDictUnificator.registerOre(OrePrefixes.circuit.get(Materials.EV), GTNLItemList.EliteCircuit.get(1));
    }

    public static void registryOreBlackList() {
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticULV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticLV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticMV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticHV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticEV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticIV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticLuV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticZPM.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticUV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticUHV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticUEV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.CircuitResonaticUIV.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.VerySimpleCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.SimpleCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.BasicCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.AdvancedCircuit.get(1));
        GTOreDictUnificator.addToBlacklist(GTNLItemList.EliteCircuit.get(1));
    }
}
