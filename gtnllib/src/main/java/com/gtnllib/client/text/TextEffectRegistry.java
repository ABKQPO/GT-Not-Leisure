package com.gtnllib.client.text;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.gtnllib.text.TextEffectStyle;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

/** Register effects during client initialization, before rendering starts. */
public class TextEffectRegistry {

    private static final Map<String, TextEffect> EFFECTS = new Object2ObjectLinkedOpenHashMap<>();
    private static List<String> identifiers = List.of();

    public static void register(String identifier, TextEffect effect) {
        new TextEffectStyle(identifier, IntList.of(), 1);
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
