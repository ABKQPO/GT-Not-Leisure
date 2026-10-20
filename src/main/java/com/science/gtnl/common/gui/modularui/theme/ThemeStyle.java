package com.science.gtnl.common.gui.modularui.theme;

public record ThemeStyle(int cornerLength, int cornerThickness, int cardAccentTop, int cardAccentWidth,
    int cardAccentHeight, int spacingSmall, int spacingMedium, int spacingLarge, int padding, int buttonHeight,
    int rowHeight, float textScale) {

    private static final int CORNER_LENGTH = 6, CORNER_THICKNESS = 2, CARD_ACCENT_TOP = 3, CARD_ACCENT_WIDTH = 2,
        CARD_ACCENT_HEIGHT = 10, SPACING_SMALL = 2, SPACING_MEDIUM = 4, SPACING_LARGE = 10, PADDING = 8,
        BUTTON_HEIGHT = 14, ROW_HEIGHT = 16;
    private static final float TEXT_SCALE = 1F;
    public static final ThemeStyle DEFAULT = new ThemeStyle(
        CORNER_LENGTH,
        CORNER_THICKNESS,
        CARD_ACCENT_TOP,
        CARD_ACCENT_WIDTH,
        CARD_ACCENT_HEIGHT,
        SPACING_SMALL,
        SPACING_MEDIUM,
        SPACING_LARGE,
        PADDING,
        BUTTON_HEIGHT,
        ROW_HEIGHT,
        TEXT_SCALE);
}
