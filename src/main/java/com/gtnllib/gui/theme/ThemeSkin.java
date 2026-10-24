package com.gtnllib.gui.theme;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.gtnllib.gui.widget.SkinDrawable;

public class ThemeSkin {

    public static final ThemeSkin NONE = new ThemeSkin(new EnumMap<>(Part.class));

    private final Map<Part, SkinDrawable> parts;

    private ThemeSkin(Map<Part, SkinDrawable> parts) {
        this.parts = Collections.unmodifiableMap(parts);
    }

    public ThemeSkin with(Part part, SkinDrawable skin) {
        Map<Part, SkinDrawable> copy = new EnumMap<>(Part.class);
        copy.putAll(parts);
        copy.put(part, skin.copy());
        return new ThemeSkin(copy);
    }

    public boolean has(Part part) {
        return parts.containsKey(part);
    }

    public SkinDrawable get(Part part) {
        SkinDrawable skin = parts.get(part);
        return skin == null ? null : skin.copy();
    }

    public IDrawable plain(Part part, Supplier<IDrawable> fallback) {
        return lit(part, 0, fallback);
    }

    public IDrawable lit(Part part, int glowColor, Supplier<IDrawable> fallback) {
        SkinDrawable skin = get(part);
        return skin == null ? fallback.get() : skin.glowColor(glowColor);
    }

    public enum Part {
        PANEL,
        CARD,
        BUTTON,
        SLOT,
        SWITCH_TRACK,
        SWITCH_KNOB,
        SLIDER_TRACK,
        SLIDER_FILL,
        SLIDER_TAG,
        DIVIDER,
        CLOSE
    }
}
