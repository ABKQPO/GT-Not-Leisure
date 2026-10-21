package com.science.gtnl.common.railgun;

import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.gtnllib.utils.Vec3d;

public class RailgunRaycast {

    private static final int ENTITY_QUERY_SEGMENT_LENGTH = 16;
    private static final int MAX_RANGE_MULTIPLIER = 4;
    private static final double MIN_LENGTH_SQUARED = 1.0E-8, PROJECTION_TOLERANCE = 1.0E-6;

    private RailgunRaycast() {}

    public record EntityHit(Entity entity, Vec3d position) {}

    public record Result(Vec3d blockEnd, MovingObjectPosition block, EntityHit hit) {}

    public static double effectiveRange(double base, int rangeModules) {
        return base * Math.min(MAX_RANGE_MULTIPLIER, 1 << Math.max(0, Math.min(2, rangeModules)));
    }

    public static Result traceFirst(World world, Entity shooter, Vec3d from, Vec3d to, double padding, double inflation,
        Predicate<Entity> filter) {
        MovingObjectPosition block = world.func_147447_a(from.toMc(), to.toMc(), false, true, false);
        Vec3d end = block != null && block.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
            ? sanitize(from, to, Vec3d.of(block.hitVec))
            : to;
        return new Result(end, block, traceEntities(world, shooter, from, end, padding, inflation, filter));
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
