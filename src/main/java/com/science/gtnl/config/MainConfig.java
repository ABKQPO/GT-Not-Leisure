package com.science.gtnl.config;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraftforge.common.config.Configuration;

// spotless:off
public class MainConfig {

    // --- Main Configuration Categories ---
    public static final String CATEGORY_GTNL_CONFIG = "gtnl_config";

    // --- Sub-Categories (direct children of gtnl_config, or deeper nested) ---
    public static final String CATEGORY_MACHINE = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER + "machine";
    public static final String CATEGORY_RE_AVARITIA = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER
        + "re_avaritia";
    public static final String CATEGORY_BLOOD_MAGIC = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER
        + "blood_magic";
    public static final String CATEGORY_RECIPE = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER + "recipe";
    public static final String CATEGORY_TICK_RATE = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER + "tickrate";
    public static final String CATEGORY_PLAYER_DOLL = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER
        + "player_doll";
    public static final String CATEGORY_NOT_ENOUGH_ITEMS = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER
        + "not_enough_items";
    public static final String CATEGORY_STICK = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER + "stick";
    public static final String CATEGORY_SUPER_CREEPER = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER
        + "super_creeper";
    public static final String CATEGORY_MESSAGE = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER + "message";
    public static final String CATEGORY_OTHER = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER + "other";
    public static final String CATEGORY_DEBUG = CATEGORY_GTNL_CONFIG + Configuration.CATEGORY_SPLITTER + "debug";

    // --- Deeper Nested Categories (paths constructed using CATEGORY_SPLITTER) ---
    public static final String SUB_CATEGORY_METEOR_MINER = CATEGORY_MACHINE + Configuration.CATEGORY_SPLITTER
        + "meteor_miner";
    public static final String SUB_CATEGORY_ARTIFICIAL_STAR = CATEGORY_MACHINE + Configuration.CATEGORY_SPLITTER
        + "artificial_star";
    public static final String SUB_CATEGORY_ETERNAL_GREGTECH_WORKSHOP = CATEGORY_MACHINE
        + Configuration.CATEGORY_SPLITTER
        + "eternal_gregtech_workshop";
    public static final String SUB_CATEGORY_PORTAL_TO_ALFHEIM = CATEGORY_MACHINE + Configuration.CATEGORY_SPLITTER
        + "portal_to_alfheim";
    public static final String SUB_CATEGORY_INFINITY_SWORD = CATEGORY_RE_AVARITIA + Configuration.CATEGORY_SPLITTER
        + "infinity_sword";
    public static final String SUB_CATEGORY_CHRONARCHS_CLOCK = CATEGORY_RE_AVARITIA + Configuration.CATEGORY_SPLITTER
        + "chronarch_clock";
    public static final String SUB_CATEGORY_METEOR = CATEGORY_BLOOD_MAGIC + Configuration.CATEGORY_SPLITTER + "meteor";

    // Machine
    public static boolean enableRecipeOutputChance = true;
    public static boolean enableMachineAmpLimit = true;
    public static double recipeOutputChance = 2.5;

    public static int meteorMinerMaxBlockPerCycle = 1;
    public static int meteorMinerMaxRowPerCycle = 1;

    public static int euEveryEnhancementCore = 100;
    public static int euEveryDepletedExcitedNaquadahFuelRod = 4125000;
    public static double secondsOfArtificialStarProgressCycleTime = 6.4;
    public static boolean enableRenderDefaultArtificialStar = true;

    public static boolean enablePortalToAlfheimBigBoom = true;
    public static boolean enableEternalGregTechWorkshopSpiralRender = false;
    public static boolean enableVoidMinerTweak = true;
    public static boolean enableIntegratedOreFactoryChange = true;
    public static boolean enablePurificationPlantBuff = false;

    // Recipe
    public static boolean enableDeleteRecipe = true;
    public static boolean enableShowDelRecipeTitle = true;
    public static boolean enableSomethingRecipe = true;
    public static boolean enableAssemblingLineRecipesTimeChange = false;
    public static boolean enableChamberRecipesBuff = true;

