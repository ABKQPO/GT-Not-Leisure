package com.science.gtnl.config;

import java.util.Objects;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.floatingwindow.FloatingWindowGeometry;
import com.science.gtnl.client.yogsothoth.YogTerminalLayout;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "yog_sothoth_terminal",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.CLIENT_FILE)
@Config.ExcludeFromAutoGui
@Config.Comment("Window state of the Yog-Sothoth terminal")
public class YogTerminalClientConfig {

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

    public static void restore(YogTerminalLayout layout) {
        layout.restorePreference(windowPreference());
    }

    private static FloatingWindowGeometry.Preference windowPreference() {
        return customWindow ? new FloatingWindowGeometry.Preference(x, y, width, height, snappedWidth, snappedHeight)
            : null;
    }

    public static void saveWindow(YogTerminalLayout layout) {
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

    public static void setCollapsed(boolean value) {
        collapsed = value;
        save();
    }

    private static void save() {
        ConfigRegistry.save(YogTerminalClientConfig.class);
    }
}
