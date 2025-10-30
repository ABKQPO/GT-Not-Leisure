package com.science.gtnl.utils.enums;

import static gregtech.api.enums.GTValues.NI;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLCreativeTabs;
import com.science.gtnl.utils.Utils;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.IItemContainer;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

@SuppressWarnings("unused")
public enum GTNLItemList implements IItemContainer {

    CompressedStargateTier0,
    CompressedStargateTier1,
    CompressedStargateTier2,
    CompressedStargateTier3,
    CompressedStargateTier4,
    CompressedStargateTier5,
    CompressedStargateTier6,
    CompressedStargateTier7,
    CompressedStargateTier8,
    CompressedStargateTier9,
    LaserBeacon,
    PlayerLeash,
    ArtificialStarRender,
    NanoPhagocytosisPlantRender,
    EternalGregTechWorkshopRender,
    PlayerDoll,
    HoneyFluidBlock,
    ShimmerFluidBlock,
    CardboardBox,
    WaterCandle,

    BronzeBrickCasing,
    SteelBrickCasing,
    CrushingWheels,
    SolarBoilingCell,
    BronzeMachineFrame,
    SteelMachineFrame,

    TestMetaBlock01_0,
    NewHorizonsCoil,
    StargateCoil,
    BlackLampOff,
    BlackLampOffBorderless,
    PinkLampOff,
    PinkLampOffBorderless,
    RedLampOff,
    RedLampOffBorderless,
    OrangeLampOff,
    OrangeLampOffBorderless,
    YellowLampOff,
    YellowLampOffBorderless,
    GreenLampOff,
    GreenLampOffBorderless,
    LimeLampOff,
    LimeLampOffBorderless,
    BlueLampOff,
    BlueLampOffBorderless,
    LightBlueLampOff,
    LightBlueLampOffBorderless,
    CyanLampOff,
    CyanLampOffBorderless,
    BrownLampOff,
    BrownLampOffBorderless,
    MagentaLampOff,
    MagentaLampOffBorderless,
    PurpleLampOff,
    PurpleLampOffBorderless,
    GrayLampOff,
    GrayLampOffBorderless,
    LightGrayLampOff,
    LightGrayLampOffBorderless,
    WhiteLampOff,
    WhiteLampOffBorderless,
    BlazeCubeBlock,
    CompressedStargateCoil,
    CompressedStargateCoil1,
    CompressedStargateCoil2,
    CompressedStargateCoil3,
    CompressedStargateCoil4,
    CompressedStargateCoil5,
    CompressedStargateCoil6,
    CompressedStargateCoil7,
    CompressedStargateCoil8,
    CompressedStargateCoil9,

    TestCasing,
    SteamAssemblyCasing,
    HeatVent,
    SlicingBlades,
    NeutroniumPipeCasing,
    NeutroniumGearbox,
    Laser_Cooling_Casing,
    Antifreeze_Heatproof_Machine_Casing,
    MolybdenumDisilicideCoil,
    EnergeticPhotovoltaicBlock,
    AdvancedPhotovoltaicBlock,
    VibrantPhotovoltaicBlock,
    TungstensteelGearbox,
    DimensionallyStableCasing,
    PressureBalancedCasing,
    ABSUltraSolidCasing,
    GravitationalFocusingLensBlock,
    GaiaStabilizedForceFieldCasing,
    HyperCore,
    ChemicallyResistantCasing,
    UltraPoweredCasing,
    SteamgateRingBlock,
    SteamgateChevronBlock,
    IronReinforcedWood,
    BronzeReinforcedWood,
    SteelReinforcedWood,
    BreelPipeCasing,
    StronzeWrappedCasing,
    HydraulicAssemblingCasing,
    HyperPressureBreelCasing,
    BreelPlatedCasing,
    SteamCompactPipeCasing,
    VibrationSafeCasing,
    IndustrialSteamCasing,
    AdvancedIndustrialSteamCasing,
    StainlessSteelGearBox,

    TrollFace,
    DepletedExcitedNaquadahFuelRod,
    BlazeCube,
    EnhancementCore,
    StellarConstructionFrameMaterial,
    ActivatedGaiaPylon,
    PrecisionSteamMechanism,
    MeteorMinerSchematic1,
    MeteorMinerSchematic2,
    CircuitResonaticULV,
    CircuitResonaticLV,
    CircuitResonaticMV,
    CircuitResonaticHV,
    CircuitResonaticEV,
    CircuitResonaticIV,
    CircuitResonaticLuV,
    CircuitResonaticZPM,
    CircuitResonaticUV,
    CircuitResonaticUHV,
    CircuitResonaticUEV,
    CircuitResonaticUIV,
    VerySimpleCircuit,
    SimpleCircuit,
    BasicCircuit,
    AdvancedCircuit,
    EliteCircuit,
    StargateSingularity,
    StargateCompressedSingularity,
    BiowareSMDCapacitor,
    BiowareSMDDiode,
    BiowareSMDInductor,
    BiowareSMDResistor,
    BiowareSMDTransistor,
    ExoticSMDCapacitor,
    ExoticSMDDiode,
    ExoticSMDInductor,
    ExoticSMDResistor,
    ExoticSMDTransistor,
    TemporallySMDCapacitor,
    TemporallySMDDiode,
    TemporallySMDInductor,
    TemporallySMDResistor,
    TemporallySMDTransistor,
    CosmicSMDCapacitor,
    CosmicSMDDiode,
    CosmicSMDInductor,
    CosmicSMDResistor,
    CosmicSMDTransistor,
    LVParallelControllerCore,
    MVParallelControllerCore,
    HVParallelControllerCore,
    EVParallelControllerCore,
    IVParallelControllerCore,
    LuVParallelControllerCore,
    ZPMParallelControllerCore,
    UVParallelControllerCore,
    UHVParallelControllerCore,
    UEVParallelControllerCore,
    UIVParallelControllerCore,
    UMVParallelControllerCore,
    UXVParallelControllerCore,
    MAXParallelControllerCore,
    NagaBook,
    TwilightForestBook,
    LichBook,
    MinotaurBook,
    HydraBook,
    KnightPhantomBook,
    UrGhastBook,
    AlphaYetiBook,
    SnowQueenBook,
    FinalBook,
    GiantBook,
    ClayedGlowstone,
    QuantumDisk,
    NeutroniumBoule,
    NeutroniumWafer,
    HighlyAdvancedSocWafer,
    HighlyAdvancedSoc,
    ZnFeAlClCatalyst,
    BlackLight,
    SteamgateDialingDevice,
    SteamgateChevron,
    SteamgateChevronUpgrade,
    SteamgateIrisBlade,
    SteamgateIrisUpgrade,
    SteamgateHeatContainmentPlate,
    SteamgateFrame,
    SteamgateCoreCrystal,
    HydraulicMotor,
    HydraulicPiston,
    HydraulicPump,
    HydraulicArm,
    HydraulicConveyor,
    HydraulicRegulator,
    HydraulicVaporGenerator,
    HydraulicSteamJetSpewer,
    HydraulicSteamReceiver,
    HydraulicSteamValve,
    HydraulicSteamRegulator,
    SadBapyCatToken,
    CompressedSteamTurbine,
    SteelTurbine,
    PipelessSteamCover,
    IronTurbine,
    BronzeTurbine,
    VoidCover,

    LVWirelessEnergyCover,
    MVWirelessEnergyCover,
    HVWirelessEnergyCover,
    EVWirelessEnergyCover,
    IVWirelessEnergyCover,
    LuVWirelessEnergyCover,
    ZPMWirelessEnergyCover,
    UVWirelessEnergyCover,
    UHVWirelessEnergyCover,
    UEVWirelessEnergyCover,
    UIVWirelessEnergyCover,
    UMVWirelessEnergyCover,
    UXVWirelessEnergyCover,
    MAXWirelessEnergyCover,

    LVWirelessEnergyCover4A,
    MVWirelessEnergyCover4A,
    HVWirelessEnergyCover4A,
    EVWirelessEnergyCover4A,
    IVWirelessEnergyCover4A,
    LuVWirelessEnergyCover4A,
    ZPMWirelessEnergyCover4A,
    UVWirelessEnergyCover4A,
    UHVWirelessEnergyCover4A,
    UEVWirelessEnergyCover4A,
    UIVWirelessEnergyCover4A,
    UMVWirelessEnergyCover4A,
    UXVWirelessEnergyCover4A,
    MAXWirelessEnergyCover4A,

    ManaElectricProspectorTool,
    DebugElectricProspectorTool,

    DebugItem,
    TestItem,
    KFCFamily,
    RecordSus,
    RecordNewHorizons,
    RecordLavaChicken,
    FakeItemSiren,
    TimeStopPocketWatch,
    InfinityTorch,
    InfinityWaterBucket,
    InfinityLavaBucket,
    InfinityHoneyBucket,
    InfinityShimmerBucket,
    SuperstrongSponge,
    SteamRocket,
    HoneyBucket,
    ShimmerBucket,
    SlimeSaddle,
    InfinityCell,
    InfinityDyeCell,
    InfinityDyeFluidCell,
    InfinityCobblestoneCell,
    NullPointerException,
    NetherTeleporter,
    SuspiciousStew,
    VeinMiningPickaxe,
    PortableBasicWorkBench,
    PortableAdvancedWorkBench,
    PortableFurnace,
    PortableAnvil,
    PortableEnderChest,
    PortableEnchantingTable,
    PortableCompressedChest,
    PortableInfinityChest,
    PortableCopperChest,
    PortableIronChest,
    PortableSilverChest,
    PortableSteelChest,
    PortableGoldenChest,
    PortableDiamondChest,
    PortableCrystalChest,
    PortableObsidianChest,
    PortableNetheriteChest,
    PortableDarkSteelChest,
    Stick,
    WirelessUpgradeChip,