    // Tickrate
    public static float defaultTickrate = 20.0f;
    public static float minTickrate = 0.1f;
    public static float maxTickrate = 1000f;
    public static boolean showTickrateMessages = true;

    // Player Doll
    public static boolean enableCustomPlayerDoll = true;
    public static boolean enableRegisterPlayerDollGlass = true;

    // Extreme Anvil
    public static String unbreakOre = "neutronUnbreak";

    // Infinity Sword
    public static boolean enableInfinitySwordBypassMechanism = true;
    public static boolean enableInfinitySwordExplosion = true;
    public static boolean enableRenderInfinitySwordSpecial = true;

    // Chronarch's Clock
    public static int chronarchsClockRadius = 3;
    public static int chronarchsClockSpeedMultiplier = 256;
    public static int chronarchsClockDurationTicks = 200;
    public static int chronarchsClockCooldown = 600;

    // Vein Mining Pickaxe
    public static int veinMiningPickaxeMaxRange = 32;
    public static int veinMiningPickaxeMaxAmount = 327670;

    // Stick
    public static boolean enableStickItem = true;

    // NotEnoughItems
    public static boolean enableSpecialCheatIcon = false;
    public static int specialIconType = 0;

    // SuperCreeper
    public static List<String> targetBlockSpecs = new CopyOnWriteArrayList<>();
    public static String[] defaultTargetBlocks = { "minecraft:chest", "appliedenergistics2:tile.BlockDrive",
        "gregtech:gt.blockmachines", "appliedenergistics2:tile.BlockController" };
    public static boolean enableSuperCreeper = false;
    public static int blockTargetInterval = 30;
    public static int playerTargetInterval = 10;
    public static int blockFindRadius = 16;
    public static int playerFindRadius = 16;
    public static int explosionPower = 3;
    public static double moveSpeed = 1.0;
    public static double explosionTriggerRange = 2.5;
    public static double creeperSpeedBonusScale = 3.0;
    public static boolean enableCreeperBurningExplosion = true;
    public static int burningExplosionTimer = 30;
    public static boolean enableCreeperIgnitedDeathExplosion = true;
    public static boolean enableCreeperHurtByCreeperExplosion = true;
    public static boolean enableCreeperKilledByCreeperExplosion = true;
    public static boolean enableCreeperFindSpider = true;
    public static boolean allowCreeperExplosionBypassGamerule = false;
    public static double spiderMoveSpeed = 1.3;
    public static int spiderFindRadius = 16;
    public static int spiderTargetInterval = 20;

    // Effect
    public static int aweEffectID = 80;
    public static int perfectPhysiqueEffect = 81;
    public static int shimmerEffect = 82;
    public static int ghostlyShapeEffect = 83;
    public static int battleEffect = 84;

    // Steam Rocket
    public static int idSchematicRocketSteam = 114514;
    public static final int NASA_WORKBENCH_STEAM_ROCKET = 114514;

    // Other
    public static boolean enableSaturationHeal = true;
    public static boolean enableDeathIncompleteMessage = true;

    // Message
    public static boolean enableShowJoinMessage = true;
    public static boolean enableShowAddMods = true;

    // Debug
    public static boolean enableDebugMode = false;

    public static boolean enableAprilFool = false;

    public static Configuration config;

    static {
        File configDir = new File("config/GTNotLeisure");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File mainConfigFile = new File(configDir, "main.cfg");

        if (config == null) {
            config = new Configuration(mainConfigFile);
            loadConfig();
        }

        LocalDate today = LocalDate.now();
        if (today.getMonthValue() == 4 && today.getDayOfMonth() == 1) {
            enableAprilFool = true;
        }

    }

    public static void reloadConfig() {
        if (config != null) {
            if (config.hasChanged()) {
                config.save();
            }
            if (targetBlockSpecs != null) targetBlockSpecs.clear();
            config.load();
            loadConfig();
            targetBlockSpecs.addAll(Arrays.asList(defaultTargetBlocks));
        }
    }

