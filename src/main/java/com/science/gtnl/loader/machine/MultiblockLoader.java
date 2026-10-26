package com.science.gtnl.loader.machine;

import net.minecraft.util.StatCollector;

import com.science.gtnl.common.machine.multiblock.AdvancedInfiniteDriller;
import com.science.gtnl.common.machine.multiblock.AdvancedRareEarthCentrifugal;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.common.machine.multiblock.AtomicEnergyExcitationPlant;
import com.science.gtnl.common.machine.multiblock.BloodSoulSacrificialArray;
import com.science.gtnl.common.machine.multiblock.BrickedBlastFurnace;
import com.science.gtnl.common.machine.multiblock.CheatOreProcessingFactory;
import com.science.gtnl.common.machine.multiblock.ComponentAssembler;
import com.science.gtnl.common.machine.multiblock.DataCenter;
import com.science.gtnl.common.machine.multiblock.DecayHastener;
import com.science.gtnl.common.machine.multiblock.Desulfurizer;
import com.science.gtnl.common.machine.multiblock.DraconicFusionCrafting;
import com.science.gtnl.common.machine.multiblock.EdenGarden;
import com.science.gtnl.common.machine.multiblock.ElectrocellGenerator;
import com.science.gtnl.common.machine.multiblock.ElementCopying;
import com.science.gtnl.common.machine.multiblock.EyeOfHarmonyInjector;
import com.science.gtnl.common.machine.multiblock.FOGAlloyBlastSmelterModule;
import com.science.gtnl.common.machine.multiblock.FOGAlloySmelterModule;
import com.science.gtnl.common.machine.multiblock.FOGExtractorModule;
import com.science.gtnl.common.machine.multiblock.FOGSolarMuonCatalystModule;
import com.science.gtnl.common.machine.multiblock.FuelRefiningComplex;
import com.science.gtnl.common.machine.multiblock.GenerationEarthEngine;
import com.science.gtnl.common.machine.multiblock.GrandAssemblyLine;
import com.science.gtnl.common.machine.multiblock.IndustrialArcaneAssembler;
import com.science.gtnl.common.machine.multiblock.LapotronChip;
import com.science.gtnl.common.machine.multiblock.LargeBioLab;
import com.science.gtnl.common.machine.multiblock.LargeBrewer;
import com.science.gtnl.common.machine.multiblock.LargeCircuitAssembler;
import com.science.gtnl.common.machine.multiblock.LargeEssentiaGenerator;
import com.science.gtnl.common.machine.multiblock.LargeGasCollector;
import com.science.gtnl.common.machine.multiblock.LargeIncubator;
import com.science.gtnl.common.machine.multiblock.LibraryOfRuina;
import com.science.gtnl.common.machine.multiblock.MassFabricator;
import com.science.gtnl.common.machine.multiblock.MatterFabricator;
import com.science.gtnl.common.machine.multiblock.MegaMixer;
import com.science.gtnl.common.machine.multiblock.MeteorMiner;
import com.science.gtnl.common.machine.multiblock.NaquadahReactor;
import com.science.gtnl.common.machine.multiblock.PCBFactory;
import com.science.gtnl.common.machine.multiblock.PetrochemicalPlant;
import com.science.gtnl.common.machine.multiblock.PhotovoltaicPowerStation;
import com.science.gtnl.common.machine.multiblock.PlatinumBasedTreatment;
import com.science.gtnl.common.machine.multiblock.ProcessingArray;
import com.science.gtnl.common.machine.multiblock.QuantumComputer;
import com.science.gtnl.common.machine.multiblock.RareEarthCentrifugal;
import com.science.gtnl.common.machine.multiblock.ReactionFurnace;
import com.science.gtnl.common.machine.multiblock.RealArtificialStar;
import com.science.gtnl.common.machine.multiblock.ResourceCollectionModule;
import com.science.gtnl.common.machine.multiblock.ShallowChemicalCoupling;
import com.science.gtnl.common.machine.multiblock.SingularityDataHub;
import com.science.gtnl.common.machine.multiblock.SpaceAssembler;
import com.science.gtnl.common.machine.multiblock.SuperSpaceElevator;
import com.science.gtnl.common.machine.multiblock.SupercomputingCenter;
import com.science.gtnl.common.machine.multiblock.TeleportationArrayToAlfheim;
import com.science.gtnl.common.machine.multiblock.WhiteNightGenerator;
import com.science.gtnl.common.machine.multiblock.WoodDistillation;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.EGTWFusionModule;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.ETGWEyeOfHarmonyModule;
import com.science.gtnl.common.machine.multiblock.module.eternalGregTechWorkshop.EternalGregTechWorkshop;
import com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter.BioengineeringModule;
import com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter.NanitesIntegratedProcessingCenter;
import com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter.OreExtractionModule;
import com.science.gtnl.common.machine.multiblock.module.nanitesIntegratedProcessingCenter.PolymerTwistingModule;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamApiaryModule;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamBeaconModule;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamBeeBreedingModule;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamElevator;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamEntityCrusherModule;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamFlightModule;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamGreenhouseModule;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamMonsterRepellentModule;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamOilDrillModule;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamOreProcessorModule;
import com.science.gtnl.common.machine.multiblock.module.steamElevator.SteamWeatherModule;
import com.science.gtnl.common.machine.multiblock.steam.FurnaceArray;
import com.science.gtnl.common.machine.multiblock.steam.HighPressureSteamFusionReactor;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamAlloySmelter;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamBending;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamCentrifuge;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamChemicalBath;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamCircuitAssembler;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamCompressor;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamCrusher;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamCutting;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamExtractor;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamExtruder;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamFormingPress;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamFurnace;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamHammer;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamLathe;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamMixer;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamOreWasher;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamSifter;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamThermalCentrifuge;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamWiremill;
import com.science.gtnl.common.machine.multiblock.steam.MegaSolarBoiler;
import com.science.gtnl.common.machine.multiblock.steam.MegaSteamCompressor;
import com.science.gtnl.common.machine.multiblock.steam.PrimitiveBrickKiln;
import com.science.gtnl.common.machine.multiblock.steam.PrimitiveDistillationTower;
import com.science.gtnl.common.machine.multiblock.steam.SteamCactusWonder;
import com.science.gtnl.common.machine.multiblock.steam.SteamCarpenter;
import com.science.gtnl.common.machine.multiblock.steam.SteamCracking;
import com.science.gtnl.common.machine.multiblock.steam.SteamExtractinator;
import com.science.gtnl.common.machine.multiblock.steam.SteamFusionReactor;
import com.science.gtnl.common.machine.multiblock.steam.SteamGateAssembler;
import com.science.gtnl.common.machine.multiblock.steam.SteamInfernalCokeOven;
import com.science.gtnl.common.machine.multiblock.steam.SteamItemVault;
import com.science.gtnl.common.machine.multiblock.steam.SteamLavaMaker;
import com.science.gtnl.common.machine.multiblock.steam.SteamManufacturer;
import com.science.gtnl.common.machine.multiblock.steam.SteamRockBreaker;
import com.science.gtnl.common.machine.multiblock.steam.SteamWoodcutter;
import com.science.gtnl.common.machine.multiblock.steam.Steamgate;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.AlloyBlastSmelter;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.BlazeBlastFurnace;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.ChemicalPlant;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.ColdIceFreezer;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.Digester;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.ElectricBlastFurnace;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.ElectricImplosionCompressor;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.EnergyInfuser;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.FishingGround;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.FlotationCellRegulator;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.HighPerformanceComputationArray;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.Incubator;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.IsaMill;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.KuangBiaoOneGiantNuclearFusionReactor;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeAlloySmelter;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeArcSmelter;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeAssembler;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeAutoclave;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeBender;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeBoiler;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeCanning;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeCentrifuge;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeChemicalBath;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeCutter;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeDistillery;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeElectrolyzer;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeElectromagnet;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeEngravingLaser;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeExtractor;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeExtruder;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeForming;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeHammer;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeIndustrialLathe;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeMacerationTower;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeMaterialPress;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeMixer;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargePacker;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargePyrolyseOven;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeRockCrusher;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeSiftingFunnel;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeSolidifier;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.LargeWiremill;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.MegaAlloyBlastSmelter;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.MegaBlastFurnace;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.MolecularTransformer;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.PrecisionAssembler;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.RocketAssembler;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.VacuumDryingFurnace;
import com.science.gtnl.common.machine.multiblock.structuralReconstructionPlan.VacuumFreezer;
import com.science.gtnl.common.machine.multiblock.wireless.AdvancedMassFabricator;
import com.science.gtnl.common.machine.multiblock.wireless.AetronPressor;
import com.science.gtnl.common.machine.multiblock.wireless.ChemicalComplex;
import com.science.gtnl.common.machine.multiblock.wireless.CompoundDistillationFractionator;
import com.science.gtnl.common.machine.multiblock.wireless.CompoundExtremeCoolingUnit;
import com.science.gtnl.common.machine.multiblock.wireless.CrackerHub;
import com.science.gtnl.common.machine.multiblock.wireless.CrystalBuilder;
import com.science.gtnl.common.machine.multiblock.wireless.DissolutionCore;
import com.science.gtnl.common.machine.multiblock.wireless.EngravingLaserPlant;
import com.science.gtnl.common.machine.multiblock.wireless.ExtremeCompressor;
import com.science.gtnl.common.machine.multiblock.wireless.ExtremeElectricFurnace;
import com.science.gtnl.common.machine.multiblock.wireless.FastNeutronBreederReactor;
import com.science.gtnl.common.machine.multiblock.wireless.FieldForgePress;
import com.science.gtnl.common.machine.multiblock.wireless.GeminiContainmentSystem;
import com.science.gtnl.common.machine.multiblock.wireless.GiantElectrochemicalWorkstation;
import com.science.gtnl.common.machine.multiblock.wireless.GiantFlotationTank;
import com.science.gtnl.common.machine.multiblock.wireless.HandOfJohnDavisonRockefeller;
import com.science.gtnl.common.machine.multiblock.wireless.HeavyRolling;
import com.science.gtnl.common.machine.multiblock.wireless.HighEnergyLaserLathe;
import com.science.gtnl.common.machine.multiblock.wireless.HighwayToHell;
import com.science.gtnl.common.machine.multiblock.wireless.HorizontalCompressor;
import com.science.gtnl.common.machine.multiblock.wireless.IntegratedAssemblyFacility;
import com.science.gtnl.common.machine.multiblock.wireless.KerrNewmanHomogenizer;
import com.science.gtnl.common.machine.multiblock.wireless.MagneticConfinementDimensionalityShockDevice;
import com.science.gtnl.common.machine.multiblock.wireless.MagneticEnergyReactionFurnace;
import com.science.gtnl.common.machine.multiblock.wireless.MantleCrusher;
import com.science.gtnl.common.machine.multiblock.wireless.MegaBathTank;
import com.science.gtnl.common.machine.multiblock.wireless.MegaBrewer;
import com.science.gtnl.common.machine.multiblock.wireless.MegaCanner;
import com.science.gtnl.common.machine.multiblock.wireless.MegaVacuumDryingFurnace;
import com.science.gtnl.common.machine.multiblock.wireless.MegaWiremill;
import com.science.gtnl.common.machine.multiblock.wireless.MicroorganismMaster;
import com.science.gtnl.common.machine.multiblock.wireless.MoltenCore;
import com.science.gtnl.common.machine.multiblock.wireless.NanitesCircuitAssemblyFactory;
import com.science.gtnl.common.machine.multiblock.wireless.NanoAssemblerMarkL;
import com.science.gtnl.common.machine.multiblock.wireless.NanoPhagocytosisPlant;
import com.science.gtnl.common.machine.multiblock.wireless.NeutroniumWireCutting;
import com.science.gtnl.common.machine.multiblock.wireless.NineIndustrialMultiMachine;
import com.science.gtnl.common.machine.multiblock.wireless.PhaseChangeCube;
import com.science.gtnl.common.machine.multiblock.wireless.SmartSiftingHub;
import com.science.gtnl.common.machine.multiblock.wireless.SmeltingMixingFurnace;
import com.science.gtnl.common.machine.multiblock.wireless.SuperconductingElectromagnetism;
import com.science.gtnl.common.machine.multiblock.wireless.SuperconductingMagneticPresser;
import com.science.gtnl.common.machine.multiblock.wireless.SwarmCore;
import com.science.gtnl.common.machine.multiblock.wireless.TransliminalOasis;
import com.science.gtnl.common.machine.multiblock.wireless.TreeDiagram;
import com.science.gtnl.common.machine.multiblock.wireless.VortexMatterCentrifuge;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GTNLMachineID;
import com.science.gtnl.utils.text.AnimatedText;
import com.science.gtnl.utils.text.AnimatedTooltipHandler;

