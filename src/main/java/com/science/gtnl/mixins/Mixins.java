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
        "gregtech.AccessorGTLanguageManager", "gregtech.AccessorCommonMetaTileEntity",
        "gregtech.AccessorMetaTileEntity", "gregtech.AccessorMTEHatch", "gregtech.AccessorProcessingLogic",
        "gregtech.AccessorRecipeDisplayInfo", "gregtech.MixinMTEBasicMachine", "gregtech.MixinBaseMetaTileEntity",
        "gregtech.assLineRemover.MixinGTMod", "gregtech.assLineRemover.MixinGTRecipeBuilder",
        "gregtech.assLineRemover.MixinTTRecipeAdder", "energymonitor.MixinBaseMetaTileEntityEnergyMonitor",
        "energymonitor.MixinCommonMetaTileEntityEnergyMonitor"),

    GREGTECH_CLIENT_EARLY(
        new MixinBuilder("Gregtech early client safety mixins").addClientMixins("gregtech.MixinGTLanguageManager")
            .setPhase(Phase.EARLY)),

    NO_NHU_EARLY(
        new MixinBuilder("Early Mixins when NHUtilities is absent").addCommonMixins("noNHU.MixinBaseMetaTileEntity")
            .setPhase(Phase.EARLY)
            .addExcludedMod(ModList.NHUtilities)),

    NH_CORE_MOD_EARLY(new MixinBuilder().addCommonMixins("nHCoreMod.AccessorBacteriaRegistry")
        .setPhase(Phase.EARLY)
        .addRequiredMod(ModList.NewHorizonsCoreMod)),

    MINECRAFT_COMMON(Side.COMMON, "minecraft.AccessorStringTranslate", "minecraft.AccessorContainerRepair",
        "minecraft.AccessorEntityLivingBase", "minecraft.AccessorTessellator", "minecraft.AccessorFoodStats",
        "minecraft.AccessorMinecraft", "minecraft.MixinCommandTeleport", "minecraft.MixinEntity",
        "minecraft.MixinEntityItem", "minecraft.MixinEntityLivingBase", "minecraft.MixinEntityLiving",
        "minecraft.MixinEntityPlayer", "minecraft.MixinExplosion", "minecraft.MixinInventoryCrafting",
        "minecraft.MixinItemStack", "minecraft.MixinMinecraftServer", "minecraft.MixinNBTTagList",
        "minecraft.MixinPotionEffect", "minecraft.MixinServerConfigurationManager", "minecraft.MixinWorld",
        "minecraft.MixinWorldServer"),

    APRIL_FOOL(new MixinBuilder("April Fool Late Mixins")
        .addSidedMixins(Side.CLIENT, "aprilFool.MixinBaseMetaTileEntityRenderer", "aprilFool.MixinCommonMetaTileEntity")
        .setPhase(Phase.EARLY)
        .setApplyIf(() -> GTNLEarlyCoreMod.enableAprilFool)),

    MINECRAFT_KILL_ENHANCE(
        new MixinBuilder("Mixins for Kill Command Enhance").addCommonMixins("minecraft.MixinCommandKill")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> MainConfig.minecraft.enableKillEnhance)),

    MINECRAFT_CLIENT(Side.CLIENT, "minecraft.AccessorEntityRenderer", "minecraft.AccessorGuiChat",
        "minecraft.MixinMinecraft", "minecraft.MixinWorldClient",
        // "minecraft.MixinSimpleReloadableResourceManager",
        "minecraft.MixinGuiContainer", "minecraft.MixinGuiFlatPresets"),

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

    LATE_COMMON(new MixinBuilder("General Late Mixins")
        .addCommonMixins(
            "aEFluidCraft.MixinItemFluidPacket",
            "appliedEnergistics.AccessorAEBaseItemBlock",
            "appliedEnergistics.AccessorContainerUpgradeable",
            "appliedEnergistics.AccessorPartInterface",
            "appliedEnergistics.AccessorTileInterface",
            "appliedEnergistics.MixinAdaptorIInventory",
            "appliedEnergistics.MixinDualityInterface",
            "appliedEnergistics.MixinInterfaceTerminalEntry",
            "appliedEnergistics.MixinEntityTinyTNTPrimed",
            "appliedEnergistics.MixinTileIOPort",
            "appliedEnergistics.assembler.AccessorContainerPatternTerm",
            "appliedEnergistics.assembler.AccessorInvTracker",
            "appliedEnergistics.assembler.MixinContainerInterfaceTerminal",
            "appliedEnergistics.MixinCraftingCPUCluster",
            "appliedEnergistics.AccessorTaskProgress",
            "appliedEnergistics.quamtumComputer.MixinCraftingCPUCluster",
            "appliedEnergistics.quamtumComputer.MixinCraftingGridCache",
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
            "gregtech.MixinMTEHatchOutputBusME",
            "gregtech.MixinMTEHatchOutputME",
            "gregtech.MixinMTEHatchAirIntake",
            "gregtech.MixinMTEHatchCraftingInputME",
            "gregtech.MixinMTEHatchCraftingInputSlave",
            "gregtech.MixinMTEHatchOutputBeamline",
            "gregtech.MixinMTEPurificationUnitBaseGui",
            "gregtech.MixinMTEDigitalTankBase",
            "gregtech.MixinMTEHatchDataAccess",
            "gregtech.MixinMTEHatchSteamBusOutput",
            "gregtech.MixinMTELightningRod",
            "gregtech.MixinMTEPlasmaForge",
            "gregtech.MixinMTEMultiBlockBase",
            "gregtech.MixinMTEMultiBlockBaseRecipeProcessing",
            "gregtech.MixinMTETreeFarm",
            "gregtech.MixinMTEVoidMinerBase",
            "gregtech.MixinMTEVoidMiners",
            "gregtech.MixinProcessingLogic",
            "gregtech.MixinRecipeGenFluids",
            "gregtech.MixinProcessingDust",
            "gregtech.MixinTTMultiblockBase",
            "inventoryBogoSorter.MixinShortcutHandler",
            "inventoryBogoSorter.MixinSortHandler",
            "modularUI.MixinFluidSlotWidget",
            "tConstruct.MixinSmelteryLogic",
            "tecTech.MixinMTEEyeOfHarmony",
            "tecTech.MixinMTEResearchStation",
            "tecTech.MixinMTEResearchStationGui",
            "tecTech.MixinTTRecipeAdder",
            "thaumicTinkerer.AccessorAspectCropLootManager",
            "thaumicTinkerer.MixinItemBloodSword",
            "visualProspecting.AccessorVeinTypeCaching")
        .addClientMixins(
            "appliedEnergistics.assembler.MixinGuiMEMonitorable",
            "appliedEnergistics.assembler.MixinGuiPatternTerm")
        .setPhase(Phase.LATE)),

    GREGTECH_MAIN_FACING(new MixinBuilder().addCommonMixins("gregtech.MixinMTEBasicMachineFacing")
        .setPhase(Phase.LATE)
        .setApplyIf(() -> MainConfig.machine.allowMainFacingInteraction)),

    GALAXY_SPACE(new MixinBuilder("Galaxy Space Mixin").addCommonMixins("galaxySpace.MixinRocketRecipeHandler")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.GalaxySpace)),

    NH_CORE_MOD(new MixinBuilder("NH Core Mod Mixin").addCommonMixins("nHCoreMod.MixinBacteriaRegistry")
        .setPhase(Phase.LATE)
        .addRequiredMod(ModList.NewHorizonsCoreMod)),

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
        .addCommonMixins("nEICustomDiagram.AccessorNeiCustomDiagram", "nEICustomDiagram.MixinNeiCustomDiagram")
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
            "randomComplement.MixinContainerCraftAmount",
            "randomComplement.MixinContainerCraftConfirm",
            "randomComplement.MixinContainerMEMonitorable",
            "randomComplement.MixinGuiBridge",
            "randomComplement.MixinQuantumCluster",
            "randomComplement.MixinTileQuantumBridge",
            "randomComplement.MixinWirelessTerminalGuiObject")
        .addClientMixins(
            "randomComplement.MixinGuiAmount",
            "randomComplement.MixinGuiCraftAmount",
            "randomComplement.MixinGuiCraftConfirm")
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
