package com.science.gtnl.client.topology;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TopologyDraw {

    private static final int SEGMENTS = 48;
    private static final float TURN = (float) (Math.PI * 2);
    private static final float FEATHER = 0.75F;

    public static final float[] CORNER_TOP_RIGHT = { 0.0F, 0.25F };
    public static final float[] CORNER_BOTTOM_RIGHT = { 0.25F, 0.5F };
    public static final float[] CORNER_BOTTOM_LEFT = { 0.5F, 0.75F };
    public static final float[] CORNER_TOP_LEFT = { 0.75F, 1.0F };

    private final Tessellator tessellator = Tessellator.instance;
    private boolean batching;

    public void begin() {
        if (batching) return;
        batching = true;
        // Deliberately no glPushAttrib: the surrounding GUI pass owns that stack, and nesting into it here left the
        // whole batch invisible.
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        tessellator.startDrawingQuads();
    }

    public void end() {
        if (!batching) return;
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        batching = false;
    }

    public void flush() {
        if (!batching) return;
        tessellator.draw();
        tessellator.startDrawingQuads();
    }

    public boolean batching() {
        return batching;
    }

    public void rect(float left, float top, float right, float bottom, int color) {
        quad(left, top, right, top, right, bottom, left, bottom, color);
    }

    public void gradient(float left, float top, float right, float bottom, int topColor, int bottomColor) {
        colored(left, top, topColor);
        colored(right, top, topColor);
        colored(right, bottom, bottomColor);
        colored(left, bottom, bottomColor);
    }

    public void roundedRect(float left, float top, float right, float bottom, float radius, int color) {
        float limit = Math.min(radius, Math.min(right - left, bottom - top) * 0.5F);
        if (limit <= 0.5F) {
            rect(left, top, right, bottom, color);
            return;
        }
        rect(left + limit, top, right - limit, bottom, color);
        rect(left, top + limit, left + limit, bottom - limit, color);
        rect(right - limit, top + limit, right, bottom - limit, color);
        pie(left + limit, top + limit, limit, CORNER_TOP_LEFT, color);
        pie(right - limit, top + limit, limit, CORNER_TOP_RIGHT, color);
        pie(right - limit, bottom - limit, limit, CORNER_BOTTOM_RIGHT, color);
        pie(left + limit, bottom - limit, limit, CORNER_BOTTOM_LEFT, color);
    }

    public void roundedOutline(float left, float top, float right, float bottom, float radius, float weight,
        int color) {
        float limit = Math.min(radius, Math.min(right - left, bottom - top) * 0.5F);
        rect(left + limit, top, right - limit, top + weight, color);
        rect(left + limit, bottom - weight, right - limit, bottom, color);
        rect(left, top + limit, left + weight, bottom - limit, color);
        rect(right - weight, top + limit, right, bottom - limit, color);
        float ringRadius = limit - weight * 0.5F;
        arc(left + limit, top + limit, ringRadius, weight, CORNER_TOP_LEFT[0], CORNER_TOP_LEFT[1], color);
        arc(right - limit, top + limit, ringRadius, weight, CORNER_TOP_RIGHT[0], CORNER_TOP_RIGHT[1], color);
        arc(right - limit, bottom - limit, ringRadius, weight, CORNER_BOTTOM_RIGHT[0], CORNER_BOTTOM_RIGHT[1], color);
        arc(left + limit, bottom - limit, ringRadius, weight, CORNER_BOTTOM_LEFT[0], CORNER_BOTTOM_LEFT[1], color);
    }

    public void disc(float centerX, float centerY, float radius, int color) {
        if (radius <= 0) return;
        float solid = Math.max(0, radius - FEATHER);
        if (solid > 0) pie(centerX, centerY, solid, 0.0F, 1.0F, color);
        if (solid < radius) {
            int faded = TopologyPalette.withAlpha(color, alpha(color) * 0.5F);
            arc(centerX, centerY, (solid + radius) * 0.5F, radius - solid, 0.0F, 1.0F, faded);
        }
    }

    public void ring(float centerX, float centerY, float radius, float thickness, int color) {
        arc(centerX, centerY, radius, thickness, 0.0F, 1.0F, color);
    }

    public void arc(float centerX, float centerY, float radius, float thickness, float from, float to, int color) {
        if (radius <= 0 || thickness <= 0 || to <= from) return;
        float inner = Math.max(0, radius - thickness * 0.5F);
        float outer = radius + thickness * 0.5F;
        int first = (int) Math.floor(from * SEGMENTS);
        int last = (int) Math.ceil(to * SEGMENTS);
        for (int index = first; index < last; index++) {
            float a0 = Math.max(from, index / (float) SEGMENTS);
            float a1 = Math.min(to, (index + 1) / (float) SEGMENTS);
            if (a1 <= a0) continue;
            float c0 = MathHelper.cos(a0 * TURN - TURN * 0.25F), s0 = MathHelper.sin(a0 * TURN - TURN * 0.25F);
            float c1 = MathHelper.cos(a1 * TURN - TURN * 0.25F), s1 = MathHelper.sin(a1 * TURN - TURN * 0.25F);
            quad(
                centerX + c0 * inner,
                centerY + s0 * inner,
                centerX + c0 * outer,
                centerY + s0 * outer,
                centerX + c1 * outer,
                centerY + s1 * outer,
                centerX + c1 * inner,
                centerY + s1 * inner,
                color);
        }
    }

    public void halo(float centerX, float centerY, float innerRadius, float outerRadius, int color, float strength) {
        if (outerRadius <= innerRadius) return;
        int steps = 7;
        for (int step = 0; step < steps; step++) {
            float t0 = step / (float) steps, t1 = (step + 1) / (float) steps;
            float r0 = innerRadius + (outerRadius - innerRadius) * t0;
            float r1 = innerRadius + (outerRadius - innerRadius) * t1;
            float fade = (1.0F - t0) * (1.0F - t0) * strength;
            arc(centerX, centerY, (r0 + r1) * 0.5F, r1 - r0, 0.0F, 1.0F, TopologyPalette.withAlpha(color, fade));
        }
    }

    public void line(float x1, float y1, float x2, float y2, float weight, int color) {
        float dx = x2 - x1, dy = y2 - y1;
        float length = MathHelper.sqrt_float(dx * dx + dy * dy);
        if (length < 1.0E-4F) return;
        float nx = -dy / length * weight * 0.5F, ny = dx / length * weight * 0.5F;
        quad(x1 + nx, y1 + ny, x2 + nx, y2 + ny, x2 - nx, y2 - ny, x1 - nx, y1 - ny, color);
    }

    public void curve(float x1, float y1, float cx, float cy, float x2, float y2, float weight, int color,
        int segments) {
        float previousX = x1, previousY = y1;
        for (int step = 1; step <= segments; step++) {
            float t = step / (float) segments;
            float x = curvePoint(x1, cx, x2, t);
            float y = curvePoint(y1, cy, y2, t);
            line(previousX, previousY, x, y, weight, color);
            previousX = x;
            previousY = y;
        }
    }

    public static float curvePoint(float start, float control, float end, float t) {
        float inverse = 1.0F - t;
        return inverse * inverse * start + 2 * inverse * t * control + t * t * end;
    }

    public static float curveX(float x1, float cx, float x2, float t) {
        return curvePoint(x1, cx, x2, t);
    }

    public static float curveY(float y1, float cy, float y2, float t) {
        return curvePoint(y1, cy, y2, t);
    }

    private void pie(float centerX, float centerY, float radius, float[] turns, int color) {
        pie(centerX, centerY, radius, turns[0], turns[1], color);
    }

    private void pie(float centerX, float centerY, float radius, float from, float to, int color) {
        arc(centerX, centerY, radius * 0.5F, radius, from, to, color);
    }

    private void quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int color) {
        colored(x1, y1, color);
        colored(x2, y2, color);
        colored(x3, y3, color);
        colored(x4, y4, color);
    }

    private void colored(float x, float y, int color) {
        tessellator.setColorRGBA((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, color >>> 24);
        tessellator.addVertex(x, y, 0);
    }

    private static float alpha(int color) {
        return (color >>> 24) / 255.0F;
    }
}
