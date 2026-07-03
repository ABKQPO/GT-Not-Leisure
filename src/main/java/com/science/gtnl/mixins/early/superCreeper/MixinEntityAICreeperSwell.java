package com.science.gtnl.mixins.early.superCreeper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.science.gtnl.config.MainConfig;
import com.science.gtnl.utils.Utils;

@Mixin(EntityAICreeperSwell.class)
public abstract class MixinEntityAICreeperSwell {

    @Shadow
    EntityCreeper swellingCreeper;

    @Unique
    private EntitySpider gtnl$targetSpider;
    @Unique
    private long gtnl$lastSpiderTargetUpdateTick = 0;

    @Unique
    private Utils.TargetInfo gtnl$cachedBlockTarget = null;
    @Unique
    private Utils.TargetInfo gtnl$cachedPlayerTarget = null;

    @Unique
    private long gtnl$lastBlockTargetUpdateTick = 0;
    @Unique
    private long gtnl$lastPlayerTargetUpdateTick = 0;

    @Unique
    public Utils.TargetInfo gtnl$findNearestTarget() {
        long currentTick = this.swellingCreeper.worldObj.getTotalWorldTime();

        if (currentTick - gtnl$lastBlockTargetUpdateTick >= MainConfig.super_creeper.blockTargetInterval) {
            gtnl$cachedBlockTarget = gtnl$findNearestTargetBlock();
            gtnl$lastBlockTargetUpdateTick = currentTick;
        }

        if (currentTick - gtnl$lastPlayerTargetUpdateTick >= MainConfig.super_creeper.playerTargetInterval) {
            gtnl$cachedPlayerTarget = gtnl$findNearestTargetPlayer();
            gtnl$lastPlayerTargetUpdateTick = currentTick;
        }

        if (gtnl$cachedBlockTarget == null) return gtnl$cachedPlayerTarget;
        if (gtnl$cachedPlayerTarget == null) return gtnl$cachedBlockTarget;

        return gtnl$cachedBlockTarget.distance < gtnl$cachedPlayerTarget.distance ? gtnl$cachedBlockTarget
            : gtnl$cachedPlayerTarget;
    }

    @Unique
    private Utils.TargetInfo gtnl$findNearestTargetBlock() {
        double minDistance = Double.MAX_VALUE;
        Utils.TargetInfo closestTarget = null;

        int radius = MainConfig.super_creeper.blockFindRadius;

        for (int x = MathHelper.floor_double(this.swellingCreeper.posX) - radius; x
            <= MathHelper.floor_double(this.swellingCreeper.posX) + radius; ++x) {
            for (int y = MathHelper.floor_double(this.swellingCreeper.posY) - radius; y
                <= MathHelper.floor_double(this.swellingCreeper.posY) + radius; ++y) {
                for (int z = MathHelper.floor_double(this.swellingCreeper.posZ) - radius; z
                    <= MathHelper.floor_double(this.swellingCreeper.posZ) + radius; ++z) {
                    Block block = this.swellingCreeper.worldObj.getBlock(x, y, z);
                    int meta = this.swellingCreeper.worldObj.getBlockMetadata(x, y, z);
                    if (Utils.isTargetBlock(block, meta)) {
                        double dist = this.swellingCreeper.getDistance(x + 0.5, y + 0.5, z + 0.5);
                        if (dist < minDistance) {
                            minDistance = dist;
                            closestTarget = new Utils.TargetInfo(x, y, z, dist);
                        }
                    }
                }
            }
        }

        return closestTarget;
    }

    @Unique
    private Utils.TargetInfo gtnl$findNearestTargetPlayer() {
        double minDistance = Double.MAX_VALUE;
        Utils.TargetInfo closestTarget = null;

        int radius = MainConfig.super_creeper.playerFindRadius;

        List<EntityPlayer> players = this.swellingCreeper.worldObj
            .getEntitiesWithinAABB(EntityPlayer.class, this.swellingCreeper.boundingBox.expand(radius, radius, radius));

        for (EntityPlayer player : players) {
            if (!player.isEntityAlive() || player.capabilities.isCreativeMode) continue;

            double dist = this.swellingCreeper.getDistanceToEntity(player);
            if (dist < minDistance) {
                minDistance = dist;
                closestTarget = new Utils.TargetInfo(player, dist);
            }
        }

        return closestTarget;
    }

