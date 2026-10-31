package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;
import com.science.gtnl.ScienceNotLeisure;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "effect",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.GENERAL_FILE)
@Config.LangKeyPattern(pattern = ConfigRegistry.LANG_PATTERN, fullyQualified = true)
@Sync
@Comment("Effect section")
public class EffectConfig {

    @Comment("When the player dies and respawns, give the player Ghost Shape effect")
    @DefaultBoolean(true)
    public static boolean enableGhostlyShape = true;
}
