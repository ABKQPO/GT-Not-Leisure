package com.science.gtnl.client.text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.science.gtnl.utils.text.effect.TextEffectStyle;

/** Register effects during client initialization, before rendering starts. */
public class TextEffectRegistry {

    private static final Map<String, TextEffect> EFFECTS = new LinkedHashMap<>();
    private static List<String> identifiers = List.of();

    private TextEffectRegistry() {}

    public static void register(String identifier, TextEffect effect) {
        new TextEffectStyle(identifier, List.of(), 1);
        if (EFFECTS.putIfAbsent(identifier, Objects.requireNonNull(effect, "effect")) != null) {
            throw new IllegalArgumentException("Text effect already registered: " + identifier);
        }
        identifiers = List.copyOf(EFFECTS.keySet());
    }

    /** Returns an immutable snapshot in registration order, including effects supplied by other mods. */
    public static List<String> identifiers() {
        return identifiers;
    }

    public static TextEffect get(String identifier) {
        return EFFECTS.get(identifier);
    }

    public static void reload() {
        for (TextEffect effect : EFFECTS.values()) effect.close();
    }
}
