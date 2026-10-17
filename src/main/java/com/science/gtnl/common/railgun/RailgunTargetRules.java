package com.science.gtnl.common.railgun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.FakePlayer;

public final class RailgunTargetRules {

    private static final double SEARCH_PADDING = 16;
    private static final double LOCAL_REFERENCE_RANGE = 64;

    private RailgunTargetRules() {}

    public static boolean canAffect(EntityPlayer shooter, Entity target, boolean allowPlayers) {
        if (target == null || target == shooter || target.isDead) return false;
        if (target instanceof EntityPlayer player) return allowPlayers && !(player instanceof FakePlayer)
            && !player.capabilities.isCreativeMode
            && player.isEntityAlive()
            && (shooter == null || shooter.canAttackPlayer(player));
        if (target instanceof EntityLivingBase living) return living.isEntityAlive() && living.getHealth() > 0;
        return target.canBeCollidedWith();
    }

    public static Entity damageAnchor(Entity target, RailgunVec reference) {
        if (target == null || target instanceof EntityDragonPart || !(target instanceof IEntityMultiPart))
            return target;
        RailgunVec center = RailgunVec.center(target);
        RailgunVec point = reference == null || !reference.finite() ? center : reference;
        if (point.distanceToSqr(center) > LOCAL_REFERENCE_RANGE) point = center;
        EntityDragonPart nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (EntityDragonPart part : parts(target)) {
            double distance = RailgunVec.center(part)
                .distanceToSqr(point);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = part;
            }
        }
        return nearest == null ? target : nearest;
    }

    private static List<EntityDragonPart> parts(Entity target) {
        List<EntityDragonPart> parts = new ArrayList<>(8);
        if (target instanceof EntityDragon dragon) {
            Collections.addAll(
                parts,
                dragon.dragonPartHead,
                dragon.dragonPartBody,
                dragon.dragonPartWing1,
                dragon.dragonPartWing2,
                dragon.dragonPartTail1,
                dragon.dragonPartTail2,
                dragon.dragonPartTail3);
        }
        if (target.worldObj != null)
            for (Object candidate : target.worldObj.getEntitiesWithinAABB(EntityDragonPart.class, searchBox(target))) {
                EntityDragonPart part = (EntityDragonPart) candidate;
                if (part.isEntityEqual(target) && !parts.contains(part)) parts.add(part);
            }
        return parts;
    }

    private static AxisAlignedBB searchBox(Entity target) {
        return target.boundingBox.expand(SEARCH_PADDING, SEARCH_PADDING, SEARCH_PADDING);
    }
}
