package com.science.gtnl.common.celestweave;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;

import net.minecraft.nbt.NBTTagCompound;

import com.science.gtnl.config.MainConfig;

public enum CelestweaveCosmeticOption {

    GLOW_COLOR("GlowColor", Kind.COLOR, "glow_color", 0F, () -> 0xFFFFFF, CelestweaveCosmetic.DEFAULT_COLOR, 1F, null,
        null),
    STRIP_STYLE("StripFx", Kind.CHOICE, "strip_fx", 0F, () -> CelestweaveStripStyle.values().length - 1, 0F, 1F,
        CelestweaveStripStyle.ids(), null),
    STRIP_SPEED("StripSpeed", Kind.DECIMAL, "strip_speed", 0.25F, () -> 3F, 1F, 0.25F, null, null,
        cosmetic -> cosmetic.stripStyle() != CelestweaveStripStyle.STEADY),
    IDLE_ARCS("IdleArcs", Kind.INT, "idle_arcs", -1F, () -> MainConfig.item.celestweave.maxIdleArcs, -1F, 1F, null,
        "by_tier"),
    ARC_FREQ("ArcFreq", Kind.DECIMAL, "arc_freq", 0.25F, () -> 3F, 1F, 0.25F, null, null),
    ARC_SPREAD("ArcSpread", Kind.DECIMAL, "arc_spread", 0.3F, () -> 2.5F, 1F, 0.1F, null, null),
    ARC_BRIGHT("ArcBright", Kind.DECIMAL, "arc_bright", 0.3F, () -> 1.6F, 1F, 0.1F, null, null),
    ARC_EVENTS("ArcEvents", Kind.FLAGS, "event", 0F, null, CelestweaveCosmetic.EVENT_ALL, 1F,
        new String[] { "hit", "dash", "land", "reflect", "wing", "antenna", "low" }, null);

    public enum Kind {
        COLOR,
        CHOICE,
        INT,
        DECIMAL,
        FLAGS
    }

    public final String key, lang, minLabel;
    public final Kind kind;
    public final float min, fallback, step;
    private final DoubleSupplier max;
    private final List<String> names;
    private final Predicate<CelestweaveCosmetic> applies;

    CelestweaveCosmeticOption(String key, Kind kind, String lang, float min, DoubleSupplier max, float fallback,
        float step, String[] names, String minLabel) {
        this(key, kind, lang, min, max, fallback, step, names, minLabel, null);
    }

    CelestweaveCosmeticOption(String key, Kind kind, String lang, float min, DoubleSupplier max, float fallback,
        float step, String[] names, String minLabel, Predicate<CelestweaveCosmetic> applies) {
        this.key = key;
        this.kind = kind;
        this.lang = lang;
        this.min = min;
        this.max = max;
        this.fallback = fallback;
        this.step = step;
        this.names = names == null ? Collections.emptyList()
            : Collections.unmodifiableList(Arrays.asList(names.clone()));
        this.minLabel = minLabel;
        this.applies = applies;
    }

    public static CelestweaveCosmeticOption byKey(String key) {
        for (CelestweaveCosmeticOption option : values()) if (option.key.equals(key)) return option;
        return null;
    }

    public float max() {
        return kind == Kind.FLAGS ? (1 << names.size()) - 1 : (float) max.getAsDouble();
    }

    public List<String> names() {
        return names;
    }

    public int flag(int index) {
        return 1 << index;
    }

    public boolean appliesTo(CelestweaveCosmetic cosmetic) {
        return applies == null || applies.test(cosmetic);
    }

    public float clamp(float value) {
        return switch (kind) {
            case COLOR -> (int) value & 0xFFFFFF;
            case FLAGS -> (int) value & (int) max();
            case CHOICE, INT -> Math.max(min, Math.min(max(), Math.round(value)));
            case DECIMAL -> Float.isFinite(value) ? Math.max(min, Math.min(max(), value)) : min;
        };
    }

    float read(NBTTagCompound tag) {
        if (!tag.hasKey(key)) return fallback;
        return switch (kind) {
            case COLOR -> tag.getInteger(key) & 0xFFFFFF;
            case FLAGS -> tag.getInteger(key) & (int) max();
            case CHOICE -> tag.hasKey(key, 8) ? Math.max(0, names.indexOf(tag.getString(key)))
                : clamp(tag.getInteger(key));
            case INT -> clamp(tag.getInteger(key));
            case DECIMAL -> clamp(tag.getFloat(key));
        };
    }

    void write(NBTTagCompound tag, float value) {
        switch (kind) {
            case CHOICE -> tag.setString(key, names.get((int) value));
            case DECIMAL -> tag.setFloat(key, value);
            default -> tag.setInteger(key, (int) value);
        }
    }
}
