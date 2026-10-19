package com.science.gtnl.client.railgun;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.client.fx.FxCamera;
import com.science.gtnl.common.equipment.EquipmentVec;

public class RailgunMuzzleAnchor {

    /**
     * The muzzle is part of the model, so keep the anchor in model space and
     * transform it with the exact matrix used to draw the held item. The
     * bevel at the front of the barrel ends at this point in the OBJ model.
     */
    private static final EquipmentVec MODEL_MUZZLE = new EquipmentVec(0.0, 0.286612, -1.213388);
    private static final long STALE_MILLIS = 100;
    private static final double FIRST_PERSON_FORWARD = 0.9;
    private static final double FIRST_PERSON_RIGHT = 0.32;
    private static final double FIRST_PERSON_UP = -0.22;
    private static final double THIRD_PERSON_DROP = 0.45;
    private static final double THIRD_PERSON_FORWARD = 0.75;
    private static final double THIRD_PERSON_RIGHT = 0.3;
    private static final double THIRD_PERSON_UP = -0.12;

    private record Anchor(EquipmentVec eyeSpace, long stamp) {}

    private static final Map<Integer, Anchor> ANCHORS = new HashMap<>();
    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);

    private RailgunMuzzleAnchor() {}

    public static void capture(int entityId) {
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
        EquipmentVec eyeSpace = transform(MODEL_MUZZLE);
        ANCHORS.put(entityId, new Anchor(eyeSpace, Minecraft.getSystemTime()));
    }

    public static EquipmentVec muzzle(int shooterId, float partialTicks) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity entity = minecraft.theWorld == null ? null : minecraft.theWorld.getEntityByID(shooterId);
        if (entity == null) return null;
        if (entity == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0)
            return firstPersonMuzzle(minecraft.thePlayer, partialTicks);
        EquipmentVec analytic = thirdPersonMuzzle(entity, partialTicks);
        EquipmentVec anchored = anchored(shooterId);
        return anchored == null ? analytic : anchored;
    }

    public static EquipmentVec muzzle(int shooterId, EquipmentVec fallback, float partialTicks) {
        EquipmentVec muzzle = muzzle(shooterId, partialTicks);
        return muzzle == null ? fallback : muzzle;
    }

    public static EquipmentVec analyticMuzzle(int shooterId, EquipmentVec fallback, float partialTicks) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity entity = minecraft.theWorld == null ? null : minecraft.theWorld.getEntityByID(shooterId);
        if (entity == null) return fallback;
        if (entity == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0)
            return firstPersonMuzzle(minecraft.thePlayer, partialTicks);
        return thirdPersonMuzzle(entity, partialTicks);
    }

    public static EquipmentVec firstPersonMuzzle(EntityPlayer player, float partialTicks) {
        if (player == null) return null;
        EquipmentVec look = EquipmentVec.look(player, partialTicks);
        EquipmentVec right = EquipmentVec.rightOf(look);
        EquipmentVec up = right.cross(look)
            .normalize();
        return EquipmentVec.interpolated(player, partialTicks)
            .add(look.scale(FIRST_PERSON_FORWARD))
            .add(right.scale(FIRST_PERSON_RIGHT))
            .add(up.scale(FIRST_PERSON_UP));
    }

    public static EquipmentVec thirdPersonMuzzle(Entity entity, float partialTicks) {
        EquipmentVec look = EquipmentVec.look(entity, partialTicks);
        EquipmentVec right = EquipmentVec.rightOf(look);
        EquipmentVec up = right.cross(look)
            .normalize();
        return EquipmentVec.interpolated(entity, partialTicks)
            .add(0, entity.getEyeHeight() - THIRD_PERSON_DROP, 0)
            .add(right.scale(THIRD_PERSON_RIGHT))
            .add(up.scale(THIRD_PERSON_UP))
            .add(look.scale(THIRD_PERSON_FORWARD));
    }

    private static EquipmentVec anchored(int entityId) {
        Anchor anchor = ANCHORS.get(entityId);
        if (anchor == null || Minecraft.getSystemTime() - anchor.stamp() > STALE_MILLIS) return null;
        return FxCamera.toWorld(anchor.eyeSpace());
    }

    private static EquipmentVec transform(EquipmentVec point) {
        double x = point.x(), y = point.y(), z = point.z();
        return new EquipmentVec(
            MATRIX.get(0) * x + MATRIX.get(4) * y + MATRIX.get(8) * z + MATRIX.get(12),
            MATRIX.get(1) * x + MATRIX.get(5) * y + MATRIX.get(9) * z + MATRIX.get(13),
            MATRIX.get(2) * x + MATRIX.get(6) * y + MATRIX.get(10) * z + MATRIX.get(14));
    }

    public static void clear() {
        ANCHORS.clear();
    }
}
