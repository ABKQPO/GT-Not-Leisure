package com.science.gtnl.client.celestweave;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveRaise {

    static final float HEIGHT = 0.25F;
    private static final float INSET = 0.0005F;
    private static final int LIT = 16;

    private CelestweaveRaise() {}

    static List<float[]> raise(List<Face> faces, int[] argb, int width, int height, float lift) {
        List<float[]> quads = new ArrayList<>();
        for (Face face : faces) if (face.vertices != null && face.vertices.length == 4
            && face.textureCoordinates != null
            && face.textureCoordinates.length == 4) raiseFace(face, argb, width, height, lift, quads);
        return quads;
    }

    static float[] normal(float[] quad) {
        float[] a = { quad[0], quad[1], quad[2] }, b = { quad[5], quad[6], quad[7] },
            c = { quad[10], quad[11], quad[12] };
        return normalize(cross(minus(b, a), minus(c, a)));
    }

    static boolean lit(int argb) {
        return (argb >>> 24) > 0 && Math.max(argb >> 16 & 255, Math.max(argb >> 8 & 255, argb & 255)) > LIT;
    }

    private static void raiseFace(Face face, int[] argb, int width, int height, float lift, List<float[]> quads) {
        Vertex[] p = face.vertices;
        TextureCoordinate[] t = face.textureCoordinates;
        float[] origin = { p[0].x, p[0].y, p[0].z }, e1 = minus(p[1], p[0]), e2 = minus(p[3], p[0]);
        float du1 = t[1].u - t[0].u, dv1 = t[1].v - t[0].v, du2 = t[3].u - t[0].u, dv2 = t[3].v - t[0].v;
        float det = du1 * dv2 - du2 * dv1;
        if (Math.abs(det) < 1.0E-9F) return;
        float[] dPdu = new float[3], dPdv = new float[3];
        for (int i = 0; i < 3; i++) {
            dPdu[i] = (e1[i] * dv2 - e2[i] * dv1) / det;
            dPdv[i] = (e2[i] * du1 - e1[i] * du2) / det;
        }
        float[] normal = normalize(cross(e1, minus(p[2], p[0])));
        if (normal == null) return;
        float[] up = { normal[0] * lift, normal[1] * lift, normal[2] * lift };
        float uMin = Math.min(Math.min(t[0].u, t[1].u), Math.min(t[2].u, t[3].u)),
            uMax = Math.max(Math.max(t[0].u, t[1].u), Math.max(t[2].u, t[3].u)),
            vMin = Math.min(Math.min(t[0].v, t[1].v), Math.min(t[2].v, t[3].v)),
            vMax = Math.max(Math.max(t[0].v, t[1].v), Math.max(t[2].v, t[3].v));
        int x0 = Math.max(0, (int) Math.ceil(uMin * width - 0.5F)),
            x1 = Math.min(width - 1, (int) Math.floor(uMax * width - 0.5F));
        int y0 = Math.max(0, (int) Math.ceil(vMin * height - 0.5F)),
            y1 = Math.min(height - 1, (int) Math.floor(vMax * height - 0.5F));
        if (x1 < x0 || y1 < y0) return;
        int columns = x1 - x0 + 1, rows = y1 - y0 + 1;
        boolean[][] open = new boolean[rows][columns];
        for (int y = 0; y < rows; y++)
            for (int x = 0; x < columns; x++) open[y][x] = lit(argb[(y0 + y) * width + x0 + x]);
        int litX0 = columns, litX1 = -1, litY0 = rows, litY1 = -1;
        for (int y = 0; y < rows; y++) for (int x = 0; x < columns; x++) if (open[y][x]) {
            litX0 = Math.min(litX0, x);
            litX1 = Math.max(litX1, x);
            litY0 = Math.min(litY0, y);
            litY1 = Math.max(litY1, y);
        }
        if (litX1 < 0) return;
        float du = centering(uMin, uMax, (x0 + litX0) / (float) width, (x0 + litX1 + 1) / (float) width);
        float dv = centering(vMin, vMax, (y0 + litY0) / (float) height, (y0 + litY1 + 1) / (float) height);
        Plane plane = new Plane(origin, t[0].u - du, t[0].v - dv, dPdu, dPdv, up);
        for (int y = 0; y < rows; y++) for (int x = 0; x < columns; x++) {
            if (!open[y][x]) continue;
            int right = x;
            while (right + 1 < columns && open[y][right + 1]) right++;
            int bottom = y;
            while (bottom + 1 < rows && rowOpen(open[bottom + 1], x, right)) bottom++;
            for (int yy = y; yy <= bottom; yy++) for (int xx = x; xx <= right; xx++) open[yy][xx] = false;
            box(
                plane,
                Math.max(uMin, (x0 + x) / (float) width),
                Math.min(uMax, (x0 + right + 1) / (float) width),
                Math.max(vMin, (y0 + y) / (float) height),
                Math.min(vMax, (y0 + bottom + 1) / (float) height),
                (x0 + x + 0.5F) / width,
                (x0 + right + 0.5F) / width,
                (y0 + y + 0.5F) / height,
                (y0 + bottom + 0.5F) / height,
                quads);
        }
    }

    private static float centering(float min, float max, float litMin, float litMax) {
        float from = Math.max(min, litMin), to = Math.min(max, litMax);
        return max - min - (to - from) > 1.0E-6F ? (min + max) / 2 - (from + to) / 2 : 0F;
    }

    private static boolean rowOpen(boolean[] row, int from, int to) {
        for (int x = from; x <= to; x++) if (!row[x]) return false;
        return true;
    }

    private static void box(Plane plane, float ua, float ub, float va, float vb, float firstColumn, float lastColumn,
        float firstRow, float lastRow, List<float[]> quads) {
        float ia = ua + INSET, ib = ub - INSET, ja = va + INSET, jb = vb - INSET;
        float[] a = plane.at(ua, va), b = plane.at(ub, va), c = plane.at(ub, vb), d = plane.at(ua, vb);
        float[] la = plane.lift(a), lb = plane.lift(b), lc = plane.lift(c), ld = plane.lift(d);
        float[] center = plane.at((ua + ub) / 2, (va + vb) / 2);
        quad(quads, plane.up, la, ia, ja, lb, ib, ja, lc, ib, jb, ld, ia, jb);
        wall(quads, center, a, b, lb, la, ia, ib, firstRow, firstRow);
        wall(quads, center, c, d, ld, lc, ib, ia, lastRow, lastRow);
        wall(quads, center, d, a, la, ld, firstColumn, firstColumn, jb, ja);
        wall(quads, center, b, c, lc, lb, lastColumn, lastColumn, ja, jb);
    }

    private static void wall(List<float[]> quads, float[] center, float[] from, float[] to, float[] liftedTo,
        float[] liftedFrom, float uFrom, float uTo, float vFrom, float vTo) {
        float[] out = new float[3];
        for (int i = 0; i < 3; i++) out[i] = (from[i] + to[i]) / 2 - center[i];
        quad(quads, out, from, uFrom, vFrom, to, uTo, vTo, liftedTo, uTo, vTo, liftedFrom, uFrom, vFrom);
    }

    private static void quad(List<float[]> quads, float[] out, float[] a, float ua, float va, float[] b, float ub,
        float vb, float[] c, float uc, float vc, float[] d, float ud, float vd) {
        float[] facing = cross(minus(b, a), minus(c, a));
        boolean flip = facing[0] * out[0] + facing[1] * out[1] + facing[2] * out[2] < 0;
        quads.add(
            flip ? new float[] { a[0], a[1], a[2], ua, va, d[0], d[1], d[2], ud, vd, c[0], c[1], c[2], uc, vc, b[0],
                b[1], b[2], ub, vb }
                : new float[] { a[0], a[1], a[2], ua, va, b[0], b[1], b[2], ub, vb, c[0], c[1], c[2], uc, vc, d[0],
                    d[1], d[2], ud, vd });
    }

    private static float[] minus(Vertex a, Vertex b) {
        return new float[] { a.x - b.x, a.y - b.y, a.z - b.z };
    }

    private static float[] minus(float[] a, float[] b) {
        return new float[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
    }

    private static float[] cross(float[] a, float[] b) {
        return new float[] { a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0] };
    }

    private static float[] normalize(float[] v) {
        float length = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        return length < 1.0E-6F ? null : new float[] { v[0] / length, v[1] / length, v[2] / length };
    }

    private record Plane(float[] origin, float u0, float v0, float[] dPdu, float[] dPdv, float[] up) {

        float[] at(float u, float v) {
            float[] point = new float[3];
            for (int i = 0; i < 3; i++) point[i] = origin[i] + (u - u0) * dPdu[i] + (v - v0) * dPdv[i];
            return point;
        }

        float[] lift(float[] point) {
            return new float[] { point[0] + up[0], point[1] + up[1], point[2] + up[2] };
        }
    }
}
