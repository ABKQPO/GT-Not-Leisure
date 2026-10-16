package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public final class RailgunCameraShake {

    private static float pitchRemaining;
    private static int recoilTicks;

    public static void recoil(float pitch, int tier) {
        pitchRemaining += pitch;
        recoilTicks = 4;
    }

    public static void clear() {
        pitchRemaining = 0;
        recoilTicks = 0;
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        var player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            clear();
            return;
        }
        if (recoilTicks > 0) {
            float change = pitchRemaining / recoilTicks;
            player.rotationPitch = Math.max(-90, player.rotationPitch - change);
            pitchRemaining -= change;
            recoilTicks--;
        }
    }
}
