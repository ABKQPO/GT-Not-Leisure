package com.science.gtnl.common.armorflight;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import com.science.gtnl.api.armorflight.ArmorFlightPiece;

public final class ArmorFlight {

    public static final double SPEED = 1.0;
    public static final int MIN_TICKS = 24, MAX_TICKS = 80;
    public static final int STAGGER = 8;
    public static final int SEQUENCE_LEAD = 14;
    public static final int HATCH_TICKS = 10;
    public static final int HOVER_TICKS = 9;
    public static final int SNAP_TICKS = 7;
    public static final double HOLD_DISTANCE = 2.6;
    public static final float SIDE_ANGLE = 40;
    public static final int ORBIT_TICKS = 10;
    public static final double BRAKE_AT = 0.76;
    public static final double LOCAL_LAUNCH_RANGE = 64.0;
    public static final int PURGE_TICKS = 12;
    public static final int LATCH_TICKS = 11;
    public static final double RETURN_SPEED = 1.7;
    public static final double BURST_DISTANCE = 1.9;

    private static final double[] DISTANCE = new double[257];

    static {
        double sum = 0;
        for (int i = 1; i <= 256; i++) {
            sum += speed((i - 0.5) / 256);
            DISTANCE[i] = sum;
        }
        for (int i = 1; i <= 256; i++) DISTANCE[i] /= sum;
    }

    private ArmorFlight() {}

    public static double clamp(double v, double min, double max) {
        return v < min ? min : Math.min(v, max);
    }

    public static float clamp(float v, float min, float max) {
        return v < min ? min : Math.min(v, max);
    }

    public static float rotLerp(float t, float from, float to) {
        return from + t * MathHelper.wrapAngleTo180_float(to - from);
    }

    public static FlightVec feet(Entity entity, float partialTick) {
        return new FlightVec(
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTick,
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTick - entity.yOffset,
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTick);
    }

    public static float bodyYaw(Entity entity, float partialTick) {
        if (entity instanceof EntityLivingBase living) {
            return rotLerp(partialTick, living.prevRenderYawOffset, living.renderYawOffset);
        }
        return entity.rotationYaw;
    }

    public static FlightVec dockPoint(FlightVec feet, float height, ArmorFlightPiece piece) {
        return feet.add(0, height * piece.heightFraction, 0);
    }

    public static FlightVec dockPoint(Entity entity, float partialTick, ArmorFlightPiece piece) {
        return dockPoint(feet(entity, partialTick), entity.height, piece);
    }

    public static int segments(ArmorFlightPiece piece) {
        return switch (piece) {
            case CHEST, LEGS -> 3;
            case FEET -> 2;
            default -> 1;
        };
    }

    public static double unfold(float hoverTicks) {
        return ease(hoverTicks / (HOVER_TICKS * 0.7));
    }

    public static float segmentLockTick(int j, int n) {
        float window = SNAP_TICKS + LATCH_TICKS - 3;
        return window * (j + 1) / n;
    }

    public static double segmentClose(float sinceSnap, int j, int n) {
        float end = segmentLockTick(j, n);
        float start = Math.max(0, end - (SNAP_TICKS + 2.5F));
        double t = clamp((sinceSnap - start) / (end - start), 0, 1);
        return t * t * t;
    }

    public static double burstSpread(float age) {
        if (age < 0) return 0;
        if (age < 2) return age / 2.0;
        return Math.max(0, 1 - (age - 2) / (PURGE_TICKS + 6.0));
    }

    public static double ease(double t) {
        t = clamp(t, 0, 1);
        return t * t * (3 - 2 * t);
    }

    public static double cruise(double t) {
        t = clamp(t, 0, 1);
        final double a = 0.25, v = 1 / (1 - a);
        if (t < a) return v * t * t / (2 * a);
        if (t > 1 - a) return 1 - v * (1 - t) * (1 - t) / (2 * a);
        return v * (t - a / 2);
    }

