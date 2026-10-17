package com.science.gtnl.client.railgun;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.railgun.RailgunVec;

public class RailgunMuzzleAnchor {

    /**
     * The muzzle is part of the model, so keep the anchor in model space and
     * transform it with the exact matrix used to draw the held item. The
     * bevel at the front of the barrel ends at this point in the OBJ model.
     */
    private static final RailgunVec MODEL_MUZZLE = new RailgunVec(0.0, 0.286612, -1.213388);
    private static final long STALE_MILLIS = 100;

    private record Anchor(RailgunVec eyeSpace, long stamp) {}

    private static final Map<Integer, Anchor> ANCHORS = new HashMap<>();
    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);

    private RailgunMuzzleAnchor() {}

    public static void capture(int entityId) {
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
        RailgunVec eyeSpace = transform(MODEL_MUZZLE);
        ANCHORS.put(entityId, new Anchor(eyeSpace, Minecraft.getSystemTime()));
    }

    public static RailgunVec muzzle(int entityId) {
        Anchor anchor = ANCHORS.get(entityId);
        if (anchor == null || Minecraft.getSystemTime() - anchor.stamp() > STALE_MILLIS) return null;
        return RailgunCamera.toWorld(anchor.eyeSpace());
    }

    private static RailgunVec transform(RailgunVec point) {
        double x = point.x(), y = point.y(), z = point.z();
        return new RailgunVec(
            MATRIX.get(0) * x + MATRIX.get(4) * y + MATRIX.get(8) * z + MATRIX.get(12),
            MATRIX.get(1) * x + MATRIX.get(5) * y + MATRIX.get(9) * z + MATRIX.get(13),
            MATRIX.get(2) * x + MATRIX.get(6) * y + MATRIX.get(10) * z + MATRIX.get(14));
    }

    public static void clear() {
        ANCHORS.clear();
    }
}
