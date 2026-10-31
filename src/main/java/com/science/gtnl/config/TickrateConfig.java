package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultFloat;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;
import com.science.gtnl.ScienceNotLeisure;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "tickrate",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.GENERAL_FILE)
@Config.LangKeyPattern(pattern = ConfigRegistry.LANG_PATTERN, fullyQualified = true)
@Sync
@Comment("Tickrate section")
public class TickrateConfig {

    @Comment("Default tickrate. The game will always initialize with this value.")
    @DefaultFloat(20.0f)
    public static float defaultTickrate = 20.0f;

    @Comment("Maximum tickrate from servers. Prevents really high tickrate values.")
    @DefaultFloat(1000.0f)
    public static float maxTickrate = 1000.0f;

    @Comment("Minimum tickrate from servers. Prevents really low tickrate values.")
    @DefaultFloat(0.1f)
    public static float minTickrate = 0.1f;
}
