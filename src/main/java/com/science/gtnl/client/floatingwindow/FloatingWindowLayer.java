package com.science.gtnl.client.floatingwindow;

import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.api.IMuiScreen;
import com.science.gtnl.mixins.early.minecraft.AccessorGuiScreen;

import codechicken.lib.gui.GuiDraw;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class FloatingWindowLayer {

    private static final FloatBuffer BASE_MATRIX = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer SCRATCH_MATRIX = BufferUtils.createFloatBuffer(16);
    private static final List<HostTooltip> TOOLTIPS = new ArrayList<>();
    private static GuiScreen frameGui;
    private static boolean capturing;
    private static boolean replaying;

    static void beginFrame(GuiScreen gui) {
        TOOLTIPS.clear();
        capturing = false;
        frameGui = FloatingWindows.hasWindows(gui) ? gui : null;
        if (frameGui == null) return;
        BASE_MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, BASE_MATRIX);
    }

    static void endFrame(GuiScreen gui) {
        flushTooltips(gui);
        frameGui = null;
    }

    static void openHostPass(GuiScreen gui) {
        capturing = gui == frameGui && !(gui instanceof IMuiScreen);
    }

    static void loadWindowMatrix(GuiScreen gui) {
        if (gui != frameGui) return;
        GL11.glLoadMatrix(BASE_MATRIX);
        GL11.glTranslatef(0, 0, 200);
    }

    public static boolean deferTooltip(GuiScreen gui, List<?> lines, int x, int y, FontRenderer font) {
        if (!capturing || replaying || gui != frameGui || lines == null || lines.isEmpty()) return false;
        float[] matrix = currentMatrix();
        if (!anchoredInWindow(gui, matrix, x, y)) {
            TOOLTIPS.add(new HostTooltip(new ArrayList<>(lines), x, y, font, matrix));
        }
        return true;
    }

    public static boolean dropTooltip(GuiScreen gui, int x, int y) {
        return capturing && !replaying && gui == frameGui && anchoredInWindow(gui, currentMatrix(), x, y);
    }

    static void flushTooltips(GuiScreen gui) {
        capturing = false;
        if (TOOLTIPS.isEmpty()) return;
        if (gui != frameGui) {
            TOOLTIPS.clear();
            return;
        }
        replaying = true;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            for (HostTooltip tooltip : TOOLTIPS) {
                GL11.glPushMatrix();
                try {
                    SCRATCH_MATRIX.clear();
                    SCRATCH_MATRIX.put(tooltip.matrix())
                        .flip();
                    GL11.glLoadMatrix(SCRATCH_MATRIX);
                    ((AccessorGuiScreen) gui)
                        .gtnl$drawHoveringText(tooltip.lines(), tooltip.x(), tooltip.y(), tooltip.font());
                } finally {
                    GL11.glPopMatrix();
                }
            }
        } finally {
            GL11.glPopAttrib();
            TOOLTIPS.clear();
            replaying = false;
        }
    }

    public static boolean mouseOverWindow(GuiScreen gui) {
        Point mouse = GuiDraw.getMousePosition();
        return FloatingWindows.windowAt(gui, mouse.x, mouse.y) != null;
    }

    private static boolean anchoredInWindow(GuiScreen gui, float[] m, int x, int y) {
        return FloatingWindows
            .windowAt(gui, Math.round(m[0] * x + m[4] * y + m[12]), Math.round(m[1] * x + m[5] * y + m[13])) != null;
    }

    private static float[] currentMatrix() {
        float[] matrix = new float[16];
        SCRATCH_MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, SCRATCH_MATRIX);
        SCRATCH_MATRIX.get(matrix);
        return matrix;
    }

    private record HostTooltip(List<?> lines, int x, int y, FontRenderer font, float[] matrix) {}
}
