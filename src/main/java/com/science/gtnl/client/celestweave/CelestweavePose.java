package com.science.gtnl.client.celestweave;

import java.util.Arrays;

import com.gtnllib.utils.ColorMath;
import com.gtnllib.utils.MathUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweavePose {

    final float[] dx, dy, dz, rx, ry, rz, sx, sy, sz, r, g, b, a;
    final boolean[] hidden, colored;
    final int[] texFrame;
    float brightness, steadyBrightness;
    float glowR, glowG, glowB;

    CelestweavePose(int size) {
        dx = new float[size];
        dy = new float[size];
        dz = new float[size];
        rx = new float[size];
        ry = new float[size];
        rz = new float[size];
        sx = new float[size];
        sy = new float[size];
        sz = new float[size];
        r = new float[size];
        g = new float[size];
        b = new float[size];
        a = new float[size];
        hidden = new boolean[size];
        colored = new boolean[size];
        texFrame = new int[size];
    }

    int size() {
        return dx.length;
    }

    void reset() {
        Arrays.fill(dx, 0);
        Arrays.fill(dy, 0);
        Arrays.fill(dz, 0);
        Arrays.fill(rx, 0);
        Arrays.fill(ry, 0);
        Arrays.fill(rz, 0);
        Arrays.fill(sx, 1);
        Arrays.fill(sy, 1);
        Arrays.fill(sz, 1);
        Arrays.fill(a, 1);
        Arrays.fill(hidden, false);
        Arrays.fill(colored, false);
        Arrays.fill(texFrame, 0);
    }

    void color(int index, int rgb, float k, float alpha) {
        if (index < 0) return;
        r[index] = Math.min(1F, ColorMath.red(rgb) * k);
        g[index] = Math.min(1F, ColorMath.green(rgb) * k);
        b[index] = Math.min(1F, ColorMath.blue(rgb) * k);
        a[index] = MathUtils.clampUnit(alpha);
        colored[index] = true;
    }

    void hide(int index, boolean hide) {
        if (index >= 0) hidden[index] = hide;
    }

    boolean visible(CelestweaveFrames.Frame frame) {
        for (CelestweaveFrames.Frame current = frame; current != null; current = current.parentFrame)
            if (hidden[current.index]) return false;
        return true;
    }
}
