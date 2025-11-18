package com.science.gtnl.config;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraftforge.common.config.Configuration;

public class MainConfig {

    public static final String CATEGORY_MACHINE = "machine";
    public static final String CATEGORY_RE_AVARITIA = "re_avaritia";
    public static final String CATEGORY_RECIPE = "recipe";
    public static final String CATEGORY_TICK_RATE = "tickrate";
    public static final String CATEGORY_SUPER_CREEPER = "super_creeper";
    public static final String CATEGORY_MESSAGE = "message";
    public static final String CATEGORY_OTHER = "other";
    public static final String CATEGORY_ITEM = "item";
    public static final String CATEGORY_DEBUG = "debug";

    // --- Sub-Categories (direct children of gtnl_config, or deeper nested) ---
    public static final String SUB_CATEGORY_QUANTUM_COMPUTER = CATEGORY_MACHINE + Configuration.CATEGORY_SPLITTER
        + "quantum_computer";
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
    public static final String SUB_CATEGORY_VEIN_MINER_PICKAXE = CATEGORY_ITEM + Configuration.CATEGORY_SPLITTER
        + "vein_miner_pickaxe";
    public static final String SUB_CATEGORY_STICK = CATEGORY_ITEM + Configuration.CATEGORY_SPLITTER + "stick";
    public static final String SUB_CATEGORY_PLAYER_DOLL = CATEGORY_ITEM + Configuration.CATEGORY_SPLITTER
        + "player_doll";
    public static final String SUB_CATEGORY_NOT_ENOUGH_ITEMS = CATEGORY_OTHER + Configuration.CATEGORY_SPLITTER
        + "not_enough_items";

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

    // Quantum Computer
    public static int quantumComputerMaximumMultiblockSize = 15;
    public static int quantumComputerMaximumQuantumDataEntangler = 1;
    public static int quantumComputerMaximumQuantumComputerMultiThreader = 1;
    public static int quantumComputerMaximumQuantumComputerCore = 1;

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
        config.setCategoryLanguageKey(CATEGORY_MACHINE, "gtnl_config." + CATEGORY_MACHINE);
        config.setCategoryLanguageKey(CATEGORY_RE_AVARITIA, "gtnl_config." + CATEGORY_RE_AVARITIA);
        config.setCategoryLanguageKey(CATEGORY_RECIPE, "gtnl_config." + CATEGORY_RECIPE);
        config.setCategoryLanguageKey(CATEGORY_TICK_RATE, "gtnl_config." + CATEGORY_TICK_RATE);
        config.setCategoryLanguageKey(CATEGORY_SUPER_CREEPER, "gtnl_config." + CATEGORY_SUPER_CREEPER);
        config.setCategoryLanguageKey(CATEGORY_MESSAGE, "gtnl_config." + CATEGORY_MESSAGE);
        config.setCategoryLanguageKey(CATEGORY_OTHER, "gtnl_config." + CATEGORY_OTHER);
        config.setCategoryLanguageKey(CATEGORY_ITEM, "gtnl_config." + CATEGORY_ITEM);
        config.setCategoryLanguageKey(CATEGORY_DEBUG, "gtnl_config." + CATEGORY_DEBUG);

        config.setCategoryLanguageKey(SUB_CATEGORY_QUANTUM_COMPUTER, "gtnl_config." + SUB_CATEGORY_QUANTUM_COMPUTER);
        config.setCategoryLanguageKey(SUB_CATEGORY_METEOR_MINER, "gtnl_config." + SUB_CATEGORY_METEOR_MINER);
        config.setCategoryLanguageKey(SUB_CATEGORY_ARTIFICIAL_STAR, "gtnl_config." + SUB_CATEGORY_ARTIFICIAL_STAR);
        config.setCategoryLanguageKey(
            SUB_CATEGORY_ETERNAL_GREGTECH_WORKSHOP,
            "gtnl_config." + SUB_CATEGORY_ETERNAL_GREGTECH_WORKSHOP);
        config.setCategoryLanguageKey(SUB_CATEGORY_PORTAL_TO_ALFHEIM, "gtnl_config." + SUB_CATEGORY_PORTAL_TO_ALFHEIM);
        config.setCategoryLanguageKey(SUB_CATEGORY_INFINITY_SWORD, "gtnl_config." + SUB_CATEGORY_INFINITY_SWORD);
        config.setCategoryLanguageKey(SUB_CATEGORY_CHRONARCHS_CLOCK, "gtnl_config." + SUB_CATEGORY_CHRONARCHS_CLOCK);
        config
            .setCategoryLanguageKey(SUB_CATEGORY_VEIN_MINER_PICKAXE, "gtnl_config." + SUB_CATEGORY_VEIN_MINER_PICKAXE);
        config.setCategoryLanguageKey(SUB_CATEGORY_STICK, "gtnl_config." + SUB_CATEGORY_STICK);
        config.setCategoryLanguageKey(SUB_CATEGORY_PLAYER_DOLL, "gtnl_config." + SUB_CATEGORY_PLAYER_DOLL);
        config.setCategoryLanguageKey(SUB_CATEGORY_NOT_ENOUGH_ITEMS, "gtnl_config." + SUB_CATEGORY_NOT_ENOUGH_ITEMS);

