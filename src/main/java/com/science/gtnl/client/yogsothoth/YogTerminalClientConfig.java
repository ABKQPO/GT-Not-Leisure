package com.science.gtnl.client.yogsothoth;

import java.util.Objects;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.floatingwindow.FloatingWindowGeometry;

@Config(modid = ScienceNotLeisure.MODID, configSubDirectory = "GTNotLeisure", filename = "YogSothothTerminalClient")
@Config.ExcludeFromAutoGui
public final class YogTerminalClientConfig {

    @Config.Comment("Use the saved Yog-Sothoth terminal rectangle instead of anchoring it beside the inventory.")
    @Config.DefaultBoolean(false)
    public static boolean customWindow;

    @Config.DefaultInt(0)
    public static int x;

    @Config.DefaultInt(0)
    public static int y;

    @Config.DefaultInt(YogTerminalLayout.MIN_WIDTH)
    @Config.RangeInt(min = YogTerminalLayout.MIN_WIDTH)
    public static int width = YogTerminalLayout.MIN_WIDTH;

    @Config.DefaultInt(YogTerminalLayout.MIN_HEIGHT)
    @Config.RangeInt(min = YogTerminalLayout.MIN_HEIGHT)
    public static int height = YogTerminalLayout.MIN_HEIGHT;

    @Config.DefaultBoolean(false)
    public static boolean snappedWidth;

    @Config.DefaultBoolean(false)
    public static boolean snappedHeight;

    @Config.DefaultBoolean(false)
    public static boolean collapsed;

    @Config.Ignore
    private static boolean registered;

    static void load(YogTerminalLayout layout) {
        try {
            ConfigurationManager.registerConfig(YogTerminalClientConfig.class);
            registered = true;
            layout.restorePreference(windowPreference());
        } catch (ConfigException e) {
            ScienceNotLeisure.LOG.error("Failed to load Yog-Sothoth terminal client preferences", e);
        }
    }

    private static FloatingWindowGeometry.Preference windowPreference() {
        return customWindow ? new FloatingWindowGeometry.Preference(x, y, width, height, snappedWidth, snappedHeight)
            : null;
    }

    static void saveWindow(YogTerminalLayout layout) {
        FloatingWindowGeometry.Preference preference = layout.preference();
        if (Objects.equals(preference, windowPreference())) return;
        customWindow = preference != null;
        x = customWindow ? preference.x() : 0;
        y = customWindow ? preference.y() : 0;
        width = customWindow ? preference.width() : YogTerminalLayout.MIN_WIDTH;
        height = customWindow ? preference.height() : YogTerminalLayout.MIN_HEIGHT;
        snappedWidth = customWindow && preference.snappedWidth();
        snappedHeight = customWindow && preference.snappedHeight();
        save();
    }

    static void setCollapsed(boolean value) {
        collapsed = value;
        save();
    }

    private static void save() {
        if (registered) ConfigurationManager.save(YogTerminalClientConfig.class);
    }
}
