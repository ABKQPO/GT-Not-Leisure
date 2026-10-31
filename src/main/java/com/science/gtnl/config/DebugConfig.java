package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;
import com.science.gtnl.ScienceNotLeisure;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "debug",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.GENERAL_FILE)
@Config.LangKeyPattern(pattern = ConfigRegistry.LANG_PATTERN, fullyQualified = true)
@Comment("Debug section")
public class DebugConfig {

    @Comment("Enable Debug Print Log")
    @DefaultBoolean(false)
    @RequiresMcRestart
    public static boolean enableDebugMode = false;
}
