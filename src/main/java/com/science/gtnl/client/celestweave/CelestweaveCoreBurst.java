package com.science.gtnl.client.celestweave;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.science.gtnllib.client.fx.FxColor;
import com.science.gtnllib.client.fx.FxEasing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveCoreBurst {

    private static final int CRACKS = 5, STEPS = 5, EMBERS = 8, SEGMENTS = 24;
    private static final float LIFETIME = 620F, Z = -0.06F;

    private CelestweaveCoreBurst() {}

    static void render(CelestweaveArmorModel model, CelestweaveFrames frames, CelestweaveClientState state,
        CelestweaveLook look, CelestweavePose pose, float scale) {
        CelestweaveFrames.Frame frame = frames.frame("core.shock");
        if (frame == null || frame.bone < 0) return;
        boolean strong = state.hitPower > 1F;
        float time = (Minecraft.getSystemTime() - state.hitAt) / (strong ? 1.5F : 1F);
        if (time < 0 || time >= LIFETIME) return;
        float size = strong ? 1.4F : 1F;
        int glowColor = look.glowColor;
        GL11.glPushMatrix();
        model.enterAnchor(frame.bone, frame, pose, scale);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        flash(tessellator, time, size, glowColor);
        cracks(tessellator, time, size, glowColor, state);
        hexagon(tessellator, time, size, glowColor);
        embers(tessellator, time, size, glowColor, state);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private static void flash(Tessellator tessellator, float time, float size, int glowColor) {
        float fade = 1F - time / 200F;
        if (fade <= 0F) return;
        float radius = (1.2F + 0.8F * FxEasing.easeOutCubic(time / 90F)) * size;
        int edge = FxColor.mixWhite(glowColor, 0.3F);
        for (int i = 0; i < SEGMENTS; i++) {
            double a0 = Math.PI * 2 * i / SEGMENTS, a1 = Math.PI * 2 * (i + 1) / SEGMENTS;
            FxColor.tessellate(tessellator, 0xFFFFFF, fade);
            tessellator.addVertex(0, 0, Z);
            tessellator.addVertex(0, 0, Z);
            FxColor.tessellate(tessellator, edge, 0F);
            tessellator.addVertex(Math.cos(a1) * radius, Math.sin(a1) * radius, Z);
            tessellator.addVertex(Math.cos(a0) * radius, Math.sin(a0) * radius, Z);
        }
    }

    private static double baseAngle(CelestweaveClientState state, int index, Random random) {
        if (!state.hitDirectional) return Math.PI * 2 * index / CRACKS + random.nextFloat() * 0.6;
        return Math.atan2(state.hitDirectionY, state.hitDirectionX) + (index - CRACKS / 2) * 0.38;
    }

    private static void cracks(Tessellator tessellator, float time, float size, int glowColor,
        CelestweaveClientState state) {
        float cool = Math.min(1F, time / 520F);
        float alpha = 1F - cool * cool;
        if (alpha <= 0F) return;
        int rgb = time < 120F ? 0xFFFFFF : FxColor.mixWhite(glowColor, Math.max(0F, 0.6F - cool));
        float grow = FxEasing.easeOutCubic(time / 110F);
        Random random = new Random(state.hitAt);
        for (int i = 0; i < CRACKS; i++) {
            double angle = baseAngle(state, i, random);
            float length = (state.hitDirectional ? 3.2F + (i == CRACKS / 2 ? 1.6F : 0F) : 3.3F) * size;
            branch(
                tessellator,
                Math.cos(angle) * 1.2,
                Math.sin(angle) * 1.2,
                angle,
                length * grow,
                0.22F,
                true,
                random,
                rgb,
                alpha);
        }
    }

    private static void branch(Tessellator tessellator, double x, double y, double angle, float length, float width,
        boolean main, Random random, int rgb, float alpha) {
        double step = length / STEPS;
        for (int k = 1; k <= STEPS; k++) {
            angle += (random.nextFloat() - 0.5F) * 0.9F;
            double nx = x + Math.cos(angle) * step, ny = y + Math.sin(angle) * step;
            float taper = 1F - (k - 1F) / STEPS * 0.6F;
            segment(tessellator, x, y, nx, ny, width * 2.6F * taper, rgb, alpha * 0.3F);
            segment(tessellator, x, y, nx, ny, width * taper, rgb, alpha);
            x = nx;
            y = ny;
            if (main && k == 2 && random.nextFloat() < 0.8F) branch(
                tessellator,
                x,
                y,
                angle + (random.nextBoolean() ? -0.7 : 0.7),
                length * 0.45F,
                width * 0.55F,
                false,
                random,
                rgb,
                alpha * 0.8F);
        }
    }

    private static void segment(Tessellator tessellator, double x0, double y0, double x1, double y1, float width,
        int rgb, float alpha) {
        double dx = x1 - x0, dy = y1 - y0, length = Math.sqrt(dx * dx + dy * dy);
        if (length < 1.0E-4) return;
        double px = -dy / length * width * 0.5, py = dx / length * width * 0.5;
        FxColor.tessellate(tessellator, rgb, alpha);
        tessellator.addVertex(x0 - px, y0 - py, Z);
        tessellator.addVertex(x0 + px, y0 + py, Z);
        tessellator.addVertex(x1 + px, y1 + py, Z);
        tessellator.addVertex(x1 - px, y1 - py, Z);
    }

    private static void hexagon(Tessellator tessellator, float time, float size, int glowColor) {
        float progress = time / 260F;
        if (progress >= 1F) return;
        float radius = (1.5F + 2F * FxEasing.easeOutCubic(progress)) * size;
        float alpha = (float) Math.pow(1F - progress, 1.5);
        int rgb = FxColor.mixWhite(glowColor, 0.6F * (1F - progress));
        for (int k = 0; k < 6; k++) {
            double a0 = k * Math.PI / 3 + Math.PI / 6, a1 = a0 + Math.PI / 3;
            segment(
                tessellator,
                Math.cos(a0) * radius,
                Math.sin(a0) * radius,
                Math.cos(a1) * radius,
                Math.sin(a1) * radius,
                0.5F,
                rgb,
                alpha * 0.35F);
            segment(
                tessellator,
                Math.cos(a0) * radius,
                Math.sin(a0) * radius,
                Math.cos(a1) * radius,
                Math.sin(a1) * radius,
                0.18F,
                rgb,
                alpha);
        }
    }

    private static void embers(Tessellator tessellator, float time, float size, int glowColor,
        CelestweaveClientState state) {
        float progress = time / 500F;
        if (progress >= 1F) return;
        Random random = new Random(state.hitAt ^ 0x5DEECE66DL);
        double bias = state.hitDirectional ? Math.atan2(state.hitDirectionY, state.hitDirectionX) : 0;
        int rgb = FxColor.mixWhite(glowColor, 0.5F);
        float radius = 0.18F * (1F - progress) + 0.06F;
        for (int i = 0; i < EMBERS; i++) {
            double angle = state.hitDirectional ? bias + (i - (EMBERS - 1) / 2.0) * 0.3 + random.nextFloat() * 0.2
                : Math.PI * 2 * i / EMBERS + random.nextFloat() * 0.4;
            double distance = (1.4 + 3.4 * (1 - Math.exp(-progress * 4)) * (0.7 + random.nextFloat() * 0.5)) * size;
            double x = Math.cos(angle) * distance, y = Math.sin(angle) * distance;
            FxColor.tessellate(tessellator, rgb, 1F - progress);
            tessellator.addVertex(x, y - radius, Z);
            tessellator.addVertex(x + radius, y, Z);
            tessellator.addVertex(x, y + radius, Z);
            tessellator.addVertex(x - radius, y, Z);
        }
    }
}
