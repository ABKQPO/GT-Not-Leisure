package com.gtnllib.client.fx.world;

import java.nio.FloatBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.gtnllib.utils.Vec3d;

public class FxCamera {

    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);
    private static final double MAX_TRUSTED_DISTANCE = 64;
    private static final double[] ROTATION = new double[9];
    private static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer(16);

    private static Vec3d position = Vec3d.ZERO;
    private static float focalX = 1F, focalY = 1F;

    public static Vec3d position() {
        return position;
    }

    public static Vec3d eye() {
        Minecraft minecraft = Minecraft.getMinecraft();
        return eye(minecraft.renderViewEntity, minecraft.timer.renderPartialTicks);
    }

    public static Vec3d eye(Entity entity, float partialTicks) {
        return entity == null ? Vec3d.ZERO : Vec3d.eye(entity, partialTicks);
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
        Vec3d fallback = eye();
        Vec3d local = rotate(new Vec3d(MATRIX.get(12), MATRIX.get(13), MATRIX.get(14))).scale(-1);
        Vec3d renderOrigin = new Vec3d(RenderManager.renderPosX, RenderManager.renderPosY, RenderManager.renderPosZ);
        Vec3d derived = renderOrigin.add(local);
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

    public static Vec3d toWorld(Vec3d eyeSpace) {
        return position.add(rotate(eyeSpace));
    }

    public static Vec3d direction(Vec3d eyeSpace) {
        return rotate(eyeSpace);
    }

    public static Vec3d forward() {
        return rotate(new Vec3d(0, 0, -1));
    }

    public static boolean inView(Vec3d center, double radius) {
        Vec3d offset = center.subtract(position);
        double x = offset.x(), y = offset.y(), z = offset.z();
        return inFrustum(
            new Vec3d(
                ROTATION[0] * x + ROTATION[1] * y + ROTATION[2] * z,
                ROTATION[3] * x + ROTATION[4] * y + ROTATION[5] * z,
                ROTATION[6] * x + ROTATION[7] * y + ROTATION[8] * z),
            radius,
            focalX,
            focalY);
    }

    public static boolean inFrustum(Vec3d eyeSpace, double radius, double focalX, double focalY) {
        double x = eyeSpace.x(), y = eyeSpace.y(), z = eyeSpace.z();
        if (z > radius) return false;
        return (focalX * Math.abs(x) + z) / Math.sqrt(focalX * focalX + 1) <= radius
            && (focalY * Math.abs(y) + z) / Math.sqrt(focalY * focalY + 1) <= radius;
    }

    private static Vec3d rotate(Vec3d eyeSpace) {
        double x = eyeSpace.x(), y = eyeSpace.y(), z = eyeSpace.z();
        return new Vec3d(
            ROTATION[0] * x + ROTATION[3] * y + ROTATION[6] * z,
            ROTATION[1] * x + ROTATION[4] * y + ROTATION[7] * z,
            ROTATION[2] * x + ROTATION[5] * y + ROTATION[8] * z);
    }
}
