package com.science.gtnl.common.railgun;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldServer;

import org.jetbrains.annotations.Nullable;

import com.gtnllib.utils.Vec3d;
import com.science.gtnl.common.railgun.RailgunDamage.DamageContext;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

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
        Int2ObjectLinkedOpenHashMap<Hit> strongest = new Int2ObjectLinkedOpenHashMap<>();
        for (Hit hit : hits) strongest.merge(
            hit.target()
                .getEntityId(),
            hit,
            (kept, next) -> next.damage() > kept.damage() ? next : kept);
        return new ObjectArrayList<>(strongest.values());
    }

    public static List<Hit> chainFrom(WorldServer world, EntityPlayerMP shooter, EntityLivingBase start,
        DamageContext ctx, IntSet excluded, @Nullable Vec3d visualStart) {
        IntOpenHashSet visited = new IntOpenHashSet(excluded);
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
        return branches(world, shooter, point, 1, ctx, new IntOpenHashSet(), point);
    }

    private static List<Hit> branches(WorldServer world, EntityPlayerMP shooter, Vec3d center, int forks,
        DamageContext ctx, IntSet visited, Vec3d visualStart) {
        List<Hit> out = new ObjectArrayList<>();
        if (ctx.chainSegments() <= 0) return out;
        List<EntityLivingBase> seeds = nearest(world, shooter, center, ctx.chainRadius(), visited, ctx.pvp(), forks);
        seeds.forEach(seed -> visited.add(seed.getEntityId()));
        for (EntityLivingBase seed : seeds) walk(world, shooter, seed, ctx, visited, visualStart, out);
        return out;
    }

    private static void walk(WorldServer world, EntityPlayerMP shooter, EntityLivingBase seed, DamageContext ctx,
        IntSet visited, Vec3d visualStart, List<Hit> out) {
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
        Vec3d from, Vec3d direction, DamageContext ctx, int maxTargets) {
        List<Hit> out = new ObjectArrayList<>();
        if (!ctx.isMaxCharged() || maxTargets <= 0) return out;
        Vec3d forward = direction.normalize();
        Vec3d to = from.add(forward.scale(PENETRATION_LENGTH));
        // Continue the shot from its impact, testing bodies rather than aiming at their feet.
        Object2DoubleLinkedOpenHashMap<EntityLivingBase> distances = new Object2DoubleLinkedOpenHashMap<>();
        for (EntityLivingBase entity : query(
            world,
            from.boxTo(to, PENETRATION_WIDTH),
            e -> e != firstHit && RailgunTargetRules.canAffect(shooter, e, ctx.pvp()))) {
            if (Vec3d.center(entity)
                .subtract(from)
                .dot(forward) < 0) continue;
            AxisAlignedBB box = entity.boundingBox.expand(PENETRATION_WIDTH, PENETRATION_WIDTH, PENETRATION_WIDTH);
            MovingObjectPosition hit = box.calculateIntercept(from.toMc(), to.toMc());
            if (box.isVecInside(from.toMc())) distances.put(entity, 0D);
            else if (hit != null) distances.put(entity, from.distanceToSqr(Vec3d.of(hit.hitVec)));
        }
        List<EntityLivingBase> candidates = new ObjectArrayList<>(distances.keySet());
        candidates.sort(Comparator.comparingDouble(distances::getDouble));
        for (EntityLivingBase entity : candidates) {
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
        List<Hit> out = new ObjectArrayList<>();
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
        double radius, IntSet visited, boolean pvp, int count) {
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
        return found.size() > count ? new ObjectArrayList<>(found.subList(0, count)) : found;
    }

    private static List<EntityLivingBase> query(WorldServer world, AxisAlignedBB box,
        Predicate<EntityLivingBase> filter) {
        List<EntityLivingBase> result = new ObjectArrayList<>();
        for (Object candidate : world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
            EntityLivingBase entity = (EntityLivingBase) candidate;
            if (filter.test(entity)) result.add(entity);
        }
        return result;
    }
}
