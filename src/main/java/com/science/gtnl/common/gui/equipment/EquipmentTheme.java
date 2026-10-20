package com.science.gtnl.common.gui.equipment;

import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.theme.ThemeBuilder;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.JsonBuilder;
import com.cleanroommc.modularui.utils.JsonHelper;
import com.google.gson.JsonObject;
import com.science.gtnl.config.MainConfig;
import com.science.gtnllib.client.hud.HudColor;
import com.science.gtnllib.gui.layout.LayoutTheme;
import com.science.gtnllib.gui.theme.LivePalette;
import com.science.gtnllib.gui.theme.PaletteThemes;
import com.science.gtnllib.gui.theme.ThemePalette;
import com.science.gtnllib.gui.theme.ThemeStyle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.modularui2.GTGuiThemes;

@SideOnly(Side.CLIENT)
public class EquipmentTheme {

    public static final String ID = "gtnl:equipment";
    private static final int ACCENT = 0xFFFF3D9A, ACCENT_DIM = 0x80FF3D9A, HOVER = 0x26FF3D9A, TEXT = 0xFFFFFFFF,
        HINT = 0xFF8E8796, PANEL_TOP = 0xF2211A27, PANEL_BOTTOM = 0xF20D0A11, CARD = 0x50000000, CARD_EDGE = 0x24FFFFFF,
        BUTTON = 0x9614141E, BUTTON_HOVER = 0xA0FF3D9A, TAB_HOVER = 0x50FF3D9A, WELL = 0x80000000,
        WELL_EDGE = 0xFF3A3640, SLOT_HOVER = 0x50FF3D9A, TRACK = 0xFF1A1420, SWITCH_OFF = 0xFF3A3640, KNOB = 0xFFF2EEF4,
        KNOB_OFF = 0xFF8C8794, TEXT_ON = 0xFFF4F0F6, TEXT_DIM = 0xFF6E6876, TEXT_OFF = 0xFF9A94A2, CURSOR = 0xFFFFE6F1,
        CURSOR_TEXT = 0xFF4A0A2A, TICK = 0x40FFFFFF, MILESTONE = 0x99FFFFFF, DANGER = 0xFFE5484D;
    public static final ThemePalette PALETTE = new ThemePalette(
        ACCENT,
        ACCENT_DIM,
        HOVER,
        TEXT,
        HINT,
        PANEL_TOP,
        PANEL_BOTTOM,
        CARD,
        CARD_EDGE,
        BUTTON,
        BUTTON_HOVER,
        TAB_HOVER,
        WELL,
        WELL_EDGE,
        SLOT_HOVER,
        TRACK,
        SWITCH_OFF,
        KNOB,
        KNOB_OFF,
        TEXT_ON,
        TEXT_DIM,
        TEXT_OFF,
        CURSOR,
        CURSOR_TEXT,
        TICK,
        MILESTONE,
        DANGER);
    public static final WidgetThemeKey<Colors> COLORS = registerColors(new Colors(PALETTE));
    public static final LivePalette LIVE = PaletteThemes
        .live(EquipmentTheme::selected, () -> MainConfig.client.enableEquipmentThemeGlowColor);

    private EquipmentTheme() {}

    public static void register() {
        PaletteThemes.extend(EquipmentTheme::addColors);
        PaletteThemes.register(ID, GTGuiThemes.STANDARD.getId(), PALETTE, ThemeStyle.DEFAULT);
        shareWithHudEditor();
    }

    public static String selected() {
        return PaletteThemes.selected(ID);
    }

    public static void shareWithHudEditor() {
        HudColor.editor(() -> colors().accentDim, () -> colors().accent);
    }

    public static LayoutTheme layout() {
        return LayoutTheme.of(
            IThemeApi.get()
                .getTheme(selected()));
    }

    public static Colors colors() {
        return LIVE.theme()
            .getWidgetTheme(COLORS)
            .getTheme();
    }

    private static void addColors(ThemeBuilder<?> theme, ThemePalette palette) {
        theme.mergeAdd(
            COLORS.getFullName(),
            new JsonBuilder().add(Colors.ACCENT_COLOR, palette.accent())
                .add(Colors.ACCENT_DIM_COLOR, palette.accentDim())
                .add(Colors.HINT_COLOR, palette.hint()));
    }

    private static WidgetThemeKey<Colors> registerColors(Colors defaults) {
        return IThemeApi.get()
            .widgetThemeKeyBuilder("gtnlEquipment", Colors.class)
            .defaultTheme(defaults)
            .defaultHoverTheme(defaults)
            .parser(Colors::new)
            .register();
    }

    public static class Colors extends WidgetTheme {

        private static final String ACCENT_COLOR = "accentColor", ACCENT_DIM_COLOR = "accentDimColor",
            HINT_COLOR = "hintColor";

        public final int accent, accentDim, hint;

        private Colors(ThemePalette palette) {
            super(0, 0, null, Color.WHITE.main, palette.text(), true, Color.WHITE.main);
            accent = palette.accent();
            accentDim = palette.accentDim();
            hint = palette.hint();
        }

        public Colors(Colors parent, JsonObject json, JsonObject fallback) {
            super(parent, json, fallback);
            accent = JsonHelper.getColor(json, parent.accent, ACCENT_COLOR);
            accentDim = JsonHelper.getColor(json, parent.accentDim, ACCENT_DIM_COLOR);
            hint = JsonHelper.getColor(json, parent.hint, HINT_COLOR);
        }
    }
}
