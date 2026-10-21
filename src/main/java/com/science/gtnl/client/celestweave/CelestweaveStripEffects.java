package com.science.gtnl.client.celestweave;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.DynamicTexture;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.science.gtnl.common.celestweave.CelestweaveCosmetic;
import com.science.gtnl.common.celestweave.CelestweaveCosmeticOption;
import com.science.gtnl.common.celestweave.CelestweaveStripStyle;
import com.science.gtnllib.client.fx.FxColor;
import com.science.gtnllib.client.fx.FxEasing;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CelestweaveStripEffects {

    private static final int SIZE = 64;
    private static final float PERIOD = 24F;
    private static final FloatBuffer PLANE = BufferUtils.createFloatBuffer(4);
    private static final Map<CelestweaveStripStyle, ICelestweaveStripEffect> EFFECTS = new EnumMap<>(
        CelestweaveStripStyle.class);

    static {
        EFFECTS.put(CelestweaveStripStyle.STEADY, new Steady());
        EFFECTS.put(CelestweaveStripStyle.FLICKER, new Flicker());
        EFFECTS.put(CelestweaveStripStyle.BREATH, new Breath());
        EFFECTS.put(CelestweaveStripStyle.FLOW, new Flow());
        EFFECTS.put(CelestweaveStripStyle.RAINBOW, new Rainbow());
        EFFECTS.put(CelestweaveStripStyle.GRADIENT, new Gradient());
    }

    private CelestweaveStripEffects() {}

    static ICelestweaveStripEffect of(CelestweaveStripStyle style) {
        return EFFECTS.getOrDefault(style, EFFECTS.get(CelestweaveStripStyle.STEADY));
    }

    static boolean registered(CelestweaveStripStyle style) {
        return EFFECTS.containsKey(style);
    }

    static float breath(float seconds) {
        float phase = seconds / 3.6F % 1F;
        if (phase < 0.45F) return FxEasing.smoothStep(phase / 0.45F);
        if (phase < 0.55F) return 1F;
        if (phase < 0.9F) return 1F - FxEasing.smoothStep((phase - 0.55F) / 0.35F);
        return 0F;
    }

    static int[] gradientPixels() {
        int[] pixels = new int[SIZE];
        for (int i = 0; i < SIZE; i++) pixels[i] = 0xFF000000 | Color.HSBtoRGB(i / (float) SIZE, 0.85F, 1F);
        return pixels;
    }

    static int[] flowPixels() {
        int[] pixels = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            float x = (i + 0.5F) / SIZE - 0.5F;
            int level = Math.round(255 * (0.28F + 0.72F * (float) Math.exp(-x * x / 0.0064F)));
            pixels[i] = 0xFF000000 | level << 16 | level << 8 | level;
        }
        return pixels;
    }

    private static float speed(CelestweaveCosmetic cosmetic) {
        return cosmetic.get(CelestweaveCosmeticOption.STRIP_SPEED);
    }

    private static void color(float r, float g, float b, float k) {
        GL11.glColor4f(Math.min(1F, r * k), Math.min(1F, g * k), Math.min(1F, b * k), 1F);
    }

    private static FloatBuffer plane(float a, float b, float c, float d) {
        PLANE.clear();
        PLANE.put(a)
            .put(b)
            .put(c)
            .put(d)
            .flip();
        return PLANE;
    }

    private static class Steady implements ICelestweaveStripEffect {

        @Override
        public boolean begin(CelestweavePose pose, CelestweaveCosmetic cosmetic, float seconds, float boneY) {
            color(pose.glowR, pose.glowG, pose.glowB, pose.steadyBrightness);
            return false;
        }
    }

    private static class Flicker implements ICelestweaveStripEffect {

        @Override
        public boolean begin(CelestweavePose pose, CelestweaveCosmetic cosmetic, float seconds, float boneY) {
            color(pose.glowR, pose.glowG, pose.glowB, pose.brightness);
            return false;
        }
    }

    private static class Breath implements ICelestweaveStripEffect {

        @Override
        public boolean begin(CelestweavePose pose, CelestweaveCosmetic cosmetic, float seconds, float boneY) {
            float level = breath(seconds * speed(cosmetic)), white = 0.25F * level * level * level * level;
            color(
                pose.glowR + (1F - pose.glowR) * white,
                pose.glowG + (1F - pose.glowG) * white,
                pose.glowB + (1F - pose.glowB) * white,
                pose.steadyBrightness * (0.12F + 0.88F * level));
            return false;
        }
    }

    private static class Rainbow implements ICelestweaveStripEffect {

        @Override
        public boolean begin(CelestweavePose pose, CelestweaveCosmetic cosmetic, float seconds, float boneY) {
            int rgb = Color.HSBtoRGB(seconds * speed(cosmetic) * 0.15F % 1F, 0.85F, 1F);
            color(FxColor.red(rgb), FxColor.green(rgb), FxColor.blue(rgb), pose.steadyBrightness);
            return false;
        }
    }

    private abstract static class Textured implements ICelestweaveStripEffect {

        private DynamicTexture texture;

        abstract int[] pixels();

        abstract float scroll();

        abstract void tint(CelestweavePose pose);

        @Override
        public boolean begin(CelestweavePose pose, CelestweaveCosmetic cosmetic, float seconds, float boneY) {
            if (texture == null) texture = upload(pixels());
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getGlTextureId());
            tint(pose);
            GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_OBJECT_LINEAR);
            GL11.glTexGen(
                GL11.GL_S,
                GL11.GL_OBJECT_PLANE,
                plane(0F, 1F / PERIOD, 0F, boneY / PERIOD + seconds * speed(cosmetic) * scroll() % 1F));
            GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
            return true;
        }

        @Override
        public void end() {
            GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
            GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
            GL11.glTexGen(GL11.GL_S, GL11.GL_OBJECT_PLANE, plane(1F, 0F, 0F, 0F));
        }

        private static DynamicTexture upload(int[] pixels) {
            DynamicTexture texture = new DynamicTexture(SIZE, 1);
            System.arraycopy(pixels, 0, texture.getTextureData(), 0, SIZE);
            texture.updateDynamicTexture();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getGlTextureId());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            return texture;
        }
    }

    private static class Flow extends Textured {

        @Override
        int[] pixels() {
            return flowPixels();
        }

        @Override
        float scroll() {
            return 0.5F;
        }

        @Override
        void tint(CelestweavePose pose) {
            color(pose.glowR, pose.glowG, pose.glowB, Math.min(1.4F, pose.steadyBrightness * 1.25F));
        }
    }

    private static class Gradient extends Textured {

        @Override
        int[] pixels() {
            return gradientPixels();
        }

        @Override
        float scroll() {
            return 0.2F;
        }

        @Override
        void tint(CelestweavePose pose) {
            color(1F, 1F, 1F, pose.steadyBrightness);
        }
    }
}
