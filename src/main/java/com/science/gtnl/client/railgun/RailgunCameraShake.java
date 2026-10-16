package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public final class RailgunCameraShake {

    private static float pitchRemaining;
    private static int recoilTicks;
    private static int shakeTicks;
    private static float amplitude, yawOffset, pitchOffset;
    private static EntityPlayer shaken;

    public static void recoil(float pitch, int tier) {
        pitchRemaining += pitch;
        recoilTicks = 4;
        shakeTicks = 8;
        amplitude = 0.5F * tier;
    }

    public static void clear() {
        restore();
        pitchRemaining = 0;
        recoilTicks = 0;
        shakeTicks = 0;
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
        if (shakeTicks > 0) shakeTicks--;
    }

    private static void restore() {
        if (shaken != null) {
            shaken.rotationYaw -= yawOffset;
            shaken.rotationPitch -= pitchOffset;
        }
        shaken = null;
        yawOffset = 0;
        pitchOffset = 0;
    }

    @SubscribeEvent
    public void render(TickEvent.RenderTickEvent event) {
        restore();
        var mc = Minecraft.getMinecraft();
        if (event.phase != TickEvent.Phase.START || shakeTicks <= 0 || mc.thePlayer == null || mc.theWorld == null)
            return;
        float scale = amplitude * shakeTicks / 8.0F;
        yawOffset = (mc.theWorld.rand.nextFloat() - 0.5F) * scale;
        pitchOffset = (mc.theWorld.rand.nextFloat() - 0.5F) * scale;
        shaken = mc.thePlayer;
        shaken.rotationYaw += yawOffset;
        shaken.rotationPitch += pitchOffset;
    }
}
