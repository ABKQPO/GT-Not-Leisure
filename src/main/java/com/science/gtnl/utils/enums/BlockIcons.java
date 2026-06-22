package com.science.gtnl.utils.enums;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;

public class BlockIcons {

    private static final String BASE_REPLICATOR = "basicmachines/replicator/";
    private static final String BASE_NINE_HATCH = RESOURCE_ROOT_ID + ":iconsets/OVERLAY_NINE_HATCH/";
    private static final String BASE = RESOURCE_ROOT_ID + ":iconsets/";

    public static IIconContainer OVERLAY_ENERGY_TRANSFER_NODE = Textures.BlockIcons
        .custom(BASE + "OVERLAY_ENERGY_TRANSFER_NODE");
    public static IIconContainer OVERLAY_ENERGY_TRANSFER_NODE_ACTIVE = Textures.BlockIcons
        .custom(BASE + "OVERLAY_ENERGY_TRANSFER_NODE_ACTIVE");

    public static IIconContainer LASER_BEACON_TOP = Textures.BlockIcons.custom(BASE + "LASER_BEACON_TOP");

    public static IIconContainer BEAMLINE_PIPE_MIRROR = Textures.BlockIcons.custom(BASE + "BEAMLINE_PIPE_MIRROR");
    public static IIconContainer OVERLAY_FRONT_FULLAUTOMAINTENANCE = Textures.BlockIcons
        .custom("iconsets/OVERLAY_FULLAUTOMAINTENANCE");
    public static IIconContainer OVERLAY_FRONT_DUAL_HATCH = Textures.BlockIcons.custom(BASE + "OVERLAY_DUAL_HATCH");
    public static IIconContainer OVERLAY_ENERGY_MONITOR = Textures.BlockIcons.custom(BASE + "OVERLAY_ENERGY_MONITOR");
    public static IIconContainer OVERLAY_FRONT_PARALLEL_CONTROLLER = Textures.BlockIcons
        .custom(BASE + "OVERLAY_PARALLEL_CONTROLLER");
    public static IIconContainer OVERLAY_FRONT_ITEMVAULTPORTHATCH = Textures.BlockIcons
        .custom(BASE + "OVERLAY_FRONT_ITEMVAULTPORTHATCH");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH = Textures.BlockIcons.custom(BASE_NINE_HATCH + "OVERLAY");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_NONE = Textures.BlockIcons.custom(BASE_NINE_HATCH + "NONE");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_BLACK = Textures.BlockIcons.custom(BASE_NINE_HATCH + "BLACK");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_RED = Textures.BlockIcons.custom(BASE_NINE_HATCH + "RED");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_GREEN = Textures.BlockIcons.custom(BASE_NINE_HATCH + "GREEN");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_BROWN = Textures.BlockIcons.custom(BASE_NINE_HATCH + "BROWN");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_BLUE = Textures.BlockIcons.custom(BASE_NINE_HATCH + "BLUE");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_PURPLE = Textures.BlockIcons
        .custom(BASE_NINE_HATCH + "PURPLE");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_CYAN = Textures.BlockIcons.custom(BASE_NINE_HATCH + "CYAN");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_LIGHTGRAY = Textures.BlockIcons
        .custom(BASE_NINE_HATCH + "LIGHTGRAY");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_GRAY = Textures.BlockIcons.custom(BASE_NINE_HATCH + "GRAY");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_PINK = Textures.BlockIcons.custom(BASE_NINE_HATCH + "PINK");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_LIME = Textures.BlockIcons.custom(BASE_NINE_HATCH + "LIME");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_YELLOW = Textures.BlockIcons
        .custom(BASE_NINE_HATCH + "YELLOW");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_LIGHTBLUE = Textures.BlockIcons
        .custom(BASE_NINE_HATCH + "LIGHTBLUE");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_MAGENTA = Textures.BlockIcons
        .custom(BASE_NINE_HATCH + "MAGENTA");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_ORANGE = Textures.BlockIcons
        .custom(BASE_NINE_HATCH + "ORANGE");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_WHITE = Textures.BlockIcons.custom(BASE_NINE_HATCH + "WHITE");

