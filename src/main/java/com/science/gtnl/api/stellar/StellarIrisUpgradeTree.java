package com.science.gtnl.api.stellar;

import net.minecraft.util.EnumChatFormatting;

import lombok.Getter;

@Getter
public class StellarIrisUpgradeTree {

    private final String id;
    private final float treeAngle;
    private final boolean repeatable;
    private final int defaultColor;
    private final int tooltipColor;

    public StellarIrisUpgradeTree(String id, float treeAngle, boolean repeatable, int defaultColor, int tooltipColor) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Tree ID must not be empty");
        }
        if (defaultColor < 0 || defaultColor > 0xFFFFFF) {
            throw new IllegalArgumentException("Tree color must be an RGB value");
        }
        if (tooltipColor < 0 || tooltipColor > 0xFFFFFF) {
            throw new IllegalArgumentException("Tree tooltip color must be an RGB value");
        }
        this.id = id;
        this.treeAngle = treeAngle;
        this.repeatable = repeatable;
        this.defaultColor = defaultColor;
        this.tooltipColor = tooltipColor;
    }

    public StellarIrisUpgradeTree(String id, float treeAngle, boolean repeatable, int defaultColor,
        EnumChatFormatting tooltipColor) {
        this(id, treeAngle, repeatable, defaultColor, toRgb(tooltipColor));
    }

    private static int toRgb(EnumChatFormatting formatting) {
        if (formatting == null) {
            throw new IllegalArgumentException("Tree tooltip color must not be null");
        }
        return switch (formatting) {
            case BLACK -> 0x000000;
            case DARK_BLUE -> 0x0000AA;
            case DARK_GREEN -> 0x00AA00;
            case DARK_AQUA -> 0x00AAAA;
            case DARK_RED -> 0xAA0000;
            case DARK_PURPLE -> 0xAA00AA;
            case GOLD -> 0xFFAA00;
            case GRAY -> 0xAAAAAA;
            case DARK_GRAY -> 0x555555;
            case BLUE -> 0x5555FF;
            case GREEN -> 0x55FF55;
            case AQUA -> 0x55FFFF;
            case RED -> 0xFF5555;
            case LIGHT_PURPLE -> 0xFF55FF;
            case YELLOW -> 0xFFFF55;
            case WHITE -> 0xFFFFFF;
            default -> throw new IllegalArgumentException("Tree tooltip formatting must be a color");
        };
    }

}
