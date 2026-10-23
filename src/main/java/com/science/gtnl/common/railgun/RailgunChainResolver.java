package com.science.gtnl.common.railgun;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldServer;

import org.jetbrains.annotations.Nullable;

import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.railgun.RailgunDamage.DamageContext;

public class RailgunChainResolver {

    private static final double AREA_PLAYER_RATIO = 0.5;

    private static final double PENETRATION_LENGTH = 32, PENETRATION_WIDTH = 1.5, SPLASH_FALLOFF = 0.6;

    public enum Kind {
        PRIMARY,
        CHAIN,
        PENETRATION,
        AREA
    }

    public record Hit(EntityLivingBase target, double damage, Kind kind, @Nullable Vec3d chainStartAt) {

        public static Hit primary(EntityLivingBase target, double damage) {
            return new Hit(target, damage, Kind.PRIMARY, null);
        }
    }

    private RailgunChainResolver() {}

    public static List<Hit> strongestPerTarget(List<Hit> hits) {
        Map<Integer, Hit> strongest = new LinkedHashMap<>();
        for (Hit hit : hits) strongest.merge(
            hit.target()
                .getEntityId(),
            hit,
            (kept, next) -> next.damage() > kept.damage() ? next : kept);
        return new ArrayList<>(strongest.values());
    }

    public static List<Hit> chainFrom(WorldServer world, EntityPlayerMP shooter, EntityLivingBase start,
        DamageContext ctx, Set<Integer> excluded, @Nullable Vec3d visualStart) {
        Set<Integer> visited = new HashSet<>(excluded);
        visited.add(start.getEntityId());
        return branches(
            world,
            shooter,
            Vec3d.pos(start),
            Math.max(1, ctx.chainForkCount()),
            ctx,
            visited,
            visualStart != null ? visualStart : Vec3d.center(start));
    }

    public static List<Hit> chainFromPoint(WorldServer world, EntityPlayerMP shooter, Vec3d point, DamageContext ctx) {
        return branches(world, shooter, point, 1, ctx, new HashSet<>(), point);
    }

    private static List<Hit> branches(WorldServer world, EntityPlayerMP shooter, Vec3d center, int forks,
        DamageContext ctx, Set<Integer> visited, Vec3d visualStart) {
        List<Hit> out = new ArrayList<>();
        if (ctx.chainSegments() <= 0) return out;
        List<EntityLivingBase> seeds = nearest(world, shooter, center, ctx.chainRadius(), visited, ctx.pvp(), forks);
        seeds.forEach(seed -> visited.add(seed.getEntityId()));
        for (EntityLivingBase seed : seeds) walk(world, shooter, seed, ctx, visited, visualStart, out);
        return out;
    }

    private static void walk(WorldServer world, EntityPlayerMP shooter, EntityLivingBase seed, DamageContext ctx,
        Set<Integer> visited, Vec3d visualStart, List<Hit> out) {
        double damage = ctx.firstDamage() * (ctx.isMaxCharged() ? 1 : ctx.chainDecay());
        out.add(new Hit(seed, damage, Kind.CHAIN, visualStart));
        EntityLivingBase previous = seed;
        for (int i = 1; i < ctx.chainSegments(); i++) {
            damage *= ctx.chainDecay();
            List<EntityLivingBase> next = nearest(
                world,
                shooter,
                Vec3d.pos(previous),
                ctx.chainRadius(),
                visited,
                ctx.pvp(),
                1);
            if (next.isEmpty()) return;
            previous = next.getFirst();
            visited.add(previous.getEntityId());
            out.add(new Hit(previous, damage, Kind.CHAIN, null));
        }
    }

    public static List<Hit> penetration(WorldServer world, EntityPlayerMP shooter, EntityLivingBase firstHit,
        DamageContext ctx, int maxTargets) {
        List<Hit> out = new ArrayList<>();
        if (!ctx.isMaxCharged() || maxTargets <= 0) return out;
        Vec3d from = Vec3d.eye(shooter);
        Vec3d direction = Vec3d.pos(firstHit)
            .subtract(from)
            .normalize();
        Vec3d to = from.add(direction.scale(PENETRATION_LENGTH));
        List<EntityLivingBase> candidates = query(
            world,
            from.boxTo(to, PENETRATION_WIDTH),
            e -> e != firstHit && RailgunTargetRules.canAffect(shooter, e, ctx.pvp()));
        candidates.sort(
            Comparator.comparingDouble(
                entity -> Vec3d.pos(entity)
                    .subtract(from)
                    .dot(direction)));
        for (EntityLivingBase entity : candidates) {
            Vec3d offset = Vec3d.pos(entity)
                .subtract(from);
            double along = offset.dot(direction);
            if (along < 0 || along > PENETRATION_LENGTH
                || offset.subtract(direction.scale(along))
                    .length() > PENETRATION_WIDTH)
                continue;
            out.add(new Hit(entity, ctx.firstDamage(), Kind.PENETRATION, null));
            if (out.size() >= maxTargets) break;
        }
        return out;
    }

    public static List<Hit> pulse(WorldServer world, EntityPlayerMP shooter, Vec3d center, double radius, double ratio,
        DamageContext ctx, int excludeId) {
        return area(world, shooter, center, radius, ratio, 0, excludeId, ctx);
    }

    public static List<Hit> splash(WorldServer world, EntityPlayerMP shooter, Vec3d center, double radius, double ratio,
        DamageContext ctx, int excludeId) {
        return area(world, shooter, center, radius, ratio, SPLASH_FALLOFF, excludeId, ctx);
    }

    private static List<Hit> area(WorldServer world, EntityPlayerMP shooter, Vec3d center, double radius, double ratio,
        double falloff, int excludeId, DamageContext ctx) {
        List<Hit> out = new ArrayList<>();
        if (radius <= 0 || ratio <= 0) return out;
        for (EntityLivingBase entity : query(
            world,
            center.boxTo(center, radius),
            e -> e.getEntityId() != excludeId && RailgunTargetRules.canAffect(shooter, e, ctx.pvp()))) {
            double distance = Math.sqrt(
                Vec3d.pos(entity)
                    .distanceToSqr(center));
            if (distance > radius) continue;
            double damage = ctx.firstDamage() * ratio * (1 - distance / radius * falloff);
            if (entity instanceof EntityPlayer) damage *= AREA_PLAYER_RATIO;
            out.add(new Hit(entity, damage, Kind.AREA, null));
        }
        return out;
    }

    private static List<EntityLivingBase> nearest(WorldServer world, EntityPlayerMP shooter, Vec3d center,
        double radius, Set<Integer> visited, boolean pvp, int count) {
        if (count <= 0) return List.of();
        List<EntityLivingBase> found = query(
            world,
            center.boxTo(center, radius),
            e -> !visited.contains(e.getEntityId()) && RailgunTargetRules.canAffect(shooter, e, pvp)
                && Vec3d.pos(e)
                    .distanceToSqr(center) <= radius * radius);
        found.sort(
            Comparator.comparingDouble(
                entity -> Vec3d.pos(entity)
                    .distanceToSqr(center)));
        return found.size() > count ? new ArrayList<>(found.subList(0, count)) : found;
    }

    private static List<EntityLivingBase> query(WorldServer world, AxisAlignedBB box,
        Predicate<EntityLivingBase> filter) {
        List<EntityLivingBase> result = new ArrayList<>();
        for (Object candidate : world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
            EntityLivingBase entity = (EntityLivingBase) candidate;
            if (filter.test(entity)) result.add(entity);
        }
        return result;
    }
}
