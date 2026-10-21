package com.science.gtnl.common.celestweave;

import java.util.Locale;

public enum CelestweaveStripStyle {

    FLICKER(false),
    STEADY(false),
    BREATH(true),
    FLOW(true),
    RAINBOW(true),
    GRADIENT(true);

    public final String id = name().toLowerCase(Locale.ROOT);
    public final boolean usesSpeed;

    CelestweaveStripStyle(boolean usesSpeed) {
        this.usesSpeed = usesSpeed;
    }

    public static String[] ids() {
        String[] ids = new String[values().length];
        for (CelestweaveStripStyle style : values()) ids[style.ordinal()] = style.id;
        return ids;
    }
}
