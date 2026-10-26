package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultDouble;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultFloat;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultIntList;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultStringList;
import com.gtnewhorizon.gtnhlib.config.Config.Name;
import com.gtnewhorizon.gtnhlib.config.Config.RangeDouble;
import com.gtnewhorizon.gtnhlib.config.Config.RangeFloat;
import com.gtnewhorizon.gtnhlib.config.Config.RangeInt;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;
import com.science.gtnl.ScienceNotLeisure;

import gregtech.api.enums.Mods;

@Sync
@Comment("Item section")
public class ItemConfig {

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
