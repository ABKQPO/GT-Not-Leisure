package com.science.gtnl.client.railgun;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.science.gtnl.client.fx.FxShockwaveRenderer;
import com.science.gtnl.client.shader.GlslProgram;
import com.science.gtnl.common.equipment.EquipmentVec;
import com.science.gtnl.common.packet.RailgunBeamChainFxPacket;
import com.science.gtnl.common.packet.RailgunFireFxPacket;
import com.science.gtnl.common.railgun.RailgunChargeTier;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RailgunClientFx {

    private static final int IMPACT_SPARKS = 24;
    private static final double SPARK_SPREAD = 0.6;
    private static final int CHAIN_SPARKS = 4;
    private static final double CHAIN_SPARK_SPREAD = 0.4;
    private static final float RAILGUN_SOUND_RANGE_VOLUME = 16.0F;
    private static final float WAVE_RED = 1F, WAVE_GREEN = 0.5F, WAVE_BLUE = 0.75F;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new RailgunClientFx());
        MinecraftForge.EVENT_BUS.register(new RailgunBeamRenderer());
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
        EquipmentVec hit = packet.firstHit;
        float intensity = 1F + 0.22F * Math.max(0, packet.tier - 1);
        RailgunBeamRenderer.shot(packet.shooterId, packet.from, hit, true, intensity);
        double radius = packet.impactRadius;
        if (radius > 0) {
            int waveLife = max ? 80 : 56 + packet.tier * 6;
            FxShockwaveRenderer.spawn(hit, radius, waveLife, WAVE_RED, WAVE_GREEN, WAVE_BLUE);
            FxShockwaveRenderer.spawn(hit, radius * 0.5, Math.max(35, waveLife - 15), WAVE_RED, WAVE_GREEN, WAVE_BLUE);
        }
        for (int index = 0; index + 1 < packet.chainPath.size(); index += 2) {
            EquipmentVec target = packet.chainPath.get(index + 1);
            RailgunArcs.chain(packet.chainPath.get(index), target, max ? 40 : 32, true);
            sparks(world, target, CHAIN_SPARKS, CHAIN_SPARK_SPREAD);
        }
        EquipmentVec muzzle = RailgunMuzzleAnchor
            .analyticMuzzle(packet.shooterId, packet.from, minecraft.timer.renderPartialTicks);
        RailgunArcs.plasma(
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
                RailgunArcs.impactSpark(
                    hit,
                    hit.add(
                        Math.cos(yaw) * Math.cos(pitch) * length,
                        Math.sin(pitch) * length,
                        Math.sin(yaw) * Math.cos(pitch) * length),
                    22 + world.rand.nextInt(10));
            }
        }
        sparks(world, hit, IMPACT_SPARKS, SPARK_SPREAD);
        if (packet.sound) world.playSound(
            hit.x(),
            hit.y(),
            hit.z(),
            "sciencenotleisure:railgun.fire.impact",
            RAILGUN_SOUND_RANGE_VOLUME,
            max ? 0.7F : 0.85F,
            false);
    }

    private static void sparks(WorldClient world, EquipmentVec at, int count, double spread) {
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
            RailgunArcs.chain(packet.path.get(index), packet.path.get(index + 1), 6, packet.ehv);
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
        RailgunOrbRenderer.clear();
        RailgunCameraShake.clear();
        RailgunCharge.clear();
        RailgunMuzzleAnchor.clear();
        RailgunSounds.clear();
        GlslProgram.release();
    }

    @SubscribeEvent
    public void reload(TextureStitchEvent.Post event) {
        GlslProgram.release();
    }
}