    public static int arrivalTicks(int flight, boolean behind) {
        return flight + HOVER_TICKS + orbitTicks(behind) + SNAP_TICKS + LATCH_TICKS;
    }

    public static double speed(double t) {
        if (t < 0.10) {
            double u = t / 0.10;
            return 1 - (1 - u) * (1 - u) * (1 - u);
        }
        if (t < BRAKE_AT) return 1 + 0.08 * (t - 0.10);
        double u = (t - BRAKE_AT) / (1 - BRAKE_AT);
        double v = 1 + 0.08 * (BRAKE_AT - 0.10);
        return v * Math.pow(1 - u, 3);
    }

    public static double flightProfile(double t) {
        t = clamp(t, 0, 1);
        double x = t * 256;
        int i = Math.min(255, (int) x);
        double f = x - i;
        return DISTANCE[i] + (DISTANCE[i + 1] - DISTANCE[i]) * f;
    }

    public static FlightVec forward(float bodyYaw) {
        double yaw = Math.toRadians(bodyYaw);
        return new FlightVec(-Math.sin(yaw), 0, Math.cos(yaw));
    }

    public static float sideAngle(ArmorFlightPiece piece, boolean behind) {
        if (!behind) return 0;
        return piece.ordinal() % 2 == 0 ? SIDE_ANGLE : -SIDE_ANGLE;
    }

    public static float backAngle(float sideAngle) {
        return sideAngle < 0 ? -180 : 180;
    }

    public static int orbitTicks(boolean behind) {
        return behind ? ORBIT_TICKS : 0;
    }

    public static FlightVec holdPoint(FlightVec dock, FlightVec direction) {
        return dock.add(direction.scale(HOLD_DISTANCE))
            .add(0, 0.25, 0);
    }

    public static FlightVec orbitPoint(FlightVec dock, float bodyYaw, float sideAngle, double u) {
        double e = ease(u);
        double swing = Math.sin(Math.PI * clamp(u, 0, 1));
        float angle = (float) (sideAngle + (backAngle(sideAngle) - sideAngle) * e);
        return dock.add(forward(bodyYaw + angle).scale(HOLD_DISTANCE * (1 + 0.12 * swing)))
            .add(0, 0.25 + 0.3 * swing, 0);
    }

    private static FlightVec cubic(FlightVec p0, FlightVec c1, FlightVec c2, FlightVec p3, double e) {
        double u = 1 - e;
        return p0.scale(u * u * u)
            .add(c1.scale(3 * u * u * e))
            .add(c2.scale(3 * u * e * e))
            .add(p3.scale(e * e * e));
    }

    private static FlightVec flatDirection(FlightVec dir) {
        FlightVec flat = dir.flat();
        return flat.lengthSqr() < 1.0E-4 ? new FlightVec(0, 0, 1) : flat.normalize();
    }

    public static FlightVec approach(FlightVec start, FlightVec hold, FlightVec forward, ArmorFlightPiece piece,
        double e) {
        double d = start.distanceTo(hold);
        FlightVec flat = flatDirection(hold.subtract(start));
        FlightVec side = new FlightVec(-flat.z(), 0, flat.x())
            .scale((piece.ordinal() % 2 == 0 ? 1 : -1) * (1.5 + Math.min(d, 40) * 0.08));
        FlightVec c1 = start.add(0, 3.0 + 0.12 * Math.min(d, 60), 0)
            .add(side);
        FlightVec c2 = hold.add(forward.scale(Math.min(5.0, 2.0 + d / 4)))
            .add(0, 0.9, 0);
        return cubic(start, c1, c2, hold, e);
    }

    public static double approachLength(FlightVec start, FlightVec hold, FlightVec forward, ArmorFlightPiece piece) {
        double len = 0;
        FlightVec prev = start;
        for (int i = 1; i <= 32; i++) {
            FlightVec p = approach(start, hold, forward, piece, i / 32.0);
            len += p.distanceTo(prev);
            prev = p;
        }
        return len;
    }

