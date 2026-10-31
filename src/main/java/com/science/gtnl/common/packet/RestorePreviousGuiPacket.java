package com.science.gtnl.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import com.gtnllib.network.ClientboundPacket;
import com.science.gtnl.ScienceNotLeisure;
import com.science.gtnl.client.GTNLInputHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RestorePreviousGuiPacket extends ClientboundPacket {

    @Override
    @SideOnly(Side.CLIENT)
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