        // Machine
        enableRecipeOutputChance = config.getBoolean(
            "enableRecipeOutputChance",
            CATEGORY_MACHINE,
            enableRecipeOutputChance,
            "Enable Output Change Function",
            "config.machine.enableRecipeOutputChance");

        enableMachineAmpLimit = config.getBoolean(
            "enableLaserHatch",
            CATEGORY_MACHINE,
            enableMachineAmpLimit,
            "Enable Machine Can't Use Laser Hatch",
            "config.machine.enableLaserHatch");

        recipeOutputChance = config.getFloat(
            "recipeOutputChance",
            CATEGORY_MACHINE,
            (float) recipeOutputChance,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            "Change Recipe Item Output, like QFT",
            "config.machine.recipeOutputChance");

        meteorMinerMaxBlockPerCycle = config.getInt(
            "meteorMinerMaxBlockPerCycle",
            SUB_CATEGORY_METEOR_MINER,
            meteorMinerMaxBlockPerCycle,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Set the Meteor Miner how many every cycle break a block",
            "config.meteor_miner.meteorMinerMaxBlockPerCycle");

        meteorMinerMaxRowPerCycle = config.getInt(
            "meteorMinerMaxRowPerCycle",
            SUB_CATEGORY_METEOR_MINER,
            meteorMinerMaxRowPerCycle,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Set the Meteor Miner how many every cycle break row blocks",
            "config.meteor_miner.meteorMinerMaxRowPerCycle");

        euEveryEnhancementCore = config.getInt(
            "euEveryEnhancementCore",
            SUB_CATEGORY_ARTIFICIAL_STAR,
            euEveryEnhancementCore,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Set the power generation of EU Every Enhancement Core",
            "config.artificial_star.euEveryEnhancementCore");

        euEveryDepletedExcitedNaquadahFuelRod = config.getInt(
            "EUEveryDepletedExcitedNaquadahFuelRod",
            SUB_CATEGORY_ARTIFICIAL_STAR,
            euEveryDepletedExcitedNaquadahFuelRod,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Set the power generation of EU Every Depleted Excited Naquadah FuelRod",
            "config.artificial_star.euEveryDepletedExcitedNaquadahFuelRod");

        secondsOfArtificialStarProgressCycleTime = config.getFloat(
            "secondsOfArtificialStarProgressCycleTime",
            SUB_CATEGORY_ARTIFICIAL_STAR,
            (float) secondsOfArtificialStarProgressCycleTime,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            "Set secondsOfArtificialStarProgressCycleTime running time",
            "config.artificial_star.secondsOfArtificialStarProgressCycleTime");

        enableRenderDefaultArtificialStar = config.getBoolean(
            "EnableDefaultRender",
            SUB_CATEGORY_ARTIFICIAL_STAR,
            enableRenderDefaultArtificialStar,
            "Open RenderDefaultArtificialStar rendering",
            "config.artificial_star.enableRenderDefaultArtificialStar");

        enableEternalGregTechWorkshopSpiralRender = config.getBoolean(
            "spiralRender",
            SUB_CATEGORY_ETERNAL_GREGTECH_WORKSHOP,
            enableEternalGregTechWorkshopSpiralRender,
            "Enable Eternal GregTech Workshop Spiral Render, like DNA",
            "config.eternal_gregtech_workshop.spiralRender");

