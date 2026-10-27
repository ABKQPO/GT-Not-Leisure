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

    FORGE_CORE(Side.COMMON, "forge.MixinFMLProxyPacket", "forge.MixinForgeHook"),

    GREGTECH_EARLY(Side.COMMON, "gregtech.AccessorMTETieredMachineBlock", "gregtech.AccessorEyeOfHarmonyRecipe",
        "gregtech.AccessorGTRecipe", "gregtech.AccessorGTRecipeBuilder", "gregtech.AccessorGTRecipeWithAlt",
        "gregtech.MixinGTRecipe", "gregtech.AccessorGTLanguageManager", "gregtech.AccessorMTEHatch",
        "gregtech.AccessorProcessingLogic", "gregtech.AccessorRecipeDisplayInfo", "gregtech.MixinBaseMetaTileEntity",
        "gregtech.MixinCommonMetaTileEntity", "gregtech.MixinMetaTileEntity", "gregtech.assLineRemover.MixinGTMod",
        "gregtech.assLineRemover.MixinGTRecipeBuilder", "gregtech.assLineRemover.MixinTTRecipeAdder",
        "energyMonitor.MixinBaseMetaTileEntityEnergyMonitor", "energyMonitor.MixinCommonMetaTileEntityEnergyMonitor"),

    GREGTECH_CLIENT_EARLY(
        new MixinBuilder("Gregtech early client safety mixins").addClientMixins("gregtech.MixinGTLanguageManager")
            .setPhase(Phase.EARLY)),

    NO_NHU_EARLY(
        new MixinBuilder("Early Mixins when NHUtilities is absent").addCommonMixins("noNHU.MixinBaseMetaTileEntity")
            .setPhase(Phase.EARLY)
            .addExcludedMod(ModList.NHUtilities)),

    MINECRAFT_COMMON(Side.COMMON, "minecraft.AccessorStringTranslate", "minecraft.AccessorContainerRepair",
        "minecraft.AccessorEntityLivingBase", "minecraft.AccessorTessellator", "minecraft.AccessorFoodStats",
        "minecraft.AccessorMinecraft", "minecraft.AccessorTileEntity", "minecraft.MixinCommandTeleport",
        "minecraft.MixinEntity", "minecraft.MixinEntityItem", "minecraft.MixinEntityLivingBase",
        "minecraft.MixinEntityLiving", "minecraft.MixinEntityPlayer", "minecraft.MixinExplosion",
        "minecraft.MixinInventoryCrafting", "minecraft.MixinItemStack", "minecraft.MixinMinecraftServer",
        "minecraft.MixinNBTTagList", "minecraft.MixinPotionEffect", "minecraft.MixinServerConfigurationManager",
        "minecraft.MixinWorld", "minecraft.MixinWorldGhostlyShape", "minecraft.MixinWorldServer",
        "minecraft.MixinContainerYogTerminal", "minecraft.MixinItemStackYogTerminalName"),

    APRIL_FOOL(new MixinBuilder("April Fool Late Mixins")
        .addSidedMixins(Side.CLIENT, "aprilFool.MixinBaseMetaTileEntityRenderer", "aprilFool.MixinCommonMetaTileEntity")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> GTNLEarlyCoreMod.enableAprilFool)),

    MINECRAFT_KILL_ENHANCE(
        new MixinBuilder("Mixins for Kill Command Enhance").addCommonMixins("minecraft.MixinCommandKill")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> MainConfig.minecraft.enableKillEnhance)),

    MINECRAFT_CLIENT(Side.CLIENT, "minecraft.AccessorEntityRenderer", "minecraft.AccessorGuiChat",
        "minecraft.MixinMinecraft", "minecraft.MixinModelBiped", "minecraft.MixinMinecraftRailgunClick",
        "minecraft.MixinRenderPlayerFirstPersonArm", "minecraft.MixinWorldClient", "minecraft.MixinItemRendererRailgun",
        // "minecraft.MixinSimpleReloadableResourceManager",
        "minecraft.MixinGuiContainer", "minecraft.MixinGuiFlatPresets"),

    TEXT_EFFECTS_COMMON(Side.CLIENT, "texteffect.MixinEnumChatFormattingTextEffects"),

    TEXT_EFFECTS_CLIENT(Side.CLIENT, "texteffect.MixinFontRendererTextEffects", "texteffect.MixinGuiNewChatTextEffects",
        "texteffect.MixinGuiTextFieldTextEffects"),

    TEXT_EFFECTS_ANGELICA(new MixinBuilder("Optional Angelica text effect batching")
        .addClientMixins("texteffect.angelica.MixinFontBatchTextEffects")
        .setPhase(Phase.EARLY)
        .addRequiredMod(ModList.Angelica)),

    TEXT_EFFECTS_NEI(
        new MixinBuilder("Optional NEI text effect input").addClientMixins("texteffect.MixinNEIFormattedTextField")
            .setPhase(Phase.LATE)
            .addRequiredMod(ModList.NotEnoughItems)),

    SUPER_CREEPER(new MixinBuilder("Mixins for Super Creeper logic")
        .addCommonMixins(
            "superCreeper.AccessorEntityCreeper",
            "superCreeper.MixinEntityAICreeperSwell",
            "superCreeper.MixinEntityCreeper",
            "superCreeper.MixinEntitySpider")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> MainConfig.super_creeper.enableSuperCreeper)),

    NEI_CHEAT_ICON(new MixinBuilder("Mixins for NEI Special Cheat Icon")
        .addCommonMixins(
            "notEnoughItems.AccessorDrawableBuilder",
            "notEnoughItems.AccessorDrawableResource",
            "notEnoughItems.MixinDrawableResource",
            "notEnoughItems.MixinLayoutManager",
            "notEnoughItems.MixinLayoutStyleMinecraft")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> MainConfig.other.not_enough_items.enableSpecialCheatIcon)),

    STICK_RENDER(new MixinBuilder("Mixins for Stick item rendering")
        .addClientMixins(
            "stick.MixinAnimatedTooltipHandler",
            "stick.MixinItemRenderer",
            "stick.MixinLayoutManager",
            "stick.MixinRenderItem")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> MainConfig.item.stick.enableStickItem)),

    WIRELESS_TILE_DATA(new MixinBuilder("ME wireless link persistence")
        .addCommonMixins("appliedEnergistics.wireless.MixinWirelessTileData")
        .setPhase(Phase.EARLY)),

    WIRELESS_CHANNEL(new MixinBuilder("ME wireless channel allocation")
        .addCommonMixins(
            "appliedEnergistics.wireless.MixinWirelessPathingCalculation",
            "appliedEnergistics.wireless.MixinWirelessPathGridCache",
            "appliedEnergistics.wireless.MixinWirelessPartPlacement",
            "appliedEnergistics.wireless.MixinWirelessGridNode",
            "appliedEnergistics.wireless.MixinWirelessVisualiserTool",
            "appliedEnergistics.wireless.AccessorTileController")
        .addClientMixins("appliedEnergistics.wireless.MixinWirelessVisualiserRender")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.AppliedEnergistics)),

    WIRELESS_MATTER_MANIPULATOR(new MixinBuilder("ME wireless links follow Matter Manipulator moves and copies")
        .addCommonMixins(
            "matterManipulator.MixinPendingBlockWireless",
            "matterManipulator.MixinPendingBuildWireless",
            "matterManipulator.MixinPendingMoveWireless")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.AppliedEnergistics)
        .addRequiredMod(ModList.MatterManipulator)),

    ME_MINI_TERMINAL(new MixinBuilder("ME mini portable terminal native terminal adapter")
        .addCommonMixins("appliedEnergistics.meminiterminal.MixinMiniTerminalMonitorableDestination")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.AppliedEnergistics)),

    FLOATING_WINDOW_HOST_TOOLTIP(new MixinBuilder("Floating window host tooltip layering")
        .addClientMixins("minecraft.AccessorGuiScreen", "minecraft.MixinFloatingWindowHostTooltip")
        .setPhase(Phase.EARLY)),

    YOG_SOTHOTH_TERMINAL_BAUBLES(new MixinBuilder("Yog-Sothoth terminal virtual bauble slots")
        .addCommonMixins("baubles.MixinBaublesApiYogTerminal")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.Baubles)),

    YOG_SOTHOTH_TERMINAL_AE(new MixinBuilder("Yog-Sothoth terminal opens stored AE terminals in place")
        .addCommonMixins("appliedEnergistics.MixinPlatformYogTerminal")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.AppliedEnergistics)),

    YOG_SOTHOTH_TERMINAL_WCT(new MixinBuilder("Yog-Sothoth terminal opened wireless crafting terminal lookup")
        .addCommonMixins("wirelessCraftingTerminal.MixinRandomUtilsYogTerminal")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.WirelessCraftingTerminal)),

    FLOATING_WINDOW_MODULAR_UI(new MixinBuilder("Floating window ModularUI hover blocking")
        .addClientMixins("modularUI.MixinFloatingWindowModularGui")
        .setPhase(Phase.LATE)),

    MODULAR_UI_LIVE_PALETTE_THEMES(
        new MixinBuilder("ModularUI 2 screens follow live palette themes").addClientMixins("modularUI2.MixinThemeAPI")
            .setPhase(Phase.LATE)),

    ME_MINI_TERMINAL_BOGO(new MixinBuilder("ME mini portable terminal native Bogo shortcut destinations")
        .addClientMixins("inventoryBogoSorter.MixinMiniTerminalShortcuts")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.InventoryBogoSorter)),

    ME_MINI_TERMINAL_MOUSE_TWEAKS(new MixinBuilder("ME mini portable terminal Mouse Tweaks shortcut destinations")
        .addClientMixins("mouseTweaks.MixinMiniTerminalMouseTweaks")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.MouseTweaks)),

    LATE_COMMON(new MixinBuilder("General Late Mixins")
        .addCommonMixins(
            "aeFluidCraft.MixinItemFluidPacket",
            "appliedEnergistics.AccessorAEBaseItemBlock",
            "appliedEnergistics.AccessorContainerUpgradeable",
            "appliedEnergistics.MixinContainerUpgradeable",
            "appliedEnergistics.AccessorPartInterface",
            "appliedEnergistics.AccessorTileInterface",
            "appliedEnergistics.MixinAdaptorIInventory",
            "appliedEnergistics.MixinDualityInterface",
            "appliedEnergistics.MixinEntityTinyTNTPrimed",
            "appliedEnergistics.MixinTileIOPort",
            "appliedEnergistics.assembler.AccessorContainerPatternTerm",
            "appliedEnergistics.assembler.AccessorInvTracker",
            "appliedEnergistics.assembler.MixinContainerInterfaceTerminal",
            "appliedEnergistics.MixinCraftingCPUCluster",
            "appliedEnergistics.AccessorTaskProgress",
            "appliedEnergistics.MixinSessionCraftCount",
            "appliedEnergistics.quamtumComputer.AccessorOptimizePattern",
            "appliedEnergistics.quamtumComputer.AccessorPacketCraftingTreeData",
            "appliedEnergistics.quamtumComputer.MixinContainerOptimizePatterns",
            "appliedEnergistics.quamtumComputer.MixinCraftingGridCache",
            "appliedEnergistics.quamtumComputer.MixinCraftingCPUStatus",
            "appliedEnergistics.quamtumComputer.MixinCraftingCPUClusterQuantumJob",
            "appliedEnergistics.quamtumComputer.MixinContainerCraftConfirm",
            "bartwork.MixinItemRegistry",
            "bartwork.MixinMultipleMetalLoader",
            "bartwork.MixinSimpleMetalLoader",
            "bartwork.MixinWerkstoff",
            "bartwork.MixinWerkstoffLoader",
            "bartwork.MixinMoltenCellLoader",
            "botania.AccessorEntityDoppleganger",
            "draconicEvolution.AccessorCustomArmorHandler",
            "draconicEvolution.MixinCustomArmorHandler",
            "draconicEvolution.MixinReactorExplosion",
            "gregtech.MixinEyeOfHarmonyRecipeStorage",
            "gregtech.MixinGodForgeMath",
            "gregtech.MixinGTOreDictUnificator",
            "gregtech.MixinGTRecipeConstants",
            "gregtech.MixinGTPPRecipeMaps",
            "gregtech.MixinGTShapedRecipe",
            "gregtech.MixinGTShapelessRecipe",
            "gregtech.MixinGTUtility",
            "gregtech.MixinGTUtil",
            "gregtech.MixinAssemblyLineUtils",
            "gregtech.MixinMTEBetterJukebox",
            "gregtech.MixinMTEForgeOfGods",
            "gregtech.MixinMTEHatch",
            "gregtech.MixinMTEHatchCraftingInputME",
            "gregtech.MixinMTEHatchOutputMEBase",
            "gregtech.MixinMTEHatchOutputBusME",
            "gregtech.MixinMTEHatchOutputME",
            "gregtech.MixinMTEHatchAirIntake",
            "gregtech.MixinMTEHatchOutputBeamline",
            "gregtech.MixinMTEPurificationUnitBaseGui",
            "gregtech.MixinMTEDigitalTankBase",
            "gregtech.MixinMTEHatchDataAccess",
            "gregtech.MixinMTEHatchSteamBusOutput",
            "gregtech.MixinMTELightningRod",
            "gregtech.MixinMTEPlasmaForge",
            "gregtech.MixinMTEMultiBlockBaseInterfaceName",
            "gregtech.MixinMTEMultiBlockBaseRecipeProcessing",
            "gregtech.MixinMTETreeFarm",
            "gregtech.MixinProcessingLogic",
            "gregtech.MixinRecipeGenFluids",
            "gregtech.MixinProcessingDust",
            "inventoryBogoSorter.MixinShortcutHandler",
            "inventoryBogoSorter.MixinSortHandler",
            "modularUI.MixinFluidSlotWidget",
            "tConstruct.MixinSmelteryLogic",
            "tecTech.MixinMTEEyeOfHarmony",
            "tecTech.AccessorMTEResearchStation",
            "tecTech.MixinMTEResearchStation",
            "tecTech.MixinMTEResearchStationGui",
            "tecTech.MixinTTRecipeAdder",
            "thaumicTinkerer.AccessorAspectCropLootManager",
            "thaumicTinkerer.MixinItemBloodSword",
            "gtneioreplugin.MixinGT5OreLayerHelper",
            "visualProspecting.AccessorVeinTypeCaching")
        .addClientMixins(
            "appliedEnergistics.MixinTileCableBusBeamBounds",
            "appliedEnergistics.MixinTESRWrapperBeamRange",
            "appliedEnergistics.assembler.MixinGuiMEMonitorable",
            "appliedEnergistics.assembler.MixinGuiPatternTerm",
            "appliedEnergistics.quamtumComputer.MixinGuiCraftingCPUTable",
            "appliedEnergistics.quamtumComputer.MixinGuiCraftAmount",
            "appliedEnergistics.quamtumComputer.MixinGuiCraftConfirm",
            "appliedEnergistics.quamtumComputer.MixinGuiCraftingTree")
        .setPhase(Phase.LATE)),

    APPLIED_ENERGISTICS_PROCESSING_PATTERN(new MixinBuilder("Applied Energistics processing pattern capacity mixins")
        .addCommonMixins(
            "appliedEnergistics.processingPattern.MixinPartPatternTerminal",
            "appliedEnergistics.processingPattern.MixinPartPatternTerminalEx",
            "appliedEnergistics.processingPattern.MixinContainerPatternTerm",
            "appliedEnergistics.processingPattern.MixinContainerPatternTermEx",
            "appliedEnergistics.processingPattern.MixinWirelessPatternTerminalGuiObject")
        .addClientMixins("appliedEnergistics.processingPattern.MixinGuiPatternTermEx")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.AppliedEnergistics)),

    APPLIED_ENERGISTICS_PROCESSING_PATTERN_NEI(new MixinBuilder("Applied Energistics processing pattern NEI layout")
        .addClientMixins("appliedEnergistics.processingPattern.MixinNEIPatternViewHandler")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.AppliedEnergistics)
        .addRequiredMod(ModList.NotEnoughItems)),

    THAUMIC_ENERGISTICS(new MixinBuilder("Thaumic Energistics multi essentia container compatibility mixins")
        .addCommonMixins(
            "thaumicEnergistics.MixinEssentiaTileContainerHelper",
            "thaumicEnergistics.MixinMEMonitorEssentiaHandler",
            "thaumicEnergistics.MixinPartEssentiaImportBus")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.ThaumicEnergistics)),

    THAUMCRAFT_CLIENT(
        new MixinBuilder("Thaumcraft client compatibility mixins").addClientMixins("thaumcraft.MixinTileJarRenderer")
            .setPhase(Phase.LATE)
            .addExcludedMod(ModList.Angelica)),

    BARTWORKS(new MixinBuilder("BartWorks compatibility mixins")
        .addCommonMixins(
            "bartwork.MixinItemRegistry",
            "bartwork.MixinMultipleMetalLoader",
            "bartwork.MixinSimpleMetalLoader",
            "bartwork.MixinWerkstoff",
            "bartwork.MixinWerkstoffLoader",
            "bartwork.MixinMoltenCellLoader")
        .setPhase(Phase.LATE)),

    VOID_MINER_TWEAK(new MixinBuilder("Void Miner Tweak compatibility mixins")
        .addCommonMixins(
            "gregtech.MixinMTEVoidMinerBase",
            "gregtech.MixinMTEVoidMinerBaseGui",
            "gregtech.MixinMTEVoidMiners")
        .setPhase(Phase.LATE)
        .setApplyIf(() -> MainConfig.machine.enableVoidMinerTweak)
        .addExcludedMod(ModList.VMTweak)),

    GREGTECH_MAIN_FACING(new MixinBuilder().addCommonMixins("gregtech.MixinMTEBasicMachineFacing")
        .setPhase(Phase.LATE)
        .setApplyIf(() -> MainConfig.machine.allowMainFacingInteraction)),

    GALAXY_SPACE(new MixinBuilder("Galaxy Space Mixin").addCommonMixins("galaxySpace.MixinRocketRecipeHandler")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.GalaxySpace)),

    ENHANCED_LOOT_BAGS(
        new MixinBuilder("Enhanced Loot Bags Mixin").addCommonMixins("enhancedLootBags.AccessorItemLootBag")
            .setPhase(Phase.LATE)
            .addRequiredMod(ModList.EnhancedLootBags)),

    NOT_ENOUGH_ENERGISTICS(new MixinBuilder("Not Enough Energistics Mixin")
        .addCommonMixins("notEnoughEnergistics.MixinNEEPatternTerminalHandler")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.NotEnoughEnergistics)),

    NOT_ENOUGH_ITEMS(new MixinBuilder("Not Enough Items stability mixins")
        .addClientMixins("notEnoughItems.AccessorItemList", "notEnoughItems.MixinItemListUpdateFilter")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.NotEnoughItems)),

    NEI_CUSTOM_DIAGRAM(new MixinBuilder("NEI Custom Diagram Mixin")
        .addCommonMixins("neiCustomDiagram.AccessorNeiCustomDiagram", "neiCustomDiagram.MixinNeiCustomDiagram")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.NEICustomDiagrams)),

    NEI_AVARITIA_ADDON(
        new MixinBuilder("NEI Avaritia Addon Mixin").addCommonMixins("notEnoughItems.MixinGuiContainerManager")
            .setPhase(Phase.LATE)
            .addRequiredMod(ModList.AvaritiaAddons)),

    AE_RANDOM_COMPLEMENT(new MixinBuilder("AE2 Random Complement Mixins")
        .addCommonMixins(
            "randomComplement.MixinAEBaseContainer",
            "randomComplement.MixinBlockCraftingUnit",
            "randomComplement.MixinContainerMEMonitorable",
            "randomComplement.MixinGuiBridge",
            "randomComplement.MixinQuantumCluster",
            "randomComplement.MixinTileQuantumBridge",
            "randomComplement.MixinWirelessTerminalGuiObject")
        .setPhase(Phase.LATE)),

    OVERPOWERED_CHANGE(new MixinBuilder("Overpowered Mixin Changes")
        .addCommonMixins(
            "overpowered.MixinBehaviourScanner",
            "overpowered.MixinGregtechWailaDataProvider",
            "overpowered.MixinParallelHelper")
        .setPhase(Phase.LATE)
        .setApplyIf(() -> MainConfig.machine.enableRecipeOutputChance)
        .addExcludedMod(ModList.Overpowered)),

    OVERPOWERED_TST_CHANGE(
        new MixinBuilder("Overpowered TST Mixin Changes").addCommonMixins("overpowered.MixinGTCM_ParallelHelper")
            .setPhase(Phase.LATE)
            .setApplyIf(() -> MainConfig.machine.enableRecipeOutputChance)
            .addRequiredMod(ModList.TwistSpaceTechnology)
            .addExcludedMod(ModList.Overpowered)),

    NO_NHU(new MixinBuilder("Mixins when NHUtilities is absent")
        .addCommonMixins(
            "noNHU.MixinAbstractPoweredMachineEntity",
            "noNHU.MixinBlockItemCapBank",
            "noNHU.MixinMTEAdvAssLineAcceleration",
            "noNHU.MixinResearchStationAcceleration",
            "noNHU.MixinTileEntityEnder")
        .setPhase(Phase.LATE)
        .addExcludedMod(ModList.NHUtilities)),

    TST(new MixinBuilder("Twist Space Technology Mixins")
        .addCommonMixins(
            "twistSpaceTechnology.MixinRecipeLoader",
            "twistSpaceTechnology.MixinTST_OreProcessingFactory",
            "twistSpaceTechnology.MixinMM_DimensionallyTranscendentMatterPlasmaForgePrototypeMK2")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.TwistSpaceTechnology)),

    PP_CHANGE(new MixinBuilder("Purification Plant Changes")
        .addCommonMixins(
            "gregtech.MixinMTEPurificationPlant",
            "gregtech.MixinMTEPurificationUnitBaryonicPerfection",
            "gregtech.MixinMTEPurificationUnitBase",
            "gregtech.MixinMTEPurificationUnitOzonation",
            "gregtech.MixinMTEPurificationUnitPhAdjustment",
            "gregtech.MixinMTEPurificationUnitPlasmaHeater",
            "gregtech.MixinMTEPurificationUnitUVTreatment",
            "gregtech.MixinMTEPurificationUnitDegasser")
        .setPhase(Phase.LATE)
        .setApplyIf(() -> MainConfig.machine.enablePurificationPlantBuff)),

    STICK_LATE(new MixinBuilder("Stick Late Mixins")
        .addCommonMixins("stick.MixinTooltipHandlerWaila", "stick.MixinOreDictTooltipHandler")
        .setPhase(Phase.LATE)
        .setApplyIf(() -> MainConfig.item.stick.enableStickItem)),

    AUTO_STRUCTURE_TOOLTIP(new MixinBuilder("Automatic multiblock structure tooltips")
        .addCommonMixins(
            "structureLib.MixinLazyStructureElement",
            "gregtech.MixinMTEEnhancedMultiBlockBaseStructureTooltip")
        .setPhase(Phase.LATE)),

    ET_FUTURUM_ELYTRA(
        new MixinBuilder("InfinityElytra EtFuturum Mixin").addCommonMixins("etFuturum.MixinStartElytraFlyingHandler")
            .addClientMixins("etFuturum.MixinLayerBetterElytra")
            .setPhase(Phase.LATE)
            .addRequiredMod(ModList.EtFuturumRequiem)),;

    private final MixinBuilder builder;

    Mixins(Side side, String... mixins) {
        this.builder = new MixinBuilder().addSidedMixins(side, mixins)
            .setPhase(Phase.EARLY);
    }
}
