package com.gtnllib.client.text;

import java.nio.FloatBuffer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

/** Replays effect text after the model geometry that owns Angelica's deferred font batch. */
public class DeferredTextEffects {

    private static final ObjectArrayFIFOQueue<Draw> PENDING = new ObjectArrayFIFOQueue<>();
    private static final ObjectArrayList<Draw> POOL = new ObjectArrayList<>();
    private static boolean flushing;

    public static boolean isFlushing() {
        return flushing;
    }

    public static void enqueue(FontRenderer font, String text, float x, float y, int color, boolean shadow) {
        Draw draw = POOL.isEmpty() ? new Draw() : POOL.pop();
        draw.capture(font, text, x, y, color, shadow);
        PENDING.enqueue(draw);
    }

    public static void flush() {
        if (flushing || PENDING.isEmpty() || EffectTextRenderer.isCapturing()) return;
        flushing = true;
        try (TextRenderState ignored = new TextRenderState()) {
            while (!PENDING.isEmpty()) {
                Draw draw = PENDING.dequeue();
                try {
                    draw.render();
                } finally {
                    recycle(draw);
                }
            }
        } finally {
            clear();
            flushing = false;
        }
    }

    public static void clear() {
        while (!PENDING.isEmpty()) recycle(PENDING.dequeue());
    }

    private static void recycle(Draw draw) {
        draw.font = null;
        draw.text = null;
        if (POOL.size() < 256) POOL.push(draw);
    }

    /** Mutable storage is reused between frames and owned exclusively by the render thread. */
    public static class Draw {

        private final FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        private final FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        private FontRenderer font;
        private String text;
        private float x;
        private float y;
        private int color;
        private boolean shadow;
        private boolean unicode;
        private boolean depthTest;
        private boolean depthMask;
        private float lightX;
        private float lightY;

        private void capture(FontRenderer font, String text, float x, float y, int color, boolean shadow) {
            this.font = font;
            this.text = text;
            this.x = x;
            this.y = y;
            this.color = color;
            this.shadow = shadow;
            unicode = font.getUnicodeFlag();
            depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
            lightX = OpenGlHelper.lastBrightnessX;
            lightY = OpenGlHelper.lastBrightnessY;
            projection.clear();
            modelView.clear();
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        }

        private void render() {
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadMatrix(projection);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadMatrix(modelView);
            if (depthTest) GL11.glEnable(GL11.GL_DEPTH_TEST);
            else GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(depthMask);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
            boolean previousUnicode = font.getUnicodeFlag();
            font.setUnicodeFlag(unicode);
            try {
                EffectTextRenderer.INSTANCE.draw(font, text, x, y, color, shadow);
            } finally {
                font.setUnicodeFlag(previousUnicode);
            }
        }
    }
}
