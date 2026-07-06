package com.science.gtnl.utils.machine.greenHouseManager;

public enum GreenHouseViewMode {

    SEEDS,
    BLOCKS,
    STATUS;

    private static final GreenHouseViewMode[] VALUES = values();

    public GreenHouseViewMode next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public GreenHouseViewMode previous() {
        return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
    }

    public GreenHouseViewMode nextWithoutBlocks() {
        return withoutBlocks() == STATUS ? SEEDS : STATUS;
    }

    public GreenHouseViewMode previousWithoutBlocks() {
        return nextWithoutBlocks();
    }

    public GreenHouseViewMode withoutBlocks() {
        return this == BLOCKS ? STATUS : this;
    }

    public static GreenHouseViewMode fromOrdinal(int ordinal) {
        if (ordinal >= 0 && ordinal < VALUES.length) {
            return VALUES[ordinal];
        }
        return STATUS;
    }

    public static GreenHouseViewMode fromOrdinalWithoutBlocks(int ordinal) {
        return fromOrdinal(ordinal).withoutBlocks();
    }
}
