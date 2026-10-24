package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultDouble;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultEnum;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultFloat;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultIntList;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultString;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultStringList;
import com.gtnewhorizon.gtnhlib.config.Config.LangKey;
import com.gtnewhorizon.gtnhlib.config.Config.Name;
import com.gtnewhorizon.gtnhlib.config.Config.RangeDouble;
import com.gtnewhorizon.gtnhlib.config.Config.RangeFloat;
import com.gtnewhorizon.gtnhlib.config.Config.RangeInt;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.utils.enums.PortalGunPortalEffect;

import gregtech.api.enums.Mods;

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
    }

    public static final Client client = new Client();

    @Comment("Client section")
    public static class Client {

        @Comment("Enable animated text effects on this client")
        @LangKey("gtnl.gui.config.general.client.enable_text_effects")
        @DefaultBoolean(true)
        public boolean enableTextEffects = true;

        @Comment("Camera and weapon shake caused by charging and firing the electromagnetic railgun")
        @LangKey("gtnl.gui.config.general.client.enable_railgun_shake")
        @DefaultBoolean(true)
        public boolean enableRailgunShake = true;

        @Comment("Soft glow and charge orb effects of the electromagnetic railgun")
        @LangKey("gtnl.gui.config.general.client.enable_railgun_bloom")
        @DefaultBoolean(true)
        public boolean enableRailgunBloom = true;

        @Comment("GLSL shaders for the railgun beam, charge orb and charge overlay")
        @LangKey("gtnl.gui.config.general.client.enable_railgun_shaders")
        @DefaultBoolean(true)
        public boolean enableRailgunShaders = true;

        @Comment("Show beams, charge orbs, arcs and impact effects of other players' railguns")
        @LangKey("gtnl.gui.config.general.client.enable_railgun_show_others_fx")
        @DefaultBoolean(true)
        public boolean enableRailgunShowOthersFx = true;

        @Comment("Railgun effects of other players are not drawn beyond this distance in blocks")
        @LangKey("gtnl.gui.config.general.client.railgun_fx_distance")
        @DefaultInt(256)
        @RangeInt(min = 0, max = 256)
        public int railgunFxDistance = 256;

        @Comment("Celestweave armor effect level: 0 off, 1 event effects only, 2 all effects")
        @LangKey("gtnl.gui.config.general.client.celestweave_fx_level")
        @DefaultInt(2)
        @RangeInt(min = 0, max = 2)
        public int celestweaveFxLevel = 2;

        @Comment("Maximum number of Celestweave arcs shown for one wearer")
        @LangKey("gtnl.gui.config.general.client.celestweave_max_arcs_per_player")
        @DefaultInt(16)
        @RangeInt(min = 0, max = 64)
        public int celestweaveMaxArcsPerPlayer = 16;

        @Comment("Maximum number of Celestweave arcs shown in total")
        @LangKey("gtnl.gui.config.general.client.celestweave_max_arcs_total")
        @DefaultInt(128)
        @RangeInt(min = 0, max = 1024)
        public int celestweaveMaxArcsTotal = 128;

        @Comment("Show arcs and event effects of other players' Celestweave armor")
        @LangKey("gtnl.gui.config.general.client.enable_celestweave_show_others_fx")
        @DefaultBoolean(true)
        public boolean enableCelestweaveShowOthersFx = true;

        @Comment("Celestweave arcs are not drawn beyond this distance in blocks")
        @LangKey("gtnl.gui.config.general.client.celestweave_fx_distance")
        @DefaultInt(32)
        @RangeInt(min = 0, max = 256)
        public int celestweaveFxDistance = 32;

        @Comment("Beyond this distance in blocks only the main body of Celestweave armor is drawn")
        @LangKey("gtnl.gui.config.general.client.celestweave_detail_distance")
        @DefaultInt(24)
        @RangeInt(min = 0, max = 256)
        public int celestweaveDetailDistance = 24;

        @Comment("Process your own idle Celestweave arcs in first person view")
        @LangKey("gtnl.gui.config.general.client.enable_celestweave_first_person_fx")
        @DefaultBoolean(false)
        public boolean enableCelestweaveFirstPersonFx = false;

        @Comment("Draw the Celestweave chestplate arm over your empty first person hand")
        @LangKey("gtnl.gui.config.general.client.enable_celestweave_first_person_arm")
        @DefaultBoolean(true)
        public boolean enableCelestweaveFirstPersonArm = true;

        @Comment("Tint the equipment screens and the HUD editor guides with the glow color of the Celestweave chestplate you wear or are editing; when off they use the colors of the chosen theme")
        @LangKey("gtnl.gui.config.general.client.enable_equipment_theme_glow_color")
        @DefaultBoolean(true)
        public boolean enableEquipmentThemeGlowColor = true;

        @Comment("Look of portal gun portals on this client, also switchable from the portal gun wheel. PIXEL_SWIRL: pixel art green swirl; ELECTRIC_DOOR: door with an electric frame and a starry inside; FLUID_VORTEX: oval opened by a swirl of portal fluid; GOLDEN_SWIRL: pixel art golden swirl")
        @LangKey("gtnl.gui.config.general.client.portal_gun_portal_effect")
        @DefaultEnum("PIXEL_SWIRL")
        public PortalGunPortalEffect portalGunPortalEffect = PortalGunPortalEffect.PIXEL_SWIRL;

        @Comment("How far portal gun trail dots spread from the flight line when they appear, in blocks")
        @LangKey("gtnl.gui.config.general.client.portal_gun_trail_width")
        @DefaultDouble(0.03)
        @RangeDouble(min = 0, max = 0.1)
        public double portalGunTrailWidth = 0.03;

        @Comment("How fast portal gun trail dots drift sideways away from the flight line when they appear, in blocks per tick")
        @LangKey("gtnl.gui.config.general.client.portal_gun_trail_spray")
        @DefaultDouble(0.0125)
        @RangeDouble(min = 0, max = 0.06)
        public double portalGunTrailSpray = 0.0125;

        @Comment("How fast portal gun trail dots fall, in blocks per tick squared")
        @LangKey("gtnl.gui.config.general.client.portal_gun_trail_gravity")
        @DefaultDouble(0.02)
        @RangeDouble(min = 0.005, max = 0.04)
        public double portalGunTrailGravity = 0.02;

        @Comment("Average radius of a portal gun trail dot, in blocks")
        @LangKey("gtnl.gui.config.general.client.portal_gun_trail_dot_size")
        @DefaultDouble(0.028)
        @RangeDouble(min = 0.01, max = 0.08)
        public double portalGunTrailDotSize = 0.028;

        @Comment("Portal gun trail dots dropped at each spot along the flight")
        @LangKey("gtnl.gui.config.general.client.portal_gun_trail_count")
        @DefaultInt(1)
        @RangeInt(min = 1, max = 4)
        public int portalGunTrailCount = 1;

        @Comment("Spots per block along the flight where portal gun trail dots are dropped")
        @LangKey("gtnl.gui.config.general.client.portal_gun_trail_density")
        @DefaultInt(12)
        @RangeInt(min = 5, max = 60)
        public int portalGunTrailDensity = 12;

        @Comment("How long portal gun trail dots float before they fall, in ticks for every block away from the gun")
        @LangKey("gtnl.gui.config.general.client.portal_gun_trail_fall_delay")
        @DefaultDouble(1.5)
        @RangeDouble(min = 0, max = 4)
        public double portalGunTrailFallDelay = 1.5;

        @Comment("Trail dots this close to where the portal gun shot lands fade out quickly, in blocks; 0 keeps them all")
        @LangKey("gtnl.gui.config.general.client.portal_gun_trail_cut_range")
        @DefaultDouble(2)
        @RangeDouble(min = 0, max = 32)
        public double portalGunTrailCutRange = 2;

        @Comment("How long trail dots near where the shot lands take to fade out, in ticks")
        @LangKey("gtnl.gui.config.general.client.portal_gun_trail_cut_ticks")
        @DefaultDouble(4)
        @RangeDouble(min = 1, max = 20)
        public double portalGunTrailCutTicks = 4;

        @Comment("Puffs of light at the portal gun muzzle when it fires")
        @LangKey("gtnl.gui.config.general.client.enable_portal_gun_puffs")
        @DefaultBoolean(true)
        public boolean enablePortalGunPuffs = true;

        @Comment("Pixel rings around the portal gun shot while it flies")
        @LangKey("gtnl.gui.config.general.client.enable_portal_gun_rings")
        @DefaultBoolean(true)
        public boolean enablePortalGunRings = true;

        @Comment("A flash and dots splashing up where the portal gun shot lands")
        @LangKey("gtnl.gui.config.general.client.enable_portal_gun_sparks")
        @DefaultBoolean(true)
        public boolean enablePortalGunSparks = true;

        @Comment("Dots splashing up where the portal gun shot lands")
        @LangKey("gtnl.gui.config.general.client.portal_gun_spark_count")
        @DefaultInt(36)
        @RangeInt(min = 0, max = 80)
        public int portalGunSparkCount = 36;

        @Comment("How far the held portal gun tips its top toward your eye in first person, in degrees")
        @LangKey("gtnl.gui.config.general.client.portal_gun_held_pitch")
        @DefaultDouble(15)
        @RangeDouble(min = -30, max = 45)
        public double portalGunHeldPitch = 15;

        @Comment("How far the held portal gun turns sideways in first person, in degrees")
        @LangKey("gtnl.gui.config.general.client.portal_gun_held_yaw")
        @DefaultDouble(15)
        @RangeDouble(min = -45, max = 45)
        public double portalGunHeldYaw = 15;

        @Comment("How far the held portal gun is pushed forward in first person, in blocks")
        @LangKey("gtnl.gui.config.general.client.portal_gun_held_forward")
        @DefaultDouble(0.15)
        @RangeDouble(min = -0.3, max = 0.5)
        public double portalGunHeldForward = 0.15;

        @Comment("Show the trail, rings, puffs and sparks of portal guns fired by other players")
        @LangKey("gtnl.gui.config.general.client.enable_portal_gun_show_others_fx")
        @DefaultBoolean(true)
        public boolean enablePortalGunShowOthersFx = true;

        @Comment("How far portal opening dots start from the edge of the portal, in blocks")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_spread")
        @DefaultDouble(0.05)
        @RangeDouble(min = 0, max = 0.1)
        public double portalGunOpenSpread = 0.05;

        @Comment("How fast portal opening dots drift outward along the portal, in blocks per tick")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_spray")
        @DefaultDouble(0.005)
        @RangeDouble(min = 0, max = 0.06)
        public double portalGunOpenSpray = 0.005;

        @Comment("How fast portal opening dots shoot out of the portal, in blocks per tick")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_jet")
        @DefaultDouble(0.17)
        @RangeDouble(min = 0, max = 0.4)
        public double portalGunOpenJet = 0.17;

        @Comment("How much speed portal opening dots keep every tick")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_drag")
        @DefaultDouble(0.96)
        @RangeDouble(min = 0.7, max = 0.99)
        public double portalGunOpenDrag = 0.96;

        @Comment("Gravity on portal opening dots, in blocks per tick squared; 0 or less makes them float")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_gravity")
        @DefaultDouble(0.021)
        @RangeDouble(min = -0.01, max = 0.04)
        public double portalGunOpenGravity = 0.021;

        @Comment("Average radius of portal opening dots, in blocks")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_dot_size")
        @DefaultDouble(0.025)
        @RangeDouble(min = 0.01, max = 0.08)
        public double portalGunOpenDotSize = 0.025;

        @Comment("How long portal opening dots wait before they move, in ticks")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_hover")
        @DefaultDouble(0)
        @RangeDouble(min = 0, max = 20)
        public double portalGunOpenHover = 0;

        @Comment("Dots thrown at each spot when a portal opens")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_count")
        @DefaultInt(1)
        @RangeInt(min = 1, max = 4)
        public int portalGunOpenCount = 1;

        @Comment("Spots per block of portal edge that throw dots when a portal opens")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_density")
        @DefaultInt(6)
        @RangeInt(min = 2, max = 40)
        public int portalGunOpenDensity = 6;

        @Comment("Throw portal opening dots from the whole portal instead of only its edge")
        @LangKey("gtnl.gui.config.general.client.enable_portal_gun_open_whole_face")
        @DefaultBoolean(false)
        public boolean enablePortalGunOpenWholeFace = false;

        @Comment("Arcs per tick while a portal opens")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_arc_rate")
        @DefaultDouble(2)
        @RangeDouble(min = 0, max = 8)
        public double portalGunOpenArcRate = 2;

        @Comment("How long arcs crackle when a portal opens, in ticks")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_arc_ticks")
        @DefaultDouble(11.5)
        @RangeDouble(min = 2, max = 20)
        public double portalGunOpenArcTicks = 11.5;

        @Comment("Chance that a portal arc has a small fork, from 0 to 1")
        @LangKey("gtnl.gui.config.general.client.portal_gun_arc_fork_chance")
        @DefaultDouble(0.75)
        @RangeDouble(min = 0, max = 1)
        public double portalGunArcForkChance = 0.75;

        @Comment("How hard opening arcs bend: long straight runs with sharp corners, 0 is a straight line")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_arc_bend")
        @DefaultDouble(0.8)
        @RangeDouble(min = 0, max = 1.5)
        public double portalGunOpenArcBend = 0.8;

        @Comment("How far opening arcs peak out of the portal, as a share of their length, 0 keeps them flat")
        @LangKey("gtnl.gui.config.general.client.portal_gun_open_arc_rise")
        @DefaultDouble(0.6)
        @RangeDouble(min = 0, max = 1.5)
        public double portalGunOpenArcRise = 0.6;

        @Comment("How long a portal arc stays before it jumps somewhere else, in ticks")
        @LangKey("gtnl.gui.config.general.client.portal_gun_arc_hop")
        @DefaultDouble(1.5)
        @RangeDouble(min = 0.5, max = 3)
        public double portalGunArcHop = 1.5;

        @Comment("Width of the white core of portal arcs, in blocks")
        @LangKey("gtnl.gui.config.general.client.portal_gun_arc_width")
        @DefaultDouble(0.006)
        @RangeDouble(min = 0.002, max = 0.04)
        public double portalGunArcWidth = 0.006;

        @Comment("Arc runs per second along the inner ring of an open portal")
        @LangKey("gtnl.gui.config.general.client.portal_gun_ring_arc_rate")
        @DefaultDouble(3)
        @RangeDouble(min = 0, max = 10)
        public double portalGunRingArcRate = 3;

        @Comment("Arc bursts per second near the middle of an open portal")
        @LangKey("gtnl.gui.config.general.client.portal_gun_center_arc_rate")
        @DefaultDouble(0.5)
        @RangeDouble(min = 0, max = 10)
        public double portalGunCenterArcRate = 0.5;

        @Comment("How much a portal squashes, stretches and rocks when something passes through it; 0 keeps it still")
        @LangKey("gtnl.gui.config.general.client.portal_gun_pass_jelly")
        @DefaultDouble(0.12)
        @RangeDouble(min = 0, max = 0.3)
        public double portalGunPassJelly = 0.12;

        @Comment("Dots thrown from a portal when something passes through it")
        @LangKey("gtnl.gui.config.general.client.portal_gun_pass_dot_count")
        @DefaultInt(24)
        @RangeInt(min = 0, max = 60)
        public int portalGunPassDotCount = 24;

        @Comment("How much a closing portal swells before it shrinks away; 0 shrinks it at once. The electric door plays its opening backwards instead")
        @LangKey("gtnl.gui.config.general.client.portal_gun_close_pop")
        @DefaultDouble(0.2)
        @RangeDouble(min = 0, max = 0.5)
        public double portalGunClosePop = 0.2;

        @Comment("How long a closing portal takes to shrink away, in ticks; the electric door always plays its opening backwards")
        @LangKey("gtnl.gui.config.general.client.portal_gun_close_ticks")
        @DefaultInt(9)
        @RangeInt(min = 4, max = 14)
        public int portalGunCloseTicks = 9;
    }

    public static final Effect effect = new Effect();

    @Sync
    @Comment("Effect section")
    public static class Effect {

        @Comment("When the player dies and respawns, give the player Ghost Shape effect")
        @DefaultBoolean(true)
        public boolean enableGhostlyShape = true;
    }

    public static final Wireless wireless = new Wireless();

    @Comment("Wireless frequency card")
    public static class Wireless {

        @Comment("Maximum runtime entrances per frequency. Lowering this only prevents new entrances.")
        @DefaultInt(4096)
        @RangeInt(min = 1)
        public int maxEntrancesPerFrequency = 4096;
    }

    public static final Item item = new Item();

    @Sync
    @Comment("Item section")
    public static class Item {

        @Name("energyModule")
        public final EnergyModule energyModule = new EnergyModule();

        @Comment("Energy module shared by the railgun and Celestweave armor")
        public static class EnergyModule {

            @Comment("EU capacity of a railgun or armor piece with a T1 energy module installed")
            @DefaultDouble(25000000)
            public double capacityT1 = 25000000;

            @Comment("EU capacity of a railgun or armor piece with a T2 energy module installed")
            @DefaultDouble(125000000)
            public double capacityT2 = 125000000;

            @Comment("EU capacity of a railgun or armor piece with a T3 energy module installed")
            @DefaultDouble(500000000)
            public double capacityT3 = 500000000;
        }

        @Name("railgun")
        public final Railgun railgun = new Railgun();

        @Comment("Electromagnetic Railgun")
        public static class Railgun {

            @Comment("EU capacity of the railgun without an energy module")
            @DefaultDouble(250000)
            public double baseCapacity = 250000;

            @Comment("EU equivalent of one high voltage lightning, used by the sustained beam")
            @DefaultDouble(1600)
            public double hvLightningEu = 1600;

            @Comment("EU equivalent of one extreme high voltage lightning, used by charged shots and the EHV beam")
            @DefaultDouble(25600)
            public double ehvLightningEu = 25600;

            @Comment("Base EU consumed by the sustained beam every damage settlement (2 ticks)")
            @DefaultDouble(100)
            public double beamEuPerSettle = 100;

            @Comment("Base EU consumed by the extreme high voltage beam every damage settlement (2 ticks)")
            @DefaultDouble(1000)
            public double ehvBeamEuPerSettle = 1000;

            @Comment("Base EU consumed when firing an EHV1 charged shot")
            @DefaultDouble(2000)
            public double shotEuEhv1 = 2000;

            @Comment("Base EU consumed when firing an EHV2 charged shot")
            @DefaultDouble(10000)
            public double shotEuEhv2 = 10000;

            @Comment("Base EU consumed when firing an EHV3 charged shot")
            @DefaultDouble(50000)
            public double shotEuEhv3 = 50000;

            @Comment("EU consumed per charging tick before reaching EHV2")
            @DefaultDouble(250)
            public double chargeEuEhv1 = 250;

            @Comment("EU consumed per charging tick between EHV2 and EHV3")
            @DefaultDouble(1000)
            public double chargeEuEhv2 = 1000;

            @Comment("EU consumed per charging tick after reaching EHV3")
            @DefaultDouble(2500)
            public double chargeEuEhv3 = 2500;

            @Comment("Extra charging speed granted by each installed acceleration module, 0.25 means +25%")
            @DefaultDouble(0.25)
            @RangeDouble(min = 0, max = 1)
            public double accelerationBonus = 0.25;

            @Comment("Damage of the sustained beam per settlement")
            @DefaultDouble(20)
            public double beamDamage = 20;

            @Comment("Armor bypass ratio of the sustained beam, from 0 to 1")
            @DefaultDouble(0.4)
            public double beamBypass = 0.4;

            @Comment("Damage of the extreme high voltage beam per settlement")
            @DefaultDouble(200)
            public double ehvBeamDamage = 200;

            @Comment("Base damage of an EHV1 charged shot")
            @DefaultDouble(100)
            public double baseDamageEhv1 = 100;

            @Comment("Base damage of an EHV2 charged shot")
            @DefaultDouble(300)
            public double baseDamageEhv2 = 300;

            @Comment("Base damage of an EHV3 charged shot")
            @DefaultDouble(600)
            public double baseDamageEhv3 = 600;

            @Comment("Armor bypass ratio of charged shots, from 0 to 1")
            @DefaultDouble(0.8)
            public double chargedBypass = 0.8;

            @Comment("Allow the railgun to damage players when the PvP setting of the railgun is enabled")
            @DefaultBoolean(true)
            public boolean enablePlayerDamage = true;

            @Comment("Apply electromagnetic paralysis to players hit by the railgun")
            @DefaultBoolean(true)
            public boolean enablePlayerParalysis = true;

            @Comment("Allow charged shots to destroy terrain when the terrain setting of the railgun is enabled")
            @DefaultBoolean(false)
            public boolean enableTerrain = false;

            @Comment("Drop items from blocks destroyed by the railgun")
            @DefaultBoolean(false)
            public boolean enableTerrainDrops = false;

            @Comment("Maximum number of blocks the railgun destroys per tick across all impacts")
            @DefaultInt(200)
            @RangeInt(min = 1, max = 10000)
            public int terrainBlocksPerTick = 200;

            @Comment("Mod IDs whose blocks are never destroyed by the railgun, case-insensitive")
            @DefaultStringList({ Mods.ModIDs.GREG_TECH, Mods.ModIDs.BART_WORKS, Mods.ModIDs.TECTECH,
                Mods.ModIDs.G_T_N_H_LANTHANIDES, Mods.ModIDs.G_T_PLUS_PLUS, Mods.ModIDs.APPLIED_ENERGISTICS2,
                Mods.ModIDs.A_E2_FLUID_CRAFT, ScienceNotLeisure.MODID })
            public String[] terrainProtectedMods = { Mods.ModIDs.GREG_TECH, Mods.ModIDs.BART_WORKS, Mods.ModIDs.TECTECH,
                Mods.ModIDs.G_T_N_H_LANTHANIDES, Mods.ModIDs.G_T_PLUS_PLUS, Mods.ModIDs.APPLIED_ENERGISTICS2,
                Mods.ModIDs.A_E2_FLUID_CRAFT, ScienceNotLeisure.MODID };

            @Comment("Refill the held railgun from the owner's wireless EU network once per second")
            @DefaultBoolean(true)
            public boolean enableWirelessRefill = true;
        }

        @Name("portal_gun")
        public final PortalGun portal_gun = new PortalGun();

        @Comment("Portal Gun")
        public static class PortalGun {

            @Comment("Shimmer the portal gun holds, in mB; shimmer is its only fuel")
            @DefaultInt(8000)
            @RangeInt(min = 100)
            public int shimmerCapacity = 8000;

            @Comment("Blocks of distance paid by 1 mB of shimmer when the destination is in the same dimension")
            @DefaultInt(4)
            @RangeInt(min = 1)
            public int blocksPerShimmer = 4;

            @Comment("Most shimmer a shot can cost, in mB. Shots to another dimension always cost this much")
            @DefaultInt(100)
            @RangeInt(min = 0)
            public int maxShimmerPerShot = 100;

            @Comment("Gravity on a portal gun shot, in blocks per tick squared; 0 flies straight")
            @DefaultDouble(0.05)
            @RangeDouble(min = 0, max = 0.2)
            public double orbGravity = 0.05;

            @Comment("How long a portal gun shot flies before it opens a portal in the air, in ticks")
            @DefaultInt(5)
            @RangeInt(min = 1, max = 100)
            public int airOpenTicks = 5;

            @Comment("How long a pair of portals stays open, in ticks")
            @DefaultInt(100)
            @RangeInt(min = 20)
            public int portalLifetime = 100;

            @Comment("Ticks after going through a pair of portals before the same pair carries that entity again; touching either portal of the pair meanwhile restarts the wait. 0 turns it off")
            @DefaultInt(20)
            @RangeInt(min = 0, max = 1200)
            public int pairCooldownTicks = 20;

            @Comment("Dimension IDs where portals can neither open nor lead to")
            @DefaultIntList({})
            public int[] dimensionBlacklist = {};
        }

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

        @Name("celestweave")
        public final Celestweave celestweave = new Celestweave();

        @Comment("Celestweave Armor")
        public static class Celestweave {

            @Comment("EU capacity of each armor piece without an energy module")
            @DefaultDouble(2500000)
            public double baseCapacity = 2500000;

            @Comment("Refill worn armor from the owner's wireless EU network every second")
            @DefaultBoolean(true)
            public boolean enableWirelessRefill = true;

            @Comment("Upper limit of the idle arc count a wearer can choose")
            @DefaultInt(8)
            @RangeInt(min = 0, max = 8)
            public int maxIdleArcs = 8;

            @Comment("Draw from the owner's wireless EU network when the armor charge cannot pay for absorbed damage")
            @DefaultBoolean(true)
            public boolean enableWirelessFallback = true;

            @Comment("EU paid per point of absorbed damage")
            @DefaultDouble(20000)
            @RangeDouble(min = 0, max = 1.0E12)
            public double shieldEuPerDamage = 20000;

            @Comment("Upper limit of EU paid for a single hit")
            @DefaultDouble(1.0E9)
            @RangeDouble(min = 0, max = 1.0E15)
            public double maxEuPerHit = 1.0E9;

            @Comment("Damage absorbed per hit by the shield")
            @DefaultFloat(1.0E6F)
            @RangeFloat(min = 0, max = 1.0E9F)
            public float maxAbsorbPerHit = 1.0E6F;

            @Comment("EU paid when the shield prevents a death")
            @DefaultDouble(1.0E8)
            @RangeDouble(min = 0, max = 1.0E15)
            public double deathSaveEu = 1.0E8;

            @Comment("Share of the incoming damage dealt by each lightning strike of the reflect module")
            @DefaultFloat(0.5F)
            @RangeFloat(min = 0, max = 10)
            public float reflectRatio = 0.5F;

            @Comment("Radius in blocks within which the reflect module strikes hostile mobs")
            @DefaultDouble(8)
            @RangeDouble(min = 0, max = 32)
            public double reflectRadius = 8;

            @Comment("Most targets struck by lightning per reflected hit, including the attacker")
            @DefaultInt(4)
            @RangeInt(min = 1, max = 16)
            public int maxReflectTargets = 4;

            @Comment("EU paid per reflected hit or projectile")
            @DefaultDouble(50000)
            @RangeDouble(min = 0, max = 1.0E12)
            public double reflectEu = 50000;

            @Comment("EU paid per tick while the wearer is flying and moving with the flight module")
            @DefaultDouble(100)
            @RangeDouble(min = 0, max = 1.0E12)
            public double flightEuPerTick = 100;

            @Comment("EU paid per tick by the walk assist module while moving on the ground")
            @DefaultDouble(20)
            @RangeDouble(min = 0, max = 1.0E12)
            public double walkEuPerTick = 20;

            @Comment("Upper limit of the walk assist speed and jump multipliers")
            @DefaultInt(4)
            @RangeInt(min = 1, max = 16)
            public int maxWalkLevel = 4;

            @Comment("Upper limit of the flight speed multiplier")
            @DefaultInt(8)
            @RangeInt(min = 1, max = 16)
            public int maxFlightLevel = 8;

            @Comment("EU paid per dash")
            @DefaultDouble(500000)
            @RangeDouble(min = 0, max = 1.0E12)
            public double dashEu = 500000;

            @Comment("Ticks between two dashes")
            @DefaultInt(40)
            @RangeInt(min = 0, max = 1200)
            public int dashCooldown = 40;

            @Comment("Dash velocity in blocks per tick")
            @DefaultFloat(2.2F)
            @RangeFloat(min = 0, max = 10)
            public float dashSpeed = 2.2F;

            @Comment("Extra reach of the reach module at level 1")
            @DefaultFloat(5)
            @RangeFloat(min = 0, max = 100)
            public float reachLevel1 = 5;

            @Comment("Extra reach of the reach module at level 2")
            @DefaultFloat(10)
            @RangeFloat(min = 0, max = 100)
            public float reachLevel2 = 10;

            @Comment("Extra reach of the reach module at level 3")
            @DefaultFloat(20)
            @RangeFloat(min = 0, max = 100)
            public float reachLevel3 = 20;

            @Comment("EU paid per point of hunger refilled by the saturation module")
            @DefaultDouble(5000)
            @RangeDouble(min = 0, max = 1.0E12)
            public double saturationEu = 5000;

            @Comment("EU paid per removed negative effect by the purification module")
            @DefaultDouble(20000)
            @RangeDouble(min = 0, max = 1.0E12)
            public double purificationEu = 20000;

            @Comment("EU paid per tick by the night vision module")
            @DefaultDouble(5)
            @RangeDouble(min = 0, max = 1.0E12)
            public double nightVisionEuPerTick = 5;

            @Comment("EU paid per point of mana supplied by the terrasteel module")
            @DefaultDouble(100)
            @RangeDouble(min = 0, max = 1.0E12)
            public double manaEu = 100;
        }
    }

    public static final Machine machine = new Machine();

    @Sync
    @Comment("Machine section")
    public static class Machine {

        @Comment("Default energy consumption per operation (in AE/t)")
        @LangKey("gtnl.gui.config.general.machine.beam_former_energy_consume")
        @DefaultDouble(10)
        public double beamFormerEnergyConsume = 10;

        @Comment("Default maximum beam length (in blocks)")
        @LangKey("gtnl.gui.config.general.machine.beam_former_length")
        @DefaultInt(256)
        public int beamFormerLength = 256;

        @Comment("Allow interaction with machines from their main facing (similar to GTM behavior)")
        @LangKey("gtnl.gui.config.general.machine.allow_main_facing_interaction")
        @DefaultBoolean(false)
        public boolean allowMainFacingInteraction = false;

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

            @Comment("Log the step breakdown of every quantum crafting calculation")
            @DefaultBoolean(false)
            public boolean enableCalculationLog = false;

            @Comment("Log stage timings and counters of every quantum crafting calculation")
            @DefaultBoolean(false)
            public boolean enableCalculationPerformanceLog = false;

            @Comment("Seconds of calculation time a quantum crafting plan with missing items may spend on trying alternative patterns one by one, after which the best plan found so far is kept. 0 never tries them")
            @RangeInt(min = 0)
            @DefaultInt(5)
            public int calculationFallbackSeconds = 5;

            @Comment("Seconds of calculation time one quantum crafting calculation may take on a dedicated server before it fails, like MaxCraftingSteps of AE2. 0 disables the limit")
            @RangeInt(min = 0)
            @DefaultInt(30)
            public int calculationLimitSeconds = 30;
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

            @Comment("Enable automatic conversion of ingotHot to ingot when writing pattern")
            @DefaultBoolean(true)
            public boolean enableReplaceHotIngotInPattern = true;

            @Comment("Enable a special icon for cheat mode")
            @DefaultBoolean(false)
            @RequiresMcRestart
            public boolean enableSpecialCheatIcon = false;

            @Comment("Specify the type of the special cheat icon")
            @DefaultInt(0)
            public int specialIconType = 0;
        }

        @Name("applied_energistics")
        public final AppliedEnergistics applied_energistics = new AppliedEnergistics();

        @Comment("Applied Energistics 2")
        @Sync
        public static class AppliedEnergistics {

            @Comment("Enable configurable Processing Pattern Terminal capacity behavior")
            @LangKey("gtnl.gui.config.general.other.applied_energistics.enable_processing_pattern_capacity_mixin")
            @DefaultBoolean(true)
            public boolean enableProcessingPatternCapacityMixin = true;

            @Comment("Multiplier for Processing Pattern Terminal capacity. The effective layout is 16N:4N.")
            @LangKey("gtnl.gui.config.general.other.applied_energistics.processing_pattern_capacity_multiplier")
            @RangeInt(min = 1, max = 64)
            @DefaultInt(2)
            public int processingPatternCapacityMultiplier = 2;
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

            @Comment("Tile entities the clock must not accelerate. The entries are the names used in GameRegistry.registerTileEntity, for example minecraft:furnace or gregtech:gt.blockmachines. An empty list accelerates everything.")
            @DefaultStringList({})
            public String[] chronarchsClockTileEntityBlacklist = {};
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

        @Comment("Enable generated Shimmer disassembly recipes")
        @DefaultBoolean(true)
        @RequiresMcRestart
        public boolean enableShimmerDisassemblyRecipes = true;

        @Comment("Register generated Shimmer fake recipes in NEI")
        @DefaultBoolean(true)
        @RequiresMcRestart
        public boolean enableShimmerFakeRecipeInNEI = true;
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
        @LangKey("gtnl.gui.config.general.minecraft.enable_kill_enhance")
        @DefaultBoolean(true)
        public boolean enableKillEnhance = true;
    }
}
