package com.science.gtnl.client.fx.world;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import com.science.gtnl.common.equipment.EquipmentVec;

public class FxVisibility {

    private FxVisibility() {}

    public static boolean allowed(boolean self, double distance, boolean showOthers, double maxDistance) {
        return self || showOthers && distance <= maxDistance;
    }

    public static boolean isViewer(Entity entity) {
        return entity != null && entity == Minecraft.getMinecraft().thePlayer;
    }

    public static boolean isViewer(int entityId) {
        Entity player = Minecraft.getMinecraft().thePlayer;
        return player != null && player.getEntityId() == entityId;
    }

    public static double distanceToViewer(Entity entity) {
        Entity viewer = Minecraft.getMinecraft().renderViewEntity;
        return viewer == null || viewer == entity ? 0 : viewer.getDistanceToEntity(entity);
    }

    public static double distanceToViewer(EquipmentVec point) {
        Entity viewer = Minecraft.getMinecraft().renderViewEntity;
        if (viewer == null || point == null) return 0;
        return Math.sqrt(viewer.getDistanceSq(point.x(), point.y(), point.z()));
    }
}