    public static double boost(double t) {
        return Math.pow(clamp(t, 0, 1), 2.2);
    }

    public static float latchScale(float ticks) {
        if (ticks <= 0) return 1;
        return 1 + 0.13F * (float) Math.exp(-ticks / 2.2) * (float) Math.cos(ticks * 1.9);
    }

    public static double burstCurve(float age) {
        if (age <= 0) return 0;
        if (age < 5) return 1 - Math.pow(1 - age / 5.0, 4);
        return 1 + 0.025 * (age - 5);
    }

    public static FlightVec purgeOffset(ArmorFlightPiece piece, float bodyYaw, double amount) {
        double fwd, up, right;
        switch (piece) {
            case HEAD -> {
                fwd = -0.3;
                up = 0.62;
                right = 0.12;
            }
            case CHEST -> {
                fwd = 0.62;
                up = 0.3;
                right = -0.1;
            }
            case LEGS -> {
                fwd = 0.35;
                up = -0.1;
                right = 0.55;
            }
            default -> {
                fwd = -0.55;
                up = -0.05;
                right = -0.4;
            }
        }
        double yaw = Math.toRadians(bodyYaw);
        double fx = -Math.sin(yaw), fz = Math.cos(yaw);
        double rx = -Math.cos(yaw), rz = -Math.sin(yaw);
        return new FlightVec((fx * fwd + rx * right) * amount, up * amount, (fz * fwd + rz * right) * amount);
    }

    public static FlightVec position(FlightVec start, FlightVec end, double t) {
        return bezier(start, end, cruise(t));
    }

    public static FlightVec bezier(FlightVec start, FlightVec end, double e) {
        double d = start.distanceTo(end);
        FlightVec c1 = start.add(0, 2.5 + 0.12 * Math.min(d, 60), 0);
        FlightVec flat = flatDirection(start.subtract(end));
        FlightVec c2 = end.add(flat.scale(Math.min(3.0, 0.3 + d / 3)))
            .add(0, 1.2, 0);
        return cubic(start, c1, c2, end, e);
    }

    public static double length(FlightVec start, FlightVec end) {
        double len = 0;
        FlightVec prev = start;
        for (int i = 1; i <= 24; i++) {
            FlightVec p = position(start, end, i / 24.0);
            len += p.distanceTo(prev);
            prev = p;
        }
        return len;
    }

    public static int ticksFor(double length) {
        return MathHelper.clamp_int((int) Math.round(length / SPEED), MIN_TICKS, MAX_TICKS);
    }

    public static int returnTicksFor(double length) {
        return MathHelper.clamp_int((int) Math.round(length / RETURN_SPEED), 12, MAX_TICKS);
    }

    public static FlightVec launchPoint(FlightVec player, FlightVec anchorTop, boolean sameDimension,
        ArmorFlightPiece piece) {
        if (sameDimension && player.distanceTo(anchorTop) <= LOCAL_LAUNCH_RANGE) {
            return anchorTop.add((piece.ordinal() - 1.5) * 0.12, 0, 0);
        }
        FlightVec flat = flatDirection(sameDimension ? anchorTop.subtract(player) : new FlightVec(1, 0, 0.35));
        double spread = (piece.ordinal() - 1.5) * 1.5;
        FlightVec side = new FlightVec(-flat.z(), 0, flat.x());
        return player.add(flat.scale(30))
            .add(side.scale(spread))
            .add(0, 16 + piece.ordinal(), 0);
    }

    public static FlightVec homeTarget(FlightVec anchorTop, boolean sameDimension, FlightVec from) {
        if (sameDimension) {
            if (anchorTop.distanceTo(from) <= LOCAL_LAUNCH_RANGE) return anchorTop;
            return from.add(flatDirection(anchorTop.subtract(from)).scale(30))
                .add(0, 18, 0);
        }
        return from.add(0, 24, 0);
    }
}
