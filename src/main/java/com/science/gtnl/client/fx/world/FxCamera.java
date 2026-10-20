package com.science.gtnl.client.fx.world;

import java.nio.FloatBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.equipment.EquipmentVec;

public class FxCamera {

    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);
    private static final double MAX_TRUSTED_DISTANCE = 64;
    private static final double[] ROTATION = new double[9];
    private static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer(16);

    private static EquipmentVec position = EquipmentVec.ZERO;
    private static float focalX = 1F, focalY = 1F;

    private FxCamera() {}

    public static EquipmentVec position() {
        return position;
    }

    public static EquipmentVec eye() {
        Minecraft minecraft = Minecraft.getMinecraft();
        return eye(minecraft.renderViewEntity, minecraft.timer.renderPartialTicks);
    }

    public static EquipmentVec eye(Entity entity, float partialTicks) {
        return entity == null ? EquipmentVec.ZERO : EquipmentVec.eye(entity, partialTicks);
    }

    public static void capture() {
        PROJECTION.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
        if (PROJECTION.get(0) > 0 && PROJECTION.get(5) > 0) {
            focalX = PROJECTION.get(0);
            focalY = PROJECTION.get(5);
        }
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
        for (int row = 0; row < 3; row++)
            for (int column = 0; column < 3; column++) ROTATION[row * 3 + column] = MATRIX.get(column * 4 + row);
        EquipmentVec fallback = eye();
        EquipmentVec local = rotate(new EquipmentVec(MATRIX.get(12), MATRIX.get(13), MATRIX.get(14))).scale(-1);
        EquipmentVec renderOrigin = new EquipmentVec(
            RenderManager.renderPosX,
            RenderManager.renderPosY,
            RenderManager.renderPosZ);
        EquipmentVec derived = renderOrigin.add(local);
        position = derived.finite() && derived.distanceToSqr(fallback) < MAX_TRUSTED_DISTANCE * MAX_TRUSTED_DISTANCE
            ? derived
            : fallback;
    }

    public static float focalX() {
        return focalX;
    }

    public static float focalY() {
        return focalY;
    }

    public static EquipmentVec toWorld(EquipmentVec eyeSpace) {
        return position.add(rotate(eyeSpace));
    }

    public static EquipmentVec direction(EquipmentVec eyeSpace) {
        return rotate(eyeSpace);
    }

    public static EquipmentVec forward() {
        return rotate(new EquipmentVec(0, 0, -1));
    }

    private static EquipmentVec rotate(EquipmentVec eyeSpace) {
        double x = eyeSpace.x(), y = eyeSpace.y(), z = eyeSpace.z();
        return new EquipmentVec(
            ROTATION[0] * x + ROTATION[3] * y + ROTATION[6] * z,
            ROTATION[1] * x + ROTATION[4] * y + ROTATION[7] * z,
            ROTATION[2] * x + ROTATION[5] * y + ROTATION[8] * z);
    }
}
