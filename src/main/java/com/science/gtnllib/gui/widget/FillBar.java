package com.science.gtnllib.gui.widget;

import java.util.function.DoubleSupplier;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeBuilder;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.utils.JsonHelper;
import com.cleanroommc.modularui.widget.Widget;
import com.google.gson.JsonObject;

public class FillBar extends Widget<FillBar> {

    private DoubleSupplier value = () -> 0;

    public FillBar value(DoubleSupplier value) {
        this.value = value;
        return this;
    }

    public static int filledWidth(double value, int width) {
        double clamped = Double.isNaN(value) ? 0 : Math.max(0, Math.min(1, value));
        return (int) Math.round(clamped * width);
    }

    @Override
    protected WidgetThemeEntry<?> getWidgetThemeInternal(ITheme theme) {
        WidgetThemeEntry<Theme> entry = theme.getWidgetTheme(Theme.KEY);
        return entry != null ? entry : theme.getFallback();
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        Theme colors = widgetTheme.getTheme() instanceof Theme bar ? bar : Theme.KEY.getDefaultValue();
        int width = getArea().w(), height = getArea().h();
        GuiDraw.drawRect(0, 0, width, height, colors.track);
        int filled = filledWidth(value.getAsDouble(), width);
        if (filled > 0) GuiDraw.drawRect(0, 0, filled, height, colors.fill);
    }

    public static class Theme extends WidgetTheme {

        public static final String TRACK = "trackColor", FILL = "fillColor";
        private static final int DEFAULT_SIZE = 18;
        public static final WidgetThemeKey<Theme> KEY = register(new Theme(0xFF2A2A2E, 0xFFDDDDDD));

        public final int track, fill;

        public Theme(int track, int fill) {
            super(DEFAULT_SIZE, DEFAULT_SIZE, null, Color.WHITE.main, Color.WHITE.main, false, Color.WHITE.main);
            this.track = track;
            this.fill = fill;
        }

        public Theme(Theme parent, JsonObject json, JsonObject fallback) {
            super(parent, json, fallback);
            track = JsonHelper.getColor(json, parent.track, TRACK);
            fill = JsonHelper.getColor(json, parent.fill, FILL);
        }

        private static WidgetThemeKey<Theme> register(Theme defaults) {
            return IThemeApi.get()
                .widgetThemeKeyBuilder("gtnllibFillBar", Theme.class)
                .defaultTheme(defaults)
                .defaultHoverTheme(defaults)
                .parser(Theme::new)
                .register();
        }

        public static class Builder extends WidgetThemeBuilder<Theme, Builder> {

            public Builder colors(int track, int fill) {
                add(TRACK, track);
                add(FILL, fill);
                return this;
            }
        }
    }
}
