package com.science.gtnllib.client.fx;

public class FxEasing {

    private FxEasing() {}

    public static float easeOutQuad(float t) {
        return -t * t + 2 * t;
    }

    public static float easeOutCubic(float t) {
        float rest = 1 - clamp(t);
        return 1 - rest * rest * rest;
    }

    public static float smoothStep(float t) {
        float p = clamp(t);
        return p * p * (3 - 2 * p);
    }

    private static float clamp(float t) {
        return Math.max(0F, Math.min(1F, t));
    }
}
