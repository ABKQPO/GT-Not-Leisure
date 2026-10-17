package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.science.gtnl.client.shader.GlslProgram;
import com.science.gtnl.common.packet.RailgunBeamChainFxPacket;
import com.science.gtnl.common.packet.RailgunFireFxPacket;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunClientFx {

    private static final int IMPACT_SPARKS = 24;
    private static final double SPARK_SPREAD = 0.6;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new RailgunClientFx());
        MinecraftForge.EVENT_BUS.register(new RailgunBeamRenderer());
        MinecraftForge.EVENT_BUS.register(new RailgunArcRenderer());
        MinecraftForge.EVENT_BUS.register(new RailgunShockwaveRenderer());
        MinecraftForge.EVENT_BUS.register(new RailgunOrbRenderer());
        MinecraftForge.EVENT_BUS.register(new RailgunHudRenderer());
        FMLCommonHandler.instance()
            .bus()
            .register(new RailgunCameraShake());
        FMLCommonHandler.instance()
            .bus()
            .register(new RailgunClientInput());
        FMLCommonHandler.instance()
            .bus()
            .register(new RailgunSounds());
    }

    public static void playFire(RailgunFireFxPacket packet) {
        if (Minecraft.getMinecraft().theWorld == null) return;
        RailgunCharge.finish(packet.shooterId);
        RailgunOrbRenderer.launch(packet.shooterId, packet.from, packet.firstHit, packet.tier, () -> impact(packet));
    }

    private static void impact(RailgunFireFxPacket packet) {
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        float intensity = 1F + 0.22F * Math.max(0, packet.tier - 1);
        RailgunBeamRenderer.shot(packet.shooterId, packet.from, packet.firstHit, true, intensity);
        for (int index = 0; index + 1 < packet.chainPath.size(); index += 2)
            RailgunArcRenderer.spawnChain(packet.chainPath.get(index), packet.chainPath.get(index + 1), 8);
        RailgunShockwaveRenderer.spawn(packet.firstHit, packet.impactRadius);
        for (int spark = 0; spark < IMPACT_SPARKS; spark++) world.spawnParticle(
            "fireworksSpark",
            packet.firstHit.x(),
            packet.firstHit.y(),
            packet.firstHit.z(),
            (world.rand.nextDouble() - 0.5) * SPARK_SPREAD,
            (world.rand.nextDouble() - 0.5) * SPARK_SPREAD,
            (world.rand.nextDouble() - 0.5) * SPARK_SPREAD);
        if (packet.sound) world.playSound(
            packet.firstHit.x(),
            packet.firstHit.y(),
            packet.firstHit.z(),
            "sciencenotleisure:railgun.fire",
            1,
            packet.tier == 3 ? 0.8F : 1,
            false);
    }

    public static void playChain(RailgunBeamChainFxPacket packet) {
        var world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        for (int index = 0; index + 1 < packet.path.size(); index += 2)
            RailgunArcRenderer.spawnChain(packet.path.get(index), packet.path.get(index + 1), 6, packet.ehv);
        if (packet.sound) world.playSound(
            packet.origin.x(),
            packet.origin.y(),
            packet.origin.z(),
            "sciencenotleisure:railgun.beam.chain",
            0.12F,
            1.8F,
            false);
    }

    @SubscribeEvent
    public void unload(WorldEvent.Unload event) {
        if (!event.world.isRemote) return;
        RailgunBeamRenderer.clear();
        RailgunArcRenderer.clear();
        RailgunShockwaveRenderer.clear();
        RailgunOrbRenderer.clear();
        RailgunCameraShake.clear();
        RailgunCharge.reset();
        RailgunMuzzleAnchor.clear();
        RailgunSounds.clear();
        GlslProgram.release();
    }

    @SubscribeEvent
    public void reload(TextureStitchEvent.Post event) {
        RailgunRenderHelper.clearSprites();
        GlslProgram.release();
    }
}
