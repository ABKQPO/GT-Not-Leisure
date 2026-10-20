package com.science.gtnl.client.fx;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FxColor {

    private FxColor() {}

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

    public static void tessellate(Tessellator tessellator, int rgb, float alpha) {
        tessellator.setColorRGBA_F(red(rgb), green(rgb), blue(rgb), Math.max(0F, Math.min(1F, alpha)));
    }

    public static void gl(int rgb, float alpha) {
        GL11.glColor4f(red(rgb), green(rgb), blue(rgb), alpha);
    }
}
