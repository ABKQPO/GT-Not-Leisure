package com.science.gtnl.loader;

import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.*;
import static com.science.gtnl.Utils.enums.GTNLMachineID.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_VALVE;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.google.common.collect.ImmutableSet;
import com.science.gtnl.Utils.enums.GTNLItemList;
import com.science.gtnl.Utils.enums.Mods;
import com.science.gtnl.Utils.item.ItemUtils;
import com.science.gtnl.Utils.text.AnimatedText;
import com.science.gtnl.common.machine.basicMachine.DebugResearchStation;
import com.science.gtnl.common.machine.basicMachine.ManaTank;
import com.science.gtnl.common.machine.basicMachine.SteamAssemblerBronze;
import com.science.gtnl.common.machine.basicMachine.SteamAssemblerSteel;
import com.science.gtnl.common.machine.basicMachine.SteamTurbine;
import com.science.gtnl.common.machine.hatch.CustomFluidHatch;
import com.science.gtnl.common.machine.hatch.CustomMaintenanceHatch;
import com.science.gtnl.common.machine.hatch.DebugEnergyHatch;
import com.science.gtnl.common.machine.hatch.DualInputHatch;
import com.science.gtnl.common.machine.hatch.DualOutputHatch;
import com.science.gtnl.common.machine.hatch.ExplosionDynamoHatch;
import com.science.gtnl.common.machine.hatch.HumongousInputBus;
import com.science.gtnl.common.machine.hatch.HumongousNinefoldInputHatch;
import com.science.gtnl.common.machine.hatch.HumongousSolidifierHatch;
import com.science.gtnl.common.machine.hatch.ManaDynamoHatch;
import com.science.gtnl.common.machine.hatch.ManaEnergyHatch;
import com.science.gtnl.common.machine.hatch.NinefoldInputHatch;
import com.science.gtnl.common.machine.hatch.ParallelControllerHatch;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputHatchME;
import com.science.gtnl.common.machine.hatch.SuperCraftingInputProxy;
import com.science.gtnl.common.machine.hatch.SuperDataAccessHatch;
import com.science.gtnl.common.machine.hatch.SuperInputBusME;
import com.science.gtnl.common.machine.hatch.SuperInputHatchME;
import com.science.gtnl.common.machine.hatch.TapDynamoHatch;
import com.science.gtnl.common.machine.hatch.WirelessSteamDynamoHatch;
import com.science.gtnl.common.machine.hatch.WirelessSteamEnergyHatch;
import com.science.gtnl.common.machine.multiblock.AdvancedCircuitAssemblyLine;
import com.science.gtnl.common.machine.multiblock.AdvancedInfiniteDriller;
import com.science.gtnl.common.machine.multiblock.AdvancedPhotovoltaicPowerStation;
import com.science.gtnl.common.machine.multiblock.AetronPressor;
import com.science.gtnl.common.machine.multiblock.AprilFool.HighPressureSteamFusionReactor;
import com.science.gtnl.common.machine.multiblock.AprilFool.MegaSolarBoiler;
import com.science.gtnl.common.machine.multiblock.AprilFool.MegaSteamCompressor;
import com.science.gtnl.common.machine.multiblock.AprilFool.SteamCactusWonder;
import com.science.gtnl.common.machine.multiblock.AprilFool.SteamCarpenter;
import com.science.gtnl.common.machine.multiblock.AprilFool.SteamExtractinator;
import com.science.gtnl.common.machine.multiblock.AprilFool.SteamFusionReactor;
import com.science.gtnl.common.machine.multiblock.AprilFool.SteamGateAssembler;
import com.science.gtnl.common.machine.multiblock.AprilFool.SteamInfernalCokeOven;
import com.science.gtnl.common.machine.multiblock.AprilFool.SteamLavaMaker;
import com.science.gtnl.common.machine.multiblock.AprilFool.SteamManufacturer;
import com.science.gtnl.common.machine.multiblock.AprilFool.SteamRockBreaker;
import com.science.gtnl.common.machine.multiblock.AprilFool.SteamWoodcutter;
import com.science.gtnl.common.machine.multiblock.AprilFool.Steamgate;
import com.science.gtnl.common.machine.multiblock.BioengineeringModule;
import com.science.gtnl.common.machine.multiblock.BloodSoulSacrificialArray;
import com.science.gtnl.common.machine.multiblock.BrickedBlastFurnace;
import com.science.gtnl.common.machine.multiblock.CheatOreProcessingFactory;
import com.science.gtnl.common.machine.multiblock.ComponentAssembler;
import com.science.gtnl.common.machine.multiblock.CrackerHub;
import com.science.gtnl.common.machine.multiblock.DecayHastener;
import com.science.gtnl.common.machine.multiblock.Desulfurizer;
import com.science.gtnl.common.machine.multiblock.DraconicFusionCrafting;
import com.science.gtnl.common.machine.multiblock.EdenGarden;
import com.science.gtnl.common.machine.multiblock.ElementCopying;
import com.science.gtnl.common.machine.multiblock.EnergeticPhotovoltaicPowerStation;
import com.science.gtnl.common.machine.multiblock.EngravingLaserPlant;
import com.science.gtnl.common.machine.multiblock.FieldForgePress;
import com.science.gtnl.common.machine.multiblock.FuelRefiningComplex;
import com.science.gtnl.common.machine.multiblock.GenerationEarthEngine;
import com.science.gtnl.common.machine.multiblock.GrandAssemblyLine;
import com.science.gtnl.common.machine.multiblock.HandOfJohnDavisonRockefeller;
import com.science.gtnl.common.machine.multiblock.HeavyRolling;
import com.science.gtnl.common.machine.multiblock.HighEnergyLaserLathe;
import com.science.gtnl.common.machine.multiblock.IndustrialArcaneAssembler;
import com.science.gtnl.common.machine.multiblock.IntegratedAssemblyFacility;
import com.science.gtnl.common.machine.multiblock.LapotronChip;
import com.science.gtnl.common.machine.multiblock.LargeBioLab;
import com.science.gtnl.common.machine.multiblock.LargeBrewer;
import com.science.gtnl.common.machine.multiblock.LargeCircuitAssembler;
import com.science.gtnl.common.machine.multiblock.LargeGasCollector;
import com.science.gtnl.common.machine.multiblock.LargeIncubator;
import com.science.gtnl.common.machine.multiblock.LargeNaquadahReactor;
import com.science.gtnl.common.machine.multiblock.LargeSteamAlloySmelter;
import com.science.gtnl.common.machine.multiblock.LargeSteamChemicalBath;
import com.science.gtnl.common.machine.multiblock.LargeSteamCircuitAssembler;
import com.science.gtnl.common.machine.multiblock.LargeSteamCrusher;
import com.science.gtnl.common.machine.multiblock.LargeSteamExtractor;
import com.science.gtnl.common.machine.multiblock.LargeSteamExtruder;
import com.science.gtnl.common.machine.multiblock.LargeSteamFormingPress;
import com.science.gtnl.common.machine.multiblock.LargeSteamFurnace;
import com.science.gtnl.common.machine.multiblock.LargeSteamSifter;
import com.science.gtnl.common.machine.multiblock.LargeSteamThermalCentrifuge;
import com.science.gtnl.common.machine.multiblock.LibraryOfRuina;
import com.science.gtnl.common.machine.multiblock.MagneticEnergyReactionFurnace;
import com.science.gtnl.common.machine.multiblock.MatterFabricator;
import com.science.gtnl.common.machine.multiblock.MegaMixer;
import com.science.gtnl.common.machine.multiblock.MeteorMiner;
import com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.EGTWFusionModule;
import com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.ETGWEyeOfHarmonyModule;
import com.science.gtnl.common.machine.multiblock.ModuleMachine.EGTW.EternalGregTechWorkshop;
import com.science.gtnl.common.machine.multiblock.NanitesCircuitAssemblyFactory;
import com.science.gtnl.common.machine.multiblock.NanitesIntegratedProcessingCenter;
import com.science.gtnl.common.machine.multiblock.NanoAssemblerMarkL;
import com.science.gtnl.common.machine.multiblock.NanoPhagocytosisPlant;
import com.science.gtnl.common.machine.multiblock.NeutroniumWireCutting;
import com.science.gtnl.common.machine.multiblock.NineIndustrialMultiMachine;
import com.science.gtnl.common.machine.multiblock.OreExtractionModule;
import com.science.gtnl.common.machine.multiblock.PetrochemicalPlant;
import com.science.gtnl.common.machine.multiblock.PlatinumBasedTreatment;
import com.science.gtnl.common.machine.multiblock.PolymerTwistingModule;
import com.science.gtnl.common.machine.multiblock.PrimitiveDistillationTower;
import com.science.gtnl.common.machine.multiblock.ProcessingArray;
import com.science.gtnl.common.machine.multiblock.RareEarthCentrifugal;
import com.science.gtnl.common.machine.multiblock.ReactionFurnace;
import com.science.gtnl.common.machine.multiblock.RealArtificialStar;
import com.science.gtnl.common.machine.multiblock.ResourceCollectionModule;
import com.science.gtnl.common.machine.multiblock.ShallowChemicalCoupling;
import com.science.gtnl.common.machine.multiblock.SmeltingMixingFurnace;
import com.science.gtnl.common.machine.multiblock.SpaceAssembler;
import com.science.gtnl.common.machine.multiblock.SteamCracking;
import com.science.gtnl.common.machine.multiblock.SteamElevator.SteamBeaconModule;
import com.science.gtnl.common.machine.multiblock.SteamElevator.SteamElevator;
import com.science.gtnl.common.machine.multiblock.SteamElevator.SteamFlightModule;
import com.science.gtnl.common.machine.multiblock.SteamElevator.SteamMonsterRepellentModule;
import com.science.gtnl.common.machine.multiblock.SteamElevator.SteamWeatherModule;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.AlloyBlastSmelter;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.BlazeBlastFurnace;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.ChemicalPlant;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.ColdIceFreezer;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.Digester;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.ElectricBlastFurnace;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.ElectricImplosionCompressor;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.EnergyInfuser;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.FishingGround;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.FlotationCellRegulator;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.Incubator;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.IsaMill;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeAlloySmelter;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeArcSmelter;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeAssembler;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeAutoclave;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeBender;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeBoiler;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeCanning;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeCentrifuge;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeChemicalBath;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeCutter;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeDistillery;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeElectrolyzer;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeElectromagnet;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeEngravingLaser;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeExtractor;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeExtruder;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeForming;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeHammer;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeIndustrialLathe;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeMacerationTower;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeMaterialPress;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeMixer;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargePacker;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargePyrolyseOven;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeSiftingFunnel;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeSolidifier;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeSteamCentrifuge;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeSteamCompressor;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeSteamHammer;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeSteamMixer;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeSteamOreWasher;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LargeWiremill;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.LuvKuangBiaoOneGiantNuclearFusionReactor;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.MegaAlloyBlastSmelter;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.MegaBlastFurnace;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.MolecularTransformer;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.PrecisionAssembler;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.UevKuangBiaoFiveGiantNuclearFusionReactor;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.UhvKuangBiaoFourGiantNuclearFusionReactor;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.UvKuangBiaoThreeGiantNuclearFusionReactor;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.VacuumDryingFurnace;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.VacuumFreezer;
import com.science.gtnl.common.machine.multiblock.StructuralReconstructionPlan.ZpmKuangBiaoTwoGiantNuclearFusionReactor;
import com.science.gtnl.common.machine.multiblock.SuperSpaceElevator;
import com.science.gtnl.common.machine.multiblock.SuperconductingElectromagnetism;
import com.science.gtnl.common.machine.multiblock.SuperconductingMagneticPresser;
import com.science.gtnl.common.machine.multiblock.TeleportationArrayToAlfheim;
import com.science.gtnl.common.machine.multiblock.VibrantPhotovoltaicPowerStation;
import com.science.gtnl.common.machine.multiblock.VortexMatterCentrifuge;
import com.science.gtnl.common.machine.multiblock.WhiteNightGenerator;
import com.science.gtnl.common.machine.multiblock.WoodDistillation;
import com.science.gtnl.common.materials.MaterialPool;
import com.science.gtnl.common.materials.MaterialUtils;

