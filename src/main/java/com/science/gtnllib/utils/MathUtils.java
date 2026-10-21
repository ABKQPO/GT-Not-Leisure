package com.science.gtnllib.utils;

import net.minecraft.util.MathHelper;

public class MathUtils {

    private MathUtils() {}

    public static float clampUnit(float value) {
        return MathHelper.clamp_float(value, 0F, 1F);
    }

    public static double clampUnit(double value) {
        return MathHelper.clamp_double(value, 0, 1);
    }

    public static float clampUnit(float value, float fallback) {
        return Float.isFinite(value) ? clampUnit(value) : fallback;
    }
}