        enablePortalToAlfheimBigBoom = config.getBoolean(
            "bigBoom",
            SUB_CATEGORY_PORTAL_TO_ALFHEIM,
            enablePortalToAlfheimBigBoom,
            "Setting this to false will reduce the Portal To Alfheim explosion to little more then a tnt blast",
            "config.portal_to_alfheim.bigBoom");

        enableVoidMinerTweak = config.getBoolean(
            "enableVoidMinerTweak",
            CATEGORY_MACHINE,
            enableVoidMinerTweak,
            "Enable Void Miner Tweak, allows you to override target dimension that Void Miner mines",
            "config.machine.enableVoidMinerTweak");

        enableIntegratedOreFactoryChange = config.getBoolean(
            "enableIntegratedOreFactoryChange",
            CATEGORY_MACHINE,
            enableIntegratedOreFactoryChange,
            "Enable Integrated Ore Factory Change, change parallel to 65536 and can use Laser Energy Hatch",
            "config.machine.enableIntegratedOreFactoryChange");

        enablePurificationPlantBuff = config.getBoolean(
            "enablePurificationPlantBuff",
            CATEGORY_MACHINE,
            enablePurificationPlantBuff,
            "Enable Purification Plant Buff, add Wireless mode and max long parallels",
            "config.machine.enablePurificationPlantBuff");

        // Recipe
        enableSomethingRecipe = config.getBoolean(
            "enableSomethingRecipe",
            CATEGORY_RECIPE,
            enableSomethingRecipe,
            "Enable Something Cheap Recipe",
            "config.recipe.enableSomethingRecipe");

        enableDeleteRecipe = config.getBoolean(
            "enableDeleteRecipe",
            CATEGORY_RECIPE,
            enableDeleteRecipe,
            "Enable Delete Recipe",
            "config.recipe.enableDeleteRecipe");

        enableSomethingRecipe = config.getBoolean(
            "enableSomethingRecipe",
            CATEGORY_RECIPE,
            enableSomethingRecipe,
            "Enable Something Cheap Recipe",
            "config.recipe.enableSomethingRecipe");

        enableAssemblingLineRecipesTimeChange = config.getBoolean(
            "enableAssemblingLineRecipesTimeChange",
            CATEGORY_RECIPE,
            enableAssemblingLineRecipesTimeChange,
            "Significantly reduce the time required for crafting recipes",
            "config.recipe.enableAssemblingLineRecipesTimeChange");

        enableChamberRecipesBuff = config.getBoolean(
            "enableChamberRecipesBuff",
            CATEGORY_RECIPE,
            enableChamberRecipesBuff,
            "Buff all chamber recipes",
            "config.recipe.enableChamberRecipesBuff");

        enableShowDelRecipeTitle = config.getBoolean(
            "enableShowDelRecipeTitle",
            CATEGORY_RECIPE,
            enableShowDelRecipeTitle,
            "Enable when player join world, Show Big Title",
            "config.recipe.enableShowDelRecipeTitle");

        // Tick Rate
        defaultTickrate = config.getFloat(
            "defaultTickrate",
            CATEGORY_TICK_RATE,
            defaultTickrate,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            "Default tickrate. The game will always initialize with this value.",
            "config.tickrate.defaultTickrate");

        minTickrate = config.getFloat(
            "minTickrate",
            CATEGORY_TICK_RATE,
            minTickrate,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            "Minimum tickrate from servers. Prevents really low tickrate values.",
            "config.tickrate.minTickrate");

        maxTickrate = config.getFloat(
            "maxTickrate",
            CATEGORY_TICK_RATE,
            maxTickrate,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            "Maximum tickrate from servers. Prevents really high tickrate values.",
            "config.tickrate.maxTickrate");

        // Quantum Computer
        quantumComputerMaximumQuantumDataEntangler = config.getInt(
            "quantumComputerMaximumQuantumDataEntangler",
            SUB_CATEGORY_QUANTUM_COMPUTER,
            quantumComputerMaximumQuantumDataEntangler,
            1,
            Integer.MAX_VALUE,
            "Maximum number of data entangler allowed in a Quantum Computer",
            "config.quantum_computer.quantumComputerMaximumQuantumDataEntangler");

