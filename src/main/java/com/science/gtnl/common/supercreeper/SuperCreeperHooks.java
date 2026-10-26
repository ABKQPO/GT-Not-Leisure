package com.science.gtnl.common.supercreeper;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.config.MainConfig;

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
}
