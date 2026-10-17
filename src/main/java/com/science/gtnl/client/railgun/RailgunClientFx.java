package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.science.gtnl.client.shader.GlslProgram;
import com.science.gtnl.common.packet.RailgunBeamChainFxPacket;
import com.science.gtnl.common.packet.RailgunFireFxPacket;
import com.science.gtnl.common.railgun.RailgunChargeTier;
import com.science.gtnl.common.railgun.RailgunVec;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunClientFx {

    private static final int IMPACT_SPARKS = 24;
    private static final double SPARK_SPREAD = 0.6;
    private static final int CHAIN_SPARKS = 4;
    private static final double CHAIN_SPARK_SPREAD = 0.4;
    private static final float RAILGUN_SOUND_RANGE_VOLUME = 16.0F;

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
        WorldClient world = Minecraft.getMinecraft().theWorld;
        if (world == null) return;
        RailgunCharge.finish(packet.shooterId);
        if (packet.sound && packet.from != null) world.playSound(
            packet.from.x(),
            packet.from.y(),
            packet.from.z(),
            max(packet) ? "sciencenotleisure:railgun.fire.max" : "sciencenotleisure:railgun.fire.charged",
            RAILGUN_SOUND_RANGE_VOLUME,
            1,
            false);
        RailgunOrbRenderer.launch(packet.shooterId, packet.from, packet.firstHit, packet.tier, () -> impact(packet));
    }

    private static boolean max(RailgunFireFxPacket packet) {
        return packet.tier == RailgunChargeTier.EHV3.ordinal();
    }

    private static void impact(RailgunFireFxPacket packet) {
        Minecraft minecraft = Minecraft.getMinecraft();
        WorldClient world = minecraft.theWorld;
        if (world == null) return;
        boolean max = max(packet);
        RailgunVec hit = packet.firstHit;
        float intensity = 1F + 0.22F * Math.max(0, packet.tier - 1);
        RailgunBeamRenderer.shot(packet.shooterId, packet.from, hit, true, intensity);
        double radius = packet.impactRadius;
        if (radius > 0) {
            int waveLife = max ? 80 : 56 + packet.tier * 6;
            RailgunShockwaveRenderer.spawn(hit, radius, waveLife);
            RailgunShockwaveRenderer.spawn(hit, radius * 0.5, Math.max(35, waveLife - 15));
        }
        for (int index = 0; index + 1 < packet.chainPath.size(); index += 2) {
            RailgunVec target = packet.chainPath.get(index + 1);
            RailgunArcRenderer.spawnChain(packet.chainPath.get(index), target, max ? 40 : 32);
            sparks(world, target, CHAIN_SPARKS, CHAIN_SPARK_SPREAD);
        }
        RailgunVec muzzle = RailgunRenderHelper
            .analyticMuzzle(packet.shooterId, packet.from, minecraft.timer.renderPartialTicks);
        RailgunArcRenderer.spawnPlasma(
            muzzle,
            hit,
            Math.max(
                10,
                (int) Math.round(
                    muzzle.subtract(hit)
                        .length() * 1.2)),
            max ? 0.3 : 0.18,
            max ? 36 : 30);
        if (radius > 0) {
            int bolts = max ? 18 : 8 + packet.tier * 2;
            for (int i = 0; i < bolts; i++) {
                double yaw = world.rand.nextDouble() * Math.PI * 2;
                double pitch = (world.rand.nextDouble() - 0.3) * Math.PI;
                double length = radius * (0.6 + world.rand.nextDouble() * 0.6);
                RailgunArcRenderer.spawnImpactSpark(
                    hit,
                    hit.add(
                        Math.cos(yaw) * Math.cos(pitch) * length,
                        Math.sin(pitch) * length,
                        Math.sin(yaw) * Math.cos(pitch) * length),
                    22 + world.rand.nextInt(10));
            }
        }
        sparks(world, hit, IMPACT_SPARKS, SPARK_SPREAD);
        if (packet.sound && max) world.playSound(
            hit.x(),
            hit.y(),
            hit.z(),
            "sciencenotleisure:railgun.fire.impact",
            RAILGUN_SOUND_RANGE_VOLUME,
            0.7F,
            false);
    }

    private static void sparks(WorldClient world, RailgunVec at, int count, double spread) {
        for (int spark = 0; spark < count; spark++) world.spawnParticle(
            "fireworksSpark",
            at.x(),
            at.y(),
            at.z(),
            (world.rand.nextDouble() - 0.5) * spread,
            (world.rand.nextDouble() - 0.5) * spread,
            (world.rand.nextDouble() - 0.5) * spread);
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
            0.35F,
            1.6F,
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