        quantumComputerMaximumQuantumComputerMultiThreader = config.getInt(
            "quantumComputerMaximumQuantumComputerMultiThreader",
            SUB_CATEGORY_QUANTUM_COMPUTER,
            quantumComputerMaximumQuantumComputerMultiThreader,
            1,
            Integer.MAX_VALUE,
            "Maximum number of multi threader allowed in a Quantum Computer",
            "config.quantum_computer.quantumComputerMaximumQuantumComputerMultiThreader");

        quantumComputerMaximumQuantumComputerCore = config.getInt(
            "quantumComputerMaximumQuantumComputerCore",
            SUB_CATEGORY_QUANTUM_COMPUTER,
            quantumComputerMaximumQuantumComputerCore,
            1,
            Integer.MAX_VALUE,
            "Maximum number of cores allowed in a Quantum Computer",
            "config.quantum_computer.quantumComputerMaximumQuantumComputerCore");

        quantumComputerMaximumMultiblockSize = config.getInt(
            "quantumComputerMaximumMultiblockSize",
            SUB_CATEGORY_QUANTUM_COMPUTER,
            quantumComputerMaximumMultiblockSize,
            3,
            Integer.MAX_VALUE,
            "Maximum size allowed for a Quantum Computer multiblock structure",
            "config.quantum_computer.quantumComputerMaximumMultiblockSize");

        // Player Doll
        enableCustomPlayerDoll = config.getBoolean(
            "enableCustomSkin",
            SUB_CATEGORY_PLAYER_DOLL,
            enableCustomPlayerDoll,
            "Enable Custom Player Skin for Player Doll",
            "config.player_doll.enableCustomPlayerDoll");

        enableRegisterPlayerDollGlass = config.getBoolean(
            "enableRegisterMAXGlass",
            SUB_CATEGORY_PLAYER_DOLL,
            enableRegisterPlayerDollGlass,
            "Make Play Doll to MAX Tier Glass",
            "config.player_doll.enableRegisterPlayerDollGlass");

        // Infinity Sword
        enableInfinitySwordBypassMechanism = config.getBoolean(
            "enableBypass",
            SUB_CATEGORY_INFINITY_SWORD,
            enableInfinitySwordBypassMechanism,
            "Enable Infinity Sword bypass against Blood Sword and Draconic Armor",
            "config.infinity_sword.enableInfinitySwordBypassMechanism");

        enableInfinitySwordExplosion = config.getBoolean(
            "enableExplosion",
            SUB_CATEGORY_INFINITY_SWORD,
            enableInfinitySwordExplosion,
            "Enable when Infinity Sword hit Infinity Suit create Explosion",
            "config.infinity_sword.enableInfinitySwordExplosion");

        enableRenderInfinitySwordSpecial = config.getBoolean(
            "enableSpecialRender",
            SUB_CATEGORY_INFINITY_SWORD,
            enableRenderInfinitySwordSpecial,
            "Enable Player Special Render",
            "config.infinity_sword.enableRenderInfinitySwordSpecial");

        // Chronarch's Clock
        chronarchsClockRadius = config.getInt(
            "chronarchsClockRadius",
            SUB_CATEGORY_CHRONARCHS_CLOCK,
            chronarchsClockRadius,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Effective radius in blocks",
            "config.chronarchsClockRadius");

        chronarchsClockSpeedMultiplier = config.getInt(
            "chronarchsClockSpeedMultiplier",
            SUB_CATEGORY_CHRONARCHS_CLOCK,
            chronarchsClockSpeedMultiplier,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Speed multiplier for the clock",
            "config.chronarchsClockSpeedMultiplier");

        chronarchsClockDurationTicks = config.getInt(
            "chronarchsClockDurationTicks",
            SUB_CATEGORY_CHRONARCHS_CLOCK,
            chronarchsClockDurationTicks,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Duration of the clock's effect in ticks",
            "config.chronarchsClockDurationTicks");

        chronarchsClockCooldown = config.getInt(
            "chronarchsClockCooldown",
            SUB_CATEGORY_CHRONARCHS_CLOCK,
            chronarchsClockCooldown,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Change Chronarchs Clock Cooldown",
            "config.chronarchsClockCooldown");

        // Vein Mining Pickaxe
        veinMiningPickaxeMaxRange = config.getInt(
            "veinMiningPickaxeMaxRange",
            SUB_CATEGORY_VEIN_MINER_PICKAXE,
            veinMiningPickaxeMaxRange,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Set maximum block distance for Vein Mining Pickaxe",
            "config.vein_miner_pickaxe.veinMiningPickaxeMaxRange");

