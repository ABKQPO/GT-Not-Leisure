package com.science.gtnl.api.topology;

/**
 * Link colours are picked to stay clear of the node health palette: nodes are green, amber, red or slate, so no
 * link hue may land in those families, and no two link hues may sit next to each other either.
 */
public enum TopologyLinkKind {

    STORAGE(0x4FC3F7),
    ENERGY(0x7E57C2),
    TUNNEL(0xD500F9),
    TOGGLE(0xFF7043),
    QUANTUM(0x00E5FF),
    WIRELESS(0xFF4081),
    ADJACENT(0x90A4AE);

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