    SatietyRing,
    SuperReachRing,
    RejectionRing,
    RoyalGel,
    PhysicsCape,
    LuckyHorseshoe,

    CircuitIntegratedPlus,

    InfinityFuelRod,
    InfinityFuelRodDepleted,

    SaplingBrickuoia,

    MilledNaquadahEnriched,

    TwilightSword,

    GaiaGlass,
    TerraGlass,
    FusionGlass,
    ConcentratingSieveMesh,

    ShirabonReinforcedBoronSilicateGlass,
    QuarkGluonPlasmaReinforcedBoronSilicateGlass,

    FortifyGlowstone,
    BlackLamp,
    BlackLampBorderless,
    PinkLamp,
    PinkLampBorderless,
    RedLamp,
    RedLampBorderless,
    OrangeLamp,
    OrangeLampBorderless,
    YellowLamp,
    YellowLampBorderless,
    GreenLamp,
    GreenLampBorderless,
    LimeLamp,
    LimeLampBorderless,
    BlueLamp,
    BlueLampBorderless,
    LightBlueLamp,
    LightBlueLampBorderless,
    CyanLamp,
    CyanLampBorderless,
    BrownLamp,
    BrownLampBorderless,
    MagentaLamp,
    MagentaLampBorderless,
    PurpleLamp,
    PurpleLampBorderless,
    GrayLamp,
    GrayLampBorderless,
    LightGrayLamp,
    LightGrayLampBorderless,
    WhiteLamp,
    WhiteLampBorderless,

    MegaAlloyBlastSmelter,
    MegaBlastFurnace,
    BrickedBlastFurnace,
    ColdIceFreezer,
    BlazeBlastFurnace,
    ChemicalPlant,
    VacuumFreezer,
    LargeMacerationTower,
    LargeCutter,
    LargeSiftingFunnel,
    LargeIndustrialLathe,
    LargeMaterialPress,
    LargeWiremill,
    LargeBender,
    ElectricImplosionCompressor,
    LargeExtruder,
    LargeArcSmelter,
    LargeForming,
    LargeElectrolyzer,
    LargeElectromagnet,
    LargeAssembler,
    LargeMixer,
    LargeCentrifuge,
    LargeChemicalBath,
    LargeAutoclave,
    LargeSolidifier,
    LargeExtractor,
    EnergyInfuser,
    LargeCanning,
    Digester,
    AlloyBlastSmelter,
    LargeSteamOreWasher,
    LargeHammer,
    IsaMill,
    FlotationCellRegulator,
    VacuumDryingFurnace,
    LargeDistillery,
    Incubator,
    LargeEngravingLaser,
    FishingGround,
    LargePacker,
    LargeAlloySmelter,
    LargePyrolyseOven,
    PreciseAssembler,
    LuvKuangBiaoOneGiantNuclearFusionReactor,
    ZpmKuangBiaoTwoGiantNuclearFusionReactor,
    UvKuangBiaoThreeGiantNuclearFusionReactor,
    UhvKuangBiaoFourGiantNuclearFusionReactor,
    UevKuangBiaoFiveGiantNuclearFusionReactor,
    LargeSteamCentrifuge,
    LargeSteamHammer,
    LargeSteamCompressor,
    LargeBoilerBronze,
    LargeBoilerSteel,
    LargeBoilerTitanium,
    LargeBoilerTungstenSteel,
    LargeSteamMixer,
    ElectricBlastFurnace,

    EternalGregTechWorkshop,
    EGTWFusionModule,
    ETGWEyeOfHarmonyModule,

    SteamElevator,
    SteamBeaconModuleI,
    SteamBeaconModuleII,
    SteamBeaconModuleIII,
    SteamMonsterRepellentModuleI,
    SteamMonsterRepellentModuleII,
    SteamMonsterRepellentModuleIII,
    SteamFlightModule,
    SteamWeatherModule,
    SteamOreProcessorModule,
    SteamEntityCrusherModule,
    SteamApiaryModule,
    SteamBeeBreedingModule,
    SteamGreenhouseModule,
    SteamOilDrillModuleI,
    SteamOilDrillModuleII,
    SteamOilDrillModuleIII,

    CompoundExtremeCoolingUnit,
    EyeOfHarmonyInjector,
    HighPerformanceComputationArray,
    NanoAssemblerMarkL,
    AetronPressor,
    NanitesCircuitAssemblyFactory,
    MegaMixer,
    HighEnergyLaserLathe,
    HeavyRolling,
    SuperconductingMagneticPresser,
    FieldForgePress,
    SuperconductingElectromagnetism,
    VortexMatterCentrifuge,
    EngravingLaserPlant,
    SpaceAssembler,
    LargeGasCollector,
    LargeBioLab,
    SuperSpaceElevator,
    NanitesIntegratedProcessingCenter,
    BioengineeringModule,
    PolymerTwistingModule,
    OreExtractionModule,
    MagneticEnergyReactionFurnace,
    NanoPhagocytosisPlant,
    IntegratedAssemblyFacility,
    HighPressureSteamFusionReactor,
    SteamExtractinator,
    SteamWoodcutter,
    SteamCarpenter,
    SteamRockBreaker,
    SteamLavaMaker,
    SteamInfernalCokeOven,
    SteamFusionReactor,
    SteamManufacturer,
    MegaSolarBoiler,
    SteamCactusWonder,
    MegaSteamCompressor,
    SteamGateAssembler,
    Steamgate,
    AdvancedInfiniteDriller,
    CircuitComponentAssemblyLine,
    PlatinumBasedTreatment,
    CrackerHub,
    LargeSteamFormingPress,
    LargeSteamSifter,
    ShallowChemicalCoupling,
    ResourceCollectionModule,
    FuelRefiningComplex,
    GrandAssemblyLine,
    DecayHastener,
    LargeSteamExtruder,
    DraconicFusionCrafting,
    LargeNaquadahReactor,
    MolecularTransformer,
    WoodDistillation,
    ElementCopying,
    LargeIncubator,
    LargeSteamExtractor,
    ReactionFurnace,
    LibraryOfRuina,
    MatterFabricator,
    LargeBrewer,
    HandOfJohnDavisonRockefeller,
    EnergeticPhotovoltaicPowerStation,
    AdvancedPhotovoltaicPowerStation,
    VibrantPhotovoltaicPowerStation,
    IndustrialArcaneAssembler,
    NineIndustrialMultiMachine,
    RareEarthCentrifugal,
    ProcessingArray,
    MeteorMiner,
    LargeSteamCrusher,
    NeutroniumWireCutting,
    EdenGarden,
    BloodSoulSacrificialArray,
    TeleportationArrayToAlfheim,
    LapotronChip,
    LargeSteamFurnace,
    LargeSteamThermalCentrifuge,
    SteamCracking,
    LargeSteamChemicalBath,
    PrimitiveDistillationTower,
    LargeSteamAlloySmelter,
    ComponentAssembler,
    RealArtificialStar,
    CheatOreProcessingFactory,
    Desulfurizer,
    LargeCircuitAssembler,
    PetrochemicalPlant,
    SmeltingMixingFurnace,
    WhiteNightGenerator,
    LargeSteamCircuitAssembler,
    GenerationEarthEngine,
    LargeSteamLathe,
    LargeSteamCutting,
    SteamItemVault,
    PrimitiveBrickKiln,
    SingularityDataHub,
    ElectrocellGenerator,
    FOGAlloySmelterModule,
    FOGAlloyBlastSmelterModule,
    FOGExtractorModule,
    RocketAssembler,
    AdvancedRareEarthCentrifugal,
    MassFabricator,
    AdvancedMassFabricator,
    HorizontalCompressor,
    MegaVacuumDryingFurnace,
    MegaBathTank,
    MegaCanner,
    CompoundDistillationFractionator,

    EnergyHatchLV,
    EnergyHatchLV4A,
    EnergyHatchLV16A,
    EnergyHatchLV64A,

    EnergyHatchMV,
    EnergyHatchMV4A,
    EnergyHatchMV16A,
    EnergyHatchMV64A,

    EnergyHatchHV,
    EnergyHatchHV4A,
    EnergyHatchHV16A,
    EnergyHatchHV64A,

    EnergyHatchEV,
    EnergyHatchEV4A,
    EnergyHatchEV16A,
    EnergyHatchEV64A,

    EnergyHatchIV,
    EnergyHatchIV4A,
    EnergyHatchIV16A,
    EnergyHatchIV64A,

    EnergyHatchLuV,
    EnergyHatchLuV4A,
    EnergyHatchLuV16A,
    EnergyHatchLuV64A,

    EnergyHatchZPM,
    EnergyHatchZPM4A,
    EnergyHatchZPM16A,
    EnergyHatchZPM64A,

    EnergyHatchUV,
    EnergyHatchUV4A,
    EnergyHatchUV16A,
    EnergyHatchUV64A,

    EnergyHatchUHV,
    EnergyHatchUHV4A,
    EnergyHatchUHV16A,
    EnergyHatchUHV64A,

