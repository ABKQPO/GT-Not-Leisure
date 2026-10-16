package com.science.gtnl.common.railgun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.WorldServer;

import org.jetbrains.annotations.Nullable;

import com.science.gtnl.common.railgun.RailgunDamage.DamageContext;

public final class RailgunChainResolver {

    public record Hit(EntityLivingBase target, double damage, boolean penetration, boolean pulse,
        @Nullable RailgunVec chainStartAt, boolean chainPropagation) {

        public Hit(EntityLivingBase target, double damage, boolean penetration, boolean pulse) {
            this(target, damage, penetration, pulse, null, false);
        }

        public Hit(EntityLivingBase target, double damage, boolean penetration, boolean pulse,
            @Nullable RailgunVec chainStartAt) {
            this(target, damage, penetration, pulse, chainStartAt, true);
        }
    }

    private RailgunChainResolver() {}

    public static List<Hit> resolveChain(WorldServer level, EntityPlayerMP source, EntityLivingBase start,
        DamageContext ctx) {
        return resolveChainFrom(level, source, start, ctx, Set.of(), null);
    }

    public static List<Hit> resolveChainForkedFrom(WorldServer level, EntityPlayerMP source, EntityLivingBase start,
        DamageContext ctx, Set<Integer> initiallyVisited, @Nullable RailgunVec firstSegmentVisualStart) {
        int forks = Math.max(1, ctx.chainForkCount());
        if (forks <= 1) {
            return resolveChainFrom(level, source, start, ctx, initiallyVisited, firstSegmentVisualStart);
        }
        List<Hit> out = new ArrayList<>();
        if (ctx.chainSegments() <= 0) return out;

        Set<Integer> visited = new HashSet<>(initiallyVisited);
        visited.add(start.getEntityId());

        List<EntityLivingBase> seeds = findNearestK(level, source, start, ctx.chainRadius(), visited, ctx.pvp(), forks);
        if (seeds.isEmpty()) return out;

        RailgunVec branchVisualStart = (firstSegmentVisualStart != null) ? firstSegmentVisualStart
            : RailgunVec.pos(start)
                .add(0.0D, start.height / 2.0D, 0.0D);

        seeds.forEach(seed -> visited.add(seed.getEntityId()));
        for (EntityLivingBase seed : seeds) {
            out.addAll(walkBranch(level, source, seed, ctx, visited, branchVisualStart));
        }
        return out;
    }

    public static List<Hit> resolveChainFromPoint(WorldServer level, EntityPlayerMP source, RailgunVec start,
        DamageContext ctx) {
        List<Hit> out = new ArrayList<>();
        if (ctx.chainSegments() <= 0) return out;

        Set<Integer> visited = new HashSet<>();
        List<EntityLivingBase> seeds = findNearestK(level, source, start, ctx.chainRadius(), visited, ctx.pvp(), 1);
        if (seeds.isEmpty()) return out;

        EntityLivingBase seed = seeds.getFirst();
        visited.add(seed.getEntityId());
        out.addAll(walkBranch(level, source, seed, ctx, visited, start));
        return out;
    }

    private static List<Hit> walkBranch(WorldServer level, EntityPlayerMP source, EntityLivingBase seed,
        DamageContext ctx, Set<Integer> visited, RailgunVec branchVisualStart) {
        List<Hit> out = new ArrayList<>();

        double damage = ctx.firstDamage();
        if (!ctx.isMaxCharged()) {
            damage *= ctx.chainDecay();
        }
        out.add(new Hit(seed, damage, false, false, branchVisualStart));
        EntityLivingBase prev = seed;
        for (int i = 1; i < ctx.chainSegments(); i++) {
            damage *= ctx.chainDecay();
            EntityLivingBase next = findNearest(level, source, prev, ctx.chainRadius(), visited, ctx.pvp());
            if (next == null) break;
            visited.add(next.getEntityId());
            out.add(new Hit(next, damage, false, false, null));
            prev = next;
        }
        return out;
    }

    public static List<Hit> resolveChainFrom(WorldServer level, EntityPlayerMP source, EntityLivingBase start,
        DamageContext ctx, Set<Integer> initiallyVisited, @Nullable RailgunVec firstSegmentVisualStart) {
        List<Hit> out = new ArrayList<>();
        if (ctx.chainSegments() <= 0) return out;

        Set<Integer> visited = new HashSet<>(initiallyVisited);
        visited.add(start.getEntityId());

        EntityLivingBase prev = start;
        double damage = ctx.firstDamage();
        boolean firstEmitted = false;
        for (int i = 0; i < ctx.chainSegments(); i++) {

            if (!ctx.isMaxCharged() || i > 0) {
                damage *= ctx.chainDecay();
            }
            EntityLivingBase next = findNearest(level, source, prev, ctx.chainRadius(), visited, ctx.pvp());
            if (next == null) break;
            visited.add(next.getEntityId());
            RailgunVec visualStart = (!firstEmitted) ? firstSegmentVisualStart : null;
            out.add(new Hit(next, damage, false, false, visualStart));
            firstEmitted = true;
            prev = next;
        }
        return out;
    }

