package com.gtnllib.utils;

import com.cleanroommc.modularui.drawable.text.TextRenderer;
import com.cleanroommc.modularui.utils.Alignment;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextRenderUtils {

    public static void drawTextLine(String text, int x, int y, int width, int height, Alignment alignment, int color,
        boolean shadow) {
        textRenderer(x, y, width, height, alignment, color, shadow, 1F).drawSimple(text);
    }

    public static void drawTextFitted(String text, int x, int y, int width, int height, Alignment alignment, int color,
        boolean shadow, float scale) {
        textRenderer(x, y, width, height, alignment, color, shadow, scale).drawCut(text);
    }

    private static TextRenderer textRenderer(int x, int y, int width, int height, Alignment alignment, int color,
        boolean shadow, float scale) {
        TextRenderer renderer = TextRenderer.SHARED;
        renderer.setScale(scale);
        renderer.setShadow(shadow);
        renderer.setColor(color);
        renderer.setAlignment(alignment, width, height);
        renderer.setPos(x, y);
        return renderer;
    }
}
