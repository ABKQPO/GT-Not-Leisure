package com.gtnllib.client.fx;

import com.gtnllib.utils.MathUtils;

public class FxEasing {

    private FxEasing() {}

    public static float easeOutQuad(float t) {
        return -t * t + 2 * t;
    }

    public static float easeOutCubic(float t) {
        float rest = 1 - MathUtils.clampUnit(t);
        return 1 - rest * rest * rest;
    }

    public static float smoothStep(float t) {
        float p = MathUtils.clampUnit(t);
        return p * p * (3 - 2 * p);
    }
}
