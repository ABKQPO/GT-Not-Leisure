package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultDouble;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultFloat;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultString;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultStringList;
import com.gtnewhorizon.gtnhlib.config.Config.Name;
import com.gtnewhorizon.gtnhlib.config.Config.RangeInt;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;
import com.science.gtnl.ScienceNotLeisure;

@Config(modid = ScienceNotLeisure.MODID, configSubDirectory = "GTNotLeisure", filename = "GTNotLeisure")
@Config.LangKeyPattern(pattern = "gtnl.gui.config.%cat.%field", fullyQualified = true)
@Comment("General section")
public class MainConfig {

    public static final Debug debug = new Debug();

    @Comment("Debug section")
    public static class Debug {

        @Comment("Enable Debug Print Log")
        @DefaultBoolean(false)
        @RequiresMcRestart
        public boolean enableDebugMode = false;

        @Sync
        @Comment("Enable GTNL Quest, add more quest")
        @DefaultBoolean(false)
        @RequiresMcRestart
        public boolean enableQuest = false;
    }

    public static final Effect effect = new Effect();

    @Sync
    @Comment("Effect section")
    public static class Effect {

        @Comment("When the player dies and respawns, give the player Ghost Shape effect")
        @DefaultBoolean(true)
        public boolean enableGhostlyShape = true;
    }

    public static final Item item = new Item();

    @Sync
    @Comment("Item section")
    public static class Item {

        @Name("player_doll")
        public final PlayerDoll player_doll = new PlayerDoll();

        @Comment("Player Doll")
        public static class PlayerDoll {

            @Comment("Enable Custom Player Skin for Player Doll")
            @DefaultBoolean(true)
            @RequiresMcRestart
            public boolean enableCustomSkin = true;

            @Comment("Make Play Doll to MAX Tier Glass")
            @DefaultBoolean(true)
            @RequiresMcRestart
            public boolean enableRegisterMAXTierGlass = true;
        }

        @Name("vein_miner_pickaxe")
        public final VeinMinerPickaxe vein_miner_pickaxe = new VeinMinerPickaxe();

        @Comment("Vein Miner Pickaxe")
        public static class VeinMinerPickaxe {

            @Comment("Set maximum number of chained blocks for Vein Mining Pickaxe")
            @DefaultInt(327670)
            public int maxAmount = 327670;

            @Comment("Set maximum block distance for Vein Mining Pickaxe")
            @DefaultInt(32)
            public int maxRange = 32;
        }

        @Name("stick")
        public final Stick stick = new Stick();

        @Comment("Stick")
        public static class Stick {

            @Comment("Enable stick fake item and block")
            @DefaultBoolean(true)
            @RequiresMcRestart
            public boolean enableStickItem = true;
        }

        @Name("steam_rocket")
        public final SteamRocket steam_rocket = new SteamRocket();

        @Comment("Steam Rocket")
        public static class SteamRocket {

            @Comment("The Page ID for the Steam Rocket Schematic")
            @DefaultInt(114514)
            public int idSchematicRocketSteam = 114514;

            @Comment("The GUI ID for the NASA Workbench when using Steam Rocket Schematic")
            @DefaultInt(114514)
            public int nasaWorkbenchSteamRocket = 114514;
        }
    }

    public static final Machine machine = new Machine();

    @Sync
    @Comment("Machine section")
    public static class Machine {

        @Comment("Default energy consumption per operation (in AE/t)")
        @DefaultDouble(10)
        public double beamFormerEnergyConsume = 10;

        @Comment("Default maximum beam length (in blocks)")
        @DefaultInt(256)
        public int beamFormerLength = 256;

        @Comment("Allow interaction with machines from their main facing (similar to GTM behavior)")
        @DefaultBoolean(false)
        public boolean allowMainFacingInteraction = false;

        @Comment("Enhance the display of input hatch bus in interface terminal to support the machine current recipe map and virtual programming circuit")
        @DefaultBoolean(true)
        public boolean enableHatchInterfaceTerminalEnhance = true;

