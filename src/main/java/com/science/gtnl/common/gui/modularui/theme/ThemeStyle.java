package com.science.gtnl.common.gui.modularui.theme;

public record ThemeStyle(int cornerLength, int cornerThickness, int cardAccentTop, int cardAccentWidth,
    int cardAccentHeight) {

    private static final int CORNER_LENGTH = 6, CORNER_THICKNESS = 2, CARD_ACCENT_TOP = 3, CARD_ACCENT_WIDTH = 2,
        CARD_ACCENT_HEIGHT = 10;
    public static final ThemeStyle DEFAULT = new ThemeStyle(
        CORNER_LENGTH,
        CORNER_THICKNESS,
        CARD_ACCENT_TOP,
        CARD_ACCENT_WIDTH,
        CARD_ACCENT_HEIGHT);
}
