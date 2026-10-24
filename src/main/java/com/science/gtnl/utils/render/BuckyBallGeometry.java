package com.science.gtnl.utils.render;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class BuckyBallGeometry {

    public static final List<double[]> vertices = new ObjectArrayList<>();
    public static final List<int[]> edges = new ObjectArrayList<>();

    static {
        double p = (1.0 + Math.sqrt(5.0)) / 2.0;
        double[][] base = { { 0, 1, 3 * p }, { 0, 1, -3 * p }, { 0, -1, 3 * p }, { 0, -1, -3 * p }, { 1, 2 + p, 2 * p },
            { 1, 2 + p, -2 * p }, { 1, -(2 + p), 2 * p }, { 1, -(2 + p), -2 * p }, { -1, 2 + p, 2 * p },
            { -1, 2 + p, -2 * p }, { -1, -(2 + p), 2 * p }, { -1, -(2 + p), -2 * p }, { p, 2, 2 * p + 1 },
            { p, 2, -(2 * p + 1) }, { p, -2, 2 * p + 1 }, { p, -2, -(2 * p + 1) }, { -p, 2, 2 * p + 1 },
            { -p, 2, -(2 * p + 1) }, { -p, -2, 2 * p + 1 }, { -p, -2, -(2 * p + 1) } };

        for (double[] v : base) {
            double len = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
            addVertex(v[0] / len, v[1] / len, v[2] / len);
            addVertex(v[1] / len, v[2] / len, v[0] / len);
            addVertex(v[2] / len, v[0] / len, v[1] / len);
        }

        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                double[] v1 = vertices.get(i);
                double[] v2 = vertices.get(j);
                double d2 = Math.pow(v1[0] - v2[0], 2) + Math.pow(v1[1] - v2[1], 2) + Math.pow(v1[2] - v2[2], 2);
                if (d2 < 0.17) { // 0.41^2 ≈ 0.168
                    edges.add(new int[] { i, j });
                }
            }
        }
    }

    private static void addVertex(double x, double y, double z) {
        vertices.add(new double[] { x, y, z });
    }
}
