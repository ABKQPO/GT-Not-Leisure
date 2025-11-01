package com.science.gtnl.common.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.science.gtnl.mixins.early.Minecraft.AccessorEntityLivingBase;

public class EntitySaddleSlime extends EntitySlime {

    public EntitySaddleSlime(World world) {
        super(world);
        this.setSlimeSize(4);
        this.isImmuneToFire = true;
        this.tasks.taskEntries.clear();
        this.targetTasks.taskEntries.clear();
        this.setHealth(Integer.MAX_VALUE);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(Integer.MAX_VALUE);
    }

    @Override
    public void updateEntityActionState() {
        if (riddenByEntity instanceof EntityPlayer && getSaddle()) {
            setJumping(false);
            moveForward = 0.0F;
            moveStrafing = 0.0F;
        } else {
            super.updateEntityActionState();
        }
    }

    @Override
    public void setDead() {
        this.isDead = true;
    }

    @Override
    public void onUpdate() {
        if (!this.worldObj.isRemote) {
            if (getSaddle() && riddenByEntity instanceof EntityPlayer player) {
                this.rotationYaw = player.rotationYaw;
                this.renderYawOffset = player.renderYawOffset;
                this.rotationYawHead = player.rotationYawHead;

                player.fallDistance = 0.0f;
                this.fallDistance = 0.0f;

                boolean jumping = ((AccessorEntityLivingBase) player).getJumping();

                double motionX = 0.0;
                double motionZ = 0.0;
                if (player.moveForward != 0 || player.moveStrafing != 0) {
                    float yaw = player.rotationYaw * 0.017453292F;
                    motionX = -MathHelper.sin(yaw) * 0.5 * player.moveForward
                        + MathHelper.cos(yaw) * 0.5 * player.moveStrafing;
                    motionZ = MathHelper.cos(yaw) * 0.5 * player.moveForward
                        + MathHelper.sin(yaw) * 0.5 * player.moveStrafing;
                }

                this.motionX = motionX;
                this.motionZ = motionZ;

                if (this.onGround) {
                    if (jumping) {
                        this.motionY = 2.0;
                    } else if (motionX != 0 || motionZ != 0) {
                        this.motionY = 0.4;
                    }
                }

                if (this.isInWater() || isMaterialFluidInBB(this.boundingBox.expand(-0.1, -0.4, -0.1))) {
                    this.motionY = jumping ? 2.0 : 0.2;
                }

                if (this.motionY < -0.25) {
                    this.motionY = Math.max(-2.0f, this.motionY - 0.2);
                    AxisAlignedBB bb = this.boundingBox.offset(0, -0.2, 0);
                    List<Entity> entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, bb);
                    for (Entity entity : entities) {
                        if (entity instanceof EntityLivingBase && entity != this.riddenByEntity && !entity.isDead) {
                            entity.attackEntityFrom(DamageSource.anvil, 5.0F);
                            this.motionY = 1.0F;
                            break;
                        }
                    }
                }
            } else {
                worldObj.removeEntity(this);
            }
        }
        super.onUpdate();
    }

    /**
     * Checks if there is any liquid material within the bounding box,
     * and applies water pushing force to the entity if applicable.
     */
    public boolean isMaterialFluidInBB(AxisAlignedBB boundingBox) {
        int minX = MathHelper.floor_double(boundingBox.minX);
        int maxX = MathHelper.floor_double(boundingBox.maxX + 1.0D);
        int minY = MathHelper.floor_double(boundingBox.minY);
        int maxY = MathHelper.floor_double(boundingBox.maxY + 1.0D);
        int minZ = MathHelper.floor_double(boundingBox.minZ);
        int maxZ = MathHelper.floor_double(boundingBox.maxZ + 1.0D);

        if (!worldObj.checkChunksExist(minX, minY, minZ, maxX, maxY, maxZ)) {
            return false;
        } else {
            boolean foundFluid = false;
            Vec3 pushVector = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

            for (int x = minX; x < maxX; ++x) {
                for (int y = minY; y < maxY; ++y) {
                    for (int z = minZ; z < maxZ; ++z) {
                        Block block = worldObj.getBlock(x, y, z);

                        if (block.getMaterial() instanceof MaterialLiquid) {
                            double liquidSurfaceY = (float) (y + 1)
                                - BlockLiquid.getLiquidHeightPercent(worldObj.getBlockMetadata(x, y, z));

                            if ((double) maxY >= liquidSurfaceY) {
                                foundFluid = true;
                                block.velocityToAddToEntity(worldObj, x, y, z, this, pushVector);
                            }
                        }
                    }
                }
            }

            if (pushVector.lengthVector() > 0.0D && this.isPushedByWater()) {
                pushVector = pushVector.normalize();
                double pushStrength = 0.014D;
                this.motionX += pushVector.xCoord * pushStrength;
                this.motionY += pushVector.yCoord * pushStrength;
                this.motionZ += pushVector.zCoord * pushStrength;
            }

            return foundFluid;
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (getSaddle()) {
            return true;
        }
        return super.attackEntityFrom(source, amount);
    }

    public void setSaddle(boolean saddle) {
        getEntityData().setBoolean("HasSlimeSaddle", saddle);
    }

    public boolean getSaddle() {
        return getEntityData().getBoolean("HasSlimeSaddle");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean("HasSlimeSaddle", getSaddle());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        getEntityData().setBoolean("HasSlimeSaddle", tag.getBoolean("HasSlimeSaddle"));
    }

    public void publicSetSlimeSize(int size) {
        this.setSlimeSize(size);
    }

    @Override
    public Item getDropItem() {
        return null;
    }

    @Override
    public boolean getCanSpawnHere() {
        return false;
    }

    @Override
    public boolean canDamagePlayer() {
        return false;
    }

    @Override
    public int getAttackStrength() {
        return 0;
    }
}
