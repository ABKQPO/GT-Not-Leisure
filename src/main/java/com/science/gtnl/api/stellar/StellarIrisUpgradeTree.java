package com.science.gtnl.api.stellar;

import net.minecraft.util.EnumChatFormatting;

import com.science.gtnl.utils.ColorUtils;

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
            case BLACK -> ColorUtils.vanillaColor(0);
            case DARK_BLUE -> ColorUtils.vanillaColor(1);
            case DARK_GREEN -> ColorUtils.vanillaColor(2);
            case DARK_AQUA -> ColorUtils.vanillaColor(3);
            case DARK_RED -> ColorUtils.vanillaColor(4);
            case DARK_PURPLE -> ColorUtils.vanillaColor(5);
            case GOLD -> ColorUtils.vanillaColor(6);
            case GRAY -> ColorUtils.vanillaColor(7);
            case DARK_GRAY -> ColorUtils.vanillaColor(8);
            case BLUE -> ColorUtils.vanillaColor(9);
            case GREEN -> ColorUtils.vanillaColor(10);
            case AQUA -> ColorUtils.vanillaColor(11);
            case RED -> ColorUtils.vanillaColor(12);
            case LIGHT_PURPLE -> ColorUtils.vanillaColor(13);
            case YELLOW -> ColorUtils.vanillaColor(14);
            case WHITE -> ColorUtils.vanillaColor(15);
            default -> throw new IllegalArgumentException("Tree tooltip formatting must be a color");
        };
    }

}
