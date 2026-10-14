package com.science.gtnl.client.metoolbox;

import com.cleanroommc.modularui.screen.ModularScreen;
import com.science.gtnl.ScienceNotLeisure;

/** Fallback host for container GUIs that do not implement MUI's screen interface. */
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
}
