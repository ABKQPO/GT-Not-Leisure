package com.gtnllib.utils;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterUtils extends Teleporter {

    private final double targetX, targetY, targetZ;

    public TeleporterUtils(WorldServer world, double x, double y, double z) {
        super(world);
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float yaw) {
        entity.setLocationAndAngles(targetX, targetY, targetZ, entity.rotationYaw, entity.rotationPitch);
        entity.motionX = entity.motionY = entity.motionZ = 0.0D;
    }

    @Override
    public boolean placeInExistingPortal(Entity entity, double x, double y, double z, float yaw) {
        placeInPortal(entity, x, y, z, yaw);
        return true;
    }

    @Override
    public boolean makePortal(Entity entity) {
        return true;
    }
}
