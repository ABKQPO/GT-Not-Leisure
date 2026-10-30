package com.gtnllib.client.hud;

import java.util.function.IntSupplier;

import com.gtnllib.gui.theme.LivePalette;
import com.gtnllib.gui.theme.ThemePalette;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum HudColor {

    THEME,
    ACCENT;

    private static final String LANG_PREFIX = "gtnllib.hud.color.";
    private static final int RGB = 0xFFFFFF, NEUTRAL = 0xFFFFFF, GUIDE = 0x80FFFFFF, SELECTION = 0xFFFFFFFF;
    private static LivePalette palette;
    private static String accentLang = LANG_PREFIX + "accent";
    private static IntSupplier guide = () -> GUIDE, selection = () -> SELECTION;

    public static void source(LivePalette palette, String accentLang) {
        HudColor.palette = palette;
        HudColor.accentLang = accentLang;
    }

    public static void editor(IntSupplier guide, IntSupplier selection) {
        HudColor.guide = guide;
        HudColor.selection = selection;
    }

    public static int guide() {
        return guide.getAsInt();
    }

    public static int selection() {
        return selection.getAsInt();
    }

    public static String theme() {
        return palette == null ? null : palette.themeId();
    }

    public String lang() {
        return this == ACCENT ? accentLang : LANG_PREFIX + HudLayout.id(this);
    }

    public int rgb() {
        if (palette == null) return NEUTRAL;
        ThemePalette themed = palette.themed();
        int theme = themed == null ? NEUTRAL : themed.accent() & RGB;
        if (this == THEME) return theme;
        int accent = palette.accent();
        return accent == LivePalette.NO_ACCENT ? theme : accent & RGB;
    }
}
