package com.gtnllib.client.hud;

import java.util.List;

import com.gtnllib.client.hud.HudLayout.Placement;

public class HudSnap {

    public static final float RANGE = 4F;
    public static final int NUDGE = 1, NUDGE_FAST = 10;

    public record Box(float minX, float minY, float maxX, float maxY, float x, float y) {

        public static Box of(HudElement element, float x, float y, float scale) {
            return new Box(
                x + element.minX * scale,
                y + element.minY * scale,
                x + element.maxX * scale,
                y + element.maxY * scale,
                x,
                y);
        }

        private float[] lines(boolean horizontal) {
            return horizontal ? new float[] { minX, x, maxX } : new float[] { minY, y, maxY };
        }
    }

    public record Snap(float x, float y, float guideX, float guideY) {

        public boolean snappedX() {
            return !Float.isNaN(guideX);
        }

        public boolean snappedY() {
            return !Float.isNaN(guideY);
        }
    }

    public static Snap snap(Box moving, List<Box> others, int width, int height, float range) {
        float[] x = nearest(moving.lines(true), targets(others, width, true), range);
        float[] y = nearest(moving.lines(false), targets(others, height, false), range);
        return new Snap(
            x == null ? moving.x : moving.x + x[0],
            y == null ? moving.y : moving.y + y[0],
            x == null ? Float.NaN : x[1],
            y == null ? Float.NaN : y[1]);
    }

    public static int nudge(boolean fast) {
        return fast ? NUDGE_FAST : NUDGE;
    }

    public static float[] nudged(HudElement element, Placement place, int dx, int dy, int width, int height) {
        return new float[] { HudLayout.clampX(element, place.x() + dx, place.scale(), width),
            HudLayout.clampY(element, place.y() + dy, place.scale(), height) };
    }

    private static float[] targets(List<Box> others, int size, boolean horizontal) {
        float[] targets = new float[3 + others.size() * 3];
        targets[0] = 0;
        targets[1] = size / 2F;
        targets[2] = size;
        for (int i = 0; i < others.size(); i++) System.arraycopy(
            others.get(i)
                .lines(horizontal),
            0,
            targets,
            3 + i * 3,
            3);
        return targets;
    }

    private static float[] nearest(float[] lines, float[] targets, float range) {
        float[] best = null;
        for (float target : targets) for (float line : lines) {
            float delta = target - line;
            if (Math.abs(delta) > range || best != null && Math.abs(delta) >= Math.abs(best[0])) continue;
            best = new float[] { delta, target };
        }
        return best;
    }
}