    EnergyHatchUEV,
    EnergyHatchUEV4A,
    EnergyHatchUEV16A,
    EnergyHatchUEV64A,

    EnergyHatchUIV,
    EnergyHatchUIV4A,
    EnergyHatchUIV16A,
    EnergyHatchUIV64A,

    EnergyHatchUMV,
    EnergyHatchUMV4A,
    EnergyHatchUMV16A,
    EnergyHatchUMV64A,

    EnergyHatchUXV,
    EnergyHatchUXV4A,
    EnergyHatchUXV16A,
    EnergyHatchUXV64A,

    EnergyHatchMAX,
    EnergyHatchMAX4A,
    EnergyHatchMAX16A,
    EnergyHatchMAX64A,

    DynamoHatchLV,
    DynamoHatchLV4A,
    DynamoHatchLV16A,
    DynamoHatchLV64A,

    DynamoHatchMV,
    DynamoHatchMV4A,
    DynamoHatchMV16A,
    DynamoHatchMV64A,

    DynamoHatchHV,
    DynamoHatchHV4A,
    DynamoHatchHV16A,
    DynamoHatchHV64A,

    DynamoHatchEV,
    DynamoHatchEV4A,
    DynamoHatchEV16A,
    DynamoHatchEV64A,

    DynamoHatchIV,
    DynamoHatchIV4A,
    DynamoHatchIV16A,
    DynamoHatchIV64A,

    DynamoHatchLuV,
    DynamoHatchLuV4A,
    DynamoHatchLuV16A,
    DynamoHatchLuV64A,

    DynamoHatchZPM,
    DynamoHatchZPM4A,
    DynamoHatchZPM16A,
    DynamoHatchZPM64A,

    DynamoHatchUV,
    DynamoHatchUV4A,
    DynamoHatchUV16A,
    DynamoHatchUV64A,

    DynamoHatchUHV,
    DynamoHatchUHV4A,
    DynamoHatchUHV16A,
    DynamoHatchUHV64A,

    DynamoHatchUEV,
    DynamoHatchUEV4A,
    DynamoHatchUEV16A,
    DynamoHatchUEV64A,

    DynamoHatchUIV,
    DynamoHatchUIV4A,
    DynamoHatchUIV16A,
    DynamoHatchUIV64A,

    DynamoHatchUMV,
    DynamoHatchUMV4A,
    DynamoHatchUMV16A,
    DynamoHatchUMV64A,

    DynamoHatchUXV,
    DynamoHatchUXV4A,
    DynamoHatchUXV16A,
    DynamoHatchUXV64A,

    DynamoHatchMAX,
    DynamoHatchMAX4A,
    DynamoHatchMAX16A,
    DynamoHatchMAX64A,

    LaserEnergyHatchIV256A,
    LaserEnergyHatchIV1024A,
    LaserEnergyHatchIV4096A,
    LaserEnergyHatchIV16384A,
    LaserEnergyHatchIV65536A,
    LaserEnergyHatchIV262144A,
    LaserEnergyHatchIV1048576A,
    LaserEnergyHatchIV4194304A,
    LaserEnergyHatchIV16777216A,

    LaserEnergyHatchLuV256A,
    LaserEnergyHatchLuV1024A,
    LaserEnergyHatchLuV4096A,
    LaserEnergyHatchLuV16384A,
    LaserEnergyHatchLuV65536A,
    LaserEnergyHatchLuV262144A,
    LaserEnergyHatchLuV1048576A,
    LaserEnergyHatchLuV4194304A,
    LaserEnergyHatchLuV16777216A,

    LaserEnergyHatchZPM256A,
    LaserEnergyHatchZPM1024A,
    LaserEnergyHatchZPM4096A,
    LaserEnergyHatchZPM16384A,
    LaserEnergyHatchZPM65536A,
    LaserEnergyHatchZPM262144A,
    LaserEnergyHatchZPM1048576A,
    LaserEnergyHatchZPM4194304A,
    LaserEnergyHatchZPM16777216A,

    LaserEnergyHatchUV256A,
    LaserEnergyHatchUV1024A,
    LaserEnergyHatchUV4096A,
    LaserEnergyHatchUV16384A,
    LaserEnergyHatchUV65536A,
    LaserEnergyHatchUV262144A,
    LaserEnergyHatchUV1048576A,
    LaserEnergyHatchUV4194304A,
    LaserEnergyHatchUV16777216A,

    LaserEnergyHatchUHV256A,
    LaserEnergyHatchUHV1024A,
    LaserEnergyHatchUHV4096A,
    LaserEnergyHatchUHV16384A,
    LaserEnergyHatchUHV65536A,
    LaserEnergyHatchUHV262144A,
    LaserEnergyHatchUHV1048576A,
    LaserEnergyHatchUHV4194304A,
    LaserEnergyHatchUHV16777216A,

    LaserEnergyHatchUEV256A,
    LaserEnergyHatchUEV1024A,
    LaserEnergyHatchUEV4096A,
    LaserEnergyHatchUEV16384A,
    LaserEnergyHatchUEV65536A,
    LaserEnergyHatchUEV262144A,
    LaserEnergyHatchUEV1048576A,
    LaserEnergyHatchUEV4194304A,
    LaserEnergyHatchUEV16777216A,

    LaserEnergyHatchUIV256A,
    LaserEnergyHatchUIV1024A,
    LaserEnergyHatchUIV4096A,
    LaserEnergyHatchUIV16384A,
    LaserEnergyHatchUIV65536A,
    LaserEnergyHatchUIV262144A,
    LaserEnergyHatchUIV1048576A,
    LaserEnergyHatchUIV4194304A,
    LaserEnergyHatchUIV16777216A,

    LaserEnergyHatchUMV256A,
    LaserEnergyHatchUMV1024A,
    LaserEnergyHatchUMV4096A,
    LaserEnergyHatchUMV16384A,
    LaserEnergyHatchUMV65536A,
    LaserEnergyHatchUMV262144A,
    LaserEnergyHatchUMV1048576A,
    LaserEnergyHatchUMV4194304A,
    LaserEnergyHatchUMV16777216A,

    LaserEnergyHatchUXV256A,
    LaserEnergyHatchUXV1024A,
    LaserEnergyHatchUXV4096A,
    LaserEnergyHatchUXV16384A,
    LaserEnergyHatchUXV65536A,
    LaserEnergyHatchUXV262144A,
    LaserEnergyHatchUXV1048576A,
    LaserEnergyHatchUXV4194304A,
    LaserEnergyHatchUXV16777216A,

    LaserEnergyHatchMAX256A,
    LaserEnergyHatchMAX1024A,
    LaserEnergyHatchMAX4096A,
    LaserEnergyHatchMAX16384A,
    LaserEnergyHatchMAX65536A,
    LaserEnergyHatchMAX262144A,
    LaserEnergyHatchMAX1048576A,
    LaserEnergyHatchMAX4194304A,
    LaserEnergyHatchMAX16777216A,

    LaserDynamoHatchIV256A,
    LaserDynamoHatchIV1024A,
    LaserDynamoHatchIV4096A,
    LaserDynamoHatchIV16384A,
    LaserDynamoHatchIV65536A,
    LaserDynamoHatchIV262144A,
    LaserDynamoHatchIV1048576A,
    LaserDynamoHatchIV4194304A,
    LaserDynamoHatchIV16777216A,

    LaserDynamoHatchLuV256A,
    LaserDynamoHatchLuV1024A,
    LaserDynamoHatchLuV4096A,
    LaserDynamoHatchLuV16384A,
    LaserDynamoHatchLuV65536A,
    LaserDynamoHatchLuV262144A,
    LaserDynamoHatchLuV1048576A,
    LaserDynamoHatchLuV4194304A,
    LaserDynamoHatchLuV16777216A,

    LaserDynamoHatchZPM256A,
    LaserDynamoHatchZPM1024A,
    LaserDynamoHatchZPM4096A,
    LaserDynamoHatchZPM16384A,
    LaserDynamoHatchZPM65536A,
    LaserDynamoHatchZPM262144A,
    LaserDynamoHatchZPM1048576A,
    LaserDynamoHatchZPM4194304A,
    LaserDynamoHatchZPM16777216A,

    LaserDynamoHatchUV256A,
    LaserDynamoHatchUV1024A,
    LaserDynamoHatchUV4096A,
    LaserDynamoHatchUV16384A,
    LaserDynamoHatchUV65536A,
    LaserDynamoHatchUV262144A,
    LaserDynamoHatchUV1048576A,
    LaserDynamoHatchUV4194304A,
    LaserDynamoHatchUV16777216A,

    LaserDynamoHatchUHV256A,
    LaserDynamoHatchUHV1024A,
    LaserDynamoHatchUHV4096A,
    LaserDynamoHatchUHV16384A,
    LaserDynamoHatchUHV65536A,
    LaserDynamoHatchUHV262144A,
    LaserDynamoHatchUHV1048576A,
    LaserDynamoHatchUHV4194304A,
    LaserDynamoHatchUHV16777216A,

    LaserDynamoHatchUEV256A,
    LaserDynamoHatchUEV1024A,
    LaserDynamoHatchUEV4096A,
    LaserDynamoHatchUEV16384A,
    LaserDynamoHatchUEV65536A,
    LaserDynamoHatchUEV262144A,
    LaserDynamoHatchUEV1048576A,
    LaserDynamoHatchUEV4194304A,
    LaserDynamoHatchUEV16777216A,

