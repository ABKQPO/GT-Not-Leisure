package com.science.gtnl.client.meminiterminal;

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

/** Keeps everything a host GUI draws below the terminal, while its tooltips stay above it. */
@SideOnly(Side.CLIENT)
public final class MiniTerminalHostLayer {

    private static final FloatBuffer BASE_MATRIX = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer SCRATCH_MATRIX = BufferUtils.createFloatBuffer(16);
    private static final List<HostTooltip> TOOLTIPS = new ArrayList<>();
    private static GuiScreen frameGui;
    private static boolean capturing;
    private static boolean replaying;

    private MiniTerminalHostLayer() {}

    static void beginFrame(GuiScreen gui) {
        TOOLTIPS.clear();
        capturing = false;
        MiniTerminalInventoryPanel overlay = MiniTerminalClient.overlay();
        frameGui = overlay != null && gui == overlay.inventory() ? gui : null;
        if (frameGui == null) return;
        BASE_MATRIX.clear();
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, BASE_MATRIX);
    }

    static void endFrame(GuiScreen gui) {
        // Hosts that skipped NEI's object pass still get their tooltips back.
        flushTooltips(gui);
        frameGui = null;
    }

    /** Opens the window between NEI's preDraw and the terminal, in which only the host's own layers draw. */
    static void openHostPass(GuiScreen gui) {
        capturing = gui == frameGui && !(gui instanceof IMuiScreen);
    }

    /** Vanilla reaches the terminal through NEI's +200 shift, MUI1 without one; both end up on the same base. */
    static void loadTerminalMatrix(GuiScreen gui) {
        if (gui != frameGui) return;
        GL11.glLoadMatrix(BASE_MATRIX);
        GL11.glTranslatef(0, 0, 200);
    }

    public static boolean deferTooltip(GuiScreen gui, List<?> lines, int x, int y, FontRenderer font) {
        if (!capturing || replaying || gui != frameGui || lines == null || lines.isEmpty()) return false;
        float[] matrix = currentMatrix();
        if (!anchoredInTerminal(gui, matrix, x, y)) {
            TOOLTIPS.add(new HostTooltip(new ArrayList<>(lines), x, y, font, matrix));
        }
        return true;
    }

    /** Item tooltips that bypass drawHoveringText (Chromatic Tooltips) still must not leak through the terminal. */
    public static boolean dropTooltip(GuiScreen gui, int x, int y) {
        return capturing && !replaying && gui == frameGui && anchoredInTerminal(gui, currentMatrix(), x, y);
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

    public static boolean mouseOverTerminal(GuiScreen gui) {
        Point mouse = GuiDraw.getMousePosition();
        return overTerminal(gui, mouse.x, mouse.y);
    }

    private static boolean overTerminal(GuiScreen gui, int x, int y) {
        MiniTerminalInventoryPanel overlay = MiniTerminalClient.overlay();
        return overlay != null && gui == overlay.inventory() && overlay.isMouseOver(x, y);
    }

    /** A tooltip anchored inside the terminal belongs to a host widget hidden under it. */
    private static boolean anchoredInTerminal(GuiScreen gui, float[] m, int x, int y) {
        // Column-major modelview; the GUI base matrix only shifts z, so this is the screen position.
        return overTerminal(gui, Math.round(m[0] * x + m[4] * y + m[12]), Math.round(m[1] * x + m[5] * y + m[13]));
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
