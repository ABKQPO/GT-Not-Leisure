package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultString;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultStringList;
import com.gtnewhorizon.gtnhlib.config.Config.Name;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;

@Sync
@Comment("Re Avaritia section")
public class ReAvaritiaConfig {

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
