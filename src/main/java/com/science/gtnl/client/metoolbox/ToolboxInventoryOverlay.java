package com.science.gtnl.client.metoolbox;

import com.cleanroommc.modularui.screen.ModularScreen;
import com.science.gtnl.ScienceNotLeisure;

/** Mounts the terminal in an independent MUI screen for container GUIs. */
final class ToolboxInventoryOverlay extends ModularScreen {

    private final ToolboxInventoryPanel panel;

    ToolboxInventoryOverlay(ToolboxInventoryPanel panel) {
        super(ScienceNotLeisure.MODID, panel);
        this.panel = panel;
        constructOverlay(panel.inventory());
    }

    @Override
    public void drawScreen() {
        panel.prepareFrame();
        panel.foreground().queueCursor();
        super.drawScreen();
    }

    @Override
    public void drawForeground() {
        super.drawForeground();
        panel.foreground().queueTooltip();
    }
}
