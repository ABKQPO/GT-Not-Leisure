package com.gtnllib.utils;

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

    public static long hash(long a, long b, long c, long d) {
        long hash = a * 0x9E3779B97F4A7C15L + b * 0xC2B2AE3D27D4EB4FL
            + c * 0x165667B19E3779F9L
            + d * 0xD6E8FEB86659FD93L;
        hash = (hash ^ (hash >>> 30)) * 0xBF58476D1CE4E5B9L;
        hash = (hash ^ (hash >>> 27)) * 0x94D049BB133111EBL;
        return hash ^ (hash >>> 31);
    }

    public static double hashUnit(long a, long b, long c, long d) {
        return (hash(a, b, c, d) >>> 11) * 0x1.0p-53;
    }
}
