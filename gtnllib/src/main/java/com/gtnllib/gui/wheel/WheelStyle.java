package com.gtnllib.gui.wheel;

import java.util.Objects;

public class WheelStyle {

    public static final WheelStyle DEFAULT = builder().build();

    public final float innerRadiusScale;
    public final float outerRadiusScale;
    public final float minInnerRadius;
    public final float separatorThickness;
    public final long openDelayMs;
    public final float openAnimationMs;
    public final float closeAnimationMs;
    public final float highlightLag;
    public final int discColor;
    public final boolean frosted;
    public final int frostedTint;
    public final int lineColor;
    public final int textColor;
    public final float titleScale;
    public final WheelHighlight highlight;
    public final int highlightColor;
    public final float deadZone;

    private WheelStyle(Builder builder) {
        innerRadiusScale = builder.innerRadiusScale;
        outerRadiusScale = builder.outerRadiusScale;
        minInnerRadius = builder.minInnerRadius;
        separatorThickness = builder.separatorThickness;
        openDelayMs = builder.openDelayMs;
        openAnimationMs = builder.openAnimationMs;
        closeAnimationMs = builder.closeAnimationMs;
        highlightLag = builder.highlightLag;
        discColor = builder.discColor;
        frosted = builder.frosted;
        frostedTint = builder.frostedTint;
        lineColor = builder.lineColor;
        textColor = builder.textColor;
        titleScale = builder.titleScale;
        highlight = builder.highlight;
        highlightColor = builder.highlightColor;
        deadZone = builder.deadZone;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder().radii(innerRadiusScale, outerRadiusScale, minInnerRadius)
            .separatorThickness(separatorThickness)
            .timing(openDelayMs, openAnimationMs, closeAnimationMs)
            .highlightLag(highlightLag)
            .discColor(discColor)
            .frosted(frosted, frostedTint)
            .lineColor(lineColor)
            .textColor(textColor)
            .titleScale(titleScale)
            .highlight(highlight, highlightColor)
            .deadZone(deadZone);
    }

    public static class Builder {

        private float innerRadiusScale = 0.17F;
        private float outerRadiusScale = 0.33F;
        private float minInnerRadius = 15F;
        private float separatorThickness = 2F;
        private long openDelayMs = 150L;
        private float openAnimationMs = 300F;
        private float closeAnimationMs = 150F;
        private float highlightLag = 5F;
        private int discColor = 0x88000000;
        private boolean frosted = true;
        private int frostedTint = 0x66FFFFFF;
        private int lineColor = 0xFFFFFFFF;
        private int textColor = 0xFDFDFD;
        private float titleScale = 1.4F;
        private WheelHighlight highlight = WheelHighlight.ANNULAR_SECTOR;
        private int highlightColor = 0xFFFABC02;
        private float deadZone = 15F;

        public Builder radii(float innerRadiusScale, float outerRadiusScale, float minInnerRadius) {
            if (innerRadiusScale <= 0 || outerRadiusScale <= innerRadiusScale)
                throw new IllegalArgumentException("outer radius must be larger than inner radius");
            this.innerRadiusScale = innerRadiusScale;
            this.outerRadiusScale = outerRadiusScale;
            this.minInnerRadius = minInnerRadius;
            return this;
        }

        public Builder separatorThickness(float separatorThickness) {
            this.separatorThickness = separatorThickness;
            return this;
        }

        public Builder timing(long openDelayMs, float openAnimationMs, float closeAnimationMs) {
            if (openDelayMs < 0 || openAnimationMs <= 0 || closeAnimationMs <= 0)
                throw new IllegalArgumentException("animation times must be positive");
            this.openDelayMs = openDelayMs;
            this.openAnimationMs = openAnimationMs;
            this.closeAnimationMs = closeAnimationMs;
            return this;
        }

        public Builder highlightLag(float highlightLag) {
            if (highlightLag < 1) throw new IllegalArgumentException("highlightLag must be >= 1");
            this.highlightLag = highlightLag;
            return this;
        }

        public Builder discColor(int discColor) {
            this.discColor = discColor;
            return this;
        }

        public Builder frosted(boolean frosted, int frostedTint) {
            this.frosted = frosted;
            this.frostedTint = frostedTint;
            return this;
        }

        public Builder lineColor(int lineColor) {
            this.lineColor = lineColor;
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder titleScale(float titleScale) {
            this.titleScale = titleScale;
            return this;
        }

        public Builder highlight(WheelHighlight highlight, int highlightColor) {
            this.highlight = Objects.requireNonNull(highlight, "highlight");
            this.highlightColor = highlightColor;
            return this;
        }

        public Builder highlightColor(int highlightColor) {
            this.highlightColor = highlightColor;
            return this;
        }

        public Builder deadZone(float deadZone) {
            if (deadZone < 0) throw new IllegalArgumentException("deadZone must be >= 0");
            this.deadZone = deadZone;
            return this;
        }

        public WheelStyle build() {
            return new WheelStyle(this);
        }
    }
}
