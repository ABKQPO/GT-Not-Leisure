package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.science.gtnl.common.packet.RailgunFireFxPacket;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class RailgunClientFx {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new RailgunClientFx());
        MinecraftForge.EVENT_BUS.register(new RailgunBeamRenderer());
        MinecraftForge.EVENT_BUS.register(new RailgunHudRenderer());
        FMLCommonHandler.instance()
            .bus()
            .register(new RailgunCameraShake());
    }

    public static void playFire(RailgunFireFxPacket packet) {
        var mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) return;
        RailgunBeamRenderer.shot(packet.shooterId, packet.from, packet.firstHit, true);
        for (int i = 0; i < packet.chainPath.size(); i += 2)
            RailgunBeamRenderer.shot(-1, packet.chainPath.get(i), packet.chainPath.get(i + 1), true);
        for (int i = 0; i < 24; i++) mc.theWorld.spawnParticle(
            "fireworksSpark",
            packet.firstHit.x(),
            packet.firstHit.y(),
            packet.firstHit.z(),
            (mc.theWorld.rand.nextDouble() - 0.5) * 0.6,
            (mc.theWorld.rand.nextDouble() - 0.5) * 0.6,
            (mc.theWorld.rand.nextDouble() - 0.5) * 0.6);
        if (packet.sound) mc.theWorld.playSound(
            packet.firstHit.x(),
            packet.firstHit.y(),
            packet.firstHit.z(),
            "sciencenotleisure:railgun.fire",
            1,
            packet.tier == 3 ? 0.8F : 1,
            false);
    }

    @SubscribeEvent
    public void unload(WorldEvent.Unload event) {
        if (event.world.isRemote) {
            RailgunBeamRenderer.clear();
            RailgunCameraShake.clear();
        }
    }
}
