package com.gtnllib.client.fx;

public class FxAngle {

    public static final float TAU = (float) (Math.PI * 2);

    /**
     * Whether a sector this many radians to either side of its middle already covers the whole turn.
     */
    public static boolean fullTurn(float halfRange) {
        return halfRange >= Math.PI;
    }

    public static float angleOf(double dx, double dy) {
        return normalizePositive((float) Math.atan2(dx, -dy));
    }

    public static float dirX(float angle) {
        return (float) Math.sin(angle);
    }

    public static float dirY(float angle) {
        return (float) -Math.cos(angle);
    }

    public static float normalizePositive(float angle) {
        float normalized = angle % TAU;
        return normalized < 0 ? normalized + TAU : normalized;
    }

    public static float normalizeSigned(float angle) {
        float normalized = normalizePositive(angle);
        return normalized > Math.PI ? normalized - TAU : normalized;
    }
}
