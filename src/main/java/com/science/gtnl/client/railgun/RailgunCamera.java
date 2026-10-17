package com.science.gtnl.client.railgun;

import java.nio.FloatBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.railgun.RailgunVec;

public class RailgunCamera {

    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);
    private static final double MAX_TRUSTED_DISTANCE = 64;
    private static final double[] ROTATION = new double[9];

    private static RailgunVec position = RailgunVec.ZERO;

    private RailgunCamera() {}

    public static RailgunVec position() {
        return position;
    }

    public static RailgunVec eye() {
        Minecraft minecraft = Minecraft.getMinecraft();
        return eye(minecraft.renderViewEntity, minecraft.timer.renderPartialTicks);
    }

    public static RailgunVec eye(Entity entity, float partialTicks) {
        return entity == null ? RailgunVec.ZERO : RailgunVec.eye(entity, partialTicks);
    }

    public static void capture() {
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
        for (int row = 0; row < 3; row++)
            for (int column = 0; column < 3; column++) ROTATION[row * 3 + column] = MATRIX.get(column * 4 + row);
        RailgunVec fallback = eye();
        RailgunVec local = rotate(new RailgunVec(MATRIX.get(12), MATRIX.get(13), MATRIX.get(14))).scale(-1);
        RailgunVec renderOrigin = new RailgunVec(
            RenderManager.renderPosX,
            RenderManager.renderPosY,
            RenderManager.renderPosZ);
        RailgunVec derived = renderOrigin.add(local);
        position = derived.finite() && derived.distanceToSqr(fallback) < MAX_TRUSTED_DISTANCE * MAX_TRUSTED_DISTANCE
            ? derived
            : fallback;
    }

    public static RailgunVec toWorld(RailgunVec eyeSpace) {
        return position.add(rotate(eyeSpace));
    }

    public static RailgunVec forward() {
        return rotate(new RailgunVec(0, 0, -1));
    }

    private static RailgunVec rotate(RailgunVec eyeSpace) {
        double x = eyeSpace.x(), y = eyeSpace.y(), z = eyeSpace.z();
        return new RailgunVec(
            ROTATION[0] * x + ROTATION[3] * y + ROTATION[6] * z,
            ROTATION[1] * x + ROTATION[4] * y + ROTATION[7] * z,
            ROTATION[2] * x + ROTATION[5] * y + ROTATION[8] * z);
    }
}
