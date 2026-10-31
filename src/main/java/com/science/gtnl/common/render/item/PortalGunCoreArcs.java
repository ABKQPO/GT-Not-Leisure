package com.science.gtnl.common.render.item;

import java.util.List;

import com.gtnllib.utils.MathUtils;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

// Why: plain arrays keep this free of Minecraft types, so the offline preview script loads it without the game.
public class PortalGunCoreArcs {

    public static final long STEP_MILLIS = 50, LOOP_MILLIS = 60000, SHAKE_MILLIS = 40;
    public static final int BOLT_SLOTS = 4, CRAWL_ARCS = 2, SPECKS = 4, CRAWL_POINTS = 5;
    public static final double INSET = 0.004, BOLT_JAG = 0.18, BOLT_WIGGLE = 0.035, CRAWL_LIFT = 0.004,
        CRAWL_JAG = 0.003, SPECK_LENGTH = 0.006, SHAKE = 0.007, JOLT = 0.013, JOLT_CHANCE = 0.12;
    public static final double SPIN_X = 120, SPIN_Y = 180, SPIN_Z = 90, SURGE_X = 40, SURGE_Y = 55, SURGE_Z = 35;
    public static final int SURGE_CYCLES_X = 7, SURGE_CYCLES_Y = 5, SURGE_CYCLES_Z = 9;
    private static final int BOLT_SALT = 21, CRAWL_SALT = 22, SPECK_SALT = 23, SHAKE_SALT = 24;

    public record Arc(double[][] points, float brightness) {}

    public static double[] angles(long millis) {
        double seconds = millis % LOOP_MILLIS / 1000.0, phase = 2 * Math.PI * (millis % LOOP_MILLIS) / LOOP_MILLIS;
        return new double[] { angle(seconds, phase, SPIN_X, SURGE_X, SURGE_CYCLES_X),
            angle(seconds, phase, SPIN_Y, SURGE_Y, SURGE_CYCLES_Y),
            angle(seconds, phase, SPIN_Z, SURGE_Z, SURGE_CYCLES_Z) };
    }

    public static double[] shake(long millis) {
        long step = millis / SHAKE_MILLIS;
        double t = millis % SHAKE_MILLIS / (double) SHAKE_MILLIS;
        double[] from = jolt(step), to = jolt(step + 1);
        return add(from, scale(subtract(to, from), t));
    }

