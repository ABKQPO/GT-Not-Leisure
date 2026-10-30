package com.gtnllib.gui.theme;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.cleanroommc.modularui.api.ITheme;
import com.cleanroommc.modularui.api.IThemeApi;
import com.cleanroommc.modularui.theme.AbstractTheme;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.theme.WidgetThemeKey;
import com.cleanroommc.modularui.theme.WidgetThemeParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@SideOnly(Side.CLIENT)
public class VariantTheme extends AbstractTheme {

    private static final JsonObject EMPTY = new JsonObject();

    private final Map<WidgetThemeKey<?>, WidgetThemeEntry<?>> widgetThemes;

    private VariantTheme(String id, ITheme parent, Map<WidgetThemeKey<?>, WidgetThemeEntry<?>> widgetThemes) {
        super(id, parent);
        this.widgetThemes = widgetThemes;
    }

    public static VariantTheme parse(String id, ITheme parent, JsonObject json) {
        Map<WidgetThemeKey<?>, WidgetThemeEntry<?>> widgetThemes = new Object2ObjectOpenHashMap<>();
        WidgetTheme fallback = new WidgetTheme(
            parent.getFallback()
                .getTheme(),
            json,
            null);
        JsonObject hover = object(json, IThemeApi.HOVER_SUFFIX);
        if (hover == null) hover = object(json, IThemeApi.FALLBACK.getFullName() + IThemeApi.HOVER_SUFFIX);
        WidgetTheme fallbackHover = hover == null ? fallback : new WidgetTheme(fallback, hover, null);
        widgetThemes.put(IThemeApi.FALLBACK, new WidgetThemeEntry<>(IThemeApi.FALLBACK, fallback, fallbackHover));
        for (WidgetThemeKey<?> key : IThemeApi.get()
            .getWidgetThemeKeys()) if (key != IThemeApi.FALLBACK) parse(widgetThemes, parent, key, json);
        return new VariantTheme(id, parent, widgetThemes);
    }

    @SuppressWarnings("unchecked")
    private static <T extends WidgetTheme> void parse(Map<WidgetThemeKey<?>, WidgetThemeEntry<?>> widgetThemes,
        ITheme parent, WidgetThemeKey<T> key, JsonObject json) {
        JsonObject standard = object(json, key.getFullName()),
            hover = object(json, key.getFullName() + IThemeApi.HOVER_SUFFIX);
        boolean definedStandard = standard != null, definedHover = hover != null;
        if (!definedStandard && !definedHover) {
            if (key.isSubWidgetTheme()) {
                WidgetThemeEntry<T> entry = (WidgetThemeEntry<T>) widgetThemes.get(key.getParent());
                widgetThemes.put(key, new WidgetThemeEntry<>(key, entry.getTheme(), entry.getHoverTheme()));
                return;
            }
            standard = EMPTY;
            hover = EMPTY;
        }
        JsonObject fallback = key.isSubWidgetTheme() ? null : json;
        WidgetThemeParser<T> parser = key.getParser();
        T theme = standard == null ? parent.getWidgetTheme(key)
            .getTheme() : parser.parse(inherited(widgetThemes, parent, key), standard, fallback);
        if (definedStandard && !definedHover) {
            standard.addProperty(IThemeApi.BACKGROUND, "none");
            hover = standard;
        }
        T hoverParent = standard != EMPTY ? theme
            : parent.getWidgetTheme(key)
                .getHoverTheme();
        widgetThemes.put(key, new WidgetThemeEntry<>(key, theme, parser.parse(hoverParent, hover, fallback)));
    }

    @SuppressWarnings("unchecked")
    private static <T extends WidgetTheme> T inherited(Map<WidgetThemeKey<?>, WidgetThemeEntry<?>> widgetThemes,
        ITheme parent, WidgetThemeKey<T> key) {
        if (!key.isSubWidgetTheme()) return parent.getWidgetTheme(key)
            .getTheme();
        return ((WidgetThemeEntry<T>) widgetThemes.get(key.getParent())).getTheme();
    }

    private static JsonObject object(JsonObject json, String key) {
        JsonElement element = json.get(key);
        return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
    }

    @Override
    public Collection<WidgetThemeEntry<?>> getWidgetThemes() {
        return Collections.unmodifiableCollection(widgetThemes.values());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends WidgetTheme> WidgetThemeEntry<T> getWidgetTheme(WidgetThemeKey<T> key) {
        WidgetThemeEntry<?> entry = widgetThemes.get(key);
        if (entry == null && key.isSubWidgetTheme()) entry = widgetThemes.get(key.getParent());
        return (WidgetThemeEntry<T>) entry;
    }
}
