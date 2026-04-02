package com.science.gtnl.utils.enums;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;

public class BlockIcons {

    private static final String BASE_REPLICATOR = "basicmachines/replicator/";
    private static final String BASE_NINE_HATCH = RESOURCE_ROOT_ID + ":iconsets/OVERLAY_NINE_HATCH/";
    private static final String BASE = RESOURCE_ROOT_ID + ":iconsets/";

    public static Textures.BlockIcons.CustomIcon OVERLAY_ENERGY_TRANSFER_NODE = new Textures.BlockIcons.CustomIcon(
        BASE + "OVERLAY_ENERGY_TRANSFER_NODE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_ENERGY_TRANSFER_NODE_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "OVERLAY_ENERGY_TRANSFER_NODE_ACTIVE");

    public static Textures.BlockIcons.CustomIcon LASER_BEACON_TOP = new Textures.BlockIcons.CustomIcon(
        BASE + "LASER_BEACON_TOP");

    public static Textures.BlockIcons.CustomIcon BEAMLINE_PIPE_MIRROR = new Textures.BlockIcons.CustomIcon(
        BASE + "BEAMLINE_PIPE_MIRROR");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_FULLAUTOMAINTENANCE = new Textures.BlockIcons.CustomIcon(
        "iconsets/OVERLAY_FULLAUTOMAINTENANCE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_DUAL_HATCH = new Textures.BlockIcons.CustomIcon(
        BASE + "OVERLAY_DUAL_HATCH");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_PARALLEL_CONTROLLER = new Textures.BlockIcons.CustomIcon(
        BASE + "OVERLAY_PARALLEL_CONTROLLER");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_ITEMVAULTPORTHATCH = new Textures.BlockIcons.CustomIcon(
        BASE + "OVERLAY_FRONT_ITEMVAULTPORTHATCH");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "OVERLAY");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_NONE = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "NONE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_BLACK = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "BLACK");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_RED = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "RED");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_GREEN = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "GREEN");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_BROWN = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "BROWN");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_BLUE = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "BLUE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_PURPLE = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "PURPLE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_CYAN = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "CYAN");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_LIGHTGRAY = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "LIGHTGRAY");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_GRAY = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "GRAY");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_PINK = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "PINK");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_LIME = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "LIME");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_YELLOW = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "YELLOW");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_LIGHTBLUE = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "LIGHTBLUE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_MAGENTA = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "MAGENTA");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_ORANGE = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "ORANGE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_NINE_HATCH_WHITE = new Textures.BlockIcons.CustomIcon(
        BASE_NINE_HATCH + "WHITE");

    public static final Textures.BlockIcons.CustomIcon[] OVERLAY_FRONT_NINE_HATCH_COLOR = {
        OVERLAY_FRONT_NINE_HATCH_NONE, OVERLAY_FRONT_NINE_HATCH_BLACK, OVERLAY_FRONT_NINE_HATCH_RED,
        OVERLAY_FRONT_NINE_HATCH_GREEN, OVERLAY_FRONT_NINE_HATCH_BROWN, OVERLAY_FRONT_NINE_HATCH_BLUE,
        OVERLAY_FRONT_NINE_HATCH_PURPLE, OVERLAY_FRONT_NINE_HATCH_CYAN, OVERLAY_FRONT_NINE_HATCH_LIGHTGRAY,
        OVERLAY_FRONT_NINE_HATCH_GRAY, OVERLAY_FRONT_NINE_HATCH_PINK, OVERLAY_FRONT_NINE_HATCH_LIME,
        OVERLAY_FRONT_NINE_HATCH_YELLOW, OVERLAY_FRONT_NINE_HATCH_LIGHTBLUE, OVERLAY_FRONT_NINE_HATCH_MAGENTA,
        OVERLAY_FRONT_NINE_HATCH_ORANGE, OVERLAY_FRONT_NINE_HATCH_WHITE, };

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_INDICATOR = new Textures.BlockIcons.CustomIcon(
        BASE + "Indicator/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_INDICATOR_RED = new Textures.BlockIcons.CustomIcon(
        BASE + "Indicator/OVERLAY_FRONT_RED");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_INDICATOR_YELLOW = new Textures.BlockIcons.CustomIcon(
        BASE + "Indicator/OVERLAY_FRONT_YELLOW");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_INDICATOR_GREEN = new Textures.BlockIcons.CustomIcon(
        BASE + "Indicator/OVERLAY_FRONT_GREEN");

    public static final Textures.BlockIcons.CustomIcon OVERLAY_SIDE_REPLICATOR_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR_ACTIVE");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_SIDE_REPLICATOR_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR_ACTIVE_GLOW");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_SIDE_REPLICATOR = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_SIDE_REPLICATOR_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR_GLOW");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_FRONT_REPLICATOR_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR_ACTIVE");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_FRONT_REPLICATOR_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR_ACTIVE_GLOW");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_FRONT_REPLICATOR = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_FRONT_REPLICATOR_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR_GLOW");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_TOP_REPLICATOR_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR_ACTIVE");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_TOP_REPLICATOR_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR_ACTIVE_GLOW");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_TOP_REPLICATOR = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_TOP_REPLICATOR_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR_GLOW");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_BOTTOM_REPLICATOR_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR_ACTIVE");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_BOTTOM_REPLICATOR_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR_ACTIVE_GLOW");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_BOTTOM_REPLICATOR = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR");
    public static final Textures.BlockIcons.CustomIcon OVERLAY_BOTTOM_REPLICATOR_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR_GLOW");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_TECTECH_MULTIBLOCK = new Textures.BlockIcons.CustomIcon(
        "iconsets/EM_COMPUTER");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE = new Textures.BlockIcons.CustomIcon(
        "iconsets/EM_COMPUTER_ACTIVE");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_GOD_FORGE_MODULE_ACTIVE = new Textures.BlockIcons.CustomIcon(
        "iconsets/GODFORGE_MODULE_ACTIVE");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_DECAY_HASTENER = new Textures.BlockIcons.CustomIcon(
        "icons/NeutronActivator_Off");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_DECAY_HASTENER_ACTIVE = new Textures.BlockIcons.CustomIcon(
        "icons/NeutronActivator_On");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_LARGE_GAS_COLLECTOR = new Textures.BlockIcons.CustomIcon(
        BASE + "LargeGasCollector/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_LARGE_GAS_COLLECTOR_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "LargeGasCollector/OVERLAY_FRONT_ACTIVE");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_CACTUS_WONDER = new Textures.BlockIcons.CustomIcon(
        BASE + "CactusWonder/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_CACTUS_WONDER_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "CactusWonder/OVERLAY_FRONT_ACTIVE");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_CARPENTER = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamCarpenter/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_CARPENTER_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamCarpenter/OVERLAY_FRONT_ACTIVE");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_EXTRACTINATOR = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamExtractinator/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_EXTRACTINATOR_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamExtractinator/OVERLAY_FRONT_ACTIVE");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_GATE = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamGate/OVERLAY_FRONT");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_GATE_ASSEMBLER = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamGateAssembler/OVERLAY_FRONT");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_INFERNAL_COKE_OVEN = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamInfernalCokeOven/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_INFERNAL_COKE_OVEN_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamInfernalCokeOven/OVERLAY_FRONT_ACTIVE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_INFERNAL_COKE_OVEN_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamInfernalCokeOven/OVERLAY_FRONT_ACTIVE_GLOW");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_LAVA_MAKER = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamLavaMaker/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_LAVA_MAKER_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamLavaMaker/OVERLAY_FRONT_ACTIVE");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_MANUFACTURER = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamManufacturer/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_MANUFACTURER_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamManufacturer/OVERLAY_FRONT_ACTIVE");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_METEOR_MINER = new Textures.BlockIcons.CustomIcon(
        BASE + "MeteorMiner/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_METEOR_MINER_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE + "MeteorMiner/OVERLAY_FRONT_GLOW");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_METEOR_MINER_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "MeteorMiner/OVERLAY_FRONT_ACTIVE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_METEOR_MINER_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE + "MeteorMiner/OVERLAY_FRONT_ACTIVE_GLOW");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_MEGA_SOLAR_BOILER = new Textures.BlockIcons.CustomIcon(
        BASE + "MegaSolarBoiler/OVERLAY_FRONT");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR = new Textures.BlockIcons.CustomIcon(
        BASE + "MegaSteamCompressor/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE + "MegaSteamCompressor/OVERLAY_FRONT_GLOW");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "MegaSteamCompressor/OVERLAY_FRONT_ACTIVE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE + "MegaSteamCompressor/OVERLAY_FRONT_ACTIVE_GLOW");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_ITEM_VAULT = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamItemVault/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_ITEM_VAULT_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamItemVault/OVERLAY_FRONT_ACTIVE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_STEAM_ITEM_VAULT_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE + "SteamItemVault/OVERLAY_FRONT_ACTIVE_GLOW");

    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_SINGULARITY_DATA_HUB = new Textures.BlockIcons.CustomIcon(
        BASE + "SingularityDataHub/OVERLAY_FRONT");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE = new Textures.BlockIcons.CustomIcon(
        BASE + "SingularityDataHub/OVERLAY_FRONT_ACTIVE");
    public static Textures.BlockIcons.CustomIcon OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        BASE + "SingularityDataHub/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR = new Textures.BlockIcons.CustomIcon(
        "icons/NeutronActivator_Off");
    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR_GLOW = new Textures.BlockIcons.CustomIcon(
        "icons/NeutronActivator_Off_GLOW");
    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR_ACTIVE = new Textures.BlockIcons.CustomIcon(
        "icons/NeutronActivator_On");
    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR_ACTIVE_GLOW = new Textures.BlockIcons.CustomIcon(
        "icons/NeutronActivator_On_GLOW");

}
