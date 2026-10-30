package com.gtnllib.client.fx.world;

import java.nio.FloatBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.gtnllib.utils.Vec3d;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Where the muzzle of a held gun is in the world, so shots and muzzle effects start at the barrel the player sees.
 * Item renderers record the muzzle while they draw the gun: in first person from the hand view, which is drawn with
 * its own field of view, and in third person from the holder's arm. When nothing was recorded lately it falls back to
 * where a gun held in the right hand usually is.
 */
@SideOnly(Side.CLIENT)
public class FxMuzzleAnchor {

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

    private record Anchor(Vec3d eyeSpace, long stamp) {}

    private record HandAnchor(Vec3d eyeSpace, long stamp, float focalX, float focalY) {}

    private static final Int2ObjectOpenHashMap<Anchor> ANCHORS = new Int2ObjectOpenHashMap<>();
    private static final FloatBuffer MATRIX = BufferUtils.createFloatBuffer(16);
    private static HandAnchor hand;

    /**
     * Records the muzzle of a gun drawn in a holder's hand; {@code muzzle} is in the model's own coordinates and the
     * current model view matrix must be the one the gun is drawn with.
     */
    public static void capture(int entityId, Vec3d muzzle) {
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
        ANCHORS.put(entityId, new Anchor(transform(muzzle), Minecraft.getSystemTime()));
    }

    /**
     * Records the muzzle of the gun drawn in the first person hand view, with the matrices it is drawn with.
     */
    public static void captureFirstPerson(Vec3d muzzle) {
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MATRIX);
        Vec3d eyeSpace = transform(muzzle);
        MATRIX.clear();
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, MATRIX);
        if (MATRIX.get(0) <= 0 || MATRIX.get(5) <= 0) return;
        hand = new HandAnchor(eyeSpace, Minecraft.getSystemTime(), MATRIX.get(0), MATRIX.get(5));
    }

    /**
     * The world position of a shooter's muzzle, or null when the shooter is not loaded.
     */
    public static Vec3d muzzle(int shooterId, float partialTicks) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity entity = minecraft.theWorld == null ? null : minecraft.theWorld.getEntityByID(shooterId);
        if (entity == null) return null;
        if (entity == minecraft.thePlayer && minecraft.gameSettings.thirdPersonView == 0) {
            Vec3d fromHand = handMuzzle();
            return fromHand != null ? fromHand : firstPersonMuzzle(minecraft.thePlayer, partialTicks);
        }
        Vec3d analytic = thirdPersonMuzzle(entity, partialTicks);
        Vec3d anchored = anchored(shooterId);
        return anchored == null ? analytic : anchored;
    }

    public static Vec3d muzzle(int shooterId, Vec3d fallback, float partialTicks) {
        Vec3d muzzle = muzzle(shooterId, partialTicks);
        return muzzle == null ? fallback : muzzle;
    }

    public static Vec3d firstPersonMuzzle(EntityPlayer player, float partialTicks) {
        if (player == null) return null;
        Vec3d look = Vec3d.look(player, partialTicks);
        Vec3d right = Vec3d.rightOf(look);
        Vec3d up = right.cross(look)
            .normalize();
        return Vec3d.interpolated(player, partialTicks)
            .add(look.scale(FIRST_PERSON_FORWARD))
            .add(right.scale(FIRST_PERSON_RIGHT))
            .add(up.scale(FIRST_PERSON_UP));
    }

    public static Vec3d thirdPersonMuzzle(Entity entity, float partialTicks) {
        Vec3d look = Vec3d.look(entity, partialTicks);
        Vec3d right = Vec3d.rightOf(look);
        Vec3d up = right.cross(look)
            .normalize();
        return Vec3d.interpolated(entity, partialTicks)
            .add(0, entity.getEyeHeight() - THIRD_PERSON_DROP, 0)
            .add(right.scale(THIRD_PERSON_RIGHT))
            .add(up.scale(THIRD_PERSON_UP))
            .add(look.scale(THIRD_PERSON_FORWARD));
    }

    /**
     * Where the first person gun points, lagging behind the view the way the held item sways when the camera turns.
     */
    public static Vec3d firstPersonAim(float partialTicks) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return FxCamera.forward();
        float armPitch = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * partialTicks;
        float armYaw = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * partialTicks;
        double pitch = Math.toRadians((player.rotationPitch - armPitch) * ARM_LAG);
        double yaw = Math.toRadians((player.rotationYaw - armYaw) * ARM_LAG);
        return FxCamera
            .direction(new Vec3d(-Math.sin(yaw), Math.sin(pitch) * Math.cos(yaw), -Math.cos(pitch) * Math.cos(yaw)));
    }

    private static Vec3d handMuzzle() {
        if (hand == null || Minecraft.getSystemTime() - hand.stamp() > STALE_MILLIS) return null;
        Vec3d eye = hand.eyeSpace();
        Vec3d ray = new Vec3d(
            eye.x() * hand.focalX() / FxCamera.focalX(),
            eye.y() * hand.focalY() / FxCamera.focalY(),
            eye.z());
        if (ray.length() < 1.0E-6 || ray.z() >= 0) return null;
        return FxCamera.toWorld(
            ray.normalize()
                .scale(FIRST_PERSON_DEPTH));
    }

    private static Vec3d anchored(int entityId) {
        Anchor anchor = ANCHORS.get(entityId);
        if (anchor == null || Minecraft.getSystemTime() - anchor.stamp() > STALE_MILLIS) return null;
        return FxCamera.toWorld(anchor.eyeSpace());
    }

    private static Vec3d transform(Vec3d point) {
        double x = point.x(), y = point.y(), z = point.z();
        return new Vec3d(
            MATRIX.get(0) * x + MATRIX.get(4) * y + MATRIX.get(8) * z + MATRIX.get(12),
            MATRIX.get(1) * x + MATRIX.get(5) * y + MATRIX.get(9) * z + MATRIX.get(13),
            MATRIX.get(2) * x + MATRIX.get(6) * y + MATRIX.get(10) * z + MATRIX.get(14));
    }

    public static void clear() {
        ANCHORS.clear();
        hand = null;
    }
}
