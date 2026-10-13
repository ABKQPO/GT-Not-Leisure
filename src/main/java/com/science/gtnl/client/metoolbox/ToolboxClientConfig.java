package com.science.gtnl.client.metoolbox;

import java.util.Objects;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.science.gtnl.ScienceNotLeisure;

@Config(modid = ScienceNotLeisure.MODID, configSubDirectory = "GTNotLeisure", filename = "METoolboxClient")
@Config.ExcludeFromAutoGui
public final class ToolboxClientConfig {

    @Config.Comment("Use the saved mini-terminal rectangle instead of anchoring it beside the inventory.")
    @Config.DefaultBoolean(false)
    public static boolean customWindow;

    @Config.DefaultInt(0)
    @Config.RangeInt(min = 0)
    public static int x;

    @Config.DefaultInt(0)
    @Config.RangeInt(min = 0)
    public static int y;

    @Config.DefaultInt(ToolboxLayout.WIDTH)
    @Config.RangeInt(min = ToolboxLayout.MIN_WIDTH)
    public static int width = ToolboxLayout.WIDTH;

    @Config.DefaultInt(166)
    @Config.RangeInt(min = ToolboxLayout.MIN_HEIGHT)
    public static int height = 166;

    @Config.DefaultBoolean(false)
    public static boolean snappedWidth;

    @Config.DefaultBoolean(false)
    public static boolean snappedHeight;

    @Config.Comment("Inventory Shift-click, Space-click and Bogo transfer shortcuts insert into ME while the mini terminal is expanded.")
    @Config.DefaultBoolean(false)
    public static boolean shiftClickToME;

    @Config.Ignore
    private static boolean registered;

    private ToolboxClientConfig() {}

    static void load(ToolboxLayout layout) {
        try {
            ConfigurationManager.registerConfig(ToolboxClientConfig.class);
            registered = true;
            layout.restorePreference(windowPreference());
        } catch (ConfigException e) {
            ScienceNotLeisure.LOG.error("Failed to load ME toolbox client preferences", e);
        }
    }

    private static ToolboxLayout.WindowPreference windowPreference() {
        return customWindow ? new ToolboxLayout.WindowPreference(x, y, width, height, snappedWidth, snappedHeight)
            : null;
    }

    static void saveWindow(ToolboxLayout layout) {
        ToolboxLayout.WindowPreference preference = layout.preference();
        if (Objects.equals(preference, windowPreference())) return;
        customWindow = preference != null;
        x = customWindow ? preference.x() : 0;
        y = customWindow ? preference.y() : 0;
        width = customWindow ? preference.width() : ToolboxLayout.WIDTH;
        height = customWindow ? preference.height() : 166;
        snappedWidth = customWindow && preference.snappedWidth();
        snappedHeight = customWindow && preference.snappedHeight();
        save();
    }

    static void toggleShiftClick() {
        shiftClickToME = !shiftClickToME;
        save();
    }

    private static void save() {
        if (registered) ConfigurationManager.save(ToolboxClientConfig.class);
    }
}
