package com.science.gtnl.config;

import java.util.Objects;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.floatingwindow.FloatingWindowGeometry;
import com.science.gtnl.client.meminiterminal.MiniTerminalLayout;

@Config(
    modid = ScienceNotLeisure.MODID,
    category = "mini_terminal",
    configSubDirectory = ConfigRegistry.DIRECTORY,
    filename = ConfigRegistry.CLIENT_FILE)
@Config.ExcludeFromAutoGui
@Config.Comment("Window state of the ME mini portable terminal")
public class MiniTerminalClientConfig {

    @Config.Comment("Use the saved mini terminal rectangle instead of anchoring it beside the inventory.")
    @Config.DefaultBoolean(false)
    public static boolean customWindow;

    @Config.DefaultInt(0)
    public static int x;

    @Config.DefaultInt(0)
    public static int y;

    @Config.DefaultInt(MiniTerminalLayout.WIDTH)
    @Config.RangeInt(min = MiniTerminalLayout.MIN_WIDTH)
    public static int width = MiniTerminalLayout.WIDTH;

    @Config.DefaultInt(166)
    @Config.RangeInt(min = MiniTerminalLayout.MIN_HEIGHT)
    public static int height = 166;

    @Config.DefaultBoolean(false)
    public static boolean snappedWidth;

    @Config.DefaultBoolean(false)
    public static boolean snappedHeight;

    @Config.Comment("Inventory Shift click, Space click and Bogo transfer shortcuts insert into ME while the mini terminal is expanded.")
    @Config.DefaultBoolean(false)
    public static boolean shiftClickToME;

    public static void restore(MiniTerminalLayout layout) {
        layout.restorePreference(windowPreference());
    }

    private static FloatingWindowGeometry.Preference windowPreference() {
        return customWindow ? new FloatingWindowGeometry.Preference(x, y, width, height, snappedWidth, snappedHeight)
            : null;
    }

    public static void saveWindow(MiniTerminalLayout layout) {
        FloatingWindowGeometry.Preference preference = layout.preference();
        if (Objects.equals(preference, windowPreference())) return;
        customWindow = preference != null;
        x = customWindow ? preference.x() : 0;
        y = customWindow ? preference.y() : 0;
        width = customWindow ? preference.width() : MiniTerminalLayout.WIDTH;
        height = customWindow ? preference.height() : 166;
        snappedWidth = customWindow && preference.snappedWidth();
        snappedHeight = customWindow && preference.snappedHeight();
        save();
    }

    public static void toggleShiftClick() {
        shiftClickToME = !shiftClickToME;
        save();
    }

    private static void save() {
        ConfigRegistry.save(MiniTerminalClientConfig.class);
    }
}
