package com.science.gtnl.loader;

import static com.science.gtnl.utils.enums.GTNLMachineID.*;
import static com.science.gtnl.utils.text.AnimatedTooltipHandler.*;
import static gregtech.api.enums.Textures.BlockIcons.*;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;

import com.google.common.collect.ImmutableSet;
import com.science.gtnl.common.machine.basicMachine.DebugResearchStation;
import com.science.gtnl.common.machine.basicMachine.DieselGenerator;
import com.science.gtnl.common.machine.basicMachine.Enchanting;
import com.science.gtnl.common.machine.basicMachine.GasTurbine;
import com.science.gtnl.common.machine.basicMachine.LootBagRedemption;
import com.science.gtnl.common.machine.basicMachine.ManaTank;
import com.science.gtnl.common.machine.basicMachine.Replicator;
import com.science.gtnl.common.machine.basicMachine.SteamAssemblerBronze;
import com.science.gtnl.common.machine.basicMachine.SteamAssemblerSteel;
import com.science.gtnl.common.machine.basicMachine.SteamTurbine;
import com.science.gtnl.common.machine.cover.VoidCover;
import com.science.gtnl.common.machine.cover.WirelessMultiEnergyCover;
import com.science.gtnl.common.machine.cover.WirelessSteamCover;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.hatch.CustomMaintenanceHatch;
import com.science.gtnl.common.machine.hatch.DebugDataAccessHatch;
import com.science.gtnl.common.machine.hatch.DebugEnergyHatch;
import com.science.gtnl.common.machine.hatch.DualInputHatch;
import com.science.gtnl.common.machine.hatch.EnergyTransferNode;
import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;
import com.science.gtnl.common.machine.hatch.HumongousDualInputHatch;
import com.science.gtnl.common.machine.hatch.HumongousNinefoldInputHatch;
import com.science.gtnl.common.machine.hatch.HumongousOutputBus;
import com.science.gtnl.common.machine.hatch.HumongousSolidifierHatch;
import com.science.gtnl.common.machine.hatch.ManaDynamoHatch;
import com.science.gtnl.common.machine.hatch.ManaEnergyHatch;
import com.science.gtnl.common.machine.hatch.NanitesInputBus;
import com.science.gtnl.common.machine.hatch.NinefoldInputHatch;
import com.science.gtnl.common.machine.hatch.OredictInputBusME;
import com.science.gtnl.common.machine.hatch.OriginalInputHatch;
import com.science.gtnl.common.machine.hatch.OriginalOutputHatch;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputProxy;
import com.science.gtnl.common.machine.hatch.SuperDataAccessHatch;
import com.science.gtnl.common.machine.hatch.SuperDualInputHatchME;
import com.science.gtnl.common.machine.hatch.SuperInputBusME;
import com.science.gtnl.common.machine.hatch.SuperInputHatchME;
import com.science.gtnl.common.machine.hatch.SuperVoidBus;
import com.science.gtnl.common.machine.hatch.SuperVoidHatch;
import com.science.gtnl.common.machine.hatch.TapDynamoHatch;
import com.science.gtnl.common.machine.hatch.TypeFilteredInputBusME;
import com.science.gtnl.common.machine.hatch.VaultPortHatch;
import com.science.gtnl.common.machine.hatch.WirelessMultiDynamoHatch;
import com.science.gtnl.common.machine.hatch.WirelessSteamDynamoHatch;
import com.science.gtnl.common.machine.hatch.WirelessSteamEnergyHatch;
import com.science.gtnl.common.machine.multiblock.AdvancedInfiniteDriller;
import com.science.gtnl.common.machine.multiblock.AdvancedRareEarthCentrifugal;
import com.science.gtnl.common.machine.multiblock.AssemblerMatrix;
import com.science.gtnl.common.machine.multiblock.AtomicEnergyExcitationPlant;
import com.science.gtnl.common.machine.multiblock.BloodSoulSacrificialArray;
import com.science.gtnl.common.machine.multiblock.BrickedBlastFurnace;
import com.science.gtnl.common.machine.multiblock.CheatOreProcessingFactory;
import com.science.gtnl.common.machine.multiblock.ComponentAssembler;
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
import com.science.gtnl.common.machine.multiblock.FuelRefiningComplex;
import com.science.gtnl.common.machine.multiblock.GenerationEarthEngine;
import com.science.gtnl.common.machine.multiblock.GrandAssemblyLine;
import com.science.gtnl.common.machine.multiblock.IndustrialArcaneAssembler;
import com.science.gtnl.common.machine.multiblock.LapotronChip;
import com.science.gtnl.common.machine.multiblock.LargeBioLab;
import com.science.gtnl.common.machine.multiblock.LargeBrewer;
import com.science.gtnl.common.machine.multiblock.LargeCircuitAssembler;
import com.science.gtnl.common.machine.multiblock.LargeGasCollector;
import com.science.gtnl.common.machine.multiblock.LargeIncubator;
import com.science.gtnl.common.machine.multiblock.LargeNaquadahReactor;
import com.science.gtnl.common.machine.multiblock.LibraryOfRuina;
import com.science.gtnl.common.machine.multiblock.MassFabricator;
import com.science.gtnl.common.machine.multiblock.MatterFabricator;
import com.science.gtnl.common.machine.multiblock.MegaMixer;
import com.science.gtnl.common.machine.multiblock.MeteorMiner;
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
import com.science.gtnl.common.machine.multiblock.steam.HighPressureSteamFusionReactor;
import com.science.gtnl.common.machine.multiblock.steam.LargeSteamAlloySmelter;
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
import com.science.gtnl.common.machine.multiblock.wireless.CircuitComponentAssemblyLine;
import com.science.gtnl.common.machine.multiblock.wireless.CompoundDistillationFractionator;
import com.science.gtnl.common.machine.multiblock.wireless.CompoundExtremeCoolingUnit;
import com.science.gtnl.common.machine.multiblock.wireless.CrackerHub;
import com.science.gtnl.common.machine.multiblock.wireless.EngravingLaserPlant;
import com.science.gtnl.common.machine.multiblock.wireless.FieldForgePress;
import com.science.gtnl.common.machine.multiblock.wireless.HandOfJohnDavisonRockefeller;
import com.science.gtnl.common.machine.multiblock.wireless.HeavyRolling;
import com.science.gtnl.common.machine.multiblock.wireless.HighEnergyLaserLathe;
import com.science.gtnl.common.machine.multiblock.wireless.HorizontalCompressor;
import com.science.gtnl.common.machine.multiblock.wireless.IntegratedAssemblyFacility;
import com.science.gtnl.common.machine.multiblock.wireless.MagneticEnergyReactionFurnace;
import com.science.gtnl.common.machine.multiblock.wireless.MegaBathTank;
import com.science.gtnl.common.machine.multiblock.wireless.MegaBrewer;
import com.science.gtnl.common.machine.multiblock.wireless.MegaCanner;
import com.science.gtnl.common.machine.multiblock.wireless.MegaVacuumDryingFurnace;
import com.science.gtnl.common.machine.multiblock.wireless.MicroorganismMaster;
import com.science.gtnl.common.machine.multiblock.wireless.NanitesCircuitAssemblyFactory;
import com.science.gtnl.common.machine.multiblock.wireless.NanoAssemblerMarkL;
import com.science.gtnl.common.machine.multiblock.wireless.NanoPhagocytosisPlant;
import com.science.gtnl.common.machine.multiblock.wireless.NeutroniumWireCutting;
import com.science.gtnl.common.machine.multiblock.wireless.NineIndustrialMultiMachine;
import com.science.gtnl.common.machine.multiblock.wireless.SmeltingMixingFurnace;
import com.science.gtnl.common.machine.multiblock.wireless.SuperconductingElectromagnetism;
import com.science.gtnl.common.machine.multiblock.wireless.SuperconductingMagneticPresser;
import com.science.gtnl.common.machine.multiblock.wireless.VortexMatterCentrifuge;
import com.science.gtnl.common.material.MaterialPool;
import com.science.gtnl.common.material.MaterialUtils;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.GTNLItemList;
import com.science.gtnl.utils.enums.GTNLMachineID;
import com.science.gtnl.utils.enums.ModList;
import com.science.gtnl.utils.item.ItemUtils;
import com.science.gtnl.utils.text.AnimatedText;

import bartworks.common.loaders.ItemRegistry;
import gregtech.api.covers.CoverRegistry;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GlassTier;
import gregtech.common.covers.CoverConveyor;
import gregtech.common.covers.CoverPump;
import gregtech.common.covers.CoverSteamRegulator;
import gregtech.common.covers.CoverSteamValve;
import gtPlusPlus.api.objects.Logger;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import tectech.thing.CustomItemList;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessMulti;

public class MachineLoader {

    public static ItemStack ResourceCollectionModule;
    public static ItemStack SuperSpaceElevator;