        @Comment("Enable Integrated Ore Factory Change, change parallel to 65536 and can use Laser Energy Hatch")
        @DefaultBoolean(true)
        @RequiresMcRestart
        public boolean enableIntegratedOreFactoryChange = true;

        @Comment("Set Machine Can Use Laser Hatch")
        @Name("enableLaserHatch")
        @DefaultBoolean(false)
        public boolean enableLaserHatch = false;

        @Comment("Enable Purification Plant Buff, add Wireless mode and max long parallels")
        @DefaultBoolean(false)
        @RequiresMcRestart
        public boolean enablePurificationPlantBuff = false;

        @Comment("Enable Output Change Function")
        @DefaultBoolean(true)
        @RequiresMcRestart
        public boolean enableRecipeOutputChance = true;

        @Comment("Enable Void Miner Tweak, allows you to override target dimension that Void Miner mines")
        @DefaultBoolean(true)
        public boolean enableVoidMinerTweak = true;

        @Comment("Change Recipe Item Output, like QFT")
        @DefaultDouble(2.5)
        public double recipeOutputChance = 2.5;

        @Name("quantum_computer")
        public final QuantumComputer quantum_computer = new QuantumComputer();

        @Comment("Quantum Computer")
        public static class QuantumComputer {

            @Comment("Maximum size allowed for a Quantum Computer multiblock structure")
            @RangeInt(min = 3)
            @DefaultInt(7)
            public int maxMultiblockSize = 7;

            @Comment("Maximum number of multi threader allowed in a Quantum Computer")
            @RangeInt(min = 1)
            @DefaultInt(1)
            public int maxMultiThreader = 1;

            @Comment("Maximum number of data entangler allowed in a Quantum Computer")
            @RangeInt(min = 1)
            @DefaultInt(1)
            public int maxDataEntangler = 1;
        }

        @Name("meteor_miner")
        public final MeteorMiner meteor_miner = new MeteorMiner();

        @Comment("Meteor Miner")
        public static class MeteorMiner {

            @Comment("Set the Meteor Miner how many every cycle break a block")
            @DefaultInt(1)
            @RequiresMcRestart
            public int meteorMinerMaxBlockPerCycle = 1;

            @Comment("Set the Meteor Miner how many every cycle break row blocks")
            @DefaultInt(1)
            @RequiresMcRestart
            public int meteorMinerMaxRowPerCycle = 1;
        }

        @Name("artificial_star")
        public final ArtificialStar artificial_star = new ArtificialStar();

        @Comment("Artificial Star")
        public static class ArtificialStar {

            @Comment("Set the power generation of EU Every Depleted Excited Naquadah FuelRod")
            @DefaultInt(4125000)
            @RequiresMcRestart
            public int euEveryDepletedExcitedNaquadahFuelRod = 4125000;

            @Comment("Open RenderDefaultArtificialStar rendering")
            @Name("EnableDefaultRender")
            @DefaultBoolean(true)
            public boolean enableRenderDefaultArtificialStar = true;

            @Comment("Set the power generation of EU Every Enhancement Core")
            @DefaultInt(100)
            @RequiresMcRestart
            public int euEveryEnhancementCore = 100;

            @Comment("Set secondsOfArtificialStarProgressCycleTime running time")
            @DefaultDouble(6.4)
            public double secondsOfArtificialStarProgressCycleTime = 6.4;
        }

        @Name("eternal_gregtech_workshop")
        public final EternalWorkshop eternal_gregtech_workshop = new EternalWorkshop();

        @Comment("Eternal Workshop")
        public static class EternalWorkshop {

            @Comment("Enable Eternal GregTech Workshop Spiral Render, like DNA")
            @DefaultBoolean(false)
            public boolean spiralRender = false;
        }

        @Name("portal_to_alfheim")
        public final PortalAlfheim portal_to_alfheim = new PortalAlfheim();

        @Comment("Portal Alfheim")
        public static class PortalAlfheim {

            @Comment("Setting this to false will reduce the Portal To Alfheim explosion to little more then a tnt blast")
            @DefaultBoolean(true)
            public boolean bigBoom = true;
        }
    }

    public static final Message message = new Message();

    @Comment("Message section")
    public static class Message {

