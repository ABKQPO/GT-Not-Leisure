package com.gtnllib.gui.widget;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeBuilder;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.widget.Widget;
import com.google.gson.JsonObject;

public class Divider extends Widget<Divider> {

    public static final int HEIGHT = 2;

    private int color;

    public Divider color(int color) {
        this.color = color;
        return this;
    }

    @Override
    protected WidgetThemeEntry<?> getWidgetThemeInternal(ITheme theme) {
        WidgetThemeEntry<Theme> entry = theme.getWidgetTheme(Theme.KEY);
        return entry != null ? entry : theme.getFallback();
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        Theme look = widgetTheme.getTheme() instanceof Theme divider ? divider : Theme.KEY.getDefaultValue();
        int width = getArea().w(), height = getArea().h();
        if (look.skin != null) look.skin.draw(0, 0, width, height, color);
        else GuiDraw.drawRect(0, height - 1, width, 1, color, 0, color, 0);
    }

    public static class Theme extends WidgetTheme {

        public static final String SKIN = "skin";
        private static final int DEFAULT_SIZE = 18;
        public static final WidgetThemeKey<Theme> KEY = register(new Theme());

        public final SkinDrawable skin;

        private Theme() {
            super(DEFAULT_SIZE, DEFAULT_SIZE, null, Color.WHITE.main, Color.WHITE.main, false, Color.WHITE.main);
            skin = null;
        }

        public Theme(Theme parent, JsonObject json, JsonObject fallback) {
            super(parent, json, fallback);
            skin = SkinDrawable.read(json, SKIN, parent.skin);
        }

        private static WidgetThemeKey<Theme> register(Theme defaults) {
            return IThemeApi.get()
                .widgetThemeKeyBuilder("gtnllibDivider", Theme.class)
                .defaultTheme(defaults)
                .defaultHoverTheme(defaults)
                .parser(Theme::new)
                .register();
        }

        public static class Builder extends WidgetThemeBuilder<Theme, Builder> {

            public Builder skin(SkinDrawable skin) {
                SkinDrawable.write(this, SKIN, skin);
                return this;
            }
        }
    }
}