        veinMiningPickaxeMaxAmount = config.getInt(
            "veinMiningPickaxeMaxAmount",
            SUB_CATEGORY_VEIN_MINER_PICKAXE,
            veinMiningPickaxeMaxAmount,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Set maximum number of chained blocks for Vein Mining Pickaxe",
            "config.vein_miner_pickaxe.veinMiningPickaxeMaxAmount");

        // Stick
        enableStickItem = config.getBoolean(
            "enableStickItem",
            SUB_CATEGORY_STICK,
            enableStickItem,
            "Enable stick fake item and block",
            "config.stick.enableStickItem");

        // Not Enough Items
        enableSpecialCheatIcon = config.getBoolean(
            "enableSpecialCheatIcon",
            SUB_CATEGORY_NOT_ENOUGH_ITEMS,
            enableSpecialCheatIcon,
            "Enable a special icon for cheat mode",
            "config.not_enough_items.enableSpecialCheatIcon");

        specialIconType = config.getInt(
            "specialIconType",
            SUB_CATEGORY_NOT_ENOUGH_ITEMS,
            specialIconType,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Specify the type of the special cheat icon",
            "config.not_enough_items.specialIconType");

        // Super Creeper
        defaultTargetBlocks = config.getStringList(
            "defaultTargetBlocks",
            CATEGORY_SUPER_CREEPER,
            defaultTargetBlocks,
            "List of target block IDs. Format: 'modid:blockname' or 'modid:blockname:meta'.",
            null,
            "config.super_creeper.defaultTargetBlocks");

        enableSuperCreeper = config.getBoolean(
            "enableSuperCreeper",
            CATEGORY_SUPER_CREEPER,
            enableSuperCreeper,
            "Enable super creeper, can find you chest and more",
            "config.super_creeper.enableSuperCreeper");

        blockTargetInterval = config.getInt(
            "blockTargetInterval",
            CATEGORY_SUPER_CREEPER,
            blockTargetInterval,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Interval in ticks between block targeting scans",
            "config.super_creeper.blockTargetInterval");

        playerTargetInterval = config.getInt(
            "playerTargetInterval",
            CATEGORY_SUPER_CREEPER,
            playerTargetInterval,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Interval in ticks between player targeting scans",
            "config.super_creeper.playerTargetInterval");

        blockFindRadius = config.getInt(
            "blockFindRadius",
            CATEGORY_SUPER_CREEPER,
            blockFindRadius,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Search radius for blocks",
            "config.super_creeper.blockFindRadius");

        playerFindRadius = config.getInt(
            "playerFindRadius",
            CATEGORY_SUPER_CREEPER,
            playerFindRadius,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Search radius for players",
            "config.super_creeper.playerFindRadius");

        explosionPower = config.getInt(
            "explosionPower",
            CATEGORY_SUPER_CREEPER,
            explosionPower,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Power of the explosion caused by Super Creeper",
            "config.super_creeper.explosionPower");

        moveSpeed = config.getFloat(
            "MoveSpeed",
            CATEGORY_SUPER_CREEPER,
            (float) moveSpeed,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            "Movement speed multiplier for Super Creeper",
            "config.super_creeper.moveSpeed");

        creeperSpeedBonusScale = config.getFloat(
            "creeperSpeedBonusScale",
            CATEGORY_SUPER_CREEPER,
            (float) creeperSpeedBonusScale,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            "The higher this value, the faster the creeper moves as it gets closer to its target",
            "config.super_creeper.creeperSpeedBonusScale");

        explosionTriggerRange = config.getFloat(
            "explosionTriggerRange",
            CATEGORY_SUPER_CREEPER,
            (float) explosionTriggerRange,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            "Distance at which the Super Creeper will trigger its explosion.",
            "config.super_creeper.explosionTriggerRange");

        enableCreeperBurningExplosion = config.getBoolean(
            "enableCreeperBurningExplosion",
            CATEGORY_SUPER_CREEPER,
            enableCreeperBurningExplosion,
            "Enable Creeper explosion when burning.",
            "config.super_creeper.enableCreeperBurningExplosion");

