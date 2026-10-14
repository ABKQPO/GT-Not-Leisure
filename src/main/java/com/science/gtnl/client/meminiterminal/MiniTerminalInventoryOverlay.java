package com.science.gtnl.client.meminiterminal;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.science.gtnl.ScienceNotLeisure;

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
        getContext().updateState(
            mouseX,
            mouseY,
            GuiContext.getDefault()
                .getPartialTicks());
        panel.prepareFrame();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            MiniTerminalHostLayer.loadTerminalMatrix(panel.inventory());
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glDepthMask(true);
            GL11.glClearDepth(1);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            super.drawScreen();
            super.drawForeground();
            GL11.glDepthMask(true);
            GL11.glClearDepth(1);
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    @Override
    public void drawScreen() {}

    @Override
    public void drawForeground() {}
}
