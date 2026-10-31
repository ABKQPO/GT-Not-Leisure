package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.RangeInt;
import com.science.gtnl.ScienceNotLeisure;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "wireless",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.GENERAL_FILE)
@Config.LangKeyPattern(pattern = ConfigRegistry.LANG_PATTERN, fullyQualified = true)
@Comment("Wireless frequency card")
public class WirelessConfig {

    @Comment("Maximum runtime entrances per frequency. Lowering this only prevents new entrances.")
    @DefaultInt(4096)
    @RangeInt(min = 1)
    public static int maxEntrancesPerFrequency = 4096;
}