import bartworks.API.BorosilicateGlass;
import goodgenerator.util.CrackRecipeAdder;
import gregtech.api.covers.CoverRegistry;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.render.TextureFactory;
import gregtech.common.covers.CoverConveyor;
import gregtech.common.covers.CoverFluidRegulator;
import gregtech.common.covers.CoverPump;
import gregtech.common.covers.CoverSteamRegulator;
import gregtech.common.covers.CoverSteamValve;
import gtPlusPlus.api.objects.Logger;
import gtPlusPlus.core.util.minecraft.FluidUtils;

public class MachineLoader {

    public static ItemStack ResourceCollectionModule;
    public static ItemStack SuperSpaceElevator;

    public static void loadMachines() {

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
            new EnergeticPhotovoltaicPowerStation(
                ENERGETIC_PHOTOVOLTAIC_POWER_STATION.ID,
                "EnergeticPhotovoltaicPowerStation",
                StatCollector.translateToLocal("NameEnergeticPhotovoltaicPowerStation")));
        addItemTooltip(GTNLItemList.EnergeticPhotovoltaicPowerStation.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.AdvancedPhotovoltaicPowerStation.set(
            new AdvancedPhotovoltaicPowerStation(
                ADVANCED_PHOTOVOLTAIC_POWER_STATION.ID,
                "AdvancedPhotovoltaicPowerStation",
                StatCollector.translateToLocal("NameAdvancedPhotovoltaicPowerStation")));
        addItemTooltip(GTNLItemList.AdvancedPhotovoltaicPowerStation.get(1), AnimatedText.SNL_QYZG);

        GTNLItemList.VibrantPhotovoltaicPowerStation.set(
            new VibrantPhotovoltaicPowerStation(
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
            new LuvKuangBiaoOneGiantNuclearFusionReactor(
                LUV_KUANG_BIAO_ONE_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoOneGiantNuclearFusionReactor",
                StatCollector.translateToLocal("NameLuvKuangBiaoOneGiantNuclearFusionReactor")));
        addItemTooltip(GTNLItemList.LuvKuangBiaoOneGiantNuclearFusionReactor.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.ZpmKuangBiaoTwoGiantNuclearFusionReactor.set(
            new ZpmKuangBiaoTwoGiantNuclearFusionReactor(
                ZPM_KUANG_BIAO_TWO_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoTwoGiantNuclearFusionReactor",
                StatCollector.translateToLocal("NameZpmKuangBiaoTwoGiantNuclearFusionReactor")));
        addItemTooltip(GTNLItemList.ZpmKuangBiaoTwoGiantNuclearFusionReactor.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.UvKuangBiaoThreeGiantNuclearFusionReactor.set(
            new UvKuangBiaoThreeGiantNuclearFusionReactor(
                UV_KUANG_BIAO_THREE_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoThreeGiantNuclearFusionReactor",
                StatCollector.translateToLocal("NameUvKuangBiaoThreeGiantNuclearFusionReactor")));
        addItemTooltip(GTNLItemList.UvKuangBiaoThreeGiantNuclearFusionReactor.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.UhvKuangBiaoFourGiantNuclearFusionReactor.set(
            new UhvKuangBiaoFourGiantNuclearFusionReactor(
                UHV_KUANG_BIAO_FOUR_GIANT_NUCLEAR_FUSION_REACTOR.ID,
                "KuangBiaoFourGiantNuclearFusionReactor",
                StatCollector.translateToLocal("NameUhvKuangBiaoFourGiantNuclearFusionReactor")));
        addItemTooltip(GTNLItemList.UhvKuangBiaoFourGiantNuclearFusionReactor.get(1), AnimatedText.SNL_QYZG_SRP);

        GTNLItemList.UevKuangBiaoFiveGiantNuclearFusionReactor.set(
            new UevKuangBiaoFiveGiantNuclearFusionReactor(
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

        GTNLItemList.AdvancedCircuitAssemblyLine.set(
            new AdvancedCircuitAssemblyLine(
                ADVANCED_CIRCUIT_ASSEMBLY_LINE.ID,
                "AdvancedCircuitAssemblyLine",
                StatCollector.translateToLocal("NameAdvancedCircuitAssemblyLine")));
        addItemTooltip(GTNLItemList.AdvancedCircuitAssemblyLine.get(1), AnimatedText.SNL_SCCR);

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

    public static void registerMTEHatch() {
        Set<Fluid> acceptedFluids = new HashSet<>();
        acceptedFluids.add(
            MaterialPool.FluidMana.getFluidOrGas(1)
                .getFluid());

        if (Mods.TwistSpaceTechnology.isModLoaded()) {
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
                StatCollector.translateToLocal("DebugEnergyHatch"),
                14));
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
                    Materials.Steam.getGas(1)
                        .getFluid(),
                    FluidUtils.getSuperHeatedSteam(1)
                        .getFluid(),
                    FluidRegistry.getFluid("supercriticalsteam"),
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
                5));
        addItemTooltip(GTNLItemList.AutoConfigurationMaintenanceHatch.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.ExplosionDynamoHatch.set(
            new ExplosionDynamoHatch(
                EXPLOSION_DYNAMO_HATCH.ID,
                "ExplosionDynamoHatch",
                StatCollector.translateToLocal("ExplosionDynamoHatch"),
                1));
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
    }

    @Deprecated
    public static void registerTestMachine() {
        GTNLItemList.QuadrupleOutputHatchEV.set(
            new DualOutputHatch(
                21700,
                4,
                "QuadrupleOutputHatchEV",
                StatCollector.translateToLocal("QuadrupleOutputHatchEV"),
                4));
        addItemTooltip(GTNLItemList.QuadrupleOutputHatchEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.NinefoldOutputHatchEV.set(
            new DualOutputHatch(
                21701,
                9,
                "NinefoldOutputHatchEV",
                StatCollector.translateToLocal("NinefoldOutputHatchEV"),
                4));
        addItemTooltip(GTNLItemList.NinefoldOutputHatchEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusULV.set(
            new HumongousInputBus(
                21702,
                "HumongousInputBusULV",
                StatCollector.translateToLocal("HumongousInputBusULV"),
                0));
        addItemTooltip(GTNLItemList.HumongousInputBusULV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusLV.set(
            new HumongousInputBus(
                21703,
                "HumongousInputBusLV",
                StatCollector.translateToLocal("HumongousInputBusLV"),
                1));
        addItemTooltip(GTNLItemList.HumongousInputBusLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusMV.set(
            new HumongousInputBus(
                21704,
                "HumongousInputBusMV",
                StatCollector.translateToLocal("HumongousInputBusMV"),
                2));
        addItemTooltip(GTNLItemList.HumongousInputBusMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusHV.set(
            new HumongousInputBus(
                21705,
                "HumongousInputBusHV",
                StatCollector.translateToLocal("HumongousInputBusHV"),
                3));
        addItemTooltip(GTNLItemList.HumongousInputBusHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusEV.set(
            new HumongousInputBus(
                21706,
                "HumongousInputBusEV",
                StatCollector.translateToLocal("HumongousInputBusEV"),
                4));
        addItemTooltip(GTNLItemList.HumongousInputBusEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusIV.set(
            new HumongousInputBus(
                21707,
                "HumongousInputBusIV",
                StatCollector.translateToLocal("HumongousInputBusIV"),
                5));
        addItemTooltip(GTNLItemList.HumongousInputBusIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusLuV.set(
            new HumongousInputBus(
                21708,
                "HumongousInputBusLuV",
                StatCollector.translateToLocal("HumongousInputBusLuV"),
                6));
        addItemTooltip(GTNLItemList.HumongousInputBusLuV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusZPM.set(
            new HumongousInputBus(
                21709,
                "HumongousInputBusZPM",
                StatCollector.translateToLocal("HumongousInputBusZPM"),
                7));
        addItemTooltip(GTNLItemList.HumongousInputBusZPM.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusUV.set(
            new HumongousInputBus(
                21710,
                "HumongousInputBusUV",
                StatCollector.translateToLocal("HumongousInputBusUV"),
                8));
        addItemTooltip(GTNLItemList.HumongousInputBusUV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusUHV.set(
            new HumongousInputBus(
                21711,
                "HumongousInputBusUHV",
                StatCollector.translateToLocal("HumongousInputBusUHV"),
                9));
        addItemTooltip(GTNLItemList.HumongousInputBusUHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusUEV.set(
            new HumongousInputBus(
                21712,
                "HumongousInputBusUEV",
                StatCollector.translateToLocal("HumongousInputBusUEV"),
                10));
        addItemTooltip(GTNLItemList.HumongousInputBusUEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusUIV.set(
            new HumongousInputBus(
                21713,
                "HumongousInputBusUIV",
                StatCollector.translateToLocal("HumongousInputBusUIV"),
                11));
        addItemTooltip(GTNLItemList.HumongousInputBusUIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusUMV.set(
            new HumongousInputBus(
                21714,
                "HumongousInputBusUMV",
                StatCollector.translateToLocal("HumongousInputBusUMV"),
                12));
        addItemTooltip(GTNLItemList.HumongousInputBusUMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusUXV.set(
            new HumongousInputBus(
                21715,
                "HumongousInputBusUXV",
                StatCollector.translateToLocal("HumongousInputBusUXV"),
                13));
        addItemTooltip(GTNLItemList.HumongousInputBusUXV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.HumongousInputBusMAX.set(
            new HumongousInputBus(
                21716,
                "HumongousInputBusMAX",
                StatCollector.translateToLocal("HumongousInputBusMAX"),
                14));
        addItemTooltip(GTNLItemList.HumongousInputBusMAX.get(1), AnimatedText.SCIENCE_NOT_LEISURE);
    }

    public static void registerBasicMachine() {
        GTNLItemList.SteamTurbineLV.set(
            new SteamTurbine(
                STEAM_TURBINE_LV.ID,
                "BasicSteamTurbine",
                StatCollector.translateToLocal("SteamTurbineLV"),
                1));
        addItemTooltip(GTNLItemList.SteamTurbineLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamTurbineMV.set(
            new SteamTurbine(
                STEAM_TURBINE_MV.ID,
                "AdvancedSteamTurbine",
                StatCollector.translateToLocal("SteamTurbineMV"),
                2));
        addItemTooltip(GTNLItemList.SteamTurbineMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.SteamTurbineHV.set(
            new SteamTurbine(
                STEAM_TURBINE_HV.ID,
                "AdvancedSteamTurbineII",
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

        GTNLItemList.GasCollectorLV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_LV.ID,
                "GasCollectorLV",
                StatCollector.translateToLocal("GasCollectorLV"),
                1,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_00"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorLV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorMV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_MV.ID,
                "GasCollectorMV",
                StatCollector.translateToLocal("GasCollectorMV"),
                2,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_00"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorHV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_HV.ID,
                "GasCollectorHV",
                StatCollector.translateToLocal("GasCollectorHV"),
                3,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_00"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorEV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_EV.ID,
                "GasCollectorEV",
                StatCollector.translateToLocal("GasCollectorEV"),
                4,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_00"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorIV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_IV.ID,
                "GasCollectorIV",
                StatCollector.translateToLocal("GasCollectorIV"),
                5,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_01"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorLuV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_LUV.ID,
                "GasCollectorLuV",
                StatCollector.translateToLocal("GasCollectorLuV"),
                6,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_01"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorLuV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorZPM.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_ZPM.ID,
                "GasCollectorZPM",
                StatCollector.translateToLocal("GasCollectorZPM"),
                7,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_01"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorZPM.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorUV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_UV.ID,
                "GasCollectorUV",
                StatCollector.translateToLocal("GasCollectorUV"),
                8,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_02"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorUV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorUHV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_UHV.ID,
                "GasCollectorUHV",
                StatCollector.translateToLocal("GasCollectorUHV"),
                9,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_02"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorUHV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorUEV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_UEV.ID,
                "GasCollectorUEV",
                StatCollector.translateToLocal("GasCollectorUEV"),
                10,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_02"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorUEV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorUIV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_UIV.ID,
                "GasCollectorUIV",
                StatCollector.translateToLocal("GasCollectorUIV"),
                11,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_02"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorUIV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorUMV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_UMV.ID,
                "GasCollectorUMV",
                StatCollector.translateToLocal("GasCollectorUMV"),
                12,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_02"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorUMV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);

        GTNLItemList.GasCollectorUXV.set(
            new MTEBasicMachineWithRecipe(
                GAS_COLLECTOR_UXV.ID,
                "GasCollectorUXV",
                StatCollector.translateToLocal("GasCollectorUXV"),
                13,
                new String[] { StatCollector.translateToLocal("Tooltip_GasCollector_03"),
                    StatCollector.translateToLocal("GT5U.MBTT.MachineType") + ": "
                        + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("GasCollectorRecipeType")
                        + EnumChatFormatting.RESET },
                RecipeRegister.GasCollectorRecipes,
                3,
                3,
                true,
                SoundResource.NONE,
                MTEBasicMachineWithRecipe.SpecialEffects.NONE,
                "GAS_COLLECTOR",
                null).getStackForm(1L));
        addItemTooltip(GTNLItemList.GasCollectorUXV.get(1), AnimatedText.SCIENCE_NOT_LEISURE);
    }

    public static void registerMTEWireAndPipe() {
        CrackRecipeAdder.registerWire(STAR_GATE_WIRE.ID, MaterialPool.Stargate, 2147483647, 2147483647, 0, true);
        MaterialUtils.generateGTFluidPipes(Materials.BlueAlloy, BLUE_ALLOY_PIPE.ID, 4000, 3000, true);
        CrackRecipeAdder.registerPipe(COMPRESSED_STEAM_PIPE.ID, MaterialPool.CompressedSteam, 250000, 10000, true);
        CrackRecipeAdder.registerPipe(STRONZE_PIPE.ID, MaterialPool.Stronze, 15000, 10000, true);
        CrackRecipeAdder.registerPipe(BREEL_PIPE.ID, MaterialPool.Breel, 10000, 10000, true);
        // 这个可用 MaterialUtils.generateNonGTFluidPipes(GregtechOrePrefixes.GT_Materials.Void, 22013, 500, 2000,
        // true);
        // 这个渲染炸了 MaterialUtils.registerPipeGTPP(22020, MaterialsAlloy.BLOODSTEEL, 123, 123, true);
    }

    private static void registerCovers() {
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
            context -> new CoverFluidRegulator(context, 1048576, TextureFactory.of(OVERLAY_PUMP)));

        CoverRegistry.registerCover(
            GTNLItemList.HydraulicSteamValve.get(1L),
            TextureFactory.of(MACHINE_CASINGS[1][0], TextureFactory.of(OVERLAY_VALVE)),
            context -> new CoverSteamValve(context, 16777216, TextureFactory.of(OVERLAY_VALVE)));

        CoverRegistry.registerCover(
            GTNLItemList.HydraulicSteamRegulator.get(1L),
            TextureFactory.of(MACHINE_CASINGS[1][0], TextureFactory.of(OVERLAY_VALVE)),
            context -> new CoverSteamRegulator(context, 16777216, TextureFactory.of(OVERLAY_VALVE)));

    }

    public static void registerGlasses() {
        BorosilicateGlass.registerGlass(BlockLoader.PlayerDoll, 0, (byte) 13);
        BorosilicateGlass.registerGlass(BlockLoader.MetaBlockGlass, 0, (byte) 10);
        BorosilicateGlass.registerGlass(BlockLoader.MetaBlockGlass, 1, (byte) 8);
        BorosilicateGlass.registerGlass(BlockLoader.MetaBlockGlass, 2, (byte) 7);

        for (int LampMeta = 1; LampMeta <= 32; LampMeta++) {
            BorosilicateGlass.registerGlass(BlockLoader.MetaBlockGlow, LampMeta, (byte) 3);
        }

        for (int LampOffMeta = 3; LampOffMeta <= 34; LampOffMeta++) {
            BorosilicateGlass.registerGlass(BlockLoader.MetaBlock, LampOffMeta, (byte) 3);
        }
    }

    public static void run() {
        Logger.INFO("GTNL Content | Registering MTE Block Machine.");
        registerMTEHatch();
        loadMachines();
        registerMTEWireAndPipe();
        registerBasicMachine();
        registerCovers();
    }
}
