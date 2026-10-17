package com.science.gtnl.client.railgun;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.railgun.RailgunVec;

public class RailgunMuzzleAnchor {

    private static final double BARREL_FORWARD = 0.16;
    private static final long STALE_MILLIS = 250;

    private record Anchor(RailgunVec eyeSpace, long stamp) {}

    private static final Map<Integer, Anchor> ANCHORS = new HashMap<>();
    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);

    private RailgunMuzzleAnchor() {}

    public static void capture(int entityId) {
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
        ANCHORS.put(
            entityId,
            new Anchor(new RailgunVec(MATRIX.get(12), MATRIX.get(13), MATRIX.get(14)), Minecraft.getSystemTime()));
    }

    public static RailgunVec muzzle(int entityId, RailgunVec look) {
        Anchor anchor = ANCHORS.get(entityId);
        if (anchor == null || Minecraft.getSystemTime() - anchor.stamp() > STALE_MILLIS) return null;
        RailgunCamera.capture();
        return RailgunCamera.toWorld(anchor.eyeSpace())
            .add(look.scale(BARREL_FORWARD));
    }

    public static void clear() {
        ANCHORS.clear();
    }
}
