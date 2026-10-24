package com.gtnllib.gui.layout;

public record Size(Kind kind, float value, int min, int max) {

    private static final int UNBOUNDED = Integer.MAX_VALUE;

    public static Size fixed(int pixels) {
        return new Size(Kind.FIXED, pixels, 0, UNBOUNDED);
    }

    public static Size content(int pixels) {
        return new Size(Kind.CONTENT, pixels, 0, UNBOUNDED);
    }

    public static Size content(int measured, int padding) {
        return content(measured + padding * 2);
    }

    public static Size fill() {
        return fill(1);
    }

    public static Size fill(float weight) {
        return new Size(Kind.FILL, Math.max(0, weight), 0, UNBOUNDED);
    }

    public static Size percent(float fraction) {
        return new Size(Kind.PERCENT, fraction, 0, UNBOUNDED);
    }

    public Size atLeast(int pixels) {
        return new Size(kind, value, pixels, max);
    }

    public Size atMost(int pixels) {
        return new Size(kind, value, min, pixels);
    }

    public boolean fills() {
        return kind == Kind.FILL;
    }

    public int clamp(int pixels) {
        return Math.max(min, Math.min(max, pixels));
    }

    public int resolve(int parent) {
        return clamp(switch (kind) {
            case FILL -> parent;
            case PERCENT -> Math.round(parent * value);
            default -> Math.round(value);
        });
    }

    public enum Kind {
        FIXED,
        CONTENT,
        FILL,
        PERCENT
    }
}
