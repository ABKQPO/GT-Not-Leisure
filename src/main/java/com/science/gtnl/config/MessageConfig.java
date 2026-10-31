package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.science.gtnl.ScienceNotLeisure;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "message",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.GENERAL_FILE)
@Config.LangKeyPattern(pattern = ConfigRegistry.LANG_PATTERN, fullyQualified = true)
@Comment("Message section")
public class MessageConfig {

    @Comment("Enable showing loaded mods info to player")
    @DefaultBoolean(true)
    public static boolean enableShowAddMods = true;

    @Comment("Enable welcome message when player joins")
    @DefaultBoolean(true)
    public static boolean enableShowJoinMessage = true;
}
