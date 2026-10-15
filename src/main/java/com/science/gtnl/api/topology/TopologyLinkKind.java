package com.science.gtnl.api.topology;

public enum TopologyLinkKind {

    STORAGE(0x4FC3F7),
    ENERGY(0xFFD54F),
    TUNNEL(0xBA68C8),
    TOGGLE(0xFF8A65),
    QUANTUM(0x4DD0E1),
    ADJACENT(0x9E9E9E);

    private static final TopologyLinkKind[] VALUES = values();

    private final int color;

    TopologyLinkKind(int color) {
        this.color = color;
    }

    public int color() {
        return color;
    }

    public String key() {
        return name().toLowerCase();
    }

    public static TopologyLinkKind byOrdinal(int ordinal) {
        return ordinal < 0 || ordinal >= VALUES.length ? ADJACENT : VALUES[ordinal];
    }
}
