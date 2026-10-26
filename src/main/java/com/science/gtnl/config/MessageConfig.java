package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;

@Comment("Message section")
public class MessageConfig {

    @Comment("Enable showing loaded mods info to player")
    @DefaultBoolean(true)
    public boolean enableShowAddMods = true;

    @Comment("Enable welcome message when player joins")
    @DefaultBoolean(true)
    public boolean enableShowJoinMessage = true;
}
