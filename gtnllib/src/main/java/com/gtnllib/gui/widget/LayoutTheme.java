package com.gtnllib.gui.widget;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeBuilder;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.JsonHelper;
import com.google.gson.JsonObject;

public class LayoutTheme extends WidgetTheme {

    public static final String SPACING_SMALL = "spacingSmall", SPACING_MEDIUM = "spacingMedium",
        SPACING_LARGE = "spacingLarge", PADDING = "padding", BUTTON_HEIGHT = "buttonHeight", ROW_HEIGHT = "rowHeight",
        TEXT_SCALE = "textScale";
    private static final int DEFAULT_SPACING_SMALL = 2, DEFAULT_SPACING_MEDIUM = 4, DEFAULT_SPACING_LARGE = 10,
        DEFAULT_PADDING = 10, DEFAULT_BUTTON_HEIGHT = 14, DEFAULT_ROW_HEIGHT = 16;
    private static final float DEFAULT_TEXT_SCALE = 1F;
    public static final LayoutTheme DEFAULT = new LayoutTheme(
        DEFAULT_SPACING_SMALL,
        DEFAULT_SPACING_MEDIUM,
        DEFAULT_SPACING_LARGE,
        DEFAULT_PADDING,
        DEFAULT_BUTTON_HEIGHT,
        DEFAULT_ROW_HEIGHT,
        DEFAULT_TEXT_SCALE);
    public static final WidgetThemeKey<LayoutTheme> KEY = IThemeApi.get()
        .widgetThemeKeyBuilder("gtnllibLayout", LayoutTheme.class)
        .defaultTheme(DEFAULT)
        .defaultHoverTheme(DEFAULT)
        .parser(LayoutTheme::new)
        .register();

    public final int spacingSmall, spacingMedium, spacingLarge, padding, buttonHeight, rowHeight;
    public final float textScale;

    public LayoutTheme(int spacingSmall, int spacingMedium, int spacingLarge, int padding, int buttonHeight,
        int rowHeight, float textScale) {
        super(0, 0, null, Color.WHITE.main, Color.WHITE.main, false, Color.WHITE.main);
        this.spacingSmall = spacingSmall;
        this.spacingMedium = spacingMedium;
        this.spacingLarge = spacingLarge;
        this.padding = padding;
        this.buttonHeight = buttonHeight;
        this.rowHeight = rowHeight;
        this.textScale = textScale;
    }

    public LayoutTheme(LayoutTheme parent, JsonObject json, JsonObject fallback) {
        super(parent, json, fallback);
        spacingSmall = Math.max(0, JsonHelper.getInt(json, parent.spacingSmall, SPACING_SMALL));
        spacingMedium = Math.max(0, JsonHelper.getInt(json, parent.spacingMedium, SPACING_MEDIUM));
        spacingLarge = Math.max(0, JsonHelper.getInt(json, parent.spacingLarge, SPACING_LARGE));
        padding = Math.max(0, JsonHelper.getInt(json, parent.padding, PADDING));
        buttonHeight = Math.max(1, JsonHelper.getInt(json, parent.buttonHeight, BUTTON_HEIGHT));
        rowHeight = Math.max(buttonHeight, JsonHelper.getInt(json, parent.rowHeight, ROW_HEIGHT));
        float scale = JsonHelper.getFloat(json, parent.textScale, TEXT_SCALE);
        textScale = scale > 0 ? scale : parent.textScale;
    }

    public static LayoutTheme of(ITheme theme) {
        WidgetThemeEntry<LayoutTheme> entry = theme == null ? null : theme.getWidgetTheme(KEY);
        return entry == null ? DEFAULT : entry.getTheme();
    }

    public int scaled(int textWidth) {
        return (int) Math.ceil(textWidth * textScale);
    }

    public static class Builder extends WidgetThemeBuilder<LayoutTheme, Builder> {

        public Builder values(LayoutTheme values) {
            add(SPACING_SMALL, values.spacingSmall);
            add(SPACING_MEDIUM, values.spacingMedium);
            add(SPACING_LARGE, values.spacingLarge);
            add(PADDING, values.padding);
            add(BUTTON_HEIGHT, values.buttonHeight);
            add(ROW_HEIGHT, values.rowHeight);
            add(TEXT_SCALE, values.textScale);
            return this;
        }
    }
}
