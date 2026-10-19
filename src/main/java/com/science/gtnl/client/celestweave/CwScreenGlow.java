package com.science.gtnl.client.celestweave;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import com.science.gtnl.client.fx.FxColor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CwScreenGlow {

    private static final int HIT_LIFETIME = 450, SAVE_LIFETIME = 900;
    private static final float HIT_ALPHA = 0.32F, SAVE_ALPHA = 0.55F, BASE = 0.35F;
    private static long born = CwClientState.NEVER;
    private static int lifetime = HIT_LIFETIME;
    private static int rgb;
    private static boolean save, directional;
    private static double directionX, directionY, directionZ;

    static void hit(float x, float y, float z, int glowColor, boolean saved, boolean omni) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;
        double dx = x - player.posX, dy = y - (player.boundingBox.minY + player.height * 0.5), dz = z - player.posZ;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        directional = !omni && length > 0.4;
        if (directional) {
            directionX = dx / length;
            directionY = dy / length;
            directionZ = dz / length;
        }
        born = Minecraft.getSystemTime();
        lifetime = saved ? SAVE_LIFETIME : HIT_LIFETIME;
        rgb = FxColor.mixWhite(glowColor, 0.15F);
        save = saved;
    }

    static void clear() {
        born = CwClientState.NEVER;
    }

    @SubscribeEvent
    public void overlay(RenderGameOverlayEvent.Pre event) {
        if (event.type != RenderGameOverlayEvent.ElementType.HELMET) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft.thePlayer;
        if (player == null || minecraft.renderViewEntity != player || minecraft.gameSettings.thirdPersonView != 0)
            return;
        long age = Minecraft.getSystemTime() - born;
        if (age < 0 || age >= lifetime) return;
        float progress = age / (float) lifetime;
        float alpha = Math.min(1F, age / 50F) * (1 - progress) * (1 - progress) * (save ? SAVE_ALPHA : HIT_ALPHA);
        float top = 1F, bottom = 1F, left = 1F, right = 1F;
        if (directional) {
            double yaw = Math.toRadians(player.rotationYaw);
            double lateral = directionX * -Math.cos(yaw) + directionZ * -Math.sin(yaw);
            top = weight(directionY);
            bottom = weight(-directionY);
            left = weight(-lateral);
            right = weight(lateral);
        }
        int width = event.resolution.getScaledWidth(), height = event.resolution.getScaledHeight();
        float thickness = Math.min(width, height) * 0.22F;
        float red = FxColor.red(rgb), green = FxColor.green(rgb), blue = FxColor.blue(rgb);
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_LIGHTING_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT);
        try {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            edge(
                tessellator,
                0,
                0,
                width,
                0,
                width - thickness,
                thickness,
                thickness,
                thickness,
                red,
                green,
                blue,
                alpha * top);
            edge(
                tessellator,
                width,
                height,
                0,
                height,
                thickness,
                height - thickness,
                width - thickness,
                height - thickness,
                red,
                green,
                blue,
                alpha * bottom);
            edge(
                tessellator,
                0,
                height,
                0,
                0,
                thickness,
                thickness,
                thickness,
                height - thickness,
                red,
                green,
                blue,
                alpha * left);
            edge(
                tessellator,
                width,
                0,
                width,
                height,
                width - thickness,
                height - thickness,
                width - thickness,
                thickness,
                red,
                green,
                blue,
                alpha * right);
            tessellator.draw();
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static float weight(double amount) {
        return Math.min(1F, BASE + (1 - BASE) * 1.3F * (float) Math.max(0, amount));
    }

    private static void edge(Tessellator tessellator, float outerX0, float outerY0, float outerX1, float outerY1,
        float innerX1, float innerY1, float innerX0, float innerY0, float red, float green, float blue, float alpha) {
        tessellator.setColorRGBA_F(red, green, blue, alpha);
        tessellator.addVertex(outerX0, outerY0, 0);
        tessellator.addVertex(outerX1, outerY1, 0);
        tessellator.setColorRGBA_F(red, green, blue, 0F);
        tessellator.addVertex(innerX1, innerY1, 0);
        tessellator.addVertex(innerX0, innerY0, 0);
    }
}
