package com.science.gtnl.utils.machine.greenHouseManager;

public enum GreenHouseViewMode {

    SEEDS,
    BLOCKS,
    STATUS;

    private static final GreenHouseViewMode[] VALUES = values();

    public GreenHouseViewMode next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public static GreenHouseViewMode fromOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < VALUES.length) {
            return VALUES[ordinal];
        }
        return SEEDS;
    }
}
