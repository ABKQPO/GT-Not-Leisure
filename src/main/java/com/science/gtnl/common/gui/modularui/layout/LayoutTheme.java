package com.science.gtnl.common.gui.modularui.layout;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeBuilder;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.JsonHelper;
import com.google.gson.JsonObject;
import com.science.gtnl.common.gui.modularui.theme.ThemeStyle;

public class LayoutTheme extends WidgetTheme {

    public static final String SPACING_SMALL = "spacingSmall", SPACING_MEDIUM = "spacingMedium",
        SPACING_LARGE = "spacingLarge", PADDING = "padding", BUTTON_HEIGHT = "buttonHeight", ROW_HEIGHT = "rowHeight",
        TEXT_SCALE = "textScale";
    public static final LayoutTheme DEFAULT = new LayoutTheme(ThemeStyle.DEFAULT);
    public static final WidgetThemeKey<LayoutTheme> KEY = IThemeApi.get()
        .widgetThemeKeyBuilder("gtnlLayout", LayoutTheme.class)
        .defaultTheme(DEFAULT)
        .defaultHoverTheme(DEFAULT)
        .parser(LayoutTheme::new)
        .register();

    public final int spacingSmall, spacingMedium, spacingLarge, padding, buttonHeight, rowHeight;
    public final float textScale;

    public LayoutTheme(ThemeStyle style) {
        super(0, 0, null, Color.WHITE.main, Color.WHITE.main, false, Color.WHITE.main);
        spacingSmall = style.spacingSmall();
        spacingMedium = style.spacingMedium();
        spacingLarge = style.spacingLarge();
        padding = style.padding();
        buttonHeight = style.buttonHeight();
        rowHeight = style.rowHeight();
        textScale = style.textScale();
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

        public Builder style(ThemeStyle style) {
            add(SPACING_SMALL, style.spacingSmall());
            add(SPACING_MEDIUM, style.spacingMedium());
            add(SPACING_LARGE, style.spacingLarge());
            add(PADDING, style.padding());
            add(BUTTON_HEIGHT, style.buttonHeight());
            add(ROW_HEIGHT, style.rowHeight());
            add(TEXT_SCALE, style.textScale());
            return this;
        }
    }
}
