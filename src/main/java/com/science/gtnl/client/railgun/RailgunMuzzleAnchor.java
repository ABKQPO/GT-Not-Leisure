package com.science.gtnl.client.railgun;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.client.fx.world.FxCamera;
import com.science.gtnl.common.equipment.EquipmentVec;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RailgunMuzzleAnchor {

    private static final long STALE_MILLIS = 100;
    private static final float ARM_LAG = 0.1F;
    private static final double FIRST_PERSON_FORWARD = 0.9;
    private static final double FIRST_PERSON_RIGHT = 0.32;
    private static final double FIRST_PERSON_UP = -0.22;
    private static final double FIRST_PERSON_DEPTH = Math.sqrt(
        FIRST_PERSON_FORWARD * FIRST_PERSON_FORWARD + FIRST_PERSON_RIGHT * FIRST_PERSON_RIGHT
            + FIRST_PERSON_UP * FIRST_PERSON_UP);
    private static final double THIRD_PERSON_DROP = 0.45;
    private static final double THIRD_PERSON_FORWARD = 0.75;
    private static final double THIRD_PERSON_RIGHT = 0.3;
    private static final double THIRD_PERSON_UP = -0.12;

    private record Anchor(EquipmentVec eyeSpace, long stamp) {}

    private record HandAnchor(EquipmentVec eyeSpace, long stamp, float focalX, float focalY) {}

    private static final Map<Integer, Anchor> ANCHORS = new HashMap<>();
    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);
    private static HandAnchor hand;

    private RailgunMuzzleAnchor() {}

    public static void capture(int entityId) {
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
        EquipmentVec eyeSpace = transform(RailgunBolts.MUZZLE);
        ANCHORS.put(entityId, new Anchor(eyeSpace, Minecraft.getSystemTime()));
    }

    public static void captureFirstPerson() {
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
        EquipmentVec eyeSpace = transform(RailgunBolts.MUZZLE);
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, MATRIX);
        if (MATRIX.get(0) <= 0 || MATRIX.get(5) <= 0) return;
        hand = new HandAnchor(eyeSpace, Minecraft.getSystemTime(), MATRIX.get(0), MATRIX.get(5));
    }

    public static EquipmentVec muzzle(int shooterId, float partialTicks) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity entity = minecraft.theWorld == null ? null : minecraft.theWorld.getEntityByID(shooterId);
        if (entity == null) return null;
        if (entity == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0) {
            EquipmentVec fromHand = handMuzzle();
            return fromHand != null ? fromHand : firstPersonMuzzle(minecraft.thePlayer, partialTicks);
        }
        EquipmentVec analytic = thirdPersonMuzzle(entity, partialTicks);
        EquipmentVec anchored = anchored(shooterId);
        return anchored == null ? analytic : anchored;
    }

    public static EquipmentVec muzzle(int shooterId, EquipmentVec fallback, float partialTicks) {
        EquipmentVec muzzle = muzzle(shooterId, partialTicks);
        return muzzle == null ? fallback : muzzle;
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

    public static EquipmentVec firstPersonAim(float partialTicks) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return FxCamera.forward();
        float armPitch = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * partialTicks;
        float armYaw = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * partialTicks;
        double pitch = Math.toRadians((player.rotationPitch - armPitch) * ARM_LAG);
        double yaw = Math.toRadians((player.rotationYaw - armYaw) * ARM_LAG);
        return FxCamera.direction(
            new EquipmentVec(-Math.sin(yaw), Math.sin(pitch) * Math.cos(yaw), -Math.cos(pitch) * Math.cos(yaw)));
    }

    private static EquipmentVec handMuzzle() {
        if (hand == null || Minecraft.getSystemTime() - hand.stamp() > STALE_MILLIS) return null;
        EquipmentVec eye = hand.eyeSpace();
        EquipmentVec ray = new EquipmentVec(
            eye.x() * hand.focalX() / FxCamera.focalX(),
            eye.y() * hand.focalY() / FxCamera.focalY(),
            eye.z());
        if (ray.length() < 1.0E-6 || ray.z() >= 0) return null;
        return FxCamera.toWorld(
            ray.normalize()
                .scale(FIRST_PERSON_DEPTH));
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
        hand = null;
    }
}
