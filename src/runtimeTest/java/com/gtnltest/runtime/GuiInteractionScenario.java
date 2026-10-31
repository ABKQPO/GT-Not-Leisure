package com.gtnltest.runtime;

import com.cleanroommc.modularui.screen.ModularScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
interface GuiInteractionScenario {

    @FunctionalInterface
    interface ScreenshotCapture {

        String capture(String label) throws Exception;
    }

    boolean tick(ModularScreen screen, RuntimeReport.Result result) throws Exception;

    String progress();
}
