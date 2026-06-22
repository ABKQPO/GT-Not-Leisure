package com.science.gtnl.mixins;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;
import com.science.gtnl.asm.GTNLEarlyCoreMod;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.enums.ModList;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Mixins implements IMixins {

    FORGE_CORE(Side.COMMON, "Forge.MixinFMLProxyPacket", "Forge.MixinForgeHook"),

    GREGTECH_EARLY(Side.COMMON, "Gregtech.AccessorMTETieredMachineBlock", "Gregtech.AccessorEyeOfHarmonyRecipe",
        "Gregtech.AccessorGTRecipe", "Gregtech.AccessorGTRecipeBuilder", "Gregtech.AccessorGTRecipeWithAlt",
        "Gregtech.AccessorGTLanguageManager", "Gregtech.AccessorCommonMetaTileEntity",
        "Gregtech.AccessorMetaTileEntity", "Gregtech.AccessorMTEHatch", "Gregtech.AccessorProcessingLogic",
        "Gregtech.AccessorRecipeDisplayInfo", "Gregtech.MixinMTEBasicMachine", "Gregtech.MixinBaseMetaTileEntity",
        "Gregtech.AssLineRemover.MixinGTMod", "Gregtech.AssLineRemover.MixinGTRecipeBuilder",
        "Gregtech.AssLineRemover.MixinTTRecipeAdder"),

    GREGTECH_CLIENT_EARLY(
        new MixinBuilder("Gregtech early client safety mixins").addClientMixins("Gregtech.MixinGTLanguageManager")
            .setPhase(Phase.EARLY)),

    NO_NHU_EARLY(
        new MixinBuilder("Early Mixins when NHUtilities is absent").addCommonMixins("NoNHU.MixinBaseMetaTileEntity")
            .setPhase(Phase.EARLY)
            .addExcludedMod(ModList.NHUtilities)),

    NH_CORE_MOD_EARLY(new MixinBuilder().addCommonMixins("NHCoreMod.AccessorBacteriaRegistry")
        .setPhase(Phase.EARLY)
        .addRequiredMod(ModList.NewHorizonsCoreMod)),

    MINECRAFT_COMMON(Side.COMMON, "Minecraft.AccessorStringTranslate", "Minecraft.AccessorContainerRepair",
        "Minecraft.AccessorEntityLivingBase", "Minecraft.AccessorTessellator", "Minecraft.AccessorFoodStats",
        "Minecraft.AccessorMinecraft", "Minecraft.MixinCommandTeleport", "Minecraft.MixinEntity",
        "Minecraft.MixinEntityItem", "Minecraft.MixinEntityLivingBase", "Minecraft.MixinEntityLiving",
        "Minecraft.MixinEntityPlayer", "Minecraft.MixinExplosion", "Minecraft.MixinInventoryCrafting",
        "Minecraft.MixinItemStack", "Minecraft.MixinMinecraftServer", "Minecraft.MixinNBTTagList",
        "Minecraft.MixinPotionEffect", "Minecraft.MixinServerConfigurationManager", "Minecraft.MixinWorld",
        "Minecraft.MixinWorldServer"),

    APRIL_FOOL(new MixinBuilder("April Fool Late Mixins")
        .addSidedMixins(Side.CLIENT, "AprilFool.MixinBaseMetaTileEntityRenderer", "AprilFool.MixinCommonMetaTileEntity")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> GTNLEarlyCoreMod.enableAprilFool)),

    MINECRAFT_KILL_ENHANCE(
        new MixinBuilder("Mixins for Kill Command Enhance").addCommonMixins("Minecraft.MixinCommandKill")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> MainConfig.minecraft.enableKillEnhance)),

    MINECRAFT_CLIENT(Side.CLIENT, "Minecraft.AccessorEntityRenderer", "Minecraft.AccessorGuiChat",
        "Minecraft.MixinMinecraft", "Minecraft.MixinWorldClient", "Minecraft.MixinSimpleReloadableResourceManager",
        "Minecraft.MixinGuiContainer", "Minecraft.MixinGuiFlatPresets"),

    SUPER_CREEPER(new MixinBuilder("Mixins for Super Creeper logic")
        .addCommonMixins(
            "SuperCreeper.AccessorEntityCreeper",
            "SuperCreeper.MixinEntityAICreeperSwell",
            "SuperCreeper.MixinEntityCreeper",
            "SuperCreeper.MixinEntitySpider")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> MainConfig.super_creeper.enableSuperCreeper)),

    NEI_CHEAT_ICON(new MixinBuilder("Mixins for NEI Special Cheat Icon")
        .addCommonMixins(
            "NotEnoughItems.AccessorDrawableBuilder",
            "NotEnoughItems.AccessorDrawableResource",
            "NotEnoughItems.MixinDrawableResource",
            "NotEnoughItems.MixinLayoutManager",
            "NotEnoughItems.MixinLayoutStyleMinecraft")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> MainConfig.other.not_enough_items.enableSpecialCheatIcon)),

    STICK_RENDER(new MixinBuilder("Mixins for Stick item rendering")
        .addClientMixins(
            "Stick.MixinAnimatedTooltipHandler",
            "Stick.MixinItemRenderer",
            "Stick.MixinLayoutManager",
            "Stick.MixinRenderItem")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> MainConfig.item.stick.enableStickItem)),

    LATE_COMMON(new MixinBuilder("General Late Mixins")
        .addCommonMixins(
            "AEFluidCraft.MixinItemFluidPacket",
            "AppliedEnergistics.AccessorAEBaseItemBlock",
            "AppliedEnergistics.AccessorContainerUpgradeable",
            "AppliedEnergistics.AccessorPartInterface",
            "AppliedEnergistics.AccessorTileInterface",
            "AppliedEnergistics.MixinAdaptorIInventory",
            "AppliedEnergistics.MixinDualityInterface",
            "AppliedEnergistics.MixinInterfaceTerminalEntry",
            "AppliedEnergistics.MixinEntityTinyTNTPrimed",
            "AppliedEnergistics.MixinTileIOPort",
            "AppliedEnergistics.assembler.AccessorContainerPatternTerm",
            "AppliedEnergistics.assembler.AccessorInvTracker",
            "AppliedEnergistics.assembler.MixinContainerInterfaceTerminal",
            "AppliedEnergistics.MixinCraftingCPUCluster",
            "AppliedEnergistics.AccessorTaskProgress",
            "AppliedEnergistics.QuamtumComputer.MixinCraftingCPUCluster",
            "AppliedEnergistics.QuamtumComputer.MixinCraftingGridCache",
            "Bartwork.MixinItemRegistry",
            "Bartwork.MixinMultipleMetalLoader",
            "Bartwork.MixinSimpleMetalLoader",
            "Bartwork.MixinWerkstoff",
            "Bartwork.MixinWerkstoffLoader",
            "Bartwork.MixinMoltenCellLoader",
            "Botania.AccessorEntityDoppleganger",
            "DraconicEvolution.AccessorCustomArmorHandler",
            "DraconicEvolution.MixinCustomArmorHandler",
            "DraconicEvolution.MixinReactorExplosion",
            "Gregtech.MixinEyeOfHarmonyRecipeStorage",
            "Gregtech.MixinGodForgeMath",
            "Gregtech.MixinGTOreDictUnificator",
            "Gregtech.MixinGTRecipeConstants",
            "Gregtech.MixinGTPPRecipeMaps",
            "Gregtech.MixinGTShapedRecipe",
            "Gregtech.MixinGTShapelessRecipe",
            "Gregtech.MixinGTUtility",
            "Gregtech.MixinGTUtil",
            "Gregtech.MixinAssemblyLineUtils",
            "Gregtech.MixinMTEBetterJukebox",
            "Gregtech.MixinMTEForgeOfGods",
            "Gregtech.MixinMTEHatch",
            "Gregtech.MixinMTEHatchOutputBusME",
            "Gregtech.MixinMTEHatchOutputME",
            "Gregtech.MixinMTEHatchAirIntake",
            "Gregtech.MixinMTEHatchCraftingInputME",
            "Gregtech.MixinMTEHatchCraftingInputSlave",
            "Gregtech.MixinMTEHatchOutputBeamline",
            "Gregtech.MixinMTEPurificationUnitBaseGui",
            "Gregtech.MixinMTEDigitalTankBase",
            "Gregtech.MixinMTEHatchDataAccess",
            "Gregtech.MixinMTEHatchSteamBusOutput",
            "Gregtech.MixinMTELightningRod",
            "Gregtech.MixinMTEPlasmaForge",
            "Gregtech.MixinMTEMultiBlockBase",
            "Gregtech.MixinMTEMultiBlockBaseRecipeProcessing",
            "Gregtech.MixinMTETreeFarm",
            "Gregtech.MixinMTEVoidMinerBase",
            "Gregtech.MixinMTEVoidMiners",
            "Gregtech.MixinProcessingLogic",
            "Gregtech.MixinRecipeGenFluids",
            "Gregtech.MixinProcessingDust",
            "Gregtech.MixinTTMultiblockBase",
            "energymonitor.AccessorMTELapotronicSuperCapacitorEnergyMonitor",
            "energymonitor.MixinBaseMetaTileEntityEnergyMonitor",
            "energymonitor.MixinCommonMetaTileEntityEnergyMonitor",
            "energymonitormodule.AccessorETGWEyeOfHarmonyModuleEnergyMonitor",
            "energymonitormodule.AccessorEternalGregTechWorkshopModuleEnergyMonitor",
            "energymonitormodule.AccessorFOGAlloyBlastSmelterModuleEnergyMonitor",
            "energymonitormodule.AccessorFOGAlloySmelterModuleEnergyMonitor",
            "energymonitormodule.AccessorFOGExtractorModuleEnergyMonitor",
            "energymonitormodule.AccessorFOGSolarMuonCatalystModuleEnergyMonitor",
            "energymonitorwirelesscustom.MixinElementCopyingEnergyMonitor",
            "energymonitorwirelesscustom.MixinReactionFurnaceEnergyMonitor",
            "InventoryBogoSorter.MixinShortcutHandler",
            "InventoryBogoSorter.MixinSortHandler",
            "ModularUI.MixinFluidSlotWidget",
            "TConstruct.MixinSmelteryLogic",
            "TecTech.MixinMTEEyeOfHarmony",
            "TecTech.MixinMTEResearchStation",
            "TecTech.MixinTTRecipeAdder",
            "ThaumicTinkerer.AccessorAspectCropLootManager",
            "ThaumicTinkerer.MixinItemBloodSword",
            "VisualProspecting.AccessorVeinTypeCaching")
        .addClientMixins(
            "AppliedEnergistics.assembler.MixinGuiMEMonitorable",
            "AppliedEnergistics.assembler.MixinGuiPatternTerm")
        .setPhase(Phase.LATE)),

    GREGTECH_MAIN_FACING(new MixinBuilder().addCommonMixins("Gregtech.MixinMTEBasicMachineFacing")
        .setPhase(Phase.LATE)
        .setApplyIf(() -> MainConfig.machine.allowMainFacingInteraction)),

    GALAXY_SPACE(new MixinBuilder("Galaxy Space Mixin").addCommonMixins("GalaxySpace.MixinRocketRecipeHandler")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.GalaxySpace)),

    NH_CORE_MOD(new MixinBuilder("NH Core Mod Mixin").addCommonMixins("NHCoreMod.MixinBacteriaRegistry")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.NewHorizonsCoreMod)),

    ENHANCED_LOOT_BAGS(
        new MixinBuilder("Enhanced Loot Bags Mixin").addCommonMixins("EnhancedLootBags.AccessorItemLootBag")
            .setPhase(Phase.LATE)
            .addRequiredMod(ModList.EnhancedLootBags)),

    NOT_ENOUGH_ENERGISTICS(new MixinBuilder("Not Enough Energistics Mixin")
        .addCommonMixins("NotEnoughEnergistics.MixinNEEPatternTerminalHandler")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.NotEnoughEnergistics)),

    NOT_ENOUGH_ITEMS(new MixinBuilder("Not Enough Items stability mixins")
        .addClientMixins("NotEnoughItems.AccessorItemList", "NotEnoughItems.MixinItemListUpdateFilter")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.NotEnoughItems)),

    NEI_CUSTOM_DIAGRAM(new MixinBuilder("NEI Custom Diagram Mixin")
        .addCommonMixins("NEICustomDiagram.AccessorNeiCustomDiagram", "NEICustomDiagram.MixinNeiCustomDiagram")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.NEICustomDiagrams)),

    NEI_AVARITIA_ADDON(
        new MixinBuilder("NEI Avaritia Addon Mixin").addCommonMixins("NotEnoughItems.MixinGuiContainerManager")
            .setPhase(Phase.LATE)
            .addRequiredMod(ModList.AvaritiaAddons)),

    AE_RANDOM_COMPLEMENT(new MixinBuilder("AE2 Random Complement Mixins")
        .addCommonMixins(
            "RandomComplement.MixinAEBaseContainer",
            "RandomComplement.MixinBlockCraftingUnit",
            "RandomComplement.MixinContainerCraftAmount",
            "RandomComplement.MixinContainerCraftConfirm",
            "RandomComplement.MixinContainerMEMonitorable",
            "RandomComplement.MixinGuiBridge",
            "RandomComplement.MixinQuantumCluster",
            "RandomComplement.MixinTileQuantumBridge",
            "RandomComplement.MixinWirelessTerminalGuiObject")
        .addClientMixins(
            "RandomComplement.MixinGuiAmount",
            "RandomComplement.MixinGuiCraftAmount",
            "RandomComplement.MixinGuiCraftConfirm")
        .setPhase(Phase.LATE)),

    OVERPOWERED_CHANGE(new MixinBuilder("Overpowered Mixin Changes")
        .addCommonMixins(
            "Overpowered.MixinBehaviourScanner",
            "Overpowered.MixinGregtechWailaDataProvider",
            "Overpowered.MixinParallelHelper")
        .setPhase(Phase.LATE)
        .setApplyIf(() -> MainConfig.machine.enableRecipeOutputChance)
        .addExcludedMod(ModList.Overpowered)),

    OVERPOWERED_TST_CHANGE(
        new MixinBuilder("Overpowered TST Mixin Changes").addCommonMixins("Overpowered.MixinGTCM_ParallelHelper")
            .setPhase(Phase.LATE)
            .setApplyIf(() -> MainConfig.machine.enableRecipeOutputChance)
            .addRequiredMod(ModList.TwistSpaceTechnology)
            .addExcludedMod(ModList.Overpowered)),

    NO_NHU(new MixinBuilder("Mixins when NHUtilities is absent")
        .addCommonMixins(
            "NoNHU.MixinAbstractPoweredMachineEntity",
            "NoNHU.MixinBlockItemCapBank",
            "NoNHU.MixinMTEAdvAssLineAcceleration",
            "NoNHU.MixinResearchStationAcceleration",
            "NoNHU.MixinTileEntityEnder")
        .setPhase(Phase.LATE)
        .addExcludedMod(ModList.NHUtilities)),

    TST(new MixinBuilder("Twist Space Technology Mixins")
        .addCommonMixins(
            "TwistSpaceTechnology.MixinRecipeLoader",
            "TwistSpaceTechnology.MixinTST_OreProcessingFactory",
            "TwistSpaceTechnology.MixinMM_DimensionallyTranscendentMatterPlasmaForgePrototypeMK2")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.TwistSpaceTechnology)),

    PP_CHANGE(new MixinBuilder("Purification Plant Changes")
        .addCommonMixins(
            "Gregtech.MixinMTEPurificationPlant",
            "Gregtech.MixinMTEPurificationUnitBaryonicPerfection",
            "Gregtech.MixinMTEPurificationUnitBase",
            "Gregtech.MixinMTEPurificationUnitOzonation",
            "Gregtech.MixinMTEPurificationUnitPhAdjustment",
            "Gregtech.MixinMTEPurificationUnitPlasmaHeater",
            "Gregtech.MixinMTEPurificationUnitUVTreatment",
            "Gregtech.MixinMTEPurificationUnitDegasser")
        .setPhase(Phase.LATE)
        .setApplyIf(() -> MainConfig.machine.enablePurificationPlantBuff)),

    STICK_LATE(new MixinBuilder("Stick Late Mixins")
        .addCommonMixins("Stick.MixinTooltipHandlerWaila", "Stick.MixinOreDictTooltipHandler")
        .setPhase(Phase.LATE)
        .setApplyIf(() -> MainConfig.item.stick.enableStickItem)),

    ET_FUTURUM_ELYTRA(
        new MixinBuilder("InfinityElytra EtFuturum Mixin").addCommonMixins("EtFuturum.MixinStartElytraFlyingHandler")
            .addClientMixins("EtFuturum.MixinLayerBetterElytra")
            .setPhase(Phase.LATE)
            .addRequiredMod(ModList.EtFuturumRequiem)),;

    private final MixinBuilder builder;

    Mixins(Side side, String... mixins) {
        this.builder = new MixinBuilder().addSidedMixins(side, mixins)
            .setPhase(Phase.EARLY);
    }
}
