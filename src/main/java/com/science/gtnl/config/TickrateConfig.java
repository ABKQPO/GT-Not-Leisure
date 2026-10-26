package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultFloat;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;

@Sync
@Comment("Tickrate section")
public class TickrateConfig {

    @Comment("Default tickrate. The game will always initialize with this value.")
    @DefaultFloat(20.0f)
    public float defaultTickrate = 20.0f;

    @Comment("Maximum tickrate from servers. Prevents really high tickrate values.")
    @DefaultFloat(1000.0f)
    public float maxTickrate = 1000.0f;

    @Comment("Minimum tickrate from servers. Prevents really low tickrate values.")
    @DefaultFloat(0.1f)
    public float minTickrate = 0.1f;
}
