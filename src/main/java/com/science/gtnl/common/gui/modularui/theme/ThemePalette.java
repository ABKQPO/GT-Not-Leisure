package com.science.gtnl.common.gui.modularui.theme;

import java.util.function.IntUnaryOperator;

import com.science.gtnl.client.fx.FxColor;

public record ThemePalette(int accent, int accentDim, int hover, int text, int hint, int panelTop, int panelBottom,
    int card, int cardEdge, int button, int buttonHover, int tabHover, int well, int wellEdge, int slotHover, int track,
    int switchOff, int knob, int knobOff, int textOn, int textDim, int textOff, int cursor, int cursorText, int tick,
    int milestone, int danger) {

    private static final int OPAQUE = 0xFF000000, RGB = 0xFFFFFF, LIGHTNESS_STEPS = 24;
    private static final double MIN_ACCENT_CONTRAST = 3, MAX_ACCENT_LIGHTNESS = 0.85, ACCENT_FAMILY_SATURATION = 0.5;

    public ThemePalette follow(int glow) {
        int from = accent & RGB, to = readableAccent(glow) & RGB;
        double[] source = FxColor.hsl(from), target = FxColor.hsl(to);
        return map(color -> shift(color, from, to, source, target), IntUnaryOperator.identity());
    }

    public int readableAccent(int glow) {
        int background = panelTop & RGB;
        double[] hsl = FxColor.hsl(glow & RGB);
        int color = hsl[2] > MAX_ACCENT_LIGHTNESS ? FxColor.hsl(hsl[0], hsl[1], MAX_ACCENT_LIGHTNESS) : glow & RGB;
        if (FxColor.contrast(color, background) >= MIN_ACCENT_CONTRAST) return OPAQUE | color;
        double dark = Math.min(hsl[2], MAX_ACCENT_LIGHTNESS), light = MAX_ACCENT_LIGHTNESS;
        for (int step = 0; step < LIGHTNESS_STEPS; step++) {
            double middle = (dark + light) / 2;
            if (FxColor.contrast(FxColor.hsl(hsl[0], hsl[1], middle), background) < MIN_ACCENT_CONTRAST) dark = middle;
            else light = middle;
        }
        return OPAQUE | FxColor.hsl(hsl[0], hsl[1], light);
    }

    public int selectedText() {
        int background = FxColor.over(buttonHover, panelTop & RGB), dark = OPAQUE | panelBottom & RGB;
        return FxColor.contrast(text & RGB, background) >= FxColor.contrast(dark & RGB, background) ? text : dark;
    }

    public boolean selectedTextShadow() {
        return selectedText() == text;
    }

    private static int shift(int color, int from, int to, double[] source, double[] target) {
        int alpha = color & OPAQUE, rgb = color & RGB;
        if (rgb == from) return alpha | to;
        double[] hsl = FxColor.hsl(rgb);
        double saturation = source[1] > 0 ? Math.min(1, hsl[1] * target[1] / source[1]) : hsl[1];
        double lightness = hsl[1] >= ACCENT_FAMILY_SATURATION ? relight(hsl[2], source[2], target[2]) : hsl[2];
        return alpha | FxColor.hsl(hsl[0] + target[0] - source[0], saturation, lightness);
    }

    private static double relight(double lightness, double from, double to) {
        if (lightness >= from) return from < 1 ? to + (lightness - from) * (1 - to) / (1 - from) : to;
        return from > 0 ? lightness * to / from : to;
    }

    public ThemePalette map(IntUnaryOperator color) {
        return map(color, color);
    }

    private ThemePalette map(IntUnaryOperator color, IntUnaryOperator signal) {
        return new ThemePalette(
            color.applyAsInt(accent),
            color.applyAsInt(accentDim),
            color.applyAsInt(hover),
            color.applyAsInt(text),
            color.applyAsInt(hint),
            color.applyAsInt(panelTop),
            color.applyAsInt(panelBottom),
            color.applyAsInt(card),
            color.applyAsInt(cardEdge),
            color.applyAsInt(button),
            color.applyAsInt(buttonHover),
            color.applyAsInt(tabHover),
            color.applyAsInt(well),
            color.applyAsInt(wellEdge),
            color.applyAsInt(slotHover),
            color.applyAsInt(track),
            color.applyAsInt(switchOff),
            color.applyAsInt(knob),
            color.applyAsInt(knobOff),
            color.applyAsInt(textOn),
            color.applyAsInt(textDim),
            color.applyAsInt(textOff),
            color.applyAsInt(cursor),
            color.applyAsInt(cursorText),
            color.applyAsInt(tick),
            color.applyAsInt(milestone),
            signal.applyAsInt(danger));
    }
}