    public static List<Arc> arcs(long millis, double half, double[] room, double[] degrees, double[] offset) {
        long step = millis / STEP_MILLIS;
        double[][] spin = rotation(degrees);
        List<Arc> arcs = new ObjectArrayList<>();
        for (int slot = 0; slot < BOLT_SLOTS; slot++) {
            int epochSteps = 4 + 2 * slot + (int) (unit(slot, 0, BOLT_SALT, 0) * 3);
            long shifted = step + (long) (unit(slot, 1, BOLT_SALT, 0) * epochSteps);
            long epoch = shifted / epochSteps;
            int local = (int) (shifted % epochSteps);
            if (unit(epoch, slot, BOLT_SALT, 0) > 0.8) continue;
            int start = (int) (unit(epoch, slot, BOLT_SALT, 1) * epochSteps),
                life = 1 + (int) (unit(epoch, slot, BOLT_SALT, 2) * 3);
            if (local < start || local >= start + life) continue;
            double[] from = add(offset, apply(spin, surface(half, epoch, slot, BOLT_SALT)));
            double[] direction = normalize(
                add(normalize(subtract(from, offset)), scale(randomVector(epoch, slot, BOLT_SALT, 4), 1.1)));
            double[] end = add(
                from,
                scale(subtract(wall(from, direction, room), from), 0.25 + 0.75 * unit(epoch, slot, BOLT_SALT, 7)));
            int segments = 2 + (int) (unit(epoch, slot, BOLT_SALT, 8) * 4);
            float brightness = (float) ((0.6 + 0.4 * unit(epoch, slot, BOLT_SALT, 9))
                * (local == start ? 1 : 0.7 + 0.3 * unit(step, slot, BOLT_SALT, 10)));
            double[][] bolt = wiggle(jagged(from, end, segments + 1, epoch, slot), step, slot);
            arcs.add(new Arc(bolt, brightness));
            if (segments > 2 && unit(epoch, slot, BOLT_SALT, 11) < 0.35) {
                double[] fork = bolt[1 + (int) (unit(epoch, slot, BOLT_SALT, 12) * (segments - 1))];
                double[] turn = normalize(add(direction, scale(randomVector(epoch, slot, BOLT_SALT, 13), 1.4)));
                double[] to = add(
                    fork,
                    scale(subtract(wall(fork, turn, room), fork), 0.2 + 0.4 * unit(epoch, slot, BOLT_SALT, 16)));
                arcs.add(new Arc(wiggle(jagged(fork, to, 3, epoch, slot + 8), step, slot + 8), brightness * 0.85F));
            }
        }
        for (int slot = 0; slot < CRAWL_ARCS; slot++) {
            if (unit(step, slot, CRAWL_SALT, 0) > 0.6) continue;
            double[] from = surface(half, step, slot, CRAWL_SALT), normal = cubeNormal(from);
            double[] along = randomVector(step, slot, CRAWL_SALT, 4);
            along = scale(subtract(along, scale(normal, dot(along, normal))), 1.3 * half);
            double[][] points = new double[CRAWL_POINTS][];
            for (int i = 0; i < CRAWL_POINTS; i++) {
                double t = i / (double) (CRAWL_POINTS - 1), pin = Math.sin(Math.PI * t);
                double[] path = add(from, scale(along, t));
                double[] onSurface = scale(path, half / maxAbs(path));
                double[] point = add(onSurface, scale(cubeNormal(onSurface), CRAWL_LIFT * pin));
                double[] jag = scale(randomVector(step, slot * 31 + i, CRAWL_SALT, 8), CRAWL_JAG * pin);
                points[i] = add(offset, apply(spin, add(point, jag)));
            }
            arcs.add(new Arc(points, (float) (0.6 + 0.4 * unit(step, slot, CRAWL_SALT, 3))));
        }
        long drift = step / 2;
        for (int slot = 0; slot < SPECKS; slot++) {
            if (unit(drift, slot, SPECK_SALT, 0) > 0.75) continue;
            double[] direction = normalize(randomVector(drift, slot, SPECK_SALT, 1));
            double[] limit = wall(new double[3], direction, room);
            double reach = Math.sqrt(dot(limit, limit));
            double distance = half * 1.8 + (reach - half * 1.8) * 0.8 * unit(drift, slot, SPECK_SALT, 4);
            double[] at = scale(direction, distance);
            double[] dash = scale(
                normalize(randomVector(drift, slot, SPECK_SALT, 5)),
                SPECK_LENGTH * (0.5 + unit(drift, slot, SPECK_SALT, 8)));
            arcs.add(
                new Arc(new double[][] { at, add(at, dash) }, (float) (0.5 + 0.5 * unit(drift, slot, SPECK_SALT, 9))));
        }
        return arcs;
    }

    private static double angle(double seconds, double phase, double speed, double surge, int cycles) {
        return ((speed * seconds + surge * Math.sin(phase * cycles)) % 360 + 360) % 360;
    }

    private static double[] jolt(long step) {
        double amplitude = unit(step, 0, SHAKE_SALT, 0) < JOLT_CHANCE ? JOLT : SHAKE;
        return scale(randomVector(step, 0, SHAKE_SALT, 1), amplitude);
    }

    private static double[][] wiggle(double[][] points, long step, int slot) {
        double[] delta = subtract(points[points.length - 1], points[0]);
        double amount = Math.sqrt(dot(delta, delta)) * BOLT_WIGGLE;
        for (int i = 1; i < points.length - 1; i++)
            points[i] = add(points[i], scale(randomVector(step, slot * 53 + i, BOLT_SALT, 40), amount));
        return points;
    }

    public static double[][] rotation(double[] degrees) {
        double[][] matrix = identity();
        for (int axis = 0; axis < 3; axis++)
            matrix = multiply(matrix, axisRotation(axis, Math.toRadians(degrees[axis])));
        return matrix;
    }

    public static double[] apply(double[][] matrix, double[] vector) {
        double[] result = new double[3];
        for (int row = 0; row < 3; row++)
            result[row] = matrix[row][0] * vector[0] + matrix[row][1] * vector[1] + matrix[row][2] * vector[2];
        return result;
    }

