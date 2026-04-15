package com.science.gtnl.utils.enums;

import gregtech.api.enums.Textures;

public enum HPCAModifier {

    RED(BlockIcons.OVERLAY_FRONT_INDICATOR_RED, 1, 3, 1.25, 1.25, 1.5, 1.25),
    YELLOW(BlockIcons.OVERLAY_FRONT_INDICATOR_YELLOW, 1.5, 1.5, 2, 1.5, 1.25, 1.5),
    GREEN(BlockIcons.OVERLAY_FRONT_INDICATOR_GREEN, 2, 1.25, 3, 1, 2, 1.5);

    public final Textures.BlockIcons.CustomIcon overlay;
    public final double computationCoefficientX;
    public final double coolantCoefficientX;
    public final double heatCoefficientX;
    public final double computationCoefficientY;
    public final double coolantCoefficientY;
    public final double heatCoefficientY;

    HPCAModifier(Textures.BlockIcons.CustomIcon overlay, double computationX, double coolantX, double heatX,
        double computationY, double coolantY, double heatY) {
        this.overlay = overlay;
        this.computationCoefficientX = computationX;
        this.coolantCoefficientX = coolantX;
        this.heatCoefficientX = heatX;
        this.computationCoefficientY = computationY;
        this.coolantCoefficientY = coolantY;
        this.heatCoefficientY = heatY;
    }
}
