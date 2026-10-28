package com.gtnllib.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.utils.Platform;
import com.cleanroommc.modularui.widget.Widget;

public class FadingNotice extends Widget<FadingNotice> {

    private static final int FADE_MILLIS = 500, PADDING = 8, ACCENT_WIDTH = 2, MIN_ALPHA = 0x10;
    private int duration = 3000, background = 0xD0000000;
    private String text = "";
    private int color;
    private long shownAt;

    public FadingNotice duration(int millis) {
        this.duration = millis;
        return this;
    }

    public FadingNotice background(int argb) {
        this.background = argb;
        return this;
    }

    public FadingNotice show(String text, int rgb) {
        this.text = text;
        this.color = rgb & 0xFFFFFF;
        this.shownAt = Minecraft.getSystemTime();
        return this;
    }

    @Override
    public boolean canHover() {
        return false;
    }

    public static float opacity(long elapsed, int duration) {
        if (elapsed < 0 || elapsed >= duration) return 0;
        return Math.min(1F, (duration - elapsed) / (float) FADE_MILLIS);
    }

    @Override
    public void draw(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        float opacity = opacity(Minecraft.getSystemTime() - shownAt, duration);
        if (text.isEmpty() || opacity <= 0) return;
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int width = getArea().w(), height = getArea().h();
        String shown = font.trimStringToWidth(text, Math.max(0, width - PADDING * 2));
        int box = Math.min(width, font.getStringWidth(shown) + PADDING * 2), left = (width - box) / 2;
        int alpha = Math.max(MIN_ALPHA, Math.round(opacity * 255));
        GL11.glEnable(GL11.GL_BLEND);
        GuiDraw.drawRect(left, 0, box, height, scaleAlpha(background, opacity));
        GuiDraw.drawRect(left, 0, ACCENT_WIDTH, height, alpha << 24 | color);
        Platform.setupDrawFont();
        font.drawStringWithShadow(shown, left + PADDING, (height - font.FONT_HEIGHT) / 2 + 1, alpha << 24 | color);
    }

    private static int scaleAlpha(int argb, float opacity) {
        return Math.round((argb >>> 24) * opacity) << 24 | argb & 0xFFFFFF;
    }
}
