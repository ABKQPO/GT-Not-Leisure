package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultDouble;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultStringList;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;

@Sync
@Comment("Super Creeper section")
public class SuperCreeperConfig {

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
