package com.science.gtnl.utils.enums;

import static com.science.gtnl.ScienceNotLeisure.RESOURCE_ROOT_ID;

import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;

public class BlockIcons {

    private static final String BASE_REPLICATOR = "basicmachines/replicator/";
    private static final String BASE_NINE_HATCH = "iconsets/overlay_nine_hatch/";
    private static final String BASE = "iconsets/";

    public static IIconContainer OVERLAY_ENERGY_TRANSFER_NODE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "OVERLAY_ENERGY_TRANSFER_NODE");
    public static IIconContainer OVERLAY_ENERGY_TRANSFER_NODE_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "OVERLAY_ENERGY_TRANSFER_NODE_ACTIVE");

    public static IIconContainer LASER_BEACON_TOP = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "LASER_BEACON_TOP");

    public static IIconContainer BEAMLINE_PIPE_MIRROR = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "BEAMLINE_PIPE_MIRROR");
    public static IIconContainer OVERLAY_FRONT_FULLAUTOMAINTENANCE = Textures.BlockIcons
        .custom(Mods.GregTech.resourceDomain, "iconsets/OVERLAY_FULLAUTOMAINTENANCE");
    public static IIconContainer OVERLAY_FRONT_DUAL_HATCH = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "OVERLAY_DUAL_HATCH");
    public static IIconContainer OVERLAY_ENERGY_MONITOR = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "OVERLAY_ENERGY_MONITOR");
    public static IIconContainer OVERLAY_FRONT_PARALLEL_CONTROLLER = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "OVERLAY_PARALLEL_CONTROLLER");
    public static IIconContainer OVERLAY_FRONT_ITEMVAULTPORTHATCH = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "OVERLAY_FRONT_ITEMVAULTPORTHATCH");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "OVERLAY");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_NONE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "NONE");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_BLACK = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "BLACK");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_RED = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "RED");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_GREEN = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "GREEN");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_BROWN = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "BROWN");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_BLUE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "BLUE");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_PURPLE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "PURPLE");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_CYAN = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "CYAN");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_LIGHTGRAY = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "LIGHTGRAY");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_GRAY = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "GRAY");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_PINK = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "PINK");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_LIME = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "LIME");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_YELLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "YELLOW");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_LIGHTBLUE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "LIGHTBLUE");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_MAGENTA = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "MAGENTA");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_ORANGE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "ORANGE");
    public static IIconContainer OVERLAY_FRONT_NINE_HATCH_WHITE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE_NINE_HATCH + "WHITE");

    public static final IIconContainer[] OVERLAY_FRONT_NINE_HATCH_COLOR = { OVERLAY_FRONT_NINE_HATCH_NONE,
        OVERLAY_FRONT_NINE_HATCH_BLACK, OVERLAY_FRONT_NINE_HATCH_RED, OVERLAY_FRONT_NINE_HATCH_GREEN,
        OVERLAY_FRONT_NINE_HATCH_BROWN, OVERLAY_FRONT_NINE_HATCH_BLUE, OVERLAY_FRONT_NINE_HATCH_PURPLE,
        OVERLAY_FRONT_NINE_HATCH_CYAN, OVERLAY_FRONT_NINE_HATCH_LIGHTGRAY, OVERLAY_FRONT_NINE_HATCH_GRAY,
        OVERLAY_FRONT_NINE_HATCH_PINK, OVERLAY_FRONT_NINE_HATCH_LIME, OVERLAY_FRONT_NINE_HATCH_YELLOW,
        OVERLAY_FRONT_NINE_HATCH_LIGHTBLUE, OVERLAY_FRONT_NINE_HATCH_MAGENTA, OVERLAY_FRONT_NINE_HATCH_ORANGE,
        OVERLAY_FRONT_NINE_HATCH_WHITE, };

    public static IIconContainer OVERLAY_FRONT_INDICATOR = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "indicator/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_INDICATOR_RED = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "indicator/OVERLAY_FRONT_RED");
    public static IIconContainer OVERLAY_FRONT_INDICATOR_YELLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "indicator/OVERLAY_FRONT_YELLOW");
    public static IIconContainer OVERLAY_FRONT_INDICATOR_GREEN = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "indicator/OVERLAY_FRONT_GREEN");

    public static final IIconContainer OVERLAY_SIDE_REPLICATOR_ACTIVE = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR_ACTIVE");
    public static final IIconContainer OVERLAY_SIDE_REPLICATOR_ACTIVE_GLOW = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR_ACTIVE_GLOW");
    public static final IIconContainer OVERLAY_SIDE_REPLICATOR = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR");
    public static final IIconContainer OVERLAY_SIDE_REPLICATOR_GLOW = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_SIDE_REPLICATOR_GLOW");
    public static final IIconContainer OVERLAY_FRONT_REPLICATOR_ACTIVE = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR_ACTIVE");
    public static final IIconContainer OVERLAY_FRONT_REPLICATOR_ACTIVE_GLOW = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR_ACTIVE_GLOW");
    public static final IIconContainer OVERLAY_FRONT_REPLICATOR = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR");
    public static final IIconContainer OVERLAY_FRONT_REPLICATOR_GLOW = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_FRONT_REPLICATOR_GLOW");
    public static final IIconContainer OVERLAY_TOP_REPLICATOR_ACTIVE = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR_ACTIVE");
    public static final IIconContainer OVERLAY_TOP_REPLICATOR_ACTIVE_GLOW = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR_ACTIVE_GLOW");
    public static final IIconContainer OVERLAY_TOP_REPLICATOR = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR");
    public static final IIconContainer OVERLAY_TOP_REPLICATOR_GLOW = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_TOP_REPLICATOR_GLOW");
    public static final IIconContainer OVERLAY_BOTTOM_REPLICATOR_ACTIVE = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR_ACTIVE");
    public static final IIconContainer OVERLAY_BOTTOM_REPLICATOR_ACTIVE_GLOW = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR_ACTIVE_GLOW");
    public static final IIconContainer OVERLAY_BOTTOM_REPLICATOR = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR");
    public static final IIconContainer OVERLAY_BOTTOM_REPLICATOR_GLOW = Textures.BlockIcons
        .customOptional(Mods.GregTech.resourceDomain, BASE_REPLICATOR + "OVERLAY_BOTTOM_REPLICATOR_GLOW");

    public static IIconContainer OVERLAY_FRONT_TECTECH_MULTIBLOCK = Textures.BlockIcons
        .custom(Mods.GregTech.resourceDomain, "iconsets/EM_COMPUTER");
    public static IIconContainer OVERLAY_FRONT_TECTECH_MULTIBLOCK_ACTIVE = Textures.BlockIcons
        .custom(Mods.GregTech.resourceDomain, "iconsets/EM_COMPUTER_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_GOD_FORGE_CONTROLLER = Textures.BlockIcons
        .custom(Mods.GregTech.resourceDomain, "iconsets/GODFORGE_CONTROLLER");

    public static IIconContainer OVERLAY_FRONT_DECAY_HASTENER = Textures.BlockIcons
        .custom(Mods.GregTech.resourceDomain, "icons/NeutronActivator_Off");
    public static IIconContainer OVERLAY_FRONT_DECAY_HASTENER_ACTIVE = Textures.BlockIcons
        .custom(Mods.GregTech.resourceDomain, "icons/NeutronActivator_On");

    public static IIconContainer OVERLAY_FRONT_LARGE_GAS_COLLECTOR = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "large_gas_collector/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_LARGE_GAS_COLLECTOR_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "large_gas_collector/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_CACTUS_WONDER = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "cactus_wonder/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_CACTUS_WONDER_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "cactus_wonder/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_STEAM_CARPENTER = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_carpenter/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_CARPENTER_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_carpenter/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_STEAM_EXTRACTINATOR = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_extractinator/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_EXTRACTINATOR_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_extractinator/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_STEAM_GATE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_gate/OVERLAY_FRONT");

    public static IIconContainer OVERLAY_FRONT_STEAM_GATE_ASSEMBLER = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_gate_assembler/OVERLAY_FRONT");

    public static IIconContainer OVERLAY_FRONT_STEAM_INFERNAL_COKE_OVEN = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_infernal_coke_oven/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_INFERNAL_COKE_OVEN_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_infernal_coke_oven/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_STEAM_INFERNAL_COKE_OVEN_ACTIVE_GLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_infernal_coke_oven/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_STEAM_LAVA_MAKER = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_lava_maker/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_LAVA_MAKER_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_lava_maker/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_STEAM_MANUFACTURER = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_manufacturer/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_MANUFACTURER_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_manufacturer/OVERLAY_FRONT_ACTIVE");

    public static IIconContainer OVERLAY_FRONT_METEOR_MINER = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "meteor_miner/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_METEOR_MINER_GLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "meteor_miner/OVERLAY_FRONT_GLOW");
    public static IIconContainer OVERLAY_FRONT_METEOR_MINER_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "meteor_miner/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_METEOR_MINER_ACTIVE_GLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "meteor_miner/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_MEGA_SOLAR_BOILER = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "mega_solar_boiler/OVERLAY_FRONT");

    public static IIconContainer OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "mega_steam_compressor/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_GLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "mega_steam_compressor/OVERLAY_FRONT_GLOW");
    public static IIconContainer OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "mega_steam_compressor/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_MEGA_STEAM_COMPRESSOR_ACTIVE_GLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "mega_steam_compressor/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_STEAM_ITEM_VAULT = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_item_vault/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_STEAM_ITEM_VAULT_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_item_vault/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_STEAM_ITEM_VAULT_ACTIVE_GLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "steam_item_vault/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_SINGULARITY_DATA_HUB = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "singularity_data_hub/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_SINGULARITY_DATA_HUB_GLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "singularity_data_hub/OVERLAY_FRONT_GLOW");
    public static IIconContainer OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "singularity_data_hub/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_SINGULARITY_DATA_HUB_ACTIVE_GLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "singularity_data_hub/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_NUCLEAR_REACTOR = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "nuclear_reactor/OVERLAY_FRONT");
    public static IIconContainer OVERLAY_FRONT_NUCLEAR_REACTOR_GLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "nuclear_reactor/OVERLAY_FRONT_GLOW");
    public static IIconContainer OVERLAY_FRONT_NUCLEAR_REACTOR_ACTIVE = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "nuclear_reactor/OVERLAY_FRONT_ACTIVE");
    public static IIconContainer OVERLAY_FRONT_NUCLEAR_REACTOR_ACTIVE_GLOW = Textures.BlockIcons
        .custom(RESOURCE_ROOT_ID, BASE + "nuclear_reactor/OVERLAY_FRONT_ACTIVE_GLOW");

    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR = Textures.BlockIcons
        .custom(Mods.GregTech.resourceDomain, "icons/NeutronActivator_Off");
    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR_GLOW = Textures.BlockIcons
        .custom(Mods.GregTech.resourceDomain, "icons/NeutronActivator_Off_GLOW");
    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR_ACTIVE = Textures.BlockIcons
        .custom(Mods.GregTech.resourceDomain, "icons/NeutronActivator_On");
    public static IIconContainer OVERLAY_FRONT_NEUTRON_ACTIVATOR_ACTIVE_GLOW = Textures.BlockIcons
        .custom(Mods.GregTech.resourceDomain, "icons/NeutronActivator_On_GLOW");

}
