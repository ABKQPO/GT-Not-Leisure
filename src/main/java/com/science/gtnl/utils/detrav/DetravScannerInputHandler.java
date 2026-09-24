package com.science.gtnl.utils.detrav;

import org.lwjgl.input.Mouse;

import com.cleanroommc.modularui.api.event.MouseInputEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DetravScannerInputHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseInput(MouseInputEvent.Pre event) {
        if (!(event.gui instanceof DetravScannerGUI scanner)) {
            return;
        }

        int wheel = Mouse.getEventDWheel();
        if (wheel == 0) {
            return;
        }

        if (scanner.handleMapWheel(wheel)) {
            event.setCanceled(true);
        }
    }
}