        enableCreeperIgnitedDeathExplosion = config.getBoolean(
            "enableCreeperIgnitedDeathExplosion",
            CATEGORY_SUPER_CREEPER,
            enableCreeperIgnitedDeathExplosion,
            "Enable creeper to explode immediately when killed while already ignited.",
            "config.super_creeper.enableCreeperIgnitedDeathExplosion");

        enableCreeperHurtByCreeperExplosion = config.getBoolean(
            "enableCreeperHurtByCreeperExplosion",
            CATEGORY_SUPER_CREEPER,
            enableCreeperHurtByCreeperExplosion,
            "Enable creeper explosion reaction when damaged by another creeper (not only when killed).",
            "config.super_creeper.enableCreeperHurtByCreeperExplosion");

        enableCreeperKilledByCreeperExplosion = config.getBoolean(
            "enableCreeperKilledByCreeperExplosion",
            CATEGORY_SUPER_CREEPER,
            enableCreeperKilledByCreeperExplosion,
            "Enable Creeper explosion when killed by another Creeper.",
            "config.super_creeper.enableCreeperKilledByCreeperExplosion");

        enableCreeperFindSpider = config.getBoolean(
            "enableCreeperFindSpider",
            CATEGORY_SUPER_CREEPER,
            enableCreeperFindSpider,
            "Enable Creeper to find and mount nearby Spiders.",
            "config.super_creeper.enableCreeperFindSpider");

        allowCreeperExplosionBypassGamerule = config.getBoolean(
            "allowCreeperExplosionBypassGamerule",
            CATEGORY_SUPER_CREEPER,
            allowCreeperExplosionBypassGamerule,
            "Allow creeper explosions to ignore mobGriefing gamerule and always break blocks",
            "config.super_creeper.allowCreeperExplosionBypassGamerule");

        spiderMoveSpeed = config.getFloat(
            "spiderMoveSpeed",
            CATEGORY_SUPER_CREEPER,
            (float) spiderMoveSpeed,
            Float.MIN_VALUE,
            Float.MAX_VALUE,
            "Spider move speed when mounted by a Creeper.",
            "config.super_creeper.spiderMoveSpeed");

        spiderFindRadius = config.getInt(
            "spiderFindRadius",
            CATEGORY_SUPER_CREEPER,
            spiderFindRadius,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Radius in which Creepers search for Spiders.",
            "config.super_creeper.spiderFindRadius");

        spiderTargetInterval = config.getInt(
            "spiderTargetInterval",
            CATEGORY_SUPER_CREEPER,
            spiderTargetInterval,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Interval (in ticks) between Creeper spider targeting attempts.",
            "config.super_creeper.spiderTargetInterval");

        burningExplosionTimer = config.getInt(
            "burningExplosionTimer",
            CATEGORY_SUPER_CREEPER,
            burningExplosionTimer,
            Integer.MIN_VALUE,
            Integer.MAX_VALUE,
            "Ticks before Creeper explodes due to burning.",
            "config.super_creeper.burningExplosionTimer");

        // Other
        enableSaturationHeal = config.getBoolean(
            "enableSaturationHeal",
            CATEGORY_OTHER,
            enableSaturationHeal,
            "Enable Saturation Heal Tweak. When hunger is 20, player regenerates health based on saturation / 6, up to 1 HP per 0.5s",
            "config.other.enableSaturationHeal");

        enableDeathIncompleteMessage = config.getBoolean(
            "enableDeathIncompleteMessage",
            CATEGORY_OTHER,
            enableDeathIncompleteMessage,
            "Enable sending the unfinished chat message when player dies",
            "config.other.enableDeathIncompleteMessage");

        // Message
        enableShowJoinMessage = config.getBoolean(
            "enableShowJoinMessage",
            CATEGORY_MESSAGE,
            enableShowJoinMessage,
            "Enable welcome message when player joins",
            "config.message.enableShowJoinMessage");

        enableShowAddMods = config.getBoolean(
            "enableShowAddMods",
            CATEGORY_MESSAGE,
            enableShowAddMods,
            "Enable showing loaded mods info to player",
            "config.message.enableShowAddMods");

        // Debug
        enableDebugMode = config.getBoolean(
            "enableDebugMode",
            CATEGORY_DEBUG,
            enableDebugMode,
            "Enable Debug Print Log",
            "config.debug.enableDebugMode");

        if (config.hasChanged()) {
            config.save();
        }
    }
}
