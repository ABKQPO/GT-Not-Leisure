package com.science.gtnl.common.railgun;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.gtnllib.utils.Vec3d;

public class RailgunRaycast {

    private static final int ENTITY_QUERY_SEGMENT_LENGTH = 16, BLOCK_TRACE_SEGMENT_LENGTH = 64;
    private static final int MAX_RANGE_MULTIPLIER = 4;
    private static final double MIN_LENGTH_SQUARED = 1.0E-8, PROJECTION_TOLERANCE = 1.0E-6;
    private static final int CHUNK_SHIFT = 4;
    private static final double EDGE_BACKOFF = 1.0E-3, CROSSING_TIE = 1.0E-9;

    private RailgunRaycast() {}

    public record EntityHit(Entity entity, Vec3d position) {}

    public record Result(Vec3d blockEnd, MovingObjectPosition block, EntityHit hit) {}

    public static double effectiveRange(double base, int rangeModules) {
        return base * Math.min(MAX_RANGE_MULTIPLIER, 1 << Math.max(0, Math.min(2, rangeModules)));
    }

    public static Result traceFirst(World world, Entity shooter, Vec3d from, Vec3d to, double padding, double inflation,
        Predicate<Entity> filter) {
        // Why: vanilla tracing calls getBlock per step, which loads or generates missing chunks on the server thread.
        Vec3d reach = loadedEnd(from, to, world.getChunkProvider()::chunkExists);
        // Why: vanilla tracing gives up after 200 block boundaries and then reports a miss, as if nothing was hit.
        MovingObjectPosition block = traceSegments(from, reach, (start, next) -> {
            MovingObjectPosition hit = world.func_147447_a(start.toMc(), next.toMc(), false, true, false);
            return hit != null && hit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK ? hit : null;
        });
        Vec3d end = block != null ? sanitize(from, reach, Vec3d.of(block.hitVec)) : reach;
        return new Result(end, block, traceEntities(world, shooter, from, end, padding, inflation, filter));
    }

    public static <T> T traceSegments(Vec3d from, Vec3d to, BiFunction<Vec3d, Vec3d, T> trace) {
        Vec3d delta = to.subtract(from);
        int segments = Math.max(1, (int) Math.ceil(delta.length() / BLOCK_TRACE_SEGMENT_LENGTH));
        Vec3d start = from;
        for (int i = 1; i <= segments; i++) {
            Vec3d next = i == segments ? to : from.add(delta.scale(i / (double) segments));
            T hit = trace.apply(start, next);
            if (hit != null) return hit;
            start = next;
        }
        return null;
    }

    public static Vec3d loadedEnd(Vec3d from, Vec3d to, BiPredicate<Integer, Integer> loaded) {
        int chunkX = chunk(from.x()), chunkZ = chunk(from.z());
        if (!loaded.test(chunkX, chunkZ)) return from;
        int lastX = chunk(to.x()), lastZ = chunk(to.z());
        Vec3d delta = to.subtract(from);
        double dx = delta.x(), dz = delta.z();
        int stepX = dx > 0 ? 1 : -1, stepZ = dz > 0 ? 1 : -1;
        while (chunkX != lastX || chunkZ != lastZ) {
            double nextX = crossing(from.x(), dx, chunkX, stepX), nextZ = crossing(from.z(), dz, chunkZ, stepZ);
            double exit = Math.min(nextX, nextZ);
            if (exit > 1) break;
            boolean tie = Math.abs(nextX - nextZ) < CROSSING_TIE, alongX = nextX < nextZ;
            double axis = tie ? Math.min(Math.abs(dx), Math.abs(dz)) : Math.abs(alongX ? dx : dz);
            Vec3d edge = from.add(delta.scale(Math.max(0, exit - EDGE_BACKOFF / axis)));
            if (tie && (!loaded.test(chunkX + stepX, chunkZ) || !loaded.test(chunkX, chunkZ + stepZ))) return edge;
            if (tie || alongX) chunkX += stepX;
            if (tie || !alongX) chunkZ += stepZ;
            if (!loaded.test(chunkX, chunkZ)) return edge;
        }
        return to;
    }

    private static int chunk(double coordinate) {
        return MathHelper.floor_double(coordinate) >> CHUNK_SHIFT;
    }

    private static double crossing(double start, double delta, int chunk, int step) {
        if (delta == 0) return Double.POSITIVE_INFINITY;
        return (((chunk + (step > 0 ? 1 : 0)) << CHUNK_SHIFT) - start) / delta;
    }

    static Vec3d sanitize(Vec3d from, Vec3d to, Vec3d raw) {
        Vec3d delta = to.subtract(from);
        double lengthSquared = delta.dot(delta);
        if (!raw.finite() || lengthSquared < MIN_LENGTH_SQUARED) return to;
        double projection = raw.subtract(from)
            .dot(delta) / lengthSquared;
        if (projection < 0 || projection > 1 + PROJECTION_TOLERANCE) return to;
        return raw.distanceToSqr(from.add(delta.scale(projection))) > 1 ? to : raw;
    }

    public static EntityHit traceEntities(World world, Entity shooter, Vec3d from, Vec3d end, double padding,
        double inflation, Predicate<Entity> filter) {
        int segments = Math.max(
            1,
            (int) Math.ceil(
                end.subtract(from)
                    .length() / ENTITY_QUERY_SEGMENT_LENGTH));
        Vec3d step = end.subtract(from)
            .scale(1.0 / segments);
        Vec3d start = from;
        for (int i = 1; i <= segments; i++) {
            Vec3d next = i == segments ? end : from.add(step.scale(i));
            EntityHit best = null;
            double distance = Double.POSITIVE_INFINITY;
            for (Object candidate : world.getEntitiesWithinAABBExcludingEntity(shooter, start.boxTo(next, padding))) {
                Entity entity = (Entity) candidate;
                if (!filter.test(entity)) continue;
                var box = entity.boundingBox.expand(inflation, inflation, inflation);
                MovingObjectPosition hit = box.calculateIntercept(start.toMc(), next.toMc());
                boolean inside = box.isVecInside(start.toMc());
                if (hit == null && !inside) continue;
                Vec3d position = inside ? start : Vec3d.of(hit.hitVec);
                double d = start.distanceToSqr(position);
                if (d < distance) {
                    distance = d;
                    best = new EntityHit(entity, position);
                }
            }
            if (best != null) return best;
            start = next;
        }
        return null;
    }
}