    public static final IIconContainer[] OVERLAY_FRONT_NINE_HATCH_COLOR = { OVERLAY_FRONT_NINE_HATCH_NONE,
        OVERLAY_FRONT_NINE_HATCH_BLACK, OVERLAY_FRONT_NINE_HATCH_RED, OVERLAY_FRONT_NINE_HATCH_GREEN,
        OVERLAY_FRONT_NINE_HATCH_BROWN, OVERLAY_FRONT_NINE_HATCH_BLUE, OVERLAY_FRONT_NINE_HATCH_PURPLE,
        OVERLAY_FRONT_NINE_HATCH_CYAN, OVERLAY_FRONT_NINE_HATCH_LIGHTGRAY, OVERLAY_FRONT_NINE_HATCH_GRAY,
        OVERLAY_FRONT_NINE_HATCH_PINK, OVERLAY_FRONT_NINE_HATCH_LIME, OVERLAY_FRONT_NINE_HATCH_YELLOW,
        OVERLAY_FRONT_NINE_HATCH_LIGHTBLUE, OVERLAY_FRONT_NINE_HATCH_MAGENTA, OVERLAY_FRONT_NINE_HATCH_ORANGE,
        OVERLAY_FRONT_NINE_HATCH_WHITE, };

    public static IIconContainer OVERLAY_FRONT_INDICATOR = Textures.BlockIcons.custom(BASE + "Indicator/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_INDICATOR_RED = Textures.BlockIcons
        .custom(BASE + "Indicator/OVERLAY_FRONT_RED");
    public static IIconContainer OVERLAY_FRONT_INDICATOR_YELLOW = Textures.BlockIcons
        .custom(BASE + "Indicator/OVERLAY_FRONT_YELLOW");
    public static IIconContainer OVERLAY_FRONT_INDICATOR_GREEN = Textures.BlockIcons
        .custom(BASE + "Indicator/OVERLAY_FRONT_GREEN");

    public static final IIconContainer OVERLAY_SIDE_REPLICATOR_ACTIVE = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR_ACTIVE");
    public static final IIconContainer OVERLAY_SIDE_REPLICATOR_ACTIVE_GLOW = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR_ACTIVE_GLOW");
    public static final IIconContainer OVERLAY_SIDE_REPLICATOR = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR");
    public static final IIconContainer OVERLAY_SIDE_REPLICATOR_GLOW = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR_GLOW");
    public static final IIconContainer OVERLAY_FRONT_REPLICATOR_ACTIVE = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR_ACTIVE");
    public static final IIconContainer OVERLAY_FRONT_REPLICATOR_ACTIVE_GLOW = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR_ACTIVE_GLOW");
    public static final IIconContainer OVERLAY_FRONT_REPLICATOR = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR");
    public static final IIconContainer OVERLAY_FRONT_REPLICATOR_GLOW = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR_GLOW");
    public static final IIconContainer OVERLAY_TOP_REPLICATOR_ACTIVE = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR_ACTIVE");
    public static final IIconContainer OVERLAY_TOP_REPLICATOR_ACTIVE_GLOW = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR_ACTIVE_GLOW");
    public static final IIconContainer OVERLAY_TOP_REPLICATOR = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR");
    public static final IIconContainer OVERLAY_TOP_REPLICATOR_GLOW = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR_GLOW");
    public static final IIconContainer OVERLAY_BOTTOM_REPLICATOR_ACTIVE = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR_ACTIVE");
    public static final IIconContainer OVERLAY_BOTTOM_REPLICATOR_ACTIVE_GLOW = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR_ACTIVE_GLOW");
    public static final IIconContainer OVERLAY_BOTTOM_REPLICATOR = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR");
    public static final IIconContainer OVERLAY_BOTTOM_REPLICATOR_GLOW = Textures.BlockIcons
        .custom(BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR_GLOW");

    public static IIconContainer OVERLAY_FRONT_TECTECH_MULTIBLOCK = Textures.BlockIcons.custom("iconsets/EM_COMPUTER");
    public static IIconContainer OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE = Textures.BlockIcons
        .custom("iconsets/EM_COMPUTER_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_GOD_FORGE_CONTROLLER = Textures.BlockIcons
        .custom("iconsets/GODFORGE_CONTROLLER");

    public static IIconContainer OVERLAY_FRONT_DECAY_HASTENER = Textures.BlockIcons
        .custom("icons/NeutronActivator_Off");
    public static IIconContainer OVERLAY_FRONT_DECAY_HASTENER_ACTIVE = Textures.BlockIcons
        .custom("icons/NeutronActivator_On");

    public static IIconContainer OVERLAY_FRONT_LARGE_GAS_COLLECTOR = Textures.BlockIcons
        .custom(BASE + "LargeGasCollector/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_LARGE_GAS_COLLECTOR_ACTIVE = Textures.BlockIcons
        .custom(BASE + "LargeGasCollector/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_CACTUS_WONDER = Textures.BlockIcons
        .custom(BASE + "CactusWonder/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_CACTUS_WONDER_ACTIVE = Textures.BlockIcons
        .custom(BASE + "CactusWonder/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_STEAM_CARPENTER = Textures.BlockIcons
        .custom(BASE + "SteamCarpenter/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_CARPENTER_ACTIVE = Textures.BlockIcons
        .custom(BASE + "SteamCarpenter/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_STEAM_EXTRACTINATOR = Textures.BlockIcons
        .custom(BASE + "SteamExtractinator/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_EXTRACTINATOR_ACTIVE = Textures.BlockIcons
        .custom(BASE + "SteamExtractinator/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_STEAM_GATE = Textures.BlockIcons
        .custom(BASE + "SteamGate/OVERLAY_FRONT");

    public static IIconContainer OVERLAY_FRONT_STEAM_GATE_ASSEMBLER = Textures.BlockIcons
        .custom(BASE + "SteamGateAssembler/OVERLAY_FRONT");

    public static IIconContainer OVERLAY_FRONT_STEAM_INFERNAL_COKE_OVEN = Textures.BlockIcons
        .custom(BASE + "SteamInfernalCokeOven/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_INFERNAL_COKE_OVEN_ACTIVE = Textures.BlockIcons
        .custom(BASE + "SteamInfernalCokeOven/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_STEAM_INFERNAL_COKE_OVEN_ACTIVE_GLOW = Textures.BlockIcons
        .custom(BASE + "SteamInfernalCokeOven/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_STEAM_LAVA_MAKER = Textures.BlockIcons
        .custom(BASE + "SteamLavaMaker/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_LAVA_MAKER_ACTIVE = Textures.BlockIcons
        .custom(BASE + "SteamLavaMaker/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_STEAM_MANUFACTURER = Textures.BlockIcons
        .custom(BASE + "SteamManufacturer/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_MANUFACTURER_ACTIVE = Textures.BlockIcons
        .custom(BASE + "SteamManufacturer/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_METEOR_MINER = Textures.BlockIcons
        .custom(BASE + "MeteorMiner/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_METEOR_MINER_GLOW = Textures.BlockIcons
        .custom(BASE + "MeteorMiner/OVERLAY_FRONT_GLOW");
    public static IIconContainer OVERLAY_FRONT_METEOR_MINER_ACTIVE = Textures.BlockIcons
        .custom(BASE + "MeteorMiner/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_METEOR_MINER_ACTIVE_GLOW = Textures.BlockIcons
        .custom(BASE + "MeteorMiner/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_MEGA_SOLAR_BOILER = Textures.BlockIcons
        .custom(BASE + "MegaSolarBoiler/OVERLAY_FRONT");

    public static IIconContainer OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR = Textures.BlockIcons
        .custom(BASE + "MegaSteamCompressor/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_GLOW = Textures.BlockIcons
        .custom(BASE + "MegaSteamCompressor/OVERLAY_FRONT_GLOW");
    public static IIconContainer OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_ACTIVE = Textures.BlockIcons
        .custom(BASE + "MegaSteamCompressor/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_ACTIVE_GLOW = Textures.BlockIcons
        .custom(BASE + "MegaSteamCompressor/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_STEAM_ITEM_VAULT = Textures.BlockIcons
        .custom(BASE + "SteamItemVault/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_ITEM_VAULT_ACTIVE = Textures.BlockIcons
        .custom(BASE + "SteamItemVault/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_STEAM_ITEM_VAULT_ACTIVE_GLOW = Textures.BlockIcons
        .custom(BASE + "SteamItemVault/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_SINGULARITY_DATA_HUB = Textures.BlockIcons
        .custom(BASE + "SingularityDataHub/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE = Textures.BlockIcons
        .custom(BASE + "SingularityDataHub/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE_GLOW = Textures.BlockIcons
        .custom(BASE + "SingularityDataHub/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR = Textures.BlockIcons
        .custom("icons/NeutronActivator_Off");
    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR_GLOW = Textures.BlockIcons
        .custom("icons/NeutronActivator_Off_GLOW");
    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR_ACTIVE = Textures.BlockIcons
        .custom("icons/NeutronActivator_On");
    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR_ACTIVE_GLOW = Textures.BlockIcons
        .custom("icons/NeutronActivator_On_GLOW");

}
