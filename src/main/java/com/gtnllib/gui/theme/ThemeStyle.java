package com.gtnllib.gui.theme;

import com.gtnllib.gui.widget.LayoutTheme;

public record ThemeStyle(int cornerLength, int cornerThickness, int cardAccentTop, int cardAccentWidth,
    int cardAccentHeight, int spacingSmall, int spacingMedium, int spacingLarge, int padding, int buttonHeight,
    int rowHeight, float textScale) {

    private static final int CORNER_LENGTH = 6, CORNER_THICKNESS = 2, CARD_ACCENT_TOP = 3, CARD_ACCENT_WIDTH = 2,
        CARD_ACCENT_HEIGHT = 10;
    public static final ThemeStyle DEFAULT = new ThemeStyle(
        CORNER_LENGTH,
        CORNER_THICKNESS,
        CARD_ACCENT_TOP,
        CARD_ACCENT_WIDTH,
        CARD_ACCENT_HEIGHT,
        LayoutTheme.DEFAULT.spacingSmall,
        LayoutTheme.DEFAULT.spacingMedium,
        LayoutTheme.DEFAULT.spacingLarge,
        LayoutTheme.DEFAULT.padding,
        LayoutTheme.DEFAULT.buttonHeight,
        LayoutTheme.DEFAULT.rowHeight,
        LayoutTheme.DEFAULT.textScale);

    public LayoutTheme layout() {
        return new LayoutTheme(spacingSmall, spacingMedium, spacingLarge, padding, buttonHeight, rowHeight, textScale);
    }
}
