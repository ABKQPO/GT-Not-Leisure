package com.gtnllib.client.fx.screen;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.gtnllib.client.fx.FxAngle;
import com.gtnllib.client.shader.ShaderEffect;
import com.gtnllib.utils.ColorMath;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FxShapes {

    private static final float QUAD_MARGIN = 2F;
    private static final int FALLBACK_SEGMENTS = 64;

    public static void prepareState() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glColor4f(1, 1, 1, 1);
    }

    public static void disc(float x, float y, float radius, int argb) {
        ShaderEffect shader = FxShaders.DISC;
        if (shader.begin()) {
            try {
                shader.uniform2f("center", x, y);
                shader.uniform1f("radius", radius);
                boundingQuad(x, y, radius, argb);
            } finally {
                shader.end();
            }
            return;
        }
        fan(x, y, radius, argb, argb);
    }

    public static void glow(float x, float y, float radius, int argb) {
        ShaderEffect shader = FxShaders.GLOW;
        if (shader.begin()) {
            try {
                shader.uniform2f("center", x, y);
                shader.uniform1f("radius", radius);
                boundingQuad(x, y, radius, argb);
            } finally {
                shader.end();
            }
            return;
        }
        fan(x, y, radius, argb, argb & 0xFFFFFF);
    }

    public static void ring(float x, float y, float innerRadius, float outerRadius, int argb) {
        ShaderEffect shader = FxShaders.RING;
        if (shader.begin()) {
            try {
                shader.uniform2f("center", x, y);
                shader.uniform1f("innerRadius", innerRadius);
                shader.uniform1f("outerRadius", outerRadius);
                boundingQuad(x, y, outerRadius, argb);
            } finally {
                shader.end();
            }
            return;
        }
        band(x, y, innerRadius, outerRadius, 0, FxAngle.TAU, argb);
    }

    public static void sector(float x, float y, float innerRadius, float outerRadius, float centerAngle,
        float halfRange, int argb) {
        // Why: a full sector would soften its two edges where they meet, leaving a faint seam across the ring.
        if (FxAngle.fullTurn(halfRange)) {
            ring(x, y, innerRadius, outerRadius, argb);
            return;
        }
        ShaderEffect shader = FxShaders.SECTOR;
        if (shader.begin()) {
            try {
                shader.uniform2f("center", x, y);
                shader.uniform1f("innerRadius", innerRadius);
                shader.uniform1f("outerRadius", outerRadius);
                shader.uniform1f("centerAngle", centerAngle);
                shader.uniform1f("halfRange", halfRange);
                boundingQuad(x, y, outerRadius, argb);
            } finally {
                shader.end();
            }
            return;
        }
        band(x, y, innerRadius, outerRadius, centerAngle - halfRange, halfRange * 2, argb);
    }

    public static void segment(float x0, float y0, float x1, float y1, float width, int argb) {
        ShaderEffect shader = FxShaders.SEGMENT;
        if (shader.begin()) {
            try {
                shader.uniform2f("from", x0, y0);
                shader.uniform2f("to", x1, y1);
                shader.uniform1f("lineWidth", width);
                float margin = width / 2F + QUAD_MARGIN;
                quad(
                    Math.min(x0, x1) - margin,
                    Math.min(y0, y1) - margin,
                    Math.max(x0, x1) + margin,
                    Math.max(y0, y1) + margin,
                    argb);
            } finally {
                shader.end();
            }
            return;
        }
        float dx = x1 - x0, dy = y1 - y0;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length <= 0) return;
        float nx = -dy / length * width / 2F, ny = dx / length * width / 2F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        ColorMath.tessellateArgb(tessellator, argb);
        tessellator.addVertex(x0 + nx, y0 + ny, 0);
        tessellator.addVertex(x1 + nx, y1 + ny, 0);
        tessellator.addVertex(x1 - nx, y1 - ny, 0);
        tessellator.addVertex(x0 - nx, y0 - ny, 0);
        tessellator.draw();
    }

    static void boundingQuad(float x, float y, float radius, int argb) {
        float extent = radius + QUAD_MARGIN;
        quad(x - extent, y - extent, x + extent, y + extent, argb);
    }

    private static void quad(float x0, float y0, float x1, float y1, int argb) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        ColorMath.tessellateArgb(tessellator, argb);
        tessellator.addVertex(x0, y0, 0);
        tessellator.addVertex(x0, y1, 0);
        tessellator.addVertex(x1, y1, 0);
        tessellator.addVertex(x1, y0, 0);
        tessellator.draw();
    }

    private static void fan(float x, float y, float radius, int centerArgb, int edgeArgb) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        ColorMath.tessellateArgb(tessellator, centerArgb);
        tessellator.addVertex(x, y, 0);
        ColorMath.tessellateArgb(tessellator, edgeArgb);
        for (int i = 0; i <= FALLBACK_SEGMENTS; i++) {
            float angle = FxAngle.TAU * i / FALLBACK_SEGMENTS;
            tessellator.addVertex(x + FxAngle.dirX(angle) * radius, y + FxAngle.dirY(angle) * radius, 0);
        }
        tessellator.draw();
    }

    private static void band(float x, float y, float innerRadius, float outerRadius, float start, float sweep,
        int argb) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLE_STRIP);
        ColorMath.tessellateArgb(tessellator, argb);
        for (int i = 0; i <= FALLBACK_SEGMENTS; i++) {
            float angle = start + sweep * i / FALLBACK_SEGMENTS;
            float dx = FxAngle.dirX(angle), dy = FxAngle.dirY(angle);
            tessellator.addVertex(x + dx * outerRadius, y + dy * outerRadius, 0);
            tessellator.addVertex(x + dx * innerRadius, y + dy * innerRadius, 0);
        }
        tessellator.draw();
    }
}
