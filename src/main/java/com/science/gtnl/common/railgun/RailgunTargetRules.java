package com.science.gtnl.common.railgun;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.FakePlayer;

public final class RailgunTargetRules {

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
}
