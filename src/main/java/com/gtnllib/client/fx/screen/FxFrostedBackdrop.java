package com.gtnllib.client.fx.screen;

import java.nio.FloatBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.Framebuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.gtnllib.GTNLLib;
import com.gtnllib.client.shader.ShaderEffect;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FxFrostedBackdrop implements AutoCloseable {

    private static final float SAMPLE_STEP = 2.0F;
    private static final int DOWNSAMPLE = 2;
    private static final int REGION_MARGIN = 40;
    private static final FloatBuffer MODELVIEW = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer PROJECTION = BufferUtils.createFloatBuffer(16);

    private Framebuffer first;
    private Framebuffer second;
    private boolean unavailable;
    private int texture = -1;

    public boolean capture(float x, float y, float radius) {
        texture = -1;
        if (unavailable || !OpenGlHelper.isFramebufferEnabled()) return false;
        Framebuffer main = Minecraft.getMinecraft()
            .getFramebuffer();
        if (main == null || main.framebufferObject < 0) return false;
        int[] region = windowRegion(x, y, radius, main.framebufferWidth, main.framebufferHeight);
        if (region == null) return false;
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT | GL11.GL_ENABLE_BIT);
        try {
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            ensureSize(
                (main.framebufferWidth + DOWNSAMPLE - 1) / DOWNSAMPLE,
                (main.framebufferHeight + DOWNSAMPLE - 1) / DOWNSAMPLE);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            int x0 = region[0] / DOWNSAMPLE, y0 = region[1] / DOWNSAMPLE;
            GL11.glScissor(x0, y0, region[2] / DOWNSAMPLE - x0 + 1, region[3] / DOWNSAMPLE - y0 + 1);
            float stepX = SAMPLE_STEP / main.framebufferWidth, stepY = SAMPLE_STEP / main.framebufferHeight;
            if (!pass(second, main.framebufferTexture, stepX, 0)) {
                unavailable = true;
                close();
                return false;
            }
            pass(first, second.framebufferTexture, 0, stepY);
            pass(second, first.framebufferTexture, stepX, 0);
            pass(first, second.framebufferTexture, 0, stepY);
            texture = first.framebufferTexture;
            return true;
        } catch (RuntimeException failure) {
            unavailable = true;
            close();
            GTNLLib.LOG.error("Frosted backdrop is unavailable, using plain shapes", failure);
            return false;
        } finally {
            main.bindFramebuffer(true);
            GL11.glPopAttrib();
        }
    }

    public void disc(float x, float y, float radius, int tint) {
        if (texture < 0) return;
        ShaderEffect shader = FxShaders.FROSTED;
        if (!shader.begin()) return;
        try {
            Minecraft minecraft = Minecraft.getMinecraft();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
            shader.uniform2f("viewSize", minecraft.displayWidth, minecraft.displayHeight);
            shader.uniform2f("center", x, y);
            shader.uniform1f("radius", radius);
            FxShapes.boundingQuad(x, y, radius, tint);
        } finally {
            shader.end();
        }
    }

    private static int[] windowRegion(float x, float y, float radius, int width, int height) {
        MODELVIEW.clear();
        PROJECTION.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (int corner = 0; corner < 4; corner++) {
            float localX = x + ((corner & 1) == 0 ? -radius : radius);
            float localY = y + ((corner & 2) == 0 ? -radius : radius);
            float[] eye = transform(MODELVIEW, localX, localY, 0, 1);
            float[] clip = transform(PROJECTION, eye[0], eye[1], eye[2], eye[3]);
            if (clip[3] == 0) return null;
            float windowX = (clip[0] / clip[3] + 1) / 2 * width;
            float windowY = (clip[1] / clip[3] + 1) / 2 * height;
            minX = Math.min(minX, windowX);
            minY = Math.min(minY, windowY);
            maxX = Math.max(maxX, windowX);
            maxY = Math.max(maxY, windowY);
        }
        int x0 = Math.max(0, (int) Math.floor(minX) - REGION_MARGIN);
        int y0 = Math.max(0, (int) Math.floor(minY) - REGION_MARGIN);
        int x1 = Math.min(width - 1, (int) Math.ceil(maxX) + REGION_MARGIN);
        int y1 = Math.min(height - 1, (int) Math.ceil(maxY) + REGION_MARGIN);
        return x0 > x1 || y0 > y1 ? null : new int[] { x0, y0, x1, y1 };
    }

    private static float[] transform(FloatBuffer matrix, float x, float y, float z, float w) {
        float[] result = new float[4];
        for (int row = 0; row < 4; row++) result[row] = matrix.get(row) * x + matrix.get(4 + row) * y
            + matrix.get(8 + row) * z
            + matrix.get(12 + row) * w;
        return result;
    }

    private void ensureSize(int width, int height) {
        if (first != null && first.framebufferWidth == width && first.framebufferHeight == height) return;
        close();
        first = create(width, height);
        second = create(width, height);
    }

    private static Framebuffer create(int width, int height) {
        Framebuffer target = new Framebuffer(width, height, false);
        target.setFramebufferFilter(GL11.GL_LINEAR);
        return target;
    }

    private static boolean pass(Framebuffer target, int input, float stepU, float stepV) {
        ShaderEffect shader = FxShaders.BLUR;
        if (!shader.begin()) return false;
        try {
            target.bindFramebuffer(true);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, input);
            shader.uniform2f("sampleStep", stepU, stepV);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-1, -1, 0, 0, 0);
            tessellator.addVertexWithUV(1, -1, 0, 1, 0);
            tessellator.addVertexWithUV(1, 1, 0, 1, 1);
            tessellator.addVertexWithUV(-1, 1, 0, 0, 1);
            tessellator.draw();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            return true;
        } finally {
            shader.end();
        }
    }

    @Override
    public void close() {
        if (first != null) first.deleteFramebuffer();
        if (second != null) second.deleteFramebuffer();
        first = null;
        second = null;
        texture = -1;
    }
}
