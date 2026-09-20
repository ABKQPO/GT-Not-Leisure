package com.science.gtnl.client.text;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.gtnewhorizon.gtnhlib.util.font.IFontParameters;
import com.science.gtnl.client.text.EffectTextLayout.Glyph;
import com.science.gtnl.client.text.EffectTextLayout.Layout;

/** Render-thread-owned, bounded cache of native font coverage masks. */
public class TextMaskCache {

    private static final int MAX_ENTRIES = 256;
    private static final long MAX_PIXELS = 4L * 1024 * 1024;
    private static final int RESOLUTION = 2;
    private final Map<Key, Mask> masks = new LinkedHashMap<>(32, 0.75f, true);
    private long pixels;

    public Mask get(FontRenderer font, String text, float width, float height, float padding) {
        IFontParameters parameters = (IFontParameters) font;
        Key key = new Key(
            font,
            text,
            width,
            height,
            padding,
            font.getUnicodeFlag(),
            parameters.getGlyphScaleX(),
            parameters.getGlyphScaleY(),
            parameters.getGlyphSpacing(),
            parameters.getShadowOffset());
        Mask present = masks.get(key);
        if (present != null) {
            if (hasAnimatedGlyphs(text)) {
                capture(font, text, width, height, padding, present.target());
            }
            return present;
        }
        int pixelWidth = Math.max(1, (int) Math.ceil((width + padding * 2) * RESOLUTION));
        int pixelHeight = Math.max(1, (int) Math.ceil((height + padding * 2) * RESOLUTION));
        int maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
        if (pixelWidth > maxTextureSize || pixelHeight > maxTextureSize || (long) pixelWidth * pixelHeight > MAX_PIXELS)
            return null;
        Mask mask;
        try (TextRenderState ignored = new TextRenderState()) {
            Framebuffer target = new Framebuffer(pixelWidth, pixelHeight, false);
            try {
                mask = capture(font, text, width, height, padding, target);
            } catch (RuntimeException exception) {
                target.deleteFramebuffer();
                throw exception;
            }
        }
        masks.put(key, mask);
        pixels += mask.pixels();
        Iterator<Entry<Key, Mask>> iterator = masks.entrySet()
            .iterator();
        if (masks.size() > MAX_ENTRIES || pixels > MAX_PIXELS) {
            try (TextRenderState ignored = new TextRenderState()) {
                while (masks.size() > MAX_ENTRIES || pixels > MAX_PIXELS) {
                    Mask removed = iterator.next()
                        .getValue();
                    iterator.remove();
                    pixels -= removed.pixels();
                    removed.target()
                        .deleteFramebuffer();
                }
            }
        }
        return mask;
    }

    private static boolean hasAnimatedGlyphs(String text) {
        for (int i = 0; i + 1 < text.length(); i++) {
            if (text.charAt(i) == '\u00a7') {
                char code = Character.toLowerCase(text.charAt(++i));
                if ("0123456789abcdeflmnorgx".indexOf(code) < 0) return true;
            }
        }
        return false;
    }

    private Mask capture(FontRenderer font, String text, float width, float height, float padding, Framebuffer target) {
        try (TextRenderState ignored = new TextRenderState()) {
            target.setFramebufferColor(0, 0, 0, 0);
            target.setFramebufferFilter(GL11.GL_LINEAR);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glColorMask(true, true, true, true);
            target.framebufferClear();
            target.bindFramebuffer(true);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(
                0,
                target.framebufferWidth / (float) RESOLUTION,
                target.framebufferHeight / (float) RESOLUTION,
                0,
                -100,
                100);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(padding, padding, 0);
            GL20.glUseProgram(0);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper
                .glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            EffectTextRenderer.beginCapture();
            try {
                font.drawString(text, 0, 0, 0xFFFFFFFF, false);
            } finally {
                EffectTextRenderer.endCapture();
            }
            writeGlyphTextureMetrics(font, text, width, height, padding);
            return new Mask(target, width, height, padding, RESOLUTION);
        }
    }

    private static void writeGlyphTextureMetrics(FontRenderer font, String text, float width, float height,
        float padding) {
        Layout layout = EffectTextLayout.create(font, text);
        GlyphTextureMetrics[] metrics = new GlyphTextureMetrics[layout.glyphs()
            .size()];
        for (int i = 0; i < metrics.length; i++) {
            metrics[i] = GlyphTextureMetrics.of(
                font,
                layout.glyphs()
                    .get(i)
                    .text()
                    .charAt(0));
        }
        GL20.glUseProgram(0);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_FOG);
        // RGB carries the atlas interval and horizontal glyph center; alpha retains font coverage.
        GL11.glColorMask(true, true, true, false);
        float cursor = 0;
        int index = 0;
        GL11.glBegin(GL11.GL_QUADS);
        for (Glyph glyph : layout.glyphs()) {
            float left = index == 0 ? -padding : cursor - layout.spacing() * 0.5f;
            float right = cursor + glyph.width() + layout.spacing() * 0.5f;
            if (index == layout.glyphs()
                .size() - 1) right = width + padding;
            float center = (cursor + glyph.width() * 0.5f) / Math.max(1, width);
            GL11.glColor3f(metrics[index].vStart(), metrics[index].vSpan(), center);
            GL11.glVertex2f(left, -padding);
            GL11.glVertex2f(left, height + padding);
            GL11.glVertex2f(right, height + padding);
            GL11.glVertex2f(right, -padding);
            // The top metadata row also retains glyph starts for source renderers using left-edge phase.
            GL11.glColor3f(metrics[index].vStart(), metrics[index].vSpan(), cursor / Math.max(1, width));
            GL11.glVertex2f(left, -padding);
            GL11.glVertex2f(left, -padding + 1f / RESOLUTION);
            GL11.glVertex2f(right, -padding + 1f / RESOLUTION);
            GL11.glVertex2f(right, -padding);
            cursor += glyph.width() + (glyph.width() > 0 ? layout.spacing() : 0);
            index++;
        }
        GL11.glEnd();
    }

    public void clear() {
        if (!masks.isEmpty()) {
            try (TextRenderState ignored = new TextRenderState()) {
                for (Mask mask : masks.values()) mask.target()
                    .deleteFramebuffer();
            }
        }
        masks.clear();
        pixels = 0;
    }

    public record Key(FontRenderer font, String text, float width, float height, float padding, boolean unicode,
        float scaleX, float scaleY, float spacing, float shadowOffset) {}

    public record Mask(Framebuffer target, float width, float height, float padding, int resolution) {

        public long pixels() {
            return (long) target.framebufferTextureWidth * target.framebufferTextureHeight;
        }

        public float textureWidth() {
            return target.framebufferTextureWidth / (float) resolution;
        }

        public float textureHeight() {
            return target.framebufferTextureHeight / (float) resolution;
        }
    }
}