    public static void registerMachines() {
        GTNLItemList.QuantumComputer.set(
            new QuantumComputer(
                QUANTUM_COMPUTER.ID,
                "QuantumComputer",
                StatCollector.translateToLocal("NameQuantumComputer")));
        addItemTooltip(GTNLItemList.QuantumComputer.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.AssemblerMatrix.set(
            new AssemblerMatrix(
                ASSEMBLER_MATRIX.ID,
                "AssemblerMatrix",
                StatCollector.translateToLocal("NameAssemblerMatrix")));
        addItemTooltip(GTNLItemList.AssemblerMatrix.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.AtomicEnergyExcitationPlant.set(
            new AtomicEnergyExcitationPlant(
                ATOMIC_ENERGY_EXCITATION_PLANT.ID,
                "AtomicEnergyExcitationPlant",
                StatCollector.translateToLocal("NameAtomicEnergyExcitationPlant")));
        addItemTooltip(GTNLItemList.AtomicEnergyExcitationPlant.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EdenGarden
            .set(new EdenGarden(EDEN_GARDEN.ID, "EdenGarden", StatCollector.translateToLocal("NameEdenGarden")));
        addItemTooltip(GTNLItemList.EdenGarden.get(1), AnimatedText.SNL_EDEN_GARDEN);

        GTNLItemList.LargeSteamCircuitAssembler.set(
            new LargeSteamCircuitAssembler(
                LARGE_STEAM_CIRCUIT_ASSEMBLER.ID,
                "LargeSteamCircuitAssembler",
                StatCollector.translateToLocal("NameLargeSteamCircuitAssembler")));
        addItemTooltip(GTNLItemList.LargeSteamCircuitAssembler.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GenerationEarthEngine.set(
            new GenerationEarthEngine(
                GENERATION_EARTH_ENGINE.ID,
                "GenerationEarthEngine",
                StatCollector.translateToLocal("NameGenerationEarthEngine")));
        addItemTooltip(GTNLItemList.GenerationEarthEngine.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.BloodSoulSacrificialArray.set(
            new BloodSoulSacrificialArray(
                BLOOD_SOUL_SACRIFICIAL_ARRAY.ID,
                "BloodSoulSacrificialArray",
                StatCollector.translateToLocal("NameBloodSoulSacrificialArray")));
        addItemTooltip(GTNLItemList.BloodSoulSacrificialArray.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.RealArtificialStar.set(
            new RealArtificialStar(
                REAL_ARTIFICIAL_STAR.ID,
                "RealArtificialStar",
                StatCollector.translateToLocal("NameRealArtificialStar")));
        addItemTooltip(GTNLItemList.RealArtificialStar.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.TeleportationArrayToAlfheim.set(
            new TeleportationArrayToAlfheim(
                TELEPORTATION_ARRAY_TO_ALFHEIM.ID,
                "TeleportationArrayToAlfheim",
                StatCollector.translateToLocal("NameTeleportationArrayToAlfheim")));
        addItemTooltip(GTNLItemList.TeleportationArrayToAlfheim.get(1), AnimatedText.SNL_NLXCJH);

        GTNLItemList.LapotronChip.set(
            new LapotronChip(LAPOTRON_CHIP.ID, "LapotronChip", StatCollector.translateToLocal("NameLapotronChip")));
        addItemTooltip(GTNLItemList.LapotronChip.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NeutroniumWireCutting.set(
            new NeutroniumWireCutting(
                NEUTRONIUM_WIRE_CUTTING.ID,
                "NeutroniumWireCutting",
                StatCollector.translateToLocal("NameNeutroniumWireCutting")));
        addItemTooltip(GTNLItemList.NeutroniumWireCutting.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeSteamCrusher.set(
            new LargeSteamCrusher(
                LARGE_STEAM_CRUSHER.ID,
                "LargeSteamCrusher",
                StatCollector.translateToLocal("NameLargeSteamCrusher")));
        addItemTooltip(GTNLItemList.LargeSteamCrusher.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.ComponentAssembler.set(
            new ComponentAssembler(
                COMPONENT_ASSEMBLER.ID,
                "ComponentAssembler",
                StatCollector.translateToLocal("NameComponentAssembler")));
        addItemTooltip(GTNLItemList.ComponentAssembler.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeSteamFurnace.set(
            new LargeSteamFurnace(
                LARGE_STEAM_FURNACE.ID,
                "LargeSteamFurnace",
                StatCollector.translateToLocal("NameLargeSteamFurnace")));
        addItemTooltip(GTNLItemList.LargeSteamFurnace.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeSteamAlloySmelter.set(
            new LargeSteamAlloySmelter(
                LARGE_STEAM_ALLOY_SMELTER.ID,
                "LargeSteamAlloySmelter",
                StatCollector.translateToLocal("NameLargeSteamAlloySmelter")));
        addItemTooltip(GTNLItemList.LargeSteamAlloySmelter.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeSteamThermalCentrifuge.set(
            new LargeSteamThermalCentrifuge(
                LARGE_STEAM_THERMAL_CENTRIFUGE.ID,
                "LargeSteamThermalCentrifuge",
                StatCollector.translateToLocal("NameLargeSteamThermalCentrifuge")));
        addItemTooltip(GTNLItemList.LargeSteamThermalCentrifuge.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.SteamCracking.set(
            new SteamCracking(STEAM_CRACKING.ID, "SteamCracking", StatCollector.translateToLocal("NameSteamCracking")));
        addItemTooltip(GTNLItemList.SteamCracking.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeSteamChemicalBath.set(
            new LargeSteamChemicalBath(
                LARGE_STEAM_CHEMICAL_BATH.ID,
                "LargeSteamChemicalBath",
                StatCollector.translateToLocal("NameLargeSteamChemicalBath")));
        addItemTooltip(GTNLItemList.LargeSteamChemicalBath.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.PrimitiveDistillationTower.set(
            new PrimitiveDistillationTower(
                PRIMITIVE_DISTILLATION_TOWER.ID,
                "PrimitiveDistillationTower",
                StatCollector.translateToLocal("NamePrimitiveDistillationTower")));
        addItemTooltip(GTNLItemList.PrimitiveDistillationTower.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.MeteorMiner
            .set(new MeteorMiner(METEOR_MINER.ID, "MeteorMiner", StatCollector.translateToLocal("NameMeteorMiner")));
        addItemTooltip(GTNLItemList.MeteorMiner.get(1), AnimatedText.SNL_TOTTO);

        GTNLItemList.Desulfurizer
            .set(new Desulfurizer(DESULFURIZER.ID, "Desulfurizer", StatCollector.translateToLocal("NameDesulfurizer")));
        addItemTooltip(GTNLItemList.Desulfurizer.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeCircuitAssembler.set(
            new LargeCircuitAssembler(
                LARGE_CIRCUIT_ASSEMBLER.ID,
                "LargeCircuitAssembler",
                StatCollector.translateToLocal("NameLargeCircuitAssembler")));
        addItemTooltip(GTNLItemList.LargeCircuitAssembler.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.PetrochemicalPlant.set(
            new PetrochemicalPlant(
                PETROCHEMICAL_PLANT.ID,
                "PetrochemicalPlant",
                StatCollector.translateToLocal("NamePetrochemicalPlant")));
        addItemTooltip(GTNLItemList.PetrochemicalPlant.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.SmeltingMixingFurnace.set(
            new SmeltingMixingFurnace(
                SMELTING_MIXING_FURNACE.ID,
                "SmeltingMixingFurnace",
                StatCollector.translateToLocal("NameSmeltingMixingFurnace")));
        addItemTooltip(GTNLItemList.SmeltingMixingFurnace.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.WhiteNightGenerator.set(
            new WhiteNightGenerator(
                WHITE_NIGHT_GENERATOR.ID,
                "WhiteNightGenerator",
                StatCollector.translateToLocal("NameWhiteNightGenerator")));
        addItemTooltip(GTNLItemList.WhiteNightGenerator.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ProcessingArray.set(
            new ProcessingArray(
                PROCESSING_ARRAY.ID,
                "ProcessingArray",
                StatCollector.translateToLocal("NameProcessingArrayGTNL")));
        addItemTooltip(GTNLItemList.ProcessingArray.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.MegaBlastFurnace.set(
            new MegaBlastFurnace(
                MEGA_BLAST_FURNACE.ID,
                "MegaBlastFurnace",
                StatCollector.translateToLocal("NameMegaBlastFurnace")));
        addItemTooltip(GTNLItemList.MegaBlastFurnace.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.BrickedBlastFurnace.set(
            new BrickedBlastFurnace(
                BRICKED_BLAST_FURNACE.ID,
                "BrickedBlastFurnace",
                StatCollector.translateToLocal("NameBrickedBlastFurnace")));
        addItemTooltip(GTNLItemList.BrickedBlastFurnace.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.RareEarthCentrifugal.set(
            new RareEarthCentrifugal(
                RARE_EARTH_CENTRIFUGAL.ID,
                "RareEarthCentrifugal",
                StatCollector.translateToLocal("NameRareEarthCentrifugal")));
        addItemTooltip(GTNLItemList.RareEarthCentrifugal.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.ColdIceFreezer.set(
            new ColdIceFreezer(
                COLD_ICE_FREEZER.ID,
                "ColdIceFreezer",
                StatCollector.translateToLocal("NameColdIceFreezer")));
        addItemTooltip(GTNLItemList.ColdIceFreezer.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.BlazeBlastFurnace.set(
            new BlazeBlastFurnace(
                BLAZE_BLAST_FURNACE.ID,
                "BlazeBlastFurnace",
                StatCollector.translateToLocal("NameBlazeBlastFurnace")));
        addItemTooltip(GTNLItemList.BlazeBlastFurnace.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.ChemicalPlant.set(
            new ChemicalPlant(CHEMICAL_PLANT.ID, "ChemicalPlant", StatCollector.translateToLocal("NameChemicalPlant")));
        addItemTooltip(GTNLItemList.ChemicalPlant.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.VacuumFreezer.set(
            new VacuumFreezer(VACUUM_FREEZER.ID, "VacuumFreezer", StatCollector.translateToLocal("NameVacuumFreezer")));
        addItemTooltip(GTNLItemList.VacuumFreezer.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.IndustrialArcaneAssembler.set(
            new IndustrialArcaneAssembler(
                INDUSTRIAL_ARCANE_ASSEMBLER.ID,
                "IndustrialArcaneAssembler",
                StatCollector.translateToLocal("NameIndustrialArcaneAssembler")));
        addItemTooltip(GTNLItemList.IndustrialArcaneAssembler.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergeticPhotovoltaicPowerStation.set(
            new PhotovoltaicPowerStation.EnergeticPhotovoltaicPowerStation(
                ENERGETIC_PHOTOVOLTAIC_POWER_STATION.ID,
                "EnergeticPhotovoltaicPowerStation",
                StatCollector.translateToLocal("NameEnergeticPhotovoltaicPowerStation")));
        addItemTooltip(GTNLItemList.EnergeticPhotovoltaicPowerStation.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.AdvancedPhotovoltaicPowerStation.set(
            new PhotovoltaicPowerStation.AdvancedPhotovoltaicPowerStation(
                ADVANCED_PHOTOVOLTAIC_POWER_STATION.ID,
                "AdvancedPhotovoltaicPowerStation",
                StatCollector.translateToLocal("NameAdvancedPhotovoltaicPowerStation")));
        addItemTooltip(GTNLItemList.AdvancedPhotovoltaicPowerStation.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.VibrantPhotovoltaicPowerStation.set(
            new PhotovoltaicPowerStation.VibrantPhotovoltaicPowerStation(
                VIBRANT_PHOTOVOLTAIC_POWER_STATION.ID,
                "VibrantPhotovoltaicPowerStation",
                StatCollector.translateToLocal("NameVibrantPhotovoltaicPowerStation")));
        addItemTooltip(GTNLItemList.VibrantPhotovoltaicPowerStation.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeMacerationTower.set(
            new LargeMacerationTower(
                LARGE_MACERATION_TOWER.ID,
                "LargeMacerationTower",
                StatCollector.translateToLocal("NameLargeMacerationTower")));
        addItemTooltip(GTNLItemList.LargeMacerationTower.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.HandOfJohnDavisonRockefeller.set(
            new HandOfJohnDavisonRockefeller(
                HAND_OF_JOHN_DAVISON_ROCKEFELLER.ID,
                "HandOfJohnDavisonRockefeller",
                StatCollector.translateToLocal("NameHandOfJohnDavisonRockefeller")));
        addItemTooltip(GTNLItemList.HandOfJohnDavisonRockefeller.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeSiftingFunnel.set(
            new LargeSiftingFunnel(
                LARGE_SIFTING_FUNNEL.ID,
                "LargeSiftingFunnel",
                StatCollector.translateToLocal("NameLargeSiftingFunnel")));
        addItemTooltip(GTNLItemList.LargeSiftingFunnel.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeCutter
            .set(new LargeCutter(LARGE_CUTTER.ID, "LargeCutter", StatCollector.translateToLocal("NameLargeCutter")));
        addItemTooltip(GTNLItemList.LargeCutter.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeBrewer
            .set(new LargeBrewer(LARGE_BREWER.ID, "LargeBrewer", StatCollector.translateToLocal("NameLargeBrewer")));
        addItemTooltip(GTNLItemList.LargeBrewer.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeIndustrialLathe.set(
            new LargeIndustrialLathe(
                LARGE_INDUSTRIAL_LATHE.ID,
                "LargeIndustrialLathe",
                StatCollector.translateToLocal("NameLargeIndustrialLathe")));
        addItemTooltip(GTNLItemList.LargeIndustrialLathe.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeMaterialPress.set(
            new LargeMaterialPress(
                LARGE_MATERIAL_PRESS.ID,
                "LargeMaterialPress",
                StatCollector.translateToLocal("NameLargeMaterialPress")));
        addItemTooltip(GTNLItemList.LargeMaterialPress.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeWiremill.set(
            new LargeWiremill(LARGE_WIREMILL.ID, "LargeWiremill", StatCollector.translateToLocal("NameLargeWiremill")));
        addItemTooltip(GTNLItemList.LargeWiremill.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeBender
            .set(new LargeBender(LARGE_BENDER.ID, "LargeBender", StatCollector.translateToLocal("NameLargeBender")));
        addItemTooltip(GTNLItemList.LargeBender.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.ElectricImplosionCompressor.set(
            new ElectricImplosionCompressor(
                ELECTRIC_IMPLOSION_COMPRESSOR.ID,
                "ElectricImplosionCompressor",
                StatCollector.translateToLocal("NameElectricImplosionCompressor")));
        addItemTooltip(GTNLItemList.ElectricImplosionCompressor.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.LargeExtruder.set(
            new LargeExtruder(LARGE_EXTRUDER.ID, "LargeExtruder", StatCollector.translateToLocal("NameLargeExtruder")));
        addItemTooltip(GTNLItemList.LargeExtruder.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeArcSmelter.set(
            new LargeArcSmelter(
                LARGE_ARC_SMELTER.ID,
                "LargeArcSmelter",
                StatCollector.translateToLocal("NameLargeArcSmelter")));
        addItemTooltip(GTNLItemList.LargeArcSmelter.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeForming.set(
            new LargeForming(LARGE_FORMING.ID, "LargeForming", StatCollector.translateToLocal("NameLargeForming")));
        addItemTooltip(GTNLItemList.LargeForming.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.MatterFabricator.set(
            new MatterFabricator(
                MATTER_FABRICATOR.ID,
                "MatterFabricator",
                StatCollector.translateToLocal("NameMatterFabricator")));
        addItemTooltip(GTNLItemList.MatterFabricator.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeElectrolyzer.set(
            new LargeElectrolyzer(
                LARGE_ELECTROLYZER.ID,
                "LargeElectrolyzer",
                StatCollector.translateToLocal("NameLargeElectrolyzer")));
        addItemTooltip(GTNLItemList.LargeElectrolyzer.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeElectromagnet.set(
            new LargeElectromagnet(
                LARGE_ELECTROMAGNET.ID,
                "LargeElectromagnet",
                StatCollector.translateToLocal("NameLargeElectromagnet")));
        addItemTooltip(GTNLItemList.LargeElectromagnet.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeAssembler.set(
            new LargeAssembler(
                LARGE_ASSEMBLER.ID,
                "LargeAssembler",
                StatCollector.translateToLocal("NameLargeAssembler")));
        addItemTooltip(GTNLItemList.LargeAssembler.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeMixer
            .set(new LargeMixer(LARGE_MIXER.ID, "LargeMixer", StatCollector.translateToLocal("NameLargeMixer")));
        addItemTooltip(GTNLItemList.LargeMixer.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeCentrifuge.set(
            new LargeCentrifuge(
                LARGE_CENTRIFUGE.ID,
                "LargeCentrifuge",
                StatCollector.translateToLocal("NameLargeCentrifuge")));
        addItemTooltip(GTNLItemList.LargeCentrifuge.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LibraryOfRuina.set(
            new LibraryOfRuina(
                LIBRARY_OF_RUINA.ID,
                "LibraryOfRuina",
                StatCollector.translateToLocal("NameLibraryOfRuina")));
        addItemTooltip(GTNLItemList.LibraryOfRuina.get(1), AnimatedText.SNL_NLXCJH);

        GTNLItemList.LargeChemicalBath.set(
            new LargeChemicalBath(
                LARGE_CHEMICAL_BATH.ID,
                "LargeChemicalBath",
                StatCollector.translateToLocal("NameLargeChemicalBath")));
        addItemTooltip(GTNLItemList.LargeChemicalBath.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeAutoclave.set(
            new LargeAutoclave(
                LARGE_AUTOCLAVE.ID,
                "LargeAutoclave",
                StatCollector.translateToLocal("NameLargeAutoclave")));
        addItemTooltip(GTNLItemList.LargeAutoclave.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeSolidifier.set(
            new LargeSolidifier(
                LARGE_SOLIDIFIER.ID,
                "LargeSolidifier",
                StatCollector.translateToLocal("NameLargeSolidifier")));
        addItemTooltip(GTNLItemList.LargeSolidifier.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeExtractor.set(
            new LargeExtractor(
                LARGE_EXTRACTOR.ID,
                "LargeExtractor",
                StatCollector.translateToLocal("NameLargeExtractor")));
        addItemTooltip(GTNLItemList.LargeExtractor.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.ReactionFurnace.set(
            new ReactionFurnace(
                REACTION_FURNACE.ID,
                "ReactionFurnace",
                StatCollector.translateToLocal("NameReactionFurnace")));
        addItemTooltip(GTNLItemList.ReactionFurnace.get(1), AnimatedText.SNL_NLXCJH);

        GTNLItemList.EnergyInfuser.set(
            new EnergyInfuser(ENERGY_INFUSER.ID, "EnergyInfuser", StatCollector.translateToLocal("NameEnergyInfuser")));
        addItemTooltip(GTNLItemList.EnergyInfuser.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.LargeCanning.set(
            new LargeCanning(LARGE_CANNING.ID, "LargeCanning", StatCollector.translateToLocal("NameLargeCanning")));
        addItemTooltip(GTNLItemList.LargeCanning.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.Digester
            .set(new Digester(DIGESTER.ID, "Digester", StatCollector.translateToLocal("NameDigester")));
        addItemTooltip(GTNLItemList.Digester.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.AlloyBlastSmelter.set(
            new AlloyBlastSmelter(
                ALLOY_BLAST_SMELTER.ID,
                "AlloyBlastSmelter",
                StatCollector.translateToLocal("NameAlloyBlastSmelter")));
        addItemTooltip(GTNLItemList.AlloyBlastSmelter.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeSteamExtractor.set(
            new LargeSteamExtractor(
                LARGE_STEAM_EXTRACTOR.ID,
                "LargeSteamExtractor",
                StatCollector.translateToLocal("NameLargeSteamExtractor")));
        addItemTooltip(GTNLItemList.LargeSteamExtractor.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeSteamOreWasher.set(
            new LargeSteamOreWasher(
                LARGE_STEAM_ORE_WASHER.ID,
                "LargeSteamOreWasher",
                StatCollector.translateToLocal("NameLargeSteamOreWasher")));
        addItemTooltip(GTNLItemList.LargeSteamOreWasher.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.LargeHammer
            .set(new LargeHammer(LARGE_HAMMER.ID, "LargeHammer", StatCollector.translateToLocal("NameLargeHammer")));
        addItemTooltip(GTNLItemList.LargeHammer.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.IsaMill.set(new IsaMill(ISA_MILL.ID, "IsaMill", StatCollector.translateToLocal("NameIsaMill")));
        addItemTooltip(GTNLItemList.IsaMill.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.FlotationCellRegulator.set(
            new FlotationCellRegulator(
                FLOTATION_CELL_REGULATOR.ID,
                "FlotationCellRegulator",
                StatCollector.translateToLocal("NameFlotationCellRegulator")));
        addItemTooltip(GTNLItemList.FlotationCellRegulator.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.VacuumDryingFurnace.set(
            new VacuumDryingFurnace(
                VACUUM_DRYING_FURNACE.ID,
                "VacuumDryingFurnace",
                StatCollector.translateToLocal("NameVacuumDryingFurnace")));
        addItemTooltip(GTNLItemList.VacuumDryingFurnace.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeDistillery.set(
            new LargeDistillery(
                LARGE_DISTILLERY.ID,
                "LargeDistillery",
                StatCollector.translateToLocal("NameLargeDistillery")));
        addItemTooltip(GTNLItemList.LargeDistillery.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.Incubator
            .set(new Incubator(INCUBATOR.ID, "Incubator", StatCollector.translateToLocal("NameIncubator")));
        addItemTooltip(GTNLItemList.Incubator.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.LargeIncubator.set(
            new LargeIncubator(
                LARGE_INCUBATOR.ID,
                "LargeIncubator",
                StatCollector.translateToLocal("NameLargeIncubator")));
        addItemTooltip(GTNLItemList.LargeIncubator.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeEngravingLaser.set(
            new LargeEngravingLaser(
                LARGE_ENGRAVING_LASER.ID,
                "LargeEngravingLaser",
                StatCollector.translateToLocal("NameLargeEngravingLaser")));
        addItemTooltip(GTNLItemList.LargeEngravingLaser.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.FishingGround.set(
            new FishingGround(FISHING_GROUND.ID, "FishingGround", StatCollector.translateToLocal("NameFishingGround")));
        addItemTooltip(GTNLItemList.FishingGround.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.ElementCopying.set(
            new ElementCopying(
                ELEMENT_COPYING.ID,
                "ElementCopying",
                StatCollector.translateToLocal("NameElementCopying")));
        addItemTooltip(GTNLItemList.ElementCopying.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.WoodDistillation.set(
            new WoodDistillation(
                WOOD_DISTILLATION.ID,
                "WoodDistillation",
                StatCollector.translateToLocal("NameWoodDistillation")));
        addItemTooltip(GTNLItemList.WoodDistillation.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargePacker
            .set(new LargePacker(LARGE_PACKER.ID, "LargePacker", StatCollector.translateToLocal("NameLargePacker")));
        addItemTooltip(GTNLItemList.LargePacker.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeAlloySmelter.set(
            new LargeAlloySmelter(
                LARGE_ALLOY_SMELTER.ID,
                "LargeAlloySmelter",
                StatCollector.translateToLocal("NameLargeAlloySmelter")));
        addItemTooltip(GTNLItemList.LargeAlloySmelter.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.MolecularTransformer.set(
            new MolecularTransformer(
                MOLECULAR_TRANSFORMER.ID,
                "MolecularTransformer",
                StatCollector.translateToLocal("NameMolecularTransformer")));
        addItemTooltip(GTNLItemList.MolecularTransformer.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.LargePyrolyseOven.set(
            new LargePyrolyseOven(
                LARGE_PYROLYSE_OVEN.ID,
                "LargePyrolyseOven",
                StatCollector.translateToLocal("NameLargePyrolyseOven")));
        addItemTooltip(GTNLItemList.LargePyrolyseOven.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.LargeNaquadahReactor.set(
            new LargeNaquadahReactor(
                LARGE_NAQUADAH_REACTOR.ID,
                "LargeNaquadahReactor",
                StatCollector.translateToLocal("NameLargeNaquadahReactor")));
        addItemTooltip(GTNLItemList.LargeNaquadahReactor.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.DraconicFusionCrafting.set(
            new DraconicFusionCrafting(
                DRACONIC_FUSION_CRAFTING.ID,
                "DraconicFusionCrafting",
                StatCollector.translateToLocal("NameDraconicFusionCrafting")));
        addItemTooltip(GTNLItemList.DraconicFusionCrafting.get(1), AnimatedText.SNL_NLXCJH);

        GTNLItemList.LargeSteamExtruder.set(
            new LargeSteamExtruder(
                LARGE_STEAM_EXTRUDER.ID,
                "LargeSteamExtruder",
                StatCollector.translateToLocal("NameLargeSteamExtruder")));
        addItemTooltip(GTNLItemList.LargeSteamExtruder.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.DecayHastener.set(
            new DecayHastener(DECAY_HASTENER.ID, "DecayHastener", StatCollector.translateToLocal("NameDecayHastener")));
        addItemTooltip(GTNLItemList.DecayHastener.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.PreciseAssembler.set(
            new PrecisionAssembler(
                PRECISION_ASSEMBLER.ID,
                "PrecisionAssembler",
                StatCollector.translateToLocal("NamePrecisionAssembler")));
        addItemTooltip(GTNLItemList.PreciseAssembler.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.MegaAlloyBlastSmelter.set(
            new MegaAlloyBlastSmelter(
                MEGA_ALLOY_BLAST_SMELTER.ID,
                "MegaAlloyBlastSmelter",
                StatCollector.translateToLocal("NameMegaAlloyBlastSmelter")));
        addItemTooltip(GTNLItemList.MegaAlloyBlastSmelter.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.GrandAssemblyLine.set(
            new GrandAssemblyLine(
                GRAND_ASSEMBLY_LINE.ID,
                "GrandAssemblyLine",
                StatCollector.translateToLocal("NameGrandAssemblyLine")));
        addItemTooltip(GTNLItemList.GrandAssemblyLine.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.FuelRefiningComplex.set(
            new FuelRefiningComplex(
                FUEL_REFINING_COMPLEX.ID,
                "FuelRefiningComplex",
                StatCollector.translateToLocal("NameFuelRefiningComplex")));
        addItemTooltip(GTNLItemList.FuelRefiningComplex.get(1), AnimatedText.SNL_QYZG);

        ResourceCollectionModule = new ResourceCollectionModule(
            RESOURCE_COLLECTION_MODULE.ID,
            "ResourceCollectionModule",
            StatCollector.translateToLocal("NameResourceCollectionModule")).getStackForm(1);
        GTNLItemList.ResourceCollectionModule.set(ResourceCollectionModule);
        addItemTooltip(GTNLItemList.ResourceCollectionModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LuvKuangBiaoOneGiantNuclearFusionReactor.set(
            new KuangBiaoOneGiantNuclearFusionReactor.LuVTier(
                LUV_KUANG_BIAO_ONE_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoOneGiantNuclearFusionReactor",
                StatCollector.translateToLocal("NameLuvKuangBiaoOneGiantNuclearFusionReactor")));
        addItemTooltip(GTNLItemList.LuvKuangBiaoOneGiantNuclearFusionReactor.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.ZpmKuangBiaoTwoGiantNuclearFusionReactor.set(
            new KuangBiaoOneGiantNuclearFusionReactor.ZPMTier(
                ZPM_KUANG_BIAO_TWO_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoTwoGiantNuclearFusionReactor",
                StatCollector.translateToLocal("NameZpmKuangBiaoTwoGiantNuclearFusionReactor")));
        addItemTooltip(GTNLItemList.ZpmKuangBiaoTwoGiantNuclearFusionReactor.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.UvKuangBiaoThreeGiantNuclearFusionReactor.set(
            new KuangBiaoOneGiantNuclearFusionReactor.UVTier(
                UV_KUANG_BIAO_THREE_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoThreeGiantNuclearFusionReactor",
                StatCollector.translateToLocal("NameUvKuangBiaoThreeGiantNuclearFusionReactor")));
        addItemTooltip(GTNLItemList.UvKuangBiaoThreeGiantNuclearFusionReactor.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.UhvKuangBiaoFourGiantNuclearFusionReactor.set(
            new KuangBiaoOneGiantNuclearFusionReactor.UHVTier(
                UHV_KUANG_BIAO_FOUR_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoFourGiantNuclearFusionReactor",
                StatCollector.translateToLocal("NameUhvKuangBiaoFourGiantNuclearFusionReactor")));
        addItemTooltip(GTNLItemList.UhvKuangBiaoFourGiantNuclearFusionReactor.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.UevKuangBiaoFiveGiantNuclearFusionReactor.set(
            new KuangBiaoOneGiantNuclearFusionReactor.UEVTier(
                UEV_KUANG_BIAO_FIVE_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoFiveGiantNuclearFusionReactor",
                StatCollector.translateToLocal("NameUevKuangBiaoFiveGiantNuclearFusionReactor")));
        addItemTooltip(GTNLItemList.UevKuangBiaoFiveGiantNuclearFusionReactor.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.LargeSteamCentrifuge.set(
            new LargeSteamCentrifuge(
                LARGE_STEAM_CENTRIFUGE.ID,
                "LargeSteamCentrifuge",
                StatCollector.translateToLocal("NameLargeSteamCentrifuge")));
        addItemTooltip(GTNLItemList.LargeSteamCentrifuge.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeSteamHammer.set(
            new LargeSteamHammer(
                LARGE_STEAM_HAMMER.ID,
                "LargeSteamHammer",
                StatCollector.translateToLocal("NameLargeSteamHammer")));
        addItemTooltip(GTNLItemList.LargeSteamHammer.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeSteamCompressor.set(
            new LargeSteamCompressor(
                LARGE_STEAM_COMPRESSOR.ID,
                "LargeSteamCompressor",
                StatCollector.translateToLocal("NameLargeSteamCompressor")));
        addItemTooltip(GTNLItemList.LargeSteamCompressor.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeSteamSifter.set(
            new LargeSteamSifter(
                LARGE_STEAM_SIFTER.ID,
                "LargeSteamSifter",
                StatCollector.translateToLocal("NameLargeSteamSifter")));
        addItemTooltip(GTNLItemList.LargeSteamSifter.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeBoilerBronze.set(
            new LargeBoiler.LargeBoilerBronze(
                LARGE_BOILER_BRONZE.ID,
                "LargeBoilerBronze",
                StatCollector.translateToLocal("NameLargeBoilerBronze")));
        addItemTooltip(GTNLItemList.LargeBoilerBronze.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeBoilerSteel.set(
            new LargeBoiler.LargeBoilerSteel(
                LARGE_BOILER_STEEL.ID,
                "LargeBoilerSteel",
                StatCollector.translateToLocal("NameLargeBoilerSteel")));
        addItemTooltip(GTNLItemList.LargeBoilerSteel.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeBoilerTitanium.set(
            new LargeBoiler.LargeBoilerTitanium(
                LARGE_BOILER_TITANIUM.ID,
                "LargeBoilerTitanium",
                StatCollector.translateToLocal("NameLargeBoilerTitanium")));
        addItemTooltip(GTNLItemList.LargeBoilerTitanium.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeBoilerTungstenSteel.set(
            new LargeBoiler.LargeBoilerTungstenSteel(
                LARGE_BOILER_TUNGSTEN_STEEL.ID,
                "LargeBoilerTungstenSteel",
                StatCollector.translateToLocal("NameLargeBoilerTungstenSteel")));
        addItemTooltip(GTNLItemList.LargeBoilerTungstenSteel.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.LargeSteamFormingPress.set(
            new LargeSteamFormingPress(
                LARGE_STEAM_FORMING_PRESS.ID,
                "LargeSteamFormingPress",
                StatCollector.translateToLocal("NameLargeSteamFormingPress")));
        addItemTooltip(GTNLItemList.LargeSteamFormingPress.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LargeSteamMixer.set(
            new LargeSteamMixer(
                LARGE_STEAM_MIXER.ID,
                "LargeSteamMixer",
                StatCollector.translateToLocal("NameLargeSteamMixer")));
        addItemTooltip(GTNLItemList.LargeSteamMixer.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.CrackerHub
            .set(new CrackerHub(CRACKER_HUB.ID, "CrackerHub", StatCollector.translateToLocal("NameCrackerHub")));
        addItemTooltip(GTNLItemList.CrackerHub.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.AdvancedInfiniteDriller.set(
            new AdvancedInfiniteDriller(
                ADVANCED_INFINITE_DRILLER.ID,
                "AdvancedInfiniteDriller",
                StatCollector.translateToLocal("NameAdvancedInfiniteDriller")));
        addItemTooltip(GTNLItemList.AdvancedInfiniteDriller.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.ElectricBlastFurnace.set(
            new ElectricBlastFurnace(
                ELECTRIC_BLAST_FURNACE.ID,
                "ElectricBlastFurnace",
                StatCollector.translateToLocal("NameElectricBlastFurnace")));
        addItemTooltip(GTNLItemList.ElectricBlastFurnace.get(1), AnimatedText.SNL_SRP);

        GTNLItemList.PlatinumBasedTreatment.set(
            new PlatinumBasedTreatment(
                PLATINUM_BASED_TREATMENT.ID,
                "PlatinumBasedTreatment",
                StatCollector.translateToLocal("NamePlatinumBasedTreatment")));
        addItemTooltip(GTNLItemList.PlatinumBasedTreatment.get(1), AnimatedText.SNL_PBTR);

        GTNLItemList.ShallowChemicalCoupling.set(
            new ShallowChemicalCoupling(
                SHALLOW_CHEMICAL_COUPLING.ID,
                "ShallowChemicalCoupling",
                StatCollector.translateToLocal("NameShallowChemicalCoupling")));
        addItemTooltip(GTNLItemList.ShallowChemicalCoupling.get(1), AnimatedText.SNL_SCCR);

        GTNLItemList.Steamgate
            .set(new Steamgate(STEAMGATE.ID, "Steamgate", StatCollector.translateToLocal("NameSteamgate")));
        addItemTooltip(GTNLItemList.Steamgate.get(1), AnimatedText.SCIENCE_NOT_LEISURE);
        addItemTooltip(GTNLItemList.Steamgate.get(1), AnimatedText.SteamgateCredits);

        GTNLItemList.SteamGateAssembler.set(
            new SteamGateAssembler(
                STEAM_GATE_ASSEMBLER.ID,
                "SteamGateAssembler",
                StatCollector.translateToLocal("NameSteamGateAssembler")));
        addItemTooltip(GTNLItemList.SteamGateAssembler.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.MegaSteamCompressor.set(
            new MegaSteamCompressor(
                MEGA_STEAM_COMPRESSOR.ID,
                "MegaSteamCompressor",
                StatCollector.translateToLocal("NameMegaSteamCompressor")));
        addItemTooltip(GTNLItemList.MegaSteamCompressor.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.MegaSolarBoiler.set(
            new MegaSolarBoiler(
                MEGA_SOLAR_BOILER.ID,
                "MegaSolarBoiler",
                StatCollector.translateToLocal("NameMegaSolarBoiler")));
        addItemTooltip(GTNLItemList.MegaSolarBoiler.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamCactusWonder.set(
            new SteamCactusWonder(
                STEAM_CACTUS_WONDER.ID,
                "SteamCactusWonder",
                StatCollector.translateToLocal("NameSteamCactusWonder")));
        addItemTooltip(GTNLItemList.SteamCactusWonder.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamCarpenter.set(
            new SteamCarpenter(
                STEAM_CARPENTER.ID,
                "SteamCarpenter",
                StatCollector.translateToLocal("NameSteamCarpenter")));
        addItemTooltip(GTNLItemList.SteamCarpenter.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamLavaMaker.set(
            new SteamLavaMaker(
                STEAM_LAVA_MAKER.ID,
                "SteamLavaMaker",
                StatCollector.translateToLocal("NameSteamLavaMaker")));
        addItemTooltip(GTNLItemList.SteamLavaMaker.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamManufacturer.set(
            new SteamManufacturer(
                STEAM_MANUFACTURER.ID,
                "SteamManufacturer",
                StatCollector.translateToLocal("NameSteamManufacturer")));
        addItemTooltip(GTNLItemList.SteamManufacturer.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamRockBreaker.set(
            new SteamRockBreaker(
                STEAM_ROCK_BREAKER.ID,
                "SteamRockBreaker",
                StatCollector.translateToLocal("NameSteamRockBreaker")));
        addItemTooltip(GTNLItemList.SteamRockBreaker.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamWoodcutter.set(
            new SteamWoodcutter(
                STEAM_WOODCUTTER.ID,
                "SteamWoodcutter",
                StatCollector.translateToLocal("NameSteamWoodcutter")));
        addItemTooltip(GTNLItemList.SteamWoodcutter.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamExtractinator.set(
            new SteamExtractinator(
                STEAM_EXTRACTINATOR.ID,
                "SteamExtractinator",
                StatCollector.translateToLocal("NameSteamExtractinator")));
        addItemTooltip(GTNLItemList.SteamExtractinator.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamFusionReactor.set(
            new SteamFusionReactor(
                STEAM_FUSION_REACTOR.ID,
                "SteamFusionReactor",
                StatCollector.translateToLocal("NameSteamFusionReactor")));
        addItemTooltip(GTNLItemList.SteamFusionReactor.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HighPressureSteamFusionReactor.set(
            new HighPressureSteamFusionReactor(
                HIGH_PRESSURE_STEAM_FUSION_REACTOR.ID,
                "HighPressureSteamFusionReactor",
                StatCollector.translateToLocal("NameHighPressureSteamFusionReactor")));
        addItemTooltip(GTNLItemList.HighPressureSteamFusionReactor.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamInfernalCokeOven.set(
            new SteamInfernalCokeOven(
                STEAM_INFERNAL_COKE_OVEN.ID,
                "SteamInfernalCokeOven",
                StatCollector.translateToLocal("NameSteamInfernalCokeOven")));
        addItemTooltip(GTNLItemList.SteamInfernalCokeOven.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.IntegratedAssemblyFacility.set(
            new IntegratedAssemblyFacility(
                INTEGRATED_ASSEMBLY_FACILITY.ID,
                "IntegratedAssemblyFacility",
                StatCollector.translateToLocal("NameIntegratedAssemblyFacility")));
        addItemTooltip(GTNLItemList.IntegratedAssemblyFacility.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.CircuitComponentAssemblyLine.set(
            new CircuitComponentAssemblyLine(
                CIRCUIT_COMPONENT_ASSEMBLY_LINE.ID,
                "CircuitComponentAssemblyLine",
                StatCollector.translateToLocal("NameCircuitComponentAssemblyLine")));
        addItemTooltip(GTNLItemList.CircuitComponentAssemblyLine.get(1), AnimatedText.SNL_SCCR);

        GTNLItemList.NanoPhagocytosisPlant.set(
            new NanoPhagocytosisPlant(
                NANO_PHAGOCYTOSIS_PLANT.ID,
                "NanoPhagocytosisPlant",
                StatCollector.translateToLocal("NameNanoPhagocytosisPlant")));
        addItemTooltip(GTNLItemList.NanoPhagocytosisPlant.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.MagneticEnergyReactionFurnace.set(
            new MagneticEnergyReactionFurnace(
                MAGNETIC_ENERGY_REACTION_FURNACE.ID,
                "MagneticEnergyReactionFurnace",
                StatCollector.translateToLocal("NameMagneticEnergyReactionFurnace")));
        addItemTooltip(GTNLItemList.MagneticEnergyReactionFurnace.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.NanitesIntegratedProcessingCenter.set(
            new NanitesIntegratedProcessingCenter(
                NANITES_INTEGRATED_PROCESSING_CENTER.ID,
                "NanitesIntegratedProcessingCenter",
                StatCollector.translateToLocal("NameNanitesIntegratedProcessingCenter")));
        addItemTooltip(GTNLItemList.NanitesIntegratedProcessingCenter.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.BioengineeringModule.set(
            new BioengineeringModule(
                BIOENGINEERING_MODULE.ID,
                "BioengineeringModule",
                StatCollector.translateToLocal("NameBioengineeringModule")));
        addItemTooltip(GTNLItemList.BioengineeringModule.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.PolymerTwistingModule.set(
            new PolymerTwistingModule(
                POLYMER_TWISTING_MODULE.ID,
                "PolymerTwistingModule",
                StatCollector.translateToLocal("NamePolymerTwistingModule")));
        addItemTooltip(GTNLItemList.PolymerTwistingModule.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.OreExtractionModule.set(
            new OreExtractionModule(
                ORE_EXTRACTION_MODULE.ID,
                "OreExtractionModule",
                StatCollector.translateToLocal("NameOreExtractionModule")));
        addItemTooltip(GTNLItemList.OreExtractionModule.get(1), AnimatedText.SNL_QYZG);

        SuperSpaceElevator = new SuperSpaceElevator(
            SUPER_SPACE_ELEVATOR.ID,
            "SuperSpaceElevator",
            StatCollector.translateToLocal("NameSuperSpaceElevator")).getStackForm(1);
        GTNLItemList.SuperSpaceElevator.set(SuperSpaceElevator);
        addItemTooltip(GTNLItemList.SuperSpaceElevator.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeBioLab
            .set(new LargeBioLab(LARGE_BIO_LAB.ID, "LargeBioLab", StatCollector.translateToLocal("NameLargeBioLab")));
        addItemTooltip(GTNLItemList.LargeBioLab.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LargeGasCollector.set(
            new LargeGasCollector(
                LARGE_GAS_COLLECTOR.ID,
                "LargeGasCollector",
                StatCollector.translateToLocal("NameLargeGasCollector")));
        addItemTooltip(GTNLItemList.LargeGasCollector.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EternalGregTechWorkshop.set(
            new EternalGregTechWorkshop(
                ETERNAL_GREG_TECH_WORKSHOP.ID,
                "EternalGregTechWorkshop",
                StatCollector.translateToLocal("NameEternalGregTechWorkshop")));
        addItemTooltip(GTNLItemList.EternalGregTechWorkshop.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EGTWFusionModule.set(
            new EGTWFusionModule(
                EGTW_FUSION_MODULE.ID,
                "EGTWFusionModule",
                StatCollector.translateToLocal("NameEGTWFusionModule")));
        addItemTooltip(GTNLItemList.EGTWFusionModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SpaceAssembler.set(
            new SpaceAssembler(
                SPACE_ASSEMBLER.ID,
                "SpaceAssembler",
                StatCollector.translateToLocal("NameSpaceAssembler")));
        addItemTooltip(GTNLItemList.SpaceAssembler.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EngravingLaserPlant.set(
            new EngravingLaserPlant(
                ENGRAVING_LASER_PLANT.ID,
                "EngravingLaserPlant",
                StatCollector.translateToLocal("NameEngravingLaserPlant")));
        addItemTooltip(GTNLItemList.EngravingLaserPlant.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.VortexMatterCentrifuge.set(
            new VortexMatterCentrifuge(
                VORTEX_MATTER_CENTRIFUGE.ID,
                "VortexMatterCentrifuge",
                StatCollector.translateToLocal("NameVortexMatterCentrifuge")));
        addItemTooltip(GTNLItemList.VortexMatterCentrifuge.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.SuperconductingElectromagnetism.set(
            new SuperconductingElectromagnetism(
                SUPERCONDUCTING_ELECTROMAGNETISM.ID,
                "SuperconductingElectromagnetism",
                StatCollector.translateToLocal("NameSuperconductingElectromagnetism")));
        addItemTooltip(GTNLItemList.SuperconductingElectromagnetism.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.FieldForgePress.set(
            new FieldForgePress(
                FIELD_FORGE_PRESS.ID,
                "FieldForgePress",
                StatCollector.translateToLocal("NameFieldForgePress")));
        addItemTooltip(GTNLItemList.FieldForgePress.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.SuperconductingMagneticPresser.set(
            new SuperconductingMagneticPresser(
                SUPERCONDUCTING_MAGNETIC_PRESSER.ID,
                "SuperconductingMagneticPresser",
                StatCollector.translateToLocal("NameSuperconductingMagneticPresser")));
        addItemTooltip(GTNLItemList.SuperconductingMagneticPresser.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.HeavyRolling.set(
            new HeavyRolling(HEAVY_ROLLING.ID, "HeavyRolling", StatCollector.translateToLocal("NameHeavyRolling")));
        addItemTooltip(GTNLItemList.HeavyRolling.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.HighEnergyLaserLathe.set(
            new HighEnergyLaserLathe(
                HIGH_ENERGY_LASER_LATHE.ID,
                "HighEnergyLaserLathe",
                StatCollector.translateToLocal("NameHighEnergyLaserLathe")));
        addItemTooltip(GTNLItemList.HighEnergyLaserLathe.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.MegaMixer
            .set(new MegaMixer(MEGA_MIXER.ID, "MegaMixer", StatCollector.translateToLocal("NameMegaMixer")));
        addItemTooltip(GTNLItemList.MegaMixer.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.SteamBeaconModuleI.set(
            new SteamBeaconModule(
                STEAM_BEACON_MODULE_I.ID,
                "SteamBeaconModuleI",
                StatCollector.translateToLocal("NameSteamBeaconModuleI"),
                1));
        addItemTooltip(GTNLItemList.SteamBeaconModuleI.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamBeaconModuleII.set(
            new SteamBeaconModule(
                STEAM_BEACON_MODULE_II.ID,
                "SteamBeaconModuleII",
                StatCollector.translateToLocal("NameSteamBeaconModuleII"),
                2));
        addItemTooltip(GTNLItemList.SteamBeaconModuleII.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamBeaconModuleIII.set(
            new SteamBeaconModule(
                STEAM_BEACON_MODULE_III.ID,
                "SteamBeaconModuleIII",
                StatCollector.translateToLocal("NameSteamBeaconModuleIII"),
                3));
        addItemTooltip(GTNLItemList.SteamBeaconModuleIII.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NanitesCircuitAssemblyFactory.set(
            new NanitesCircuitAssemblyFactory(
                NANITES_CIRCUIT_ASSEMBLY_FACTORY.ID,
                "NanitesCircuitAssemblyFactory",
                StatCollector.translateToLocal("NameNanitesCircuitAssemblyFactory")));
        addItemTooltip(GTNLItemList.NanitesCircuitAssemblyFactory.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.ETGWEyeOfHarmonyModule.set(
            new ETGWEyeOfHarmonyModule(
                ETGW_EYE_OF_HARMONY_MODULE.ID,
                "ETGWEyeOfHarmonyModule",
                StatCollector.translateToLocal("NameETGWEyeOfHarmonyModule")));
        addItemTooltip(GTNLItemList.ETGWEyeOfHarmonyModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.AetronPressor.set(
            new AetronPressor(AETRON_PRESSOR.ID, "AetronPressor", StatCollector.translateToLocal("NameAetronPressor")));
        addItemTooltip(GTNLItemList.AetronPressor.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.SteamElevator.set(
            new SteamElevator(STEAM_ELEVATOR.ID, "SteamElevator", StatCollector.translateToLocal("NameSteamElevator")));
        addItemTooltip(GTNLItemList.SteamElevator.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamMonsterRepellentModuleI.set(
            new SteamMonsterRepellentModule(
                STEAM_MONSTER_REPELLENT_MODULE_I.ID,
                "SteamMonsterRepellentModuleI",
                StatCollector.translateToLocal("NameSteamMonsterRepellentModuleI"),
                1));
        addItemTooltip(GTNLItemList.SteamMonsterRepellentModuleI.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamMonsterRepellentModuleII.set(
            new SteamMonsterRepellentModule(
                STEAM_MONSTER_REPELLENT_MODULE_II.ID,
                "SteamMonsterRepellentModuleII",
                StatCollector.translateToLocal("NameSteamMonsterRepellentModuleII"),
                2));
        addItemTooltip(GTNLItemList.SteamMonsterRepellentModuleII.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamMonsterRepellentModuleIII.set(
            new SteamMonsterRepellentModule(
                STEAM_MONSTER_REPELLENT_MODULE_III.ID,
                "SteamMonsterRepellentModuleIII",
                StatCollector.translateToLocal("NameSteamMonsterRepellentModuleIII"),
                3));
        addItemTooltip(GTNLItemList.SteamMonsterRepellentModuleIII.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamFlightModule.set(
            new SteamFlightModule(
                STEAM_FLIGHT_MODULE.ID,
                "SteamFlightModule",
                StatCollector.translateToLocal("NameSteamFlightModule")));
        addItemTooltip(GTNLItemList.SteamFlightModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamWeatherModule.set(
            new SteamWeatherModule(
                STEAM_WEATHER_MODULE.ID,
                "SteamWeatherModule",
                StatCollector.translateToLocal("NameSteamWeatherModule")));
        addItemTooltip(GTNLItemList.SteamWeatherModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NanoAssemblerMarkL.set(
            new NanoAssemblerMarkL(
                NANO_ASSEMBLER_MARK_L.ID,
                "NanoAssemblerMarkL",
                StatCollector.translateToLocal("NameNanoAssemblerMarkL")));
        addItemTooltip(GTNLItemList.NanoAssemblerMarkL.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.HighPerformanceComputationArray.set(
            new HighPerformanceComputationArray(
                HIGH_PERFORMANCE_COMPUTATION_ARRAY.ID,
                "HighPerformanceComputationArray",
                StatCollector.translateToLocal("NameHighPerformanceComputationArray")));
        addItemTooltip(GTNLItemList.HighPerformanceComputationArray.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EyeOfHarmonyInjector.set(
            new EyeOfHarmonyInjector(
                EYE_OF_HARMONY_INJECTOR.ID,
                "EyeOfHarmonyInjector",
                StatCollector.translateToLocal("NameEyeOfHarmonyInjector")));
        addItemTooltip(GTNLItemList.EyeOfHarmonyInjector.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.CompoundExtremeCoolingUnit.set(
            new CompoundExtremeCoolingUnit(
                COMPOUND_EXTREME_COOLING_UNIT.ID,
                "CompoundExtremeCoolingUnit",
                StatCollector.translateToLocal("NameCompoundExtremeCoolingUnit")));
        addItemTooltip(GTNLItemList.CompoundExtremeCoolingUnit.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.SteamOreProcessorModule.set(
            new SteamOreProcessorModule(
                STEAM_ORE_PROCESSOR_MODULE.ID,
                "SteamOreProcessorModule",
                StatCollector.translateToLocal("NameSteamOreProcessorModule")));
        addItemTooltip(GTNLItemList.SteamOreProcessorModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LargeSteamLathe.set(
            new LargeSteamLathe(
                LARGE_STEAM_LATHE.ID,
                "LargeSteamLathe",
                StatCollector.translateToLocal("NameLargeSteamLathe")));
        addItemTooltip(GTNLItemList.LargeSteamLathe.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.LargeSteamCutting.set(
            new LargeSteamCutting(
                LARGE_STEAM_CUTTING.ID,
                "LargeSteamCutting",
                StatCollector.translateToLocal("NameLargeSteamCutting")));
        addItemTooltip(GTNLItemList.LargeSteamCutting.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.SteamItemVault.set(
            new SteamItemVault(
                STEAM_ITEM_VAULT.ID,
                "SteamItemVault",
                StatCollector.translateToLocal("NameSteamItemVault")));
        addItemTooltip(GTNLItemList.SteamItemVault.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.PrimitiveBrickKiln.set(
            new PrimitiveBrickKiln(
                PRIMITIVE_BRICK_KILN.ID,
                "PrimitiveBrickKiln",
                StatCollector.translateToLocal("NamePrimitiveBrickKiln")));
        addItemTooltip(GTNLItemList.PrimitiveBrickKiln.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SingularityDataHub.set(
            new SingularityDataHub(
                SINGULARITY_DATA_HUB.ID,
                "SingularityDataHub",
                StatCollector.translateToLocal("NameSingularityDataHub")));
        addItemTooltip(GTNLItemList.SingularityDataHub.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ElectrocellGenerator.set(
            new ElectrocellGenerator(
                ELECTROCELL_GENERATOR.ID,
                "ElectrocellGenerator",
                StatCollector.translateToLocal("NameElectrocellGenerator")));
        addItemTooltip(GTNLItemList.ElectrocellGenerator.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.FOGAlloySmelterModule.set(
            new FOGAlloySmelterModule(
                FOG_ALLOY_SMELTER_MODULE.ID,
                "FOGAlloySmelterModule",
                StatCollector.translateToLocal("NameFOGAlloySmelterModule")));
        addItemTooltip(GTNLItemList.FOGAlloySmelterModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.FOGAlloyBlastSmelterModule.set(
            new FOGAlloyBlastSmelterModule(
                FOG_ALLOY_BLAST_SMELTER_MODULE.ID,
                "FOGAlloyBlastSmelterModule",
                StatCollector.translateToLocal("NameFOGAlloyBlastSmelterModule")));
        addItemTooltip(GTNLItemList.FOGAlloyBlastSmelterModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.FOGExtractorModule.set(
            new FOGExtractorModule(
                FOG_EXTRACTOR_MODULE.ID,
                "FOGExtractorModule",
                StatCollector.translateToLocal("NameFOGExtractorModule")));
        addItemTooltip(GTNLItemList.FOGExtractorModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamEntityCrusherModule.set(
            new SteamEntityCrusherModule(
                STEAM_ENTITY_CRUSHER_MODULE.ID,
                "SteamEntityCrusherModule",
                StatCollector.translateToLocal("NameSteamEntityCrusherModule")));
        addItemTooltip(GTNLItemList.SteamEntityCrusherModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamApiaryModule.set(
            new SteamApiaryModule(
                STEAM_APIARY_MODULE.ID,
                "SteamApiaryModule",
                StatCollector.translateToLocal("NameSteamApiaryModule")));
        addItemTooltip(GTNLItemList.SteamApiaryModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamBeeBreedingModule.set(
            new SteamBeeBreedingModule(
                STEAM_BEE_BREEDING_MODULE.ID,
                "SteamBeeBreedingModule",
                StatCollector.translateToLocal("NameSteamBeeBreedingModule")));
        addItemTooltip(GTNLItemList.SteamBeeBreedingModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamGreenhouseModule.set(
            new SteamGreenhouseModule(
                STEAM_GREENHOUSE_MODULE.ID,
                "SteamGreenhouseModule",
                StatCollector.translateToLocal("NameSteamGreenhouseModule")));
        addItemTooltip(GTNLItemList.SteamGreenhouseModule.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.RocketAssembler.set(
            new RocketAssembler(
                ROCKET_ASSEMBLER.ID,
                "RocketAssembler",
                StatCollector.translateToLocal("NameRocketAssembler")));
        addItemTooltip(GTNLItemList.RocketAssembler.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.SteamOilDrillModuleI.set(
            new SteamOilDrillModule(
                STEAM_OIL_DRILL_MODULE_I.ID,
                "SteamOilDrillModuleI",
                StatCollector.translateToLocal("NameSteamOilDrillModuleI"),
                2));
        addItemTooltip(GTNLItemList.SteamOilDrillModuleI.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamOilDrillModuleII.set(
            new SteamOilDrillModule(
                STEAM_OIL_DRILL_MODULE_II.ID,
                "SteamOilDrillModuleII",
                StatCollector.translateToLocal("NameSteamOilDrillModuleII"),
                3));
        addItemTooltip(GTNLItemList.SteamOilDrillModuleII.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamOilDrillModuleIII.set(
            new SteamOilDrillModule(
                STEAM_OIL_DRILL_MODULE_III.ID,
                "SteamOilDrillModuleIII",
                StatCollector.translateToLocal("NameSteamOilDrillModuleIII"),
                4));
        addItemTooltip(GTNLItemList.SteamOilDrillModuleIII.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.AdvancedRareEarthCentrifugal.set(
            new AdvancedRareEarthCentrifugal(
                ADVANCED_RARE_EARTH_CENTRIFUGAL.ID,
                "AdvancedRareEarthCentrifugal",
                StatCollector.translateToLocal("NameAdvancedRareEarthCentrifugal")));
        addItemTooltip(GTNLItemList.AdvancedRareEarthCentrifugal.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.MassFabricator.set(
            new MassFabricator(
                MASS_FABRICATOR.ID,
                "MassFabricator",
                StatCollector.translateToLocal("NameMassFabricator")));
        addItemTooltip(GTNLItemList.MassFabricator.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.AdvancedMassFabricator.set(
            new AdvancedMassFabricator(
                ADVANCED_MASS_FABRICATOR.ID,
                "AdvancedMassFabricator",
                StatCollector.translateToLocal("NameAdvancedMassFabricator")));
        addItemTooltip(GTNLItemList.AdvancedMassFabricator.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.HorizontalCompressor.set(
            new HorizontalCompressor(
                HORIZONTAL_COMPRESSOR.ID,
                "HorizontalCompressor",
                StatCollector.translateToLocal("NameHorizontalCompressor")));
        addItemTooltip(GTNLItemList.HorizontalCompressor.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.MegaVacuumDryingFurnace.set(
            new MegaVacuumDryingFurnace(
                MEGA_VACUUM_DRYING_FURNACE.ID,
                "MegaVacuumDryingFurnace",
                StatCollector.translateToLocal("NameMegaVacuumDryingFurnace")));
        addItemTooltip(GTNLItemList.MegaVacuumDryingFurnace.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.MegaBathTank.set(
            new MegaBathTank(MEGA_BATH_TANK.ID, "MegaBathTank", StatCollector.translateToLocal("NameMegaBathTank")));
        addItemTooltip(GTNLItemList.MegaBathTank.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.MegaCanner
            .set(new MegaCanner(MEGA_CANNER.ID, "MegaCanner", StatCollector.translateToLocal("NameMegaCanner")));
        addItemTooltip(GTNLItemList.MegaCanner.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.CompoundDistillationFractionator.set(
            new CompoundDistillationFractionator(
                COMPOUND_DISTILLATION_FRACTIONATOR.ID,
                "CompoundDistillationFractionator",
                StatCollector.translateToLocal("NameCompoundDistillationFractionator")));
        addItemTooltip(GTNLItemList.CompoundDistillationFractionator.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.MegaBrewer
            .set(new MegaBrewer(MEGA_BREWER.ID, "MegaBrewer", StatCollector.translateToLocal("NameMegaBrewer")));
        addItemTooltip(GTNLItemList.MegaBrewer.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.MicroorganismMaster.set(
            new MicroorganismMaster(
                MICROORGANISM_MASTER.ID,
                "MicroorganismMaster",
                StatCollector.translateToLocal("NameMicroorganismMaster")));
        addItemTooltip(GTNLItemList.MicroorganismMaster.get(1), AnimatedText.SNL_QYZG);

        // Special Machine
        GTNLItemList.CheatOreProcessingFactory.set(
            new CheatOreProcessingFactory(
                CHEAT_ORE_PROCESSING_FACTORY.ID,
                "CheatOreProcessingFactory",
                StatCollector.translateToLocal("NameCheatOreProcessingFactory")));
        addItemTooltip(GTNLItemList.CheatOreProcessingFactory.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.NineIndustrialMultiMachine.set(
            new NineIndustrialMultiMachine(
                NINE_INDUSTRIAL_MULTI_MACHINE.ID,
                "NineIndustrialMultiMachine",
                StatCollector.translateToLocal("NameNineIndustrialMultiMachine")));
        addItemTooltip(GTNLItemList.NineIndustrialMultiMachine.get(1), AnimatedText.SNL_QYZG);
    }

    public static void registerHatch() {
        Set<Fluid> acceptedFluids = new HashSet<>();
        acceptedFluids.add(
            MaterialPool.FluidMana.getFluidOrGas(1)
                .getFluid());

        if (ModList.TwistSpaceTechnology.isModLoaded()) {
            acceptedFluids.add(
                FluidUtils.getFluidStack("liquid mana", 1)
                    .getFluid());
        }

        GTNLItemList.FluidManaInputHatch.set(
            new CustomFluidHatch(
                ImmutableSet.copyOf(acceptedFluids),
                512000,
                FLUID_MANA_INPUT_HATCH.ID,
                "FluidManaInputHatch",
                StatCollector.translateToLocal("FluidManaInputHatch"),
                6));
        addItemTooltip(GTNLItemList.FluidManaInputHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.FluidIceInputHatch.set(
            new CustomFluidHatch(
                ImmutableSet.of(
                    FluidUtils.getFluidStack("ice", 1)
                        .getFluid()),
                256000,
                FLUID_ICE_INPUT_HATCH.ID,
                "FluidIceInputHatch",
                StatCollector.translateToLocal("FluidIceInputHatch"),
                5));
        addItemTooltip(GTNLItemList.FluidIceInputHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.FluidBlazeInputHatch.set(
            new CustomFluidHatch(
                ImmutableSet.of(
                    FluidUtils.getFluidStack("molten.blaze", 1)
                        .getFluid()),
                256000,
                FLUID_BLAZE_INPUT_HATCH.ID,
                "FluidBlazeInputHatch",
                StatCollector.translateToLocal("FluidBlazeInputHatch"),
                5));
        addItemTooltip(GTNLItemList.FluidBlazeInputHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SuperCraftingInputHatchME.set(
            new SuperCraftingInputHatchME(
                SUPER_CRAFTING_INPUT_HATCH_ME.ID,
                "SuperCraftingInputBuffer(ME)",
                StatCollector.translateToLocal("SuperCraftingInputHatchME"),
                true));
        addItemTooltip(GTNLItemList.SuperCraftingInputHatchME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SuperCraftingInputBusME.set(
            new SuperCraftingInputHatchME(
                SUPER_CRAFTING_INPUT_BUS_ME.ID,
                "SuperCraftingInputBus(ME)",
                StatCollector.translateToLocal("SuperCraftingInputBusME"),
                false));
        addItemTooltip(GTNLItemList.SuperCraftingInputBusME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousSolidifierHatch.set(
            new HumongousSolidifierHatch(
                HUMONGOUS_SOLIDIFIER_HATCH.ID,
                "HumongousSolidifierHatch",
                StatCollector.translateToLocal("HumongousSolidifierHatch"),
                14));
        addItemTooltip(GTNLItemList.HumongousSolidifierHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DebugEnergyHatch.set(
            new DebugEnergyHatch(
                DEBUG_ENERGY_HATCH.ID,
                "DebugEnergyHatch",
                StatCollector.translateToLocal("DebugEnergyHatch")));
        addItemTooltip(GTNLItemList.DebugEnergyHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchEV.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_EV.ID,
                9,
                "NinefoldInputHatchEV",
                StatCollector.translateToLocal("NinefoldInputHatchEV"),
                4));
        addItemTooltip(GTNLItemList.NinefoldInputHatchEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchIV.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_IV.ID,
                9,
                "NinefoldInputHatchIV",
                StatCollector.translateToLocal("NinefoldInputHatchIV"),
                5));
        addItemTooltip(GTNLItemList.NinefoldInputHatchIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchLuV.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_LUV.ID,
                9,
                "NinefoldInputHatchLuV",
                StatCollector.translateToLocal("NinefoldInputHatchLuV"),
                6));
        addItemTooltip(GTNLItemList.NinefoldInputHatchLuV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchZPM.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_ZPM.ID,
                9,
                "NinefoldInputHatchZPM",
                StatCollector.translateToLocal("NinefoldInputHatchZPM"),
                7));
        addItemTooltip(GTNLItemList.NinefoldInputHatchZPM.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUV.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_UV.ID,
                9,
                "NinefoldInputHatchUV",
                StatCollector.translateToLocal("NinefoldInputHatchUV"),
                8));
        addItemTooltip(GTNLItemList.NinefoldInputHatchUV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUHV.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_UHV.ID,
                9,
                "NinefoldInputHatchUHV",
                StatCollector.translateToLocal("NinefoldInputHatchUHV"),
                9));
        addItemTooltip(GTNLItemList.NinefoldInputHatchUHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUEV.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_UEV.ID,
                9,
                "NinefoldInputHatchUEV",
                StatCollector.translateToLocal("NinefoldInputHatchUEV"),
                10));
        addItemTooltip(GTNLItemList.NinefoldInputHatchUEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUIV.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_UIV.ID,
                9,
                "NinefoldInputHatchUIV",
                StatCollector.translateToLocal("NinefoldInputHatchUIV"),
                11));
        addItemTooltip(GTNLItemList.NinefoldInputHatchUIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUMV.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_UMV.ID,
                9,
                "NinefoldInputHatchUMV",
                StatCollector.translateToLocal("NinefoldInputHatchUMV"),
                12));
        addItemTooltip(GTNLItemList.NinefoldInputHatchUMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchUXV.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_UXV.ID,
                9,
                "NinefoldInputHatchUXV",
                StatCollector.translateToLocal("NinefoldInputHatchUXV"),
                13));
        addItemTooltip(GTNLItemList.NinefoldInputHatchUXV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldInputHatchMAX.set(
            new NinefoldInputHatch(
                NINEFOLD_INPUT_HATCH_MAX.ID,
                9,
                "NinefoldInputHatchMAX",
                StatCollector.translateToLocal("NinefoldInputHatchMAX"),
                14));
        addItemTooltip(GTNLItemList.NinefoldInputHatchMAX.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousNinefoldInputHatch.set(
            new HumongousNinefoldInputHatch(
                HUMONGOUS_NINEFOLD_INPUT_HATCH.ID,
                9,
                "HumongousNinefoldInputHatch",
                StatCollector.translateToLocal("HumongousNinefoldInputHatch")));
        addItemTooltip(GTNLItemList.HumongousNinefoldInputHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchLV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_LV.ID,
                "DualInputHatchLV",
                StatCollector.translateToLocal("DualInputHatchLV"),
                1));
        addItemTooltip(GTNLItemList.DualInputHatchLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchMV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_MV.ID,
                "DualInputHatchMV",
                StatCollector.translateToLocal("DualInputHatchMV"),
                2));
        addItemTooltip(GTNLItemList.DualInputHatchMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchHV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_HV.ID,
                "DualInputHatchHV",
                StatCollector.translateToLocal("DualInputHatchHV"),
                3));
        addItemTooltip(GTNLItemList.DualInputHatchHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchEV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_EV.ID,
                "DualInputHatchEV",
                StatCollector.translateToLocal("DualInputHatchEV"),
                4));
        addItemTooltip(GTNLItemList.DualInputHatchEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchIV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_IV.ID,
                "DualInputHatchIV",
                StatCollector.translateToLocal("DualInputHatchIV"),
                5));
        addItemTooltip(GTNLItemList.DualInputHatchIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchLuV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_LUV.ID,
                "DualInputHatchLuV",
                StatCollector.translateToLocal("DualInputHatchLuV"),
                6));
        addItemTooltip(GTNLItemList.DualInputHatchLuV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchZPM.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_ZPM.ID,
                "DualInputHatchZPM",
                StatCollector.translateToLocal("DualInputHatchZPM"),
                7));
        addItemTooltip(GTNLItemList.DualInputHatchZPM.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchUV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_UV.ID,
                "DualInputHatchUV",
                StatCollector.translateToLocal("DualInputHatchUV"),
                8));
        addItemTooltip(GTNLItemList.DualInputHatchUV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchUHV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_UHV.ID,
                "DualInputHatchUHV",
                StatCollector.translateToLocal("DualInputHatchUHV"),
                9));
        addItemTooltip(GTNLItemList.DualInputHatchUHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchUEV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_UEV.ID,
                "DualInputHatchUEV",
                StatCollector.translateToLocal("DualInputHatchUEV"),
                10));
        addItemTooltip(GTNLItemList.DualInputHatchUEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchUIV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_UIV.ID,
                "DualInputHatchUIV",
                StatCollector.translateToLocal("DualInputHatchUIV"),
                11));
        addItemTooltip(GTNLItemList.DualInputHatchUIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchUMV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_UMV.ID,
                "DualInputHatchUMV",
                StatCollector.translateToLocal("DualInputHatchUMV"),
                12));
        addItemTooltip(GTNLItemList.DualInputHatchUMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchUXV.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_UXV.ID,
                "DualInputHatchUXV",
                StatCollector.translateToLocal("DualInputHatchUXV"),
                13));
        addItemTooltip(GTNLItemList.DualInputHatchUXV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DualInputHatchMAX.set(
            new DualInputHatch(
                DUAL_INPUT_HATCH_MAX.ID,
                "DualInputHatchMAX",
                StatCollector.translateToLocal("DualInputHatchMAX"),
                14));
        addItemTooltip(GTNLItemList.DualInputHatchMAX.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SuperCraftingInputProxy.set(
            new SuperCraftingInputProxy(
                SUPER_CRAFTING_INPUT_PROXY.ID,
                "SuperCraftingInputProxy",
                StatCollector.translateToLocal("SuperCraftingInputProxy")).getStackForm(1L));
        addItemTooltip(GTNLItemList.SuperCraftingInputProxy.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SuperDataAccessHatch.set(
            new SuperDataAccessHatch(
                SUPER_DATA_ACCESS_HATCH.ID,
                "SuperDataAccessHatch",
                StatCollector.translateToLocal("SuperDataAccessHatch"),
                14));
        addItemTooltip(GTNLItemList.SuperDataAccessHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.BigSteamInputHatch.set(
            new CustomFluidHatch(
                ImmutableSet.of(
                    Materials.Steam.mGas,
                    FluidUtils.getSuperHeatedSteam(1)
                        .getFluid(),
                    Materials.DenseSupercriticalSteam.mGas,
                    MaterialPool.CompressedSteam.getMolten(1)
                        .getFluid()),
                4096000,
                BIG_STEAM_INPUT_HATCH.ID,
                "BigSteamInputHatch",
                StatCollector.translateToLocal("BigSteamInputHatch"),
                1,
                ItemUtils.PICTURE_GTNL_STEAM_LOGO));
        addItemTooltip(GTNLItemList.BigSteamInputHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchLV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_LV.ID,
                "ParallelControllerHatchLV",
                StatCollector.translateToLocal("ParallelControllerHatchLV"),
                1));
        addItemTooltip(GTNLItemList.ParallelControllerHatchLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchMV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_MV.ID,
                "ParallelControllerHatchMV",
                StatCollector.translateToLocal("ParallelControllerHatchMV"),
                2));
        addItemTooltip(GTNLItemList.ParallelControllerHatchMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchHV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_HV.ID,
                "ParallelControllerHatchHV",
                StatCollector.translateToLocal("ParallelControllerHatchHV"),
                3));
        addItemTooltip(GTNLItemList.ParallelControllerHatchHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchEV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_EV.ID,
                "ParallelControllerHatchEV",
                StatCollector.translateToLocal("ParallelControllerHatchEV"),
                4));
        addItemTooltip(GTNLItemList.ParallelControllerHatchEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchIV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_IV.ID,
                "ParallelControllerHatchIV",
                StatCollector.translateToLocal("ParallelControllerHatchIV"),
                5));
        addItemTooltip(GTNLItemList.ParallelControllerHatchIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchLuV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_LUV.ID,
                "ParallelControllerHatchLuV",
                StatCollector.translateToLocal("ParallelControllerHatchLuV"),
                6));
        addItemTooltip(GTNLItemList.ParallelControllerHatchLuV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchZPM.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_ZPM.ID,
                "ParallelControllerHatchZPM",
                StatCollector.translateToLocal("ParallelControllerHatchZPM"),
                7));
        addItemTooltip(GTNLItemList.ParallelControllerHatchZPM.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_UV.ID,
                "ParallelControllerHatchUV",
                StatCollector.translateToLocal("ParallelControllerHatchUV"),
                8));
        addItemTooltip(GTNLItemList.ParallelControllerHatchUV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUHV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_UHV.ID,
                "ParallelControllerHatchUHV",
                StatCollector.translateToLocal("ParallelControllerHatchUHV"),
                9));
        addItemTooltip(GTNLItemList.ParallelControllerHatchUHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUEV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_UEV.ID,
                "ParallelControllerHatchUEV",
                StatCollector.translateToLocal("ParallelControllerHatchUEV"),
                10));
        addItemTooltip(GTNLItemList.ParallelControllerHatchUEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUIV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_UIV.ID,
                "ParallelControllerHatchUIV",
                StatCollector.translateToLocal("ParallelControllerHatchUIV"),
                11));
        addItemTooltip(GTNLItemList.ParallelControllerHatchUIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUMV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_UMV.ID,
                "ParallelControllerHatchUMV",
                StatCollector.translateToLocal("ParallelControllerHatchUMV"),
                12));
        addItemTooltip(GTNLItemList.ParallelControllerHatchUMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchUXV.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_UXV.ID,
                "ParallelControllerHatchUXV",
                StatCollector.translateToLocal("ParallelControllerHatchUXV"),
                13));
        addItemTooltip(GTNLItemList.ParallelControllerHatchUXV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ParallelControllerHatchMAX.set(
            new ParallelControllerHatch(
                PARALLEL_CONTROLLER_HATCH_MAX.ID,
                "ParallelControllerHatchMAX",
                StatCollector.translateToLocal("ParallelControllerHatchMAX"),
                14));
        addItemTooltip(GTNLItemList.ParallelControllerHatchMAX.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.TapDynamoHatchLV.set(
            new TapDynamoHatch(
                TAP_DYNAMO_HATCH.ID,
                "TapDynamoHatchLV",
                StatCollector.translateToLocal("TapDynamoHatchLV"),
                1));
        addItemTooltip(GTNLItemList.TapDynamoHatchLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.PipelessSteamHatch.set(
            new WirelessSteamEnergyHatch(
                PIPELESS_STEAM_HATCH.ID,
                "PipelessSteamHatch",
                StatCollector.translateToLocal("PipelessSteamHatch"),
                0));
        addItemTooltip(GTNLItemList.PipelessSteamHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.PipelessSteamVent.set(
            new WirelessSteamDynamoHatch(
                PIPELESS_STEAM_VENT.ID,
                "PipelessSteamVent",
                StatCollector.translateToLocal("PipelessSteamVent"),
                0));
        addItemTooltip(GTNLItemList.PipelessSteamVent.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.PipelessJetstreamHatch.set(
            new WirelessSteamEnergyHatch(
                PIPELESS_JETSTREAM_HATCH.ID,
                "PipelessJetstreamHatch",
                StatCollector.translateToLocal("PipelessJetstreamHatch"),
                1));
        addItemTooltip(GTNLItemList.PipelessJetstreamHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.PipelessJetstreamVent.set(
            new WirelessSteamDynamoHatch(
                PIPELESS_JETSTREAM_VENT.ID,
                "PipelessJetstreamVent",
                StatCollector.translateToLocal("PipelessJetstreamVent"),
                1));
        addItemTooltip(GTNLItemList.PipelessJetstreamVent.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.AutoConfigurationMaintenanceHatch.set(
            new CustomMaintenanceHatch(
                AUTO_CONFIGURATION_MAINTENANCE_HATCH.ID,
                "AutoConfigurationMaintenanceHatch",
                StatCollector.translateToLocal("AutoConfigurationMaintenanceHatch"),
                80,
                120,
                5,
                new String[] { StatCollector.translateToLocal("Tooltip_AutoConfigurationMaintenanceHatch_00"),
                    StatCollector.translateToLocal("Tooltip_AutoConfigurationMaintenanceHatch_01"),
                    StatCollector.translateToLocal("Tooltip_AutoConfigurationMaintenanceHatch_02"), }));
        addItemTooltip(GTNLItemList.AutoConfigurationMaintenanceHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ExplosionDynamoHatch.set(
            new ExplosionDynamoHatch(
                EXPLOSION_DYNAMO_HATCH.ID,
                "ExplosionDynamoHatch",
                StatCollector.translateToLocal("ExplosionDynamoHatch"),
                5));
        addItemTooltip(GTNLItemList.ExplosionDynamoHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DebugResearchStation.set(
            new DebugResearchStation(
                DEBUG_RESEARCH_STATION.ID,
                "DebugResearchStation",
                StatCollector.translateToLocal("DebugResearchStation"),
                14));
        addItemTooltip(GTNLItemList.DebugResearchStation.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SuperInputBusME.set(
            new SuperInputBusME(
                SUPER_INPUT_BUS_ME.ID,
                false,
                "SuperInputBusME",
                StatCollector.translateToLocal("SuperInputBusME")));
        addItemTooltip(GTNLItemList.SuperInputBusME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.AdvancedSuperInputBusME.set(
            new SuperInputBusME(
                ADVANCED_SUPER_INPUT_BUS_ME.ID,
                true,
                "AdvancedSuperInputBusME",
                StatCollector.translateToLocal("AdvancedSuperInputBusME")));
        addItemTooltip(GTNLItemList.AdvancedSuperInputBusME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SuperInputHatchME.set(
            new SuperInputHatchME(
                SUPER_INPUT_HATCH_ME.ID,
                false,
                "SuperInputHatchME",
                StatCollector.translateToLocal("SuperInputHatchME")));
        addItemTooltip(GTNLItemList.SuperInputHatchME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.AdvancedSuperInputHatchME.set(
            new SuperInputHatchME(
                ADVANCED_SUPER_INPUT_HATCH_ME.ID,
                true,
                "AdvancedSuperInputHatchME",
                StatCollector.translateToLocal("AdvancedSuperInputHatchME")));
        addItemTooltip(GTNLItemList.AdvancedSuperInputHatchME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.Replicator
            .set(new Replicator(REPLICATOR.ID, "Replicator", StatCollector.translateToLocal("Replicator"), 7));

        GTNLItemList.Enchanting
            .set(new Enchanting(ENCHANTING.ID, "Enchanting", StatCollector.translateToLocal("Enchanting"), 7));
        addItemTooltip(GTNLItemList.Enchanting.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.OredictInputBusME.set(
            new OredictInputBusME(
                OREDICT_INPUT_BUS_HATCH_ME.ID,
                "OredictInputBusME",
                StatCollector.translateToLocal("OredictInputBusME")));
        addItemTooltip(GTNLItemList.OredictInputBusME.get(1), AnimatedText.SNL_SKYINR);

        GTNLItemList.NanitesInputBus.set(
            new NanitesInputBus(
                NANITES_INPUT_BUS.ID,
                "NanitesInputBus",
                StatCollector.translateToLocal("NanitesInputBus")));
        addItemTooltip(GTNLItemList.NanitesInputBus.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.VaultPortHatch.set(
            new VaultPortHatch(
                VAULT_PORT_HATCH.ID,
                "VaultPortHatch",
                StatCollector.translateToLocal("VaultPortHatch")));
        addItemTooltip(GTNLItemList.VaultPortHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.OriginalInputHatch.set(
            new OriginalInputHatch(
                ORIGINAL_INPUT_HATCH.ID,
                "OriginalInputHatch",
                StatCollector.translateToLocal("OriginalInputHatch")));
        addItemTooltip(GTNLItemList.OriginalInputHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.OriginalOutputHatch.set(
            new OriginalOutputHatch(
                ORIGINAL_OUTPUT_HATCH.ID,
                "OriginalOutputHatch",
                StatCollector.translateToLocal("OriginalOutputHatch")));
        addItemTooltip(GTNLItemList.OriginalOutputHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SuperVoidBus
            .set(new SuperVoidBus(SUPER_VOID_BUS.ID, "SuperVoidBus", StatCollector.translateToLocal("SuperVoidBus")));
        addItemTooltip(GTNLItemList.SuperVoidBus.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SuperVoidHatch.set(
            new SuperVoidHatch(
                SUPER_VOID_HATCH.ID,
                "SuperVoidHatch",
                StatCollector.translateToLocal("SuperVoidHatch")));
        addItemTooltip(GTNLItemList.SuperVoidHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DebugDataAccessHatch.set(
            new DebugDataAccessHatch(
                DEBUG_DATA_ACCESS_HATCH.ID,
                "DebugDataAccessHatch",
                StatCollector.translateToLocal("DebugDataAccessHatch")));
        addItemTooltip(GTNLItemList.DebugDataAccessHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchLV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_LV.ID,
                "HumongousDualInputHatchLV",
                StatCollector.translateToLocal("HumongousDualInputHatchLV"),
                1));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchMV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_MV.ID,
                "HumongousDualInputHatchMV",
                StatCollector.translateToLocal("HumongousDualInputHatchMV"),
                2));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchHV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_HV.ID,
                "HumongousDualInputHatchHV",
                StatCollector.translateToLocal("HumongousDualInputHatchHV"),
                3));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchEV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_EV.ID,
                "HumongousDualInputHatchEV",
                StatCollector.translateToLocal("HumongousDualInputHatchEV"),
                4));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchIV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_IV.ID,
                "HumongousDualInputHatchIV",
                StatCollector.translateToLocal("HumongousDualInputHatchIV"),
                5));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchLuV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_LUV.ID,
                "HumongousDualInputHatchLuV",
                StatCollector.translateToLocal("HumongousDualInputHatchLuV"),
                6));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchLuV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchZPM.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_ZPM.ID,
                "HumongousDualInputHatchZPM",
                StatCollector.translateToLocal("HumongousDualInputHatchZPM"),
                7));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchZPM.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_UV.ID,
                "HumongousDualInputHatchUV",
                StatCollector.translateToLocal("HumongousDualInputHatchUV"),
                8));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchUV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUHV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_UHV.ID,
                "HumongousDualInputHatchUHV",
                StatCollector.translateToLocal("HumongousDualInputHatchUHV"),
                9));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchUHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUEV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_UEV.ID,
                "HumongousDualInputHatchUEV",
                StatCollector.translateToLocal("HumongousDualInputHatchUEV"),
                10));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchUEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUIV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_UIV.ID,
                "HumongousDualInputHatchUIV",
                StatCollector.translateToLocal("HumongousDualInputHatchUIV"),
                11));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchUIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUMV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_UMV.ID,
                "HumongousDualInputHatchUMV",
                StatCollector.translateToLocal("HumongousDualInputHatchUMV"),
                12));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchUMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchUXV.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_UXV.ID,
                "HumongousDualInputHatchUXV",
                StatCollector.translateToLocal("HumongousDualInputHatchUXV"),
                13));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchUXV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousDualInputHatchMAX.set(
            new HumongousDualInputHatch(
                HUMONGOUS_DUAL_INPUT_HATCH_MAX.ID,
                "HumongousDualInputHatchMAX",
                StatCollector.translateToLocal("HumongousDualInputHatchMAX"),
                14));
        addItemTooltip(GTNLItemList.HumongousDualInputHatchMAX.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ManaDynamoHatchLV.set(
            new ManaDynamoHatch(
                MANA_DYNAMO_HATCH_LV.ID,
                "ManaDynamoHatchLV",
                StatCollector.translateToLocal("ManaDynamoHatchLV"),
                1,
                16));
        addItemTooltip(GTNLItemList.ManaDynamoHatchLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ManaDynamoHatchHV.set(
            new ManaDynamoHatch(
                MANA_DYNAMO_HATCH_HV.ID,
                "ManaDynamoHatchHV",
                StatCollector.translateToLocal("ManaDynamoHatchHV"),
                3,
                16));
        addItemTooltip(GTNLItemList.ManaDynamoHatchHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ManaDynamoHatchIV.set(
            new ManaDynamoHatch(
                MANA_DYNAMO_HATCH_IV.ID,
                "ManaDynamoHatchIV",
                StatCollector.translateToLocal("ManaDynamoHatchIV"),
                5,
                16));
        addItemTooltip(GTNLItemList.ManaDynamoHatchIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ManaDynamoHatchZPM.set(
            new ManaDynamoHatch(
                MANA_DYNAMO_HATCH_ZPM.ID,
                "ManaDynamoHatchZPM",
                StatCollector.translateToLocal("ManaDynamoHatchZPM"),
                7,
                16));
        addItemTooltip(GTNLItemList.ManaDynamoHatchZPM.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ManaEnergyHatchLV.set(
            new ManaEnergyHatch(
                MANA_ENERGY_HATCH_LV.ID,
                "ManaEnergyHatchLV",
                StatCollector.translateToLocal("ManaEnergyHatchLV"),
                1,
                16));
        addItemTooltip(GTNLItemList.ManaEnergyHatchLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ManaEnergyHatchHV.set(
            new ManaEnergyHatch(
                MANA_ENERGY_HATCH_HV.ID,
                "ManaEnergyHatchHV",
                StatCollector.translateToLocal("ManaEnergyHatchHV"),
                3,
                16));
        addItemTooltip(GTNLItemList.ManaEnergyHatchHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ManaEnergyHatchIV.set(
            new ManaEnergyHatch(
                MANA_ENERGY_HATCH_IV.ID,
                "ManaEnergyHatchIV",
                StatCollector.translateToLocal("ManaEnergyHatchIV"),
                5,
                16));
        addItemTooltip(GTNLItemList.ManaEnergyHatchIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ManaEnergyHatchZPM.set(
            new ManaEnergyHatch(
                MANA_ENERGY_HATCH_ZPM.ID,
                "ManaEnergyHatchZPM",
                StatCollector.translateToLocal("ManaEnergyHatchZPM"),
                7,
                16));
        addItemTooltip(GTNLItemList.ManaEnergyHatchZPM.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.TypeFilteredInputBusME.set(
            new TypeFilteredInputBusME(
                TYPE_FILTERED_INPUT_BUS_ME.ID,
                "TypeFilteredInputBusME",
                StatCollector.translateToLocal("TypeFilteredInputBusME")));
        addItemTooltip(GTNLItemList.TypeFilteredInputBusME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusLV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_LV.ID,
                "HumongousOutputBusLV",
                StatCollector.translateToLocal("HumongousOutputBusLV"),
                1));
        addItemTooltip(GTNLItemList.HumongousOutputBusLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusMV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_MV.ID,
                "HumongousOutputBusMV",
                StatCollector.translateToLocal("HumongousOutputBusMV"),
                2));
        addItemTooltip(GTNLItemList.HumongousOutputBusMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusHV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_HV.ID,
                "HumongousOutputBusHV",
                StatCollector.translateToLocal("HumongousOutputBusHV"),
                3));
        addItemTooltip(GTNLItemList.HumongousOutputBusHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusEV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_EV.ID,
                "HumongousOutputBusEV",
                StatCollector.translateToLocal("HumongousOutputBusEV"),
                4));
        addItemTooltip(GTNLItemList.HumongousOutputBusEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusIV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_IV.ID,
                "HumongousOutputBusIV",
                StatCollector.translateToLocal("HumongousOutputBusIV"),
                5));
        addItemTooltip(GTNLItemList.HumongousOutputBusIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusLuV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_LUV.ID,
                "HumongousOutputBusLuV",
                StatCollector.translateToLocal("HumongousOutputBusLuV"),
                6));
        addItemTooltip(GTNLItemList.HumongousOutputBusLuV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusZPM.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_ZPM.ID,
                "HumongousOutputBusZPM",
                StatCollector.translateToLocal("HumongousOutputBusZPM"),
                7));
        addItemTooltip(GTNLItemList.HumongousOutputBusZPM.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusUV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_UV.ID,
                "HumongousOutputBusUV",
                StatCollector.translateToLocal("HumongousOutputBusUV"),
                8));
        addItemTooltip(GTNLItemList.HumongousOutputBusUV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusUHV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_UHV.ID,
                "HumongousOutputBusUHV",
                StatCollector.translateToLocal("HumongousOutputBusUHV"),
                9));
        addItemTooltip(GTNLItemList.HumongousOutputBusUHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusUEV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_UEV.ID,
                "HumongousOutputBusUEV",
                StatCollector.translateToLocal("HumongousOutputBusUEV"),
                10));
        addItemTooltip(GTNLItemList.HumongousOutputBusUEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusUIV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_UIV.ID,
                "HumongousOutputBusUIV",
                StatCollector.translateToLocal("HumongousOutputBusUIV"),
                11));
        addItemTooltip(GTNLItemList.HumongousOutputBusUIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusUMV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_UMV.ID,
                "HumongousOutputBusUMV",
                StatCollector.translateToLocal("HumongousOutputBusUMV"),
                12));
        addItemTooltip(GTNLItemList.HumongousOutputBusUMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusUXV.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_UXV.ID,
                "HumongousOutputBusUXV",
                StatCollector.translateToLocal("HumongousOutputBusUXV"),
                13));
        addItemTooltip(GTNLItemList.HumongousOutputBusUXV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousOutputBusMAX.set(
            new HumongousOutputBus(
                HUMONGOUS_OUTPUT_BUS_MAX.ID,
                "HumongousOutputBusMAX",
                StatCollector.translateToLocal("HumongousOutputBusMAX"),
                14));
        addItemTooltip(GTNLItemList.HumongousOutputBusMAX.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SuperDualInputHatchME.set(
            new SuperDualInputHatchME(
                SUPER_DUAL_INPUT_HATCH_ME.ID,
                "SuperDualInputHatchME",
                StatCollector.translateToLocal("SuperDualInputHatchME"),
                9,
                false));
        addItemTooltip(GTNLItemList.SuperDualInputHatchME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.AdvancedSuperDualInputHatchME.set(
            new SuperDualInputHatchME(
                ADVANCED_SUPER_DUAL_INPUT_HATCH_ME.ID,
                "AdvancedSuperDualInputHatchME",
                StatCollector.translateToLocal("AdvancedSuperDualInputHatchME"),
                10,
                true));
        addItemTooltip(GTNLItemList.AdvancedSuperDualInputHatchME.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchLV.set(ItemList.Hatch_Energy_LV.get(1));

        GTNLItemList.EnergyHatchLV4A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_LV_4A.ID,
                "EnergyHatchLV4A",
                StatCollector.translateToLocal("EnergyHatchLV4A"),
                1,
                4));
        addItemTooltip(GTNLItemList.EnergyHatchLV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchLV16A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_LV_16A.ID,
                "EnergyHatchLV16A",
                StatCollector.translateToLocal("EnergyHatchLV16A"),
                1,
                16));
        addItemTooltip(GTNLItemList.EnergyHatchLV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchLV64A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_LV_64A.ID,
                "EnergyHatchLV64A",
                StatCollector.translateToLocal("EnergyHatchLV64A"),
                1,
                64));
        addItemTooltip(GTNLItemList.EnergyHatchLV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchMV.set(ItemList.Hatch_Energy_MV.get(1));

        GTNLItemList.EnergyHatchMV4A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_MV_4A.ID,
                "EnergyHatchMV4A",
                StatCollector.translateToLocal("EnergyHatchMV4A"),
                2,
                4));
        addItemTooltip(GTNLItemList.EnergyHatchMV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchMV16A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_MV_16A.ID,
                "EnergyHatchMV16A",
                StatCollector.translateToLocal("EnergyHatchMV16A"),
                2,
                16));
        addItemTooltip(GTNLItemList.EnergyHatchMV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchMV64A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_MV_64A.ID,
                "EnergyHatchMV64A",
                StatCollector.translateToLocal("EnergyHatchMV64A"),
                2,
                64));
        addItemTooltip(GTNLItemList.EnergyHatchMV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchHV.set(ItemList.Hatch_Energy_HV.get(1));

        GTNLItemList.EnergyHatchHV4A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_HV_4A.ID,
                "EnergyHatchHV4A",
                StatCollector.translateToLocal("EnergyHatchHV4A"),
                3,
                4));
        addItemTooltip(GTNLItemList.EnergyHatchHV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchHV16A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_HV_16A.ID,
                "EnergyHatchHV16A",
                StatCollector.translateToLocal("EnergyHatchHV16A"),
                3,
                16));
        addItemTooltip(GTNLItemList.EnergyHatchHV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchHV64A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_HV_64A.ID,
                "EnergyHatchHV64A",
                StatCollector.translateToLocal("EnergyHatchHV64A"),
                3,
                64));
        addItemTooltip(GTNLItemList.EnergyHatchHV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchEV.set(ItemList.Hatch_Energy_EV.get(1));

        GTNLItemList.EnergyHatchEV4A.set(CustomItemList.eM_energyMulti4_EV.get(1));

        GTNLItemList.EnergyHatchEV16A.set(CustomItemList.eM_energyMulti16_EV.get(1));

        GTNLItemList.EnergyHatchEV64A.set(CustomItemList.eM_energyMulti64_EV.get(1));

        GTNLItemList.EnergyHatchIV.set(ItemList.Hatch_Energy_IV.get(1));

        GTNLItemList.EnergyHatchIV4A.set(CustomItemList.eM_energyMulti4_IV.get(1));

        GTNLItemList.EnergyHatchIV16A.set(CustomItemList.eM_energyMulti16_IV.get(1));

        GTNLItemList.EnergyHatchIV64A.set(CustomItemList.eM_energyMulti64_IV.get(1));

        GTNLItemList.EnergyHatchLuV.set(ItemList.Hatch_Energy_LuV.get(1));

        GTNLItemList.EnergyHatchLuV4A.set(CustomItemList.eM_energyMulti4_LuV.get(1));

        GTNLItemList.EnergyHatchLuV16A.set(CustomItemList.eM_energyMulti16_LuV.get(1));

        GTNLItemList.EnergyHatchLuV64A.set(CustomItemList.eM_energyMulti64_LuV.get(1));

        GTNLItemList.EnergyHatchZPM.set(ItemList.Hatch_Energy_ZPM.get(1));

        GTNLItemList.EnergyHatchZPM4A.set(CustomItemList.eM_energyMulti4_ZPM.get(1));

        GTNLItemList.EnergyHatchZPM16A.set(CustomItemList.eM_energyMulti16_ZPM.get(1));

        GTNLItemList.EnergyHatchZPM64A.set(CustomItemList.eM_energyMulti64_ZPM.get(1));

        GTNLItemList.EnergyHatchUV.set(ItemList.Hatch_Energy_UV.get(1));

        GTNLItemList.EnergyHatchUV4A.set(CustomItemList.eM_energyMulti4_UV.get(1));

        GTNLItemList.EnergyHatchUV16A.set(CustomItemList.eM_energyMulti16_UV.get(1));

        GTNLItemList.EnergyHatchUV64A.set(CustomItemList.eM_energyMulti64_UV.get(1));

        GTNLItemList.EnergyHatchUHV.set(ItemList.Hatch_Energy_UHV.get(1));

        GTNLItemList.EnergyHatchUHV4A.set(CustomItemList.eM_energyMulti4_UHV.get(1));

        GTNLItemList.EnergyHatchUHV16A.set(CustomItemList.eM_energyMulti16_UHV.get(1));

        GTNLItemList.EnergyHatchUHV64A.set(CustomItemList.eM_energyMulti64_UHV.get(1));

        GTNLItemList.EnergyHatchUEV.set(ItemList.Hatch_Energy_UEV.get(1));

        GTNLItemList.EnergyHatchUEV4A.set(CustomItemList.eM_energyMulti4_UEV.get(1));

        GTNLItemList.EnergyHatchUEV16A.set(CustomItemList.eM_energyMulti16_UEV.get(1));

        GTNLItemList.EnergyHatchUEV64A.set(CustomItemList.eM_energyMulti64_UEV.get(1));

        GTNLItemList.EnergyHatchUIV.set(ItemList.Hatch_Energy_UIV.get(1));

        GTNLItemList.EnergyHatchUIV4A.set(CustomItemList.eM_energyMulti4_UIV.get(1));

        GTNLItemList.EnergyHatchUIV16A.set(CustomItemList.eM_energyMulti16_UIV.get(1));

        GTNLItemList.EnergyHatchUIV64A.set(CustomItemList.eM_energyMulti64_UIV.get(1));

        GTNLItemList.EnergyHatchUMV.set(ItemList.Hatch_Energy_UMV.get(1));

        GTNLItemList.EnergyHatchUMV4A.set(CustomItemList.eM_energyMulti4_UMV.get(1));

        GTNLItemList.EnergyHatchUMV16A.set(CustomItemList.eM_energyMulti16_UMV.get(1));

        GTNLItemList.EnergyHatchUMV64A.set(CustomItemList.eM_energyMulti64_UMV.get(1));

        GTNLItemList.EnergyHatchUXV.set(ItemList.Hatch_Energy_UXV.get(1));

        GTNLItemList.EnergyHatchUXV4A.set(CustomItemList.eM_energyMulti4_UXV.get(1));

        GTNLItemList.EnergyHatchUXV16A.set(CustomItemList.eM_energyMulti16_UXV.get(1));

        GTNLItemList.EnergyHatchUXV64A.set(CustomItemList.eM_energyMulti64_UXV.get(1));

        GTNLItemList.EnergyHatchMAX.set(
            new MTEHatchEnergy(
                ENERGY_HATCH_MAX.ID,
                "EnergyHatchMAX",
                StatCollector.translateToLocal("EnergyHatchMAX"),
                14));
        addItemTooltip(GTNLItemList.EnergyHatchMAX.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchMAX4A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_MAX_4A.ID,
                "EnergyHatchMAX4A",
                StatCollector.translateToLocal("EnergyHatchMAX4A"),
                14,
                4));
        addItemTooltip(GTNLItemList.EnergyHatchMAX4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchMAX16A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_MAX_16A.ID,
                "EnergyHatchMAX16A",
                StatCollector.translateToLocal("EnergyHatchMAX16A"),
                14,
                16));
        addItemTooltip(GTNLItemList.EnergyHatchMAX16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyHatchMAX64A.set(
            new MTEHatchEnergyMulti(
                ENERGY_HATCH_MAX_64A.ID,
                "EnergyHatchMAX64A",
                StatCollector.translateToLocal("EnergyHatchMAX64A"),
                14,
                64));
        addItemTooltip(GTNLItemList.EnergyHatchMAX64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DynamoHatchLV.set(ItemList.Hatch_Dynamo_LV.get(1));

        GTNLItemList.DynamoHatchLV4A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_LV_4A.ID,
                "DynamoHatchLV4A",
                StatCollector.translateToLocal("DynamoHatchLV4A"),
                1,
                4));

        GTNLItemList.DynamoHatchLV16A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_LV_16A.ID,
                "DynamoHatchLV16A",
                StatCollector.translateToLocal("DynamoHatchLV16A"),
                1,
                16));

        GTNLItemList.DynamoHatchLV64A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_LV_64A.ID,
                "DynamoHatchLV64A",
                StatCollector.translateToLocal("DynamoHatchLV64A"),
                1,
                64));

        GTNLItemList.DynamoHatchMV.set(ItemList.Hatch_Dynamo_MV.get(1));

        GTNLItemList.DynamoHatchMV4A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_MV_4A.ID,
                "DynamoHatchMV4A",
                StatCollector.translateToLocal("DynamoHatchMV4A"),
                2,
                4));

        GTNLItemList.DynamoHatchMV16A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_MV_16A.ID,
                "DynamoHatchMV16A",
                StatCollector.translateToLocal("DynamoHatchMV16A"),
                2,
                16));

        GTNLItemList.DynamoHatchMV64A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_MV_64A.ID,
                "DynamoHatchMV64A",
                StatCollector.translateToLocal("DynamoHatchMV64A"),
                2,
                64));

        GTNLItemList.DynamoHatchHV.set(ItemList.Hatch_Dynamo_HV.get(1));

        GTNLItemList.DynamoHatchHV4A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_HV_4A.ID,
                "DynamoHatchHV4A",
                StatCollector.translateToLocal("DynamoHatchHV4A"),
                3,
                4));

        GTNLItemList.DynamoHatchHV16A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_HV_16A.ID,
                "DynamoHatchHV16A",
                StatCollector.translateToLocal("DynamoHatchHV16A"),
                3,
                16));

        GTNLItemList.DynamoHatchHV64A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_HV_64A.ID,
                "DynamoHatchHV64A",
                StatCollector.translateToLocal("DynamoHatchHV64A"),
                3,
                64));

        GTNLItemList.DynamoHatchEV.set(ItemList.Hatch_Dynamo_EV.get(1));

        GTNLItemList.DynamoHatchEV4A.set(CustomItemList.eM_dynamoMulti4_EV.get(1));

        GTNLItemList.DynamoHatchEV16A.set(CustomItemList.eM_dynamoMulti16_EV.get(1));

        GTNLItemList.DynamoHatchEV64A.set(CustomItemList.eM_dynamoMulti64_EV.get(1));

        GTNLItemList.DynamoHatchIV.set(ItemList.Hatch_Dynamo_IV.get(1));

        GTNLItemList.DynamoHatchIV4A.set(CustomItemList.eM_dynamoMulti4_IV.get(1));

        GTNLItemList.DynamoHatchIV16A.set(CustomItemList.eM_dynamoMulti16_IV.get(1));

        GTNLItemList.DynamoHatchIV64A.set(CustomItemList.eM_dynamoMulti64_IV.get(1));

        GTNLItemList.DynamoHatchLuV.set(ItemList.Hatch_Dynamo_LuV.get(1));

        GTNLItemList.DynamoHatchLuV4A.set(CustomItemList.eM_dynamoMulti4_LuV.get(1));

        GTNLItemList.DynamoHatchLuV16A.set(CustomItemList.eM_dynamoMulti16_LuV.get(1));

        GTNLItemList.DynamoHatchLuV64A.set(CustomItemList.eM_dynamoMulti64_LuV.get(1));

        GTNLItemList.DynamoHatchZPM.set(ItemList.Hatch_Dynamo_ZPM.get(1));

        GTNLItemList.DynamoHatchZPM4A.set(CustomItemList.eM_dynamoMulti4_ZPM.get(1));

        GTNLItemList.DynamoHatchZPM16A.set(CustomItemList.eM_dynamoMulti16_ZPM.get(1));

        GTNLItemList.DynamoHatchZPM64A.set(CustomItemList.eM_dynamoMulti64_ZPM.get(1));

        GTNLItemList.DynamoHatchUV.set(ItemList.Hatch_Dynamo_UV.get(1));

        GTNLItemList.DynamoHatchUV4A.set(CustomItemList.eM_dynamoMulti4_UV.get(1));

        GTNLItemList.DynamoHatchUV16A.set(CustomItemList.eM_dynamoMulti16_UV.get(1));

        GTNLItemList.DynamoHatchUV64A.set(CustomItemList.eM_dynamoMulti64_UV.get(1));

        GTNLItemList.DynamoHatchUHV.set(ItemList.Hatch_Dynamo_UHV.get(1));

        GTNLItemList.DynamoHatchUHV4A.set(CustomItemList.eM_dynamoMulti4_UHV.get(1));

        GTNLItemList.DynamoHatchUHV16A.set(CustomItemList.eM_dynamoMulti16_UHV.get(1));

        GTNLItemList.DynamoHatchUHV64A.set(CustomItemList.eM_dynamoMulti64_UHV.get(1));

        GTNLItemList.DynamoHatchUEV.set(ItemList.Hatch_Dynamo_UEV.get(1));

        GTNLItemList.DynamoHatchUEV4A.set(CustomItemList.eM_dynamoMulti4_UEV.get(1));

        GTNLItemList.DynamoHatchUEV16A.set(CustomItemList.eM_dynamoMulti16_UEV.get(1));

        GTNLItemList.DynamoHatchUEV64A.set(CustomItemList.eM_dynamoMulti64_UEV.get(1));

        GTNLItemList.DynamoHatchUIV.set(ItemList.Hatch_Dynamo_UIV.get(1));

        GTNLItemList.DynamoHatchUIV4A.set(CustomItemList.eM_dynamoMulti4_UIV.get(1));

        GTNLItemList.DynamoHatchUIV16A.set(CustomItemList.eM_dynamoMulti16_UIV.get(1));

        GTNLItemList.DynamoHatchUIV64A.set(CustomItemList.eM_dynamoMulti64_UIV.get(1));

        GTNLItemList.DynamoHatchUMV.set(ItemList.Hatch_Dynamo_UMV.get(1));

        GTNLItemList.DynamoHatchUMV4A.set(CustomItemList.eM_dynamoMulti4_UMV.get(1));

        GTNLItemList.DynamoHatchUMV16A.set(CustomItemList.eM_dynamoMulti16_UMV.get(1));

        GTNLItemList.DynamoHatchUMV64A.set(CustomItemList.eM_dynamoMulti64_UMV.get(1));

        GTNLItemList.DynamoHatchUXV.set(ItemList.Hatch_Dynamo_UXV.get(1));

        GTNLItemList.DynamoHatchUXV4A.set(CustomItemList.eM_dynamoMulti4_UXV.get(1));

        GTNLItemList.DynamoHatchUXV16A.set(CustomItemList.eM_dynamoMulti16_UXV.get(1));

        GTNLItemList.DynamoHatchUXV64A.set(CustomItemList.eM_dynamoMulti64_UXV.get(1));

        GTNLItemList.DynamoHatchMAX.set(
            new MTEHatchDynamo(
                DYNAMO_HATCH_MAX.ID,
                "DynamoHatchMAX",
                StatCollector.translateToLocal("DynamoHatchMAX"),
                14));

        GTNLItemList.DynamoHatchMAX4A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_MAX_4A.ID,
                "DynamoHatchMAX4A",
                StatCollector.translateToLocal("DynamoHatchMAX4A"),
                14,
                4));

        GTNLItemList.DynamoHatchMAX16A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_MAX_16A.ID,
                "DynamoHatchMAX16A",
                StatCollector.translateToLocal("DynamoHatchMAX16A"),
                14,
                16));

        GTNLItemList.DynamoHatchMAX64A.set(
            new MTEHatchDynamoMulti(
                DYNAMO_HATCH_MAX_64A.ID,
                "DynamoHatchMAX64A",
                StatCollector.translateToLocal("DynamoHatchMAX64A"),
                14,
                64));

        GTNLItemList.WirelessEnergyHatchLV.set(ItemList.Wireless_Hatch_Energy_LV.get(1));

        GTNLItemList.WirelessEnergyHatchLV4A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LV_4A.ID,
                "WirelessEnergyHatchLV4A",
                StatCollector.translateToLocal("WirelessEnergyHatchLV4A"),
                1,
                4));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLV16A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LV_16A.ID,
                "WirelessEnergyHatchLV16A",
                StatCollector.translateToLocal("WirelessEnergyHatchLV16A"),
                1,
                16));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLV64A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LV_64A.ID,
                "WirelessEnergyHatchLV64A",
                StatCollector.translateToLocal("WirelessEnergyHatchLV64A"),
                1,
                64));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMV.set(ItemList.Wireless_Hatch_Energy_MV.get(1));

        GTNLItemList.WirelessEnergyHatchMV4A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MV_4A.ID,
                "WirelessEnergyHatchMV4A",
                StatCollector.translateToLocal("WirelessEnergyHatchMV4A"),
                2,
                4));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMV16A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MV_16A.ID,
                "WirelessEnergyHatchMV16A",
                StatCollector.translateToLocal("WirelessEnergyHatchMV16A"),
                2,
                16));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMV64A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MV_64A.ID,
                "WirelessEnergyHatchMV64A",
                StatCollector.translateToLocal("WirelessEnergyHatchMV64A"),
                2,
                64));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchHV.set(ItemList.Wireless_Hatch_Energy_HV.get(1));

        GTNLItemList.WirelessEnergyHatchHV4A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_HV_4A.ID,
                "WirelessEnergyHatchHV4A",
                StatCollector.translateToLocal("WirelessEnergyHatchHV4A"),
                3,
                4));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchHV4A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchHV16A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_HV_16A.ID,
                "WirelessEnergyHatchHV16A",
                StatCollector.translateToLocal("WirelessEnergyHatchHV16A"),
                3,
                16));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchHV16A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchHV64A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_HV_64A.ID,
                "WirelessEnergyHatchHV64A",
                StatCollector.translateToLocal("WirelessEnergyHatchHV64A"),
                3,
                64));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchHV64A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchEV.set(ItemList.Wireless_Hatch_Energy_EV.get(1));

        GTNLItemList.WirelessEnergyHatchEV4A.set(CustomItemList.eM_energyWirelessMulti4_EV.get(1));

        GTNLItemList.WirelessEnergyHatchEV16A.set(CustomItemList.eM_energyWirelessMulti16_EV.get(1));

        GTNLItemList.WirelessEnergyHatchEV64A.set(CustomItemList.eM_energyWirelessMulti64_EV.get(1));

        GTNLItemList.LaserEnergyHatchIV256A.set(CustomItemList.eM_energyTunnel1_IV.get(1));

        GTNLItemList.LaserEnergyHatchIV1024A.set(CustomItemList.eM_energyTunnel2_IV.get(1));

        GTNLItemList.LaserEnergyHatchIV4096A.set(CustomItemList.eM_energyTunnel3_IV.get(1));

        GTNLItemList.LaserEnergyHatchIV16384A.set(CustomItemList.eM_energyTunnel4_IV.get(1));

        GTNLItemList.LaserEnergyHatchIV65536A.set(CustomItemList.eM_energyTunnel5_IV.get(1));

        GTNLItemList.LaserEnergyHatchIV262144A.set(CustomItemList.eM_energyTunnel6_IV.get(1));

        GTNLItemList.LaserEnergyHatchIV1048576A.set(CustomItemList.eM_energyTunnel7_IV.get(1));

        GTNLItemList.LaserEnergyHatchIV4194304A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_IV_4194304A.ID,
                "LaserEnergyHatchIV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchIV4194304A"),
                5,
                4194304));
        addItemTooltip(GTNLItemList.LaserEnergyHatchIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchIV16777216A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_IV_16777216A.ID,
                "LaserEnergyHatchIV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchIV16777216A"),
                5,
                16777216));
        addItemTooltip(GTNLItemList.LaserEnergyHatchIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchLuV256A.set(CustomItemList.eM_energyTunnel1_LuV.get(1));

        GTNLItemList.LaserEnergyHatchLuV1024A.set(CustomItemList.eM_energyTunnel2_LuV.get(1));

        GTNLItemList.LaserEnergyHatchLuV4096A.set(CustomItemList.eM_energyTunnel3_LuV.get(1));

        GTNLItemList.LaserEnergyHatchLuV16384A.set(CustomItemList.eM_energyTunnel4_LuV.get(1));

        GTNLItemList.LaserEnergyHatchLuV65536A.set(CustomItemList.eM_energyTunnel5_LuV.get(1));

        GTNLItemList.LaserEnergyHatchLuV262144A.set(CustomItemList.eM_energyTunnel6_LuV.get(1));

        GTNLItemList.LaserEnergyHatchLuV1048576A.set(CustomItemList.eM_energyTunnel7_LuV.get(1));

        GTNLItemList.LaserEnergyHatchLuV4194304A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_LUV_4194304A.ID,
                "LaserEnergyHatchLuV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchLuV4194304A"),
                6,
                4194304));
        addItemTooltip(GTNLItemList.LaserEnergyHatchLuV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchLuV16777216A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_LUV_16777216A.ID,
                "LaserEnergyHatchLuV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchLuV16777216A"),
                6,
                16777216));
        addItemTooltip(GTNLItemList.LaserEnergyHatchLuV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchZPM256A.set(CustomItemList.eM_energyTunnel1_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchZPM1024A.set(CustomItemList.eM_energyTunnel2_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchZPM4096A.set(CustomItemList.eM_energyTunnel3_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchZPM16384A.set(CustomItemList.eM_energyTunnel4_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchZPM65536A.set(CustomItemList.eM_energyTunnel5_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchZPM262144A.set(CustomItemList.eM_energyTunnel6_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchZPM1048576A.set(CustomItemList.eM_energyTunnel7_ZPM.get(1));

        GTNLItemList.LaserEnergyHatchZPM4194304A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_ZPM_4194304A.ID,
                "LaserEnergyHatchZPM4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchZPM4194304A"),
                7,
                4194304));
        addItemTooltip(GTNLItemList.LaserEnergyHatchZPM4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchZPM16777216A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_ZPM_16777216A.ID,
                "LaserEnergyHatchZPM16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchZPM16777216A"),
                7,
                16777216));
        addItemTooltip(GTNLItemList.LaserEnergyHatchZPM16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUV256A.set(CustomItemList.eM_energyTunnel1_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV1024A.set(CustomItemList.eM_energyTunnel2_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV4096A.set(CustomItemList.eM_energyTunnel3_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV16384A.set(CustomItemList.eM_energyTunnel4_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV65536A.set(CustomItemList.eM_energyTunnel5_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV262144A.set(CustomItemList.eM_energyTunnel6_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV1048576A.set(CustomItemList.eM_energyTunnel7_UV.get(1));

        GTNLItemList.LaserEnergyHatchUV4194304A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_UV_4194304A.ID,
                "LaserEnergyHatchUV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchUV4194304A"),
                8,
                4194304));
        addItemTooltip(GTNLItemList.LaserEnergyHatchUV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUV16777216A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_UV_16777216A.ID,
                "LaserEnergyHatchUV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchUV16777216A"),
                8,
                16777216));
        addItemTooltip(GTNLItemList.LaserEnergyHatchUV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUHV256A.set(CustomItemList.eM_energyTunnel1_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV1024A.set(CustomItemList.eM_energyTunnel2_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV4096A.set(CustomItemList.eM_energyTunnel3_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV16384A.set(CustomItemList.eM_energyTunnel4_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV65536A.set(CustomItemList.eM_energyTunnel5_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV262144A.set(CustomItemList.eM_energyTunnel6_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV1048576A.set(CustomItemList.eM_energyTunnel7_UHV.get(1));

        GTNLItemList.LaserEnergyHatchUHV4194304A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_UHV_4194304A.ID,
                "LaserEnergyHatchUHV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchUHV4194304A"),
                9,
                4194304));
        addItemTooltip(GTNLItemList.LaserEnergyHatchUHV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUHV16777216A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_UHV_16777216A.ID,
                "LaserEnergyHatchUHV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchUHV16777216A"),
                9,
                16777216));
        addItemTooltip(GTNLItemList.LaserEnergyHatchUHV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUEV256A.set(CustomItemList.eM_energyTunnel1_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV1024A.set(CustomItemList.eM_energyTunnel2_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV4096A.set(CustomItemList.eM_energyTunnel3_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV16384A.set(CustomItemList.eM_energyTunnel4_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV65536A.set(CustomItemList.eM_energyTunnel5_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV262144A.set(CustomItemList.eM_energyTunnel6_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV1048576A.set(CustomItemList.eM_energyTunnel7_UEV.get(1));

        GTNLItemList.LaserEnergyHatchUEV4194304A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_UEV_4194304A.ID,
                "LaserEnergyHatchUEV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchUEV4194304A"),
                10,
                4194304));
        addItemTooltip(GTNLItemList.LaserEnergyHatchUEV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUEV16777216A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_UEV_16777216A.ID,
                "LaserEnergyHatchUEV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchUEV16777216A"),
                10,
                16777216));
        addItemTooltip(GTNLItemList.LaserEnergyHatchUEV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUIV256A.set(CustomItemList.eM_energyTunnel1_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV1024A.set(CustomItemList.eM_energyTunnel2_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV4096A.set(CustomItemList.eM_energyTunnel3_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV16384A.set(CustomItemList.eM_energyTunnel4_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV65536A.set(CustomItemList.eM_energyTunnel5_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV262144A.set(CustomItemList.eM_energyTunnel6_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV1048576A.set(CustomItemList.eM_energyTunnel7_UIV.get(1));

        GTNLItemList.LaserEnergyHatchUIV4194304A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_UIV_4194304A.ID,
                "LaserEnergyHatchUIV4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchUIV4194304A"),
                11,
                4194304));
        addItemTooltip(GTNLItemList.LaserEnergyHatchUIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUIV16777216A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_UIV_16777216A.ID,
                "LaserEnergyHatchUIV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchUIV16777216A"),
                11,
                16777216));
        addItemTooltip(GTNLItemList.LaserEnergyHatchUIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUMV256A.set(CustomItemList.eM_energyTunnel1_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV1024A.set(CustomItemList.eM_energyTunnel2_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV4096A.set(CustomItemList.eM_energyTunnel3_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV16384A.set(CustomItemList.eM_energyTunnel4_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV65536A.set(CustomItemList.eM_energyTunnel5_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV262144A.set(CustomItemList.eM_energyTunnel6_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV1048576A.set(CustomItemList.eM_energyTunnel7_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV4194304A.set(CustomItemList.eM_energyTunnel8_UMV.get(1));

        GTNLItemList.LaserEnergyHatchUMV16777216A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_UMV_16777216A.ID,
                "LaserEnergyHatchUMV16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchUMV16777216A"),
                12,
                16777216));
        addItemTooltip(GTNLItemList.LaserEnergyHatchUMV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchUXV256A.set(CustomItemList.eM_energyTunnel1_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV1024A.set(CustomItemList.eM_energyTunnel2_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV4096A.set(CustomItemList.eM_energyTunnel3_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV16384A.set(CustomItemList.eM_energyTunnel4_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV65536A.set(CustomItemList.eM_energyTunnel5_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV262144A.set(CustomItemList.eM_energyTunnel6_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV1048576A.set(CustomItemList.eM_energyTunnel7_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV4194304A.set(CustomItemList.eM_energyTunnel8_UXV.get(1));

        GTNLItemList.LaserEnergyHatchUXV16777216A.set(CustomItemList.eM_energyTunnel9_UXV.get(1));

        GTNLItemList.LaserEnergyHatchMAX256A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_MAX_256A.ID,
                "LaserEnergyHatchMAX256A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX256A"),
                14,
                256));
        addItemTooltip(GTNLItemList.LaserEnergyHatchMAX256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX1024A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_MAX_1024A.ID,
                "LaserEnergyHatchMAX1024A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX1024A"),
                14,
                1024));
        addItemTooltip(GTNLItemList.LaserEnergyHatchMAX1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX4096A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_MAX_4096A.ID,
                "LaserEnergyHatchMAX4096A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX4096A"),
                14,
                4096));
        addItemTooltip(GTNLItemList.LaserEnergyHatchMAX4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX16384A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_MAX_16384A.ID,
                "LaserEnergyHatchMAX16384A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX16384A"),
                14,
                16384));
        addItemTooltip(GTNLItemList.LaserEnergyHatchMAX16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX65536A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_MAX_65536A.ID,
                "LaserEnergyHatchMAX65536A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX65536A"),
                14,
                65536));
        addItemTooltip(GTNLItemList.LaserEnergyHatchMAX65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX262144A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_MAX_262144A.ID,
                "LaserEnergyHatchMAX262144A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX262144A"),
                14,
                262144));
        addItemTooltip(GTNLItemList.LaserEnergyHatchMAX262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX1048576A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_MAX_1048576A.ID,
                "LaserEnergyHatchMAX1048576A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX1048576A"),
                14,
                1048576));
        addItemTooltip(GTNLItemList.LaserEnergyHatchMAX1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX4194304A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_MAX_4194304A.ID,
                "LaserEnergyHatchMAX4194304A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX4194304A"),
                14,
                4194304));
        addItemTooltip(GTNLItemList.LaserEnergyHatchMAX4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserEnergyHatchMAX16777216A.set(
            new MTEHatchEnergyTunnel(
                LASER_ENERGY_HATCH_MAX_16777216A.ID,
                "LaserEnergyHatchMAX16777216A",
                StatCollector.translateToLocal("LaserEnergyHatchMAX16777216A"),
                14,
                16777216));
        addItemTooltip(GTNLItemList.LaserEnergyHatchMAX16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchIV256A.set(CustomItemList.eM_dynamoTunnel1_IV.get(1));

        GTNLItemList.LaserDynamoHatchIV1024A.set(CustomItemList.eM_dynamoTunnel2_IV.get(1));

        GTNLItemList.LaserDynamoHatchIV4096A.set(CustomItemList.eM_dynamoTunnel3_IV.get(1));

        GTNLItemList.LaserDynamoHatchIV16384A.set(CustomItemList.eM_dynamoTunnel4_IV.get(1));

        GTNLItemList.LaserDynamoHatchIV65536A.set(CustomItemList.eM_dynamoTunnel5_IV.get(1));

        GTNLItemList.LaserDynamoHatchIV262144A.set(CustomItemList.eM_dynamoTunnel6_IV.get(1));

        GTNLItemList.LaserDynamoHatchIV1048576A.set(CustomItemList.eM_dynamoTunnel7_IV.get(1));

        GTNLItemList.LaserDynamoHatchIV4194304A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_IV_4194304A.ID,
                "LaserDynamoHatchIV4194304A",
                StatCollector.translateToLocal("LaserDynamoHatchIV4194304A"),
                5,
                4194304));
        addItemTooltip(GTNLItemList.LaserDynamoHatchIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchIV16777216A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_IV_16777216A.ID,
                "LaserDynamoHatchIV16777216A",
                StatCollector.translateToLocal("LaserDynamoHatchIV16777216A"),
                5,
                16777216));
        addItemTooltip(GTNLItemList.LaserDynamoHatchIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchLuV256A.set(CustomItemList.eM_dynamoTunnel1_LuV.get(1));

        GTNLItemList.LaserDynamoHatchLuV1024A.set(CustomItemList.eM_dynamoTunnel2_LuV.get(1));

        GTNLItemList.LaserDynamoHatchLuV4096A.set(CustomItemList.eM_dynamoTunnel3_LuV.get(1));

        GTNLItemList.LaserDynamoHatchLuV16384A.set(CustomItemList.eM_dynamoTunnel4_LuV.get(1));

        GTNLItemList.LaserDynamoHatchLuV65536A.set(CustomItemList.eM_dynamoTunnel5_LuV.get(1));

        GTNLItemList.LaserDynamoHatchLuV262144A.set(CustomItemList.eM_dynamoTunnel6_LuV.get(1));

        GTNLItemList.LaserDynamoHatchLuV1048576A.set(CustomItemList.eM_dynamoTunnel7_LuV.get(1));

        GTNLItemList.LaserDynamoHatchLuV4194304A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_LUV_4194304A.ID,
                "LaserDynamoHatchLuV4194304A",
                StatCollector.translateToLocal("LaserDynamoHatchLuV4194304A"),
                6,
                4194304));
        addItemTooltip(GTNLItemList.LaserDynamoHatchLuV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchLuV16777216A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_LUV_16777216A.ID,
                "LaserDynamoHatchLuV16777216A",
                StatCollector.translateToLocal("LaserDynamoHatchLuV16777216A"),
                6,
                16777216));
        addItemTooltip(GTNLItemList.LaserDynamoHatchLuV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchZPM256A.set(CustomItemList.eM_dynamoTunnel1_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchZPM1024A.set(CustomItemList.eM_dynamoTunnel2_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchZPM4096A.set(CustomItemList.eM_dynamoTunnel3_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchZPM16384A.set(CustomItemList.eM_dynamoTunnel4_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchZPM65536A.set(CustomItemList.eM_dynamoTunnel5_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchZPM262144A.set(CustomItemList.eM_dynamoTunnel6_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchZPM1048576A.set(CustomItemList.eM_dynamoTunnel7_ZPM.get(1));

        GTNLItemList.LaserDynamoHatchZPM4194304A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_ZPM_4194304A.ID,
                "LaserDynamoHatchZPM4194304A",
                StatCollector.translateToLocal("LaserDynamoHatchZPM4194304A"),
                7,
                4194304));
        addItemTooltip(GTNLItemList.LaserDynamoHatchZPM4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchZPM16777216A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_ZPM_16777216A.ID,
                "LaserDynamoHatchZPM16777216A",
                StatCollector.translateToLocal("LaserDynamoHatchZPM16777216A"),
                7,
                16777216));
        addItemTooltip(GTNLItemList.LaserDynamoHatchZPM16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUV256A.set(CustomItemList.eM_dynamoTunnel1_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV1024A.set(CustomItemList.eM_dynamoTunnel2_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV4096A.set(CustomItemList.eM_dynamoTunnel3_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV16384A.set(CustomItemList.eM_dynamoTunnel4_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV65536A.set(CustomItemList.eM_dynamoTunnel5_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV262144A.set(CustomItemList.eM_dynamoTunnel6_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV1048576A.set(CustomItemList.eM_dynamoTunnel7_UV.get(1));

        GTNLItemList.LaserDynamoHatchUV4194304A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_UV_4194304A.ID,
                "LaserDynamoHatchUV4194304A",
                StatCollector.translateToLocal("LaserDynamoHatchUV4194304A"),
                8,
                4194304));
        addItemTooltip(GTNLItemList.LaserDynamoHatchUV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUV16777216A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_UV_16777216A.ID,
                "LaserDynamoHatchUV16777216A",
                StatCollector.translateToLocal("LaserDynamoHatchUV16777216A"),
                8,
                16777216));
        addItemTooltip(GTNLItemList.LaserDynamoHatchUV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUHV256A.set(CustomItemList.eM_dynamoTunnel1_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV1024A.set(CustomItemList.eM_dynamoTunnel2_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV4096A.set(CustomItemList.eM_dynamoTunnel3_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV16384A.set(CustomItemList.eM_dynamoTunnel4_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV65536A.set(CustomItemList.eM_dynamoTunnel5_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV262144A.set(CustomItemList.eM_dynamoTunnel6_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV1048576A.set(CustomItemList.eM_dynamoTunnel7_UHV.get(1));

        GTNLItemList.LaserDynamoHatchUHV4194304A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_UHV_4194304A.ID,
                "LaserDynamoHatchUHV4194304A",
                StatCollector.translateToLocal("LaserDynamoHatchUHV4194304A"),
                9,
                4194304));
        addItemTooltip(GTNLItemList.LaserDynamoHatchUHV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUHV16777216A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_UHV_16777216A.ID,
                "LaserDynamoHatchUHV16777216A",
                StatCollector.translateToLocal("LaserDynamoHatchUHV16777216A"),
                9,
                16777216));
        addItemTooltip(GTNLItemList.LaserDynamoHatchUHV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUEV256A.set(CustomItemList.eM_dynamoTunnel1_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV1024A.set(CustomItemList.eM_dynamoTunnel2_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV4096A.set(CustomItemList.eM_dynamoTunnel3_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV16384A.set(CustomItemList.eM_dynamoTunnel4_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV65536A.set(CustomItemList.eM_dynamoTunnel5_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV262144A.set(CustomItemList.eM_dynamoTunnel6_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV1048576A.set(CustomItemList.eM_dynamoTunnel7_UEV.get(1));

        GTNLItemList.LaserDynamoHatchUEV4194304A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_UEV_4194304A.ID,
                "LaserDynamoHatchUEV4194304A",
                StatCollector.translateToLocal("LaserDynamoHatchUEV4194304A"),
                10,
                4194304));
        addItemTooltip(GTNLItemList.LaserDynamoHatchUEV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUEV16777216A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_UEV_16777216A.ID,
                "LaserDynamoHatchUEV16777216A",
                StatCollector.translateToLocal("LaserDynamoHatchUEV16777216A"),
                10,
                16777216));
        addItemTooltip(GTNLItemList.LaserDynamoHatchUEV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUIV256A.set(CustomItemList.eM_dynamoTunnel1_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV1024A.set(CustomItemList.eM_dynamoTunnel2_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV4096A.set(CustomItemList.eM_dynamoTunnel3_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV16384A.set(CustomItemList.eM_dynamoTunnel4_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV65536A.set(CustomItemList.eM_dynamoTunnel5_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV262144A.set(CustomItemList.eM_dynamoTunnel6_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV1048576A.set(CustomItemList.eM_dynamoTunnel7_UIV.get(1));

        GTNLItemList.LaserDynamoHatchUIV4194304A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_UIV_4194304A.ID,
                "LaserDynamoHatchUIV4194304A",
                StatCollector.translateToLocal("LaserDynamoHatchUIV4194304A"),
                11,
                4194304));
        addItemTooltip(GTNLItemList.LaserDynamoHatchUIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUIV16777216A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_UIV_16777216A.ID,
                "LaserDynamoHatchUIV16777216A",
                StatCollector.translateToLocal("LaserDynamoHatchUIV16777216A"),
                11,
                16777216));
        addItemTooltip(GTNLItemList.LaserDynamoHatchUIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUMV256A.set(CustomItemList.eM_dynamoTunnel1_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV1024A.set(CustomItemList.eM_dynamoTunnel2_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV4096A.set(CustomItemList.eM_dynamoTunnel3_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV16384A.set(CustomItemList.eM_dynamoTunnel4_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV65536A.set(CustomItemList.eM_dynamoTunnel5_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV262144A.set(CustomItemList.eM_dynamoTunnel6_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV1048576A.set(CustomItemList.eM_dynamoTunnel7_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV4194304A.set(CustomItemList.eM_dynamoTunnel8_UMV.get(1));

        GTNLItemList.LaserDynamoHatchUMV16777216A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_UMV_16777216A.ID,
                "LaserDynamoHatchUMV16777216A",
                StatCollector.translateToLocal("LaserDynamoHatchUMV16777216A"),
                12,
                16777216));
        addItemTooltip(GTNLItemList.LaserDynamoHatchUMV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchUXV256A.set(CustomItemList.eM_dynamoTunnel1_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV1024A.set(CustomItemList.eM_dynamoTunnel2_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV4096A.set(CustomItemList.eM_dynamoTunnel3_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV16384A.set(CustomItemList.eM_dynamoTunnel4_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV65536A.set(CustomItemList.eM_dynamoTunnel5_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV262144A.set(CustomItemList.eM_dynamoTunnel6_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV1048576A.set(CustomItemList.eM_dynamoTunnel7_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV4194304A.set(CustomItemList.eM_dynamoTunnel8_UXV.get(1));

        GTNLItemList.LaserDynamoHatchUXV16777216A.set(CustomItemList.eM_dynamoTunnel9_UXV.get(1));

        GTNLItemList.LaserDynamoHatchMAX256A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_MAX_256A.ID,
                "LaserDynamoHatchMAX256A",
                StatCollector.translateToLocal("LaserDynamoHatchMAX256A"),
                14,
                256));
        addItemTooltip(GTNLItemList.LaserDynamoHatchMAX256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX1024A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_MAX_1024A.ID,
                "LaserDynamoHatchMAX1024A",
                StatCollector.translateToLocal("LaserDynamoHatchMAX1024A"),
                14,
                1024));
        addItemTooltip(GTNLItemList.LaserDynamoHatchMAX1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX4096A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_MAX_4096A.ID,
                "LaserDynamoHatchMAX4096A",
                StatCollector.translateToLocal("LaserDynamoHatchMAX4096A"),
                14,
                4096));
        addItemTooltip(GTNLItemList.LaserDynamoHatchMAX4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX16384A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_MAX_16384A.ID,
                "LaserDynamoHatchMAX16384A",
                StatCollector.translateToLocal("LaserDynamoHatchMAX16384A"),
                14,
                16384));
        addItemTooltip(GTNLItemList.LaserDynamoHatchMAX16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX65536A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_MAX_65536A.ID,
                "LaserDynamoHatchMAX65536A",
                StatCollector.translateToLocal("LaserDynamoHatchMAX65536A"),
                14,
                65536));
        addItemTooltip(GTNLItemList.LaserDynamoHatchMAX65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX262144A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_MAX_262144A.ID,
                "LaserDynamoHatchMAX262144A",
                StatCollector.translateToLocal("LaserDynamoHatchMAX262144A"),
                14,
                262144));
        addItemTooltip(GTNLItemList.LaserDynamoHatchMAX262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX1048576A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_MAX_1048576A.ID,
                "LaserDynamoHatchMAX1048576A",
                StatCollector.translateToLocal("LaserDynamoHatchMAX1048576A"),
                14,
                1048576));
        addItemTooltip(GTNLItemList.LaserDynamoHatchMAX1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX4194304A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_MAX_4194304A.ID,
                "LaserDynamoHatchMAX4194304A",
                StatCollector.translateToLocal("LaserDynamoHatchMAX4194304A"),
                14,
                4194304));
        addItemTooltip(GTNLItemList.LaserDynamoHatchMAX4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LaserDynamoHatchMAX16777216A.set(
            new MTEHatchDynamoTunnel(
                LASER_DYNAMO_HATCH_MAX_16777216A.ID,
                "LaserDynamoHatchMAX16777216A",
                StatCollector.translateToLocal("LaserDynamoHatchMAX16777216A"),
                14,
                16777216));
        addItemTooltip(GTNLItemList.LaserDynamoHatchMAX16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV.set(ItemList.Wireless_Hatch_Energy_IV.get(1));

        GTNLItemList.WirelessEnergyHatchIV4A.set(CustomItemList.eM_energyWirelessMulti4_IV.get(1));

        GTNLItemList.WirelessEnergyHatchIV16A.set(CustomItemList.eM_energyWirelessMulti16_IV.get(1));

        GTNLItemList.WirelessEnergyHatchIV64A.set(CustomItemList.eM_energyWirelessMulti64_IV.get(1));

        GTNLItemList.WirelessEnergyHatchIV256A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_IV_256A.ID,
                "WirelessEnergyHatchIV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV256A"),
                5,
                256));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchIV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV1024A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_IV_1024A.ID,
                "WirelessEnergyHatchIV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV1024A"),
                5,
                1024));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchIV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV4096A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_IV_4096A.ID,
                "WirelessEnergyHatchIV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV4096A"),
                5,
                4096));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchIV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV16384A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_IV_16384A.ID,
                "WirelessEnergyHatchIV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV16384A"),
                5,
                16384));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchIV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV65536A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_IV_65536A.ID,
                "WirelessEnergyHatchIV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV65536A"),
                5,
                65536));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchIV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV262144A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_IV_262144A.ID,
                "WirelessEnergyHatchIV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV262144A"),
                5,
                262144));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchIV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV1048576A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_IV_1048576A.ID,
                "WirelessEnergyHatchIV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV1048576A"),
                5,
                1048576));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchIV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV4194304A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_IV_4194304A.ID,
                "WirelessEnergyHatchIV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV4194304A"),
                5,
                4194304));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchIV16777216A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_IV_16777216A.ID,
                "WirelessEnergyHatchIV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchIV16777216A"),
                5,
                16777216));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV.set(ItemList.Wireless_Hatch_Energy_LuV.get(1));

        GTNLItemList.WirelessEnergyHatchLuV4A.set(CustomItemList.eM_energyWirelessMulti4_LuV.get(1));

        GTNLItemList.WirelessEnergyHatchLuV16A.set(CustomItemList.eM_energyWirelessMulti16_LuV.get(1));

        GTNLItemList.WirelessEnergyHatchLuV64A.set(CustomItemList.eM_energyWirelessMulti64_LuV.get(1));

        GTNLItemList.WirelessEnergyHatchLuV256A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LUV_256A.ID,
                "WirelessEnergyHatchLuV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV256A"),
                6,
                256));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV1024A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LUV_1024A.ID,
                "WirelessEnergyHatchLuV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV1024A"),
                6,
                1024));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV4096A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LUV_4096A.ID,
                "WirelessEnergyHatchLuV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV4096A"),
                6,
                4096));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV16384A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LUV_16384A.ID,
                "WirelessEnergyHatchLuV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV16384A"),
                6,
                16384));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV65536A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LUV_65536A.ID,
                "WirelessEnergyHatchLuV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV65536A"),
                6,
                65536));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV262144A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LUV_262144A.ID,
                "WirelessEnergyHatchLuV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV262144A"),
                6,
                262144));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV1048576A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LUV_1048576A.ID,
                "WirelessEnergyHatchLuV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV1048576A"),
                6,
                1048576));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV4194304A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LUV_4194304A.ID,
                "WirelessEnergyHatchLuV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV4194304A"),
                6,
                4194304));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchLuV16777216A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_LUV_16777216A.ID,
                "WirelessEnergyHatchLuV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchLuV16777216A"),
                6,
                16777216));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchLuV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM.set(ItemList.Wireless_Hatch_Energy_ZPM.get(1));

        GTNLItemList.WirelessEnergyHatchZPM4A.set(CustomItemList.eM_energyWirelessMulti4_ZPM.get(1));

        GTNLItemList.WirelessEnergyHatchZPM16A.set(CustomItemList.eM_energyWirelessMulti16_ZPM.get(1));

        GTNLItemList.WirelessEnergyHatchZPM64A.set(CustomItemList.eM_energyWirelessMulti64_ZPM.get(1));

        GTNLItemList.WirelessEnergyHatchZPM256A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_ZPM_256A.ID,
                "WirelessEnergyHatchZPM256A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM256A"),
                7,
                256));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM1024A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_ZPM_1024A.ID,
                "WirelessEnergyHatchZPM1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM1024A"),
                7,
                1024));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM4096A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_ZPM_4096A.ID,
                "WirelessEnergyHatchZPM4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM4096A"),
                7,
                4096));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM16384A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_ZPM_16384A.ID,
                "WirelessEnergyHatchZPM16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM16384A"),
                7,
                16384));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM65536A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_ZPM_65536A.ID,
                "WirelessEnergyHatchZPM65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM65536A"),
                7,
                65536));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM262144A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_ZPM_262144A.ID,
                "WirelessEnergyHatchZPM262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM262144A"),
                7,
                262144));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM1048576A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_ZPM_1048576A.ID,
                "WirelessEnergyHatchZPM1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM1048576A"),
                7,
                1048576));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM4194304A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_ZPM_4194304A.ID,
                "WirelessEnergyHatchZPM4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM4194304A"),
                7,
                4194304));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchZPM16777216A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_ZPM_16777216A.ID,
                "WirelessEnergyHatchZPM16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchZPM16777216A"),
                7,
                16777216));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchZPM16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV.set(ItemList.Wireless_Hatch_Energy_UV.get(1));

        GTNLItemList.WirelessEnergyHatchUV4A.set(CustomItemList.eM_energyWirelessMulti4_UV.get(1));

        GTNLItemList.WirelessEnergyHatchUV16A.set(CustomItemList.eM_energyWirelessMulti16_UV.get(1));

        GTNLItemList.WirelessEnergyHatchUV64A.set(CustomItemList.eM_energyWirelessMulti64_UV.get(1));

        GTNLItemList.WirelessEnergyHatchUV256A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UV_256A.ID,
                "WirelessEnergyHatchUV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV256A"),
                8,
                256));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV1024A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UV_1024A.ID,
                "WirelessEnergyHatchUV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV1024A"),
                8,
                1024));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV4096A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UV_4096A.ID,
                "WirelessEnergyHatchUV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV4096A"),
                8,
                4096));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV16384A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UV_16384A.ID,
                "WirelessEnergyHatchUV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV16384A"),
                8,
                16384));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV65536A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UV_65536A.ID,
                "WirelessEnergyHatchUV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV65536A"),
                8,
                65536));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV262144A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UV_262144A.ID,
                "WirelessEnergyHatchUV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV262144A"),
                8,
                262144));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV1048576A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UV_1048576A.ID,
                "WirelessEnergyHatchUV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV1048576A"),
                8,
                1048576));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV4194304A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UV_4194304A.ID,
                "WirelessEnergyHatchUV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV4194304A"),
                8,
                4194304));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUV16777216A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UV_16777216A.ID,
                "WirelessEnergyHatchUV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchUV16777216A"),
                8,
                16777216));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV.set(ItemList.Wireless_Hatch_Energy_UHV.get(1));

        GTNLItemList.WirelessEnergyHatchUHV4A.set(CustomItemList.eM_energyWirelessMulti4_UHV.get(1));

        GTNLItemList.WirelessEnergyHatchUHV16A.set(CustomItemList.eM_energyWirelessMulti16_UHV.get(1));

        GTNLItemList.WirelessEnergyHatchUHV64A.set(CustomItemList.eM_energyWirelessMulti64_UHV.get(1));

        GTNLItemList.WirelessEnergyHatchUHV256A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UHV_256A.ID,
                "WirelessEnergyHatchUHV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV256A"),
                9,
                256));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV1024A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UHV_1024A.ID,
                "WirelessEnergyHatchUHV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV1024A"),
                9,
                1024));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV4096A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UHV_4096A.ID,
                "WirelessEnergyHatchUHV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV4096A"),
                9,
                4096));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV16384A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UHV_16384A.ID,
                "WirelessEnergyHatchUHV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV16384A"),
                9,
                16384));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV65536A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UHV_65536A.ID,
                "WirelessEnergyHatchUHV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV65536A"),
                9,
                65536));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV262144A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UHV_262144A.ID,
                "WirelessEnergyHatchUHV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV262144A"),
                9,
                262144));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV1048576A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UHV_1048576A.ID,
                "WirelessEnergyHatchUHV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV1048576A"),
                9,
                1048576));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV4194304A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UHV_4194304A.ID,
                "WirelessEnergyHatchUHV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV4194304A"),
                9,
                4194304));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUHV16777216A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UHV_16777216A.ID,
                "WirelessEnergyHatchUHV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchUHV16777216A"),
                9,
                16777216));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUHV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV.set(ItemList.Wireless_Hatch_Energy_UEV.get(1));

        GTNLItemList.WirelessEnergyHatchUEV4A.set(CustomItemList.eM_energyWirelessMulti4_UEV.get(1));

        GTNLItemList.WirelessEnergyHatchUEV16A.set(CustomItemList.eM_energyWirelessMulti16_UEV.get(1));

        GTNLItemList.WirelessEnergyHatchUEV64A.set(CustomItemList.eM_energyWirelessMulti64_UEV.get(1));

        GTNLItemList.WirelessEnergyHatchUEV256A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UEV_256A.ID,
                "WirelessEnergyHatchUEV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV256A"),
                10,
                256));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV1024A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UEV_1024A.ID,
                "WirelessEnergyHatchUEV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV1024A"),
                10,
                1024));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV4096A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UEV_4096A.ID,
                "WirelessEnergyHatchUEV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV4096A"),
                10,
                4096));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV16384A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UEV_16384A.ID,
                "WirelessEnergyHatchUEV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV16384A"),
                10,
                16384));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV65536A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UEV_65536A.ID,
                "WirelessEnergyHatchUEV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV65536A"),
                10,
                65536));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV262144A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UEV_262144A.ID,
                "WirelessEnergyHatchUEV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV262144A"),
                10,
                262144));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV1048576A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UEV_1048576A.ID,
                "WirelessEnergyHatchUEV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV1048576A"),
                10,
                1048576));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV4194304A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UEV_4194304A.ID,
                "WirelessEnergyHatchUEV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV4194304A"),
                10,
                4194304));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUEV16777216A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UEV_16777216A.ID,
                "WirelessEnergyHatchUEV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchUEV16777216A"),
                10,
                16777216));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUEV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV.set(ItemList.Wireless_Hatch_Energy_UIV.get(1));

        GTNLItemList.WirelessEnergyHatchUIV4A.set(CustomItemList.eM_energyWirelessMulti4_UIV.get(1));

        GTNLItemList.WirelessEnergyHatchUIV16A.set(CustomItemList.eM_energyWirelessMulti16_UIV.get(1));

        GTNLItemList.WirelessEnergyHatchUIV64A.set(CustomItemList.eM_energyWirelessMulti64_UIV.get(1));

        GTNLItemList.WirelessEnergyHatchUIV256A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UIV_256A.ID,
                "WirelessEnergyHatchUIV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV256A"),
                11,
                256));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV1024A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UIV_1024A.ID,
                "WirelessEnergyHatchUIV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV1024A"),
                11,
                1024));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV4096A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UIV_4096A.ID,
                "WirelessEnergyHatchUIV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV4096A"),
                11,
                4096));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV16384A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UIV_16384A.ID,
                "WirelessEnergyHatchUIV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV16384A"),
                11,
                16384));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV65536A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UIV_65536A.ID,
                "WirelessEnergyHatchUIV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV65536A"),
                11,
                65536));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV262144A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UIV_262144A.ID,
                "WirelessEnergyHatchUIV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV262144A"),
                11,
                262144));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV1048576A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UIV_1048576A.ID,
                "WirelessEnergyHatchUIV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV1048576A"),
                11,
                1048576));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV4194304A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UIV_4194304A.ID,
                "WirelessEnergyHatchUIV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV4194304A"),
                11,
                4194304));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUIV16777216A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UIV_16777216A.ID,
                "WirelessEnergyHatchUIV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchUIV16777216A"),
                11,
                16777216));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUIV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV.set(ItemList.Wireless_Hatch_Energy_UMV.get(1));

        GTNLItemList.WirelessEnergyHatchUMV4A.set(CustomItemList.eM_energyWirelessMulti4_UMV.get(1));

        GTNLItemList.WirelessEnergyHatchUMV16A.set(CustomItemList.eM_energyWirelessMulti16_UMV.get(1));

        GTNLItemList.WirelessEnergyHatchUMV64A.set(CustomItemList.eM_energyWirelessMulti64_UMV.get(1));

        GTNLItemList.WirelessEnergyHatchUMV256A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UMV_256A.ID,
                "WirelessEnergyHatchUMV256A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV256A"),
                12,
                256));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV1024A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UMV_1024A.ID,
                "WirelessEnergyHatchUMV1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV1024A"),
                12,
                1024));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV4096A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UMV_4096A.ID,
                "WirelessEnergyHatchUMV4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV4096A"),
                12,
                4096));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV16384A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UMV_16384A.ID,
                "WirelessEnergyHatchUMV16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV16384A"),
                12,
                16384));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV65536A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UMV_65536A.ID,
                "WirelessEnergyHatchUMV65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV65536A"),
                12,
                65536));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV262144A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UMV_262144A.ID,
                "WirelessEnergyHatchUMV262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV262144A"),
                12,
                262144));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV1048576A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UMV_1048576A.ID,
                "WirelessEnergyHatchUMV1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV1048576A"),
                12,
                1048576));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV4194304A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UMV_4194304A.ID,
                "WirelessEnergyHatchUMV4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV4194304A"),
                12,
                4194304));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUMV16777216A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_UMV_16777216A.ID,
                "WirelessEnergyHatchUMV16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchUMV16777216A"),
                12,
                16777216));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchUMV16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchUXV.set(ItemList.Wireless_Hatch_Energy_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV4A.set(CustomItemList.eM_energyWirelessMulti4_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV16A.set(CustomItemList.eM_energyWirelessMulti16_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV64A.set(CustomItemList.eM_energyWirelessMulti64_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV256A.set(CustomItemList.eM_energyWirelessTunnel1_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV1024A.set(CustomItemList.eM_energyWirelessTunnel2_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV4096A.set(CustomItemList.eM_energyWirelessTunnel3_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV16384A.set(CustomItemList.eM_energyWirelessTunnel4_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV65536A.set(CustomItemList.eM_energyWirelessTunnel5_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV262144A.set(CustomItemList.eM_energyWirelessTunnel6_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV1048576A.set(CustomItemList.eM_energyWirelessTunnel7_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV4194304A.set(CustomItemList.eM_energyWirelessTunnel8_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchUXV16777216A.set(CustomItemList.eM_energyWirelessTunnel9_UXV.get(1));

        GTNLItemList.WirelessEnergyHatchMAX.set(ItemList.Wireless_Hatch_Energy_MAX.get(1));

        GTNLItemList.WirelessEnergyHatchMAX4A.set(CustomItemList.eM_energyWirelessMulti4_MAX.get(1));

        GTNLItemList.WirelessEnergyHatchMAX16A.set(CustomItemList.eM_energyWirelessMulti16_MAX.get(1));

        GTNLItemList.WirelessEnergyHatchMAX64A.set(CustomItemList.eM_energyWirelessMulti64_MAX.get(1));

        GTNLItemList.WirelessEnergyHatchMAX256A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MAX_256A.ID,
                "WirelessEnergyHatchMAX256A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX256A"),
                14,
                256));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX256A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX1024A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MAX_1024A.ID,
                "WirelessEnergyHatchMAX1024A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX1024A"),
                14,
                1024));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX1024A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX4096A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MAX_4096A.ID,
                "WirelessEnergyHatchMAX4096A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX4096A"),
                14,
                4096));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX4096A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX16384A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MAX_16384A.ID,
                "WirelessEnergyHatchMAX16384A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX16384A"),
                14,
                16384));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX16384A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX65536A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MAX_65536A.ID,
                "WirelessEnergyHatchMAX65536A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX65536A"),
                14,
                65536));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX65536A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX262144A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MAX_262144A.ID,
                "WirelessEnergyHatchMAX262144A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX262144A"),
                14,
                262144));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX262144A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX1048576A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MAX_1048576A.ID,
                "WirelessEnergyHatchMAX1048576A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX1048576A"),
                14,
                1048576));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX1048576A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX4194304A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MAX_4194304A.ID,
                "WirelessEnergyHatchMAX4194304A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX4194304A"),
                14,
                4194304));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX4194304A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.WirelessEnergyHatchMAX16777216A.set(
            new MTEHatchWirelessMulti(
                WIRELESS_ENERGY_HATCH_MAX_16777216A.ID,
                "WirelessEnergyHatchMAX16777216A",
                StatCollector.translateToLocal("WirelessEnergyHatchMAX16777216A"),
                14,
                16777216));
        addItemTooltip(GTNLItemList.WirelessEnergyHatchMAX16777216A.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LegendaryWirelessEnergyHatch.set(
            new MTEHatchWirelessMulti(
                LEGENDARY_WIRELESS_ENERGY_HATCH.ID,
                "LegendaryWirelessEnergyHatch",
                StatCollector.translateToLocal("LegendaryWirelessEnergyHatch"),
                14,
                Integer.MAX_VALUE));
        addItemTooltip(GTNLItemList.LegendaryWirelessEnergyHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LegendaryWirelessDynamoHatch.set(
            new WirelessMultiDynamoHatch(
                LEGENDARY_WIRELESS_DYNAMO_HATCH.ID,
                "LegendaryWirelessDynamoHatch",
                StatCollector.translateToLocal("LegendaryWirelessDynamoHatch"),
                14,
                Integer.MAX_VALUE));
        addItemTooltip(GTNLItemList.LegendaryWirelessDynamoHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);
    }

    public static void registerBasicMachine() {
        GTNLItemList.SteamTurbineLV.set(
            new SteamTurbine(
                STEAM_TURBINE_LV.ID,
                "SteamTurbineLV",
                StatCollector.translateToLocal("SteamTurbineLV"),
                1));
        addItemTooltip(GTNLItemList.SteamTurbineLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamTurbineMV.set(
            new SteamTurbine(
                STEAM_TURBINE_MV.ID,
                "SteamTurbineMV",
                StatCollector.translateToLocal("SteamTurbineMV"),
                2));
        addItemTooltip(GTNLItemList.SteamTurbineMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamTurbineHV.set(
            new SteamTurbine(
                STEAM_TURBINE_HV.ID,
                "SteamTurbineHV",
                StatCollector.translateToLocal("SteamTurbineHV"),
                3));
        addItemTooltip(GTNLItemList.SteamTurbineHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamAssemblerBronze.set(
            new SteamAssemblerBronze(
                STEAM_ASSEMBLER_BRONZE.ID,
                "SteamAssembler",
                StatCollector.translateToLocal("SteamAssemblerBronze")));
        addItemTooltip(GTNLItemList.SteamAssemblerBronze.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamAssemblerSteel.set(
            new SteamAssemblerSteel(
                STEAM_ASSEMBLER_STEEL.ID,
                "HighPressureSteamAssembler",
                StatCollector.translateToLocal("SteamAssemblerSteel")));
        addItemTooltip(GTNLItemList.SteamAssemblerSteel.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ManaTank.set(new ManaTank(MANA_TANK.ID, "ManaTank", StatCollector.translateToLocal("ManaTank")));
        addItemTooltip(GTNLItemList.ManaTank.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        for (GasCollectorTier tier : GasCollectorTier.values()) {
            IMetaTileEntity mte = new MTEBasicMachineWithRecipe(
                tier.id.ID,
                tier.unlocalizedName,
                StatCollector.translateToLocal(tier.unlocalizedName),
                tier.tier,
                new String[] { StatCollector.translateToLocal(tier.tooltipKey),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipePool.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR");

            tier.itemEnum.set(mte);

            addItemTooltip(tier.itemEnum.get(1), AnimatedText.SCIENCE_NOT_LEISURE);
        }

        GTNLItemList.GasTurbineLV
            .set(new GasTurbine(GAS_TURBINE_LV.ID, "GasTurbineLV", StatCollector.translateToLocal("GasTurbineLV"), 1));
        addItemTooltip(GTNLItemList.GasTurbineLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasTurbineMV
            .set(new GasTurbine(GAS_TURBINE_MV.ID, "GasTurbineMV", StatCollector.translateToLocal("GasTurbineMV"), 2));
        addItemTooltip(GTNLItemList.GasTurbineMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasTurbineHV
            .set(new GasTurbine(GAS_TURBINE_HV.ID, "GasTurbineHV", StatCollector.translateToLocal("GasTurbineHV"), 3));
        addItemTooltip(GTNLItemList.GasTurbineHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DieselGeneratorLV.set(
            new DieselGenerator(
                DIESEL_GENERATOR_LV.ID,
                "DieselGeneratorLV",
                StatCollector.translateToLocal("DieselGeneratorLV"),
                1));
        addItemTooltip(GTNLItemList.DieselGeneratorLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DieselGeneratorMV.set(
            new DieselGenerator(
                DIESEL_GENERATOR_MV.ID,
                "DieselGeneratorMV",
                StatCollector.translateToLocal("DieselGeneratorMV"),
                2));
        addItemTooltip(GTNLItemList.DieselGeneratorMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.DieselGeneratorHV.set(
            new DieselGenerator(
                DIESEL_GENERATOR_HV.ID,
                "DieselGeneratorHV",
                StatCollector.translateToLocal("DieselGeneratorHV"),
                3));
        addItemTooltip(GTNLItemList.DieselGeneratorHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.EnergyTransferNode.set(
            new EnergyTransferNode(
                ENERGY_TRANSFER_NODE.ID,
                "EnergyTransferNode",
                StatCollector.translateToLocal("EnergyTransferNode")));
        addItemTooltip(GTNLItemList.EnergyTransferNode.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.LootBagRedemption.set(
            new LootBagRedemption(
                LOOT_BAG_REDEMPTION.ID,
                "LootBagRedemption",
                StatCollector.translateToLocal("LootBagRedemption"),
                1));
        addItemTooltip(GTNLItemList.LootBagRedemption.get(1), AnimatedText.SCIENCE_NOT_LEISURE);
    }

    public static void registerWireAndPipe() {
        MaterialUtils.registerWires(STAR_GATE_WIRE.ID, MaterialPool.Stargate, 2147483647, 2147483647, 0, false);
        MaterialUtils.registerItemPipe(BLUE_ALLOY_PIPE.ID, Materials.BlueAlloy, 8, 2048, false, 20);
        MaterialUtils.registerFluidPipes(COMPRESSED_STEAM_PIPE.ID, MaterialPool.CompressedSteam, 250000, 10000, true);
        MaterialUtils.registerFluidPipes(STRONZE_PIPE.ID, MaterialPool.Stronze, 15000, 10000, true);
        MaterialUtils.registerFluidPipes(BREEL_PIPE.ID, MaterialPool.Breel, 10000, 10000, true);
    }

    public static void registerCovers() {
        CoverRegistry.registerCover(
            GTNLItemList.HydraulicPump.get(1L),
            TextureFactory.of(MACHINE_CASINGS[1][0], TextureFactory.of(OVERLAY_PUMP)),
            context -> new CoverPump(context, 1048576, TextureFactory.of(OVERLAY_PUMP)));

        CoverRegistry.registerCover(
            GTNLItemList.HydraulicConveyor.get(1L),
            TextureFactory.of(MACHINE_CASINGS[1][0], TextureFactory.of(OVERLAY_CONVEYOR)),
            context -> new CoverConveyor(context, 1, 16, TextureFactory.of(OVERLAY_CONVEYOR)));

        CoverRegistry.registerCover(
            GTNLItemList.HydraulicRegulator.get(1L),
            TextureFactory.of(MACHINE_CASINGS[1][0], TextureFactory.of(OVERLAY_PUMP)),
            context -> new CoverSteamRegulator(context, 1048576, TextureFactory.of(OVERLAY_PUMP)));

        CoverRegistry.registerCover(
            GTNLItemList.HydraulicSteamValve.get(1L),
            TextureFactory.of(MACHINE_CASINGS[1][0], TextureFactory.of(OVERLAY_VALVE)),
            context -> new CoverSteamValve(context, 16777216, TextureFactory.of(OVERLAY_VALVE)));

        CoverRegistry.registerCover(
            GTNLItemList.HydraulicSteamRegulator.get(1L),
            TextureFactory.of(MACHINE_CASINGS[1][0], TextureFactory.of(OVERLAY_VALVE)),
            context -> new CoverSteamRegulator(context, 16777216, TextureFactory.of(OVERLAY_VALVE)));

        CoverRegistry.registerCover(
            GTNLItemList.PipelessSteamCover.get(1L),
            TextureFactory.of(MACHINE_CASINGS[1][0], TextureFactory.of(OVERLAYS_ENERGY_ON_WIRELESS[0])),
            WirelessSteamCover::new,
            CoverRegistry.INTERCEPTS_RIGHT_CLICK_COVER_PLACER);

        CoverRegistry.registerCover(
            GTNLItemList.VoidCover.get(1L),
            TextureFactory.of(MACHINE_CASINGS[1][0], TextureFactory.of(ITEM_VOID_SIGN)),
            VoidCover::new,
            CoverRegistry.INTERCEPTS_RIGHT_CLICK_COVER_PLACER);

        for (int i = 0; i < 14; i++) {
            int tier = i + 1;
            CoverRegistry.registerCover(
                GTNLItemList.WIRELESS_ENERGY_COVER_4A[i].get(1),
                TextureFactory.of(MACHINE_CASINGS[1][0], TextureFactory.of(OVERLAYS_ENERGY_ON_WIRELESS_4A[0])),
                context -> new WirelessMultiEnergyCover(context, (int) GTValues.V[tier], 4),
                CoverRegistry.INTERCEPTS_RIGHT_CLICK_COVER_PLACER);
        }

        // CoverRegistry.registerCover(
        // GTNLItemList.SteelTurbine.get(1L),
        // TextureFactory.of(TexturesGtBlock.Overlay_Water),
        // ctx -> new FluidCover(ctx, Materials.Water.mFluid, "InfinityWaterCover"),
        // CoverRegistry.INTERCEPTS_RIGHT_CLICK_COVER_PLACER);
    }

    public static void registerGlasses() {
        GlassTier.addCustomGlass(ItemRegistry.bw_realglas2, 1, 13, 1);
        if (MainConfig.enableRegisterPlayerDollGlass) {
            GlassTier.addCustomGlass(BlockLoader.playerDoll, 0, 14, 1);
        }
        GlassTier.addCustomGlass(ItemRegistry.bw_realglas2, 2, 14, 2);
        GlassTier.addCustomGlass(BlockLoader.metaBlockGlass, 0, 10, 2);
        GlassTier.addCustomGlass(BlockLoader.metaBlockGlass, 1, 8, 2);
        GlassTier.addCustomGlass(BlockLoader.metaBlockGlass, 2, 7, 1);

        for (int lampMeta = 1; lampMeta <= 32; lampMeta++) {
            GlassTier.addCustomGlass(BlockLoader.metaBlockGlow, lampMeta, 3, 1);
        }

        for (int lampOffMeta = 3; lampOffMeta <= 34; lampOffMeta++) {
            GlassTier.addCustomGlass(BlockLoader.metaBlock, lampOffMeta, 3, 1);
        }
    }

    public static void registry() {
        Logger.INFO("GTNL Content | Registering MTE Block Machine.");
        registerHatch();
        registerMachines();
        registerWireAndPipe();
        registerBasicMachine();
        registerCovers();

        if (MainConfig.enableIntegratedOreFactoryChange) {
            addItemTooltip(ItemList.Ore_Processor.get(1), AnimatedText.SCIENCE_NOT_LEISURE_CHANGE);
        }

        if (MainConfig.enableVoidMinerTweak) {
            addItemTooltip(ItemRegistry.voidminer[0], AnimatedText.SCIENCE_NOT_LEISURE_CHANGE);
            addItemTooltip(ItemRegistry.voidminer[1], AnimatedText.SCIENCE_NOT_LEISURE_CHANGE);
            addItemTooltip(ItemRegistry.voidminer[2], AnimatedText.SCIENCE_NOT_LEISURE_CHANGE);
        }

        if (MainConfig.enablePurificationPlantBuff) {
            addItemTooltip(
                ItemList.Machine_Multi_PurificationPlant.get(1),
                () -> StatCollector.translateToLocal("Tooltip_PurificationPlant_00"));
            addItemTooltip(
                ItemList.Machine_Multi_PurificationPlant.get(1),
                () -> StatCollector.translateToLocal("Tooltip_PurificationPlant_01"));
            addItemTooltip(
                ItemList.Machine_Multi_PurificationPlant.get(1),
                () -> StatCollector.translateToLocal("Tooltip_PurificationPlant_02"));
            addItemTooltip(ItemList.Machine_Multi_PurificationPlant.get(1), AnimatedText.SCIENCE_NOT_LEISURE_CHANGE);
        }

        addItemTooltip(
            CustomItemList.Machine_Multi_Research.get(1),
            () -> StatCollector.translateToLocal("Tooltip_ResearchStation_00"));
        addItemTooltip(CustomItemList.Machine_Multi_Research.get(1), AnimatedText.SCIENCE_NOT_LEISURE_CHANGE);
    }

    public enum GasCollectorTier {

        LV(GTNLItemList.GasCollectorLV, GAS_COLLECTOR_LV, "GasCollectorLV", 1, "Tooltip_GasCollector_00"),
        MV(GTNLItemList.GasCollectorMV, GAS_COLLECTOR_MV, "GasCollectorMV", 2, "Tooltip_GasCollector_00"),
        HV(GTNLItemList.GasCollectorHV, GAS_COLLECTOR_HV, "GasCollectorHV", 3, "Tooltip_GasCollector_00"),
        EV(GTNLItemList.GasCollectorEV, GAS_COLLECTOR_EV, "GasCollectorEV", 4, "Tooltip_GasCollector_00"),
        IV(GTNLItemList.GasCollectorIV, GAS_COLLECTOR_IV, "GasCollectorIV", 5, "Tooltip_GasCollector_01"),
        LuV(GTNLItemList.GasCollectorLuV, GAS_COLLECTOR_LUV, "GasCollectorLuV", 6, "Tooltip_GasCollector_01"),
        ZPM(GTNLItemList.GasCollectorZPM, GAS_COLLECTOR_ZPM, "GasCollectorZPM", 7, "Tooltip_GasCollector_01"),
        UV(GTNLItemList.GasCollectorUV, GAS_COLLECTOR_UV, "GasCollectorUV", 8, "Tooltip_GasCollector_02"),
        UHV(GTNLItemList.GasCollectorUHV, GAS_COLLECTOR_UHV, "GasCollectorUHV", 9, "Tooltip_GasCollector_02"),
        UEV(GTNLItemList.GasCollectorUEV, GAS_COLLECTOR_UEV, "GasCollectorUEV", 10, "Tooltip_GasCollector_02"),
        UIV(GTNLItemList.GasCollectorUIV, GAS_COLLECTOR_UIV, "GasCollectorUIV", 11, "Tooltip_GasCollector_02"),
        UMV(GTNLItemList.GasCollectorUMV, GAS_COLLECTOR_UMV, "GasCollectorUMV", 12, "Tooltip_GasCollector_02"),
        UXV(GTNLItemList.GasCollectorUXV, GAS_COLLECTOR_UXV, "GasCollectorUXV", 13, "Tooltip_GasCollector_03");

        public final GTNLItemList itemEnum;
        public final GTNLMachineID id;
        public final String unlocalizedName;
        public final int tier;
        public final String tooltipKey;

        GasCollectorTier(GTNLItemList itemEnum, GTNLMachineID id, String name, int tier, String tooltipKey) {
            this.itemEnum = itemEnum;
            this.id = id;
            this.unlocalizedName = name;
            this.tier = tier;
            this.tooltipKey = tooltipKey;
        }
    }
}