    @Unique
    public boolean gtnl$executeSpider() {
        long currentTick = this.swellingCreeper.worldObj.getTotalWorldTime();

        if (currentTick - gtnl$lastSpiderTargetUpdateTick >= MainConfig.super_creeper.spiderTargetInterval) {
            gtnl$lastSpiderTargetUpdateTick = currentTick;

            if (this.swellingCreeper.ridingEntity != null || this.swellingCreeper.isRiding()) {
                return false;
            }

            List<EntitySpider> nearbySpiders = this.swellingCreeper.worldObj.getEntitiesWithinAABB(
                EntitySpider.class,
                this.swellingCreeper.boundingBox.expand(
                    MainConfig.super_creeper.spiderFindRadius,
                    MainConfig.super_creeper.spiderFindRadius,
                    MainConfig.super_creeper.spiderFindRadius));

            EntitySpider closestSpider = null;
            double closestDistance = MainConfig.super_creeper.spiderFindRadius;

            for (EntitySpider spider : nearbySpiders) {
                if (spider.isEntityAlive() && spider.riddenByEntity == null) {
                    double dist = this.swellingCreeper.getDistanceToEntity(spider);
                    if (dist < closestDistance) {
                        closestDistance = dist;
                        closestSpider = spider;
                    }
                }
            }

            if (closestSpider != null) {
                this.gtnl$targetSpider = closestSpider;
                return true;
            }
        }

        return false;
    }

    @Inject(method = "shouldExecute", at = @At("HEAD"), cancellable = true)
    public void shouldExecute(CallbackInfoReturnable<Boolean> cir) {
        Utils.TargetInfo customTarget = gtnl$findNearestTarget();
        if (MainConfig.super_creeper.enableCreeperFindSpider) gtnl$executeSpider();
        double customTargetDistance = 0;
        double spiderDistance = 0;

        if (customTarget != null) {
            customTargetDistance = customTarget.distance;
        }
        if (this.gtnl$targetSpider != null) {
            spiderDistance = this.swellingCreeper.getDistanceToEntity(this.gtnl$targetSpider);
        }

        if (this.swellingCreeper.getCreeperState() < 0) {
            if (spiderDistance < MainConfig.super_creeper.spiderFindRadius
                || customTargetDistance < MainConfig.super_creeper.blockFindRadius) {
                cir.setReturnValue(true);
            }
        } else {
            cir.setReturnValue(false);
        }
        cir.cancel();
    }

    @Inject(method = "resetTask", at = @At("HEAD"))
    public void resetTask(CallbackInfo ci) {
        this.gtnl$targetSpider = null;
    }

    @Inject(method = "updateTask", at = @At("HEAD"), cancellable = true)
    public void updateTask(CallbackInfo ci) {
        Utils.TargetInfo customTarget = gtnl$findNearestTarget();
        double customTargetDistance = Double.MAX_VALUE;
        double spiderDistance = 0;

        if (customTarget != null) {
            customTargetDistance = customTarget.distance;
        }
        if (this.gtnl$targetSpider != null) {
            spiderDistance = this.swellingCreeper.getDistanceToEntity(this.gtnl$targetSpider);
        }

        if (MainConfig.super_creeper.enableCreeperFindSpider && this.gtnl$targetSpider != null
            && spiderDistance < customTargetDistance
            && this.swellingCreeper.ridingEntity == null
            && !this.swellingCreeper.isRiding()) {
            if (spiderDistance < 2.0D) {
                this.swellingCreeper.mountEntity(this.gtnl$targetSpider);
            }
            this.swellingCreeper.getNavigator()
                .tryMoveToEntityLiving(this.gtnl$targetSpider, MainConfig.super_creeper.moveSpeed);
            return;
        } else {
            this.gtnl$targetSpider = null;
        }

        if (customTarget == null) {
            this.swellingCreeper.setCreeperState(-1);
        } else {
            double targetX = customTarget.x + 0.5;
            double targetY = customTarget.y + 0.5;
            double targetZ = customTarget.z + 0.5;

            if (customTargetDistance <= MainConfig.super_creeper.explosionTriggerRange) {
                if (this.swellingCreeper.getCreeperState() < 1) {
                    this.swellingCreeper.setCreeperState(1);
                    ((AccessorEntityCreeper) this.swellingCreeper)
                        .setExplosionRadius(MainConfig.super_creeper.explosionPower);
                }
                this.swellingCreeper.getNavigator()
                    .clearPathEntity();
            } else {
                if (swellingCreeper.ridingEntity != null
                    && swellingCreeper.ridingEntity instanceof EntityLiving entityLiving) {
                    entityLiving.getNavigator()
                        .tryMoveToXYZ(
                            targetX,
                            targetY,
                            targetZ,
                            Math.max(
                                2,
                                MainConfig.super_creeper.moveSpeed
                                    + (MainConfig.super_creeper.creeperSpeedBonusScale / customTarget.distance)));
                } else {
                    this.swellingCreeper.getNavigator()
                        .tryMoveToXYZ(
                            targetX,
                            targetY,
                            targetZ,
                            Math.max(
                                2,
                                MainConfig.super_creeper.moveSpeed
                                    + (MainConfig.super_creeper.creeperSpeedBonusScale / customTarget.distance)));
                }

                boolean canSeeTarget = true;
                if (customTarget.isEntityTarget() && customTarget.entityTarget != null) {
                    canSeeTarget = this.swellingCreeper.getEntitySenses()
                        .canSee(customTarget.entityTarget);
                }

                if (!canSeeTarget) {
                    this.swellingCreeper.setCreeperState(-1);
                }
            }
        }
        ci.cancel();
    }
}
