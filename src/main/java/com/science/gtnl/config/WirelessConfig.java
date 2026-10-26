package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultInt;
import com.gtnewhorizon.gtnhlib.config.Config.RangeInt;

@Comment("Wireless frequency card")
public class WirelessConfig {

    @Comment("Maximum runtime entrances per frequency. Lowering this only prevents new entrances.")
    @DefaultInt(4096)
    @RangeInt(min = 1)
    public int maxEntrancesPerFrequency = 4096;
}