    public static void loadConfig() {

        // Machine
        enableRecipeOutputChance = config
            .get(
                CATEGORY_MACHINE,
                "enableRecipeOutputChance",
                enableRecipeOutputChance,
                "Enable Output Change Function")
            .getBoolean(enableRecipeOutputChance);

        enableMachineAmpLimit = config
            .get(CATEGORY_MACHINE, "enableLaserHatch", enableMachineAmpLimit, "Enable Machine Can't Use Laser Hatch")
            .getBoolean(enableMachineAmpLimit);

        recipeOutputChance = config
            .get(CATEGORY_MACHINE, "RecipeChanceOutput", recipeOutputChance, "Change Recipe Item Output, like QFT")
            .getDouble(recipeOutputChance);

        meteorMinerMaxBlockPerCycle = config
            .get(
                SUB_CATEGORY_METEOR_MINER,
                "MaxBlockCount",
                meteorMinerMaxBlockPerCycle,
                "Set the Meteor Miner how many every cycle break a block")
            .getInt(meteorMinerMaxBlockPerCycle);

        meteorMinerMaxRowPerCycle = config
            .get(
                SUB_CATEGORY_METEOR_MINER,
                "MaxRawCount",
                meteorMinerMaxRowPerCycle,
                "Set the Meteor Miner how many every cycle break row blocks")
            .getInt(meteorMinerMaxRowPerCycle);

        euEveryEnhancementCore = config
            .get(
                SUB_CATEGORY_ARTIFICIAL_STAR,
                "EUEveryEnhancementCore",
                euEveryEnhancementCore,
                "Set the power generation of EU Every Enhancement Core")
            .getInt(euEveryEnhancementCore);

        euEveryDepletedExcitedNaquadahFuelRod = config
            .get(
                SUB_CATEGORY_ARTIFICIAL_STAR,
                "EUEveryDepletedExcitedNaquadahFuelRod",
                euEveryDepletedExcitedNaquadahFuelRod,
                "Set the power generation of EU Every Depleted Excited Naquadah FuelRod")
            .getInt(euEveryDepletedExcitedNaquadahFuelRod);

        secondsOfArtificialStarProgressCycleTime = config
            .get(
                SUB_CATEGORY_ARTIFICIAL_STAR,
                "secondsOfArtificialStarProgressCycleTime",
                secondsOfArtificialStarProgressCycleTime,
                "Set secondsOfArtificialStarProgressCycleTime running time")
            .getDouble(secondsOfArtificialStarProgressCycleTime);

        enableRenderDefaultArtificialStar = config
            .get(
                SUB_CATEGORY_ARTIFICIAL_STAR,
                "EnableDefaultRender",
                enableRenderDefaultArtificialStar,
                "Open RenderDefaultArtificialStar rendering")
            .getBoolean(enableRenderDefaultArtificialStar);

        enableEternalGregTechWorkshopSpiralRender = config
            .get(
                SUB_CATEGORY_ETERNAL_GREGTECH_WORKSHOP,
                "spiralRender",
                enableEternalGregTechWorkshopSpiralRender,
                "Enable Eternal GregTech Workshop Spiral Render, like DNA")
            .getBoolean(enableEternalGregTechWorkshopSpiralRender);

        enablePortalToAlfheimBigBoom = config
            .get(
                SUB_CATEGORY_PORTAL_TO_ALFHEIM,
                "bigBoom",
                enablePortalToAlfheimBigBoom,
                "Setting this to false will reduce the Portal To Alfheim explosion to little more then a tnt blast")
            .getBoolean(enablePortalToAlfheimBigBoom);

        enableVoidMinerTweak = config
            .get(
                CATEGORY_MACHINE,
                "enableVoidMinerTweak",
                enableVoidMinerTweak,
                "Enable Void Miner Tweak, allows you to override target dimension that Void Miner mines")
            .getBoolean(enableVoidMinerTweak);

        enableIntegratedOreFactoryChange = config
            .get(
                CATEGORY_MACHINE,
                "enableIntegratedOreFactoryChange",
                enableIntegratedOreFactoryChange,
                "Enable Integrated Ore Factory Change, change parallel to 65536 and can use Laser Energy Hatch")
            .getBoolean(enableIntegratedOreFactoryChange);

        enablePurificationPlantBuff = config
            .get(
                CATEGORY_MACHINE,
                "enablePurificationPlantBuff",
                enablePurificationPlantBuff,
                "Enable Purification Plant Buff, add Wireless mode and max long parallels")
            .getBoolean(enablePurificationPlantBuff);

        // Recipe
        enableSomethingRecipe = config
            .get(CATEGORY_RECIPE, "enableSomethingRecipe", enableSomethingRecipe, "Enable Something Cheap Recipe")
            .getBoolean(enableSomethingRecipe);

        enableDeleteRecipe = config
            .get(CATEGORY_RECIPE, "enableDeleteRecipe", enableDeleteRecipe, "Enable Delete Recipe")
            .getBoolean(enableDeleteRecipe);

        enableSomethingRecipe = config
            .get(CATEGORY_RECIPE, "enableSomethingRecipe", enableSomethingRecipe, "Enable Something Cheap Recipe")
            .getBoolean(enableSomethingRecipe);

        enableAssemblingLineRecipesTimeChange = config
            .get(
                CATEGORY_RECIPE,
                "enableAssemblingLineRecipesTimeChange",
                enableAssemblingLineRecipesTimeChange,
                "Significantly reduce the time required for crafting recipes")
            .getBoolean(enableAssemblingLineRecipesTimeChange);

        enableChamberRecipesBuff = config
            .get(CATEGORY_RECIPE, "enableChamberRecipesBuff", enableChamberRecipesBuff, "Buff all chamber recipes")
            .getBoolean(enableChamberRecipesBuff);

        enableShowDelRecipeTitle = config
            .get(
                CATEGORY_RECIPE,
                "enableShowDelRecipeTitle",
                enableShowDelRecipeTitle,
                "Enable when player join world, Show Big Title")
            .getBoolean(enableShowDelRecipeTitle);

        // Tick Rate
        defaultTickrate = (float) config
            .get(
                CATEGORY_TICK_RATE,
                "Default",
                defaultTickrate,
                "Default tickrate. The game will always initialize with this value.")
            .getDouble(defaultTickrate);

        minTickrate = (float) config
            .get(
                CATEGORY_TICK_RATE,
                "Minimum",
                minTickrate,
                "Minimum tickrate from servers. Prevents really low tickrate values.")
            .getDouble(minTickrate);

        maxTickrate = (float) config
            .get(
                CATEGORY_TICK_RATE,
                "Maximum",
                maxTickrate,
                "Maximum tickrate from servers. Prevents really high tickrate values.")
            .getDouble(maxTickrate);

        showTickrateMessages = config
            .get(
                CATEGORY_TICK_RATE,
                "show-messages",
                showTickrateMessages,
                "If it will show log messages in the console and the game")
            .getBoolean(showTickrateMessages);

        // Player Doll
        enableCustomPlayerDoll = config
            .get(
                CATEGORY_PLAYER_DOLL,
                "enableCustomSkin",
                enableCustomPlayerDoll,
                "Enable Custom Player Skin for Player Doll")
            .getBoolean(enableCustomPlayerDoll);

        enableRegisterPlayerDollGlass = config
            .get(
                CATEGORY_PLAYER_DOLL,
                "enableRegisterMAXGlass",
                enableRegisterPlayerDollGlass,
                "Make Play Doll to MAX Tier Glass")
            .getBoolean(enableRegisterPlayerDollGlass);

        // Infinity Sword
        enableInfinitySwordBypassMechanism = config
            .get(
                SUB_CATEGORY_INFINITY_SWORD,
                "enableBypass",
                enableInfinitySwordBypassMechanism,
                "Enable Infinity Sword bypass against Blood Sword and Draconic Armor")
            .getBoolean(enableInfinitySwordBypassMechanism);

        enableInfinitySwordExplosion = config
            .get(
                SUB_CATEGORY_INFINITY_SWORD,
                "enableExplosion",
                enableInfinitySwordExplosion,
                "Enable when Infinity Sword hit Infinity Suit create Explosion")
            .getBoolean(enableInfinitySwordExplosion);

        enableRenderInfinitySwordSpecial = config
            .get(
                SUB_CATEGORY_INFINITY_SWORD,
                "enableSpecialRender",
                enableRenderInfinitySwordSpecial,
                "Enable Player Special Render")
            .getBoolean(enableRenderInfinitySwordSpecial);

        // Chronarch's Clock
        chronarchsClockRadius = config
            .get(SUB_CATEGORY_CHRONARCHS_CLOCK, "radius", chronarchsClockRadius, "Effective radius in blocks")
            .getInt(chronarchsClockRadius);

        chronarchsClockSpeedMultiplier = config
            .get(
                SUB_CATEGORY_CHRONARCHS_CLOCK,
                "speedMultiplier",
                chronarchsClockSpeedMultiplier,
                "Speed multiplier for the clock")
            .getInt(chronarchsClockSpeedMultiplier);

        chronarchsClockDurationTicks = config
            .get(
                SUB_CATEGORY_CHRONARCHS_CLOCK,
                "durationTicks",
                chronarchsClockDurationTicks,
                "Duration of the clock's effect in ticks")
            .getInt(chronarchsClockDurationTicks);

        chronarchsClockCooldown = config
            .get(SUB_CATEGORY_CHRONARCHS_CLOCK, "Cooldown", chronarchsClockCooldown, "Change Chronarchs Clock Cooldown")
            .getInt(chronarchsClockCooldown);

        // Vein Mining Pickaxe
        veinMiningPickaxeMaxRange = config
            .get(
                SUB_CATEGORY_CHRONARCHS_CLOCK,
                "veinMiningPickaxeMaxRange",
                veinMiningPickaxeMaxRange,
                "Set maximum block distance for Vein Mining Pickaxe")
            .getInt(veinMiningPickaxeMaxRange);

        veinMiningPickaxeMaxAmount = config
            .get(
                SUB_CATEGORY_CHRONARCHS_CLOCK,
                "veinMiningPickaxeMaxAmount",
                veinMiningPickaxeMaxAmount,
                "Set maximum number of chained blocks for Vein Mining Pickaxe")
            .getInt(veinMiningPickaxeMaxAmount);

        // Stick
        enableStickItem = config
            .get(CATEGORY_STICK, "enableStickItem", enableStickItem, "Enable stick fake item and block")
            .getBoolean(enableStickItem);

        // Not Enough Items
        enableSpecialCheatIcon = config
            .get(
                CATEGORY_NOT_ENOUGH_ITEMS,
                "enableSpecialCheatIcon",
                enableSpecialCheatIcon,
                "Enable a special icon for cheat mode")
            .getBoolean(enableSpecialCheatIcon);

        specialIconType = config
            .get(
                CATEGORY_NOT_ENOUGH_ITEMS,
                "specialIconType",
                specialIconType,
                "Specify the type of the special cheat icon")
            .getInt(specialIconType);

        // Super Creeper
        defaultTargetBlocks = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "defaultTargetBlocks",
                defaultTargetBlocks,
                "List of target block IDs. Format: 'modid:blockname' or 'modid:blockname:meta'.")
            .getStringList();

