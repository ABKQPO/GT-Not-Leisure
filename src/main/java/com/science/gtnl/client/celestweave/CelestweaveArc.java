package com.science.gtnl.client.celestweave;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveArc {

    public enum Kind {
        IDLE,
        ANTENNA,
        BURST
    }

    private static final long RELAYOUT_MS = 50;

    final String anchor;
    final float ax, ay, az, bx, by, bz;
    final int segs;
    final float spread, width;
    final long born;
    final int lifetime;
    final int rgb;
    final Kind kind;
    final float[] points;
    long nextLayout = Long.MIN_VALUE;
    int bone = -1;
    CelestweaveFrames.Frame frame;
    private CelestweaveFrames resolvedFor;

    CelestweaveArc(String anchor, float[] a, float[] b, int segs, float spread, float width, long born, int lifetime,
        int rgb, Kind kind) {
        this.anchor = anchor;
        this.ax = a[0];
        this.ay = a[1];
        this.az = a[2];
        this.bx = b[0];
        this.by = b[1];
        this.bz = b[2];
        this.segs = Math.max(1, Math.min(32, segs));
        this.spread = spread;
        this.width = width;
        this.born = born;
        this.lifetime = Math.max(1, lifetime);
        this.rgb = rgb;
        this.kind = kind;
        this.points = new float[(this.segs + 1) * 3];
    }

    boolean expired(long now) {
        return now - born > lifetime;
    }

    boolean resolve(CelestweaveFrames frames) {
        if (resolvedFor != frames) {
            resolvedFor = frames;
            bone = frames.anchorBone(anchor);
            frame = anchor.startsWith("bone:") ? null : frames.frame(anchor);
        }
        return bone >= 0;
    }

    float[] layout(long now, Random random) {
        if (now < nextLayout) return points;
        nextLayout = now + RELAYOUT_MS;
        float dx = bx - ax, dy = by - ay, dz = bz - az;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        float p1x = dz, p1y = 0F, p1z = -dx;
        float p1 = (float) Math.sqrt(p1x * p1x + p1z * p1z);
        if (p1 < 1.0E-3F) {
            p1x = 1F;
            p1z = 0F;
        } else {
            p1x /= p1;
            p1z /= p1;
        }
        float p2x = dy * p1z - dz * p1y, p2y = dz * p1x - dx * p1z, p2z = dx * p1y - dy * p1x;
        float p2 = (float) Math.sqrt(p2x * p2x + p2y * p2y + p2z * p2z);
        if (p2 > 1.0E-6F) {
            p2x /= p2;
            p2y /= p2;
            p2z /= p2;
        }
        points[0] = ax;
        points[1] = ay;
        points[2] = az;
        for (int i = 1; i < segs; i++) {
            float u = i / (float) segs;
            float amp = spread * (float) Math.sin(Math.PI * u) * Math.max(0.6F, len * 0.14F);
            float r1 = (random.nextFloat() * 2F - 1F) * amp, r2 = (random.nextFloat() * 2F - 1F) * amp;
            points[i * 3] = ax + dx * u + p1x * r1 + p2x * r2;
            points[i * 3 + 1] = ay + dy * u + p1y * r1 + p2y * r2;
            points[i * 3 + 2] = az + dz * u + p1z * r1 + p2z * r2;
        }
        points[segs * 3] = bx;
        points[segs * 3 + 1] = by;
        points[segs * 3 + 2] = bz;
        return points;
    }
}
