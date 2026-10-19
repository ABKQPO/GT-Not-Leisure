package com.science.gtnl.common.celestweave;

import java.util.Locale;

public enum CelestweaveStripStyle {

    STEADY,
    BREATH,
    FLOW,
    RAINBOW,
    GRADIENT;

    public final String id = name().toLowerCase(Locale.ROOT);

    public static String[] ids() {
        String[] ids = new String[values().length];
        for (CelestweaveStripStyle style : values()) ids[style.ordinal()] = style.id;
        return ids;
    }
}
