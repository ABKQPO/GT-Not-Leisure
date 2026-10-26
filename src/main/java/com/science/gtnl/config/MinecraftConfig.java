package com.science.gtnl.config;

import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.LangKey;
import com.gtnewhorizon.gtnhlib.config.Config.Sync;

@Sync
@Comment("Minecraft section")
public class MinecraftConfig {

    @Comment({ "Enhances the /kill command to support modern selectors and improved behaviour." })
    @LangKey("gtnl.gui.config.general.minecraft.enable_kill_enhance")
    @DefaultBoolean(true)
    public boolean enableKillEnhance = true;
}