import gregtech.api.enums.Mods;

public class MultiblockLoader {

    public static void registry() {
        GTNLItemList.QuantumComputer.set(new QuantumComputer(GTNLMachineID.QUANTUM_COMPUTER.ID, "QuantumComputer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.QuantumComputer.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.AssemblerMatrix.set(new AssemblerMatrix(GTNLMachineID.ASSEMBLER_MATRIX.ID, "AssemblerMatrix", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.AssemblerMatrix.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.AtomicEnergyExcitationPlant.set(
            new AtomicEnergyExcitationPlant(
                GTNLMachineID.ATOMIC_ENERGY_EXCITATION_PLANT.ID,
                "AtomicEnergyExcitationPlant",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.AtomicEnergyExcitationPlant.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EdenGarden.set(new EdenGarden(GTNLMachineID.EDEN_GARDEN.ID, "EdenGarden", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EdenGarden.get(1), AnimatedText.GTNL_EDEN_GARDEN);

        GTNLItemList.LargeSteamCircuitAssembler.set(
            new LargeSteamCircuitAssembler(
                GTNLMachineID.LARGE_STEAM_CIRCUIT_ASSEMBLER.ID,
                "LargeSteamCircuitAssembler",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LargeSteamCircuitAssembler.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.GenerationEarthEngine
            .set(new GenerationEarthEngine(GTNLMachineID.GENERATION_EARTH_ENGINE.ID, "GenerationEarthEngine", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.GenerationEarthEngine.get(1), AnimatedText.GT_NOT_LEISURE);

        if (Mods.BloodMagic.isModLoaded()) {
            GTNLItemList.BloodSoulSacrificialArray.set(
                new BloodSoulSacrificialArray(
                    GTNLMachineID.BLOOD_SOUL_SACRIFICIAL_ARRAY.ID,
                    "BloodSoulSacrificialArray",
                    ""));
            AnimatedTooltipHandler
                .addItemTooltip(GTNLItemList.BloodSoulSacrificialArray.get(1), AnimatedText.GT_NOT_LEISURE);
        }

        GTNLItemList.RealArtificialStar
            .set(new RealArtificialStar(GTNLMachineID.REAL_ARTIFICIAL_STAR.ID, "RealArtificialStar", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.RealArtificialStar.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.TeleportationArrayToAlfheim.set(
            new TeleportationArrayToAlfheim(
                GTNLMachineID.TELEPORTATION_ARRAY_TO_ALFHEIM.ID,
                "TeleportationArrayToAlfheim",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.TeleportationArrayToAlfheim.get(1), AnimatedText.GTNL_NLXCJH);

        GTNLItemList.LapotronChip.set(new LapotronChip(GTNLMachineID.LAPOTRON_CHIP.ID, "LapotronChip", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LapotronChip.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NeutroniumWireCutting
            .set(new NeutroniumWireCutting(GTNLMachineID.NEUTRONIUM_WIRE_CUTTING.ID, "NeutroniumWireCutting", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NeutroniumWireCutting.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeSteamCrusher
            .set(new LargeSteamCrusher(GTNLMachineID.LARGE_STEAM_CRUSHER.ID, "LargeSteamCrusher", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamCrusher.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.ComponentAssembler
            .set(new ComponentAssembler(GTNLMachineID.COMPONENT_ASSEMBLER.ID, "ComponentAssembler", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ComponentAssembler.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeSteamFurnace
            .set(new LargeSteamFurnace(GTNLMachineID.LARGE_STEAM_FURNACE.ID, "LargeSteamFurnace", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamFurnace.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeSteamAlloySmelter
            .set(new LargeSteamAlloySmelter(GTNLMachineID.LARGE_STEAM_ALLOY_SMELTER.ID, "LargeSteamAlloySmelter", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamAlloySmelter.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeSteamThermalCentrifuge.set(
            new LargeSteamThermalCentrifuge(
                GTNLMachineID.LARGE_STEAM_THERMAL_CENTRIFUGE.ID,
                "LargeSteamThermalCentrifuge",
                ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamThermalCentrifuge.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SteamCracking.set(new SteamCracking(GTNLMachineID.STEAM_CRACKING.ID, "SteamCracking", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamCracking.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeSteamChemicalBath
            .set(new LargeSteamChemicalBath(GTNLMachineID.LARGE_STEAM_CHEMICAL_BATH.ID, "LargeSteamChemicalBath", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamChemicalBath.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.PrimitiveDistillationTower.set(
            new PrimitiveDistillationTower(
                GTNLMachineID.PRIMITIVE_DISTILLATION_TOWER.ID,
                "PrimitiveDistillationTower",
                ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PrimitiveDistillationTower.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MeteorMiner.set(new MeteorMiner(GTNLMachineID.METEOR_MINER.ID, "MeteorMiner", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MeteorMiner.get(1), AnimatedText.GTNL_TOTTO);

        GTNLItemList.Desulfurizer.set(new Desulfurizer(GTNLMachineID.DESULFURIZER.ID, "Desulfurizer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.Desulfurizer.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeCircuitAssembler
            .set(new LargeCircuitAssembler(GTNLMachineID.LARGE_CIRCUIT_ASSEMBLER.ID, "LargeCircuitAssembler", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeCircuitAssembler.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.PetrochemicalPlant
            .set(new PetrochemicalPlant(GTNLMachineID.PETROCHEMICAL_PLANT.ID, "PetrochemicalPlant", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PetrochemicalPlant.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SmeltingMixingFurnace
            .set(new SmeltingMixingFurnace(GTNLMachineID.SMELTING_MIXING_FURNACE.ID, "SmeltingMixingFurnace", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SmeltingMixingFurnace.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.WhiteNightGenerator
            .set(new WhiteNightGenerator(GTNLMachineID.WHITE_NIGHT_GENERATOR.ID, "WhiteNightGenerator", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.WhiteNightGenerator.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ProcessingArray.set(new ProcessingArray(GTNLMachineID.PROCESSING_ARRAY.ID, "ProcessingArray", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ProcessingArray.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.MegaBlastFurnace
            .set(new MegaBlastFurnace(GTNLMachineID.MEGA_BLAST_FURNACE.ID, "MegaBlastFurnace", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MegaBlastFurnace.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.BrickedBlastFurnace
            .set(new BrickedBlastFurnace(GTNLMachineID.BRICKED_BLAST_FURNACE.ID, "BrickedBlastFurnace", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.BrickedBlastFurnace.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.RareEarthCentrifugal
            .set(new RareEarthCentrifugal(GTNLMachineID.RARE_EARTH_CENTRIFUGAL.ID, "RareEarthCentrifugal", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.RareEarthCentrifugal.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.ColdIceFreezer.set(new ColdIceFreezer(GTNLMachineID.COLD_ICE_FREEZER.ID, "ColdIceFreezer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ColdIceFreezer.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.BlazeBlastFurnace
            .set(new BlazeBlastFurnace(GTNLMachineID.BLAZE_BLAST_FURNACE.ID, "BlazeBlastFurnace", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.BlazeBlastFurnace.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.ChemicalPlant.set(new ChemicalPlant(GTNLMachineID.CHEMICAL_PLANT.ID, "ChemicalPlant", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ChemicalPlant.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.VacuumFreezer.set(new VacuumFreezer(GTNLMachineID.VACUUM_FREEZER.ID, "VacuumFreezer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.VacuumFreezer.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.IndustrialArcaneAssembler.set(
            new IndustrialArcaneAssembler(
                GTNLMachineID.INDUSTRIAL_ARCANE_ASSEMBLER.ID,
                "IndustrialArcaneAssembler",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.IndustrialArcaneAssembler.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EnergeticPhotovoltaicPowerStation.set(
            new PhotovoltaicPowerStation.EnergeticPhotovoltaicPowerStation(
                GTNLMachineID.ENERGETIC_PHOTOVOLTAIC_POWER_STATION.ID,
                "EnergeticPhotovoltaicPowerStation",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.EnergeticPhotovoltaicPowerStation.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.AdvancedPhotovoltaicPowerStation.set(
            new PhotovoltaicPowerStation.AdvancedPhotovoltaicPowerStation(
                GTNLMachineID.ADVANCED_PHOTOVOLTAIC_POWER_STATION.ID,
                "AdvancedPhotovoltaicPowerStation",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.AdvancedPhotovoltaicPowerStation.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.VibrantPhotovoltaicPowerStation.set(
            new PhotovoltaicPowerStation.VibrantPhotovoltaicPowerStation(
                GTNLMachineID.VIBRANT_PHOTOVOLTAIC_POWER_STATION.ID,
                "VibrantPhotovoltaicPowerStation",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.VibrantPhotovoltaicPowerStation.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeMacerationTower
            .set(new LargeMacerationTower(GTNLMachineID.LARGE_MACERATION_TOWER.ID, "LargeMacerationTower", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeMacerationTower.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.HandOfJohnDavisonRockefeller.set(
            new HandOfJohnDavisonRockefeller(
                GTNLMachineID.HAND_OF_JOHN_DAVISON_ROCKEFELLER.ID,
                "HandOfJohnDavisonRockefeller",
                ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.HandOfJohnDavisonRockefeller.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeSiftingFunnel
            .set(new LargeSiftingFunnel(GTNLMachineID.LARGE_SIFTING_FUNNEL.ID, "LargeSiftingFunnel", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSiftingFunnel.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeCutter.set(new LargeCutter(GTNLMachineID.LARGE_CUTTER.ID, "LargeCutter", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeCutter.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeBrewer.set(new LargeBrewer(GTNLMachineID.LARGE_BREWER.ID, "LargeBrewer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeBrewer.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeIndustrialLathe
            .set(new LargeIndustrialLathe(GTNLMachineID.LARGE_INDUSTRIAL_LATHE.ID, "LargeIndustrialLathe", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeIndustrialLathe.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeMaterialPress
            .set(new LargeMaterialPress(GTNLMachineID.LARGE_MATERIAL_PRESS.ID, "LargeMaterialPress", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeMaterialPress.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeWiremill.set(new LargeWiremill(GTNLMachineID.LARGE_WIREMILL.ID, "LargeWiremill", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeWiremill.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeBender.set(new LargeBender(GTNLMachineID.LARGE_BENDER.ID, "LargeBender", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeBender.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.ElectricImplosionCompressor.set(
            new ElectricImplosionCompressor(
                GTNLMachineID.ELECTRIC_IMPLOSION_COMPRESSOR.ID,
                "ElectricImplosionCompressor",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ElectricImplosionCompressor.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.LargeExtruder.set(new LargeExtruder(GTNLMachineID.LARGE_EXTRUDER.ID, "LargeExtruder", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeExtruder.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeArcSmelter
            .set(new LargeArcSmelter(GTNLMachineID.LARGE_ARC_SMELTER.ID, "LargeArcSmelter", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeArcSmelter.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeForming.set(new LargeForming(GTNLMachineID.LARGE_FORMING.ID, "LargeForming", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeForming.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.MatterFabricator
            .set(new MatterFabricator(GTNLMachineID.MATTER_FABRICATOR.ID, "MatterFabricator", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MatterFabricator.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeElectrolyzer
            .set(new LargeElectrolyzer(GTNLMachineID.LARGE_ELECTROLYZER.ID, "LargeElectrolyzer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeElectrolyzer.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeElectromagnet
            .set(new LargeElectromagnet(GTNLMachineID.LARGE_ELECTROMAGNET.ID, "LargeElectromagnet", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeElectromagnet.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeAssembler.set(new LargeAssembler(GTNLMachineID.LARGE_ASSEMBLER.ID, "LargeAssembler", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeAssembler.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeMixer.set(new LargeMixer(GTNLMachineID.LARGE_MIXER.ID, "LargeMixer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeMixer.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeCentrifuge.set(new LargeCentrifuge(GTNLMachineID.LARGE_CENTRIFUGE.ID, "LargeCentrifuge", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeCentrifuge.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LibraryOfRuina.set(new LibraryOfRuina(GTNLMachineID.LIBRARY_OF_RUINA.ID, "LibraryOfRuina", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LibraryOfRuina.get(1), AnimatedText.GTNL_NLXCJH);

        GTNLItemList.LargeChemicalBath
            .set(new LargeChemicalBath(GTNLMachineID.LARGE_CHEMICAL_BATH.ID, "LargeChemicalBath", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeChemicalBath.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeAutoclave.set(new LargeAutoclave(GTNLMachineID.LARGE_AUTOCLAVE.ID, "LargeAutoclave", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeAutoclave.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeSolidifier.set(new LargeSolidifier(GTNLMachineID.LARGE_SOLIDIFIER.ID, "LargeSolidifier", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSolidifier.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeExtractor.set(new LargeExtractor(GTNLMachineID.LARGE_EXTRACTOR.ID, "LargeExtractor", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeExtractor.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.ReactionFurnace.set(new ReactionFurnace(GTNLMachineID.REACTION_FURNACE.ID, "ReactionFurnace", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ReactionFurnace.get(1), AnimatedText.GTNL_NLXCJH);

        GTNLItemList.EnergyInfuser.set(new EnergyInfuser(GTNLMachineID.ENERGY_INFUSER.ID, "EnergyInfuser", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EnergyInfuser.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.LargeCanning.set(new LargeCanning(GTNLMachineID.LARGE_CANNING.ID, "LargeCanning", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeCanning.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.Digester.set(new Digester(GTNLMachineID.DIGESTER.ID, "Digester", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.Digester.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.AlloyBlastSmelter
            .set(new AlloyBlastSmelter(GTNLMachineID.ALLOY_BLAST_SMELTER.ID, "AlloyBlastSmelter", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.AlloyBlastSmelter.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeSteamExtractor
            .set(new LargeSteamExtractor(GTNLMachineID.LARGE_STEAM_EXTRACTOR.ID, "LargeSteamExtractor", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamExtractor.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeSteamOreWasher
            .set(new LargeSteamOreWasher(GTNLMachineID.LARGE_STEAM_ORE_WASHER.ID, "LargeSteamOreWasher", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamOreWasher.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.LargeHammer.set(new LargeHammer(GTNLMachineID.LARGE_HAMMER.ID, "LargeHammer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeHammer.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.IsaMill.set(new IsaMill(GTNLMachineID.ISA_MILL.ID, "IsaMill", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.IsaMill.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.FlotationCellRegulator
            .set(new FlotationCellRegulator(GTNLMachineID.FLOTATION_CELL_REGULATOR.ID, "FlotationCellRegulator", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FlotationCellRegulator.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.VacuumDryingFurnace
            .set(new VacuumDryingFurnace(GTNLMachineID.VACUUM_DRYING_FURNACE.ID, "VacuumDryingFurnace", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.VacuumDryingFurnace.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeDistillery.set(new LargeDistillery(GTNLMachineID.LARGE_DISTILLERY.ID, "LargeDistillery", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeDistillery.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.Incubator.set(new Incubator(GTNLMachineID.INCUBATOR.ID, "Incubator", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.Incubator.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.LargeIncubator.set(new LargeIncubator(GTNLMachineID.LARGE_INCUBATOR.ID, "LargeIncubator", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeIncubator.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeEngravingLaser
            .set(new LargeEngravingLaser(GTNLMachineID.LARGE_ENGRAVING_LASER.ID, "LargeEngravingLaser", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeEngravingLaser.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.FishingGround.set(new FishingGround(GTNLMachineID.FISHING_GROUND.ID, "FishingGround", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FishingGround.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.ElementCopying.set(new ElementCopying(GTNLMachineID.ELEMENT_COPYING.ID, "ElementCopying", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ElementCopying.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.WoodDistillation
            .set(new WoodDistillation(GTNLMachineID.WOOD_DISTILLATION.ID, "WoodDistillation", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.WoodDistillation.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargePacker.set(new LargePacker(GTNLMachineID.LARGE_PACKER.ID, "LargePacker", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargePacker.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeAlloySmelter
            .set(new LargeAlloySmelter(GTNLMachineID.LARGE_ALLOY_SMELTER.ID, "LargeAlloySmelter", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeAlloySmelter.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.MolecularTransformer
            .set(new MolecularTransformer(GTNLMachineID.MOLECULAR_TRANSFORMER.ID, "MolecularTransformer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MolecularTransformer.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.LargePyrolyseOven
            .set(new LargePyrolyseOven(GTNLMachineID.LARGE_PYROLYSE_OVEN.ID, "LargePyrolyseOven", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargePyrolyseOven.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.LargeNaquadahReactor.set(
            new NaquadahReactor.LargeNaquadahReactor(
                GTNLMachineID.LARGE_NAQUADAH_REACTOR.ID,
                "LargeNaquadahReactor",
                ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeNaquadahReactor.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.DraconicFusionCrafting
            .set(new DraconicFusionCrafting(GTNLMachineID.DRACONIC_FUSION_CRAFTING.ID, "DraconicFusionCrafting", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DraconicFusionCrafting.get(1), AnimatedText.GTNL_NLXCJH);

        GTNLItemList.LargeSteamExtruder
            .set(new LargeSteamExtruder(GTNLMachineID.LARGE_STEAM_EXTRUDER.ID, "LargeSteamExtruder", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamExtruder.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.DecayHastener.set(new DecayHastener(GTNLMachineID.DECAY_HASTENER.ID, "DecayHastener", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DecayHastener.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.PreciseAssembler
            .set(new PrecisionAssembler(GTNLMachineID.PRECISION_ASSEMBLER.ID, "PrecisionAssembler", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PreciseAssembler.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.MegaAlloyBlastSmelter
            .set(new MegaAlloyBlastSmelter(GTNLMachineID.MEGA_ALLOY_BLAST_SMELTER.ID, "MegaAlloyBlastSmelter", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MegaAlloyBlastSmelter.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.GrandAssemblyLine
            .set(new GrandAssemblyLine(GTNLMachineID.GRAND_ASSEMBLY_LINE.ID, "GrandAssemblyLine", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.GrandAssemblyLine.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.FuelRefiningComplex
            .set(new FuelRefiningComplex(GTNLMachineID.FUEL_REFINING_COMPLEX.ID, "FuelRefiningComplex", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FuelRefiningComplex.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.ResourceCollectionModule.set(
            new ResourceCollectionModule(GTNLMachineID.RESOURCE_COLLECTION_MODULE.ID, "ResourceCollectionModule", ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ResourceCollectionModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LuvKuangBiaoOneGiantNuclearFusionReactor.set(
            new KuangBiaoOneGiantNuclearFusionReactor.LuVTier(
                GTNLMachineID.LUV_KUANG_BIAO_ONE_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoOneGiantNuclearFusionReactor",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.LuvKuangBiaoOneGiantNuclearFusionReactor.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.ZpmKuangBiaoTwoGiantNuclearFusionReactor.set(
            new KuangBiaoOneGiantNuclearFusionReactor.ZPMTier(
                GTNLMachineID.ZPM_KUANG_BIAO_TWO_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoTwoGiantNuclearFusionReactor",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.ZpmKuangBiaoTwoGiantNuclearFusionReactor.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.UvKuangBiaoThreeGiantNuclearFusionReactor.set(
            new KuangBiaoOneGiantNuclearFusionReactor.UVTier(
                GTNLMachineID.UV_KUANG_BIAO_THREE_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoThreeGiantNuclearFusionReactor",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.UvKuangBiaoThreeGiantNuclearFusionReactor.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.UhvKuangBiaoFourGiantNuclearFusionReactor.set(
            new KuangBiaoOneGiantNuclearFusionReactor.UHVTier(
                GTNLMachineID.UHV_KUANG_BIAO_FOUR_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoFourGiantNuclearFusionReactor",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.UhvKuangBiaoFourGiantNuclearFusionReactor.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.UevKuangBiaoFiveGiantNuclearFusionReactor.set(
            new KuangBiaoOneGiantNuclearFusionReactor.UEVTier(
                GTNLMachineID.UEV_KUANG_BIAO_FIVE_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoFiveGiantNuclearFusionReactor",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.UevKuangBiaoFiveGiantNuclearFusionReactor.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.LargeSteamCentrifuge
            .set(new LargeSteamCentrifuge(GTNLMachineID.LARGE_STEAM_CENTRIFUGE.ID, "LargeSteamCentrifuge", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamCentrifuge.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeSteamHammer
            .set(new LargeSteamHammer(GTNLMachineID.LARGE_STEAM_HAMMER.ID, "LargeSteamHammer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamHammer.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeSteamCompressor
            .set(new LargeSteamCompressor(GTNLMachineID.LARGE_STEAM_COMPRESSOR.ID, "LargeSteamCompressor", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamCompressor.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeSteamSifter
            .set(new LargeSteamSifter(GTNLMachineID.LARGE_STEAM_SIFTER.ID, "LargeSteamSifter", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamSifter.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeBoilerBronze
            .set(new LargeBoiler.LargeBoilerBronze(GTNLMachineID.LARGE_BOILER_BRONZE.ID, "LargeBoilerBronze", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeBoilerBronze.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeBoilerSteel
            .set(new LargeBoiler.LargeBoilerSteel(GTNLMachineID.LARGE_BOILER_STEEL.ID, "LargeBoilerSteel", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeBoilerSteel.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeBoilerTitanium.set(
            new LargeBoiler.LargeBoilerTitanium(GTNLMachineID.LARGE_BOILER_TITANIUM.ID, "LargeBoilerTitanium", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeBoilerTitanium.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeBoilerTungstenSteel.set(
            new LargeBoiler.LargeBoilerTungstenSteel(
                GTNLMachineID.LARGE_BOILER_TUNGSTEN_STEEL.ID,
                "LargeBoilerTungstenSteel",
                ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeBoilerTungstenSteel.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.LargeSteamFormingPress
            .set(new LargeSteamFormingPress(GTNLMachineID.LARGE_STEAM_FORMING_PRESS.ID, "LargeSteamFormingPress", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamFormingPress.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LargeSteamMixer
            .set(new LargeSteamMixer(GTNLMachineID.LARGE_STEAM_MIXER.ID, "LargeSteamMixer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamMixer.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.CrackerHub.set(new CrackerHub(GTNLMachineID.CRACKER_HUB.ID, "CrackerHub", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.CrackerHub.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.AdvancedInfiniteDriller.set(
            new AdvancedInfiniteDriller(GTNLMachineID.ADVANCED_INFINITE_DRILLER.ID, "AdvancedInfiniteDriller", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.AdvancedInfiniteDriller.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.ElectricBlastFurnace
            .set(new ElectricBlastFurnace(GTNLMachineID.ELECTRIC_BLAST_FURNACE.ID, "ElectricBlastFurnace", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ElectricBlastFurnace.get(1), AnimatedText.GTNL_SRP);

        GTNLItemList.PlatinumBasedTreatment
            .set(new PlatinumBasedTreatment(GTNLMachineID.PLATINUM_BASED_TREATMENT.ID, "PlatinumBasedTreatment", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PlatinumBasedTreatment.get(1), AnimatedText.GTNL_PBTR);

        GTNLItemList.ShallowChemicalCoupling.set(
            new ShallowChemicalCoupling(GTNLMachineID.SHALLOW_CHEMICAL_COUPLING.ID, "ShallowChemicalCoupling", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ShallowChemicalCoupling.get(1), AnimatedText.GTNL_SCCR);

        GTNLItemList.Steamgate.set(new Steamgate(GTNLMachineID.STEAMGATE.ID, "Steamgate", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.Steamgate.get(1), AnimatedText.GT_NOT_LEISURE);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.Steamgate.get(1), AnimatedText.SteamgateCredits);

        GTNLItemList.SteamGateAssembler
            .set(new SteamGateAssembler(GTNLMachineID.STEAM_GATE_ASSEMBLER.ID, "SteamGateAssembler", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamGateAssembler.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.MegaSteamCompressor
            .set(new MegaSteamCompressor(GTNLMachineID.MEGA_STEAM_COMPRESSOR.ID, "MegaSteamCompressor", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MegaSteamCompressor.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.MegaSolarBoiler
            .set(new MegaSolarBoiler(GTNLMachineID.MEGA_SOLAR_BOILER.ID, "MegaSolarBoiler", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MegaSolarBoiler.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamCactusWonder
            .set(new SteamCactusWonder(GTNLMachineID.STEAM_CACTUS_WONDER.ID, "SteamCactusWonder", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamCactusWonder.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamCarpenter.set(new SteamCarpenter(GTNLMachineID.STEAM_CARPENTER.ID, "SteamCarpenter", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamCarpenter.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamLavaMaker.set(new SteamLavaMaker(GTNLMachineID.STEAM_LAVA_MAKER.ID, "SteamLavaMaker", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamLavaMaker.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamManufacturer
            .set(new SteamManufacturer(GTNLMachineID.STEAM_MANUFACTURER.ID, "SteamManufacturer", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamManufacturer.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamRockBreaker
            .set(new SteamRockBreaker(GTNLMachineID.STEAM_ROCK_BREAKER.ID, "SteamRockBreaker", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamRockBreaker.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamWoodcutter.set(new SteamWoodcutter(GTNLMachineID.STEAM_WOODCUTTER.ID, "SteamWoodcutter", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamWoodcutter.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamExtractinator
            .set(new SteamExtractinator(GTNLMachineID.STEAM_EXTRACTINATOR.ID, "SteamExtractinator", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamExtractinator.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamFusionReactor
            .set(new SteamFusionReactor(GTNLMachineID.STEAM_FUSION_REACTOR.ID, "SteamFusionReactor", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamFusionReactor.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HighPressureSteamFusionReactor.set(
            new HighPressureSteamFusionReactor(
                GTNLMachineID.HIGH_PRESSURE_STEAM_FUSION_REACTOR.ID,
                "HighPressureSteamFusionReactor",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HighPressureSteamFusionReactor.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamInfernalCokeOven
            .set(new SteamInfernalCokeOven(GTNLMachineID.STEAM_INFERNAL_COKE_OVEN.ID, "SteamInfernalCokeOven", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamInfernalCokeOven.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.IntegratedAssemblyFacility.set(
            new IntegratedAssemblyFacility(
                GTNLMachineID.INTEGRATED_ASSEMBLY_FACILITY.ID,
                "IntegratedAssemblyFacility",
                ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.IntegratedAssemblyFacility.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.TreeDiagram.set(new TreeDiagram(GTNLMachineID.TREE_DIAGRAM.ID, "TreeDiagram", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.TreeDiagram.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NanoPhagocytosisPlant
            .set(new NanoPhagocytosisPlant(GTNLMachineID.NANO_PHAGOCYTOSIS_PLANT.ID, "NanoPhagocytosisPlant", ""));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NanoPhagocytosisPlant.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MagneticEnergyReactionFurnace.set(
            new MagneticEnergyReactionFurnace(
                GTNLMachineID.MAGNETIC_ENERGY_REACTION_FURNACE.ID,
                "MagneticEnergyReactionFurnace",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.MagneticEnergyReactionFurnace.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.NanitesIntegratedProcessingCenter.set(
            new NanitesIntegratedProcessingCenter(
                GTNLMachineID.NANITES_INTEGRATED_PROCESSING_CENTER.ID,
                "NanitesIntegratedProcessingCenter",
                ""));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.NanitesIntegratedProcessingCenter.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.BioengineeringModule.set(
            new BioengineeringModule(
                GTNLMachineID.BIOENGINEERING_MODULE.ID,
                "BioengineeringModule",
                StatCollector.translateToLocal("gtnl.machine.bioengineering_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.BioengineeringModule.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.PolymerTwistingModule.set(
            new PolymerTwistingModule(
                GTNLMachineID.POLYMER_TWISTING_MODULE.ID,
                "PolymerTwistingModule",
                StatCollector.translateToLocal("gtnl.machine.polymer_twisting_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PolymerTwistingModule.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.OreExtractionModule.set(
            new OreExtractionModule(
                GTNLMachineID.ORE_EXTRACTION_MODULE.ID,
                "OreExtractionModule",
                StatCollector.translateToLocal("gtnl.machine.ore_extraction_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.OreExtractionModule.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SuperSpaceElevator.set(
            new SuperSpaceElevator(
                GTNLMachineID.SUPER_SPACE_ELEVATOR.ID,
                "SuperSpaceElevator",
                StatCollector.translateToLocal("gtnl.machine.super_space_elevator.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SuperSpaceElevator.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeBioLab.set(
            new LargeBioLab(
                GTNLMachineID.LARGE_BIO_LAB.ID,
                "LargeBioLab",
                StatCollector.translateToLocal("gtnl.machine.large_bio_lab.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeBioLab.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LargeGasCollector.set(
            new LargeGasCollector(
                GTNLMachineID.LARGE_GAS_COLLECTOR.ID,
                "LargeGasCollector",
                StatCollector.translateToLocal("gtnl.machine.large_gas_collector.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeGasCollector.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EternalGregTechWorkshop.set(
            new EternalGregTechWorkshop(
                GTNLMachineID.ETERNAL_GREG_TECH_WORKSHOP.ID,
                "EternalGregTechWorkshop",
                StatCollector.translateToLocal("gtnl.machine.eternal_greg_tech_workshop.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EternalGregTechWorkshop.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EGTWFusionModule.set(
            new EGTWFusionModule(
                GTNLMachineID.EGTW_FUSION_MODULE.ID,
                "EGTWFusionModule",
                StatCollector.translateToLocal("gtnl.machine.etgw_fusion_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EGTWFusionModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SpaceAssembler.set(
            new SpaceAssembler(
                GTNLMachineID.SPACE_ASSEMBLER.ID,
                "SpaceAssembler",
                StatCollector.translateToLocal("gtnl.machine.space_assembler.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SpaceAssembler.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EngravingLaserPlant.set(
            new EngravingLaserPlant(
                GTNLMachineID.ENGRAVING_LASER_PLANT.ID,
                "EngravingLaserPlant",
                StatCollector.translateToLocal("gtnl.machine.engraving_laser_plant.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EngravingLaserPlant.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.VortexMatterCentrifuge.set(
            new VortexMatterCentrifuge(
                GTNLMachineID.VORTEX_MATTER_CENTRIFUGE.ID,
                "VortexMatterCentrifuge",
                StatCollector.translateToLocal("gtnl.machine.vortex_matter_centrifuge.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.VortexMatterCentrifuge.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SuperconductingElectromagnetism.set(
            new SuperconductingElectromagnetism(
                GTNLMachineID.SUPERCONDUCTING_ELECTROMAGNETISM.ID,
                "SuperconductingElectromagnetism",
                StatCollector.translateToLocal("gtnl.machine.superconducting_electromagnetism.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SuperconductingElectromagnetism.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.FieldForgePress.set(
            new FieldForgePress(
                GTNLMachineID.FIELD_FORGE_PRESS.ID,
                "FieldForgePress",
                StatCollector.translateToLocal("gtnl.machine.field_forge_press.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FieldForgePress.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SuperconductingMagneticPresser.set(
            new SuperconductingMagneticPresser(
                GTNLMachineID.SUPERCONDUCTING_MAGNETIC_PRESSER.ID,
                "SuperconductingMagneticPresser",
                StatCollector.translateToLocal("gtnl.machine.superconducting_magnetic_presser.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SuperconductingMagneticPresser.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.HeavyRolling.set(
            new HeavyRolling(
                GTNLMachineID.HEAVY_ROLLING.ID,
                "HeavyRolling",
                StatCollector.translateToLocal("gtnl.machine.heavy_rolling.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.HeavyRolling.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.HighEnergyLaserLathe.set(
            new HighEnergyLaserLathe(
                GTNLMachineID.HIGH_ENERGY_LASER_LATHE.ID,
                "HighEnergyLaserLathe",
                StatCollector.translateToLocal("gtnl.machine.high_energy_laser_lathe.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.HighEnergyLaserLathe.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MegaMixer.set(
            new MegaMixer(
                GTNLMachineID.MEGA_MIXER.ID,
                "MegaMixer",
                StatCollector.translateToLocal("gtnl.machine.mega_mixer.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MegaMixer.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SteamBeaconModuleI.set(
            new SteamBeaconModule(
                GTNLMachineID.STEAM_BEACON_MODULE_I.ID,
                "SteamBeaconModuleI",
                StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.1.name"),
                1));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamBeaconModuleI.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamBeaconModuleII.set(
            new SteamBeaconModule(
                GTNLMachineID.STEAM_BEACON_MODULE_II.ID,
                "SteamBeaconModuleII",
                StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.2.name"),
                2));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamBeaconModuleII.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamBeaconModuleIII.set(
            new SteamBeaconModule(
                GTNLMachineID.STEAM_BEACON_MODULE_III.ID,
                "SteamBeaconModuleIII",
                StatCollector.translateToLocal("gtnl.machine.steam_beacon_module.3.name"),
                3));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamBeaconModuleIII.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NanitesCircuitAssemblyFactory.set(
            new NanitesCircuitAssemblyFactory(
                GTNLMachineID.NANITES_CIRCUIT_ASSEMBLY_FACTORY.ID,
                "NanitesCircuitAssemblyFactory",
                StatCollector.translateToLocal("gtnl.machine.nanites_circuit_assembly_factory.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.NanitesCircuitAssemblyFactory.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.ETGWEyeOfHarmonyModule.set(
            new ETGWEyeOfHarmonyModule(
                GTNLMachineID.ETGW_EYE_OF_HARMONY_MODULE.ID,
                "ETGWEyeOfHarmonyModule",
                StatCollector.translateToLocal("gtnl.machine.etgw_eye_of_harmony_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ETGWEyeOfHarmonyModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.AetronPressor.set(
            new AetronPressor(
                GTNLMachineID.AETRON_PRESSOR.ID,
                "AetronPressor",
                StatCollector.translateToLocal("gtnl.machine.aetron_pressor.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.AetronPressor.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SteamElevator.set(
            new SteamElevator(
                GTNLMachineID.STEAM_ELEVATOR.ID,
                "SteamElevator",
                StatCollector.translateToLocal("gtnl.machine.steam_elevator.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamElevator.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamMonsterRepellentModuleI.set(
            new SteamMonsterRepellentModule(
                GTNLMachineID.STEAM_MONSTER_REPELLENT_MODULE_I.ID,
                "SteamMonsterRepellentModuleI",
                StatCollector.translateToLocal("gtnl.machine.steam_monster_repellent_module.1.name"),
                1));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SteamMonsterRepellentModuleI.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamMonsterRepellentModuleII.set(
            new SteamMonsterRepellentModule(
                GTNLMachineID.STEAM_MONSTER_REPELLENT_MODULE_II.ID,
                "SteamMonsterRepellentModuleII",
                StatCollector.translateToLocal("gtnl.machine.steam_monster_repellent_module.2.name"),
                2));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SteamMonsterRepellentModuleII.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamMonsterRepellentModuleIII.set(
            new SteamMonsterRepellentModule(
                GTNLMachineID.STEAM_MONSTER_REPELLENT_MODULE_III.ID,
                "SteamMonsterRepellentModuleIII",
                StatCollector.translateToLocal("gtnl.machine.steam_monster_repellent_module.3.name"),
                3));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SteamMonsterRepellentModuleIII.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamFlightModule.set(
            new SteamFlightModule(
                GTNLMachineID.STEAM_FLIGHT_MODULE.ID,
                "SteamFlightModule",
                StatCollector.translateToLocal("gtnl.machine.steam_flight_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamFlightModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamWeatherModule.set(
            new SteamWeatherModule(
                GTNLMachineID.STEAM_WEATHER_MODULE.ID,
                "SteamWeatherModule",
                StatCollector.translateToLocal("gtnl.machine.steam_weather_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamWeatherModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.NanoAssemblerMarkL.set(
            new NanoAssemblerMarkL(
                GTNLMachineID.NANO_ASSEMBLER_MARK_L.ID,
                "NanoAssemblerMarkL",
                StatCollector.translateToLocal("gtnl.machine.nano_assembler_mark_l.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NanoAssemblerMarkL.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.HighPerformanceComputationArray.set(
            new HighPerformanceComputationArray(
                GTNLMachineID.HIGH_PERFORMANCE_COMPUTATION_ARRAY.ID,
                "HighPerformanceComputationArray",
                StatCollector.translateToLocal("gtnl.machine.high_performance_computation_array.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.HighPerformanceComputationArray.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.EyeOfHarmonyInjector.set(
            new EyeOfHarmonyInjector(
                GTNLMachineID.EYE_OF_HARMONY_INJECTOR.ID,
                "EyeOfHarmonyInjector",
                StatCollector.translateToLocal("gtnl.machine.eye_of_harmony_injector.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.EyeOfHarmonyInjector.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.CompoundExtremeCoolingUnit.set(
            new CompoundExtremeCoolingUnit(
                GTNLMachineID.COMPOUND_EXTREME_COOLING_UNIT.ID,
                "CompoundExtremeCoolingUnit",
                StatCollector.translateToLocal("gtnl.machine.compound_extreme_cooling_unit.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.CompoundExtremeCoolingUnit.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SteamOreProcessorModule.set(
            new SteamOreProcessorModule(
                GTNLMachineID.STEAM_ORE_PROCESSOR_MODULE.ID,
                "SteamOreProcessorModule",
                StatCollector.translateToLocal("gtnl.machine.steam_ore_processor_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamOreProcessorModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LargeSteamLathe.set(
            new LargeSteamLathe(
                GTNLMachineID.LARGE_STEAM_LATHE.ID,
                "LargeSteamLathe",
                StatCollector.translateToLocal("gtnl.machine.large_steam_lathe.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamLathe.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeSteamCutting.set(
            new LargeSteamCutting(
                GTNLMachineID.LARGE_STEAM_CUTTING.ID,
                "LargeSteamCutting",
                StatCollector.translateToLocal("gtnl.machine.large_steam_cutting.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamCutting.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SteamItemVault.set(
            new SteamItemVault(
                GTNLMachineID.STEAM_ITEM_VAULT.ID,
                "SteamItemVault",
                StatCollector.translateToLocal("gtnl.machine.steam_item_vault.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamItemVault.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.PrimitiveBrickKiln.set(
            new PrimitiveBrickKiln(
                GTNLMachineID.PRIMITIVE_BRICK_KILN.ID,
                "PrimitiveBrickKiln",
                StatCollector.translateToLocal("gtnl.machine.primitive_brick_kiln.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PrimitiveBrickKiln.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SingularityDataHub.set(
            new SingularityDataHub(
                GTNLMachineID.SINGULARITY_DATA_HUB.ID,
                "SingularityDataHub",
                StatCollector.translateToLocal("gtnl.machine.singularity_data_hub.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SingularityDataHub.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ElectrocellGenerator.set(
            new ElectrocellGenerator(
                GTNLMachineID.ELECTROCELL_GENERATOR.ID,
                "ElectrocellGenerator",
                StatCollector.translateToLocal("gtnl.machine.electrocell_generator.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ElectrocellGenerator.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.FOGAlloySmelterModule.set(
            new FOGAlloySmelterModule(
                GTNLMachineID.FOG_ALLOY_SMELTER_MODULE.ID,
                "FOGAlloySmelterModule",
                StatCollector.translateToLocal("gtnl.machine.fog_alloy_smelter_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FOGAlloySmelterModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.FOGAlloyBlastSmelterModule.set(
            new FOGAlloyBlastSmelterModule(
                GTNLMachineID.FOG_ALLOY_BLAST_SMELTER_MODULE.ID,
                "FOGAlloyBlastSmelterModule",
                StatCollector.translateToLocal("gtnl.machine.fog_alloy_blast_smelter_module.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.FOGAlloyBlastSmelterModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.FOGExtractorModule.set(
            new FOGExtractorModule(
                GTNLMachineID.FOG_EXTRACTOR_MODULE.ID,
                "FOGExtractorModule",
                StatCollector.translateToLocal("gtnl.machine.fog_extractor_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FOGExtractorModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamEntityCrusherModule.set(
            new SteamEntityCrusherModule(
                GTNLMachineID.STEAM_ENTITY_CRUSHER_MODULE.ID,
                "SteamEntityCrusherModule",
                StatCollector.translateToLocal("gtnl.machine.steam_entity_crusher_module.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.SteamEntityCrusherModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamApiaryModule.set(
            new SteamApiaryModule(
                GTNLMachineID.STEAM_APIARY_MODULE.ID,
                "SteamApiaryModule",
                StatCollector.translateToLocal("gtnl.machine.steam_apiary_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamApiaryModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamBeeBreedingModule.set(
            new SteamBeeBreedingModule(
                GTNLMachineID.STEAM_BEE_BREEDING_MODULE.ID,
                "SteamBeeBreedingModule",
                StatCollector.translateToLocal("gtnl.machine.steam_bee_breeding_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamBeeBreedingModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamGreenhouseModule.set(
            new SteamGreenhouseModule(
                GTNLMachineID.STEAM_GREENHOUSE_MODULE.ID,
                "SteamGreenhouseModule",
                StatCollector.translateToLocal("gtnl.machine.steam_greenhouse_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamGreenhouseModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.RocketAssembler.set(
            new RocketAssembler(
                GTNLMachineID.ROCKET_ASSEMBLER.ID,
                "RocketAssembler",
                StatCollector.translateToLocal("gtnl.machine.rocket_assembler.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.RocketAssembler.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SteamOilDrillModuleI.set(
            new SteamOilDrillModule(
                GTNLMachineID.STEAM_OIL_DRILL_MODULE_I.ID,
                "SteamOilDrillModuleI",
                StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.1.name"),
                2));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamOilDrillModuleI.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamOilDrillModuleII.set(
            new SteamOilDrillModule(
                GTNLMachineID.STEAM_OIL_DRILL_MODULE_II.ID,
                "SteamOilDrillModuleII",
                StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.2.name"),
                3));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamOilDrillModuleII.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.SteamOilDrillModuleIII.set(
            new SteamOilDrillModule(
                GTNLMachineID.STEAM_OIL_DRILL_MODULE_III.ID,
                "SteamOilDrillModuleIII",
                StatCollector.translateToLocal("gtnl.machine.steam_oil_drill_module.3.name"),
                4));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SteamOilDrillModuleIII.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.AdvancedRareEarthCentrifugal.set(
            new AdvancedRareEarthCentrifugal(
                GTNLMachineID.ADVANCED_RARE_EARTH_CENTRIFUGAL.ID,
                "AdvancedRareEarthCentrifugal",
                StatCollector.translateToLocal("gtnl.machine.advanced_rare_earth_centrifugal.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.AdvancedRareEarthCentrifugal.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MassFabricator.set(
            new MassFabricator(
                GTNLMachineID.MASS_FABRICATOR.ID,
                "MassFabricator",
                StatCollector.translateToLocal("gtnl.machine.mass_fabricator.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MassFabricator.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.AdvancedMassFabricator.set(
            new AdvancedMassFabricator(
                GTNLMachineID.ADVANCED_MASS_FABRICATOR.ID,
                "AdvancedMassFabricator",
                StatCollector.translateToLocal("gtnl.machine.advanced_mass_fabricator.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.AdvancedMassFabricator.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.HorizontalCompressor.set(
            new HorizontalCompressor(
                GTNLMachineID.HORIZONTAL_COMPRESSOR.ID,
                "HorizontalCompressor",
                StatCollector.translateToLocal("gtnl.machine.horizontal_compressor.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.HorizontalCompressor.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MegaVacuumDryingFurnace.set(
            new MegaVacuumDryingFurnace(
                GTNLMachineID.MEGA_VACUUM_DRYING_FURNACE.ID,
                "MegaVacuumDryingFurnace",
                StatCollector.translateToLocal("gtnl.machine.mega_vacuum_drying_furnace.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MegaVacuumDryingFurnace.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MegaBathTank.set(
            new MegaBathTank(
                GTNLMachineID.MEGA_BATH_TANK.ID,
                "MegaBathTank",
                StatCollector.translateToLocal("gtnl.machine.mega_bath_tank.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MegaBathTank.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MegaCanner.set(
            new MegaCanner(
                GTNLMachineID.MEGA_CANNER.ID,
                "MegaCanner",
                StatCollector.translateToLocal("gtnl.machine.mega_canner.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MegaCanner.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.CompoundDistillationFractionator.set(
            new CompoundDistillationFractionator(
                GTNLMachineID.COMPOUND_DISTILLATION_FRACTIONATOR.ID,
                "CompoundDistillationFractionator",
                StatCollector.translateToLocal("gtnl.machine.compound_distillation_fractionator.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.CompoundDistillationFractionator.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MegaBrewer.set(
            new MegaBrewer(
                GTNLMachineID.MEGA_BREWER.ID,
                "MegaBrewer",
                StatCollector.translateToLocal("gtnl.machine.mega_brewer.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MegaBrewer.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MicroorganismMaster.set(
            new MicroorganismMaster(
                GTNLMachineID.MICROORGANISM_MASTER.ID,
                "MicroorganismMaster",
                StatCollector.translateToLocal("gtnl.machine.microorganism_master.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MicroorganismMaster.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MoltenCore.set(
            new MoltenCore(
                GTNLMachineID.MOLTEN_CORE.ID,
                "MoltenCore",
                StatCollector.translateToLocal("gtnl.machine.molten_core.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MoltenCore.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.CrystalBuilder.set(
            new CrystalBuilder(
                GTNLMachineID.CRYSTAL_BUILDER.ID,
                "CrystalBuilder",
                StatCollector.translateToLocal("gtnl.machine.crystal_builder.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.CrystalBuilder.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeRockCrusher.set(
            new LargeRockCrusher(
                GTNLMachineID.LARGE_ROCK_CRUSHER.ID,
                "LargeRockCrusher",
                StatCollector.translateToLocal("gtnl.machine.large_rock_crusher.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeRockCrusher.get(1), AnimatedText.GTNL_QYZG_SRP);

        GTNLItemList.PhaseChangeCube.set(
            new PhaseChangeCube(
                GTNLMachineID.PHASE_CHANGE_CUBE.ID,
                "PhaseChangeCube",
                StatCollector.translateToLocal("gtnl.machine.phase_change_cube.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PhaseChangeCube.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MantleCrusher.set(
            new MantleCrusher(
                GTNLMachineID.MANTLE_CRUSHER.ID,
                "MantleCrusher",
                StatCollector.translateToLocal("gtnl.machine.mantle_crusher.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MantleCrusher.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SmartSiftingHub.set(
            new SmartSiftingHub(
                GTNLMachineID.SMART_SIFTING_HUB.ID,
                "SmartSiftingHub",
                StatCollector.translateToLocal("gtnl.machine.smart_sifting_hub.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SmartSiftingHub.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.GiantElectrochemicalWorkstation.set(
            new GiantElectrochemicalWorkstation(
                GTNLMachineID.GIANT_ELECTROCHEMICAL_WORKSTATION.ID,
                "GiantElectrochemicalWorkstation",
                StatCollector.translateToLocal("gtnl.machine.giant_electrochemical_workstation.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.GiantElectrochemicalWorkstation.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.GeminiContainmentSystem.set(
            new GeminiContainmentSystem(
                GTNLMachineID.GEMINI_CONTAINMENT_SYSTEM.ID,
                "GeminiContainmentSystem",
                StatCollector.translateToLocal("gtnl.machine.gemini_containment_system.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.GeminiContainmentSystem.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.ExtremeCompressor.set(
            new ExtremeCompressor(
                GTNLMachineID.EXTREME_COMPRESSOR.ID,
                "ExtremeCompressor",
                StatCollector.translateToLocal("gtnl.machine.extreme_compressor.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ExtremeCompressor.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.ExtremeElectricFurnace.set(
            new ExtremeElectricFurnace(
                GTNLMachineID.EXTREME_ELECTRIC_FURNACE.ID,
                "ExtremeElectricFurnace",
                StatCollector.translateToLocal("gtnl.machine.extreme_electric_furnace.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ExtremeElectricFurnace.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.DataCenter.set(
            new DataCenter(
                GTNLMachineID.DATA_CENTER.ID,
                "DataCenter",
                StatCollector.translateToLocal("gtnl.machine.data_center.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DataCenter.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.KerrNewmanHomogenizer.set(
            new KerrNewmanHomogenizer(
                GTNLMachineID.KERR_NEWMAN_HOMOGENIZER.ID,
                "KerrNewmanHomogenizer",
                StatCollector.translateToLocal("gtnl.machine.kerr_newman_homogenizer.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.KerrNewmanHomogenizer.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.LargeSteamBending.set(
            new LargeSteamBending(
                GTNLMachineID.LARGE_STEAM_BENDING.ID,
                "LargeSteamBending",
                StatCollector.translateToLocal("gtnl.machine.large_steam_bending.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamBending.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LargeSteamWiremill.set(
            new LargeSteamWiremill(
                GTNLMachineID.LARGE_STEAM_WIREMILL.ID,
                "LargeSteamWiremill",
                StatCollector.translateToLocal("gtnl.machine.large_steam_wiremill.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeSteamWiremill.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.ChemicalComplex.set(
            new ChemicalComplex(
                GTNLMachineID.CHEMICAL_COMPLEX.ID,
                "ChemicalComplex",
                StatCollector.translateToLocal("gtnl.machine.chemical_complex.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.ChemicalComplex.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MegaWiremill.set(
            new MegaWiremill(
                GTNLMachineID.MEGA_WIREMILL.ID,
                "MegaWiremill",
                StatCollector.translateToLocal("gtnl.machine.mega_wiremill.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.MegaWiremill.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.GiantFlotationTank.set(
            new GiantFlotationTank(
                GTNLMachineID.GIANT_FLOTATION_TANK.ID,
                "GiantFlotationTank",
                StatCollector.translateToLocal("gtnl.machine.giant_flotation_tank.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.GiantFlotationTank.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.DissolutionCore.set(
            new DissolutionCore(
                GTNLMachineID.DISSOLUTION_CORE.ID,
                "DissolutionCore",
                StatCollector.translateToLocal("gtnl.machine.dissolution_core.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.DissolutionCore.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SwarmCore.set(
            new SwarmCore(
                GTNLMachineID.SWARM_CORE.ID,
                "SwarmCore",
                StatCollector.translateToLocal("gtnl.machine.swarm_core.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SwarmCore.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.HyperNaquadahReactor.set(
            new NaquadahReactor.HyperNaquadahReactor(
                GTNLMachineID.HYPER_NAQUADAH_REACTOR.ID,
                "HyperNaquadahReactor",
                StatCollector.translateToLocal("gtnl.machine.hyper_naquadah_reactor.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.HyperNaquadahReactor.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.AdvancedHyperNaquadahReactor.set(
            new NaquadahReactor.AdvancedHyperNaquadahReactor(
                GTNLMachineID.ADVANCED_HYPER_NAQUADAH_REACTOR.ID,
                "AdvancedHyperNaquadahReactor",
                StatCollector.translateToLocal("gtnl.machine.advanced_hyper_naquadah_reactor.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.AdvancedHyperNaquadahReactor.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.TransliminalOasis.set(
            new TransliminalOasis(
                GTNLMachineID.TRANSLIMINAL_OASIS.ID,
                "TransliminalOasis",
                StatCollector.translateToLocal("gtnl.machine.transliminal_oasis.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.TransliminalOasis.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.FastNeutronBreederReactor.set(
            new FastNeutronBreederReactor(
                GTNLMachineID.FAST_NEUTRON_BREEDER_REACTOR.ID,
                "FastNeutronBreederReactor",
                StatCollector.translateToLocal("gtnl.machine.fast_neutron_breeder_reactor.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FastNeutronBreederReactor.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.MagneticConfinementDimensionalityShockDevice.set(
            new MagneticConfinementDimensionalityShockDevice(
                GTNLMachineID.MAGNETIC_CONFINEMENT_DIMENSIONALITY_SHOCK_DEVICE.ID,
                "MagneticConfinementDimensionalityShockDevice",
                StatCollector.translateToLocal("gtnl.machine.magnetic_confinement_dimensionality_shock_device.name")));
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.MagneticConfinementDimensionalityShockDevice.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.SupercomputingCenter.set(
            new SupercomputingCenter(
                GTNLMachineID.SUPERCOMPUTING_CENTER.ID,
                "SupercomputingCenter",
                StatCollector.translateToLocal("gtnl.machine.supercomputing_center.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.SupercomputingCenter.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.PCBFactory.set(
            new PCBFactory(
                GTNLMachineID.PCB_FACTORY.ID,
                "PCBFactory",
                StatCollector.translateToLocal("gtnl.machine.pcb_factory.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.PCBFactory.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.FurnaceArray.set(
            new FurnaceArray(
                GTNLMachineID.FURNACE_ARRAY.ID,
                "FurnaceArray",
                StatCollector.translateToLocal("gtnl.machine.furnace_array.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FurnaceArray.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.FOGSolarMuonCatalystModule.set(
            new FOGSolarMuonCatalystModule(
                GTNLMachineID.FOG_SOLAR_MUON_CATALYST_MODULE.ID,
                "FOGSolarMuonCatalystModule",
                StatCollector.translateToLocal("gtnl.machine.fog_solar_muon_catalyst_module.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FOGSolarMuonCatalystModule.get(1), AnimatedText.TIDAL);
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.FOGSolarMuonCatalystModule.get(1), AnimatedText.WAVE);
        AnimatedTooltipHandler
            .addItemTooltip(GTNLItemList.FOGSolarMuonCatalystModule.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.LargeEssentiaGenerator.set(
            new LargeEssentiaGenerator(
                GTNLMachineID.LARGE_ESSENTIA_GENERATOR.ID,
                "NameLargeEssentiaGenerator",
                StatCollector.translateToLocal("gtnl.machine.large_essentia_generator.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.LargeEssentiaGenerator.get(1), AnimatedText.GT_NOT_LEISURE);

        GTNLItemList.HighwayToHell.set(
            new HighwayToHell(
                GTNLMachineID.HIGHWAY_TO_HELL.ID,
                "NameHighwayToHell",
                StatCollector.translateToLocal("gtnl.machine.highway_to_hell.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.HighwayToHell.get(1), AnimatedText.GT_NOT_LEISURE);

        // Special Machine
        GTNLItemList.CheatOreProcessingFactory.set(
            new CheatOreProcessingFactory(
                GTNLMachineID.CHEAT_ORE_PROCESSING_FACTORY.ID,
                "CheatOreProcessingFactory",
                StatCollector.translateToLocal("gtnl.machine.cheat_ore_processing_factory.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.CheatOreProcessingFactory.get(1), AnimatedText.GTNL_QYZG);

        GTNLItemList.NineIndustrialMultiMachine.set(
            new NineIndustrialMultiMachine(
                GTNLMachineID.NINE_INDUSTRIAL_MULTI_MACHINE.ID,
                "NineIndustrialMultiMachine",
                StatCollector.translateToLocal("gtnl.machine.nine_industrial_multi_machine.name")));
        AnimatedTooltipHandler.addItemTooltip(GTNLItemList.NineIndustrialMultiMachine.get(1), AnimatedText.GTNL_QYZG);
    }
}
