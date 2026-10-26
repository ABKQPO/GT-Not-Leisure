package com.science.gtnl.common.supercreeper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;
import com.science.gtnl.mixins.early.superCreeper.AccessorEntityCreeper;

public class SuperCreeperHooks {

    private SuperCreeperHooks() {}

    public static boolean isTargetBlock(Block block, int meta) {
        if (MainConfig.super_creeper.defaultTargetBlocks == null
            || MainConfig.super_creeper.defaultTargetBlocks.length == 0) return false;
        if (block == null) {
            return false;
        }
        String blockId = Block.blockRegistry.getNameForObject(block);
        if (blockId == null) {
            return false;
        }

        for (String spec : MainConfig.super_creeper.defaultTargetBlocks) {
            String[] parts = spec.split(":");
            if (parts.length == 2) {
                if (spec.equals(blockId)) {
                    return true;
                }
            } else if (parts.length == 3) {
                String idPart = parts[0] + ":" + parts[1];
                int desiredMeta;
                try {
                    desiredMeta = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    ScienceNotLeisure.LOG.warn("Invalid meta value in config: {}", spec);
                    continue;
                }

                if (idPart.equals(blockId) && meta == desiredMeta) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class TargetInfo {

        public double x, y, z;
        public double distance;
        public EntityLivingBase entityTarget;

        public TargetInfo(double x, double y, double z, double distance) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.distance = distance;
            this.entityTarget = null;
        }

        public TargetInfo(EntityLivingBase entity, double distance) {
            this.entityTarget = entity;
            this.x = entity.posX;
            this.y = entity.posY;
            this.z = entity.posZ;
            this.distance = distance;
        }

        public boolean isEntityTarget() {
            return entityTarget != null;
        }
    }

    public static class State {

        public EntitySpider targetSpider;
        public long lastSpiderTargetUpdateTick;
        public TargetInfo cachedBlockTarget;
        public TargetInfo cachedPlayerTarget;
        public long lastBlockTargetUpdateTick;
        public long lastPlayerTargetUpdateTick;
    }

    public static boolean shouldExecute(EntityCreeper creeper, State state) {
        TargetInfo customTarget = findNearestTarget(creeper, state);
        if (MainConfig.super_creeper.enableCreeperFindSpider) executeSpider(creeper, state);
        double customTargetDistance = 0;
        double spiderDistance = 0;

        if (customTarget != null) {
            customTargetDistance = customTarget.distance;
        }
        if (state.targetSpider != null) {
            spiderDistance = creeper.getDistanceToEntity(state.targetSpider);
        }

        if (creeper.getCreeperState() < 0) {
            if (spiderDistance < MainConfig.super_creeper.spiderFindRadius
                || customTargetDistance < MainConfig.super_creeper.blockFindRadius) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    public static void resetTask(State state) {
        state.targetSpider = null;
    }

    // Returns whether the vanilla swell task is skipped; while walking to a spider the vanilla task still runs.
    public static boolean updateTask(EntityCreeper creeper, State state) {
        TargetInfo customTarget = findNearestTarget(creeper, state);
        double customTargetDistance = Double.MAX_VALUE;
        double spiderDistance = 0;

        if (customTarget != null) {
            customTargetDistance = customTarget.distance;
        }
        if (state.targetSpider != null) {
            spiderDistance = creeper.getDistanceToEntity(state.targetSpider);
        }

        if (MainConfig.super_creeper.enableCreeperFindSpider && state.targetSpider != null
            && spiderDistance < customTargetDistance
            && creeper.ridingEntity == null
            && !creeper.isRiding()) {
            if (spiderDistance < 2.0D) {
                creeper.mountEntity(state.targetSpider);
            }
            creeper.getNavigator()
                .tryMoveToEntityLiving(state.targetSpider, MainConfig.super_creeper.moveSpeed);
            return false;
        } else {
            state.targetSpider = null;
        }

        if (customTarget == null) {
            creeper.setCreeperState(-1);
        } else {
            double targetX = customTarget.x + 0.5;
            double targetY = customTarget.y + 0.5;
            double targetZ = customTarget.z + 0.5;

            if (customTargetDistance <= MainConfig.super_creeper.explosionTriggerRange) {
                if (creeper.getCreeperState() < 1) {
                    creeper.setCreeperState(1);
                    ((AccessorEntityCreeper) creeper).setExplosionRadius(MainConfig.super_creeper.explosionPower);
                }
                creeper.getNavigator()
                    .clearPathEntity();
            } else {
                if (creeper.ridingEntity != null && creeper.ridingEntity instanceof EntityLiving entityLiving) {
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
                    creeper.getNavigator()
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
                    canSeeTarget = creeper.getEntitySenses()
                        .canSee(customTarget.entityTarget);
                }

                if (!canSeeTarget) {
                    creeper.setCreeperState(-1);
                }
            }
        }
        return true;
    }

    public static TargetInfo findNearestTarget(EntityCreeper creeper, State state) {
        long currentTick = creeper.worldObj.getTotalWorldTime();

        if (currentTick - state.lastBlockTargetUpdateTick >= MainConfig.super_creeper.blockTargetInterval) {
            state.cachedBlockTarget = findNearestTargetBlock(creeper);
            state.lastBlockTargetUpdateTick = currentTick;
        }

        if (currentTick - state.lastPlayerTargetUpdateTick >= MainConfig.super_creeper.playerTargetInterval) {
            state.cachedPlayerTarget = findNearestTargetPlayer(creeper);
            state.lastPlayerTargetUpdateTick = currentTick;
        }

        if (state.cachedBlockTarget == null) return state.cachedPlayerTarget;
        if (state.cachedPlayerTarget == null) return state.cachedBlockTarget;

        return state.cachedBlockTarget.distance < state.cachedPlayerTarget.distance ? state.cachedBlockTarget
            : state.cachedPlayerTarget;
    }

    public static TargetInfo findNearestTargetBlock(EntityCreeper creeper) {
        double minDistance = Double.MAX_VALUE;
        TargetInfo closestTarget = null;

        int radius = MainConfig.super_creeper.blockFindRadius;

        for (int x = MathHelper.floor_double(creeper.posX) - radius; x
            <= MathHelper.floor_double(creeper.posX) + radius; ++x) {
            for (int y = MathHelper.floor_double(creeper.posY) - radius; y
                <= MathHelper.floor_double(creeper.posY) + radius; ++y) {
                for (int z = MathHelper.floor_double(creeper.posZ) - radius; z
                    <= MathHelper.floor_double(creeper.posZ) + radius; ++z) {
                    Block block = creeper.worldObj.getBlock(x, y, z);
                    int meta = creeper.worldObj.getBlockMetadata(x, y, z);
                    if (isTargetBlock(block, meta)) {
                        double dist = creeper.getDistance(x + 0.5, y + 0.5, z + 0.5);
                        if (dist < minDistance) {
                            minDistance = dist;
                            closestTarget = new TargetInfo(x, y, z, dist);
                        }
                    }
                }
            }
        }

        return closestTarget;
    }

    public static TargetInfo findNearestTargetPlayer(EntityCreeper creeper) {
        double minDistance = Double.MAX_VALUE;
        TargetInfo closestTarget = null;

        int radius = MainConfig.super_creeper.playerFindRadius;

        List<EntityPlayer> players = creeper.worldObj
            .getEntitiesWithinAABB(EntityPlayer.class, creeper.boundingBox.expand(radius, radius, radius));

        for (EntityPlayer player : players) {
            if (!player.isEntityAlive() || player.capabilities.isCreativeMode) continue;

            double dist = creeper.getDistanceToEntity(player);
            if (dist < minDistance) {
                minDistance = dist;
                closestTarget = new TargetInfo(player, dist);
            }
        }

        return closestTarget;
    }

    public static boolean executeSpider(EntityCreeper creeper, State state) {
        long currentTick = creeper.worldObj.getTotalWorldTime();

        if (currentTick - state.lastSpiderTargetUpdateTick >= MainConfig.super_creeper.spiderTargetInterval) {
            state.lastSpiderTargetUpdateTick = currentTick;

            if (creeper.ridingEntity != null || creeper.isRiding()) {
                return false;
            }

            List<EntitySpider> nearbySpiders = creeper.worldObj.getEntitiesWithinAABB(
                EntitySpider.class,
                creeper.boundingBox.expand(
                    MainConfig.super_creeper.spiderFindRadius,
                    MainConfig.super_creeper.spiderFindRadius,
                    MainConfig.super_creeper.spiderFindRadius));

            EntitySpider closestSpider = null;
            double closestDistance = MainConfig.super_creeper.spiderFindRadius;

            for (EntitySpider spider : nearbySpiders) {
                if (spider.isEntityAlive() && spider.riddenByEntity == null) {
                    double dist = creeper.getDistanceToEntity(spider);
                    if (dist < closestDistance) {
                        closestDistance = dist;
                        closestSpider = spider;
                    }
                }
            }

            if (closestSpider != null) {
                state.targetSpider = closestSpider;
                return true;
            }
        }

        return false;
    }

    public static boolean solidBlockAround(EntitySpider spider) {
        int blockX = MathHelper.floor_double(spider.posX);
        int blockY = MathHelper.floor_double(spider.boundingBox.minY);
        int blockZ = MathHelper.floor_double(spider.posZ);

        for (int xOffset = -2; xOffset <= 2; ++xOffset) {
            for (int zOffset = -2; zOffset <= 2; ++zOffset) {
                if (xOffset == 0 && zOffset == 0) continue;
                Block block = spider.worldObj.getBlock(blockX + xOffset, blockY, blockZ + zOffset);
                if (block != null && block.getMaterial()
                    .isSolid()
                    && block.getMaterial()
                        .isOpaque()) {
                    return true;
                }
            }
        }
        return false;
    }
}