    public static List<Hit> resolvePenetration(WorldServer level, EntityPlayerMP source, EntityLivingBase firstHit,
        DamageContext ctx, int maxTargets) {
        List<Hit> out = new ArrayList<>();
        if (!ctx.isMaxCharged() || maxTargets <= 0) return out;

        RailgunVec from = RailgunVec.eye(source);
        RailgunVec dir = RailgunVec.pos(firstHit)
            .subtract(from)
            .normalize();
        double maxLen = 32.0D;
        RailgunVec to = from.add(dir.scale(maxLen));

        AxisAlignedBB box = from.boxTo(to, 1.5D);
        List<EntityLivingBase> candidates = query(
            level,
            box,
            e -> e != source && e != firstHit && shouldTarget(source, e, ctx.pvp()));

        candidates.sort((a, b) -> {
            double da = RailgunVec.pos(a)
                .subtract(from)
                .dot(dir);
            double db = RailgunVec.pos(b)
                .subtract(from)
                .dot(dir);
            return Double.compare(da, db);
        });
        int taken = 0;
        for (EntityLivingBase ent : candidates) {
            RailgunVec v = RailgunVec.pos(ent)
                .subtract(from);
            double along = v.dot(dir);
            if (along < 0.0D || along > maxLen) continue;

            double perp = v.subtract(dir.scale(along))
                .length();
            if (perp > 1.5D) continue;
            out.add(new Hit(ent, ctx.firstDamage(), true, false));
            if (++taken >= maxTargets) break;
        }
        return out;
    }

    public static List<Hit> resolvePulse(WorldServer level, EntityPlayerMP source, RailgunVec center, double radius,
        double damageRatio, DamageContext ctx, int excludeId) {
        List<Hit> out = new ArrayList<>();
        if (!ctx.isMaxCharged() || radius <= 0) return out;
        AxisAlignedBB box = center.boxTo(center, radius);
        double r2 = radius * radius;
        for (EntityLivingBase ent : query(
            level,
            box,
            e -> e != source && e.getEntityId() != excludeId && shouldTarget(source, e, ctx.pvp()))) {
            if (RailgunVec.pos(ent)
                .distanceToSqr(center) > r2) continue;
            double dmg = ctx.firstDamage() * damageRatio;
            if (ent instanceof EntityPlayer) {
                dmg *= 0.5D;
            }
            out.add(new Hit(ent, dmg, false, true));
        }
        return out;
    }

    public static List<Hit> resolveImpactSplash(WorldServer level, EntityPlayerMP source, RailgunVec center,
        double radius, double damageRatio, int primaryId, DamageContext ctx) {
        List<Hit> out = new ArrayList<>();
        if (radius <= 0 || damageRatio <= 0) return out;
        AxisAlignedBB box = center.boxTo(center, radius);
        double r2 = radius * radius;
        for (EntityLivingBase ent : query(level, box, e -> e != source && shouldTarget(source, e, ctx.pvp()))) {
            if (ent.getEntityId() == primaryId) continue;
            double d2 = RailgunVec.pos(ent)
                .distanceToSqr(center);
            if (d2 > r2) continue;
            double d = Math.sqrt(d2);

            double falloff = 1.0D - (d / radius) * 0.6D;
            double dmg = ctx.firstDamage() * damageRatio * falloff;
            if (ent instanceof EntityPlayer) {
                dmg *= 0.5D;
            }
            out.add(new Hit(ent, dmg, false, true));
        }
        return out;
    }

    private static EntityLivingBase findNearest(WorldServer level, EntityPlayerMP source, Entity from, double radius,
        Set<Integer> visited, boolean pvp) {
        RailgunVec c = RailgunVec.pos(from);
        AxisAlignedBB box = c.boxTo(c, radius);
        EntityLivingBase best = null;
        double r2 = radius * radius;
        double bestDistSqr = Double.MAX_VALUE;
        for (EntityLivingBase ent : query(level, box, e -> shouldTarget(source, e, pvp))) {
            if (ent == source) continue;
            if (visited.contains(ent.getEntityId())) continue;
            double d2 = RailgunVec.pos(ent)
                .distanceToSqr(c);
            if (d2 > r2) continue;
            if (d2 < bestDistSqr) {
                best = ent;
                bestDistSqr = d2;
            }
        }
        return best;
    }

    private static List<EntityLivingBase> findNearestK(WorldServer level, EntityPlayerMP source, Entity from,
        double radius, Set<Integer> visited, boolean pvp, int k) {
        return findNearestK(level, source, RailgunVec.pos(from), radius, visited, pvp, k);
    }

    private static List<EntityLivingBase> findNearestK(WorldServer level, EntityPlayerMP source, RailgunVec center,
        double radius, Set<Integer> visited, boolean pvp, int k) {
        if (k <= 0) return List.of();
        AxisAlignedBB box = center.boxTo(center, radius);
        double r2 = radius * radius;
        List<EntityLivingBase> all = new ArrayList<>();
        for (EntityLivingBase ent : query(level, box, e -> shouldTarget(source, e, pvp))) {
            if (ent == source) continue;
            if (visited.contains(ent.getEntityId())) continue;
            if (RailgunVec.pos(ent)
                .distanceToSqr(center) > r2) continue;
            all.add(ent);
        }
        all.sort(
            (a, b) -> Double.compare(
                RailgunVec.pos(a)
                    .distanceToSqr(center),
                RailgunVec.pos(b)
                    .distanceToSqr(center)));
        if (all.size() > k) {
            return new ArrayList<>(all.subList(0, k));
        }
        return all;
    }

    private static boolean shouldTarget(EntityPlayerMP source, EntityLivingBase entity, boolean pvp) {
        return RailgunTargetRules.canAffect(source, entity, pvp);
    }

    private static List<EntityLivingBase> query(WorldServer world, AxisAlignedBB box,
        java.util.function.Predicate<EntityLivingBase> filter) {
        List<EntityLivingBase> result = new ArrayList<>();
        for (Object candidate : world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
            EntityLivingBase entity = (EntityLivingBase) candidate;
            if (filter.test(entity)) result.add(entity);
        }
        return result;
    }
}
