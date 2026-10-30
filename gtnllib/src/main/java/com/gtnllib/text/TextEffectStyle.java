package com.gtnllib.text;

import java.util.Objects;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;

/** Serializable visual parameters independent of client rendering classes. */
public record TextEffectStyle(String rendererId, IntList colors, float speed) {

    private static final Pattern IDENTIFIER = Pattern.compile("[a-z0-9_.-]+:[a-z0-9_./-]+");

    public TextEffectStyle {
        Objects.requireNonNull(rendererId, "rendererId");
        if (rendererId.length() > 128 || !IDENTIFIER.matcher(rendererId)
            .matches()) throw new IllegalArgumentException("Invalid effect identifier");
        colors = IntLists.unmodifiable(new IntArrayList(colors));
        if (colors.size() > 8) throw new IllegalArgumentException("At most eight palette colors are supported");
        for (int i = 0; i < colors.size(); i++) {
            if ((colors.getInt(i) & 0xFF000000) != 0)
                throw new IllegalArgumentException("Palette colors must be 24-bit RGB");
        }
        if (!Float.isFinite(speed) || speed < 0)
            throw new IllegalArgumentException("Speed must be finite and nonnegative");
    }

    public TextEffectStyle withColors(int... palette) {
        return new TextEffectStyle(rendererId, IntList.of(palette), speed);
    }

    public TextEffectStyle withSpeed(float value) {
        return new TextEffectStyle(rendererId, colors, value);
    }
}
