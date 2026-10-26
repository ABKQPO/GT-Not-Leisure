package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;

@Sync
@Comment("Effect section")
public class EffectConfig {

    @Comment("When the player dies and respawns, give the player Ghost Shape effect")
    @DefaultBoolean(true)
    public boolean enableGhostlyShape = true;
}
