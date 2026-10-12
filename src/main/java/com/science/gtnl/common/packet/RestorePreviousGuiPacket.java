package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLInputHandler;
import com.science.gtnl.common.packet.base.ClientboundPacket;

public class RestorePreviousGuiPacket extends ClientboundPacket {

    @Override
    public void handleClient(Minecraft minecraft) {
        if (minecraft == null || minecraft.thePlayer == null) {
            ScienceNotLeisure.LOG.error("Cannot restore previous GUI without an active client player");
            return;
        }
        GuiScreen previousScreen = GTNLInputHandler.LAST_GUI_SCREEN;
        if (previousScreen == null) {
            ScienceNotLeisure.LOG.error("Cannot restore previous GUI because no client screen was saved");
            return;
        }

        minecraft.displayGuiScreen(previousScreen);
        GTNLInputHandler.LAST_GUI_SCREEN = null;
    }
}
