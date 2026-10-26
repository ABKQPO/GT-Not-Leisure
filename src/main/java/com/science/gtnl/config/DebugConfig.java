package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.RequiresMcRestart;

@Comment("Debug section")
public class DebugConfig {

    @Comment("Enable Debug Print Log")
    @DefaultBoolean(false)
    @RequiresMcRestart
    public boolean enableDebugMode = false;
}
