package com.science.gtnl.common.render.model.pigmeeModel;

public class PigmeeFumoAnimation {

    public static final long REVOLUTION_NANOS = 3_000_000_000L;

    public static float degreesSince(long startNanos) {
        return degreesAt(System.nanoTime() - startNanos);
    }

    public static float currentDegrees() {
        return degreesAt(System.nanoTime());
    }

    public static float degreesAt(long elapsedNanos) {
        return Math.floorMod(elapsedNanos, REVOLUTION_NANOS) * (360.0F / REVOLUTION_NANOS);
    }
}
