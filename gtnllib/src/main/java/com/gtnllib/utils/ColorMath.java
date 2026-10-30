package com.gtnllib.utils;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorMath {

    private static final double RED_WEIGHT = 0.2126, GREEN_WEIGHT = 0.7152, BLUE_WEIGHT = 0.0722, LINEAR_KNEE = 0.04045,
        LINEAR_SLOPE = 12.92, GAMMA_OFFSET = 0.055, GAMMA = 2.4, CONTRAST_FLARE = 0.05;

    public static float red(int rgb) {
        return (rgb >> 16 & 255) / 255F;
    }

    public static float green(int rgb) {
        return (rgb >> 8 & 255) / 255F;
    }

    public static float blue(int rgb) {
        return (rgb & 255) / 255F;
    }

    public static int mixWhite(int rgb, float k) {
        int r = rgb >> 16 & 255, g = rgb >> 8 & 255, b = rgb & 255;
        return Math.round(r + (255 - r) * k) << 16 | Math.round(g + (255 - g) * k) << 8 | Math.round(b + (255 - b) * k);
    }

    public static int scale(int rgb, float k) {
        int r = Math.min(255, Math.round((rgb >> 16 & 255) * k));
        int g = Math.min(255, Math.round((rgb >> 8 & 255) * k));
        int b = Math.min(255, Math.round((rgb & 255) * k));
        return r << 16 | g << 8 | b;
    }

    public static int lerp(int from, int to, float t) {
        int r = Math.round((from >> 16 & 255) + ((to >> 16 & 255) - (from >> 16 & 255)) * t);
        int g = Math.round((from >> 8 & 255) + ((to >> 8 & 255) - (from >> 8 & 255)) * t);
        int b = Math.round((from & 255) + ((to & 255) - (from & 255)) * t);
        return r << 16 | g << 8 | b;
    }

    public static int over(int argb, int rgb) {
        return lerp(rgb, argb & 0xFFFFFF, (argb >>> 24) / 255F);
    }

    public static double[] hsl(int rgb) {
        double r = (rgb >> 16 & 255) / 255D, g = (rgb >> 8 & 255) / 255D, b = (rgb & 255) / 255D;
        double max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b)), lightness = (max + min) / 2;
        if (max == min) return new double[] { 0, 0, lightness };
        double delta = max - min, saturation = delta / (1 - Math.abs(2 * lightness - 1));
        double sector = max == r ? (g - b) / delta % 6 : max == g ? (b - r) / delta + 2 : (r - g) / delta + 4;
        return new double[] { (sector * 60 + 360) % 360, saturation, lightness };
    }

    public static int hsl(double hue, double saturation, double lightness) {
        double h = (hue % 360 + 360) % 360, s = MathUtils.clampUnit(saturation), l = MathUtils.clampUnit(lightness);
        double chroma = (1 - Math.abs(2 * l - 1)) * s, x = chroma * (1 - Math.abs(h / 60 % 2 - 1)), m = l - chroma / 2;
        int sector = (int) (h / 60);
        double r = sector == 0 || sector == 5 ? chroma : sector == 1 || sector == 4 ? x : 0;
        double g = sector == 1 || sector == 2 ? chroma : sector == 0 || sector == 3 ? x : 0;
        double b = sector == 3 || sector == 4 ? chroma : sector == 2 || sector == 5 ? x : 0;
        return channel(r + m) << 16 | channel(g + m) << 8 | channel(b + m);
    }

    public static double luminance(int rgb) {
        return RED_WEIGHT * linear(rgb >> 16 & 255) + GREEN_WEIGHT * linear(rgb >> 8 & 255)
            + BLUE_WEIGHT * linear(rgb & 255);
    }

    public static double contrast(int rgb, int other) {
        double first = luminance(rgb) + CONTRAST_FLARE, second = luminance(other) + CONTRAST_FLARE;
        return Math.max(first, second) / Math.min(first, second);
    }

    private static double linear(int channel) {
        double value = channel / 255D;
        return value <= LINEAR_KNEE ? value / LINEAR_SLOPE
            : Math.pow((value + GAMMA_OFFSET) / (1 + GAMMA_OFFSET), GAMMA);
    }

    private static int channel(double value) {
        return (int) Math.round(MathUtils.clampUnit(value) * 255);
    }

    public static void tessellate(Tessellator tessellator, int rgb, float alpha) {
        tessellator.setColorRGBA_F(red(rgb), green(rgb), blue(rgb), MathUtils.clampUnit(alpha));
    }

    public static void tessellateArgb(Tessellator tessellator, int argb) {
        tessellator.setColorRGBA_I(argb & 0xFFFFFF, argb >>> 24);
    }

    public static void gl(int rgb, float alpha) {
        GL11.glColor4f(red(rgb), green(rgb), blue(rgb), alpha);
    }
}