        enableSuperCreeper = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "enableSuperCreeper",
                enableSuperCreeper,
                "Enable super creeper, can find you chest and more")
            .getBoolean(enableSuperCreeper);

        blockTargetInterval = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "blockTargetInterval",
                blockTargetInterval,
                "Interval in ticks between block targeting scans")
            .getInt(blockTargetInterval);

        playerTargetInterval = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "playerTargetInterval",
                playerTargetInterval,
                "Interval in ticks between player targeting scans")
            .getInt(playerTargetInterval);

        blockFindRadius = config
            .get(CATEGORY_SUPER_CREEPER, "blockFindRadius", blockFindRadius, "Search radius for blocks")
            .getInt(blockFindRadius);

        playerFindRadius = config
            .get(CATEGORY_SUPER_CREEPER, "playerFindRadius", playerFindRadius, "Search radius for players")
            .getInt(playerFindRadius);

        explosionPower = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "explosionPower",
                explosionPower,
                "Power of the explosion caused by Super Creeper")
            .getInt(explosionPower);

        moveSpeed = config
            .get(CATEGORY_SUPER_CREEPER, "MoveSpeed", moveSpeed, "Movement speed multiplier for Super Creeper")
            .getDouble(moveSpeed);

        creeperSpeedBonusScale = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "creeperSpeedBonusScale",
                creeperSpeedBonusScale,
                "The higher this value, the faster the creeper moves as it gets closer to its target")
            .getDouble(creeperSpeedBonusScale);

        explosionTriggerRange = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "explosionTriggerRange",
                explosionTriggerRange,
                "Distance at which the Super Creeper will trigger its explosion.")
            .getDouble(explosionTriggerRange);

        enableCreeperBurningExplosion = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "enableCreeperBurningExplosion",
                enableCreeperBurningExplosion,
                "Enable Creeper explosion when burning.")
            .getBoolean(enableCreeperBurningExplosion);

        enableCreeperIgnitedDeathExplosion = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "enableCreeperIgnitedDeathExplosion",
                enableCreeperIgnitedDeathExplosion,
                "Enable creeper to explode immediately when killed while already ignited.")
            .getBoolean(enableCreeperIgnitedDeathExplosion);

        enableCreeperHurtByCreeperExplosion = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "enableCreeperHurtByCreeperExplosion",
                enableCreeperHurtByCreeperExplosion,
                "Enable creeper explosion reaction when damaged by another creeper (not only when killed).")
            .getBoolean(enableCreeperHurtByCreeperExplosion);

        enableCreeperKilledByCreeperExplosion = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "enableCreeperKilledByCreeperExplosion",
                enableCreeperKilledByCreeperExplosion,
                "Enable Creeper explosion when killed by another Creeper.")
            .getBoolean(enableCreeperKilledByCreeperExplosion);

        enableCreeperFindSpider = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "enableCreeperFindSpider",
                enableCreeperFindSpider,
                "Enable Creeper to find and mount nearby Spiders.")
            .getBoolean(enableCreeperFindSpider);

        allowCreeperExplosionBypassGamerule = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "allowCreeperExplosionBypassGamerule",
                allowCreeperExplosionBypassGamerule,
                "Allow creeper explosions to ignore mobGriefing gamerule and always break blocks")
            .getBoolean(allowCreeperExplosionBypassGamerule);

        spiderMoveSpeed = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "spiderMoveSpeed",
                spiderMoveSpeed,
                "Spider move speed when mounted by a Creeper.")
            .getDouble(spiderMoveSpeed);

        spiderFindRadius = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "spiderFindRadius",
                spiderFindRadius,
                "Radius in which Creepers search for Spiders.")
            .getInt(spiderFindRadius);

        spiderTargetInterval = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "spiderTargetInterval",
                spiderTargetInterval,
                "Interval (in ticks) between Creeper spider targeting attempts.")
            .getInt(spiderTargetInterval);

        burningExplosionTimer = config
            .get(
                CATEGORY_SUPER_CREEPER,
                "burningExplosionTimer",
                burningExplosionTimer,
                "Ticks before Creeper explodes due to burning.")
            .getInt(burningExplosionTimer);

        // Other
        enableSaturationHeal = config.get(
            CATEGORY_OTHER,
            "enableSaturationHeal",
            enableSaturationHeal,
            "Enable Saturation Heal Tweak. When hunger is 20, player regenerates health based on saturation / 6, up to 1 HP per 0.5s")
            .getBoolean(enableSaturationHeal);

        enableDeathIncompleteMessage = config
            .get(
                CATEGORY_OTHER,
                "enableDeathIncompleteMessage",
                enableDeathIncompleteMessage,
                "Enable sending the unfinished chat message when player dies")
            .getBoolean(enableDeathIncompleteMessage);

        // Message
        enableShowJoinMessage = config
            .get(
                CATEGORY_MESSAGE,
                "enableShowJoinMessage",
                enableShowJoinMessage,
                "Enable welcome message when player joins")
            .getBoolean(enableShowJoinMessage);

        enableShowAddMods = config
            .get(CATEGORY_MESSAGE, "enableShowAddMods", enableShowAddMods, "Enable showing loaded mods info to player")
            .getBoolean(enableShowAddMods);

        // Debug
        enableDebugMode = config.get(CATEGORY_DEBUG, "enableDebugMode", enableDebugMode, "Enable Debug Print Log")
            .getBoolean(enableDebugMode);

        if (config.hasChanged()) {
            config.save();
        }
    }
}
