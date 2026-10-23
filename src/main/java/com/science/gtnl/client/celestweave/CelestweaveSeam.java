package com.science.gtnl.client.celestweave;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public record CelestweaveSeam(CelestweaveFrames.Part part, String anchor, float[] a, float[] b, float[] half) {

    static final float DIRECTED_START = 0.15F, DIRECTED_END = 0.85F, MIN_SPAN_SHARE = 0.45F, FORK_CHANCE = 0.4F,
        FORK_FROM = 0.3F, FORK_TO = 0.7F, FORK_MIN_LENGTH = 1.2F;
    private static final int TRIES = 8, FORK_TRIES = 6;

    static CelestweaveSeam line(CelestweaveFrames.Part part, String anchor, float[] a, float[] b) {
        return new CelestweaveSeam(part, anchor, a, b, null);
    }

    static CelestweaveSeam patch(CelestweaveFrames.Part part, String anchor, float[] a, float[] b, float[] half) {
        return new CelestweaveSeam(part, anchor, a, b, half);
    }

    boolean shown(CelestweaveLook look) {
        return look.wears(part.piece) && part.visible(look);
    }

    boolean directed() {
        return half == null;
    }

    float[][] endpoints(Random random) {
        if (directed()) return new float[][] { along(random.nextFloat() * DIRECTED_START),
            along(DIRECTED_END + random.nextFloat() * (1F - DIRECTED_END)) };
        float span = Math.max(distance(a, b), 2F * length(half));
        float[] from = point(random), to = point(random);
        for (int i = 1; i < TRIES && distance(from, to) < MIN_SPAN_SHARE * span; i++) {
            from = point(random);
            to = point(random);
        }
        return new float[][] { from, to };
    }

    float[][] fork(float[][] main, Random random) {
        if (directed() || random.nextFloat() >= FORK_CHANCE) return null;
        float[] from = lerp(main[0], main[1], FORK_FROM + random.nextFloat() * (FORK_TO - FORK_FROM));
        float[] to = point(random);
        for (int i = 0; i < FORK_TRIES && distance(from, to) < FORK_MIN_LENGTH; i++) to = point(random);
        return new float[][] { from, to };
    }

    float[] point(Random random) {
        float[] p = along(random.nextFloat());
        float v = random.nextFloat() * 2F - 1F;
        for (int i = 0; i < 3; i++) p[i] += half[i] * v;
        return p;
    }

    private float[] along(float t) {
        return lerp(a, b, t);
    }

    static float[] lerp(float[] from, float[] to, float t) {
        return new float[] { from[0] + (to[0] - from[0]) * t, from[1] + (to[1] - from[1]) * t,
            from[2] + (to[2] - from[2]) * t };
    }

    static float distance(float[] p, float[] q) {
        return length(new float[] { q[0] - p[0], q[1] - p[1], q[2] - p[2] });
    }

    private static float length(float[] v) {
        return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }
}
