package com.science.gtnl.client.meminiterminal;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.science.gtnl.ScienceNotLeisure;

/** Keeps MUI's overlay lifecycle while drawing inside NEI's container object pass. */
final class MiniTerminalInventoryOverlay extends ModularScreen {

    private final MiniTerminalInventoryPanel panel;

    MiniTerminalInventoryOverlay(MiniTerminalInventoryPanel panel) {
        super(ScienceNotLeisure.MODID, panel);
        this.panel = panel;
        constructOverlay(panel.inventory());
    }

    void drawInContainer(int mouseX, int mouseY) {
        if (MiniTerminalClient.overlay() != panel || !panel.isValid()
            || !panel.isOpen()
            || Minecraft.getMinecraft().currentScreen != panel.inventory()) return;
        getContext().updateState(mouseX, mouseY, GuiContext.getDefault().getPartialTicks());
        panel.prepareFrame();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            // NEI has already translated to screen coordinates. Its cursor pass runs after this callback.
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            super.drawScreen();
            super.drawForeground();
            // Moving panels are drawn in the foreground, after MUI's normal per-panel depth clear.
            GL11.glDepthMask(true);
            GL11.glClearDepth(1);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    // OverlayStack still handles input, updates and queued widget tooltips, but must not draw the panel again.
    @Override
    public void drawScreen() {}

    @Override
    public void drawForeground() {}
}