        @Comment("Enable showing loaded mods info to player")
        @DefaultBoolean(true)
        public boolean enableShowAddMods = true;

        @Comment("Enable welcome message when player joins")
        @DefaultBoolean(true)
        public boolean enableShowJoinMessage = true;
    }

    public static final Other other = new Other();

    @Comment("Other section")
    public static class Other {

        @Comment("Enable sending the unfinished chat message when player dies")
        @DefaultBoolean(true)
        public boolean enableDeathIncompleteMessage = true;

        @Sync
        @Comment("Enable Saturation Heal Tweak. When hunger is 20, player regenerates health based on saturation / 6, up to 1 HP per 0.5s")
        @DefaultBoolean(true)
        public boolean enableSaturationHeal = true;

        @Name("not_enough_items")
        public final NEI not_enough_items = new NEI();

        public static class NEI {

            @Comment("Enable a special icon for cheat mode")
            @DefaultBoolean(false)
            @RequiresMcRestart
            public boolean enableSpecialCheatIcon = false;

            @Comment("Specify the type of the special cheat icon")
            @DefaultInt(0)
            public int specialIconType = 0;
        }
    }

    public static final ReAvaritia re_avaritia = new ReAvaritia();

    @Sync
    @Comment("Re Avaritia section")
    public static class ReAvaritia {

        @Comment("The OreDict used for unbreakable blocks in the Extreme Anvil")
        @DefaultString("neutronUnbreak")
        @RequiresMcRestart
        public String unbreakOre = "neutronUnbreak";

        @Name("infinity_sword")
        public final InfinitySword infinity_sword = new InfinitySword();

        @Comment("Infinity Sword")
        public static class InfinitySword {

            @Comment("Enable Infinity Sword bypass against Blood Sword and Draconic Armor")
            @DefaultBoolean(true)
            public boolean enableBypass = true;

            @Comment("Enable when Infinity Sword hit Infinity Suit create Explosion")
            @DefaultBoolean(true)
            public boolean enableExplosion = true;

            @Comment("Enable Player Special Render")
            @DefaultBoolean(true)
            public boolean enableSpecialRender = true;
        }

        @Name("chronarch_clock")
        public final ChronarchClock chronarch_clock = new ChronarchClock();

        @Comment("Chronarch Clock")
        public static class ChronarchClock {

            @Comment("Change Chronarchs Clock Cooldown")
            @DefaultInt(600)
            public int chronarchsClockCooldown = 600;

            @Comment("Duration of the clock's effect in ticks")
            @DefaultInt(200)
            public int chronarchsClockDurationTicks = 200;

            @Comment("Effective radius in blocks")
            @DefaultInt(3)
            public int chronarchsClockRadius = 3;

            @Comment("Speed multiplier for the clock")
            @DefaultInt(256)
            public int chronarchsClockSpeedMultiplier = 256;
        }
    }

    public static final Recipe recipe = new Recipe();

    @Sync
    @Comment("Recipe section")
    public static class Recipe {

        @Comment("Significantly reduce the time required for crafting recipes")
        @DefaultBoolean(false)
        @RequiresMcRestart
        public boolean enableAssemblingLineRecipesTimeChange = false;

        @Comment("Buff all chamber recipes")
        @DefaultBoolean(true)
        @RequiresMcRestart
        public boolean enableChamberRecipesBuff = true;

        @Comment("Enable Delete Recipe")
        @DefaultBoolean(true)
        @RequiresMcRestart
        public boolean enableDeleteRecipe = true;

        @Comment("Enable when player join world, Show Big Title")
        @DefaultBoolean(true)
        public boolean enableShowDelRecipeTitle = true;

        @Comment("Enable Something Cheap Recipe")
        @DefaultBoolean(true)
        @RequiresMcRestart
        public boolean enableSomethingRecipe = true;
    }

    public static final SuperCreeper super_creeper = new SuperCreeper();

    @Sync
    @Comment("Super Creeper section")
    public static class SuperCreeper {

        @Comment("Movement speed multiplier for Super Creeper")
        @DefaultDouble(1.0)
        public double moveSpeed = 1.0;