    LaserDynamoHatchUIV256A,
    LaserDynamoHatchUIV1024A,
    LaserDynamoHatchUIV4096A,
    LaserDynamoHatchUIV16384A,
    LaserDynamoHatchUIV65536A,
    LaserDynamoHatchUIV262144A,
    LaserDynamoHatchUIV1048576A,
    LaserDynamoHatchUIV4194304A,
    LaserDynamoHatchUIV16777216A,

    LaserDynamoHatchUMV256A,
    LaserDynamoHatchUMV1024A,
    LaserDynamoHatchUMV4096A,
    LaserDynamoHatchUMV16384A,
    LaserDynamoHatchUMV65536A,
    LaserDynamoHatchUMV262144A,
    LaserDynamoHatchUMV1048576A,
    LaserDynamoHatchUMV4194304A,
    LaserDynamoHatchUMV16777216A,

    LaserDynamoHatchUXV256A,
    LaserDynamoHatchUXV1024A,
    LaserDynamoHatchUXV4096A,
    LaserDynamoHatchUXV16384A,
    LaserDynamoHatchUXV65536A,
    LaserDynamoHatchUXV262144A,
    LaserDynamoHatchUXV1048576A,
    LaserDynamoHatchUXV4194304A,
    LaserDynamoHatchUXV16777216A,

    LaserDynamoHatchMAX256A,
    LaserDynamoHatchMAX1024A,
    LaserDynamoHatchMAX4096A,
    LaserDynamoHatchMAX16384A,
    LaserDynamoHatchMAX65536A,
    LaserDynamoHatchMAX262144A,
    LaserDynamoHatchMAX1048576A,
    LaserDynamoHatchMAX4194304A,
    LaserDynamoHatchMAX16777216A,

    WirelessEnergyHatchLV,
    WirelessEnergyHatchLV4A,
    WirelessEnergyHatchLV16A,
    WirelessEnergyHatchLV64A,

    WirelessEnergyHatchMV,
    WirelessEnergyHatchMV4A,
    WirelessEnergyHatchMV16A,
    WirelessEnergyHatchMV64A,

    WirelessEnergyHatchHV,
    WirelessEnergyHatchHV4A,
    WirelessEnergyHatchHV16A,
    WirelessEnergyHatchHV64A,

    WirelessEnergyHatchEV,
    WirelessEnergyHatchEV4A,
    WirelessEnergyHatchEV16A,
    WirelessEnergyHatchEV64A,

    WirelessEnergyHatchIV,
    WirelessEnergyHatchIV4A,
    WirelessEnergyHatchIV16A,
    WirelessEnergyHatchIV64A,
    WirelessEnergyHatchIV256A,
    WirelessEnergyHatchIV1024A,
    WirelessEnergyHatchIV4096A,
    WirelessEnergyHatchIV16384A,
    WirelessEnergyHatchIV65536A,
    WirelessEnergyHatchIV262144A,
    WirelessEnergyHatchIV1048576A,
    WirelessEnergyHatchIV4194304A,
    WirelessEnergyHatchIV16777216A,

    WirelessEnergyHatchLuV,
    WirelessEnergyHatchLuV4A,
    WirelessEnergyHatchLuV16A,
    WirelessEnergyHatchLuV64A,
    WirelessEnergyHatchLuV256A,
    WirelessEnergyHatchLuV1024A,
    WirelessEnergyHatchLuV4096A,
    WirelessEnergyHatchLuV16384A,
    WirelessEnergyHatchLuV65536A,
    WirelessEnergyHatchLuV262144A,
    WirelessEnergyHatchLuV1048576A,
    WirelessEnergyHatchLuV4194304A,
    WirelessEnergyHatchLuV16777216A,

    WirelessEnergyHatchZPM,
    WirelessEnergyHatchZPM4A,
    WirelessEnergyHatchZPM16A,
    WirelessEnergyHatchZPM64A,
    WirelessEnergyHatchZPM256A,
    WirelessEnergyHatchZPM1024A,
    WirelessEnergyHatchZPM4096A,
    WirelessEnergyHatchZPM16384A,
    WirelessEnergyHatchZPM65536A,
    WirelessEnergyHatchZPM262144A,
    WirelessEnergyHatchZPM1048576A,
    WirelessEnergyHatchZPM4194304A,
    WirelessEnergyHatchZPM16777216A,

    WirelessEnergyHatchUV,
    WirelessEnergyHatchUV4A,
    WirelessEnergyHatchUV16A,
    WirelessEnergyHatchUV64A,
    WirelessEnergyHatchUV256A,
    WirelessEnergyHatchUV1024A,
    WirelessEnergyHatchUV4096A,
    WirelessEnergyHatchUV16384A,
    WirelessEnergyHatchUV65536A,
    WirelessEnergyHatchUV262144A,
    WirelessEnergyHatchUV1048576A,
    WirelessEnergyHatchUV4194304A,
    WirelessEnergyHatchUV16777216A,

    WirelessEnergyHatchUHV,
    WirelessEnergyHatchUHV4A,
    WirelessEnergyHatchUHV16A,
    WirelessEnergyHatchUHV64A,
    WirelessEnergyHatchUHV256A,
    WirelessEnergyHatchUHV1024A,
    WirelessEnergyHatchUHV4096A,
    WirelessEnergyHatchUHV16384A,
    WirelessEnergyHatchUHV65536A,
    WirelessEnergyHatchUHV262144A,
    WirelessEnergyHatchUHV1048576A,
    WirelessEnergyHatchUHV4194304A,
    WirelessEnergyHatchUHV16777216A,

    WirelessEnergyHatchUEV,
    WirelessEnergyHatchUEV4A,
    WirelessEnergyHatchUEV16A,
    WirelessEnergyHatchUEV64A,
    WirelessEnergyHatchUEV256A,
    WirelessEnergyHatchUEV1024A,
    WirelessEnergyHatchUEV4096A,
    WirelessEnergyHatchUEV16384A,
    WirelessEnergyHatchUEV65536A,
    WirelessEnergyHatchUEV262144A,
    WirelessEnergyHatchUEV1048576A,
    WirelessEnergyHatchUEV4194304A,
    WirelessEnergyHatchUEV16777216A,

    WirelessEnergyHatchUIV,
    WirelessEnergyHatchUIV4A,
    WirelessEnergyHatchUIV16A,
    WirelessEnergyHatchUIV64A,
    WirelessEnergyHatchUIV256A,
    WirelessEnergyHatchUIV1024A,
    WirelessEnergyHatchUIV4096A,
    WirelessEnergyHatchUIV16384A,
    WirelessEnergyHatchUIV65536A,
    WirelessEnergyHatchUIV262144A,
    WirelessEnergyHatchUIV1048576A,
    WirelessEnergyHatchUIV4194304A,
    WirelessEnergyHatchUIV16777216A,

    WirelessEnergyHatchUMV,
    WirelessEnergyHatchUMV4A,
    WirelessEnergyHatchUMV16A,
    WirelessEnergyHatchUMV64A,
    WirelessEnergyHatchUMV256A,
    WirelessEnergyHatchUMV1024A,
    WirelessEnergyHatchUMV4096A,
    WirelessEnergyHatchUMV16384A,
    WirelessEnergyHatchUMV65536A,
    WirelessEnergyHatchUMV262144A,
    WirelessEnergyHatchUMV1048576A,
    WirelessEnergyHatchUMV4194304A,
    WirelessEnergyHatchUMV16777216A,

    WirelessEnergyHatchUXV,
    WirelessEnergyHatchUXV4A,
    WirelessEnergyHatchUXV16A,
    WirelessEnergyHatchUXV64A,
    WirelessEnergyHatchUXV256A,
    WirelessEnergyHatchUXV1024A,
    WirelessEnergyHatchUXV4096A,
    WirelessEnergyHatchUXV16384A,
    WirelessEnergyHatchUXV65536A,
    WirelessEnergyHatchUXV262144A,
    WirelessEnergyHatchUXV1048576A,
    WirelessEnergyHatchUXV4194304A,
    WirelessEnergyHatchUXV16777216A,

    WirelessEnergyHatchMAX,
    WirelessEnergyHatchMAX4A,
    WirelessEnergyHatchMAX16A,
    WirelessEnergyHatchMAX64A,
    WirelessEnergyHatchMAX256A,
    WirelessEnergyHatchMAX1024A,
    WirelessEnergyHatchMAX4096A,
    WirelessEnergyHatchMAX16384A,
    WirelessEnergyHatchMAX65536A,
    WirelessEnergyHatchMAX262144A,
    WirelessEnergyHatchMAX1048576A,
    WirelessEnergyHatchMAX4194304A,
    WirelessEnergyHatchMAX16777216A,

    LegendaryWirelessEnergyHatch,
    LegendaryWirelessDynamoHatch,

