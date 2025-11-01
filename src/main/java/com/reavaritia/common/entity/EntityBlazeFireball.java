package com.reavaritia.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityBlazeFireball extends EntitySmallFireball {

    public EntityBlazeFireball(World world) {
        super(world);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityBlazeFireball(World world, EntityPlayer player) {
        super(world, player, 0, 0, 0);
        Vec3 look = player.getLookVec();
        this.setPosition(player.posX + look.xCoord, player.posY + 1.5 + look.yCoord, player.posZ + look.zCoord);
        this.accelerationX = look.xCoord * 0.1;
        this.accelerationY = look.yCoord * 0.1;
        this.accelerationZ = look.zCoord * 0.1;
    }

    @Override
    public void onImpact(MovingObjectPosition mop) {
        if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = mop.blockX;
            int y = mop.blockY;
            int z = mop.blockZ;

            if (worldObj.getBlock(x, y, z) == Blocks.sand) {
                worldObj.setBlock(x, y, z, Blocks.glass);
            } else {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
                            int targetX = x + i;
                            int targetY = y + j;
                            int targetZ = z + k;
                            if (worldObj.isAirBlock(targetX, targetY, targetZ)) {
                                worldObj.setBlock(targetX, targetY, targetZ, Blocks.fire);
                            }
                        }
                    }
                }
            }
        }

        if (mop.entityHit instanceof EntityLivingBase target) {

            if (target instanceof EntityEnderman) {
                ((EntityEnderman) target).setAttackTarget(shootingEntity);
                target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) shootingEntity), 50.0F);
            } else {
                if (!target.isPotionActive(Potion.fireResistance.id)) {
                    target.setFire(5);
                    target.attackEntityFrom(DamageSource.onFire, 50.0F);
                }
            }

            if (target instanceof EntitySkeleton) {
                if (!worldObj.isRemote) {
                    target.entityDropItem(new ItemStack(Items.skull, 1, 1), 0.0F);
                }
            }
        }

        this.setDead();
    }
}
