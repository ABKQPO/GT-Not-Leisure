package com.gtnllib.client.fx;

import com.gtnllib.utils.MathUtils;

public class FxEasing {

    public static float easeOutQuad(float t) {
        return -t * t + 2 * t;
    }

    public static float easeOutCubic(float t) {
        float rest = 1 - MathUtils.clampUnit(t);
        return 1 - rest * rest * rest;
    }

    public static float easeOutBack(float t) {
        float p = MathUtils.clampUnit(t) - 1;
        return 1 + 2.70158F * p * p * p + 1.70158F * p * p;
    }

    public static float smoothStep(float t) {
        float p = MathUtils.clampUnit(t);
        return p * p * (3 - 2 * p);
    }

    public static float smoothStep(float from, float to, float value) {
        return smoothStep((value - from) / (to - from));
    }
}