    AdvancedSuperDualInputHatchME,
    SuperDualInputHatchME,
    HumongousOutputBusLV,
    HumongousOutputBusMV,
    HumongousOutputBusHV,
    HumongousOutputBusEV,
    HumongousOutputBusIV,
    HumongousOutputBusLuV,
    HumongousOutputBusZPM,
    HumongousOutputBusUV,
    HumongousOutputBusUHV,
    HumongousOutputBusUEV,
    HumongousOutputBusUIV,
    HumongousOutputBusUMV,
    HumongousOutputBusUXV,
    HumongousOutputBusMAX,
    TypeFilteredInputBusME,
    LootBagRedemption,
    EnergyTransferNode,
    DieselGeneratorLV,
    DieselGeneratorMV,
    DieselGeneratorHV,
    GasTurbineLV,
    GasTurbineMV,
    GasTurbineHV,
    DebugDataAccessHatch,
    SuperVoidHatch,
    SuperVoidBus,
    OriginalInputHatch,
    OriginalOutputHatch,
    VaultPortHatch,
    NanitesInputBus,
    Enchanting,
    OredictInputBusME,
    Replicator,
    SuperInputHatchME,
    AdvancedSuperInputHatchME,
    SuperInputBusME,
    AdvancedSuperInputBusME,
    DebugResearchStation,
    ExplosionDynamoHatch,
    AutoConfigurationMaintenanceHatch,
    PipelessSteamHatch,
    PipelessSteamVent,
    PipelessJetstreamHatch,
    PipelessJetstreamVent,
    SteamTurbineLV,
    SteamTurbineMV,
    SteamTurbineHV,
    SteamAssemblerBronze,
    SteamAssemblerSteel,
    ManaTank,
    BigSteamInputHatch,
    SuperDataAccessHatch,
    FluidManaInputHatch,
    FluidIceInputHatch,
    FluidBlazeInputHatch,
    SuperCraftingInputHatchME,
    SuperCraftingInputBusME,
    SuperCraftingInputProxy,
    HumongousSolidifierHatch,
    DebugEnergyHatch,
    ParallelControllerHatchLV,
    ParallelControllerHatchMV,
    ParallelControllerHatchHV,
    ParallelControllerHatchEV,
    ParallelControllerHatchIV,
    ParallelControllerHatchLuV,
    ParallelControllerHatchZPM,
    ParallelControllerHatchUV,
    ParallelControllerHatchUHV,
    ParallelControllerHatchUEV,
    ParallelControllerHatchUIV,
    ParallelControllerHatchUMV,
    ParallelControllerHatchUXV,
    ParallelControllerHatchMAX,
    TapDynamoHatchLV,
    NinefoldInputHatchEV,
    NinefoldInputHatchIV,
    NinefoldInputHatchLuV,
    NinefoldInputHatchZPM,
    NinefoldInputHatchUV,
    NinefoldInputHatchUHV,
    NinefoldInputHatchUEV,
    NinefoldInputHatchUIV,
    NinefoldInputHatchUMV,
    NinefoldInputHatchUXV,
    NinefoldInputHatchMAX,
    HumongousNinefoldInputHatch,
    HumongousDualInputHatchLV,
    HumongousDualInputHatchMV,
    HumongousDualInputHatchHV,
    HumongousDualInputHatchEV,
    HumongousDualInputHatchIV,
    HumongousDualInputHatchLuV,
    HumongousDualInputHatchZPM,
    HumongousDualInputHatchUV,
    HumongousDualInputHatchUHV,
    HumongousDualInputHatchUEV,
    HumongousDualInputHatchUIV,
    HumongousDualInputHatchUMV,
    HumongousDualInputHatchUXV,
    HumongousDualInputHatchMAX,
    DualInputHatchLV,
    DualInputHatchMV,
    DualInputHatchHV,
    DualInputHatchEV,
    DualInputHatchIV,
    DualInputHatchLuV,
    DualInputHatchZPM,
    DualInputHatchUV,
    DualInputHatchUHV,
    DualInputHatchUEV,
    DualInputHatchUIV,
    DualInputHatchUMV,
    DualInputHatchUXV,
    DualInputHatchMAX,
    ManaDynamoHatchLV,
    ManaDynamoHatchHV,
    ManaDynamoHatchIV,
    ManaDynamoHatchZPM,
    ManaEnergyHatchLV,
    ManaEnergyHatchHV,
    ManaEnergyHatchIV,
    ManaEnergyHatchZPM,
    GasCollectorLV,
    GasCollectorMV,
    GasCollectorHV,
    GasCollectorEV,
    GasCollectorIV,
    GasCollectorLuV,
    GasCollectorZPM,
    GasCollectorUV,
    GasCollectorUHV,
    GasCollectorUEV,
    GasCollectorUIV,
    GasCollectorUMV,
    GasCollectorUXV;

    public static final GTNLItemList[] HUMONGOUS_OUTPUT_BUS = { HumongousOutputBusLV, HumongousOutputBusMV,
        HumongousOutputBusHV, HumongousOutputBusEV, HumongousOutputBusIV, HumongousOutputBusLuV, HumongousOutputBusZPM,
        HumongousOutputBusUV, HumongousOutputBusUHV, HumongousOutputBusUEV, HumongousOutputBusUIV,
        HumongousOutputBusUMV, HumongousOutputBusUXV, HumongousOutputBusMAX };

    public static final GTNLItemList[] ENERGY_HATCH_LV = { EnergyHatchLV, EnergyHatchLV4A, EnergyHatchLV16A,
        EnergyHatchLV64A };
    public static final GTNLItemList[] ENERGY_HATCH_MV = { EnergyHatchMV, EnergyHatchMV4A, EnergyHatchMV16A,
        EnergyHatchMV64A };
    public static final GTNLItemList[] ENERGY_HATCH_HV = { EnergyHatchHV, EnergyHatchHV4A, EnergyHatchHV16A,
        EnergyHatchHV64A };
    public static final GTNLItemList[] ENERGY_HATCH_EV = { EnergyHatchEV, EnergyHatchEV4A, EnergyHatchEV16A,
        EnergyHatchEV64A };
    public static final GTNLItemList[] ENERGY_HATCH_IV = { EnergyHatchIV, EnergyHatchIV4A, EnergyHatchIV16A,
        EnergyHatchIV64A };
    public static final GTNLItemList[] ENERGY_HATCH_LUV = { EnergyHatchLuV, EnergyHatchLuV4A, EnergyHatchLuV16A,
        EnergyHatchLuV64A };
    public static final GTNLItemList[] ENERGY_HATCH_ZPM = { EnergyHatchZPM, EnergyHatchZPM4A, EnergyHatchZPM16A,
        EnergyHatchZPM64A };
    public static final GTNLItemList[] ENERGY_HATCH_UV = { EnergyHatchUV, EnergyHatchUV4A, EnergyHatchUV16A,
        EnergyHatchUV64A };
    public static final GTNLItemList[] ENERGY_HATCH_UHV = { EnergyHatchUHV, EnergyHatchUHV4A, EnergyHatchUHV16A,
        EnergyHatchUHV64A };
    public static final GTNLItemList[] ENERGY_HATCH_UEV = { EnergyHatchUEV, EnergyHatchUEV4A, EnergyHatchUEV16A,
        EnergyHatchUEV64A };
    public static final GTNLItemList[] ENERGY_HATCH_UIV = { EnergyHatchUIV, EnergyHatchUIV4A, EnergyHatchUIV16A,
        EnergyHatchUIV64A };
    public static final GTNLItemList[] ENERGY_HATCH_UMV = { EnergyHatchUMV, EnergyHatchUMV4A, EnergyHatchUMV16A,
        EnergyHatchUMV64A };
    public static final GTNLItemList[] ENERGY_HATCH_UXV = { EnergyHatchUXV, EnergyHatchUXV4A, EnergyHatchUXV16A,
        EnergyHatchUXV64A };
    public static final GTNLItemList[] ENERGY_HATCH_MAX = { EnergyHatchMAX, EnergyHatchMAX4A, EnergyHatchMAX16A,
        EnergyHatchMAX64A };

    public static final GTNLItemList[][] ENERGY_HATCH = { ENERGY_HATCH_LV, ENERGY_HATCH_MV, ENERGY_HATCH_HV,
        ENERGY_HATCH_EV, ENERGY_HATCH_IV, ENERGY_HATCH_LUV, ENERGY_HATCH_ZPM, ENERGY_HATCH_UV, ENERGY_HATCH_UHV,
        ENERGY_HATCH_UEV, ENERGY_HATCH_UIV, ENERGY_HATCH_UMV, ENERGY_HATCH_UXV, ENERGY_HATCH_MAX };

