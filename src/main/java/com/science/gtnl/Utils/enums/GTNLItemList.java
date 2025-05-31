package com.science.gtnl.Utils.enums;

import static gregtech.api.enums.GTValues.NI;
import static gregtech.api.enums.GTValues.W;

import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.science.gtnl.Utils.Utils;
import com.science.gtnl.client.GTNLCreativeTabs;

import gregtech.api.GregTechAPI;
import gregtech.api.interfaces.IItemContainer;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

@SuppressWarnings("unused")
public enum GTNLItemList implements IItemContainer {

    StargateCoil,
    StargateTier0,
    StargateTier1,
    StargateTier2,
    StargateTier3,
    StargateTier4,
    StargateTier5,
    StargateTier6,
    StargateTier7,
    StargateTier8,
    StargateTier9,
    Stargate_Coil_Compressed,
    LaserBeacon,
    ArtificialStarRender,
    NanoPhagocytosisPlantRender,
    EternalGregTechWorkshopRender,
    PlayerDoll,

    BronzeBrickCasing,
    SteelBrickCasing,
    CrushingWheels,
    SolarBoilingCell,

    TestMetaBlock01_0,
    NewHorizonsCoil,
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

    TestItem,
    KFCFamily,
    SatietyRing,
    SuperReachRing,
    RejectionRing,
    RecordSus,
    RecordNewHorizons,
    FakeItemSiren,
    PhysicsCape,
    TimeStopPocketWatch,
    MilledNaquadahEnriched,

    CircuitIntegratedPlus,

    InfinityFuelRod,
    InfinityFuelRodDepleted,

    SaplingBrickuoia,

    TwilightSword,

    GaiaGlass,
    TerraGlass,
    FusionGlass,
    ConcentratingSieveMesh,

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
    AdvancedCircuitAssemblyLine,
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
    QuadrupleOutputHatchEV,
    NinefoldOutputHatchEV,
    HumongousInputBusULV,
    HumongousInputBusLV,
    HumongousInputBusMV,
    HumongousInputBusHV,
    HumongousInputBusEV,
    HumongousInputBusIV,
    HumongousInputBusLuV,
    HumongousInputBusZPM,
    HumongousInputBusUV,
    HumongousInputBusUHV,
    HumongousInputBusUEV,
    HumongousInputBusUIV,
    HumongousInputBusUMV,
    HumongousInputBusUXV,
    HumongousInputBusMAX,
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
            GTLog.out.println("Object in the ReAvaItemList is null at:");
            new NullPointerException().printStackTrace(GTLog.out);
            return Utils.copyAmount(Math.toIntExact(aAmount), TestMetaBlock01_0.get(1));
        }
        return Utils.copyAmount(Math.toIntExact(aAmount), mStack);
    }

    public int getMeta() {
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
            new Exception(this + " is now deprecated").printStackTrace(GTLog.err);
            // warn only once
            mWarned = true;
        }
    }

    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public ItemStack getWithName(int aAmount, String aDisplayName, Object... aReplacements) {
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
    public boolean isStackEqual(Object aStack) {
        return isStackEqual(aStack, false, false);
    }

    @Override
    public boolean isStackEqual(Object aStack, boolean aWildcard, boolean aIgnoreNBT) {
        if (mDeprecated && !mWarned) {
            new Exception(this + " is now deprecated").printStackTrace(GTLog.err);
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
        return GTUtility.copyAmountAndMetaData(aAmount, W, GTOreDictUnificator.get(mStack));
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