        @Comment("Allow creeper explosions to ignore mobGriefing gamerule and always break blocks")
        @DefaultBoolean(false)
        public boolean allowCreeperExplosionBypassGamerule = false;

        @Comment("Search radius for blocks")
        @DefaultInt(16)
        public int blockFindRadius = 16;

        @Comment("Interval in ticks between block targeting scans")
        @DefaultInt(30)
        public int blockTargetInterval = 30;

        @Comment("Ticks before Creeper explodes due to burning.")
        @DefaultInt(30)
        public int burningExplosionTimer = 30;

        @Comment("The higher this value, the faster the creeper moves as it gets closer to its target")
        @DefaultDouble(3.0)
        public double creeperSpeedBonusScale = 3.0;

        @Comment("List of target block IDs. Format: 'modid:blockname' or 'modid:blockname:meta'.")
        @DefaultStringList({ "minecraft:chest", "appliedenergistics2:tile.BlockDrive", "gregtech:gt.blockmachines",
            "appliedenergistics2:tile.BlockController" })
        public String[] defaultTargetBlocks = { "minecraft:chest", "appliedenergistics2:tile.BlockDrive",
            "gregtech:gt.blockmachines", "appliedenergistics2:tile.BlockController" };

        @Comment("Enable Creeper explosion when burning.")
        @DefaultBoolean(true)
        public boolean enableCreeperBurningExplosion = true;

        @Comment("Enable Creeper to find and mount nearby Spiders.")
        @DefaultBoolean(true)
        public boolean enableCreeperFindSpider = true;

        @Comment("Enable creeper explosion reaction when damaged by another creeper (not only when killed).")
        @DefaultBoolean(true)
        public boolean enableCreeperHurtByCreeperExplosion = true;

        @Comment("Enable creeper to explode immediately when killed while already ignited.")
        @DefaultBoolean(true)
        public boolean enableCreeperIgnitedDeathExplosion = true;

        @Comment("Enable Creeper explosion when killed by another Creeper.")
        @DefaultBoolean(true)
        public boolean enableCreeperKilledByCreeperExplosion = true;

        @Comment("Enable super creeper, can find you chest and more")
        @DefaultBoolean(false)
        @RequiresMcRestart
        public boolean enableSuperCreeper = false;

        @Comment("Power of the explosion caused by Super Creeper")
        @DefaultInt(3)
        public int explosionPower = 3;

        @Comment("Distance at which the Super Creeper will trigger its explosion.")
        @DefaultDouble(2.5)
        public double explosionTriggerRange = 2.5;

        @Comment("Search radius for players")
        @DefaultInt(16)
        public int playerFindRadius = 16;

        @Comment("Interval in ticks between player targeting scans")
        @DefaultInt(10)
        public int playerTargetInterval = 10;

        @Comment("Radius in which Creepers search for Spiders.")
        @DefaultInt(16)
        public int spiderFindRadius = 16;

        @Comment("Spider move speed when mounted by a Creeper.")
        @DefaultDouble(1.3)
        public double spiderMoveSpeed = 1.3;

        @Comment("Interval (in ticks) between Creeper spider targeting attempts.")
        @DefaultInt(20)
        public int spiderTargetInterval = 20;
    }

    public static final Tickrate tickrate = new Tickrate();

    @Sync
    @Comment("Tickrate section")
    public static class Tickrate {

        @Comment("Default tickrate. The game will always initialize with this value.")
        @DefaultFloat(20.0f)
        public float defaultTickrate = 20.0f;

        @Comment("Maximum tickrate from servers. Prevents really high tickrate values.")
        @DefaultFloat(1000.0f)
        public float maxTickrate = 1000.0f;

        @Comment("Minimum tickrate from servers. Prevents really low tickrate values.")
        @DefaultFloat(0.1f)
        public float minTickrate = 0.1f;
    }

    public static final Minecraft minecraft = new Minecraft();

    @Sync
    @Comment("Minecraft section")
    public static class Minecraft {

        @Comment({ "Enhances the /kill command to support modern selectors and improved behaviour." })
        @DefaultBoolean(true)
        public boolean enableKillEnhance = true;
    }
}