    public static final GTNLItemList[] DYNAMO_HATCH_LV = { DynamoHatchLV, DynamoHatchLV4A, DynamoHatchLV16A,
        DynamoHatchLV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_MV = { DynamoHatchMV, DynamoHatchMV4A, DynamoHatchMV16A,
        DynamoHatchMV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_HV = { DynamoHatchHV, DynamoHatchHV4A, DynamoHatchHV16A,
        DynamoHatchHV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_EV = { DynamoHatchEV, DynamoHatchEV4A, DynamoHatchEV16A,
        DynamoHatchEV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_IV = { DynamoHatchIV, DynamoHatchIV4A, DynamoHatchIV16A,
        DynamoHatchIV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_LUV = { DynamoHatchLuV, DynamoHatchLuV4A, DynamoHatchLuV16A,
        DynamoHatchLuV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_ZPM = { DynamoHatchZPM, DynamoHatchZPM4A, DynamoHatchZPM16A,
        DynamoHatchZPM64A };
    public static final GTNLItemList[] DYNAMO_HATCH_UV = { DynamoHatchUV, DynamoHatchUV4A, DynamoHatchUV16A,
        DynamoHatchUV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_UHV = { DynamoHatchUHV, DynamoHatchUHV4A, DynamoHatchUHV16A,
        DynamoHatchUHV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_UEV = { DynamoHatchUEV, DynamoHatchUEV4A, DynamoHatchUEV16A,
        DynamoHatchUEV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_UIV = { DynamoHatchUIV, DynamoHatchUIV4A, DynamoHatchUIV16A,
        DynamoHatchUIV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_UMV = { DynamoHatchUMV, DynamoHatchUMV4A, DynamoHatchUMV16A,
        DynamoHatchUMV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_UXV = { DynamoHatchUXV, DynamoHatchUXV4A, DynamoHatchUXV16A,
        DynamoHatchUXV64A };
    public static final GTNLItemList[] DYNAMO_HATCH_MAX = { DynamoHatchMAX, DynamoHatchMAX4A, DynamoHatchMAX16A,
        DynamoHatchMAX64A };

    public static final GTNLItemList[][] DYNAMO_HATCH = { DYNAMO_HATCH_LV, DYNAMO_HATCH_MV, DYNAMO_HATCH_HV,
        DYNAMO_HATCH_EV, DYNAMO_HATCH_IV, DYNAMO_HATCH_LUV, DYNAMO_HATCH_ZPM, DYNAMO_HATCH_UV, DYNAMO_HATCH_UHV,
        DYNAMO_HATCH_UEV, DYNAMO_HATCH_UIV, DYNAMO_HATCH_UMV, DYNAMO_HATCH_UXV, DYNAMO_HATCH_MAX };

    public static final GTNLItemList[] LASER_ENERGY_HATCH_IV = { LaserEnergyHatchIV256A, LaserEnergyHatchIV1024A,
        LaserEnergyHatchIV4096A, LaserEnergyHatchIV16384A, LaserEnergyHatchIV65536A, LaserEnergyHatchIV262144A,
        LaserEnergyHatchIV1048576A, LaserEnergyHatchIV4194304A, LaserEnergyHatchIV16777216A };

    public static final GTNLItemList[] LASER_ENERGY_HATCH_LUV = { LaserEnergyHatchLuV256A, LaserEnergyHatchLuV1024A,
        LaserEnergyHatchLuV4096A, LaserEnergyHatchLuV16384A, LaserEnergyHatchLuV65536A, LaserEnergyHatchLuV262144A,
        LaserEnergyHatchLuV1048576A, LaserEnergyHatchLuV4194304A, LaserEnergyHatchLuV16777216A };

    public static final GTNLItemList[] LASER_ENERGY_HATCH_ZPM = { LaserEnergyHatchZPM256A, LaserEnergyHatchZPM1024A,
        LaserEnergyHatchZPM4096A, LaserEnergyHatchZPM16384A, LaserEnergyHatchZPM65536A, LaserEnergyHatchZPM262144A,
        LaserEnergyHatchZPM1048576A, LaserEnergyHatchZPM4194304A, LaserEnergyHatchZPM16777216A };

    public static final GTNLItemList[] LASER_ENERGY_HATCH_UV = { LaserEnergyHatchUV256A, LaserEnergyHatchUV1024A,
        LaserEnergyHatchUV4096A, LaserEnergyHatchUV16384A, LaserEnergyHatchUV65536A, LaserEnergyHatchUV262144A,
        LaserEnergyHatchUV1048576A, LaserEnergyHatchUV4194304A, LaserEnergyHatchUV16777216A };

    public static final GTNLItemList[] LASER_ENERGY_HATCH_UHV = { LaserEnergyHatchUHV256A, LaserEnergyHatchUHV1024A,
        LaserEnergyHatchUHV4096A, LaserEnergyHatchUHV16384A, LaserEnergyHatchUHV65536A, LaserEnergyHatchUHV262144A,
        LaserEnergyHatchUHV1048576A, LaserEnergyHatchUHV4194304A, LaserEnergyHatchUHV16777216A };

    public static final GTNLItemList[] LASER_ENERGY_HATCH_UEV = { LaserEnergyHatchUEV256A, LaserEnergyHatchUEV1024A,
        LaserEnergyHatchUEV4096A, LaserEnergyHatchUEV16384A, LaserEnergyHatchUEV65536A, LaserEnergyHatchUEV262144A,
        LaserEnergyHatchUEV1048576A, LaserEnergyHatchUEV4194304A, LaserEnergyHatchUEV16777216A };

    public static final GTNLItemList[] LASER_ENERGY_HATCH_UIV = { LaserEnergyHatchUIV256A, LaserEnergyHatchUIV1024A,
        LaserEnergyHatchUIV4096A, LaserEnergyHatchUIV16384A, LaserEnergyHatchUIV65536A, LaserEnergyHatchUIV262144A,
        LaserEnergyHatchUIV1048576A, LaserEnergyHatchUIV4194304A, LaserEnergyHatchUIV16777216A };

    public static final GTNLItemList[] LASER_ENERGY_HATCH_UMV = { LaserEnergyHatchUMV256A, LaserEnergyHatchUMV1024A,
        LaserEnergyHatchUMV4096A, LaserEnergyHatchUMV16384A, LaserEnergyHatchUMV65536A, LaserEnergyHatchUMV262144A,
        LaserEnergyHatchUMV1048576A, LaserEnergyHatchUMV4194304A, LaserEnergyHatchUMV16777216A };

    public static final GTNLItemList[] LASER_ENERGY_HATCH_UXV = { LaserEnergyHatchUXV256A, LaserEnergyHatchUXV1024A,
        LaserEnergyHatchUXV4096A, LaserEnergyHatchUXV16384A, LaserEnergyHatchUXV65536A, LaserEnergyHatchUXV262144A,
        LaserEnergyHatchUXV1048576A, LaserEnergyHatchUXV4194304A, LaserEnergyHatchUXV16777216A };

    public static final GTNLItemList[] LASER_ENERGY_HATCH_MAX = { LaserEnergyHatchMAX256A, LaserEnergyHatchMAX1024A,
        LaserEnergyHatchMAX4096A, LaserEnergyHatchMAX16384A, LaserEnergyHatchMAX65536A, LaserEnergyHatchMAX262144A,
        LaserEnergyHatchMAX1048576A, LaserEnergyHatchMAX4194304A, LaserEnergyHatchMAX16777216A };

    public static final GTNLItemList[][] LASER_ENERGY_HATCH = { LASER_ENERGY_HATCH_IV, LASER_ENERGY_HATCH_LUV,
        LASER_ENERGY_HATCH_ZPM, LASER_ENERGY_HATCH_UV, LASER_ENERGY_HATCH_UHV, LASER_ENERGY_HATCH_UEV,
        LASER_ENERGY_HATCH_UIV, LASER_ENERGY_HATCH_UMV, LASER_ENERGY_HATCH_UXV, LASER_ENERGY_HATCH_MAX };

    public static final GTNLItemList[] LASER_DYNAMO_HATCH_IV = { LaserDynamoHatchIV256A, LaserDynamoHatchIV1024A,
        LaserDynamoHatchIV4096A, LaserDynamoHatchIV16384A, LaserDynamoHatchIV65536A, LaserDynamoHatchIV262144A,
        LaserDynamoHatchIV1048576A, LaserDynamoHatchIV4194304A, LaserDynamoHatchIV16777216A };

    public static final GTNLItemList[] LASER_DYNAMO_HATCH_LUV = { LaserDynamoHatchLuV256A, LaserDynamoHatchLuV1024A,
        LaserDynamoHatchLuV4096A, LaserDynamoHatchLuV16384A, LaserDynamoHatchLuV65536A, LaserDynamoHatchLuV262144A,
        LaserDynamoHatchLuV1048576A, LaserDynamoHatchLuV4194304A, LaserDynamoHatchLuV16777216A };

    public static final GTNLItemList[] LASER_DYNAMO_HATCH_ZPM = { LaserDynamoHatchZPM256A, LaserDynamoHatchZPM1024A,
        LaserDynamoHatchZPM4096A, LaserDynamoHatchZPM16384A, LaserDynamoHatchZPM65536A, LaserDynamoHatchZPM262144A,
        LaserDynamoHatchZPM1048576A, LaserDynamoHatchZPM4194304A, LaserDynamoHatchZPM16777216A };

    public static final GTNLItemList[] LASER_DYNAMO_HATCH_UV = { LaserDynamoHatchUV256A, LaserDynamoHatchUV1024A,
        LaserDynamoHatchUV4096A, LaserDynamoHatchUV16384A, LaserDynamoHatchUV65536A, LaserDynamoHatchUV262144A,
        LaserDynamoHatchUV1048576A, LaserDynamoHatchUV4194304A, LaserDynamoHatchUV16777216A };

    public static final GTNLItemList[] LASER_DYNAMO_HATCH_UHV = { LaserDynamoHatchUHV256A, LaserDynamoHatchUHV1024A,
        LaserDynamoHatchUHV4096A, LaserDynamoHatchUHV16384A, LaserDynamoHatchUHV65536A, LaserDynamoHatchUHV262144A,
        LaserDynamoHatchUHV1048576A, LaserDynamoHatchUHV4194304A, LaserDynamoHatchUHV16777216A };

    public static final GTNLItemList[] LASER_DYNAMO_HATCH_UEV = { LaserDynamoHatchUEV256A, LaserDynamoHatchUEV1024A,
        LaserDynamoHatchUEV4096A, LaserDynamoHatchUEV16384A, LaserDynamoHatchUEV65536A, LaserDynamoHatchUEV262144A,
        LaserDynamoHatchUEV1048576A, LaserDynamoHatchUEV4194304A, LaserDynamoHatchUEV16777216A };

    public static final GTNLItemList[] LASER_DYNAMO_HATCH_UIV = { LaserDynamoHatchUIV256A, LaserDynamoHatchUIV1024A,
        LaserDynamoHatchUIV4096A, LaserDynamoHatchUIV16384A, LaserDynamoHatchUIV65536A, LaserDynamoHatchUIV262144A,
        LaserDynamoHatchUIV1048576A, LaserDynamoHatchUIV4194304A, LaserDynamoHatchUIV16777216A };

    public static final GTNLItemList[] LASER_DYNAMO_HATCH_UMV = { LaserDynamoHatchUMV256A, LaserDynamoHatchUMV1024A,
        LaserDynamoHatchUMV4096A, LaserDynamoHatchUMV16384A, LaserDynamoHatchUMV65536A, LaserDynamoHatchUMV262144A,
        LaserDynamoHatchUMV1048576A, LaserDynamoHatchUMV4194304A, LaserDynamoHatchUMV16777216A };

    public static final GTNLItemList[] LASER_DYNAMO_HATCH_UXV = { LaserDynamoHatchUXV256A, LaserDynamoHatchUXV1024A,
        LaserDynamoHatchUXV4096A, LaserDynamoHatchUXV16384A, LaserDynamoHatchUXV65536A, LaserDynamoHatchUXV262144A,
        LaserDynamoHatchUXV1048576A, LaserDynamoHatchUXV4194304A, LaserDynamoHatchUXV16777216A };

    public static final GTNLItemList[] LASER_DYNAMO_HATCH_MAX = { LaserDynamoHatchMAX256A, LaserDynamoHatchMAX1024A,
        LaserDynamoHatchMAX4096A, LaserDynamoHatchMAX16384A, LaserDynamoHatchMAX65536A, LaserDynamoHatchMAX262144A,
        LaserDynamoHatchMAX1048576A, LaserDynamoHatchMAX4194304A, LaserDynamoHatchMAX16777216A };

    public static final GTNLItemList[][] LASER_DYNAMO_HATCH = { LASER_DYNAMO_HATCH_IV, LASER_DYNAMO_HATCH_LUV,
        LASER_DYNAMO_HATCH_ZPM, LASER_DYNAMO_HATCH_UV, LASER_DYNAMO_HATCH_UHV, LASER_DYNAMO_HATCH_UEV,
        LASER_DYNAMO_HATCH_UIV, LASER_DYNAMO_HATCH_UMV, LASER_DYNAMO_HATCH_UXV, LASER_DYNAMO_HATCH_MAX };

    public static final GTNLItemList[] WIRELESS_ENERGY_COVER = new GTNLItemList[] { LVWirelessEnergyCover,
        MVWirelessEnergyCover, HVWirelessEnergyCover, EVWirelessEnergyCover, IVWirelessEnergyCover,
        LuVWirelessEnergyCover, ZPMWirelessEnergyCover, UVWirelessEnergyCover, UHVWirelessEnergyCover,
        UEVWirelessEnergyCover, UIVWirelessEnergyCover, UMVWirelessEnergyCover, UXVWirelessEnergyCover,
        MAXWirelessEnergyCover };

    public static final GTNLItemList[] WIRELESS_ENERGY_COVER_4A = new GTNLItemList[] { LVWirelessEnergyCover4A,
        MVWirelessEnergyCover4A, HVWirelessEnergyCover4A, EVWirelessEnergyCover4A, IVWirelessEnergyCover4A,
        LuVWirelessEnergyCover4A, ZPMWirelessEnergyCover4A, UVWirelessEnergyCover4A, UHVWirelessEnergyCover4A,
        UEVWirelessEnergyCover4A, UIVWirelessEnergyCover4A, UMVWirelessEnergyCover4A, UXVWirelessEnergyCover4A,
        MAXWirelessEnergyCover4A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_LV = { WirelessEnergyHatchLV, WirelessEnergyHatchLV4A,
        WirelessEnergyHatchLV16A, WirelessEnergyHatchLV64A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_MV = { WirelessEnergyHatchMV, WirelessEnergyHatchMV4A,
        WirelessEnergyHatchMV16A, WirelessEnergyHatchMV64A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_HV = { WirelessEnergyHatchHV, WirelessEnergyHatchHV4A,
        WirelessEnergyHatchHV16A, WirelessEnergyHatchHV64A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_EV = { WirelessEnergyHatchEV, WirelessEnergyHatchEV4A,
        WirelessEnergyHatchEV16A, WirelessEnergyHatchEV64A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_IV = { WirelessEnergyHatchIV, WirelessEnergyHatchIV4A,
        WirelessEnergyHatchIV16A, WirelessEnergyHatchIV64A, WirelessEnergyHatchIV256A, WirelessEnergyHatchIV1024A,
        WirelessEnergyHatchIV4096A, WirelessEnergyHatchIV16384A, WirelessEnergyHatchIV65536A,
        WirelessEnergyHatchIV262144A, WirelessEnergyHatchIV1048576A, WirelessEnergyHatchIV4194304A,
        WirelessEnergyHatchIV16777216A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_LUV = { WirelessEnergyHatchLuV, WirelessEnergyHatchLuV4A,
        WirelessEnergyHatchLuV16A, WirelessEnergyHatchLuV64A, WirelessEnergyHatchLuV256A, WirelessEnergyHatchLuV1024A,
        WirelessEnergyHatchLuV4096A, WirelessEnergyHatchLuV16384A, WirelessEnergyHatchLuV65536A,
        WirelessEnergyHatchLuV262144A, WirelessEnergyHatchLuV1048576A, WirelessEnergyHatchLuV4194304A,
        WirelessEnergyHatchLuV16777216A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_ZPM = { WirelessEnergyHatchZPM, WirelessEnergyHatchZPM4A,
        WirelessEnergyHatchZPM16A, WirelessEnergyHatchZPM64A, WirelessEnergyHatchZPM256A, WirelessEnergyHatchZPM1024A,
        WirelessEnergyHatchZPM4096A, WirelessEnergyHatchZPM16384A, WirelessEnergyHatchZPM65536A,
        WirelessEnergyHatchZPM262144A, WirelessEnergyHatchZPM1048576A, WirelessEnergyHatchZPM4194304A,
        WirelessEnergyHatchZPM16777216A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_UV = { WirelessEnergyHatchUV, WirelessEnergyHatchUV4A,
        WirelessEnergyHatchUV16A, WirelessEnergyHatchUV64A, WirelessEnergyHatchUV256A, WirelessEnergyHatchUV1024A,
        WirelessEnergyHatchUV4096A, WirelessEnergyHatchUV16384A, WirelessEnergyHatchUV65536A,
        WirelessEnergyHatchUV262144A, WirelessEnergyHatchUV1048576A, WirelessEnergyHatchUV4194304A,
        WirelessEnergyHatchUV16777216A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_UHV = { WirelessEnergyHatchUHV, WirelessEnergyHatchUHV4A,
        WirelessEnergyHatchUHV16A, WirelessEnergyHatchUHV64A, WirelessEnergyHatchUHV256A, WirelessEnergyHatchUHV1024A,
        WirelessEnergyHatchUHV4096A, WirelessEnergyHatchUHV16384A, WirelessEnergyHatchUHV65536A,
        WirelessEnergyHatchUHV262144A, WirelessEnergyHatchUHV1048576A, WirelessEnergyHatchUHV4194304A,
        WirelessEnergyHatchUHV16777216A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_UEV = { WirelessEnergyHatchUEV, WirelessEnergyHatchUEV4A,
        WirelessEnergyHatchUEV16A, WirelessEnergyHatchUEV64A, WirelessEnergyHatchUEV256A, WirelessEnergyHatchUEV1024A,
        WirelessEnergyHatchUEV4096A, WirelessEnergyHatchUEV16384A, WirelessEnergyHatchUEV65536A,
        WirelessEnergyHatchUEV262144A, WirelessEnergyHatchUEV1048576A, WirelessEnergyHatchUEV4194304A,
        WirelessEnergyHatchUEV16777216A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_UIV = { WirelessEnergyHatchUIV, WirelessEnergyHatchUIV4A,
        WirelessEnergyHatchUIV16A, WirelessEnergyHatchUIV64A, WirelessEnergyHatchUIV256A, WirelessEnergyHatchUIV1024A,
        WirelessEnergyHatchUIV4096A, WirelessEnergyHatchUIV16384A, WirelessEnergyHatchUIV65536A,
        WirelessEnergyHatchUIV262144A, WirelessEnergyHatchUIV1048576A, WirelessEnergyHatchUIV4194304A,
        WirelessEnergyHatchUIV16777216A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_UMV = { WirelessEnergyHatchUMV, WirelessEnergyHatchUMV4A,
        WirelessEnergyHatchUMV16A, WirelessEnergyHatchUMV64A, WirelessEnergyHatchUMV256A, WirelessEnergyHatchUMV1024A,
        WirelessEnergyHatchUMV4096A, WirelessEnergyHatchUMV16384A, WirelessEnergyHatchUMV65536A,
        WirelessEnergyHatchUMV262144A, WirelessEnergyHatchUMV1048576A, WirelessEnergyHatchUMV4194304A,
        WirelessEnergyHatchUMV16777216A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_UXV = { WirelessEnergyHatchUXV, WirelessEnergyHatchUXV4A,
        WirelessEnergyHatchUXV16A, WirelessEnergyHatchUXV64A, WirelessEnergyHatchUXV256A, WirelessEnergyHatchUXV1024A,
        WirelessEnergyHatchUXV4096A, WirelessEnergyHatchUXV16384A, WirelessEnergyHatchUXV65536A,
        WirelessEnergyHatchUXV262144A, WirelessEnergyHatchUXV1048576A, WirelessEnergyHatchUXV4194304A,
        WirelessEnergyHatchUXV16777216A };

    public static final GTNLItemList[] WIRELESS_ENERGY_HATCH_MAX = { WirelessEnergyHatchMAX, WirelessEnergyHatchMAX4A,
        WirelessEnergyHatchMAX16A, WirelessEnergyHatchMAX64A, WirelessEnergyHatchMAX256A, WirelessEnergyHatchMAX1024A,
        WirelessEnergyHatchMAX4096A, WirelessEnergyHatchMAX16384A, WirelessEnergyHatchMAX65536A,
        WirelessEnergyHatchMAX262144A, WirelessEnergyHatchMAX1048576A, WirelessEnergyHatchMAX4194304A,
        WirelessEnergyHatchMAX16777216A };

    public static final GTNLItemList[][] WIRELESS_ENERGY_HATCHES = { WIRELESS_ENERGY_HATCH_LV, WIRELESS_ENERGY_HATCH_MV,
        WIRELESS_ENERGY_HATCH_HV, WIRELESS_ENERGY_HATCH_EV, WIRELESS_ENERGY_HATCH_IV, WIRELESS_ENERGY_HATCH_LUV,
        WIRELESS_ENERGY_HATCH_ZPM, WIRELESS_ENERGY_HATCH_UV, WIRELESS_ENERGY_HATCH_UHV, WIRELESS_ENERGY_HATCH_UEV,
        WIRELESS_ENERGY_HATCH_UIV, WIRELESS_ENERGY_HATCH_UMV, WIRELESS_ENERGY_HATCH_UXV, WIRELESS_ENERGY_HATCH_MAX };

    public boolean mHasNotBeenSet;
    public boolean mDeprecated;
    public boolean mWarned;

    public ItemStack mStack;

    // endregion

    GTNLItemList() {
        mHasNotBeenSet = true;
    }

    GTNLItemList(boolean aDeprecated) {
        if (aDeprecated) {
            mDeprecated = true;
            mHasNotBeenSet = true;
        }
    }

    public Item getItem() {
        sanityCheck();
        if (Utils.isStackInvalid(mStack)) return null;
        return mStack.getItem();
    }

    public Block getBlock() {
        sanityCheck();
        return Block.getBlockFromItem(getItem());
    }

    @Override
    public ItemStack get(long aAmount, Object... aReplacements) {
        sanityCheck();
        // if invalid, return a replacements
        if (Utils.isStackInvalid(mStack)) {
            ScienceNotLeisure.LOG.warn("Object in the GTNLItemList is null at:", new NullPointerException());
            return Utils.copyAmount(Math.toIntExact(aAmount), TestMetaBlock01_0.get(1));
        }
        return Utils.copyAmount(Math.toIntExact(aAmount), mStack);
    }

    public ItemStack getWithMeta(long aAmount, int meta, Object... aReplacements) {
        sanityCheck();
        // if invalid, return a replacements
        if (Utils.isStackInvalid(mStack)) {
            ScienceNotLeisure.LOG.warn("Object in the GTNLItemList is null at:", new NullPointerException());
            ItemStack fallback = TestMetaBlock01_0.get(1);
            fallback.setItemDamage(meta);
            return Utils.copyAmount(Math.toIntExact(aAmount), fallback);
        }

        ItemStack stack = Utils.copyAmount(Math.toIntExact(aAmount), mStack);
        stack.setItemDamage(meta);
        return stack;
    }

    public int getWithMeta() {
        return mStack.getItemDamage();
    }

    public GTNLItemList set(Item aItem) {
        if (aItem == null) return this;
        return set(Utils.newItemStack(aItem));
    }

    public GTNLItemList set(ItemStack aStack) {
        if (aStack == null) return this;
        mHasNotBeenSet = false;
        mStack = Utils.copyAmount(1, aStack);
        if (Utils.isClientSide()) {
            Item item = mStack.getItem();
            if (item == null) return this;
            if (Block.getBlockFromItem(item) == GregTechAPI.sBlockMachines) {
                GTNLCreativeTabs.addToMachineList(mStack.copy());
            }
        }
        return this;
    }

    public GTNLItemList set(IMetaTileEntity metaTileEntity) {
        if (metaTileEntity == null) throw new IllegalArgumentException();
        return set(metaTileEntity.getStackForm(1L));
    }

    public boolean hasBeenSet() {
        return !mHasNotBeenSet;
    }

    /**
     * Returns the internal stack. This method is unsafe. It's here only for quick operations. DON'T CHANGE THE RETURNED
     * VALUE!
     */
    public ItemStack getInternalStack_unsafe() {
        return mStack;
    }

    public void sanityCheck() {
        if (mHasNotBeenSet)
            throw new IllegalAccessError("The Enum '" + name() + "' has not been set to an Item at this time!");
        if (mDeprecated && !mWarned) {
            ScienceNotLeisure.LOG.error("{} is now deprecated", this, new Exception());
            // warn only once
            mWarned = true;
        }
    }

    @Override
    public boolean isStackEqual(Object aStack) {
        return isStackEqual(aStack, false, false);
    }

    @Override
    public boolean isStackEqual(Object aStack, boolean aWildcard, boolean aIgnoreNBT) {
        if (mDeprecated && !mWarned) {
            ScienceNotLeisure.LOG.error("{} is now deprecated", this, new Exception());
            // warn only once
            mWarned = true;
        }
        if (GTUtility.isStackInvalid(aStack)) return false;
        return GTUtility.areUnificationsEqual((ItemStack) aStack, aWildcard ? getWildcard(1) : get(1), aIgnoreNBT);
    }

    @Override
    public ItemStack getWildcard(long aAmount, Object... aReplacements) {
        sanityCheck();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, GTRecipeBuilder.WILDCARD, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getUndamaged(long aAmount, Object... aReplacements) {
        sanityCheck();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, 0, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getAlmostBroken(long aAmount, Object... aReplacements) {
        sanityCheck();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, mStack.getMaxDamage() - 1, GTOreDictUnificator.get(mStack));
    }

    @Override
    public ItemStack getWithName(long aAmount, String aDisplayName, Object... aReplacements) {
        ItemStack rStack = get(1, aReplacements);
        if (GTUtility.isStackInvalid(rStack)) return NI;

        // CamelCase alphanumeric words from aDisplayName
        StringBuilder tCamelCasedDisplayNameBuilder = new StringBuilder();
        final String[] tDisplayNameWords = aDisplayName.split("\\W");
        for (String tWord : tDisplayNameWords) {
            if (!tWord.isEmpty()) tCamelCasedDisplayNameBuilder.append(
                tWord.substring(0, 1)
                    .toUpperCase(Locale.US));
            if (tWord.length() > 1) tCamelCasedDisplayNameBuilder.append(
                tWord.substring(1)
                    .toLowerCase(Locale.US));
        }
        if (tCamelCasedDisplayNameBuilder.length() == 0) {
            // CamelCased DisplayName is empty, so use hash of aDisplayName
            tCamelCasedDisplayNameBuilder.append(((Long) (long) aDisplayName.hashCode()));
        }

        // Construct a translation key from UnlocalizedName and CamelCased DisplayName
        final String tKey = rStack.getUnlocalizedName() + ".with." + tCamelCasedDisplayNameBuilder + ".name";

        rStack.setStackDisplayName(GTLanguageManager.addStringLocalization(tKey, aDisplayName));
        return GTUtility.copyAmount(aAmount, rStack);
    }

    @Override
    public ItemStack getWithCharge(long aAmount, int aEnergy, Object... aReplacements) {
        ItemStack rStack = get(1, aReplacements);
        if (GTUtility.isStackInvalid(rStack)) return null;
        GTModHandler.chargeElectricItem(rStack, aEnergy, Integer.MAX_VALUE, true, false);
        return GTUtility.copyAmount(aAmount, rStack);
    }

    @Override
    public ItemStack getWithDamage(long aAmount, long aMetaValue, Object... aReplacements) {
        sanityCheck();
        if (GTUtility.isStackInvalid(mStack)) return GTUtility.copyAmount(aAmount, aReplacements);
        return GTUtility.copyAmountAndMetaData(aAmount, aMetaValue, GTOreDictUnificator.get(mStack));
    }

    @Override
    public IItemContainer registerOre(Object... aOreNames) {
        sanityCheck();
        for (Object tOreName : aOreNames) GTOreDictUnificator.registerOre(tOreName, get(1));
        return this;
    }

    @Override
    public IItemContainer registerWildcardAsOre(Object... aOreNames) {
        sanityCheck();
        for (Object tOreName : aOreNames) GTOreDictUnificator.registerOre(tOreName, getWildcard(1));
        return this;
    }

}
