package com.science.gtnl.client.topology;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Color;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Bridge between the explorer chrome and the ModularUI theme that is active for the screen.
 * <p>
 * Every surface, button and chrome text colour is taken from the theme, so the viewer follows the Minecraft theme by
 * default and any resource pack override for free. Only the graph itself keeps its own palette, because those colours
 * encode network data rather than styling.
 */
@SideOnly(Side.CLIENT)
public class TopologyTheme {

    private static final int FADE_TARGET = 0xFF9A9A9A;
    private static final float MUTED_FADE = 0.45F;
    private static final float DIM_FADE = 0.72F;
    private static final int GLYPH_HEIGHT = 8;

    private TopologyTheme() {}

    private static WidgetTheme panel(ModularGuiContext context) {
        return context.getTheme()
            .getPanelTheme()
            .getTheme();
    }

    private static WidgetTheme button(ModularGuiContext context, boolean hovered) {
        return context.getTheme()
            .getButtonTheme()
            .getTheme(hovered);
    }

    public static int text(ModularGuiContext context) {
        return opaque(panel(context).getTextColor());
    }

    public static int textMuted(ModularGuiContext context) {
        return fade(text(context), MUTED_FADE);
    }

    public static int textDim(ModularGuiContext context) {
        return fade(text(context), DIM_FADE);
    }

    public static int buttonText(ModularGuiContext context, boolean hovered) {
        return opaque(button(context, hovered).getTextColor());
    }

    public static boolean buttonShadow(ModularGuiContext context, boolean hovered) {
        return button(context, hovered).getTextShadow();
    }

    /**
     * Paints the vanilla button surface. A pressed button uses the darker vanilla variant so a latched filter reads as
     * held down rather than merely highlighted.
     */
    public static void drawButton(ModularGuiContext context, int x, int y, int width, int height, boolean hovered,
        boolean pressed) {
        if (width <= 0 || height <= 0) return;
        WidgetTheme theme = button(context, hovered);
        IDrawable surface = pressed ? hovered ? GuiTextures.MC_BUTTON_HOVERED_PRESSED : GuiTextures.MC_BUTTON_PRESSED
            : theme.getBackground();
        if (surface != null) surface.draw(context, x, y, width, height, theme);
    }

    /**
     * ModularUI draws every sibling's background before any sibling's content, so a full screen canvas would erase a
     * neighbour's themed background. Chrome therefore paints its own surface here, inside its own draw pass, which
     * keeps the painter's order exactly as the widgets are ordered.
     */
    public static void drawPanel(ModularGuiContext context, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) return;
        WidgetTheme theme = panel(context);
        IDrawable surface = theme.getBackground();
        if (surface != null) surface.draw(context, x, y, width, height, theme);
    }

    public static void drawInset(ModularGuiContext context, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) return;
        GuiTextures.DISPLAY_SMALL.draw(context, x, y, width, height, panel(context));
    }

    public static void drawDisplay(ModularGuiContext context, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) return;
        GuiTextures.DISPLAY.draw(context, x, y, width, height, panel(context));
    }

    /**
     * Vanilla centres button captions on the glyph box rather than on the line height, which is a pixel higher than
     * the naive calculation. Every button in the viewer goes through this so they all sit the same way.
     */
    public static int captionTop(int top, int height) {
        return top + (height - GLYPH_HEIGHT) / 2;
    }

    public static void label(String value, int x, int y, int color, boolean shadow) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        if (shadow) font.drawStringWithShadow(value, x, y, color);
        else font.drawString(value, x, y, color);
    }

    public static void centeredLabel(String value, int centerX, int y, int color, boolean shadow) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        label(value, centerX - font.getStringWidth(value) / 2, y, color, shadow);
    }

    private static int fade(int color, float amount) {
        return opaque(Color.interpolate(color, FADE_TARGET, amount));
    }

    private static int opaque(int color) {
        return 0xFF000000 | (color & 0xFFFFFF);
    }
}
