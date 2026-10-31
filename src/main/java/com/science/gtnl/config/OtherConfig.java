package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.LangKey;
import com.gtnewhorizon.gtnhlib.config.Config.RangeInt;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;
import com.science.gtnl.ScienceNotLeisure;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "other",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.GENERAL_FILE)
@Config.LangKeyPattern(pattern = ConfigRegistry.LANG_PATTERN, fullyQualified = true)
@Comment("Other section")
public class OtherConfig {

    @Comment("Enable sending the unfinished chat message when player dies")
    @DefaultBoolean(true)
    public static boolean enableDeathIncompleteMessage = true;

    @Sync
    @Comment("Enable Saturation Heal Tweak. When hunger is 20, player regenerates health based on saturation / 6, up to 1 HP per 0.5s")
    @DefaultBoolean(true)
    public static boolean enableSaturationHeal = true;

    public static final NEI not_enough_items = new NEI();

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

    public static final AppliedEnergistics applied_energistics = new AppliedEnergistics();

    @Comment("Applied Energistics 2")
    @Sync
    public static class AppliedEnergistics {

        @Comment("Enable configurable Processing Pattern Terminal capacity behavior")
        @LangKey("gtnl.gui.config.other.applied_energistics.enable_processing_pattern_capacity_mixin")
        @DefaultBoolean(true)
        public boolean enableProcessingPatternCapacityMixin = true;

        @Comment("Multiplier for Processing Pattern Terminal capacity. The effective layout is 16N:4N.")
        @LangKey("gtnl.gui.config.other.applied_energistics.processing_pattern_capacity_multiplier")
        @RangeInt(min = 1, max = 64)
        @DefaultInt(2)
        public int processingPatternCapacityMultiplier = 2;
    }
}
