package com.gtnllib.config;

import java.util.Collection;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.Config.Comment;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultBoolean;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultFloat;
import com.gtnewhorizon.gtnhlib.config.Config.DefaultString;
import com.gtnewhorizon.gtnhlib.config.Config.LangKey;
import com.gtnewhorizon.gtnhlib.config.Config.RangeFloat;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnllib.GTNLLib;

@Config(
    modid = GTNLLib.MODID,
    category = GTNLLibConfig.CATEGORY,
    configSubDirectory = GTNLLib.CONFIG_DIRECTORY,
    filename = GTNLLib.MODID)
@Config.LangKeyPattern(pattern = "gtnllib.gui.config.%cat.%field", fullyQualified = true)
@Comment("Client settings of the shared interface and HUD")
public class GTNLLibConfig {

    @Config.Ignore
    public static final String CATEGORY = "client";
    @Config.Ignore
    private static final String THEME = "theme";
    @Config.Ignore
    private static String[] themes = {};

    @Comment("Show every HUD element; arrange them with Edit HUD")
    @LangKey("gtnllib.gui.config.client.enable_hud")
    @DefaultBoolean(true)
    public static boolean enableHud = true;

    @Comment("Base scale of every HUD element; each element can be scaled further in the HUD editor")
    @LangKey("gtnllib.gui.config.client.hud_scale")
    @DefaultFloat(1F)
    @RangeFloat(min = 0.5F, max = 3F)
    public static float hudScale = 1F;

    @Comment("Theme of the screens that follow the chosen theme; only registered themes can be chosen, an empty or unknown id keeps the default theme of each screen")
    @LangKey("gtnllib.gui.config.client.theme")
    @DefaultString("")
    public static String theme = "";

    private GTNLLibConfig() {}

    public static void register() {
        try {
            ConfigurationManager.registerConfig(GTNLLibConfig.class);
        } catch (ConfigException e) {
            GTNLLib.LOG.error("Failed to load the GTNLLib client settings", e);
        }
    }

    public static String[] themes() {
        return themes.clone();
    }

    public static void listThemes(Collection<String> ids) {
        themes = ids.toArray(new String[0]);
        Configuration config = ConfigurationManager.getConfig(GTNLLibConfig.class);
        Property property = config == null ? null
            : config.getCategory(CATEGORY)
                .get(THEME);
        if (property != null) property.setValidValues(themes.clone());
    }
}
