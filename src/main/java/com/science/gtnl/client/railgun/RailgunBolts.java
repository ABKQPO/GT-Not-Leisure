package com.science.gtnl.client.railgun;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.gtnllib.utils.MathUtils;
import com.gtnllib.utils.Vec3d;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RailgunBolts {

    public static final float MIN_INTENSITY = 0.05F;
    public static final int FLICKER_TICKS = 3;

    public static final int ORB_MAX_ARCS = 2, ORB_MAX_DISCHARGES = 1, ARC_SEGMENTS = 5;
    public static final float DISCHARGE_START = 0.5F;
    public static final double ARC_LOW = 1.02, ARC_HIGH = 1.22;
    public static final double DISCHARGE_MIN_REACH = 0.6, DISCHARGE_REACH_GAIN = 0.8;

    public static final Vec3d MUZZLE = new Vec3d(0.0, 0.286612, -1.213388);
    public static final double ORB_MUZZLE_GAP = 0.1;
    public static final double ORB_CLEARANCE = 1.1;
    public static final int TETHER_SEGMENTS = 8, LEG_SEGMENTS = 4;
    public static final double GROOVE_FRONT = -1.125, GROOVE_BACK = 0.1875;
    public static final double GROOVE_CENTER = (GROOVE_FRONT + GROOVE_BACK) / 2;
    public static final double GROOVE_LOW = 0.255, GROOVE_HIGH = 0.305;
    public static final double GROOVE_INNER = 0.097, GROOVE_OUTER = 0.124;
    public static final int ANCHORS_PER_SIDE = 2, ANCHOR_SEGMENTS = 5;
    public static final double ANCHOR_MIN_REACH = 0.12, ANCHOR_REACH_GAIN = 0.22;
    public static final int FLOW_MAX_PER_SIDE = 2, FLOW_SEGMENTS = 4;
    public static final double FLOW_MIN_SPAN = 0.25, FLOW_SPAN_GAIN = 0.4;
    public static final double FLOW_LENGTH = 0.09, FLOW_LENGTH_GAIN = 0.05;
    public static final double FLOW_SLOW_TICKS = 26, FLOW_FAST_TICKS = 10;
    public static final float SPARK_START = 0.5F;
    public static final double SPARK_MIN_REACH = 0.03, SPARK_REACH_GAIN = 0.04;
    public static final float HOP_START = 0.2F;
    public static final int HOP_SLOTS = 3, HOP_SEGMENTS = 4;
    public static final double HOP_MIN = 0.04, HOP_MAX = 0.12, HOP_LIFT = 0.025, HOP_CHANCE = 0.55,
        HOP_CROSS_CHANCE = 0.3, HOP_JITTER = 0.012;
    public static final List<Edge> HOP_EDGES = List.of(
        new Edge(new Vec3d(0.125, 0.375, -1.125), new Vec3d(0.125, 0.375, 0.375)),
        new Edge(new Vec3d(0.25, 0.25, -0.4375), new Vec3d(0.25, 0.25, 0.375)),
        new Edge(new Vec3d(0.25, 0.0625, -0.4375), new Vec3d(0.25, 0.0625, 0.375)),
        new Edge(new Vec3d(0.1875, 0.375, 0.75), new Vec3d(0.1875, 0.375, 1.25)));

    private static final int DISCHARGE_SLOT_OFFSET = 8, FLOW_SLOT_OFFSET = 16, SPARK_SLOT_OFFSET = 32,
        HOP_SLOT_OFFSET = 48, TETHER_SLOT_OFFSET = 64, ANCHOR_SLOT_OFFSET = 80;

    public record Bolt(List<Vec3d> points, double width, float alpha) {}

    public record Edge(Vec3d from, Vec3d to) {

        public Vec3d at(double t, int side) {
            Vec3d point = from.add(
                to.subtract(from)
                    .scale(t));
            return new Vec3d(point.x() * side, point.y(), point.z());
        }

        public double length() {
            return to.subtract(from)
                .length();
        }

        public boolean coversZ(double z) {
            return z >= Math.min(from.z(), to.z()) && z <= Math.max(from.z(), to.z());
        }

        public double paramAtZ(double z) {
            return (z - from.z()) / (to.z() - from.z());
        }
    }

    private RailgunBolts() {}

    public static int orbArcs(float intensity) {
        float p = MathUtils.clampUnit(intensity, 0F);
        return p < MIN_INTENSITY ? 0 : 1 + Math.round(p * (ORB_MAX_ARCS - 1));
    }

    public static int dischargeSlots(float intensity) {
        float p = MathUtils.clampUnit(intensity, 0F);
        return p <= DISCHARGE_START ? 0 : ORB_MAX_DISCHARGES;
    }

    public static double dischargeChance(float intensity) {
        float p = MathUtils.clampUnit(intensity, 0F);
        return p <= DISCHARGE_START ? 0 : Math.min(1, (p - DISCHARGE_START) * 0.8);
    }

    public static double dischargeReach(float intensity) {
        return DISCHARGE_MIN_REACH + DISCHARGE_REACH_GAIN * MathUtils.clampUnit(intensity, 0F);
    }

    public static List<Bolt> orb(int owner, long tick, float intensity) {
        float p = MathUtils.clampUnit(intensity, 0F);
        int arcs = orbArcs(p), discharges = dischargeSlots(p);
        List<Bolt> bolts = new ArrayList<>(arcs + discharges);
        for (int slot = 0; slot < arcs; slot++) {
            Random random = random(owner, slot, tick);
            bolts.add(new Bolt(surfaceArc(random), 0.025 + 0.015 * p, (0.5F + 0.4F * p) * flicker(slot, tick)));
        }
        double chance = dischargeChance(p), reachLimit = dischargeReach(p);
        for (int index = 0; index < discharges; index++) {
            int slot = DISCHARGE_SLOT_OFFSET + index;
            Random random = random(owner, slot, tick);
            if (random.nextDouble() >= chance) continue;
            Vec3d from = unit(random);
            double reach = reachLimit * (0.7 + 0.3 * random.nextDouble());
            bolts.add(
                new Bolt(
                    jagged(random, from, from.scale(1 + reach), 4, 0.22),
                    0.03 + 0.015 * p,
                    0.85F * flicker(slot, tick)));
        }
        return bolts;
    }

    private static List<Vec3d> surfaceArc(Random random) {
        Vec3d start = unit(random);
        Vec3d across = unit(random);
        across = across.subtract(start.scale(across.dot(start)))
            .normalize();
        if (across.length() < 0.5) across = Vec3d.rightOf(start);
        double sweep = Math.toRadians(30 + 35 * random.nextDouble());
        double radius = between(random, 1.04, 1.1);
        List<Vec3d> points = new ArrayList<>(ARC_SEGMENTS + 1);
        for (int i = 0; i <= ARC_SEGMENTS; i++) {
            if (i > 0) radius = Math.max(ARC_LOW, Math.min(ARC_HIGH, radius + (random.nextDouble() * 2 - 1) * 0.04));
            double angle = sweep * i / ARC_SEGMENTS;
            points.add(
                start.scale(Math.cos(angle))
                    .add(across.scale(Math.sin(angle)))
                    .scale(radius));
        }
        return points;
    }

    public static int flowsPerSide(float intensity) {
        float p = MathUtils.clampUnit(intensity, 0F);
        return p < MIN_INTENSITY ? 0 : 1 + Math.round(p * (FLOW_MAX_PER_SIDE - 1));
    }

    public static double flowSpan(float intensity) {
        return FLOW_MIN_SPAN + FLOW_SPAN_GAIN * MathUtils.clampUnit(intensity, 0F);
    }

    public static double flowPeriod(float intensity) {
        return FLOW_SLOW_TICKS + (FLOW_FAST_TICKS - FLOW_SLOW_TICKS) * MathUtils.clampUnit(intensity, 0F);
    }

    public static double sparkChance(float intensity) {
        float p = MathUtils.clampUnit(intensity, 0F);
        return p <= SPARK_START ? 0 : Math.min(1, (p - SPARK_START) * 1.2);
    }

    public static double hopChance(float intensity) {
        float p = MathUtils.clampUnit(intensity, 0F);
        return p <= HOP_START ? 0 : HOP_CHANCE * (p - HOP_START) / (1 - HOP_START);
    }

    public static List<Bolt> body(int owner, double age, float intensity) {
        float p = MathUtils.clampUnit(intensity, 0F);
        long tick = (long) Math.floor(age);
        int flows = flowsPerSide(p);
        if (p < MIN_INTENSITY) return List.of();
        List<Bolt> bolts = new ArrayList<>(2 + ANCHORS_PER_SIDE * 2 + flows * 2 + 2 + HOP_SLOTS);
        double span = flowSpan(p), period = flowPeriod(p), length = FLOW_LENGTH + FLOW_LENGTH_GAIN * p;
        for (int side = -1; side <= 1; side += 2) {
            int slot = TETHER_SLOT_OFFSET + (side < 0 ? 0 : 1);
            Random random = random(owner, slot, tick);
            List<Vec3d> points = channel(random, side, GROOVE_CENTER, GROOVE_FRONT, TETHER_SEGMENTS);
            points.add(MUZZLE);
            bolts.add(new Bolt(points, 0.005 + 0.002 * p, (0.5F + 0.45F * p) * flicker(slot, tick)));
        }
        double anchorReach = ANCHOR_MIN_REACH + ANCHOR_REACH_GAIN * p;
        for (int side = -1; side <= 1; side += 2) {
            for (int index = 0; index < ANCHORS_PER_SIDE; index++) {
                int slot = ANCHOR_SLOT_OFFSET + (side < 0 ? 0 : ANCHORS_PER_SIDE) + index;
                Random random = random(owner, slot, tick);
                int direction = index % 2 == 0 ? -1 : 1;
                double reach = anchorReach * (0.75 + 0.25 * random.nextDouble());
                bolts.add(
                    new Bolt(
                        channel(random, side, GROOVE_CENTER, GROOVE_CENTER + direction * reach, ANCHOR_SEGMENTS),
                        0.0045 + 0.0025 * p,
                        (0.5F + 0.4F * p) * flicker(slot, tick)));
            }
        }
        for (int side = -1; side <= 1; side += 2) {
            for (int index = 0; index < flows; index++) {
                int slot = FLOW_SLOT_OFFSET + (side < 0 ? 0 : FLOW_MAX_PER_SIDE) + index;
                int direction = (index + (side < 0 ? 0 : 1)) % 2 == 0 ? -1 : 1;
                double offset = MathUtils.hashUnit(owner, slot, 0, 0) * period;
                double phase = ((age + offset) / period) % 1;
                double head = Math.min(span, phase * (span + length)), tail = Math.max(0, head - length);
                if (head - tail < 1.0E-3) continue;
                Random random = new Random(Double.doubleToLongBits(MathUtils.hashUnit(owner, slot, 1, tick)));
                float fade = (float) Math.sqrt(Math.sin(Math.PI * phase));
                bolts.add(
                    new Bolt(
                        channel(
                            random,
                            side,
                            GROOVE_CENTER + direction * tail,
                            GROOVE_CENTER + direction * head,
                            FLOW_SEGMENTS),
                        0.004 + 0.003 * p,
                        (0.55F + 0.35F * p) * fade));
            }
        }
        double sparkChance = sparkChance(p);
        for (int side = -1; side <= 1; side += 2) {
            int slot = SPARK_SLOT_OFFSET + (side < 0 ? 0 : 1);
            Random random = random(owner, slot, tick);
            if (random.nextDouble() >= sparkChance) continue;
            double z = GROOVE_CENTER + (random.nextDouble() * 2 - 1) * span;
            Vec3d from = new Vec3d(side * GROOVE_OUTER, between(random, GROOVE_LOW, GROOVE_HIGH), z);
            Vec3d to = from.add(
                side * (SPARK_MIN_REACH + SPARK_REACH_GAIN * p * random.nextDouble()),
                0.005 + 0.025 * random.nextDouble(),
                (random.nextDouble() - 0.5) * 0.04);
            bolts.add(new Bolt(jagged(random, from, to, 3, 0.3), 0.003 + 0.002 * p, 0.8F * flicker(slot, tick)));
        }
        double hopChance = hopChance(p);
        for (int index = 0; index < HOP_SLOTS; index++) {
            int slot = HOP_SLOT_OFFSET + index;
            Random random = random(owner, slot, tick);
            if (random.nextDouble() >= hopChance) continue;
            bolts.add(new Bolt(hop(random, p), 0.0035 + 0.002 * p, 0.85F * flicker(slot, tick)));
        }
        return bolts;
    }

    private static List<Vec3d> hop(Random random, float p) {
        int side = random.nextBoolean() ? 1 : -1;
        Edge edge = HOP_EDGES.get(random.nextInt(HOP_EDGES.size()));
        double start = random.nextDouble();
        Vec3d from = edge.at(start, side);
        Vec3d to = random.nextDouble() < HOP_CROSS_CHANCE ? crossTarget(random, edge, from, side) : null;
        if (to == null) {
            double reach = (HOP_MIN + (HOP_MAX - HOP_MIN) * p * random.nextDouble()) / edge.length();
            double end = start + (random.nextBoolean() ? reach : -reach);
            if (end < 0 || end > 1) end = 2 * start - end;
            to = edge.at(MathUtils.clampUnit(end), side);
        }
        Vec3d lift = new Vec3d(side * between(random, 0.2, 1), between(random, -0.5, 1), between(random, -0.4, 0.4))
            .normalize()
            .scale(HOP_LIFT * between(random, 0.4, 1.6));
        List<Vec3d> points = new ArrayList<>(HOP_SEGMENTS + 1);
        for (int i = 0; i <= HOP_SEGMENTS; i++) {
            double t = i / (double) HOP_SEGMENTS;
            Vec3d jitter = i == 0 || i == HOP_SEGMENTS ? Vec3d.ZERO
                : unit(random).scale(HOP_JITTER * random.nextDouble());
            points.add(
                from.add(
                    to.subtract(from)
                        .scale(t))
                    .add(lift.scale(Math.sin(Math.PI * t)))
                    .add(jitter));
        }
        return points;
    }

    private static Vec3d crossTarget(Random random, Edge origin, Vec3d point, int side) {
        List<Edge> options = new ArrayList<>();
        for (Edge edge : HOP_EDGES) if (edge != origin && edge.coversZ(point.z())) options.add(edge);
        if (options.isEmpty()) return null;
        Edge target = options.get(random.nextInt(options.size()));
        double t = target.paramAtZ(point.z()) + (random.nextDouble() * 2 - 1) * HOP_MIN / target.length();
        return target.at(MathUtils.clampUnit(t), side);
    }

    public static List<Bolt> tethers(int owner, long tick, float intensity, Vec3d from, Vec3d to) {
        float p = MathUtils.clampUnit(intensity, 0F);
        if (p < MIN_INTENSITY || from == null || to == null) return List.of();
        List<Bolt> bolts = new ArrayList<>(2);
        for (int index = 0; index < 2; index++) {
            int slot = TETHER_SLOT_OFFSET + 2 + index;
            Random random = random(owner, slot, tick);
            bolts.add(
                new Bolt(
                    jagged(random, from, to, LEG_SEGMENTS, 0.3),
                    0.035 + 0.02 * p,
                    (0.45F + 0.45F * p) * flicker(slot, tick)));
        }
        return bolts;
    }

    private static List<Vec3d> channel(Random random, int side, double start, double end, int segments) {
        List<Vec3d> points = new ArrayList<>(segments + 1);
        double x = between(random, GROOVE_INNER, GROOVE_OUTER), y = between(random, GROOVE_LOW, GROOVE_HIGH);
        double stepX = (GROOVE_OUTER - GROOVE_INNER) * 0.45, stepY = (GROOVE_HIGH - GROOVE_LOW) * 0.45;
        for (int i = 0; i <= segments; i++) {
            if (i > 0) {
                x = Math.max(GROOVE_INNER, Math.min(GROOVE_OUTER, x + (random.nextDouble() * 2 - 1) * stepX));
                y = Math.max(GROOVE_LOW, Math.min(GROOVE_HIGH, y + (random.nextDouble() * 2 - 1) * stepY));
            }
            points.add(new Vec3d(side * x, y, start + (end - start) * i / segments));
        }
        return points;
    }

    private static List<Vec3d> jagged(Random random, Vec3d from, Vec3d to, int segments, double jitter) {
        Vec3d delta = to.subtract(from);
        double step = delta.length() / segments;
        Vec3d axis = delta.normalize();
        Vec3d side = Vec3d.rightOf(axis), up = side.cross(axis)
            .normalize();
        List<Vec3d> points = new ArrayList<>(segments + 1);
        points.add(from);
        double offsetSide = 0, offsetUp = 0;
        for (int i = 1; i <= segments; i++) {
            offsetSide += (random.nextDouble() * 2 - 1) * jitter * step;
            offsetUp += (random.nextDouble() * 2 - 1) * jitter * step;
            points.add(
                from.add(delta.scale(i / (double) segments))
                    .add(side.scale(offsetSide))
                    .add(up.scale(offsetUp)));
        }
        return points;
    }

    private static Vec3d unit(Random random) {
        double z = random.nextDouble() * 2 - 1, angle = random.nextDouble() * Math.PI * 2;
        double ring = Math.sqrt(1 - z * z);
        return new Vec3d(ring * Math.cos(angle), ring * Math.sin(angle), z);
    }

    private static double between(Random random, double low, double high) {
        return low + (high - low) * random.nextDouble();
    }

    private static float flicker(int slot, long tick) {
        long step = Math.floorMod(tick + slot * 5L, FLICKER_TICKS);
        return 1F - 0.45F * step / FLICKER_TICKS;
    }

    private static Random random(int owner, int slot, long tick) {
        long window = Math.floorDiv(tick + slot * 5L, FLICKER_TICKS);
        long seed = owner * 0x9E3779B97F4A7C15L ^ slot * 0xC2B2AE3D27D4EB4FL ^ window * 0x165667B19E3779F9L;
        return new Random(seed);
    }
}
