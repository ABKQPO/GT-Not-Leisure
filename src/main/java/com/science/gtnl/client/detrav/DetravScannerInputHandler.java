package com.science.gtnl.client.detrav;

import org.lwjgl.input.Mouse;

import com.cleanroommc.modularui.api.event.MouseInputEvent;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;
import com.science.gtnl.client.gui.detrav.DetravScannerGUI;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(side = Side.CLIENT, phase = Phase.PRE)
public class DetravScannerInputHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onMouseInput(MouseInputEvent.Pre event) {
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