    private static double[][] jagged(double[] from, double[] to, int count, long step, int slot) {
        double[] delta = subtract(to, from);
        double length = Math.sqrt(dot(delta, delta));
        double[] axis = normalize(delta);
        double[] side = normalize(
            cross(axis, Math.abs(axis[1]) > 0.9 ? new double[] { 1, 0, 0 } : new double[] { 0, 1, 0 }));
        double[] up = cross(axis, side);
        double[][] points = new double[count][];
        for (int i = 0; i < count; i++) {
            double t = i / (double) (count - 1);
            double pin = Math.sin(Math.PI * t);
            double x = (unit(step, slot * 97 + i, BOLT_SALT, 20) * 2 - 1) * BOLT_JAG * length * pin;
            double y = (unit(step, slot * 97 + i, BOLT_SALT, 21) * 2 - 1) * BOLT_JAG * length * pin;
            points[i] = add(add(from, scale(delta, t)), add(scale(side, x), scale(up, y)));
        }
        return points;
    }

    private static double[] surface(double half, long step, int slot, int salt) {
        int axis = (int) (unit(step, slot, salt, 30) * 3);
        double[] point = new double[3];
        for (int i = 0; i < 3; i++) point[i] = (unit(step, slot, salt, 31 + i) * 2 - 1) * half;
        point[axis] = unit(step, slot, salt, 34) < 0.5 ? -half : half;
        return point;
    }

    private static double[] wall(double[] start, double[] direction, double[] room) {
        double reach = Double.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            if (Math.abs(direction[i]) < 1.0E-9) continue;
            double limit = (direction[i] > 0 ? room[i] : -room[i]) - Math.signum(direction[i]) * INSET;
            reach = Math.min(reach, (limit - start[i]) / direction[i]);
        }
        return add(start, scale(direction, Math.max(0, reach)));
    }

    private static double[] cubeNormal(double[] point) {
        int axis = 0;
        for (int i = 1; i < 3; i++) if (Math.abs(point[i]) > Math.abs(point[axis])) axis = i;
        double[] normal = new double[3];
        normal[axis] = point[axis] < 0 ? -1 : 1;
        return normal;
    }

    private static double maxAbs(double[] point) {
        return Math.max(Math.abs(point[0]), Math.max(Math.abs(point[1]), Math.abs(point[2])));
    }

    private static double[] randomVector(long step, int slot, int salt, int key) {
        return new double[] { unit(step, slot, salt, key) * 2 - 1, unit(step, slot, salt, key + 1) * 2 - 1,
            unit(step, slot, salt, key + 2) * 2 - 1 };
    }

    private static double unit(long step, int slot, int salt, int key) {
        return MathUtils.hashUnit(step, slot, salt, key);
    }

    private static double[][] axisRotation(int axis, double radians) {
        double cos = Math.cos(radians), sin = Math.sin(radians);
        return switch (axis) {
            case 0 -> new double[][] { { 1, 0, 0 }, { 0, cos, -sin }, { 0, sin, cos } };
            case 1 -> new double[][] { { cos, 0, sin }, { 0, 1, 0 }, { -sin, 0, cos } };
            default -> new double[][] { { cos, -sin, 0 }, { sin, cos, 0 }, { 0, 0, 1 } };
        };
    }

    private static double[][] identity() {
        return new double[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
    }

    private static double[][] multiply(double[][] a, double[][] b) {
        double[][] result = new double[3][3];
        for (int row = 0; row < 3; row++) for (int column = 0; column < 3; column++)
            for (int k = 0; k < 3; k++) result[row][column] += a[row][k] * b[k][column];
        return result;
    }

    private static double[] add(double[] a, double[] b) {
        return new double[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
    }

    private static double[] subtract(double[] a, double[] b) {
        return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
    }

    private static double[] scale(double[] a, double k) {
        return new double[] { a[0] * k, a[1] * k, a[2] * k };
    }

    private static double dot(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    private static double[] cross(double[] a, double[] b) {
        return new double[] { a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0] };
    }

    private static double[] normalize(double[] a) {
        double length = Math.sqrt(dot(a, a));
        return length < 1.0E-9 ? new double[] { 0, 1, 0 } : scale(a, 1 / length);
    }
}
