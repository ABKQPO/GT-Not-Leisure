package com.gtnllib.client.hud;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import com.google.gson.JsonPrimitive;
import com.gtnllib.client.hud.HudElement.Choice;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class HudOption {

    public enum Kind {
        TOGGLE,
        NUMBER,
        CHOICE
    }

    public final String id, lang;
    public final Kind kind;
    public final double min, max;
    public final int steps;
    private final boolean on;
    private final double number;
    private final String choice;
    private final List<Choice> choices;

    private HudOption(String id, String lang, Kind kind, boolean on, double min, double max, int steps, double number,
        String choice, List<Choice> choices) {
        this.id = Objects.requireNonNull(id, "id");
        this.lang = Objects.requireNonNull(lang, "lang");
        if (id.isEmpty()) throw new IllegalArgumentException("HUD option id must not be empty");
        this.kind = kind;
        this.on = on;
        this.min = min;
        this.max = max;
        this.steps = steps;
        this.number = number;
        this.choice = choice;
        this.choices = choices;
    }

    public static HudOption toggle(String id, String lang, boolean on) {
        return new HudOption(id, lang, Kind.TOGGLE, on, 0, 1, 1, 0, null, Collections.emptyList());
    }

    public static HudOption number(String id, String lang, double min, double max, int steps, double value) {
        if (!(max > min) || steps < 1 || !Double.isFinite(min) || !Double.isFinite(max))
            throw new IllegalArgumentException("Bad HUD option range " + min + ".." + max + " for " + id);
        if (!(value >= min && value <= max))
            throw new IllegalArgumentException("HUD option " + id + " default " + value + " is out of range");
        return new HudOption(
            id,
            lang,
            Kind.NUMBER,
            false,
            min,
            max,
            steps,
            snap(value, min, max, steps),
            null,
            Collections.emptyList());
    }

    public static HudOption choice(String id, String lang, String value, Choice... choices) {
        List<Choice> list = Collections.unmodifiableList(Arrays.asList(choices.clone()));
        Set<String> ids = new ObjectOpenHashSet<>();
        for (Choice each : list) if (!ids.add(each.id()))
            throw new IllegalArgumentException("Duplicate choice " + each.id() + " in HUD option " + id);
        if (!ids.contains(value)) throw new IllegalArgumentException("HUD option " + id + " has no choice " + value);
        return new HudOption(id, lang, Kind.CHOICE, false, 0, 1, 1, 0, value, list);
    }

    public List<Choice> choices() {
        return choices;
    }

    public boolean defaultOn() {
        return on;
    }

    public double defaultNumber() {
        return number;
    }

    public String defaultChoice() {
        return choice;
    }

    public boolean on(JsonPrimitive saved) {
        return saved != null && saved.isBoolean() ? saved.getAsBoolean() : on;
    }

    public double number(JsonPrimitive saved) {
        return saved != null && saved.isNumber() ? snap(saved.getAsDouble()) : number;
    }

    public String choice(JsonPrimitive saved) {
        if (saved == null || !saved.isString()) return choice;
        String id = saved.getAsString();
        for (Choice each : choices) if (each.id()
            .equals(id)) return id;
        return choice;
    }

    public double snap(double value) {
        return Double.isFinite(value) ? snap(value, min, max, steps) : number;
    }

    private static double snap(double value, double min, double max, int steps) {
        double step = (max - min) / steps, clamped = Math.max(min, Math.min(max, value));
        return Math.min(max, min + Math.round((clamped - min) / step) * step);
    }

    public String format(double value) {
        double step = (max - min) / steps;
        int decimals = step >= 1 && step == Math.rint(step) ? 0 : step >= 0.1 ? 1 : 2;
        return String.format(Locale.ROOT, "%." + decimals + "f", value);
    }
}
